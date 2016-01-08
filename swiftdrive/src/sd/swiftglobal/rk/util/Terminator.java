package sd.swiftglobal.rk.util;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetTool;

/* This file is part of Swift Drive				   *
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

@Deprecated
public class Terminator implements Logging, Settings {
	private final SwiftNetTool tool;
	private final int timeout;
	private boolean running    = true,
					terminated = false,
					run = true,
					thread = false;
	private Thread sleep = null;

	int runx = 0;
	
	public Terminator(SwiftNetTool tool, int timeout) {
		this.tool = tool;
		this.timeout = timeout;
	}
	
	public Terminator(SwiftNetTool tool) {
		this.tool = tool;
		timeout = DEF_TIME;
	}
	
	public void run() {
		cancel();
		if(run && !thread) {
			runx++;
			thread = true;
			terminated = false;
			running = true;
			sleep = new Thread(new Runnable() {
				public void run() {
					try {
						echo("Terminator started" + runx);
						Thread.sleep(timeout * 1000);
					}
					catch(InterruptedException ix) {
						running = false;
					}
					terminate();
				}
			});
			sleep.setName("Terminator");
			sleep.start();
			thread = false;
		}
	}
	
	public void terminate() {
		if(running && !terminated) {
			echo("Timed out" + runx);
			tool.kill(EXC_TERM);
			terminated = true;
		}
	}
	
	public boolean terminated() {
		return terminated;
	}

	public void cancel() {
		echo("Cancelling countdown" + runx);
		if(sleep != null) sleep.interrupt();
	}

	public void destroy() {
		run = false;
		cancel();
	}

	public void echo(Object o) {
		print("[ Term   ] " + o.toString() + "\n", LOG_LOW);
	}
}
