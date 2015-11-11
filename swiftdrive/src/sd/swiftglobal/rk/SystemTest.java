package sd.swiftglobal.rk;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import sd.swiftclient.rk.net.Client;
import sd.swiftglobal.rk.expt.CommandException;
import sd.swiftglobal.rk.expt.DisconnectException;
import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.gui.SwiftLogin;
import sd.swiftglobal.rk.gui.SwiftScreen;
import sd.swiftglobal.rk.type.Generic;
import sd.swiftglobal.rk.type.ServerCommand;
import sd.swiftglobal.rk.type.SwiftFile;
import sd.swiftglobal.rk.type.users.UserHandler;
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
		new SystemTest(1);
	}
	
	private boolean scanning = true;

	public SystemTest() {
		SwiftScreen screen = new SwiftScreen("SwiftScreen");
		screen.setPanel(new SwiftLogin(screen));
	}

	public SystemTest(int nill) {
		try(Server server = new Server()) {
			UserHandler list = new UserHandler();
			server.setUserlist(list);
			try(Client client = new Client("localhost", 3141, this)) {
				try(Scanner scan = new Scanner(System.in)) {
					String sc = "";
					client.login("username", "password");
					SwiftFile file = new SwiftFile(LC_PATH + "cinput", true);
					client.sfcmd(new ServerCommand(CMD_WRITE_FILE, LC_PATH + "coutput"), file);
					file = client.sfcmd(new ServerCommand(CMD_READ_FILE, LC_PATH + "input"));
					file.write(new File(LC_PATH + "output").toPath(), false);
					while((sc = scan.nextLine()) != null && !sc.equals("stop") && scanning) {
						if(!sc.equals("")) {
							Generic gen = new Generic();
							gen.add(sc);
							client.scmd(new ServerCommand(CMD_WRITE_DATA, LC_PATH + "test"), gen);
						}
					}
				} catch (CommandException | FileException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
