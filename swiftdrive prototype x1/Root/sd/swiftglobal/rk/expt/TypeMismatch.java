package sd.swiftglobal.rk.expt;

import java.io.IOException;

public class TypeMismatch extends Exception {
	private static final long serialVersionUID = 1L;
	
	public TypeMismatch(IOException ix) {
		super(ix.getMessage());
	}
}