package sd.swiftclient.rk.net;

import java.util.Scanner;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.DisconnectException;
import sd.swiftglobal.rk.util.Logging;

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
public class ClientTest implements Settings, Logging {
	public static void main(String[] args) {
		new ClientTest();
	}
	
	ClientTest() {
		try(Client c = new Client("localhost", 3141)) {
			c.fileOut(LC_PATH + "input");
			
			try(Scanner scan = new Scanner(System.in)) {
				String s;
				
				while((s = scan.nextLine()) != null && !s.equals("stop")) {
					c.out(s);
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		catch(DisconnectException dx) {
			error("Failed to initialize client", LOG_FRC);
			error(dx.getMessage(), LOG_FRC);
		}
	}
}