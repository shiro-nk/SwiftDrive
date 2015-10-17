package sd.swiftserver.rk.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import sd.swiftglobal.rk.Settings;

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
