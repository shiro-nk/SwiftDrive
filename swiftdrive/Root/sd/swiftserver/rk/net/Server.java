package sd.swiftserver.rk.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import sd.swiftglobal.rk.Settings;

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
public class Server implements Settings {

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
			
			System.out.println("Server initialized on port " + port);
		}
		catch(IOException ix) {
			System.err.println("Failed to initalize server on " + port);
			System.err.println(ix.getMessage());
			online = false;
		}
		
		while(online) {
			try {
				Runner r = new Runner(server.accept(), this, clients.size() + 1);
				new Thread(r).start();
				clients.add(r);
				
				System.out.println("Client connected >> ID: " + clients.size());
			}
			catch(IOException ix) {
				System.err.println("Error while accepting connection from client");
				System.err.println(ix.getMessage());
			}
		}
	}
	
	protected void removeClient(int id) {
		clients.set(id, null);
	}
}
