package sd.swiftglobal.rk.expt;

import java.io.IOException;

import sd.swiftglobal.rk.Settings;

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
			case EXC_FATAL:
				return "Fatal error of unknown origin";
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
		}
	}
	
	public int getErrID() {
		return ERRID;
	}
}
