package sd.swiftglobal.rk.expt;

import java.io.IOException;

import sd.swiftglobal.rk.Settings;

public class SwiftException extends Exception implements Settings {
	private static final long serialVersionUID = 1L;

	public final int ERRID;

	public SwiftException(int errid) {
		super(getErr(errid));
		ERRID = errid;
	}
	
	public SwiftException(int errid, IOException ix) {
		super(getErr(errid) + ": " + ix.getMessage());
		ERRID = errid;
	}
	
	public static String getErr(int errid) {
		switch(errid) {
			default:
			case EXC_FATAL:
				return "Fatal error of unknown origin";
			case EXC_WRITE:
				return "Error while writing to socket";
			case EXC_READ:
				return "Error while reading from socket";
			case EXC_F404:
				return "File not found error";
			case EXC_SIZE:
				return "File too large";
		}
	}
	
	public int getErrID() {
		return ERRID;
	}
}
