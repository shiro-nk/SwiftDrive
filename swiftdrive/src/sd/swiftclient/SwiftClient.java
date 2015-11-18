package sd.swiftclient;

import sd.swiftclient.rk.gui.SwiftLogin;
import sd.swiftclient.rk.net.Client;
import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.gui.SwiftGreeter;
import sd.swiftglobal.rk.gui.SwiftScreen;
import sd.swiftglobal.rk.type.users.User;
import sd.swiftglobal.rk.util.Logging;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetContainer;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetTool;

/* This file is part of Swift Drive				 *
 * Copyright (C) Ryan Kerr 2015					 *
 * Please refer to <http://www.gnu.org/licenses> */

public class SwiftClient implements Logging, SwiftNetContainer, Settings {
	public static void main(String[] args) {
		new SwiftClient();
	}

	private SwiftScreen screen;
	private Client client;

	public SwiftClient() {
		echo("Starting SwiftClient", LOG_PRI);
		screen = new SwiftScreen("Swift Drive", this);
		echo("Initializing Login Screen", LOG_TRI);
		SwiftLogin login = new SwiftLogin(screen, this);
		echo("Showing Login Screen", LOG_TRI);
		screen.setPanel(login);
	}
	
	public void setClient(Client cli) {
		client = cli;
	}

	public Client getClient() {
		return client;
	}

	public boolean hasTool() {
		return client != null ? true : false;
	}

	public SwiftNetTool getTool() {
		return client;	
	}

	public User getUser() {
		return client != null ? client.getUser() : null;
	}

	public void kill() {

	}
	
	public void dereference(SwiftNetTool tool) {
		screen.setPanel(new SwiftGreeter(screen));
	}

	public void echo(Object o, int level) {
		print("[ Client ] " + o.toString() + "\n", level);
	}
}
