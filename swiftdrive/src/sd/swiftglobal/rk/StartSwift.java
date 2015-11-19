package sd.swiftglobal.rk;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import sd.swiftclient.SwiftClient;
import sd.swiftglobal.rk.expt.DisconnectException;
import sd.swiftglobal.rk.util.Logging;
import sd.swiftserver.rk.net.Server;

/* This file is part of Swift Drive				  *
 * Copyright (c) 2015 Ryan Kerr					  *
 * Please refer to <http://www.gnu.org/licenses/> */

public class StartSwift implements Settings, Logging {
	public static void main(String[] args) {
		System.out.println("\n");
		System.out.println("[ Start  ] Welcome to Swift Drive!");
		StartSwift start = new StartSwift();
		System.out.println();
		createDirectory();
		if(0 < args.length && args[0].equals("server") && 2 == args.length) {
			int port = Integer.parseInt(args[1].replaceAll("[^0-9]", ""));
			start.startServer(port);
		}
		else if(args.length == 0) {
			start.startClient();
		}
		else {
			System.out.println("[ Usage  ] java -jar swiftdrive.jar <server> <port>");
			System.out.println("[ Usage  ] Leave <server> and <port> blank for client mode");
		}
	}

	public StartSwift() {
		echo("Swift Drive is licensed under the GNU GPLv3", LOG_PRI);
		echo("Copyright (c) 2015 Ryan Kerr, Mohan Pan, Isaiah Power", LOG_PRI);
		echo("Please refer to LICENSE and README for more information", LOG_PRI);
	}

	public void startServer(int port) {
		try(Server server = new Server(port)) {	
			echo("Server started. Type \"stop\" to stop the server", LOG_PRI);
			Scanner scan = new Scanner(System.in);
			while(!scan.nextLine().equals("stop"));
			scan.close();
		}
		catch(DisconnectException dx) {
			echo("The server disconnected unexpectedly: " + dx.getMessage(), LOG_PRI);
		}
	}

	public void startClient() {
		new SwiftClient();
	}

	public static void createDirectory() {
		File file = new File(LC_PATH);
		if(file.exists() && file.isDirectory()) {
			file.mkdir();
			try {
				new File(LC_PATH + "users").createNewFile();
			}
			catch(IOException ix) {
			
			}
		}
	}

	public void echo(Object o, int level) {
		print("[ Start  ] " + o.toString() + "\n", level);
	}
}
