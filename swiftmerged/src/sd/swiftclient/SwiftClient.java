package sd.swiftclient;

import sd.swiftclient.rk.gui.SwiftLogin;
import sd.swiftclient.rk.net.Client;
import sd.swiftglobal.rk.Meta.LeaveBlank;
import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.gui.SplashScreen;
import sd.swiftglobal.rk.gui.SwiftGUI.SwiftContainer;
import sd.swiftglobal.rk.gui.SwiftGUI.SwiftMaster;
import sd.swiftglobal.rk.gui.SwiftGreeter;
import sd.swiftglobal.rk.gui.SwiftScreen;
import sd.swiftglobal.rk.type.users.User;
import sd.swiftglobal.rk.util.Logging;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetContainer;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetTool;

/* This file is part of Swift Drive				 *
 * Copyright (C) 2015 Ryan Kerr					 *
 * Please refer to <http://www.gnu.org/licenses> */

public class SwiftClient implements Logging, SwiftNetContainer, Settings, SwiftMaster {
	public static void main(String[] args) {
		new SwiftClient();
	}

	private SwiftScreen screen;
	private Client client;
	private SwiftContainer container;

	public SwiftClient() {
		SplashScreen splash = new SplashScreen();
		echo("Starting SwiftClient", LOG_PRI);
		screen = new SwiftScreen("Swift Drive", this);
		echo("Initializing Login Screen", LOG_TRI);
		SwiftLogin login = new SwiftLogin(screen, this);
		echo("Showing Login Screen", LOG_TRI);
		screen.setPanel(login);
		splash.pause();
		splash.close();
		screen.activate();
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

	@LeaveBlank
	public void setNetContainer(SwiftNetContainer c) {

	}

	public SwiftNetContainer getNetContainer() {
		return this;
	}

	public SwiftContainer getContainer() {
		return container; 
	}
	
	public void setContainer(SwiftContainer c) {
		container = c;
	}

	public User getUser() {
		return client != null ? client.getUser() : null;
	}

	public void kill() {

	}
	
	public void dereference(SwiftNetTool tool) {
		int err = tool.getErrID();
		echo("Dereference", LOG_PRI);
		if(err == EXC_NWRITE || err == EXC_NREAD || err == EXC_TERM || err == EXC_CONN) {
			screen.setPanel(new SwiftGreeter(screen));
		}
		else if(err == EXC_SAFE) System.exit(5);
	}

	public void echo(Object o, int level) {
		print("[ Client ] " + o.toString() + "\n", level);
	}
}
