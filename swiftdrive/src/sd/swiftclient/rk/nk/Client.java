package sd.swiftclient.rk.nk;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.DisconnectException;
import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.type.Data;
import sd.swiftglobal.rk.type.SwiftFile;
import sd.swiftglobal.rk.util.Logging;

/* This file is part of Swift Drive				   *
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

public class Client extends Thread implements Settings, Logging, Closeable, Runnable {
	
	private final Socket server;
	
	private DataInputStream  dis;
	private DataOutputStream dos;
	
	private boolean online = false;
	
	public Client(String hostname, int port) throws DisconnectException {
		try {
			server = new Socket(hostname, port);
			dis = new DataInputStream(server.getInputStream());
			dos = new DataOutputStream(server.getOutputStream());
			online = true;
		}
		catch(IOException ix) {
			online = false;
			throw new DisconnectException(EXC_CONN);
		}
	}
	
	public void run() {
		//TODO: Add ping and timeout system
		try {
			while(online) {
				//TODO: This is the ping loop
			}
		}
//		catch(IOException ix) {
		catch(Exception ex) {
				
		}
	}
	
	public Data getData(Data template) throws DisconnectException {
		template.reset();
		
		try {
			int size = dis.readInt();
			for(int i = 0; i < size; i++) template.add(dis.readUTF());
			return template;
		}
		catch(IOException ix) {
			close();
			throw new DisconnectException(EXC_NREAD, ix);
		}
	}
	
	public <Type extends Data> void sendData(Type data) throws DisconnectException {
		try {
			dos.writeInt(Type.getTypeID());
			dos.writeInt(data.getSize());
			for(String s : data.getArray()) dos.writeUTF(s);
		}
		catch(IOException ix) {
			close();
			throw new DisconnectException(EXC_CONN, ix);
		}
	}
	
	public SwiftFile getFile() throws DisconnectException {
		try {
			return new SwiftFile(dis);
		}
		catch(IOException ix) {
			close();
			throw new DisconnectException(EXC_CONN, ix);
		}
	}
	
	public void sendFile(SwiftFile file) throws DisconnectException, FileException {
		try {
			file.send(dos);
		}
		catch(IOException ix) {
			close();
			throw new DisconnectException(EXC_CONN, ix);
		}
	}
	
	public void disconnect() {
		
	}
	
	public void close() {
		try {
			if(dis != null) dis.close();
			if(dos != null) dos.close();
			if(server != null) server.close();
			online = false;
		}
		catch(IOException ix) {
			error("Error closing sockets", LOG_FRC);
		}
	}
}
