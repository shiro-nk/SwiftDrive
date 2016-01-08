package sd.swiftglobal.rk.type;

import sd.swiftglobal.rk.Meta.Typedef;
import sd.swiftglobal.rk.Settings;

/* This file is part of Swift Drive                * 
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

/**
 * <b>Server Command:</b><br>
 * Small class for formatting specific commands with their paths without.
 * (Slightly redundant, but it saved a few lines of code)
 */

@Typedef("ServerCommand")
public class ServerCommand implements Settings {
	private int command;
	private String info;
	
	/**
	 * <b>Create Server Command:</b><br>
	 * Creates a server command object.
	 *
	 * @param command An integer corresponding to a command from Settings (CMD)
	 * @param path A path (without LC_PATH prefix) for a file on server
	 */
	public ServerCommand(int command, String path) {
		this.info = path;
		this.command = command;
	}
	
	/**
	 * <b>Format Server Command:</b><br>
	 * @return Formatted server command
	 */
	public String toString() {
		return info + ":" + command;
	}
}
