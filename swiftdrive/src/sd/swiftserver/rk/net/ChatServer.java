package sd.swiftserver.rk.net;

import java.io.IOException;
import java.net.ServerSocket;

import sd.swiftglobal.rk.util.SwiftNet.SwiftNetContainer;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetTool;

public class ChatServer implements Runnable, SwiftNetContainer {
	private ServerSocket server;	
	private boolean accepting = false;

	public ChatServer(int port, SwiftNetContainer parent) throws IOException {
		server = new ServerSocket(port);
	}

	public void run() {
		accepting = true;
		try {
			ChatConnection cc = new ChatConnection(this, server.accept());
			new Thread(cc).start();
		}
		catch(IOException ix) {

		}
	}
	
	public void dereference(SwiftNetTool tool) {
		
	}

	public void close() {
		try {
			if(server != null) server.close();
		}
		catch(IOException ix) {

		}
	}
}
