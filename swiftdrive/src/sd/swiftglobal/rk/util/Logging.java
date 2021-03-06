package sd.swiftglobal.rk.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import sd.swiftglobal.rk.Settings;

/* This file is part of Swift Drive				   *
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

/**
 * This interface provides a shortcut for controlled logging. <br>
 * Errors and plain outputs can be shown based on their importance <br>
 * defined by the Log level definitions below.
 * 
 * @author Ryan Kerr
 */
public interface Logging {
	/** Log (LOG) Level definitions **/
	public static final int LOG_FRC = 0, // Display All
							LOG_OFF = 1, // No Logging
							LOG_PRI = 2, // User level logging
							LOG_SEC = 3, // Partial debug level logging
							LOG_TRI = 4, // Debug level logging
							LOG_LOW = 5, // Verbose debug level logging
							LOG_LVL = Settings.DEF_LOGL;
	
	public static final String LOG_DIR = Settings.LC_PATH + "log";
	
	/**
	 * System.out.println wrapper that only prints <br>
	 * if the priority is equal or higher than the defined log level
	 * @param o Object to write to console as a string
	 * @param lvl Priority of message
	 */
	public default void print(Object o, int lvl) {
		if(lvl == LOG_FRC || lvl <= LOG_LVL) System.out.print(o.toString());
	}
	
	/** 
	 * Same as print(Object, int) with mid-priority
	 * @param o Object to written to console as a string
	 */
	public default void print(Object o) {
		print(o, LOG_SEC);
	}
	
	/**
	 * Same as print(Object, in) except a new line is added
	 * @param o Object to be written to console as a string
	 * @param lvl Priority of message
	 */
	public default void echo(Object o, int lvl) {
		print(o + "\n", lvl);
	}

	/**
	 * Same as echo(Object, int) with mid-priority
	 * @param o Priority of message
	 */
	public default void echo(Object o) {
		echo(o, LOG_SEC);
	}
	
	/**
	 * System.err.println wrapper which only prints the error <br>
	 * if the given priority is higher than the default priority
	 * @param o Error to write to console
	 * @param lvl Priority of error message
	 */
	public default void error(Object o, int lvl) {
		if(lvl == LOG_FRC || lvl <= LOG_LVL) System.err.println(o.toString());
	}
	
	/**
	 * Same as error(Object, int) with mid-priority
	 * @param o Error to write to console
	 */
	public default void error(Object o) {
		error(o, LOG_SEC);
	}
	
	/**
	 * Appends the given message to the defined log file (LOG_FILE)
	 * @param o Object to write to Log file
	 */
	public default void log(Object o) {
		try(PrintWriter log = new PrintWriter(new FileWriter(LOG_DIR, true))) {
			log.write(o.toString());
		}
		catch(IOException ix) {
			System.err.println("Error appending to log");
		} 
	}
}
