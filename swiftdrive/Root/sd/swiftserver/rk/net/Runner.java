package sd.swiftserver.rk.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.types.SwiftFile;

public class Runner implements Runnable, Settings {
	
	Server server = null;
	Socket client = null;
	
	DataInputStream  dis = null;
	DataOutputStream dos = null;
	
	boolean online = false;
	
	final int CLIENT_ID;
	
	public Runner(Socket client, Server server, int id) {
		this.client = client;
		this.server = server;
		CLIENT_ID   = id;
		
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
						System.out.println("Ready to receive file of " + size + " bytes");
						dos.writeInt(SIG_READY);
						SwiftFile fi = new SwiftFile(in(), size, dis);
						fi.writeFile();
						break;
					case DAT_DATA:
						String s = dis.readUTF();
						System.out.println(s);
						break;
				}
			}
		}
		catch(IOException ix) {
			
		}
		catch(FileException fx) {
			System.err.println("Error reading file");
			System.err.println(fx.getMessage());
		}
	}
	
	public void ready() {
		try {
			dos.writeInt(SIG_READY);
		}
		catch(IOException ix) {
			System.err.println("Error while writing SIG_READY to socket");
			System.err.println("Disconnecting client from server");
			close();
		}
	}
	
	public int getInt() {
		try {
			System.out.println("Listening for an int on client " + CLIENT_ID);
			return dis.readInt();
		}
		catch(IOException ix) {
			System.err.println("Error reading integer from client: " + ix.getMessage());
			return DAT_OFF;
		}
	}
	
	public String in() {
		return "output";
	}
	
	public void out(String s) {
		
	}
	
	public void fileOut() {
		
	}

	public void close() {
		online = false;
		server.removeClient(CLIENT_ID);

		try {
			if(dis != null) dis.close();
			if(dos != null) dos.close();
		}
		catch(IOException ix) {
			System.err.println("Failed to close resources!");
			System.err.println(ix.getMessage());
		}
	}
}