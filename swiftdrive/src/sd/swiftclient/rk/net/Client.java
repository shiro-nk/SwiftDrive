package sd.swiftclient.rk.net;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import sd.swiftglobal.rk.Meta.DirectKiller;
import sd.swiftglobal.rk.Meta.DirectTimeout;
import sd.swiftglobal.rk.Meta.IndirectTimeout;
import sd.swiftglobal.rk.Meta.LeaveBlank;
import sd.swiftglobal.rk.Meta.PingHandler;
import sd.swiftglobal.rk.Meta.Pointer;
import sd.swiftglobal.rk.Meta.RequiresPingHandler;
import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.CommandException;
import sd.swiftglobal.rk.expt.DisconnectException;
import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.type.Data;
import sd.swiftglobal.rk.type.Generic;
import sd.swiftglobal.rk.type.ServerCommand;
import sd.swiftglobal.rk.type.SwiftFile;
import sd.swiftglobal.rk.util.Logging;
import sd.swiftglobal.rk.util.Ping;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetContainer;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetTool;
import sd.swiftglobal.rk.util.Terminator;

/* This file is part of Swift Drive				   *
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

/**
 * Client side of the data transfer network.
 *
 * @author Ryan Kerr
 */
public class Client implements SwiftNetTool, Settings, Logging, Closeable {
	
	private final Socket server;
	private DataInputStream  dis;
	private DataOutputStream dos;
	private Ping ping;
	private SwiftNetContainer parent;
	private Terminator term;
	private int connectionID = 0;
	private ChatClient cclient;
	private boolean unlocked = false;

	/**
	 * Establishes a connecting and I/O sockets with the server
	 * @param hostname Host to connect to
	 * @param port Host port
	 * @throws DisconnectException If something fails while connecting
	 */
	public Client(String hostname, int port, SwiftNetContainer parent) throws DisconnectException {
		try {
			this.parent = parent;
			server = new Socket(hostname, port);
			dis = new DataInputStream(server.getInputStream());
			dos = new DataOutputStream(server.getOutputStream());
			ping = new Ping(dis, dos, this);
			new Thread(ping).start();
			term = new Terminator(this);
		}
		catch(IOException ix) {
			kill();
			throw new DisconnectException(EXC_CONN);
		}
	}
	
	@PingHandler @IndirectTimeout
	public <Type extends Data> void scmd(ServerCommand scmd, Type outbound) throws DisconnectException, CommandException {
		if(unlocked) {
			ping.standby();
			ping.deactivate();
			sendData(outbound);
			int signal = scmd(scmd);
			ping.activate();
			if(signal != SIG_READY) throw new CommandException(signal);
		}
	}
	
	@PingHandler @IndirectTimeout
	public Data scmd(ServerCommand scmd, Data inbound, int Type) throws DisconnectException, CommandException {
		if(unlocked) {
			ping.pause();
			int signal = scmd(scmd);
			if(signal == SIG_READY) {
				inbound = getData(inbound);
				ping.activate();
				return inbound;
			} 
			else {
				ping.activate();
				throw new CommandException(signal);
			}
		}
		return new Generic();
	}
	
	public void sfcmd(ServerCommand scmd, SwiftFile sf) throws DisconnectException, FileException, CommandException {
		if(unlocked) {
			ping.pause();
			try {
				writeInt(DAT_FILE);
				sf.send(dos);
			}
			catch(IOException ix) {
				throw new DisconnectException(EXC_NWRITE, ix);
			}
			int signal = scmd(scmd);
			ping.activate();
			if(signal != SIG_READY) throw new CommandException(signal);
		}
	}
	
	@PingHandler @IndirectTimeout
	public SwiftFile sfcmd(ServerCommand scmd) throws DisconnectException, FileException, CommandException {
		if(unlocked) {
			ping.pause();
			int signal1 = scmd(scmd),
				signal2 = scmd(new ServerCommand(CMD_SEND_FILE, ""));
			if(signal1 == SIG_READY && signal2 == SIG_READY) {
				try {
					SwiftFile file = new SwiftFile(dis);
					ping.activate();
					return file;
				}
				catch(IOException ix) {
					throw new DisconnectException(EXC_NREAD, ix);
				}
			}
			else {
				ping.activate();
				throw new CommandException(signal1);
			}
		}
		return new SwiftFile(0);
	}

