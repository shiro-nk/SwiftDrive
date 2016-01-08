package sd.swiftglobal.rk.expt;

/**
 * <b>Command Execution Exception:</b><br>
 * Thrown if the server is given a blank or malformed command
 *
 * @author Ryan Kerr
 */
public class CommandException extends SwiftException {
	private static final long serialVersionUID = 1L;

	public CommandException(int errid) {
		super(errid);
	}
}
