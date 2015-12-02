package sd.swiftglobal.rk;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import sd.swiftclient.rk.net.Client;
import sd.swiftglobal.rk.expt.SwiftException;
import sd.swiftglobal.rk.type.ServerCommand;
import sd.swiftglobal.rk.type.SwiftFile;
import sd.swiftglobal.rk.type.tasks.SubTask;
import sd.swiftglobal.rk.type.tasks.Task;
import sd.swiftglobal.rk.type.tasks.TaskHandler;
import sd.swiftglobal.rk.type.users.UserHandler;
import sd.swiftglobal.rk.util.Logging;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetContainer;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetTool;
import sd.swiftserver.rk.net.Server;

/* This file is part of Swift Drive				  *
 * Copyright (c) 2015 Ryan Kerr					  *
 * Please refer to <http://www.gnu.org/licenses/> */

public class SwiftTest implements Settings, Logging, SwiftNetContainer {
	public static void main(String[] args) {
		System.out.println("\n");
		println("Welcome to Swift Drive!");
		println("Version " + VER_MAJOR + " Release " + VER_MINOR + " Patch " + VER_PATCH);
		SwiftTest test = new SwiftTest();
		System.out.println();
		createDirectory();

		println(System.getProperty("os.name"));
		if(System.getProperty("os.name").equalsIgnoreCase("windows")) {
			System.err.println("Error: Testing classes are incompatible at the current time");
			System.exit(100);
		}

		if(0 < args.length && args[0].equals("server") && 2 == args.length) {
			int port = Integer.parseInt(args[1].replaceAll("[^0-9]", ""));
			test.startServer(port);
		}
		if(0 < args.length && args[0].equals("client") && 3 == args.length) {
			int port = Integer.parseInt(args[2].replaceAll("[^0-9]", ""));
			test.startClient(args[1], port);
		}
		else {
			println("[ Usage ] java -jar swiftdrive.jar <server> <port>");
			println("[ Usage ] Leave <server> and <port> blank for client mode");
		}
	}

	public static void println(String s) {
		System.out.println("[ Test   ] " + s);
	}

	public SwiftTest() {
		echo("Swift Drive is licensed under GNU GPLv3", LOG_PRI);
		echo("Copyright (c) 2015 Ryan Kerr, Mohan Pan, Isaiah Power", LOG_PRI);
		echo("Please refer to LICENSE and README for more information", LOG_PRI);
	}

	public void startServer(int port) {
		try(Server server = new Server(port)) {	
			TaskHandler th = new TaskHandler(server.getUserlist());
			echo("Server started. Type \"stop\" to stop the server", LOG_PRI);
			Scanner scan = new Scanner(System.in);
			String input = "";
			
			while(!(input = scan.nextLine()).equals("stop")) {
				if(input.equals("print")) {
					for(Task t : th.getArray()) {
						echo(" * " + t);
						for(SubTask s : t.getArray()) {
							echo(" ** " + s);
						}
					}
					echo(" ------------------------------------------ ");
					th.reload();
					for(Task t : th.getArray()) {
						echo(" * " + t);
						for(SubTask s : t.getArray()) {
							echo(" ** " + s);
						}
					}
					
				}
			}

			scan.close();
		}
		catch(SwiftException sfx) {
			echo("The server disconnected unexpectedly: " + sfx.getMessage(), LOG_PRI);
		}
	}

	public void startClient(String hostname, int port) {
		UserHandler u = null;
		TaskHandler th = null;

		try {
			u  = new UserHandler();
			th = new TaskHandler(u);
		}
		catch(SwiftException s) {

		}
			
		echo("Connecting to " + hostname + ":" + port);
		try(Client client = new Client(hostname, port, this)) {
			echo("Client started");
			Scanner scan = new Scanner(System.in);
			System.out.print("[ Login  ] Username: ");
			String username = scan.nextLine();
			System.out.print("[ Login  ] Password: ");
			String password = scan.nextLine();

			if(!client.login(username, password.getBytes())) {
				echo("Credentials invalid, exiting");
				System.exit(5);
			}

			String input = "";
			while(!(input = scan.nextLine()).equals("stop")) {
				if(input.equals("get")) {
					SwiftFile file = client.sfcmd(
						new ServerCommand(CMD_READ_FILE, "task/index")
					);
					file.setFile(new File(LC_PATH + "task/index"), false);
					file.write();
				}

				if(input.equals("print")) {
					for(Task t : th.getArray()) {
						echo(" * " + t);
						for(SubTask s : t.getArray()) {
							echo(" ** " + s);
						}
					}
					echo(" ------------------------------------------ ");
					th.reload();
					for(Task t : th.getArray()) {
						echo(" * " + t);
						for(SubTask s : t.getArray()) {
							echo(" ** " + s);
						}
					}
				}

				if(input.equals("addtask")) {
					th.add(new Task(0, "A;A;A", u));
				}

				if(input.equals("deltask")) {
					th.add(new Task(0, "A;A;A", u));
				}

				if(input.equals("set")) {
					SwiftFile file = new SwiftFile(LC_PATH + "task/index", true);
					client.sfcmd(new ServerCommand(CMD_WRITE_FILE, "task/index"), file);
				}
			}

			client.disconnect();
			scan.close();
		}
		catch(SwiftException | IOException sfx) {
			echo("The client disconnected unexpectedly: " + sfx.getMessage(), LOG_PRI);
		}

	}

	public static void createDirectory() {
		File file = new File(LC_PATH);
		if(!file.exists() && !file.isDirectory()) {
			System.out.println("[ Start  ] Creating " + LC_PATH);
			file.mkdir();
		}
	}

	public void echo(Object o, int level) {
		print("[ Test   ] " + o.toString() + "\n", level);
	}

	@Override
	public void dereference(SwiftNetTool t) {
			
	}
}
