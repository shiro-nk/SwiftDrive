package sd.swiftclient;

import sd.swiftclient.mp.gui.LoginPage;
import sd.swiftclient.rk.net.Client;
import sd.swiftglobal.mp.gui.GraphicalInterface.SwiftContainer;
import sd.swiftglobal.mp.gui.GraphicalInterface.SwiftMaster;
import sd.swiftglobal.mp.gui.Screen;
import sd.swiftglobal.rk.gui.SplashScreen;
import sd.swiftglobal.rk.Meta.LeaveBlank;
import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.util.Logging;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetContainer;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetTool;

public class ClientMaster implements Settings, Logging, SwiftMaster, SwiftNetContainer {
	private SwiftContainer container;
	private Client client;
	private Screen screen;

	public ClientMaster() {
		SplashScreen splash = new SplashScreen();
		echo("Starting swift client", LOG_PRI);
		screen = new Screen(this);
		echo("Displaying login screen", LOG_TRI);
		screen.setPanel(new LoginPage(screen, this));
		splash.pause();
		splash.close();
		screen.activate();
	}
	
	@LeaveBlank
	public void setContainer(SwiftContainer c) {
		container = c;
	}

	public SwiftContainer getContainer() {
		return screen;
	}

	@LeaveBlank
	public void setNetContainer(SwiftNetContainer c) {

	}

	public SwiftNetContainer getNetContainer() {
		return this;
	}

	public void dereference(SwiftNetTool tool) {
		echo("Dereferencing", LOG_PRI);
		int err = tool.getErrID();
		if(err == EXC_TERM || err == EXC_NREAD || err == EXC_NWRITE || err == EXC_CONN) {
			System.exit(1);
		}
		else if(err == EXC_SAFE) {
			System.exit(1000);
		}
	}

	public boolean hasTool() {
		return client == null ? false : true;
	}

	public SwiftNetTool getTool() {
		return client;
	}

	public void setClient(Client c) {
		client = c;
	}

	public Client getClient() {
		return client;
	}

	public void echo(Object o, int level) {
		print("[ Client ] " + o.toString() + "\n", level);
	}
}
