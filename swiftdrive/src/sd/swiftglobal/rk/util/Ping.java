package sd.swiftglobal.rk.util;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import sd.swiftglobal.rk.Settings;

/* This file is part of Swift Drive				   *
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

public class Ping implements Settings, Runnable, Closeable {
	private boolean online = false,
					active = false;
	
	private DataInputStream  dis;
	private	DataOutputStream dos;
	
	public Ping(DataInputStream dis, DataOutputStream dos) {
		this.dis = dis;
		this.dos = dos;
		online = false;
	}
	
	public void run() {
		while(online) {
			while(active) {
				try {
					Thread.sleep(DEF_BEAT);
					dos.writeInt(DAT_PING);
					dis.readInt();
				}
				catch(IOException ix) {
					
				}
				catch(InterruptedException ix) {
					
				}
			}
		}
	}
	
	public void deactivate() {
		active = false;
	}
	
	public void activate() {
		active = true;
	}
	
	public void close() {
		
	}
}
