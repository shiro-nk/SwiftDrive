package sd.swiftglobal.rk.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.DisconnectException;

public class PingTimer extends Thread implements Runnable, Settings {
	
	boolean active,
			kill;

	long increment;
	
	int cycle,
		timer,
		timeout;

	DataInputStream  dis;
	DataOutputStream dos;
	
	public PingTimer(DataInputStream in, DataOutputStream out) {
		
	}
	
	public void run() {
		
	}
	
	public void ping() throws DisconnectException {
		try {
			dos.writeInt(DAT_PING);
		}
		catch(IOException ix) {
			throw new DisconnectException("");
		}
	}
	
	public void pingListen(int p) {

	}
}
