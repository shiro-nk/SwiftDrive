package sd.swiftclient.rk.net;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
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
import sd.swiftglobal.rk.expt.SwiftException;
import sd.swiftglobal.rk.type.Data;
import sd.swiftglobal.rk.type.Generic;
import sd.swiftglobal.rk.type.ServerCommand;
import sd.swiftglobal.rk.type.SwiftFile;
import sd.swiftglobal.rk.type.users.User;
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
	private boolean unlocked = false;
	private User user;
	public static String SV_DIV;

	/**
	 * Establishes a connecting and I/O sockets with the server
	 * @param hostname Host to connect to
	 * @param port Host port
	 * @throws DisconnectException If something fails while connecting
	 */
	public Client(String hostname, int port, SwiftNetContainer parent) throws DisconnectException {
		try {
			echo("Starting client", LOG_PRI);
			this.parent = parent;
			server = new Socket();
			server.connect(new InetSocketAddress(hostname, port), 2500);
			echo("Connected to " + hostname + ":" + port, LOG_TRI);
			dis = new DataInputStream(server.getInputStream());
			dos = new DataOutputStream(server.getOutputStream());
			ping = new Ping(dis, dos, this);
			//new Thread(ping).start();
			term = new Terminator(this);
			echo("Client ready", LOG_PRI);
		}
		catch(IOException ix) {
			echo("Error connecting to server", LOG_PRI);
			kill();
			throw new DisconnectException(EXC_CONN);
		}
	}
	
	@PingHandler @IndirectTimeout
	public <Type extends Data> void scmd(ServerCommand scmd, Type outbound) throws DisconnectException, CommandException {
		if(unlocked) {
			echo("Data command request started", LOG_SEC);
			ping.standby();
			ping.deactivate();
			sendData(outbound);
			int signal = scmd(scmd);
			ping.activate();
			echo("Data command request completed with signal " + signal, LOG_SEC);
			if(signal != SIG_READY) throw new CommandException(signal);
		}
	}
	
	@PingHandler @IndirectTimeout
	public Data scmd(ServerCommand scmd, Data inbound, int Type) throws DisconnectException, CommandException {
		if(unlocked) {
			echo("Data download request started", LOG_SEC);
			ping.pause();
			int signal = scmd(scmd);
			if(signal == SIG_READY) {
				inbound = getData(inbound);
				ping.activate();
				echo("Data download request completed with signal " + signal, LOG_SEC);
				return inbound;
			} 
			else {
				echo("Request could not be completed: " + SwiftException.getErr(signal));
				ping.activate();
				throw new CommandException(signal);
			}
		}
		return new Generic();
	}
	
	public void sfcmd(ServerCommand scmd, SwiftFile sf) throws DisconnectException, FileException, CommandException {
		if(unlocked) {
			echo("File command request started", LOG_SEC);
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
			echo("File command request completed with signal " + signal, LOG_SEC);
			if(signal != SIG_READY) throw new CommandException(signal);
		}
	}
	
	@PingHandler @IndirectTimeout
	public SwiftFile sfcmd(ServerCommand scmd) throws DisconnectException, FileException, CommandException {
		if(unlocked) {
			echo("Stage 1 file download request started", LOG_SEC);
			ping.pause();
			int signal = scmd(scmd);
			if(signal == SIG_READY) {
				return sfcmd();
			}
			else {
				echo("Stage 1 request could not be completed: " + SwiftException.getErr(signal));
				ping.activate();
				throw new CommandException(signal);
			}
		}
		return new SwiftFile(0);
	}

	@PingHandler @IndirectTimeout
	public SwiftFile sfcmd() throws CommandException, DisconnectException {
		if(unlocked) {
			echo("File download request started", LOG_SEC);
			int signal = scmd(new ServerCommand(CMD_SEND_FILE, ""));
			if(signal == SIG_READY) {
				try {
					echo("Signals returned true: Download commencing", LOG_SEC);
					SwiftFile file = new SwiftFile(dis);
					ping.activate();
					echo("File download request completed", LOG_SEC);
					return file;
				}
					catch(IOException ix) {
					throw new DisconnectException(EXC_NREAD, ix);
				}
			}	
			else {
				echo("Request could not be completed: " + SwiftException.getErr(signal));
				ping.activate();
				throw new CommandException(signal);
			}
		}
		return new SwiftFile(0);
	}

	@IndirectTimeout @RequiresPingHandler
	private int scmd(ServerCommand scmd) throws DisconnectException, CommandException {
		if(unlocked) {
			echo("Sending command", LOG_TRI);
			if(scmd.toString() != null) {
				writeInt(DAT_SCMD);
				writeUTF(scmd.toString());
				int rtn = readInt();
				echo("Command returned with signal " + rtn, LOG_TRI);
				return rtn;
			}
			else {
				echo("Command could not be completed due to a malformed command", LOG_TRI);
				return EXC_BCMD;
			}
		}
		return EXC_LOCK;
	}	
	
	@IndirectTimeout @RequiresPingHandler
	private <Type extends Data> void sendData(Type outbound) throws DisconnectException {
		if(unlocked) {
			echo("Data upload started", LOG_TRI);
			writeInt(DAT_DATA);
			writeInt(outbound.getSize());
			for(String s : outbound.getArray()) writeUTF(s);
			for(String s : outbound.getArray()) echo("Sent: " + s, LOG_SEC);
			echo("Data upload complete", LOG_TRI);
		}
	}
	
	@IndirectTimeout @RequiresPingHandler
	private Data getData(@Pointer Data inbound) throws DisconnectException {
		if(unlocked) {
			echo("Data download started", LOG_TRI);
			inbound.reset();
			int size = readInt();
			for(int i = 0; i < size; i++) inbound.add(readUTF());
			for(String s : inbound.getArray()) echo("Received: " + s);
			echo("Data download complete", LOG_TRI);
			return inbound;
		}
		return new Generic();
	}

	@RequiresPingHandler @DirectKiller
	private void writeInt(int x) throws DisconnectException {
		try {
			echo("Writing integer to socket", LOG_LOW);
			dos.writeInt(x);
			echo("Done", LOG_LOW);
		}
		catch(IOException ix) {
			echo("Failed", LOG_LOW);
			kill();
			throw new DisconnectException(EXC_WRITE, ix);
		}
	}
	
	@DirectTimeout @RequiresPingHandler @DirectKiller
	private int readInt() throws DisconnectException {
		try {
			echo("Reading integer from socket", LOG_LOW);
			term.run();
			int rtn = dis.readInt();
			term.cancel();
			echo("Done", LOG_LOW);
			return rtn;
		}
		catch(IOException ix) {
			echo("Failed", LOG_LOW);
			kill();
			throw new DisconnectException(EXC_WRITE, ix);
		}
	}
	
	@RequiresPingHandler @DirectKiller
	private void writeUTF(String s) throws DisconnectException {
		try {
			echo("Writing string to socket", LOG_LOW);
			dos.writeUTF(s);
			echo("Done", LOG_LOW);
		}
		catch(IOException ix) {
			echo("Failed", LOG_LOW);
			kill();
			throw new DisconnectException(EXC_WRITE, ix);
		}
	}
	
	@DirectTimeout @RequiresPingHandler @DirectKiller
	private String readUTF() throws DisconnectException {
		try {
			echo("Reading string from a socket", LOG_LOW);
			term.run();
			String rtn = dis.readUTF();
			term.cancel();
			echo("Done", LOG_LOW);
			return rtn;
		}
		catch(IOException ix) {
			echo("Failed", LOG_LOW);
			kill();
			throw new DisconnectException(EXC_READ, ix);
		}
	}
	
	public boolean login(String u, char[] p) throws DisconnectException {
		return login(u, new String(p).getBytes());
	}

	@PingHandler @DirectKiller
	public boolean login(String username, byte[] password) throws DisconnectException {
		if(!unlocked) {
			echo("Logging in to the server as " + username, LOG_PRI);
			boolean rtn = false;
			echo("Sending user information", LOG_SEC);
			writeInt(DAT_LGIN);
			writeUTF(username);
			writeInt(password.length);
			try {
				for(byte p : password) dos.writeByte(p);
				term.run();
				rtn = dis.readBoolean();
				term.cancel();
				if(rtn) {
					echo("Login approved", LOG_PRI);
					echo("Receiving full user information", LOG_SEC);
					user = new User(readUTF());
					SV_DIV = readUTF();
					echo("Initializing ping", LOG_TRI);
					new Thread(ping).start();
				}
				else {
					echo("Login revoked", LOG_PRI);
					kill();
				}
			} 
			catch(IOException ix) {
				kill();
				throw new DisconnectException(EXC_NETIO, ix);
			}
			unlocked = rtn;
			echo("Login complete", LOG_PRI);
			return rtn;
		}
		return true;
	}
	
	@PingHandler @DirectKiller
	public void disconnect() throws DisconnectException {
		echo("Requesting disconnection", LOG_SEC);
		ping.pause();
		writeInt(DAT_NULL);
		ping.activate();
		echo("Disconnection request complete, terminating", LOG_SEC);
		kill();
	}
	
	@Override
	public int getID() {
		return connectionID;
	}

	public User getUser() {
		return user;
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

//	public ChatClient getChat() {
//		return cclient;
//	}
	
	public boolean isUnlocked() {
		return unlocked;
	}

	public void echo(Object o, int level) {
		print("[ Client ] " + o.toString() + "\n", level);
	}

	@Override
	public void close() {
		try {
			//if(ping != null) ping.stop();
			if(dis != null) dis.close();
			if(dos != null) dos.close();
			if(server != null) server.close();
		}
		catch(IOException ix) {
			error("Error closing client sockets", LOG_FRC);
		}
	}
}
