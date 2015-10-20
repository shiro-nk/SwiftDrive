package sd.swiftserver.rk.net;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.type.Data;
import sd.swiftglobal.rk.util.Logging;

/* This file is part of Swift Drive				   *
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

public class Connection implements Runnable, Closeable, Settings, Logging {
	
	private final Server server;
	private final Socket socket;
	private final int CLIENT_ID;
	
	private DataInputStream  dis = null;
	private DataOutputStream dos = null;
	
	private boolean online = false;
	
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
			//Data last = null;
			
			while(online) {
				int type = dis.readInt();
				
				switch(type) {
					case DAT_PING:
						break;
					case DAT_SCMD:
						break;
					case DAT_DATA:
						break;
					case DAT_FILE:
						break;
				}
			}
		}
		catch(IOException ix) {
			
		}
	}
	
	public void dataHandler(Data d) {
		
	}
	
	public void fileHandler() {
		
	}
	
	public void pingHandler() {
		
	}
	
	public void close() {
		server.rmClient(CLIENT_ID);
		
		try {
			if(dis    != null) dis.close();
			if(dos    != null) dos.close();
			if(socket != null) socket.close();
		}
		catch(IOException ix) {
			
		}
	}
}
