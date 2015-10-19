package sd.swiftserver.rk.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.util.Logging;

/*
 * This file is part of Swift Drive
 * Copyright (C) 2015 Ryan Kerr
 *
 * Swift Drive is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * Swift Drive is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of  MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Swift Drive. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * 
 * @author Ryan Kerr
 */
public class Server implements Settings, Logging {

	protected final int port;
	
	private ServerSocket server = null;
	
	private boolean online = false;
	
	private ArrayList<Runner> clients = new ArrayList<Runner>();
	
	public Server() {
		this(DEF_PORT);
	}

	public Server(int port) {
		this.port = port;
		
		try {
			server = new ServerSocket(port);
			online = true;
			
			echo("Server initialized on port " + port, LOG_PRI);
		}
		catch(IOException ix) {
			error("Failed to initalize server on " + port, LOG_PRI);
			error(ix.getMessage(), LOG_SEC);
			online = false;
		}
		
		while(online) {
			try {
				Runner r = new Runner(server.accept(), this, clients.size() + 1);
				new Thread(r).start();
				clients.add(r);
				
				echo("Client Connected: " + clients.size(), LOG_PRI);
			}
			catch(IOException ix) {
				error("Error while accepting connection from client", LOG_PRI);
				error(ix.getMessage(), LOG_SEC);
			}
		}
	}
	
	protected void removeClient(int id) {
		echo("Client " + id + " has disconnected", LOG_PRI);
		clients.set(id - 1, null);
	}
}