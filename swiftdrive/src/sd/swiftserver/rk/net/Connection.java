package sd.swiftserver.rk.net;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.type.Data;
import sd.swiftglobal.rk.type.Generic;
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
	
	private boolean online  = false,
					closing = false;

	private SwiftFile swap = null;
	private Data swap_data = null;

	private Terminator term;
	
	private SwiftFile output;
	
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
				echo("Receiving int ... ");
				int type = readInt();
				echo("Type: " + type);
				switch(type) {
					case DAT_NULL:
						kill();
						break;

					case DAT_PING:						
						echo("Pong", LOG_FRC);
						dos.writeInt(closing ? SIG_FAIL : SIG_READY);
						break;
					
					case DAT_FILE:
						echo("Moving data to swap file");
						swap = new SwiftFile(dis);
						swap.resetPos();
						break;
						
					case DAT_SCMD:
						echo("Command Started");
						command();
						break;

					case DAT_DATA:
						echo("Creating generic object");
						swap_data = new Generic();
						int size = readInt();
						echo("Size: " + size);
						for(int i = 0; i < size; i++) swap_data.add(readUTF());
						for(String s : swap_data.getArray()) echo(s, LOG_FRC);
						break;
				}
			}
		}
		catch(IOException ix) {
			kill();
			ix.printStackTrace();
		}
	}
	
//	/**
//	 * Receive generic data type from socket
//	 * @param template Data to write information from socket to
//	 * @return The data in the parameters with new information
//	 * @throws IOException If something fails while reading from the socket 
//	 */
//	public Data getData(Data template) throws IOException {
//		template.reset();
//		int size = readInt();
//		for(int i = 0; i < size; i++) template.add(readUTF());
//		return template;
//	}
	
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
	
	public void command() throws IOException {
		String commandLine = readUTF();
		String[] split = commandLine.split(":");
		String path = split[0];
		int command = split.length == 2 ? Integer.parseInt(split[1]) : 0,
			status  = 0;
		boolean append = false,
				send   = true;

		if(split[0] != null)		
			try {
				if(!split[0].equals("")) echo(split[0]);
				switch(command) {
					case CMD_READ_FILE:
						swap = new SwiftFile(split[0], true);
						break;
					case CMD_SEND_FILE:
						send = false;
						if(swap != null) {
							writeInt(SIG_READY);
							swap.send(dos);
						}
						else {
							writeInt(SIG_FAIL);
						}
						break;


					case CMD_READ_DATA:
						swap_data = new SwiftFile(split[0], true);
						break;
					case CMD_SEND_DATA:
						send = false;
						if(swap != null) {
							writeInt(SIG_READY);
							sendData(swap_data);
						}
						else {
							writeInt(SIG_FAIL);
						}
						break;

					case CMD_APPND_FILE:
						append = true;
					case CMD_WRITE_FILE:
						swap.write(new File(path).toPath(), append);
						break;
					case CMD_APPND_DATA:
						append = true;
					case CMD_WRITE_DATA:
						SwiftFile temp = new SwiftFile(0);
						temp.convert(swap_data);
						temp.write(new File(path).toPath(), append);
						break;
			}
		}
		catch(FileException fx) { 
			fx.printStackTrace();
			kill();
		}
		catch(IOException ix) {
			ix.printStackTrace();
			kill();
		}
		
		if(send) dos.writeInt(status);
	}

	private void writeInt(int i) {
		try {
			dos.writeInt(i);
		}
		catch(IOException ix) {
			ix.printStackTrace();
			kill();
		}
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
		System.out.println("Server close");
		close();
		server.dereference(this);
	}
	
	private String readUTF() throws IOException {
		term = new Terminator(this);
		term.run();
		String rtn = dis.readUTF();
		term.cancel();
		return rtn;
	}
	
	private int readInt() throws IOException {
		term = new Terminator(this);
		term.run();
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
	
	@Deprecated
	public void echo(Object str) {
		try {
			if(output == null) output = new SwiftFile(LC_PATH + "con" + getID(), false);
			output.reset();
			if(str.toString().equals("") || str == null) output.add(" ");
			else output.add(str.toString() + "\n");
			output.toData();
			output.append();
		}
		catch(IOException ix) {
			ix.printStackTrace();
		}
		catch(FileException fx) {
			fx.printStackTrace();
		}
	}

	@Deprecated
	public void echo(Object str, int level) {
		echo(str);
	}
}
