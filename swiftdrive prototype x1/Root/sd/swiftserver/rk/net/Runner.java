package sd.swiftserver.rk.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.DisconnectException;
import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.types.Data;
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
			int dat  = DAT_INIT,
			    size = 0;
			
			while(online && (dat = getInt()) != DAT_NULL) {
				size = getInt();
				
				switch(dat) {
					case DAT_PING:
						break;

					case DAT_FILE:
						getFile(size);
						break;
					
					case DAT_DATA:
						String s = in();
						System.out.println(s);
						break;
				}
			}
		}
		catch(DisconnectException dx) {
			error("Fatal Error! Data may be out of sync. Client will be disconnected", LOG_FRC);
			error(dx.getMessage(), LOG_SEC);
		}
		finally {
			close();
		}
	}
	
	public void pingHandler() {
		
	}
	
	public void getFile(int size) throws DisconnectException {
		try {
			String path = in();
			ready();
			SwiftFile fi = new SwiftFile(LC_PATH + path, size, dis);
			fi.writeFile();
		}
		catch(FileException fx) {
			
		}
	}
	
	public void dataHandler() {
		
	}
	
	public void ready() throws DisconnectException {
		try {
			dos.writeInt(SIG_READY);
		}
		catch(IOException ix) {
			throw new DisconnectException("Error while writing SIG_READY", ix);
		}
	}
	
	public int getInt() throws DisconnectException {
		try {
			return dis.readInt();
		}
		catch(IOException ix) {
			throw new DisconnectException("Error while reading an integer", ix);
		}
	}
	
	public String in() throws DisconnectException {
		try {
			return dis.readUTF();
		}
		catch(IOException ix) {
			throw new DisconnectException("Error while reading a string", ix);
		}
	}
	
	public Data in(Data template, int size) throws DisconnectException {
		template.clear();
		for(int i = 0; i < size; i++) template.add(in());
		return template;
	}
	
	public void out(String s) throws DisconnectException {
		try {
			dos.writeUTF(s);
		}
		catch(IOException ix) {
			throw new DisconnectException(ix.getMessage());
		}
	}

	public void out(Data d) throws DisconnectException {
		for(int i = 0; i < d.getSize(); i++) out(d.next());
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
			error(ix.getMessage(), LOG_FRC);
		}
	}
}