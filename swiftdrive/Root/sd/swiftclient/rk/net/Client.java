package sd.swiftclient.rk.net;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.DisconnectException;
import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.types.SwiftFile;

public class Client extends Thread implements Settings, Closeable {
	
	Socket server = null;

	DataInputStream  dis = null;
	DataOutputStream dos = null;
	
	final String host;
	final int    port;

	boolean online = false,
			active = false;
	
	public Client(String hostname, int port) throws DisconnectException {
		System.out.println("Client has been initialized");
		
		this.port = port;
		this.host = hostname;
		
		try {
			server = new Socket(this.host, this.port);
			System.out.println("Connected to " + host + ":" + port);
			
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
			throw new DisconnectException(ix.getMessage());
		}
	}

	public Client(String hostname) throws DisconnectException {
		this(hostname, DEF_PORT);
	}
	
	public Client(byte ip1, byte ip2, byte ip3, byte ip4, int port) throws DisconnectException {
		this("" + ip1 + ip2 + ip3 + ip4, port);
	}
	
	public Client(byte ip1, byte ip2, byte ip3, byte ip4) throws DisconnectException {
		this("" + ip1 + ip2 + ip3 + ip4, DEF_PORT);
	}
	
	public void run() {
		while(online) {
			try {
				Thread.sleep(1000);
			}
			catch(InterruptedException ix) {
				System.err.println(ix.getMessage());
			}
		}
	}
	
	public int getInt() {
		try {
			return dis.readInt();
		}
		catch(IOException ix) {
			System.err.println("Error while reading integer from socket");
			System.err.println(ix.getMessage());
			return DAT_OFF;
		}
	}
	
	public String in() {
		try {
			return dis.readUTF();
		}
		catch(IOException ix) {
			System.err.println("Error while reading a string");
			return "";
		}
	}
	
	public void out(String out) {
		try {
			dos.writeInt(DAT_DATA);
			dos.writeInt(1);
			dos.writeUTF(out);
		}
		catch(IOException ix) {
			ix.printStackTrace();
		}
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
	
	public void fileOut(String path) {
		fileOut(new File(path));
	}
	
	public void fileOut(File file) {
		try {
			SwiftFile fi = new SwiftFile(file);
			fi.toData();
			for(String s : fi.getData()) {
				System.out.println(s);
			}
			try {
				dos.writeInt(DAT_FILE);
				dos.writeInt(fi.getFileSize());
				
				if(getInt() == SIG_READY) {
					System.out.println("SIG_READY received!");
					for(byte b : fi.getBytes()) {
						dos.writeByte(b);
						System.out.println(b);
					}
				}
			}
			catch(IOException ix) {
				System.err.println("Error while writing files to socket");
				System.err.println(ix.getMessage());
			}
		}
		catch(FileException fx) {
			System.err.println("Failed to send file");
		}
	}
}
