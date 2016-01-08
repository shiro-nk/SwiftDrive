package sd.swiftglobal.rk.expt;

import java.io.IOException;

import sd.swiftglobal.rk.Settings;

/**
 * <b>Disconnection Exception:</b><br>
 * Error thrown when an error occurred between client and server.
 *
 * @author Ryan Kerr
 */
public class DisconnectException extends SwiftException implements Settings {
	private static final long serialVersionUID = 1L;

	public DisconnectException(int errid) {
		super(errid);
	}
	
	public DisconnectException(int errid, IOException ix) {
		super(errid, ix);
	}
}
