package sd.swiftserver.rk.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import sd.swiftglobal.rk.Settings;

public class Runner implements Runnable, Settings {
	
	Socket client = null;
	
	DataInputStream  dis = null;
	DataOutputStream dos = null;
	
	boolean online = false;
	
	public Runner(Socket client) {
		this.client = client;
		
		try {
			dis = new DataInputStream(client.getInputStream());
			dos = new DataOutputStream(client.getOutputStream());
			
			online = true;
		}
		catch(IOException ix) {
			System.err.println("Error establishing sockets: " + ix.getMessage());
			online = false;
		}
	}
	
	public void run() {
		try {
			int dat,
			    size;
			
			while(online && (dat = getInt()) != DAT_OFF) {
				size = getInt();
				
				switch(dat) {
					case DAT_PING:
						break;
					case DAT_FILE:
						fileIn(size);
						break;
					case DAT_DATA:
						String s = dis.readUTF();
						System.out.println(s);
						break;
				}
			}
		}
		catch(Exception e) {
			
		}
	}
	
	public int getInt() {
		try {
			return dis.readInt();
		}
		catch(IOException ix) {
			System.err.println("Error reading integer from client: " + ix.getMessage());
			return DAT_OFF;
		}
	}
	
	public String in() {
		return "";
	}
	
	public void out(String s) {
		
	}
	
	public void fileIn(int size) {
		
	}
}