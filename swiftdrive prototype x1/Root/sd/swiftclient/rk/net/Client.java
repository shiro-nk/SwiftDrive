package sd.swiftclient.rk.net;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.DisconnectException;
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
public class Client extends Thread implements Settings, Closeable, Logging {
	
	Socket server = null;

	DataInputStream  dis = null;
	DataOutputStream dos = null;
	
	final String host;
	final int    port;

	boolean online = false,
			active = false;
	
	public Client(String hostname, int port) throws DisconnectException {
		echo("Client has been initialized");
		
		this.port = port;
		this.host = hostname;
		
		try {
			server = new Socket(this.host, this.port);
			echo("Connected to " + host + ":" + port);
			
			try {
				dis = new DataInputStream(server.getInputStream());
				dos = new DataOutputStream(server.getOutputStream());
				
				online = true;
			}
			catch(IOException ix) {
				online = false;
			}
		}
		catch(IOException ix) {
			throw new DisconnectException(ix.getMessage());
		}
	}

	public Client(String hostname) throws DisconnectException {
		this(hostname, DEF_PORT);
	}
	
	public Client(byte ip1, byte ip2, byte ip3, byte ip4, int port) throws DisconnectException {
		this("" + ip1 + ip2 + ip3 + ip4, port);
	}
	
	public Client(byte ip1, byte ip2, byte ip3, byte ip4) throws DisconnectException {
		this("" + ip1 + ip2 + ip3 + ip4, DEF_PORT);
	}
	
	public void run() {
		while(online) {
			try {
				Thread.sleep(1000);
			}
			catch(InterruptedException ix) {
				error(ix.getMessage());
			}
		}
	}
	
	public int getInt() {
		try {
			return dis.readInt();
		}
		catch(IOException ix) {
			error("Error while reading integer from socket");
			error(ix.getMessage());
			return DAT_NULL;
		}
	}
	
	public String in() {
		try {
			return dis.readUTF();
		}
		catch(IOException ix) {
			error("Error while reading a string");
			return "";
		}
	}
	
	public void out(String out) {
		try {
			dos.writeInt(DAT_DATA);
			dos.writeInt(1);
			dos.writeUTF(out);
		}
		catch(IOException ix) {
			error(ix.getMessage());
		}
	}
	
	public void close() {
		try {
			if(dis != null) dis.close();
			if(dos != null) dos.close();
			
			if(server != null) server.close();
		}
		catch(IOException ix) {
			error("Failed to close resources!");
			error(ix.getMessage());
		}
	}
	
	public void fileOut(File file) {
		try {
			SwiftFile fi = new SwiftFile(file);
			
			fi.toData();
			for(String s : fi.getData()) {
				echo(s);
			}
			
			try {
				dos.writeInt(DAT_FILE);
				dos.writeInt(fi.getFileSize());
				
				if(getInt() == SIG_READY) {
					echo("SIG_READY received!");
					for(byte b : fi.getBytes()) {
						dos.writeByte(b);
						echo(b);
					}
				}
			}
			catch(IOException ix) {
				error("Error while writing files to socket");
				error(ix.getMessage());
			}
		}
		catch(FileException fx) {
			error("Failed to send file");
		}
	}

	public void fileOut(String path) {
		fileOut(new File(path));
	}
}