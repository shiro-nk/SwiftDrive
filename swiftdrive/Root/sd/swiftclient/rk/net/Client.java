package sd.swiftclient.rk.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.DisconnectException;

public class Client extends Thread implements Settings {
	
	Socket server = null;

	DataInputStream  dis = null;
	DataOutputStream dos = null;
	
	final String host;
	final int    port;

	boolean online = false,
			active = false;
	
	int timer = 0;
	
	public Client(String hostname, int port) {
		this.port = port;
		this.host = hostname;
		
		try {
			server = new Socket(this.host, this.port);
		
			try {
				dis = new DataInputStream(server.getInputStream());
				dos = new DataOutputStream(server.getOutputStream());
				
				online = true;
			}
			catch(IOException ix) {
				online = false;
			}
		}
		catch(IOException ix) {
			
		}
	}

	public Client(String hostname) {
		this(hostname, DEF_PORT);
	}
	
	public Client(byte ip1, byte ip2, byte ip3, byte ip4, int port) {
		this("" + ip1 + ip2 + ip3 + ip4, port);
	}
	
	public Client(byte ip1, byte ip2, byte ip3, byte ip4) {
		this("" + ip1 + ip2 + ip3 + ip4, DEF_PORT);
	}
	
	public void run() {
		while(online && !active && (timer <= DEF_BEAT)) {
			try {
				Thread.sleep(1000);
			}
			catch(InterruptedException ix) {
				System.err.println(ix.getMessage());
			}
		}
	}
	
	public void resetTimer() {
		timer = DEF_BEAT;
	}
	
	public int getInt() throws DisconnectException {
		try {
			return dis.readInt();
		}
		catch(IOException ix) {
			throw new DisconnectException(ix.getMessage());
		}
	}
	
	public String in() {
		return "";
	}
	
	public void close() {
		try {
			if(dis != null) dis.close();
			if(dos != null) dos.close();
			
			if(server != null) server.close();
		}
		catch(IOException ix) {
			System.err.println("Failed to close resources!");
			System.err.println(ix.getMessage());
			
			System.exit(100);
		}
	}
}
