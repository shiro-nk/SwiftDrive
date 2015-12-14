package sd.swiftclient.rk.net;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import sd.swiftglobal.rk.Meta.DirectKiller;
import sd.swiftglobal.rk.Meta.DirectTimeout;
import sd.swiftglobal.rk.Meta.IndirectTimeout;
import sd.swiftglobal.rk.Meta.LeaveBlank;
import sd.swiftglobal.rk.Meta.PingHandler;
import sd.swiftglobal.rk.Meta.Pointer;
import sd.swiftglobal.rk.Meta.RequiresPingHandler;
import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.CommandException;
import sd.swiftglobal.rk.expt.DisconnectException;
import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.expt.SwiftException;
import sd.swiftglobal.rk.type.Data;
import sd.swiftglobal.rk.type.Generic;
import sd.swiftglobal.rk.type.ServerCommand;
import sd.swiftglobal.rk.type.SwiftFile;
import sd.swiftglobal.rk.type.users.User;
import sd.swiftglobal.rk.util.Logging;
import sd.swiftglobal.rk.util.Ping;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetContainer;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetTool;
import sd.swiftglobal.rk.util.Terminator;

/* This file is part of Swift Drive				   *
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

/**
 * Client:
 * The client object provides an interface for connecting and interacting
 * with a computer running the server class.
 *
 * @author Ryan Kerr
 */
public class Client implements SwiftNetTool, Settings, Logging, Closeable {
	
	private final Socket server;
	private DataInputStream  dis;
	private DataOutputStream dos;
	private Ping ping;
	private SwiftNetContainer parent;
	private Terminator term;
	private int connectionID = 0;
	private boolean unlocked = false,
					online = false;
	private User user;
	public static String SV_DIV;
	public int kill;

	/**
	 * Establishes a connection and I/O sockets with the server program
	 * @param hostname Host to connect to
	 * @param port Host port
	 * @throws DisconnectException If something fails while connecting
	 */
	public Client(String hostname, int port, SwiftNetContainer parent) throws DisconnectException {
		try {
			echo("Starting client", LOG_PRI);
			this.parent = parent;
			server = new Socket();
			server.connect(new InetSocketAddress(hostname, port), 2500);
			echo("Connected to " + hostname + ":" + port, LOG_TRI);
			dis = new DataInputStream(server.getInputStream());
			dos = new DataOutputStream(server.getOutputStream());
			ping = new Ping(dis, dos, this);
			//new Thread(ping).start();
			term = new Terminator(this);
			version();
			online = true;
			echo("Client ready", LOG_PRI);
		}
		catch(IOException ix) {
			echo("Error connecting to server", LOG_PRI);
			kill(EXC_INIT);
			throw new DisconnectException(EXC_CONN, ix);
		}
	}

	/**
	 * <b>Data Upload:</b><br>
	 * Sends data to the server and then sends a command so that the
	 * server acts on that data type in some way. <br><br>
	 *
	 * This will not execute until the login() method is successfully executed
	 *
	 * @param scmd Command for the server to execute on <b>outbound</b>
	 * @param outbound Data to be sent to the server
	 * @throws DisconnectException For unexpected disconnections
	 * @throws CommandException For malformed commands (no command specified) or impossible requests
	 */
	@PingHandler @IndirectTimeout @Deprecated
	public <Type extends Data> void scmd(ServerCommand scmd, Type outbound) throws DisconnectException, CommandException {
		if(unlocked) {
			echo("Data command request started", LOG_SEC);
			ping.standby();
			ping.deactivate();
			sendData(outbound);
			int signal = scmd(scmd);
			ping.activate();
			echo("Data command request completed with signal " + signal, LOG_SEC);
			if(signal != SIG_READY) throw new CommandException(signal);
		}
	}
	
