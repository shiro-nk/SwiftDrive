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
		echo("Initializing connection " + id, LOG_PRI);
		this.server = server;
		this.socket = socket;
		CLIENT_ID   = id;
		dis = new DataInputStream(socket.getInputStream());
		dos = new DataOutputStream(socket.getOutputStream());
		online = true;
		echo("Connection " + id + " is ready", LOG_PRI);
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
				echo("Waiting for the client", LOG_PRI);
				echo("Listening for a type identifier", LOG_SEC);
				int type = readInt();
				echo("Type " + type + " received", LOG_SEC);
				if(!loggedin) {
					echo("Warning: The client is not logged in to the server", LOG_SEC);
					if(type == DAT_LGIN) login();
					if(type == DAT_FILE) new SwiftFile(dis);
				}
				else {
					switch(type) {
						case DAT_NULL:
							echo("Received the connection kill signal", LOG_PRI);
							kill();
							break;

						case DAT_PING:
							echo("Pong", LOG_LOW);
							dos.writeInt(closing ? SIG_FAIL : SIG_READY);
							break;
					
						case DAT_FILE:
							echo("Downloading a file from the client", LOG_PRI);
							swap = new SwiftFile(dis);
							swap.resetPos();
							echo("File download complete", LOG_SEC);
							break;
						
						case DAT_SCMD:
							command();
							break;

						case DAT_DATA:
							echo("Downloading data from the client and storing in swap storage", LOG_PRI);
							swap_data = new Generic();
							int size = readInt();
							for(int i = 0; i < size; i++) swap_data.add(readUTF());
							for(String s : swap_data.getArray()) echo(s, LOG_FRC);
							echo("Download complete", LOG_SEC);
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
		echo("Starting data transfer to client", LOG_SEC);
		dos.writeInt(data.getSize());
		for(String s : data.getArray()) dos.writeUTF(s);
		echo("Data transfer complete", LOG_SEC);
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
		echo("Processing command request", LOG_PRI);
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
						echo("Reading file " + path + " into swap", LOG_SEC);
						swap = new SwiftFile(path, true);
						break;
					case CMD_SEND_FILE:
						echo("Starting file transfer to client", LOG_SEC);
						send = false;
						if(swap != null) {
							writeInt(SIG_READY);
							swap.send(dos);
							echo("File transfer complete", LOG_SEC);
						}
						else {
							echo("File tranfser failed", LOG_SEC);
							writeInt(SIG_FAIL);
						}
						break;


					case CMD_READ_DATA:
						echo("Reading data file " + path + " into swap", LOG_SEC);
						swap_data = new SwiftFile(path, true);
						break;
					case CMD_SEND_DATA:
						echo("Starting stage 1 data transfer to client", LOG_SEC);
						send = false;
						if(swap_data != null) {
							writeInt(SIG_READY);
							sendData(swap_data);
							echo("Stage 1 data transfer complete", LOG_SEC);
						}
						else {
							echo("Data transfer failed");
							writeInt(SIG_FAIL);
						}
						break;

					case CMD_APPND_FILE:
						append = true;
					case CMD_WRITE_FILE:
						echo((append ? "Appending" : "Writing") + " file to disk", LOG_SEC);
						swap.write(new File(path).toPath(), append);
						break;
					case CMD_APPND_DATA:
						append = true;
					case CMD_WRITE_DATA:
						echo((append ? "Appending" : "Writing") + " data to disk", LOG_SEC);
						SwiftFile temp = new SwiftFile(0);
						temp.convert(swap_data);
						temp.write(new File(path).toPath(), append);
						break;
			}
		}
		catch(FileException fx) {
			//TODO prevent connection death as a result of bad files
			echo("An error occured while attempting to read from/write to disk", LOG_PRI);
			dos.writeInt(SIG_FAIL);
			send = false;
		}
		catch(IOException ix) {
			kill();
		}
		
		if(send) dos.writeInt(status);
		echo("Command request completed with status " + status, LOG_PRI);
	}

	private void login() throws IOException {
		echo("Processing login request", LOG_PRI);
		String username = readUTF();
		int    size = readInt();
		byte[] pass = readByteArray(size);
		boolean rtn = false;
		user = server.getUserlist().getUser(username); 
		rtn = user != null ? Arrays.equals(user.getPassword(), pass) ? true : false : false;	
		dos.writeBoolean(rtn);
		loggedin = rtn;
		
		if(rtn) {
			echo("Login request approved", LOG_PRI);
			dos.writeUTF(user.toString());
			dos.writeUTF(LC_DIV);
		}
		else {
			echo("Login request denied. Connection will now close", LOG_PRI);
			kill();
		}
		echo("Login request completed with status " + rtn, LOG_PRI);
	}

	private void writeInt(int i) {
		try {
			echo("Writing integer to socket", LOG_LOW);
			dos.writeInt(i);
			print("... done", LOG_LOW);
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
		echo("Warning: Console id changed to " + id, LOG_PRI);
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
			echo("Reading string from socket", LOG_LOW);
			term = new Terminator(this);
			term.run();
			String rtn = dis.readUTF();
			term.cancel();
			print("... done", LOG_LOW);
			return rtn;
		}
		catch(SocketException | EOFException exc) {
			throw new IOException("Fatal error: Connection Lost");
		}
	}
	
	private int readInt() throws IOException {
		try {
			echo("Reading integer from socket", LOG_LOW);
			term = new Terminator(this);
			term.run();
			int rtn = dis.readInt();
			term.cancel();
			print("... done", LOG_LOW);
			return rtn;
		}
		catch(SocketException | EOFException exc) {
			throw new IOException("Fatal error: Connection Lost");
		}
	}

	private byte[] readByteArray(int size) throws IOException {
		echo("Reading byte array from socket", LOG_LOW);
		byte[] rtn = new byte[size];
		for(int i = 0; i < size; i++) {
			term = new Terminator(this);
			term.run();
			rtn[i] = dis.readByte();
			term.cancel();
		}
		print("... done", LOG_LOW);
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
	
	@Deprecated
	public void echo(Object str, int level) {
		print("[Con" + getID() + "] " + str.toString() + "\n", level);
	}
}
