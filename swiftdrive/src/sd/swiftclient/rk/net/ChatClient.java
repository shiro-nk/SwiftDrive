package sd.swiftclient.rk.net;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import sd.swiftglobal.rk.Meta.LeaveBlank;
import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.DisconnectException;
import sd.swiftglobal.rk.type.Data;
import sd.swiftglobal.rk.util.Logging;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetContainer;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetTool;

/* This file is part of Swift Drive				   *
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

public class ChatClient extends Data implements SwiftNetTool, Settings, Logging, Closeable {
	private Socket server;
	private Client client;
	private SwiftNetContainer parent;
	private int id;
	private Thread inthread;
	private DataInputStream dis;
	private DataOutputStream dos;
	
	private boolean online = false;

	public ChatClient(String hostname, int port, SwiftNetContainer parent, Socket server, Client client) throws DisconnectException {
		super(DAT_DATA, 0);
		this.server = server;
		this.client = client;
		this.parent = parent;
		id = this.client.getID();
		
		try {
			this.server = new Socket(hostname, port);
			dis = new DataInputStream(server.getInputStream());
			dos = new DataOutputStream(server.getOutputStream());
			
			inthread = new Thread(new Runnable() {
				public void run() {
					while(online) {
						try {
							pushMessageToStack(dis.readUTF());
						}
						catch(IOException ix) {
							kill();
						}
					}
				}
			});
		}
		catch(IOException ix) {
			kill();
			throw new DisconnectException(EXC_CONN, ix);
		}
	}
	
	public void start() {
		inthread.start();
	}
	
	public void send(String message) throws DisconnectException {
		try {
			dos.writeUTF(id + ";" + message);
		}
		catch(IOException ix) {
			throw new DisconnectException(EXC_WRITE, ix);
		}
	}
	
	public boolean hasNext() {
		return false;
	}
	
	private void pushMessageToStack(String raw) throws IOException {
		add(raw);
		String[] split = raw.split();
		echo(split[0] + ": " + split[1]);
	}
	
	@Override
	public int getID() {
		return id;
	}

	public void kill() {
		close();
		getParent().dereference(this);
	}
	
	@Override @LeaveBlank
	public void setParent(SwiftNetContainer c) {
	
	}

	@Override
	public SwiftNetContainer getParent() {
		return parent;
	}
	
	@LeaveBlank @Override public void convert(Data dat) {}
	@LeaveBlank @Override public void toData() {}
	@LeaveBlank @Override public void fromData() {}

	@Override
	public void close() {
		try {
			if(server != null) server.close();
			if(dis != null) dis.close();
			if(dos != null) dos.close();
		}
		catch(IOException ix) {
			error("Error closing sockets", LOG_FRC);
		}
	}
}
