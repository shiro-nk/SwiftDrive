package sd.swiftserver.rk.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.types.SwiftFile;
import sd.swiftglobal.rk.util.Logging;

/*
 * This file is part of Swift Drive
 * Copyright (C) 2015 Ryan Kerr
 *
 * Swift Drive is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * Swift Drive is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of  MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Swift Drive. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * 
 * @author Ryan Kerr
 */
public class Runner implements Runnable, Settings, Logging {
	
	Server server = null;
	Socket client = null;
	
	DataInputStream  dis = null;
	DataOutputStream dos = null;
	
	boolean online = false;
	
	final int CLIENT_ID;
	
	public Runner(Socket client, Server server, int id) {
		this.client = client;
		this.server = server;
		CLIENT_ID   = id;
		
		try {
			dis = new DataInputStream(client.getInputStream());
			dos = new DataOutputStream(client.getOutputStream());
			
			online = true;
		}
		catch(IOException ix) {
			error("Error establishing sockets: " + ix.getMessage());
			online = false;
		}
	}
	
	public void run() {
		try {
			int dat,
			    size;
			
			while(online && (dat = getInt()) != DAT_OFF) {
				size = getInt();
				
				switch(dat) {
					case DAT_PING:
						break;
					case DAT_FILE:
						echo("Ready to receive file of " + size + " bytes", LOG_SEC);
						ready();
						SwiftFile fi = new SwiftFile(LC_PATH + "output", size, dis);
						fi.writeFile(false);
						break;
					case DAT_DATA:
						String s = dis.readUTF();
						System.out.println(s);
						break;
				}
			}
		}
		catch(IOException ix) {
			error("Failed to do something of the sort" + ix.getMessage());
		}
		catch(FileException fx) {
			error("Error reading file");
			error(fx.getMessage());
		}
	}
	
	public void ready() {
		try {
			dos.writeInt(SIG_READY);
		}
		catch(IOException ix) {
			error("Error while writing SIG_READY to socket");
			error("Disconnecting client from server");
			close();
		}
	}
	
	public int getInt() {
		try {
			System.out.println("Listening for an int on client " + CLIENT_ID);
			return dis.readInt();
		}
		catch(IOException ix) {
			error("Error reading integer from client: " + ix.getMessage());
			return DAT_OFF;
		}
	}
	
	public String in() {
		return "output";
	}
	
	public void out(String s) {
		
	}
	
	public void fileOut() {
		
	}

	public void close() {
		online = false;
		server.removeClient(CLIENT_ID);

		try {
			if(dis != null) dis.close();
			if(dos != null) dos.close();
		}
		catch(IOException ix) {
			error("Failed to close resources!");
			error(ix.getMessage());
		}
	}
}