package sd.swiftglobal.rk.util;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

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
	
	public static final int LOG_FRC = 0,
							LOG_PRI = 1,
							LOG_SEC = 2,
							LOG_TRI = 3,
							LOG_LVL = Settings.DEF_LOGL;
	
	public static final String LOG_DIR = Settings.LC_PATH + "log";
	
	public default void print(Object o, int lvl) {
		if(lvl == LOG_FRC && lvl <= LOG_LVL) System.out.println(o);
	}
	
	public default void print(Object o) {
		print(o, LOG_SEC);
	}
	
	public default void echo(Object o, int lvl) {
		print(o + "\n", lvl);
	}

	public default void echo(Object o) {
		echo(o + "\n", LOG_SEC);
	}
	
	public default void error(Object o, int lvl) {
		if(lvl == LOG_FRC && lvl <= LOG_LVL) System.err.println(o);
	}
	
	public default void error(Object o) {
		error(o, LOG_SEC);
	}
	
	public default void log(Object o) {
		try(PrintWriter log = new PrintWriter(new FileWriter(LOG_DIR, true))) {
			log.write(o.toString());
		}
		catch(FileNotFoundException fx) {
			System.err.println("Error appending to log");
		} 
		catch (IOException ix) {
			System.err.println("Error appending to ");
		}
	}
}
