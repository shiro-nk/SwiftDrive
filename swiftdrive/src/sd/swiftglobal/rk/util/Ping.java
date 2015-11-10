package sd.swiftglobal.rk.util;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import sd.swiftglobal.rk.Meta.Typedef;
import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.type.SwiftFile;
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

	private SwiftFile output;

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
			echo("Ping has started");
			synchronized(ping) {
				if(!active) {
					try {
						echo("Ping: Waiting");
						ping.wait();
						echo("Ping: Unlocked");
					}
					catch(InterruptedException ix) {
						
					}
				}
			}
			echo("Ping: " + active);
			while(active) {
				echo("Ping: " + ++x);
				sleep = new Thread(new Runnable() {
					public void run() {
						try {
							Thread.sleep(DEF_PING * 1000);
							echo("xasdf");
							if(active) {
								try {
									locked = true;
									echo("Ping: Ping");
									dos.writeInt(DAT_PING);
									term.run();
									dis.readInt();
									echo("Ping: Response");
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
							synchronized(lock) { lock.notifyAll(); }
							echo("Ping: Interrupted");
						}
					}
				});
				sleep.start();
				try {
					sleep.join();
					echo("Ping joined");
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				echo("Ping hit end");
			}
		}
		echo(y);
		if(y== 5) System.exit(128904);
	}
	
	/** Temporarily disable the ping heart beat **/
	public void deactivate() {
		echo("Deactivated");
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
		echo("Activated");
		active = true;
		synchronized(ping) { ping.notifyAll(); }
	}
	
	public void standby() {
		echo("Waiting for locks to release");
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
	
	public void pause() {
		deactivate();
		standby();
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

	@Deprecated
	public void echo(Object str) {
		try {
			if(output == null) output = new SwiftFile(LC_PATH + "ping", false);
			output.reset();
			output.add(str.toString() + "\n");
			output.toData();
			output.append();
		}
		catch(IOException ix) {
			ix.printStackTrace();
		}
		catch(FileException fx) {
			fx.printStackTrace();
		}
	}

	@Deprecated
	public void echo(Object str, int level) {
		echo(str);
	}
	
	@Typedef("Lock")
	private class Lock {}
}
