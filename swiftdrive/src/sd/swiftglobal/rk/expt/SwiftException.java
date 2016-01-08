package sd.swiftglobal.rk.expt;

import java.io.IOException;

import sd.swiftglobal.rk.Settings;

/* This file is part of Swift Drive				   *
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

/**
 * <b>Swift Drive Custom Exceptions:</b><br>
 * Provides more specific error messages and the option to link an
 * IOException to the exception.
 *
 * @author Ryan Kerr
 */
public class SwiftException extends Exception implements Settings {
	private static final long serialVersionUID = 1L;

	public final int ERRID;

	/**
	 * <b>Constructor</b><br>
	 * Throw an exception with a specific error id (and message)
	 *
	 * @param errid Error ID (using <b>Settings</b> EXC_ identifiers)
	 */
	public SwiftException(int errid) {
		super(getErr(errid));
		ERRID = errid;
	}
	
	/**
	 * <b>Constructor:</b><br>
	 * Throw an error triggered by an IOException with an added 
	 * error id
	 *
	 * @param errid (EXC) Error Identification
	 * @param ix The IOException that provides more information
	 */
	public SwiftException(int errid, IOException ix) {
		super(getErr(errid) + ": " + ix.getMessage());
		ERRID = errid;
	}
	
	/**
	 * <b>Get Corresponding Error Message:</b><br>
	 * Get a human readable error message which corresponds to the
	 * given (EXC) number
	 *
	 * @param errid Error ID
	 * @return Error Message
	 */
	public static String getErr(int errid) {
		switch(errid) {
			default:
			case EXC_UNKN:
				return "Error of unknown magnitude";
			case EXC_WRITE:
				return "Error while writing to socket";
			case EXC_READ:
				return "Error while reading from socket";
			case EXC_F404:
				return "File not found error";
			case EXC_SIZE:
				return "File too large";
			case EXC_MISS:
				return "Information missing error";
			case EXC_CONN:
				return "Error establishing/maintaining connection";
			case EXC_FATAL:
				return "Fatal Error! Program should now halt!";
		}
	}
	
	/**
	 * <b>Get Error Code</b><br>
	 * @return (EXC) error code
	 */
	public int getErrID() {
		return ERRID;
	}
}
