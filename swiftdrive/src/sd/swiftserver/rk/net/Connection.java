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
import sd.swiftglobal.rk.type.tasks.SubTask;
import sd.swiftglobal.rk.type.tasks.Task;
import sd.swiftglobal.rk.type.users.User;
import sd.swiftglobal.rk.util.Logging;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetContainer;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetTool;

/* This file is part of Swift Drive				   *
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

/**
 * <b>Connection</b><br>
 * Controls data I/O between the server computer and the client.
 *
 * @author Ryan Kerr
 */
public class Connection implements SwiftNetTool, Runnable, Closeable, Settings, Logging {
	private final Server server;
	private final Socket socket;
	
	private int CLIENT_ID,
				kill;
	
	private DataInputStream  dis = null;
	private DataOutputStream dos = null;
	
	private boolean online   = false,
					loggedin = false;

	private SwiftFile swap = null;
	private Data swap_data = null;

	private User user;

	/**
	 * <b>Initialize Connection:</b><br>
	 * Once a connection is established by the server, the server will
	 * initialize the connection class. This opens the I/O streams
	 * required for the transfer of data over the network socket.
	 * In order to verify that the server and client are compatible,
	 * version() is called. If version() is false, the connection is
	 * terminated immediately.
	 *
	 * @param server Parent server (required for termination)
	 * @param socket Socket to client
	 * @param id Connection number
	 * @throws IOException If getting Input/Output streams fail
	 */
	public Connection(Server server, Socket socket, int id) throws IOException {
		CLIENT_ID = id;
		echo("Initializing connection", LOG_PRI);
		this.server = server;
		this.socket = socket;
		this.socket.setSoTimeout(DEF_TIME * 1000);
		dis = new DataInputStream(socket.getInputStream());
		dos = new DataOutputStream(socket.getOutputStream());

		if(version()) {
			online = true;
			echo("Connection " + id + " is ready", LOG_PRI);
		}
		else {
			echo("Client incompatible! Closing", LOG_PRI);
			kill(EXC_VER);
			throw new IOException("Versions incompatible");
		}
	}
	
