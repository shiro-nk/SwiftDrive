package sd.swiftglobal.rk;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/*
 * This file is part of Swift Drive
 * Copyright (C) 2015 Ryan Kerr
 *
 * Swift Drive is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * Swift Drive is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of  MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Swift Drive. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * 
 * @author Ryan Kerr
 */
public interface Settings {
	
	// Data Type Identifiers
	public static final int DAT_NULL = 0,
							DAT_INIT = 1,
							DAT_PING = 2,
							DAT_FILE = 3,
							DAT_DATA = 4;
	
	// Log Level Specifiers
	public static final int LOG_FRC = 0,
							LOG_PRI = 1,
							LOG_SEC = 2,
							LOG_TRI = 3,
							LOG_ALL = 4,
							LOG_LVL = LOG_TRI;
	
	// Local Settings
	public static final String LC_DIV  = System.getProperty("file.separator"),
							   LC_USER = System.getProperty("user.home"),
							   LC_PATH = LC_USER + LC_DIV + "swift" + LC_DIV;

	// Character Settings
	public static final Charset CHARSET = StandardCharsets.UTF_8;
	
	// Data Transfer Signals
	public static final int SIG_READY = 1;

	// Program Defaults
	public static final int DEF_PORT = 3141,
							DEF_BEAT = 25,
							DEF_LOGL = LOG_TRI;
}