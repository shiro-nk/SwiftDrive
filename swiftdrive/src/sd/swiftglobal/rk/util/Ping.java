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
 * <b>Ping</b><br>
 * The Ping class is present in order to ensure connectivity is
 * maintained between the server and the client during operation.
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
	
	private Lock lock = new Lock(),
				 ping = new Lock();

	/**
	 * <b>Initialize Ping:</b><br>
	 * Creates a new ping object. This constructor will reference all
	 * the variables and initialize a terminator, however, they will
	 * not be used until run() is called.
	 *
	 * <b>Note:</b> Run is initialized with <b>new Thread(new Ping(a,b,c)).start()</b>
	 *
	 * @param dis Stream to read the return value (signal) from
	 * @param dos Stream to send the ping to
	 * @param c The client to be killed in the event of a failure
	 */
	public Ping(DataInputStream dis, DataOutputStream dos, Client c) {
		this.dis = dis;
		this.dos = dos;
		this.tool = c;
		online = true;
		active = true;
	}
	
	/**
	 * <b>Start the ping thread.</b><br>
	 * This thread only pings every DEF_BEAT seconds while active. <br>
	 * Once the ping timer is offline, it cannot be reactivated. <br>
	 * <br>
	 * If the ping system is not activated, the thread will wait for
	 * the client to unlock the <b>ping</b> variable. While active,
	 * the ping thread will start a new thread which waits for DEF_PING
	 * seconds. If deactivate() or stop() isn't called during this sleep
	 * stage, the thread will lock the <b>lock</b> variable, and send a
	 * message to the server. In order to prevent hanging, the method
	 * runs <b>terminator</b>, listens for a response, and kills terminator.
	 * If the response from the server is <b>SIG_READY</b>, the ping will continue,
	 * by unlocking the <b>lock</b> variable, restarting the method. If the
	 * signal received is <b>SIG_FAIL</b> (server closed), the client will be closed.
	 * If no message was received within the timeout duration or the socket,
	 * has been closed, the terminator will close the client and the ping.<br><br>
	 *
	 * <b>Note:</b> The reason I have a separate thread for running rather
	 * than making this entire class a thread is to avoid locking methods,
	 * such as activate() or deactivate(), while the thread is waiting.
	 */
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
									echo("Listening for response", LOG_LOW);
									int signal = dis.readInt();
									echo("Done", LOG_LOW);
									echo("Response received", LOG_TRI);
									if(signal == SIG_FAIL) {
										echo("Close signal received!", LOG_TRI);
										tool.kill(EXC_SAFE);
									}
									else {
										locked = false;
										echo("Unlocking io lock", LOG_LOW);
										synchronized(lock) { lock.notifyAll(); }
									}
								}
								catch(IOException ix) {
									close();
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
				sleep.setName("Ping");
				sleep.start();
				try { sleep.join(); } 
				catch (InterruptedException e) {}
			}
		}
	}
	
	/**
	 * <b>Ping Deactivate:</b><br>
	 * Forces the thread to wait until activate() is called. <br><br>
	 *
	 * <b>Note:</b> This method does not yield to send-receive processes
	 */
	public void deactivate() {
		echo("Requesting ping to standby", LOG_LOW);
		active = false;
		echo("Attempting to interrupt sleep", LOG_LOW);
		if(sleep != null) sleep.interrupt();
		echo("Deactivation request completed", LOG_LOW);
	}
	
	/**
	 * <b>Ping Activation:</b><br>
	 * Re-enables the ping thread. This method is called in order to
	 * restart the ping countdown. This is done by unlocking the
	 * <b>ping</b> variable, allowing the run() method to continue.
	 */
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
	
	/**
	 * <b>Standby:</b><br>
	 * Causes the running method to yield runtime to the run() method
	 * while run() is sending and receiving. This is intended to
	 * protect the client-server connection from losing it's position.
	 * standby() works by checking whether the send-receive process is
	 * running. If it is running, the send-receive in run() would have
	 * locked <b>lock</b>. This method waits until <b>lock</b> is
	 * released by run() before allowing the calling method to proceed.
	 */
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
	
	/**
	 * <b>Pause:</b><br>
	 * Deactivates ping and waits in the event the thread was running
	 * the send-receive process. <br><br>
	 * 
	 * <b>Note:</b> See deactivate() and standby() for more information
	 */
	public void pause() {
		deactivate();
		standby();
	}
	
	/**
	 * <b>Stop:</b><br>
	 * Deactivates and sets the running status to false, preventing the
	 * thread from repeating the process. <br><br>
	 *
	 * <b>Note:</b> The reasons the <b>ping</b> variable is unlocked is
	 * to destroy any thread that may be waiting on this variable.
	 */
	public void stop() {
		echo("Stopping", LOG_TRI);
		online = false;
		deactivate();

		echo("HALT");
		synchronized(ping) {
			ping.notifyAll();
		}
	}

	/**
	 * <b>Close:</b><br>
	 * Called when ping fails in order to destroy the client and ping.
	 */
	public void close() {
		echo("Killing interface", LOG_TRI);
		stop();
		active = false;
		online = false;
		tool.kill(EXC_CONN);
	}

	/**
	 * <b>Echo:</b><br>
	 * Overridden method from the Logging interface. <br>
	 * Adds [ Ping   ] tag to System.out.println()
	 */
	@Override
	public void echo(Object str, int level) {
		print("[ Ping   ] " + str.toString() + "\n", level);
	}

	@Typedef("Lock")
	private class Lock {}
}
