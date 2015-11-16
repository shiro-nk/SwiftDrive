package sd.swiftserver.rk.net;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.type.Data;
import sd.swiftglobal.rk.type.Generic;
import sd.swiftglobal.rk.type.SwiftFile;
import sd.swiftglobal.rk.type.users.User;
import sd.swiftglobal.rk.util.Console;
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
	
	private int CLIENT_ID;
	
	private DataInputStream  dis = null;
	private DataOutputStream dos = null;
	
	private boolean online   = false,
					closing  = false,
					loggedin = false;

	private SwiftFile swap = null;
	private Data swap_data = null;

	private Terminator term;
	private User user;

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
				if(!loggedin) {
					System.err.println("Warning: User is not logged in!");
					if(type == DAT_LGIN) login();
					if(type == DAT_FILE) new SwiftFile(dis);
				}
				else {
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
		}
		catch(IOException ix) {
			kill();
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
		dos.writeInt(data.getSize());
		for(String s : data.getArray()) dos.writeUTF(s);
	}
	
	/**
	 * Reads a String from a socket, converts it into a path and command. <br>
	 * Once split, the switch statement matches the command with the funciton<br>
	 * required to be carried out. <br><br>
	 * The commands identifiers are set in the CMD_* section of the settings class. <br>
	 * The commands are straightforward: read will store file data from the <b>path</b> into the swap <br>
	 * variable. Write does the opposite, taking the swap variable and storing it at <b>path</b>. <br>
	 * The send command is similar to write except rather than writing to a file, <br>
	 * the swap variable is written to the socket for the client to read.
	 * @throws IOException if the command could not be read from the socket
	 */
	public void command() throws IOException {
		String commandLine = readUTF();
		String[] split = commandLine.split(":");
		String path = split[0];
		int command = split.length == 2 ? Integer.parseInt(split[1]) : 0,
			status  = 0;
		boolean append = false,
				send   = true;

		if(path != null)	
			try {
				path = LC_PATH + path;
				switch(command) {
					case CMD_READ_FILE:
						swap = new SwiftFile(path, true);
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
						swap_data = new SwiftFile(path, true);
						break;
					case CMD_SEND_DATA:
						send = false;
						if(swap_data != null) {
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
			//TODO prevent connection death as a result of bad files
			dos.writeInt(SIG_FAIL);
		}
		catch(IOException ix) {
			kill();
		}
		
		if(send) dos.writeInt(status);
	}

	private void login() throws IOException {
		String username = readUTF();
		int    size = readInt();
		byte[] pass = readByteArray(size);
		boolean rtn = false;
		user = server.getUserlist().getUser(username); 
		rtn = user != null ? Arrays.equals(user.getPassword(), pass) ? true : false : false;	
		System.out.println("RTN: " + rtn);
		dos.writeBoolean(rtn);
		loggedin = rtn;
		
		if(rtn) {
			dos.writeUTF(user.toString());
			dos.writeUTF(LC_DIV);
		}
		else {
			kill();
		}
	}

	private void writeInt(int i) {
		try {
			dos.writeInt(i);
		}
		catch(IOException ix) {
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
	
	public void setID(int id) {
		CLIENT_ID = id;
	}

	/** @return The array index given in the constructor **/
	public int getID() {
		return CLIENT_ID;
	}

	public String getIP() {
		return socket.getInetAddress().toString();
	}

	public int getPort() {
		return socket.getPort();
	}

	public User getUser() {
		return user;
	}

	/** Close and remove the index of the connection from the parent **/
	public void kill() {
		close();
		server.dereference(this);
	}
	
	private String readUTF() throws IOException {
		try {
			term = new Terminator(this);
			term.run();
			String rtn = dis.readUTF();
			term.cancel();
			return rtn;
		}
		catch(SocketException | EOFException exc) {
			throw new IOException("Fatal error: Connection Lost");
		}
	}
	
	private int readInt() throws IOException {
		try {
			term = new Terminator(this);
			term.run();
			int rtn = dis.readInt();
			term.cancel();
			return rtn;
		}
		catch(SocketException | EOFException exc) {
			throw new IOException("Fatal error: Connection Lost");
		}
	}

	private byte[] readByteArray(int size) throws IOException {
		byte[] rtn = new byte[size];
		for(int i = 0; i < size; i++) {
			term = new Terminator(this);
			term.run();
			rtn[i] = dis.readByte();
			term.cancel();
		}
		return rtn;
	}
	
	/** Allows for try-with-resources **/
	public void close() {
		try {
			if(dis    != null) dis.close();
			if(dos    != null) dos.close();
			if(socket != null) socket.close();
			if(term   != null) term.cancel();
			online = false;
		}
		catch(IOException ix) {
			
		}
	}
	
	Console con = new Console("Connection " + getID());
	@Deprecated
	public void echo(Object str) {
		con.setTitle("Connection " + getID());
		con.append(str.toString());
	}

	@Deprecated
	public void echo(Object str, int level) {
		echo(str);
	}
}