	/**
	 * <b>Data Download:</b><br>
	 * Sends a command to the server (usually a read command) and downloads
	 * the data that was read on the server. <br><br>
	 *
	 * This method will return a blank generic type until the login method
	 * was not successfully called prior to running this method.
	 *
	 * @param scmd Command for the server to execute
	 * @param inbound Data downloaded from the server
	 * @param type null
	 * @throws DisconnectException For unexpected disconnections
	 * @throws CommandException For malformed commands (no command specified) or impossible requests
	 */
	@PingHandler @IndirectTimeout @Deprecated
	public Data scmd(ServerCommand scmd, Data inbound, int type) throws DisconnectException, CommandException {
		if(unlocked) {
			echo("Data download request started", LOG_SEC);
			ping.pause();
			int signal = scmd(scmd);
			if(signal == SIG_READY) {
				inbound = getData(inbound);
				ping.activate();
				echo("Data download request completed with signal " + signal, LOG_SEC);
				return inbound;
			} 
			else {
				echo("Request could not be completed: " + SwiftException.getErr(signal));
				ping.activate();
				throw new CommandException(signal);
			}
		}
		return new Generic();
	}
	
	/**
	 * <b>File Upload:</b><br>
	 * Sends a file to the server and then follows the upload with a
	 * command (usually to save the data sent on the server) <br><br>
	 *
	 * This method will not run without a successful login attempt
	 *
	 * @param scmd Command for the server to execute (usually a write command)
	 * @param sf The SwiftFile to send to save on the server
	 * @throws DisconnectException For unexpected disconnections
	 * @throws FileException If the file could not be read from the disk
	 * @throws CommandException For malformed/blank commands or impossible requests
	 */
	@PingHandler @IndirectTimeout
	public void sfcmd(ServerCommand scmd, SwiftFile sf) throws DisconnectException, FileException, CommandException {
		if(unlocked) {
			echo("File command request started", LOG_SEC);
			ping.pause();
			try {
				writeInt(DAT_FILE);
				sf.send(dos);
			}
			catch(IOException ix) {
				throw new DisconnectException(EXC_NWRITE, ix);
			}
			int signal = scmd(scmd);
			ping.activate();
			echo("File command request completed with signal " + signal, LOG_SEC);
			if(signal != SIG_READY) throw new CommandException(signal);
		}
	}
	
	/**
	 * <b>File Download:</b><br>
	 * Sends a command to the server to read a file and then downloads
	 * that file from the server <br><br>
	 *
	 * Method will not run without a successful login attempt
	 *
	 * @param scmd Command specifying the file to read from the server
	 * @throws DisconnectException In the event that the server disconnected
	 * @throws CommandException If the command could not be completed or was malformed
	 * @throws FileException If the file could not be kept locally
	 */
	@PingHandler @IndirectTimeout
	public SwiftFile sfcmd(ServerCommand scmd) throws DisconnectException, FileException, CommandException {
		if(unlocked) {
			echo("Stage 1 file download request started", LOG_SEC);
			ping.pause();
			int signal = scmd(scmd);
			if(signal == SIG_READY) {
				return sfcmd();
			}
			else {
				echo("Stage 1 request could not be completed: " + SwiftException.getErr(signal));
				ping.activate();
				throw new CommandException(signal);
			}
		}
		return new SwiftFile(0);
	}

	/**
	 *	<b>File Download:</b><br>
	 *	Requests to download whatever file is present in the server swap variable.
	 *
	 *	@return The file downloaded from the server
	 *	@throws CommandException For malformed or incomplete commands
	 *	@throws DisconnectException In the event that the server disconnects
	 */
	@PingHandler @IndirectTimeout
	public SwiftFile sfcmd() throws CommandException, DisconnectException {
		if(unlocked) {
			echo("File download request started", LOG_SEC);
			int signal = scmd(new ServerCommand(CMD_SEND_FILE, ""));
			if(signal == SIG_READY) {
				try {
					echo("Signals returned true: Download commencing", LOG_SEC);
					SwiftFile file = new SwiftFile(dis);
					ping.activate();
					echo("File download request completed", LOG_SEC);
					return file;
				}
					catch(IOException ix) {
					throw new DisconnectException(EXC_NREAD, ix);
				}
			}	
			else {
				echo("Request could not be completed: " + SwiftException.getErr(signal));
				ping.activate();
				throw new CommandException(signal);
			}
		}
		return new SwiftFile(0);
	}

