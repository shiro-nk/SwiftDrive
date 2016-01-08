package sd.swiftglobal.rk.expt;

import java.io.IOException;

import sd.swiftglobal.rk.Settings;

/**
 * <b>File I/O Exception:</b><br>
 * Thrown if there is a failure when reading or writing to a file on
 * disk
 *
 * @author Ryan Kerr
 */
public class FileException extends SwiftException implements Settings {
	private static final long serialVersionUID = 1L;

	public FileException(int errid) {
		super(errid);
	}
	
	public FileException(int errid, IOException ix) {
		super(errid, ix);
	}
}
