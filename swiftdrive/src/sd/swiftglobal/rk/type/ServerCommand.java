package sd.swiftglobal.rk.type;

import sd.swiftglobal.rk.Meta.Typedef;
import sd.swiftglobal.rk.Settings;

@Typedef("ServerCommand")
public class ServerCommand implements Settings {
	private int command;
	private String info;
	
	public ServerCommand(int command, String path) {
		this.info = path;
		this.command = command;
	}
	
	public String toString() {
		return info + ":" + command;
	}
}
