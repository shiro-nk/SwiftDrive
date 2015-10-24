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
					active = false,
					pinged = false;
	
	private DataInputStream  dis;
	private	DataOutputStream dos;
	private SwiftNetTool 	tool;
	
	private int loopPos = 0;
	
	/**
	 * Initialize the ping tool
	 * @param dis Stream to read from
	 * @param dos Stream to ping to
	 */
	public Ping(DataInputStream dis, DataOutputStream dos, SwiftNetTool t) {
		this.dis = dis;
		this.dos = dos;
		this.tool = t;
		online = true;
		active = true;
	}
	
	/**
	 * Start the ping thread.
	 * This thread only pings every DEF_BEAT seconds while active.
	 * Once the ping timer is offline, it cannot be reactivated.
	 */
	public void run() {
		while(online) {
			while(active) {
				System.out.println("ActiveC: " + active);
				Terminator term = null;
				try {
					for(loopPos = 0; loopPos < DEF_PING * (1000/DEF_TIME); loopPos++) 
						try {
							Thread.sleep(DEF_TIME);
						}
						catch(InterruptedException ix) {
							ix.printStackTrace();
							active = false;
						}
					System.out.println("Active: " + active);
					if(active) {
						pinged = true;
						System.out.println("Ping");
						dos.writeInt(DAT_PING);
						term = new Terminator(tool);
						dis.readInt();
						System.out.println("xPing");
						term.cancel();
						pinged = false;
					}
				}
				catch(IOException ix) {
					if(term == null || !term.terminated()) close();
				}
			}
		}
		System.out.println("break the loop");
	}
	
	/** Temporarily disable the ping heart beat **/
	public void deactivate() {
		System.out.println("Deactivated");
		active = false;
	}
	
	/** Re-enables the ping **/
	public void activate() {
		System.out.println("Activated");
		loopPos = 0;
		active = true;
	}
	
	public void lock() {
		while(pinged);
	}
	
	public void close() {
		pinged = false;
		active = false;
		online = false;
		tool.kill();
	}
}