package sd.swiftserver;

import sd.swiftglobal.rk.expt.DisconnectException;
import sd.swiftglobal.rk.type.users.UserHandler;
import sd.swiftserver.rk.net.Server;

public class SwiftServer {
	public static void main(String[] args) {
		new SwiftServer();
	}

	private UserHandler userlist;

	private SwiftServer() {
		userlist = new UserHandler();
		try(Server server = new Server(3141)) {
			server.setUserlist(userlist);
		}
		catch(DisconnectException dx) {
			dx.printStackTrace();
		}
	}
}
