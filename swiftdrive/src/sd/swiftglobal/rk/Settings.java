package sd.swiftglobal.rk;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import sd.swiftglobal.rk.util.Logging;

public interface Settings {
	public static final int DAT_NULL = 0,
							DAT_INIT = 1,
							DAT_PING = 2,
							DAT_FILE = 3,
							DAT_DATA = 4;

	// Local Settings
	public static final String LC_DIV  = System.getProperty("file.separator"),
							   LC_USER = System.getProperty("user.home"),
							   LC_PATH = LC_USER + LC_DIV + "swift" + LC_DIV;

	// Character Settings
	public static final Charset CHARSET = StandardCharsets.UTF_8;

	// Data Transfer Signals	
	public static final int SIG_READY = 1;

	// Exception ID
	public static final int EXC_FATAL = 0,
							EXC_WRITE = 1,
							EXC_READ  = 2,
							EXC_SIZE  = 3,
							EXC_F404  = 4;
	
	// Program Defaults
	public static final int DEF_PORT = 3141,
							DEF_BEAT = 25,
							DEF_LOGL = Logging.LOG_SEC;
}
