package sd.swiftglobal.rk.util;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetTool;

/* This file is part of Swift Drive				   *
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

public class Terminator extends Thread implements Runnable, Settings {
	private final SwiftNetTool tool;
	private final int timeout;
	private boolean running    = true,
					terminated = false;
	
	private int id = 0;
	private static int a = 0;
	private static int b = 0;
	public Terminator(SwiftNetTool tool, int timeout) {
		this.tool = tool;
		this.timeout = timeout;
		start();
		id = tool.getID() + tool.getID() == 500 ? ++a : ++b;
	}
	
	public Terminator(SwiftNetTool tool) {
		this.tool = tool;
		timeout = DEF_TOUT;
		start();
	}
	
	public void run() {
		terminated = false;
		running = true;
		System.out.println("Terminator activated by " + id);
		for(int i = 0; i < timeout * (1000/DEF_TIME); i++) {
			try {
				Thread.sleep(DEF_TIME);
				if(!running) i = Integer.MAX_VALUE;
			}
			catch(InterruptedException ix) {
				
			}
		}
		System.out.println("Terminator " + id + " timeout reached with running status of " + running);
		terminate();
	}
	
	public void terminate() {
		if(running) {
			System.out.println("Killing");
			tool.kill();
			terminated = true;
		}
	}
	
	public boolean terminated() {
		return terminated;
	}
	
	public void cancel() {
		System.out.println("Termination " + id + " cancelled");
		running = false;
	}
}
