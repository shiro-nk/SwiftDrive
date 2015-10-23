package sd.swiftglobal.rk;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import sd.swiftglobal.rk.util.Logging;

/* This file is part of Swift Drive				   *
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

/**
 * This interface is to act as the preprocessor equivalent
 * to C. All default settings (except for the Logging interface)
 * and pseudo-preprocessor definitions are stored here.
 * 
 * The reason for not using enumeration is to allow for
 * direct access to these variables by simply implementing
 * the Settings class.
 * 
 * @author Ryan Kerr
 */
public interface Settings {
	/** Data (DAT) type headers for transfer **/
	public static final int DAT_NULL = 0,
							DAT_INIT = 1,
							DAT_PING = 2,
							DAT_FILE = 3,
							DAT_DATA = 4,
							DAT_SCMD = 5,
							DAT_LGIN = 6,
							DAT_LGCF = 7;

	/** Local (LC) Environment Variables **/
	public static final String LC_DIV  = System.getProperty("file.separator"),
							   LC_USER = System.getProperty("user.home"),
							   LC_PATH = LC_USER + LC_DIV + "swift" + LC_DIV;

	/** The default charset **/
	public static final Charset CHARSET = StandardCharsets.UTF_8;

	/** Transfer response signals (SIG) **/
	public static final int SIG_CLOSE = 0,
							SIG_READY = 1,
							SIG_FAIL  = 2;

	/** States (STA) that the server is in **/
	public static final int STA_LISTENING  = 1,
							STA_PROCESSING = 2,
							STA_RESPONDING = 3,
							STA_SHUTDOWN   = 4;
	
	/** General SwiftException (EXC) cause identifiers **/
	public static final int EXC_FATAL  = 0,
							EXC_WRITE  = 1,
							EXC_READ   = 2,
							EXC_SIZE   = 3,
							EXC_F404   = 4,
							EXC_MISS   = 5,
							EXC_UNKN   = 6,
							EXC_CONN   = 7,
							EXC_NREAD  = 8,
							EXC_NWRITE = 9,
							EXC_TYPEMM = 10;
	
	/** Preset Defaults / Definitions (DEF) **/
	public static final int DEF_PORT = 3141,
							DEF_TIME = 200,
							DEF_PING = 10,
							DEF_TOUT = 15,
							DEF_DDOS = 500,
							DEF_LOGL = Logging.LOG_TRI;
}
