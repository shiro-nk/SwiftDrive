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
import sd.swiftglobal.rk.type.ServerCommand;
import sd.swiftglobal.rk.type.SwiftFile;
import sd.swiftglobal.rk.util.Logging;
import sd.swiftglobal.rk.util.Ping;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetContainer;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetTool;

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
		}
		catch(IOException ix) {
			throw new DisconnectException(EXC_CONN);
		}
	}
	
	public Data scmd(ServerCommand scmd, int type, Data output) throws DisconnectException {
		ping.standby();
		ping.deactivate();
		try {
			output.reset();
			dos.writeInt(DAT_SCMD);
			dos.writeUTF(scmd.next());
			int stype = dis.readInt();
			if(stype == type) {
				int size = dis.readInt();
				for(int i = 0; i < size; i++) output.add(dis.readUTF());
			}
			else {
				
			}
			return output;
		}
		catch(IOException ix) {
			throw new DisconnectException(0, ix);
		}
	}
	
	public <Type extends Data> int scmd(ServerCommand scmd, Type input) {
		return 0;
	}
	
	public SwiftFile scmd(ServerCommand scmd) throws FileException, DisconnectException {
		return null;
	}
	
	private Data receiveData(Data template) throws DisconnectException {
		template.reset();
		try {
			int size = dis.readInt();
			for(int i = 0; i < size; i++) template.add(dis.readUTF());
			return template;
		}
		catch(IOException ix) {
			kill();
			throw new DisconnectException(EXC_NREAD, ix);
		}
	}
	
	private <Type extends Data> void sendData(Type data) throws DisconnectException {
		try {
			echo("Ping lock", LOG_FRC);
			dos.writeInt(Type.getTypeID());
			dos.writeInt(data.getSize());
			for(String s : data.getArray()) dos.writeUTF(s);
		}
		catch(IOException ix) {
			kill();
			throw new DisconnectException(EXC_CONN, ix);
		}
	}
	
	public SwiftFile getFile() throws DisconnectException {
		try {
			return new SwiftFile(dis);
		}
		catch(IOException ix) {
			kill();
			throw new DisconnectException(EXC_CONN, ix);
		}
	}
	
	public void sendFile(SwiftFile file) throws DisconnectException, FileException {
		try {
			file.send(dos);
		}
		catch(IOException ix) {
			kill();
			throw new DisconnectException(EXC_CONN, ix);
		}
	}
	
	public void disconnect() throws DisconnectException {
		try {
			dos.writeInt(DAT_NULL);
		}
		catch(IOException ix) {
			kill();
			throw new DisconnectException(EXC_WRITE, ix);
		}
	}
	
	public int getID() {
		return 500;
	}
	
	public void setParent(SwiftNetContainer c) {
		
	}
	
	public SwiftNetContainer getParent() {
		return null;
	}
	
	public void kill() {
		parent.terminate(this);
	}
	
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
