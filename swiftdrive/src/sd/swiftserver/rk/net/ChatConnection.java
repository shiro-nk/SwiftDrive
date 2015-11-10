package sd.swiftserver.rk.net;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import sd.swiftglobal.rk.Meta.LeaveBlank;
import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.util.Logging;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetContainer;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetTool;

public class ChatConnection implements SwiftNetTool, Settings, Logging, Runnable, Closeable {
	private DataInputStream dis;
	private DataOutputStream dos;
	private Socket client;
	private final int id;

	public ChatConnection(ChatServer server, Socket client) {
		this.client = client;
		this.id = 0;

		try {
			dis = new DataInputStream(client.getInputStream());
			dos = new DataOutputStream(client.getOutputStream());
		}
		catch(IOException ix) {

		}
	}

	public void run() {
		
	}

	public SwiftNetContainer getParent() {
		return null;
	}
	
	@LeaveBlank
	public void setParent(SwiftNetContainer parent) {

	}

	public int getID() {
		return id;
	}

	public void kill() {

	}

	public void close() {

	}
}
