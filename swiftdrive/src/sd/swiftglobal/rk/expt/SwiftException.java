package sd.swiftglobal.rk.expt;

import java.io.IOException;

import sd.swiftglobal.rk.Settings;

/* This file is part of Swift Drive				   *
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

/**
 * 
 * @author Ryan Kerr
 */
public class SwiftException extends Exception implements Settings {
	private static final long serialVersionUID = 1L;

	public final int ERRID;

	public SwiftException(int errid) {
		super(getErr(errid));
		ERRID = errid;
	}
	
	public SwiftException(int errid, IOException ix) {
		super(getErr(errid) + ": " + ix.getMessage());
		ERRID = errid;
	}
	
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
	
	public int getErrID() {
		return ERRID;
	}
}
