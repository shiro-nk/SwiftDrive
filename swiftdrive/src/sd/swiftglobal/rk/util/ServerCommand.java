package sd.swiftglobal.rk.util;

import sd.swiftglobal.rk.Settings;

public class ServerCommand implements Settings {
	private int command;
	private String info;
	
	public void setInfo(String info, int command) {
		this.info = info;
		this.command = command;
		
		if(info != null && !info.trim().equals("")) command++;
	}
	
	public void setInfo(int command) {
		setInfo("", command);
	}
	
	public String toString() {
		return info + ":" + command;
	}
}
