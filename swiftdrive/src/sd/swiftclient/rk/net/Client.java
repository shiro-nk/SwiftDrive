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
	
	/**
	 * Establishes a connecting and I/O sockets with the server
	 * @param hostname Host to connect to
	 * @param port Host port
	 * @throws DisconnectException If something fails while connecting
	 */
	public Client(SwiftNetContainer parent, String hostname, int port) throws DisconnectException {
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
	public <Type extends Data> void scmd(ServerCommand scmd, Type outbound) throws DisconnectException {
		ping.standby();
		ping.deactivate();
		sendData(outbound);
		if(scmd(scmd) )
		ping.activate();
	}
	
	@PingHandler @IndirectTimeout
	public Data scmd(ServerCommand scmd, Data inbound, int Type) throws DisconnectException, CommandException {
		ping.pause();
		if(scmd(scmd) == SIG_READY) {
			getData(inbound);
			ping.activate();
			return inbound;
		} 
		else {
			int exc = readInt();
			ping.activate();
			throw new CommandException(EXC_UNKN);
		}
	}
	
	public int sfcmd(ServerCommand scmd, SwiftFile sf) throws DisconnectException, FileException, CommandException {
		ping.pause();
		try {
			sf.send(dos);
		}
		catch(IOException ix) {
			throw new DisconnectException(EXC_NWRITE, ix);
		}
		
		switch(scmd(scmd)) {
			case SIG_READY:
				ping.activate();
				return 1;
			case SIG_FAIL:
				int exc = readInt();
				ping.activate();
				return exc;
			case SIG_BCMD:
			default:
				throw new CommandException(EXC_UNKN);
		}
	}
	
	@PingHandler @IndirectTimeout
	public SwiftFile sfcmd(ServerCommand scmd) throws DisconnectException, FileException, CommandException {
		ping.pause();
		switch(scmd(scmd)) {
			case SIG_READY:
				try {
					SwiftFile file = new SwiftFile(dis);
					ping.activate();
					return file;
				}
				catch(IOException ix) {
					throw new DisconnectException(EXC_NREAD, ix);
				}
			case SIG_FAIL:
				ping.activate();
				throw new FileException(EXC_F404);
			case SIG_BCMD:
			default:
				ping.activate();
				throw new CommandException(0);
		}
	}
	
	@IndirectTimeout @RequiresPingHandler
	private int scmd(ServerCommand scmd) throws DisconnectException {
		if(scmd.get(0) != null) {
			writeInt(DAT_SCMD);
			writeUTF(scmd.get(0));
			return readInt();
		}
		else {
			return 0;
		}
	}
	
	@IndirectTimeout @RequiresPingHandler
	private <Type extends Data> void sendData(Type outbound) throws DisconnectException {
		writeInt(Type.getTypeID());
		writeInt(outbound.getSize());
		for(String s : outbound.getArray()) writeUTF(s);
	}
	
	@IndirectTimeout @RequiresPingHandler
	private Data getData(@Pointer Data inbound) throws DisconnectException {
		inbound.reset();
		int size = readInt();
		for(int i = 0; i < size; i++) inbound.add(readUTF());
		return inbound;
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
	
	@PingHandler @DirectKiller
	public void disconnect() throws DisconnectException {
		ping.pause();
		writeInt(DAT_NULL);
		ping.activate();
	}
	
	@Override
	public int getID() {
		return 500;
	}
	
	@Override
	@LeaveBlank
	public void setParent(SwiftNetContainer c) {
		
	}
	
	@Override
	public SwiftNetContainer getParent() {
		return parent;
	}
	
	@Override
	public void kill() {
		parent.terminate(this);
	}
	
	@Override
	public void close() {
		try {
			ping.stop();
			if(dis != null) dis.close();
			if(dos != null) dos.close();
			if(server != null) server.close();
		}
		catch(IOException ix) {
			error("Error closing sockets", LOG_FRC);
		}
	}
}