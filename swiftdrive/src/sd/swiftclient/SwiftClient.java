package sd.swiftclient;

import sd.swiftclient.rk.net.Client;
import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.gui.SwiftLogin;
import sd.swiftglobal.rk.gui.SwiftScreen;
import sd.swiftglobal.rk.type.users.User;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetContainer;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetTool;

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
		screen = new SwiftScreen("Server", this);
		SwiftLogin login = new SwiftLogin(screen, this);
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

	public void kill() {

	}

	public void dereference(SwiftNetTool tool) {

	}
}