	/**
	 *	<b>Command Requests and Responses:</b><br>
	 *	Sends a command to the server and waits for the response signal. <br>
	 *	Signals may include COMPLETE, FAILED, or BAD_COMMAND
	 *
	 *	@return The signal received from the command. Returns BAD_COMMAND if blank and EXC_LOCK if not logged in.
	 *	@param scmd Command to execute on server
	 *	@throws DisconnectException In the event of an unexpected disconnection
	 *	@throws CommandException For malformed (blank) commands (this ignores signals)
	 */
	@IndirectTimeout @RequiresPingHandler
	private int scmd(ServerCommand scmd) throws DisconnectException, CommandException {
		if(unlocked) {
			echo("Sending command", LOG_TRI);
			if(scmd.toString() != null) {
				writeInt(DAT_SCMD);
				writeUTF(scmd.toString());
				int rtn = readInt();
				echo("Command returned with signal " + rtn, LOG_TRI);
				return rtn;
			}
			else {
				echo("Command could not be completed due to a malformed command", LOG_TRI);
				return EXC_BCMD;
			}
		}
		return EXC_LOCK;
	}	
	
	/**
	 * <b>Send Data:</b><br>
	 * Sends data to the server to be stored in memory. <br><br>
	 *
	 * This method will not execute unless the client is logged in.
	 *
	 * @param outbound Data to be sent to the server
	 * @throws DisconnectException In the event of a broken connection
	 */
	@IndirectTimeout @RequiresPingHandler
	private <Type extends Data> void sendData(Type outbound) throws DisconnectException {
		if(unlocked) {
			echo("Data upload started", LOG_TRI);
			writeInt(DAT_DATA);
			writeInt(outbound.getSize());
			for(String s : outbound.getArray()) writeUTF(s);
			for(String s : outbound.getArray()) echo("Sent: " + s, LOG_SEC);
			echo("Data upload complete", LOG_TRI);
		}
	}
	
	/**
	 * <b>Receive Data:</b><br>
	 * Receives whatever data is stored in the server memory. <br><br>
	 *
	 * This method will not execute unless the client is logged in
	 *
	 * @param inbound The data type used as a template to save the data in.
	 * @return The value passed to the method with the new data <br>
	 * 		   If the client is not logged in, the parameter will be returned
	 * @throws DisconnectException in the event of a disconnection
	 */
	@IndirectTimeout @RequiresPingHandler
	private Data getData(@Pointer Data inbound) throws DisconnectException {
		if(unlocked) {
			echo("Data download started", LOG_TRI);
			inbound.reset();
			int size = readInt();
			for(int i = 0; i < size; i++) inbound.add(readUTF());
			for(String s : inbound.getArray()) echo("Received: " + s);
			echo("Data download complete", LOG_TRI);
			return inbound;
		}
		return inbound;
	}

	/**
	 * <b>Write int:</b><br>
	 * Writes an int to the socket (used for data type identifiers)
	 *
	 * @param x Integer to send to the server
	 * @throws DisconnectException if the connection times out
	 */
	@RequiresPingHandler @DirectKiller
	private void writeInt(int x) throws DisconnectException {
		try {
			echo("Writing integer to socket", LOG_LOW);
			dos.writeInt(x);
			echo("Done", LOG_LOW);
		}
		catch(IOException ix) {
			echo("Failed", LOG_LOW);
			kill(EXC_NWRITE);
			throw new DisconnectException(EXC_WRITE, ix);
		}
	}
	
	/**
	 * <b>Read int:</b><br>
	 * Reads an int from the socket (used for signal handling)
	 *
	 * @return Integer from server
	 * @throws DisconnectException if the connection times out
	 */
	@DirectTimeout @RequiresPingHandler @DirectKiller
	private int readInt() throws DisconnectException {
		try {
			echo("Reading integer from socket", LOG_LOW);
			term.run();
			int rtn = dis.readInt();
			term.cancel();
			echo("Done", LOG_LOW);
			return rtn;
		}
		catch(IOException ix) {
			echo("Failed", LOG_LOW);
			kill(EXC_NREAD);
			throw new DisconnectException(EXC_WRITE, ix);
		}
	}
	
	/**
	 * <b>Write String:</b><br>
	 * Writes an string to the server
	 *
	 *
	 */
	@RequiresPingHandler @DirectKiller
	private void writeUTF(String s) throws DisconnectException {
		try {
			echo("Writing string to socket", LOG_LOW);
			dos.writeUTF(s);
			echo("Done", LOG_LOW);
		}
		catch(IOException ix) {
			echo("Failed", LOG_LOW);
			kill(EXC_NWRITE);
			throw new DisconnectException(EXC_WRITE, ix);
		}
	}
	
