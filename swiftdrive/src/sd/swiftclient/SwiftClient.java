package sd.swiftclient;

import sd.swiftclient.rk.net.Client;
import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.DisconnectException;
import sd.swiftglobal.rk.gui.SwiftLogin;
import sd.swiftglobal.rk.gui.SwiftScreen;
import sd.swiftglobal.rk.type.users.User;
import sd.swiftglobal.rk.type.users.UserHandler;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetContainer;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetTool;
import sd.swiftserver.rk.net.Server;

/* This file is part of Swift Drive				 *
 * Copyright (C) Ryan Kerr 2015					 *
 * Please refer to <http://www.gnu.org/licenses> */

public class SwiftClient implements SwiftNetContainer, Settings {
	public static void main(String[] args) {
		new SwiftClient();
	}

	private SwiftScreen screen;
	private Client client;
	private User user;

	public SwiftClient() {
		//TODO TESTING
		Server server = null;
		try {
			server = new Server(3141);
			server.setUserlist(new UserHandler());
		}
		catch(DisconnectException dx) {
			dx.printStackTrace();
		}
		//TODO END TESTING
		
		screen = new SwiftScreen("Server", this, client);
		SwiftLogin login = new SwiftLogin(screen, this);
		screen.setPanel(login);
		
		try {
			Thread.sleep(100000);
		}
		catch(InterruptedException ix) {

		}
		server.close();
	}
	
	public void setClient(Client cli) {
		client = cli;
	}

	public Client getClient() {
		return client;
	}

	public void kill() {

	}

	public void dereference(SwiftNetTool tool) {

	}
}
