package sd.swiftglobal.rk;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import sd.swiftclient.rk.net.Client;
import sd.swiftglobal.rk.expt.CommandException;
import sd.swiftglobal.rk.expt.DisconnectException;
import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.type.Generic;
import sd.swiftglobal.rk.type.SwiftFile;
import sd.swiftglobal.rk.util.ServerCommand;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetContainer;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetTool;
import sd.swiftserver.rk.net.Server;

/* This file is part of Swift Drive				   *
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

/**
 * Class to test other classes
 *
 * @author Ryan Kerr
 */
public class SystemTest implements SwiftNetContainer, Settings {
	public static void main(String[] args) {
		new SystemTest();
	}
	
	private boolean scanning = true;
	
	public SystemTest() {
		try(Server server = new Server()) {
			try(Client client = new Client("localhost", 3141, this)) {
				try(Scanner scan = new Scanner(System.in)) {
					String sc = "";
					SwiftFile file = client.sfcmd(new ServerCommand(CMD_READ_FILE, LC_PATH + "input"));
					file.write(new File(LC_PATH + "output").toPath(), false);
					while((sc = scan.nextLine()) != null && !scan.equals("stop") && scanning) {
						Generic gen = new Generic();
						gen.add(sc);
						client.scmd(new ServerCommand(CMD_WRITE_DATA, LC_PATH + "test"), gen);
					}
				} catch (CommandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (FileException | IOException fix) {
					fix.printStackTrace();
				}
			}
			Thread.sleep(10000000);
		}
		catch(InterruptedException ix) {
			
		}
		catch(DisconnectException dx) {
			dx.printStackTrace();
		}
	}
	
	public void dereference(SwiftNetTool t) {
		scanning = false;
		System.out.println("Client close");
		System.exit(1000);
	}
}
