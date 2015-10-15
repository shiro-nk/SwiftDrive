package sd.swiftserver.rk.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import sd.swiftglobal.rk.Settings;

public class Server implements Settings {

	final int port;
	
	ServerSocket server = null;
	Socket       client = null;
	
	boolean online = false;
	
	ArrayList<Runner> clients = new ArrayList<Runner>();
	
	public Server() {
		this(DEF_PORT);
	}

	public Server(int port) {
		this.port = port;
		
		try {
			server = new ServerSocket(port);
			online = true;
		}
		catch(IOException ix) {
			System.err.println("Failed to initalize server on " + port);
			System.err.println(ix.getMessage());
			online = false;
		}
		
		while(online) {
			try {
				Runner r = new Runner(server.accept());
				new Thread(r).start();
				clients.add(r);
			}
			catch(IOException ix) {
				
			}
		}
	}
}
