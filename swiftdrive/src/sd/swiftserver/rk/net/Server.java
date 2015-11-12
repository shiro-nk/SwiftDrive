package sd.swiftserver.rk.net;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

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
	private boolean   accepting = false;
	private final int PORT;
	private UserHandler userlist;

	/**
	 * Initialize the server and the listening thread on the given port
	 * @param port The port to accept connections on
	 * @throws DisconnectException if the port is already in use
	 */
	public Server(int port) throws DisconnectException {
		PORT = port;
		try {
			server = new ServerSocket(port);
			new Thread(this).start();
		}
		catch(IOException ix) {
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
		echo("Server intialized on port " + PORT, LOG_PRI);
		while(accepting) {
			try {
				Connection cli = new Connection(this, server.accept(), clients.size());
				new Thread(cli).start();
				clients.add(cli);
				echo("Client " + cli.getIP() + ":" + cli.getPort() + " has connected w/" + cli.getID() , LOG_PRI);
				if(DEF_DDOS < clients.size()) accepting = false;
			}
			catch(IOException ix) {
				error("Error during client connect attempt: " + ix.getMessage(), LOG_PRI);
			}
		}
	}

	public void cleanStack() {
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

	/**
	 * Destroy client
	 * @param client Client to destroy
	 */
	public void dereference(SwiftNetTool client) {
		int id = client.getID();
		if(0 <= id && id < clients.size()) {
			clients.set(id, null);
		}
		System.out.println("Stack size: " + clients.size());
		cleanStack();
	}
	
	/** Force close **/
	public void close() {
		try {
			server.close();
			accepting = false;
		}
		catch(IOException ix) {
		
		}
	}
}
