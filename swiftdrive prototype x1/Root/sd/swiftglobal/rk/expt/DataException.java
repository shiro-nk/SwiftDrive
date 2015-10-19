package sd.swiftglobal.rk.expt;

public class DataException extends Exception {
	private static final long serialVersionUID = 1L;

	public DataException(String msg) {
		super(msg);
	}
	
	public DataException(DisconnectException dx) {
		this(dx.getMessage());
	}
	
	public DataException(FileException fx) {
		this(fx.getMessage());
	}
}
