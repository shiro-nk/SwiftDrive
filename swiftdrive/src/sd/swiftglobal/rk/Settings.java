package sd.swiftglobal.rk;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import sd.swiftglobal.rk.util.Logging;

/* This file is part of Swift Drive				   *
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

/**
 * 
 * @author Ryan Kerr
 */
public interface Settings {
	public static final int DAT_NULL = 0,
							DAT_INIT = 1,
							DAT_PING = 2,
							DAT_FILE = 3,
							DAT_DATA = 4,
							DAT_SCMD = 5,
							DAT_LGIN = 6,
							DAT_LGCF = 7;

	// Local Settings
	public static final String LC_DIV  = System.getProperty("file.separator"),
							   LC_USER = System.getProperty("user.home"),
							   LC_PATH = LC_USER + LC_DIV + "swift" + LC_DIV;

	// Character Settings
	public static final Charset CHARSET = StandardCharsets.UTF_8;

	// Data Transfer Signals	
	public static final int SIG_CLOSE = 0,
							SIG_READY = 1,
							SIG_FAIL  = 2;

	public static final int STA_LISTENING  = 1,
							STA_PROCESSING = 2,
							STA_RESPONDING = 3,
							STA_SHUTDOWN   = 4;
	
	// Exception ID
	public static final int EXC_FATAL = 0,
							EXC_WRITE = 1,
							EXC_READ  = 2,
							EXC_SIZE  = 3,
							EXC_F404  = 4,
							EXC_MISS  = 5,
							EXC_UNKN  = 6,
							EXC_CONN  = 7;
	
	// Program Defaults
	public static final int DEF_PORT = 3141,
							DEF_BEAT = 25,
							DEF_LOGL = Logging.LOG_SEC;
}
