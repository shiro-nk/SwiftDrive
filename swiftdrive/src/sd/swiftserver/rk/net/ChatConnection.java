

public class ChatConnection implements SwiftNetTool, Settings, Logging, Runnable, Closeable {
	private DataInputStream dis;
	private DataOutputStream dos;
	private Socket client;
	private final int id;


	public ChatConnection(ChatServer server, Socket client) {
		this.client = client;
	}
}