	@DirectTimeout @RequiresPingHandler @DirectKiller
	private String readUTF() throws DisconnectException {
		try {
			echo("Reading string from a socket", LOG_LOW);
			term.run();
			String rtn = dis.readUTF();
			term.cancel();
			echo("Done", LOG_LOW);
			return rtn;
		}
		catch(IOException ix) {
			echo("Failed", LOG_LOW);
			kill(EXC_NREAD);
			throw new DisconnectException(EXC_READ, ix);
		}
	}
	
	public boolean login(String u, char[] p) throws DisconnectException {
		return login(u, new String(p).getBytes());
	}

	@PingHandler @DirectKiller
	public boolean login(String username, byte[] password) throws DisconnectException {
		if(!unlocked) {
			echo("Logging in to the server as " + username, LOG_PRI);
			boolean rtn = false;
			echo("Sending user information", LOG_SEC);
			writeInt(DAT_LGIN);
			writeUTF(username);
			writeInt(password.length);
			try {
				for(byte p : password) dos.writeByte(p);
				term.run();
				rtn = dis.readBoolean();
				term.cancel();
				if(rtn) {
					echo("Login approved", LOG_PRI);
					echo("Receiving full user information", LOG_SEC);
					user = new User(readUTF());
					SV_DIV = readUTF();
					echo("Initializing ping", LOG_TRI);
					new Thread(ping).start();
				}
				else {
					echo("Login revoked", LOG_PRI);
					kill(EXC_LOGIN);
				}
			} 
			catch(IOException ix) {
				kill(EXC_CONN);
				throw new DisconnectException(EXC_NETIO, ix);
			}
			unlocked = rtn;
			echo("Login complete", LOG_PRI);
			return rtn;
		}
		return true;
	}
	
	/**
	 * <b>Version Verification:</b><br>
	 * Ensures the server and the client are of compatible versions.
	 * This is to prevent communication errors arising from different
	 * protocols being used.
	 *
	 * @return True if compatible; otherwise false
	 * @throws IOException if connection is lost
	 * @throws DisconnectException if the versions are incompatible
	 */
	private boolean version() throws IOException, DisconnectException {
		dos.writeDouble(VERSION);
		term.run();
		boolean rtn = dis.readBoolean();
		term.cancel();
		if(!rtn) {
			term.run();
			double version = dis.readDouble();
			term.cancel();
			echo("Server and client are incompatible!", LOG_PRI);
			echo((VERSION < version ? "Upgrade" : "Downgrade") + " client to connect", LOG_PRI);
			throw new DisconnectException(EXC_VER);
		}
		return rtn;
	}

	@PingHandler @DirectKiller
	public void disconnect(int x) throws DisconnectException {
		System.out.println("On: " + online);
		if(online) {
			disconnect();
			kill(EXC_LOGOUT);
		}
	}

	public void disconnect() throws DisconnectException {
		if(online) {
			echo("Requesting disconnection", LOG_SEC);
			ping.pause();
			term.run();
			writeInt(DAT_NULL); echo("Kill signal");
			term.cancel();
			echo("Disconnect request complete");
		}
	}
	
	@Override
	public int getID() {
		return connectionID;
	}

	public User getUser() {
		return user;
	}
	
	@Override @LeaveBlank
	public void setParent(SwiftNetContainer c) {
		
	}
	
	@Override
	public SwiftNetContainer getParent() {
		return parent;
	}
	
	@Override
	public void kill(int err) {
		kill = err;
		close();
		parent.dereference(this);
	}

	public int getErrID() {
		return kill;
	}

//	public ChatClient getChat() {
//		return cclient;
//	}
	
	public boolean isUnlocked() {
		return unlocked;
	}

	public void echo(Object o, int level) {
		print("[ Client ] " + o.toString() + "\n", level);
	}

	@Override
	public void close() {
		try {
			echo("Closing");
			online = false;
			if(term != null) term.destroy();
			if(ping != null) ping.stop();
			if(dis != null) dis.close();
			if(dos != null) dos.close();
			if(server != null) server.close();
		}
		catch(IOException ix) {
			error("Error closing client sockets", LOG_FRC);
		}
	}
}
