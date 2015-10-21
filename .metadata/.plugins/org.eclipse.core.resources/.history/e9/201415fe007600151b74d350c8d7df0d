package sd.swiftglobal.rk.util;

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
public interface Logging {
	
	public static final int LOG_DEF = Settings.DEF_LOGL,
							LOG_LVL = Settings.LOG_LVL,
							LOG_FRC = Settings.LOG_FRC;
	
	public default void print(Object out, int level) {
		if(level == LOG_FRC || level <= LOG_LVL) System.out.print(out);
	}
	
	public default void print(Object out) {
		print(out, LOG_DEF);
	}
	
	public default void echo(Object out, int level) {
		print(out + "\n", level);
	}
	
	public default void echo(Object out) {
		echo(out, LOG_DEF);
	}
	
	public default void error(Object out, int level) {
		if(level == LOG_FRC || level <= LOG_LVL) System.err.println(out);;
	}
	
	public default void error(Object out) {
		error(out, LOG_DEF);
	}
}
