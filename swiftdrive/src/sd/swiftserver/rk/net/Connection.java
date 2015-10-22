package sd.swiftserver.rk.net;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.type.Data;
import sd.swiftglobal.rk.type.SwiftFile;
import sd.swiftglobal.rk.util.Logging;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetContainer;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetTool;

/* This file is part of Swift Drive				   *
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

public class Connection implements SwiftNetTool, Runnable, Closeable, Settings, Logging {
	
	private final Server server;
	private final Socket socket;
	private final int CLIENT_ID;
	
	private DataInputStream  dis = null;
	private DataOutputStream dos = null;
	
	private boolean online = false;

	private SwiftFile swap = null;
	private Data swap_data = null;
	
	public Connection(Server server, Socket socket, int id) throws IOException {
		this.server = server;
		this.socket = socket;
		CLIENT_ID   = id;
		dis = new DataInputStream(socket.getInputStream());
		dos = new DataOutputStream(socket.getOutputStream());
		online = true;
	}
	
	public void run() {
		try {
			while(online) {
				System.out.println("listen for int");
				int type = dis.readInt();
				switch(type) {
					case DAT_NULL:
						System.out.println("shutdown calls");
						close();
						break;
					case DAT_PING:
						echo("Pinged!", LOG_FRC);
						break;
					case DAT_SCMD:
						break;
					case DAT_DATA:
						System.out.println("Got data");
						dataHandler();
						break;
					case DAT_FILE:
						swap = new SwiftFile(dis);
						swap.resetPos();
						break;
				}
			}
		}
		catch(IOException ix) {
			ix.printStackTrace();
		}
	}
	
	public Data dataHandler() throws IOException {
		int type = dis.readInt();
		switch(type) {
			case DAT_DATA:
				swap_data = getData(new SwiftFile(0));
				for(String s : swap_data.getArray()) echo(s);
				break;
			default:
		}
		
		return null;
	}
	
	public Data getData(Data template) throws IOException {
		template.reset();

		int size = dis.readInt();
		for(int i = 0; i < size; i++) template.add(dis.readUTF());
		return template;
	}
	
	public <Type extends Data> void sendData(Type template) throws IOException {
		
	}
	
	public void fileHandler() {
		
	}
	
	public void pingHandler() {
		
	}
	
	public boolean isOnline() {
		return online;
	}

	public void setParent(SwiftNetContainer c) {
		
	}
	
	public SwiftNetContainer getParent() {
		return server;
	}
	
	public int getID() {
		return CLIENT_ID;
	}
	
	public void kill() {
		close();
		server.terminate(this);
	}
	
	public void close() {
		try {
			if(dis    != null) dis.close();
			if(dos    != null) dos.close();
			if(socket != null) socket.close();
			online = false;
		}
		catch(IOException ix) {
			
		}
	}
}
