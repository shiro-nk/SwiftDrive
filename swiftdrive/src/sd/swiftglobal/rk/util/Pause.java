package sd.swiftglobal.rk.util;

public class Pause extends Thread implements Runnable {
	private int sleep;
	private boolean seconds;
	
	public Pause(int sleep, boolean inSeconds) {
		this.sleep = sleep;
		seconds = inSeconds;
	}
	
	public Pause(int sleep) {
		this(sleep, true);
	}
	
	public void run() {
		try {
			Thread.sleep(sleep * (seconds ? 1000 : 1));
		}
		catch(InterruptedException ix) {
			
		}
	}
}
