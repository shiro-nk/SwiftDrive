package sd.swiftglobal.rk.util;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetTool;

/* This file is part of Swift Drive				   *
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

/**
 * Separate thread for heart beat operations.
 * (Requires its own thread so other operations
 * aren't lost in the client thread)
 *
 * @author Ryan Kerr
 */
public class Ping implements Settings, Runnable, Closeable {
	private boolean online = false,
					active = false;
	
	private DataInputStream  dis;
	private	DataOutputStream dos;
	private SwiftNetTool 	tool;
	
	/**
	 * Initialize the ping tool
	 * @param dis Stream to read from
	 * @param dos Stream to ping to
	 */
	public Ping(DataInputStream dis, DataOutputStream dos, SwiftNetTool t) {
		this.dis = dis;
		this.dos = dos;
		this.tool = t;
		online = false;
	}
	
	/**
	 * Start the ping thread.
	 * This thread only pings every DEF_BEAT seconds while active.
	 * Once the ping timer is offline, it cannot be reactivated.
	 */
	public void run() {
		while(online) {
			while(active) {
				try {
					Thread.sleep(DEF_BEAT);
					dos.writeInt(DAT_PING);
					dis.readInt();
				}
				catch(IOException ix) {
					close();
				}
				catch(InterruptedException ix) {}
			}
		}
	}
	
	/** Temporarily disable the ping heart beat **/
	public void deactivate() {
		active = false;
	}
	
	/** Re-enables the ping **/
	public void activate() {
		active = true;
	}
	
	public void close() {
		tool.kill();
	}
}
