package sd.swiftserver.rk.net;

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
public class ServerTest {
	public static void main(String[] args) {
		String s = "";
		String[] l = new String[0];
		
		for(String x : l) s += x;
		byte[] b = s.getBytes(StandardCharsets.UTF_8);
		
		for(byte x : b) System.out.println(x);
		System.out.println("Len: " + b.length + ", " + l.length);
	}
}
