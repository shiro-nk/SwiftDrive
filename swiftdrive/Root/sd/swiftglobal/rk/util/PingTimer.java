package sd.swiftglobal.rk.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.DisconnectException;

/*
 * This file is part of Swift Drive
 * Copyright (C) 2015 Ryan Kerr
 *
 * Swift Drive is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * Swift Drive is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of  MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Swift Drive. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * 
 * @author Ryan Kerr
 */
public class PingTimer extends Thread implements Runnable, Settings {
	
	boolean active,
			kill;

	long increment;
	
	int cycle,
		timer,
		timeout;

	DataInputStream  dis;
	DataOutputStream dos;
	
	public PingTimer(DataInputStream in, DataOutputStream out) {
		
	}
	
	public void run() {
		
	}
	
	public void ping() throws DisconnectException {
		try {
			dos.writeInt(DAT_PING);
		}
		catch(IOException ix) {
			throw new DisconnectException("");
		}
	}
	
	public void pingListen(int p) {

	}
}
