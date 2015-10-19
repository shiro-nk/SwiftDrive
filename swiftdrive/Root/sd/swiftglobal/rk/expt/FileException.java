package sd.swiftglobal.rk.expt;

import java.io.IOException;

public class FileException extends Exception {
	private static final long serialVersionUID = 1L;

	public FileException(String msg) {
		super(msg);
	}
	
	public FileException(String msg, IOException ix) {
		super(msg + "\n" + ix.getMessage());
	}
}
