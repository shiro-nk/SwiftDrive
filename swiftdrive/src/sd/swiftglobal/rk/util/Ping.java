package sd.swiftglobal.rk.util;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import sd.swiftclient.rk.net.Client;
import sd.swiftglobal.rk.Meta.Typedef;
import sd.swiftglobal.rk.Settings;

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
public class Ping implements Settings, Logging, Runnable, Closeable {
	private boolean online = false,
					active = false,
					locked = false;
	
	private DataInputStream  dis;
	private	DataOutputStream dos;
	private Client tool;
	private Thread sleep;
	private Terminator term;;
	
	private Lock lock = new Lock(),
				 ping = new Lock();

	/**
	 * Initialize the ping tool
	 * @param dis Stream to read from
	 * @param dos Stream to ping to
	 */
	public Ping(DataInputStream dis, DataOutputStream dos, Client c) {
		this.dis = dis;
		this.dos = dos;
		this.tool = c;
		term = new Terminator(tool);
		online = true;
		active = true;
	}
	
	/**
	 * Start the ping thread.
	 * This thread only pings every DEF_BEAT seconds while active.
	 * Once the ping timer is offline, it cannot be reactivated.
	 */
	int pingno = 0;
	public void run() {
		echo("Initialized", LOG_PRI);
		while(online) {
			synchronized(ping) {
				if(!active) {
					try {
						echo("Standing by", LOG_PRI);
						ping.wait();
						echo("Released", LOG_PRI);
					}
					catch(InterruptedException ix) {
						
					}
				}
			}
			while(active) {
				sleep = new Thread(new Runnable() {
					public void run() {
						try {
							echo("Starting sleep", LOG_PRI);
							Thread.sleep(DEF_PING * 1000);
							echo("Ready to send", LOG_PRI);
							if(active && tool.isUnlocked()) {
								try {
									locked = true;
									echo("Sending ping to server", LOG_SEC);
									dos.writeInt(DAT_PING);
									term.run();
									echo("Listening for response", LOG_PRI);
									dis.readInt();
									echo("Response received", LOG_SEC);
									term.cancel();
									locked = false;
									echo("Unlocking io lock", LOG_PRI);
									synchronized(lock) { lock.notifyAll(); }
								}
								catch(IOException ix) {
									if(term == null || !term.terminated()) close();
								}
							}
						}
						catch(InterruptedException ix) {
							echo("Interrupted");
							active = false;
							synchronized(lock) { lock.notifyAll(); }
						}
					}
				});
				sleep.start();
				try {
					sleep.join();
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/** Temporarily disable the ping heart beat **/
	public void deactivate() {
		echo("Requesting ping to standby", LOG_PRI);
		active = false;
		echo("Attempting to interrupt sleep", LOG_PRI);
		if(sleep != null) sleep.interrupt();
		echo("Deactivation request completed", LOG_PRI);
	}
	
	/** Re-enables the ping **/
	public void activate() {
		echo("Requesting ping to wake up", LOG_PRI);
		try {
			Thread.sleep(500);
		}
		catch(InterruptedException ix) {
			
		}
		active = true;
		echo("Attempting to wake from standby", LOG_PRI);
		synchronized(ping) { ping.notifyAll(); }
		echo("Activation request complete", LOG_PRI);
	}
	
	public void standby() {
		if(locked) {
			synchronized(lock) {
				try {
					echo("IO Standing By", LOG_PRI);
					lock.wait();
				}
				catch(InterruptedException ix) {
					
				}
			}
		}
		echo("IO Ready", LOG_PRI);
	}
	
	public void pause() {
		deactivate();
		standby();
	}
	
	public void stop() {
		echo("Stopping", LOG_SEC);
		deactivate();
		online = false;
	}
	
	public void close() {
		echo("Killing client", LOG_SEC);
		active = false;
		online = false;
		tool.kill();
	}

	public void echo(Object str) {
		System.out.println("[Ping] " + str.toString());
	}

	@Typedef("Lock")
	private class Lock {}
}
