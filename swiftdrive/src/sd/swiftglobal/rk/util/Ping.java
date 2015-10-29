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
					locked = false;
	
	private DataInputStream  dis;
	private	DataOutputStream dos;
	private SwiftNetTool 	tool;
	private Thread sleep;
	private Terminator term;;
	
	private Lock lock = new Lock(),
				 ping = new Lock();
	
	/**
	 * Initialize the ping tool
	 * @param dis Stream to read from
	 * @param dos Stream to ping to
	 */
	public Ping(DataInputStream dis, DataOutputStream dos, SwiftNetTool t) {
		this.dis = dis;
		this.dos = dos;
		this.tool = t;
		term = new Terminator(tool);
		online = true;
		active = true;
	}
	
	/**
	 * Start the ping thread.
	 * This thread only pings every DEF_BEAT seconds while active.
	 * Once the ping timer is offline, it cannot be reactivated.
	 */
	int x = 0; int y = 0;
	public void run() {
		while(online) {
			System.out.println("Ping has started");
			synchronized(ping) {
				if(!active) {
					try {
						System.out.println("Ping: Waiting");
						ping.wait();
						System.out.println("Ping: Unlocked");
					}
					catch(InterruptedException ix) {
						
					}
				}
			}
			System.out.println("Ping: " + active);
			while(active) {
				System.out.println("Ping: " + ++x);
				sleep = new Thread(new Runnable() {
					public void run() {
						try {
							Thread.sleep(DEF_PING * 1000);
							System.out.println("xasdf");
							if(active) {
								try {
									locked = true;
									System.out.println("Ping: Ping");
									dos.writeInt(DAT_PING);
									term.run();
									dis.readInt();
									System.out.println("Ping: Response");
									term.cancel();
									
									synchronized(lock) { lock.notifyAll(); }
								}
								catch(IOException ix) {
									if(term == null || !term.terminated()) close();
								}
							}
						}
						catch(InterruptedException ix) {
							active = false;
							System.out.println("Ping: Interrupted");
						}
					}
				});
				sleep.start();
				try {
					sleep.join();
					System.out.println("Ping joined");
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("Ping hit end");
			}
		}
		System.out.println(y);
		if(y== 5) System.exit(128904);
	}
	
	/** Temporarily disable the ping heart beat **/
	public void deactivate() {
		System.out.println("Deactivated");
		active = false;
		sleep.interrupt();
	}
	
	/** Re-enables the ping **/
	public void activate() {
		try {
			Thread.sleep(500);
		}
		catch(InterruptedException ix) {
			
		}
		System.out.println("Activated");
		active = true;
		synchronized(ping) { ping.notifyAll(); }
	}
	
	public void standby() {
		System.out.println("Waiting for locks to release");
		if(locked) {
			synchronized(lock) {
				try {
					lock.wait();
				}
				catch(InterruptedException ix) {
					
				}
			}
		}
	}
	
	public void stop() {
		deactivate();
		online = false;
	}
	
	public void close() {
		active = false;
		online = false;
		tool.kill();
	}
	
	private class Lock { /* Equivalent to C++ Typedef*/ }
}