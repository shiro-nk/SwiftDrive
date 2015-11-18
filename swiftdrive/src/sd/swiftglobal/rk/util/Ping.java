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
		echo("Initialized", LOG_LOW);
		while(online) {
			synchronized(ping) {
				if(!active) {
					try {
						echo("Standing by", LOG_LOW);
						ping.wait();
						echo("Released", LOG_LOW);
					}
					catch(InterruptedException ix) {
						
					}
				}
			}
			while(active) {
				sleep = new Thread(new Runnable() {
					public void run() {
						try {
							echo("Starting sleep", LOG_LOW);
							Thread.sleep(DEF_PING * 1000);
							echo("Ready to send", LOG_LOW);
							if(active && tool.isUnlocked()) {
								try {
									locked = true;
									echo("Sending ping to server", LOG_TRI);
									dos.writeInt(DAT_PING);
									term.run();
									echo("Listening for response", LOG_LOW);
									int signal = dis.readInt();
									echo("Done", LOG_LOW);
									echo("Response received", LOG_TRI);
									if(signal == SIG_FAIL) {
										echo("Close signal received!", LOG_TRI);
										tool.kill(EXC_SAFE);
									}
									term.cancel();
									locked = false;
									echo("Unlocking io lock", LOG_LOW);
									synchronized(lock) { lock.notifyAll(); }
								}
								catch(IOException ix) {
									if(term == null || !term.terminated()) close();
								}
							}
						}
						catch(InterruptedException ix) {
							echo("Interrupted", LOG_TRI);
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
		echo("Requesting ping to standby", LOG_LOW);
		active = false;
		echo("Attempting to interrupt sleep", LOG_LOW);
		if(sleep != null) sleep.interrupt();
		echo("Deactivation request completed", LOG_LOW);
	}
	
	/** Re-enables the ping **/
	public void activate() {
		echo("Requesting ping to wake up", LOG_LOW);
		try {
			Thread.sleep(500);
		}
		catch(InterruptedException ix) {
			
		}
		active = true;
		echo("Attempting to wake from standby", LOG_LOW);
		synchronized(ping) { ping.notifyAll(); }
		echo("Activation request complete", LOG_LOW);
	}
	
	public void standby() {
		if(locked) {
			synchronized(lock) {
				try {
					echo("IO Standing By", LOG_LOW);
					lock.wait();
				}
				catch(InterruptedException ix) {
					
				}
			}
		}
		echo("IO Ready", LOG_LOW);
	}
	
	public void pause() {
		deactivate();
		standby();
	}
	
	public void stop() {
		echo("Stopping", LOG_TRI);
		deactivate();
		online = false;
	}
	
	public void close() {
		echo("Killing client", LOG_TRI);
		stop();
		active = false;
		online = false;
		tool.kill(EXC_CONN);
	}

	public void echo(Object str, int level) {
		print("[ Ping   ] " + str.toString() + "\n", level);
	}

	@Typedef("Lock")
	private class Lock {}
}
