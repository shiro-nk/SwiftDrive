package sd.swiftserver.rk.net;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;

import sd.swiftglobal.rk.Meta.LeaveBlank;
import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.DisconnectException;
import sd.swiftglobal.rk.type.users.UserHandler;
import sd.swiftglobal.rk.util.Logging;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetContainer;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetTool;

/* This file is part of Swift Drive				   *
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

/**
 * The server initializes the server socket and manages client connections. <br>
 *
 * @author Ryan Kerr
 */
public class Server implements SwiftNetContainer, Runnable, Settings, Logging, Closeable {
	private ArrayList<Connection> clients = new ArrayList<Connection>();
	private final ServerSocket server;
	private boolean   accepting = false,
					  closed    = false;
	private final int PORT;
	private UserHandler userlist;

	/**
	 * Initialize the server and the listening thread on the given port
	 * @param port The port to accept connections on
	 * @throws DisconnectException if the port is already in use
	 */
	public Server(int port) throws DisconnectException {
		echo("Initializing server on port " + port, LOG_PRI);
		PORT = port;
		try {
			userlist = new UserHandler();
			server = new ServerSocket(port);
			new Thread(this).start();
			echo("Initialized on " + InetAddress.getLocalHost().toString() + ":" + port, LOG_PRI);
		}
		catch(IOException ix) {
			echo("Failed to initialize server", LOG_PRI);
			throw new DisconnectException(EXC_CONN, ix);
		}
	}
	
	/**
	 * Initialize the server and the listening thread on the default port
	 * @throws DisconnectException if the port is already in use
	 */
	public Server() throws DisconnectException {
		this(DEF_PORT);
	}
	
	/**
	 * Listens for and accepts client connections. <br>
	 * Starts connection thread and indexes the connection in Client array
	 */
	public void run() {
		accepting = true;
		closed = false;
		while(accepting && !closed) {
			try {
				echo("Listening for client connections", LOG_PRI);
				Connection cli = new Connection(this, server.accept(), clients.size());
				echo(cli.getIP() + ":" + cli.getPort() + " has connected (" + cli.getID() + ")" , LOG_PRI);
				new Thread(cli).start();
				echo("Connection thread initialized", LOG_SEC);
				clients.add(cli);
				if(DEF_DDOS < clients.size()) {
					echo("Warning: The client array size is above threshold. Request will not be accepted.", LOG_PRI);
					accepting = false;
				}
			}
			catch(IOException ix) {
				echo("Error during client connect attempt: " + ix.getMessage(), LOG_PRI);
			}
		}
	}

	public void cleanStack() {
		echo("Removing all null reference from client array", LOG_SEC);
		ArrayList<Connection> swap = new ArrayList<Connection>();
		for(Connection c : clients.toArray(new Connection[clients.size()])) {
			if(c != null) swap.add(c);
		}
		
		clients.clear();
		Connection[] current = swap.toArray(new Connection[swap.size()]);
		for(int i = 0; i < current.length; i++) { 
			current[i].setID(i);
			clients.add(current[i]);
		}

		if(accepting == false && clients.size() < DEF_DDOS) {
			echo("Warning: Client array now below danger threshold, connections are now available", LOG_PRI);
			accepting = true;
			new Thread(this).start();
		}
		echo("Null removal complete");
	}

	public UserHandler getUserlist() {
		return userlist;
	}

	public void setUserlist(UserHandler list) {
		userlist = list;
	}

	/** @return Port number **/
	public int getPort() {
		return PORT;
	}

	public boolean hasTool() {
		return false;
	}

	public SwiftNetTool getTool() {
		return null;
	}

	@LeaveBlank
	public void setTool(SwiftNetTool t) {

	}

	public void echo(Object o, int level) {
		print("[ Server ] " + o.toString() + "\n", level);
	}

	/**
	 * Destroy client
	 * @param client Client to destroy
	 */
	public void dereference(SwiftNetTool client) {
		echo("Dereferencing a connection from the client array", LOG_SEC);
		int id = client.getID();
		if(0 <= id && id < clients.size()) {
			clients.set(id, null);
		}
		echo("Stack size: " + clients.size(), LOG_SEC);
		cleanStack();
		echo("Dereference complete", LOG_SEC);
	}
	
	/** Force close **/
	public void close() {
		try {
			server.close();
			accepting = false;
			closed = true;
		}
		catch(IOException ix) {
		
		}
	}
}
