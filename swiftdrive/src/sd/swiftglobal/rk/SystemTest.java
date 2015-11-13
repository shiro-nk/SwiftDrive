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
		new SystemTest(true);
	}
	
	private boolean scanning = true;
	private Server server;

	public SystemTest(boolean nill) {
		try {
			server = new Server();
			server.setUserlist(new UserHandler());
		}
		catch(DisconnectException dx) {
			dx.printStackTrace();
		}

		Scanner scan = new Scanner(System.in);
		scan.nextLine();
		server.close();
		scan.close();
	}

	public SystemTest() {
		Client cli = null;
		SwiftScreen screen = new SwiftScreen("SwiftScreen", this);
		screen.setPanel(new SwiftLogin(screen, null));
	}

	public SystemTest(int nill) {
		try(Server server = new Server()) {
			UserHandler list = new UserHandler();
			server.setUserlist(list);
			try(Client client = new Client("localhost", 3141, this)) {
				try(Scanner scan = new Scanner(System.in)) {
					String sc = "";
					System.out.println("Logging in");
					client.login("username", "password".getBytes());
					System.out.println("Reading cinput and sending");
					SwiftFile file = new SwiftFile(LC_PATH + "cinput", true);
					System.out.println("Dumping file to coutput");
					client.sfcmd(new ServerCommand(CMD_WRITE_FILE, LC_PATH + "coutput"), file);
					System.out.println("Requesting input");
					file = client.sfcmd(new ServerCommand(CMD_READ_FILE, LC_PATH + "input"));
					System.out.println("writing output");
					file.write(new File(LC_PATH + "output").toPath(), false);
					while((sc = scan.nextLine()) != null && !sc.equals("stop") && scanning) {
						if(!sc.equals("")) {
							Generic gen = new Generic();
							gen.add(sc);
							client.scmd(new ServerCommand(CMD_WRITE_DATA, LC_PATH + "test"), gen);
							System.out.println("RESP Request");
							client.scmd(new ServerCommand(CMD_SEND_DATA, ""), gen, DAT_DATA);
							System.out.println("Response: " + gen.next());
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
	
	public boolean hasTool() {
		return false;
	}

	public SwiftNetTool getTool() {
		return null;
	}

	public void kill() {

	}
	public void dereference(SwiftNetTool t) {
		scanning = false;
		System.out.println("Client close");
		System.exit(1000);
	}
}
