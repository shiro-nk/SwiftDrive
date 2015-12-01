package sd.swiftglobal.rk;

import sd.swiftclient.rk.net.Client;
import sd.swiftglobal.rk.expt.SwiftException;
import sd.swiftglobal.rk.type.ServerCommand;
import sd.swiftglobal.rk.type.SwiftFile;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetContainer;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetTool;
import sd.swiftserver.rk.net.Server;

public class TypeTesting implements Settings, SwiftNetContainer {
	public static void main(String[] args) {
		new TypeTesting();
	}

	public TypeTesting() {
		try(Server server = new Server()) {
			try(Client client = new Client("localhost", 3141, this)) {
				if(!client.login("username", "password".getBytes())) System.exit(5);
				System.out.println("Running");
				SwiftFile file = client.sfcmd (
					new ServerCommand (
						CMD_READ_FILE, "task/index"
					)
				);

				file.fromData();
				for(String s : file.getArray()) System.out.println(s);
				client.disconnect();
				System.out.println("Finished");
			}
		}
		catch(SwiftException x) {
			x.printStackTrace();
		}
	}

	@Override
	public void dereference(SwiftNetTool t) {

	}
}
