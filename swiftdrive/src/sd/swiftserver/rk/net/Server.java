package sd.swiftserver.rk.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.DisconnectException;
import sd.swiftglobal.rk.util.Logging;

/* This file is part of Swift Drive				   *
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

public class Server implements Settings, Logging {
	private ArrayList<Connection> clients = new ArrayList<Connection>();
	
	private boolean   online = false;
	private final int PORT;
	
	public Server(int port) throws DisconnectException {
		PORT = port;
		
		try(ServerSocket server = new ServerSocket(port)) {
			online = true;
			echo("Server intialized on port" + port, LOG_SEC);
			
			while(online) {
				try {
					Connection cli = new Connection(this, server.accept(), clients.size());
					new Thread(cli).start();
					clients.add(cli);
					echo("Client " + (clients.size() - 1) + " has connected", LOG_SEC);
				}
				catch(IOException ix) {
					error("Error during client connect attempt: " + ix.getMessage(), LOG_SEC);
				}
			}
		}
		catch(IOException ix) {
			throw new DisconnectException(EXC_CONN, ix);
		}
	}
	
	public Server() throws DisconnectException {
		this(DEF_PORT);
	}
	
	public int getPort() {
		return PORT;
	}
	
	public void rmClient(int id) {
		if(0 < id && id < clients.size()) clients.set(id, null);
	}
}