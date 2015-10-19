package sd.swiftglobal.rk.expt;

import java.io.IOException;

public class DisconnectException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public DisconnectException(String msg) {
		super(msg);
	}
	
	public DisconnectException(String msg, IOException ix) {
		super(msg + "\n" + ix.getMessage());
	}
}
