package sd.swiftglobal.rk.util;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetTool;

/* This file is part of Swift Drive				   *
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

public class Terminator implements Settings {
	private final SwiftNetTool tool;
	private final int timeout;
	private boolean running    = true,
					terminated = false;
	private Thread sleep = null;
	
	public Terminator(SwiftNetTool tool, int timeout) {
		this.tool = tool;
		this.timeout = timeout;
	}
	
	public Terminator(SwiftNetTool tool) {
		this.tool = tool;
		timeout = DEF_TIME;
	}
	
	public void run() {
		terminated = false;
		running = true;
		
		sleep = new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(timeout * 1000);
				}
				catch(InterruptedException ix) {
					running = false;
				}
				terminate();
			}
		});
		
		sleep.start();
	}
	
	public void terminate() {
		if(running) {
			System.out.println("exTerminate");
			tool.kill();
			terminated = true;
		}
	}
	
	public boolean terminated() {
		return terminated;
	}
	
	public void cancel() {
		if(sleep != null) sleep.interrupt();
	}
}
