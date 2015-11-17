package sd.swiftglobal.rk.expt;

import java.io.IOException;

import sd.swiftglobal.rk.Settings;

public class FileException extends SwiftException implements Settings {
	private static final long serialVersionUID = 1L;

	public FileException(int errid) {
		super(errid);
	}
	
	public FileException(int errid, IOException ix) {
		super(errid, ix);
	}
}
