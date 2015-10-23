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
import sd.swiftglobal.rk.util.Terminator;

/* This file is part of Swift Drive				   *
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

/**
 * The connection manages incoming information from clients
 *
 * @author Ryan Kerr
 */
public class Connection implements SwiftNetTool, Runnable, Closeable, Settings, Logging {
	private final Server server;
	private final Socket socket;
	private final int CLIENT_ID;
	
	private DataInputStream  dis = null;
	private DataOutputStream dos = null;
	
	private boolean online = false;

	private SwiftFile swap = null;
	private Data swap_data = null;
	
	/**
	 * Creates DataInput and DataOutput streams for communication
	 * @param server Parent server (required for termination)
	 * @param socket Socket to client
	 * @param id Connection number
	 * @throws IOException If getting Input/Output streams fail
	 */
	public Connection(Server server, Socket socket, int id) throws IOException {
		this.server = server;
		this.socket = socket;
		CLIENT_ID   = id;
		dis = new DataInputStream(socket.getInputStream());
		dos = new DataOutputStream(socket.getOutputStream());
		online = true;
	}
	
	/**
	 * <b>Server listening system</b><br>
	 * Starts by listening for an integer that specifies the inbound data type <br>
	 * then acts according to the type (for example, listen for an integer that <br>
	 * specifies file size) <br>
	 * <br>
	 * This method will stop running if the connection is closed (online = false)
	 */
	public void run() {
		try {
			while(online) {
				System.out.println("Receiving int ... ");
				int type = readInt();
				System.out.println("Super Type: " + type);
				switch(type) {
					case DAT_NULL:
						System.out.println("shutdown calls");
						close();
						break;
					case DAT_PING:
						echo("Pinged!", LOG_FRC);
						dos.writeInt(DAT_PING);
						break;
					case DAT_SCMD:
						break;
					case DAT_DATA:
						System.out.println("Got data");
						dataHandler();
						dos.writeInt(5);
						break;
					case DAT_FILE:
						swap = new SwiftFile(dis);
						swap.resetPos();
						break;
				}
			}
		}
		catch(IOException ix) {
			kill();
			ix.printStackTrace();
		}
	}
	
	public Data dataHandler() throws IOException {
		swap_data = getData(new SwiftFile(0));
		for(String s : swap_data.getArray()) echo(s);
		return swap_data;
	}
	
	/**
	 * Receive generic data type from socket
	 * @param template Data to write information from socket to
	 * @return The data in the parameters with new information
	 * @throws IOException If something fails while reading from the socket 
	 */
	public Data getData(Data template) throws IOException {
		template.reset();
		int size = readInt();
		for(int i = 0; i < size; i++) template.add(readUTF());
		return template;
	}
	
	/**
	 * Send data over socket
	 * @param data Data to send (Generic due to the Type.getTypeID requirement)
	 * @throws IOException If something fails while writing to the socket
	 */
	public <Type extends Data> void sendData(Type data) throws IOException {
		dos.writeInt(Type.getTypeID());
		dos.writeInt(data.getSize());
		for(String s : data.getArray()) dos.writeUTF(s);
	}
	
	public void fileHandler() {
		
	}
	
	public void pingHandler() {
		
	}

	/** @return False if connection is down **/
	public boolean isOnline() {
		return online;
	}

	/* This is already set in the constructor */
	public void setParent(SwiftNetContainer c) { /* null */ }
	
	/** @return The controlling server (allows calling terminate() externally) **/
	public SwiftNetContainer getParent() {
		return server;
	}
	
	/** @return The array index given in the constructor **/
	public int getID() {
		return CLIENT_ID;
	}
	
	/** Close and remove the index of the connection from the parent **/
	public void kill() {
		close();
		server.terminate(this);
	}
	
	private String readUTF() throws IOException {
		Terminator term = new Terminator(this, 30);
		String rtn = dis.readUTF();
		term.cancel();
		return rtn;
	}
	
	private int readInt() throws IOException {
		Terminator term = new Terminator(this, 30);
		int rtn = dis.readInt();
		term.cancel();
		return rtn;
	}
	
	/** Allows for try-with-resources **/
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