	@IndirectTimeout @RequiresPingHandler
	private int scmd(ServerCommand scmd) throws DisconnectException, CommandException {
		if(unlocked) {
			if(scmd.toString() != null) {
				writeInt(DAT_SCMD);
				writeUTF(scmd.toString());
				return readInt();
			}
			else {
				return EXC_BCMD;
			}
		}
		return EXC_LOCK;
	}	
	
	@IndirectTimeout @RequiresPingHandler
	private <Type extends Data> void sendData(Type outbound) throws DisconnectException {
		if(unlocked) {
			writeInt(DAT_DATA);
			writeInt(outbound.getSize());
			for(String s : outbound.getArray()) writeUTF(s);
		}
	}
	
	@IndirectTimeout @RequiresPingHandler
	private Data getData(@Pointer Data inbound) throws DisconnectException {
		if(unlocked) {
			inbound.reset();
			int size = readInt();
			for(int i = 0; i < size; i++) inbound.add(readUTF());
			return inbound;
		}
		return new Generic();
	}

	@RequiresPingHandler @DirectKiller
	private void writeInt(int x) throws DisconnectException {
		try {
			dos.writeInt(x);
		}
		catch(IOException ix) {
			kill();
			throw new DisconnectException(EXC_WRITE, ix);
		}
	}
	
	@DirectTimeout @RequiresPingHandler @DirectKiller
	private int readInt() throws DisconnectException {
		try {
			term.run();
			int rtn = dis.readInt();
			term.cancel();
			return rtn;
		}
		catch(IOException ix) {
			kill();
			throw new DisconnectException(EXC_WRITE, ix);
		}
	}
	
	@RequiresPingHandler @DirectKiller
	private void writeUTF(String s) throws DisconnectException {
		try {
			dos.writeUTF(s);
		}
		catch(IOException ix) {
			kill();
			throw new DisconnectException(EXC_WRITE, ix);
		}
	}
	
	@DirectTimeout @RequiresPingHandler @DirectKiller
	private String readUTF() throws DisconnectException {
		try {
			term.run();
			String rtn = dis.readUTF();
			term.cancel();
			return rtn;
		}
		catch(IOException ix) {
			kill();
			throw new DisconnectException(EXC_READ, ix);
		}
	}
	
	public boolean login(String u, char[] p) throws DisconnectException {
		System.out.println(u + ": " + new String(p));
		return login(u, new String(p).getBytes());
	}

	@PingHandler @DirectKiller
	public boolean login(String username, byte[] password) throws DisconnectException {
		if(!unlocked) {
			boolean rtn = false;
			ping.pause();
			writeInt(DAT_LGIN);
			writeUTF(username);
			writeInt(password.length);
			try {
				for(byte p : password) dos.writeByte(p);
				rtn = dis.readBoolean();
			} 
			catch(IOException ix) {
				kill();
				throw new DisconnectException(EXC_NETIO, ix);
			}
			unlocked = rtn;
			ping.activate();
			return rtn;
		}
		return true;
	}
	
	@PingHandler @DirectKiller
	public void disconnect() throws DisconnectException {
		ping.pause();
		writeInt(DAT_NULL);
		ping.activate();
		kill();
	}
	
	@Override
	public int getID() {
		return connectionID;
	}
	
	@Override @LeaveBlank
	public void setParent(SwiftNetContainer c) {
		
	}
	
	@Override
	public SwiftNetContainer getParent() {
		return parent;
	}
	
	@Override
	public void kill() {
		close();
		parent.dereference(this);
	}

	public ChatClient getChat() {
		return cclient;
	}
	
	public boolean isUnlocked() {
		return unlocked;
	}

	@Override
	public void close() {
		try {
			if(ping != null) ping.stop();
			if(dis != null) dis.close();
			if(dos != null) dos.close();
			if(server != null) server.close();
		}
		catch(IOException ix) {
			error("Error closing sockets", LOG_FRC);
		}
	}
}