	/**
	 * <b>Receiver:</b><br>
	 * Starts by listening for an integer that specifies the inbound data type <br>
	 * then acts according to the type (for example, listen for an integer that <br>
	 * specifies file size) <br><br>
	 *
	 * This method will stop running if the connection is closed (online = false)
	 */
	public void run() {
		try {
			while(online) {
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
							kill(EXC_SAFE);
							break;

						case DAT_PING:
							echo("Pong", LOG_LOW);
							dos.writeInt(server.closing() ? SIG_FAIL : SIG_READY);
							break;
					
						case DAT_FILE:
							echo("Downloading a file from the client", LOG_SEC);
							swap = new SwiftFile(dis);
							swap.resetPos();
							echo("File download complete", LOG_SEC);
							break;
						
						case DAT_SCMD:
							command();
							break;

						case DAT_DATA:
							echo("Downloading data from the client and storing in swap", LOG_SEC);
							swap_data = new Generic();
							int size = readInt();
							for(int i = 0; i < size; i++) swap_data.add(readUTF());
							for(String s : swap_data.getArray()) echo("Received: " + s, LOG_PRI);
							echo("Download complete", LOG_SEC);
							break;

						case DAT_DIRC:
							break;

						case DAT_VERS:
							echo("Returning version info");
							break;

						case DAT_STSK:
							String atskname = readUTF(),
								   stskname = readUTF();

							Task atask = server.getTasklist().get(atskname);

							if(atask != null) {
								writeInt(SIG_READY);
								SubTask subtask = atask.get(stskname);
								
								if(subtask != null)	{
									writeInt(SIG_READY);
									subtask.setStatus(readInt());
								}
								else writeInt(SIG_FAIL);
							}
							else {
								writeInt(SIG_FAIL);
							}

							try {
								atask.write();
							}
							catch(FileException fx) {

							}
							break;
						
						case DAT_DSBT:
							String btskname = readUTF(),
								   sbtkname = readUTF();

							Task btask = server.getTasklist().get(btskname);

							if(btask != null) {
								SubTask subtask = btask.get(sbtkname);
							
								if(subtask != null)	{
									writeInt(SIG_READY);
									dos.writeUTF(subtask.toString());
								}
								else {
									writeInt(SIG_FAIL);
								}
							}
							else {
								writeInt(SIG_FAIL);
							}

							try {
								btask.write();
							}
							catch(FileException fx) {

							}
							break;
	
						case DAT_DTSK:
							String ctskname = readUTF();
							Task ctask = server.getTasklist().get(ctskname);
	
							if(ctask != null) {
								writeInt(SIG_READY);
								SubTask[] csubtasks = ctask.getArray();
								writeInt(csubtasks.length);
	
								for(SubTask s : csubtasks) {
									dos.writeUTF(s.toString());
								}
							}
							else {
								writeInt(SIG_FAIL);
							}

							try {
								ctask.write();
							}
							catch(FileException fx) {

							}
							break;
					}
				}
			}
		}
		catch(IOException ix) {
			kill(EXC_CONN);
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
	 * <b>Send Data:</b><br>
	 * Writes the given data to socket.
	 *
	 * @param data Data to send (Generic due to the Type.getTypeID requirement)
	 * @throws IOException If something fails while writing to the socket
	 */
	public <Type extends Data> void sendData(Type data) throws IOException {
		echo("Starting data transfer to client", LOG_TRI);
		dos.writeInt(data.getSize());
		for(String s : data.getArray()) dos.writeUTF(s);
		for(String s : data.getArray()) echo("Sent: " + s, LOG_PRI);
		echo("Data transfer complete", LOG_TRI);
	}
	
	/**
	 * <b>Command Execution:</b><br>
	 *
	 * Reads a String from a socket, converts it into a path and command. <br>
	 * Once split, the switch statement matches the command with the funciton<br>
	 * required to be carried out. <br><br>
	 * The commands identifiers are set in the CMD_* section of the settings class. <br>
	 * The commands are straightforward: read will store file data from the <b>path</b> into the swap <br>
	 * variable. Write does the opposite, taking the swap variable and storing it at <b>path</b>. <br>
	 * The send command is similar to write except rather than writing to a file, <br>
	 * the swap variable is written to the socket for the client to read.
	 *
	 * @throws IOException if the command could not be read from the socket
	 */
	public void command() throws IOException {
		echo("Processing command request", LOG_SEC);
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
							for(String s : swap.getArray()) echo("* " + s);
							echo("File transfer complete", LOG_SEC);
						}
						else {
							echo("File tranfer failed", LOG_SEC);
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
						echo((append ? "Appending" : "Writing") + " file to " + path, LOG_SEC);
						swap.write(new File(path).toPath(), append);
						break;

					case CMD_APPND_DATA:
						append = true;
					case CMD_WRITE_DATA:
						echo((append ? "Appending" : "Writing") + " data to " + path, LOG_SEC);
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
			kill(EXC_CONN);
		}
		
		if(send) dos.writeInt(status);
		echo("Command request completed (" + status + ")", LOG_SEC);
	}

	/**
	 * <b>Login:</b><br>
	 * The login method reads the username then the password. The server
	 * then searches for a matching username in the local <b>UserHandler</b>.
	 * If no match is found, the login fails immediately and the client is
	 * notified. If a match is found, the password is compared to the
	 * local user password. If the password matches, all methods using
	 * net I/O are unlocked and the client is notified. Otherwise, the
	 * connection is terminated after notifying the client of the failure.
	 *
	 * @throws IOException when the connection is broken.
	 */
	private void login() throws IOException {
		echo("Processing login request", LOG_PRI);
		String username = readUTF();
		int    size = readInt();
		byte[] pass = readByteArray(size);
		boolean rtn = false;
		echo("Username: " + username + "; Password: " + new String(pass));
		user = server.getUserlist().get(username); 
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
			kill(EXC_LOGIN);
		}
		echo("Login request completed (" + rtn + ")", LOG_PRI);
	}

	/** Write integer to socket **/
	private void writeInt(int i) {
		try {
			echo("Writing integer to socket", LOG_LOW);
			dos.writeInt(i);
			echo("Done", LOG_LOW);
		}
		catch(IOException ix) {
			kill(EXC_NWRITE);
		}
	}
	
	/**
	 * <b>Read String from Socket</b><br>
	 * Reads a string from the socket
	 *
	 * @return String read from socket
	 * @throws IOException if failed to read a string from socket
	 */
	private String readUTF() throws IOException {
		try {
			echo("Reading string from socket", LOG_LOW);
			String rtn = dis.readUTF();
			echo("Done", LOG_LOW);
			return rtn;
		}
		catch(SocketException | EOFException exc) {
			echo("Failed", LOG_LOW);
			throw new IOException("Fatal error: Connection Lost");
		}
	}
	
	/**
	 * <b>Read Integer from Socket</b><br>
	 * Reads an integer from socket
	 *
	 * @return Integer read from socket
	 * @throws IOException if failed to read int from socket
	 */
	private int readInt() throws IOException {
		try {
			echo("Reading integer from socket", LOG_LOW);
			int rtn = dis.readInt();
			echo("Done", LOG_LOW);
			return rtn;
		}
		catch(SocketException | EOFException exc) {
			echo("Failed", LOG_LOW);
			throw new IOException("Fatal error: Connection Lost");
		}
	}

	/**
	 * <b>Read Byte Array</b><br>
	 * Reads an array of bytes from the socket.
	 *
	 * @param size Number of bytes to be read from the socket
	 * @return Array of bytes
	 * @throws IOException if there was an error while reading bytes
	 */
	private byte[] readByteArray(int size) throws IOException {
		echo("Reading byte array from socket", LOG_LOW);
		byte[] rtn = new byte[size];
		for(int i = 0; i < size; i++) {
			rtn[i] = dis.readByte();
		}
		echo("Done", LOG_LOW);
		return rtn;
	}

	/**
	 * <b>Version Verification:</b><br>
	 * Ensures the server and the client are of compatible versions.
	 * This is to prevent communication errors arising from different
	 * protocols being used.
	 *
	 * @return True if compatible; otherwise false
	 * @throws IOException if connection is lost
	 */
	private boolean version() throws IOException {
		double version = dis.readDouble();
		boolean rtn = version == VERSION;
		dos.writeBoolean(rtn);
		if(!rtn) dos.writeDouble(VERSION);
		return rtn;
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
	
	/** @param id New index position **/
	public void setID(int id) {
		echo("Warning: Console id changed to " + id, LOG_PRI);
		CLIENT_ID = id;
	}

	/** @return The array index given in the constructor **/
	public int getID() {
		return CLIENT_ID;
	}

	/** @return IP Address of Client **/
	public String getIP() {
		return socket.getInetAddress().toString();
	}

	/** @return Port used for connection **/
	public int getPort() {
		return socket.getPort();
	}

	/** @return User logged into server **/
	public User getUser() {
		return user;
	}

	
	/** @return Error code **/
	public int getErrID() {
		return kill;
	}

	@Override
	public void kill(int err) {
		close();
		kill = err;
		getParent().dereference(this);
	}

	@Override
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
	
	@Override
	public void echo(Object str, int level) {
		print("[ Runner ] [" + getID() + "] " + str.toString() + "\n", level);
	}
}
