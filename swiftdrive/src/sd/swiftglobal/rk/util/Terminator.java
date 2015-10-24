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
	
	private static int no = 0;
	
	public Terminator(SwiftNetTool tool, int timeout) {
		this.tool = tool;
		this.timeout = timeout;
		start();
	}
	
	public Terminator(SwiftNetTool tool) {
		this.tool = tool;
		timeout = DEF_TOUT;
		start();
		if(tool.getID() == 500) no++;
	}
	
	public void run() {
		terminated = false;
		running = true;
		for(int i = 0; i < timeout * (1000/DEF_TIME); i++) {
			try {
				Thread.sleep(DEF_TIME);
				if(!running) System.out.println(tool.getID() == 500 ? "ClientSide" : "ServerSide");
				if(!running) i = Integer.MAX_VALUE - 1;
				if(!running) System.out.println("Not Running " + no + " " + i + " : " + timeout *(1000/DEF_TIME));
				if(!running) System.out.println(i < (timeout * (1000/DEF_TIME)));
			}
			catch(InterruptedException ix) {
				ix.printStackTrace();
			}
		}
		terminate();
	}
	
	public void terminate() {
		if(running) {
			tool.kill();
			terminated = true;
		}
	}
	
	public boolean terminated() {
		return terminated;
	}
	
	public void cancel() {
		System.out.println("Close " + no);
		running = false;
	}
}
