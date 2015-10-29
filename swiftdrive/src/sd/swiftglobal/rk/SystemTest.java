package sd.swiftglobal.rk;

import java.util.Scanner;

import sd.swiftclient.rk.nk.Client;
import sd.swiftglobal.rk.expt.DisconnectException;
import sd.swiftglobal.rk.type.Data;
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
			try(Client client = new Client(this, "localhost", 3141)) {
				try(Scanner scan = new Scanner(System.in)) {
					String sc = "";
					while((sc = scan.nextLine()) != null && !scan.equals("stop") && scanning) {
						Data dat = new Data(DAT_DATA, 0) {
							public void toData() {
							}
							public void fromData() {
							}
							public void convert(Data dat) {
							}
						};
						dat.add(sc);
						try {
						}
						catch(DisconnectException dx) {
							dx.printStackTrace();
						}
					}
				}
			}
		}
		catch(DisconnectException dx) {
			dx.printStackTrace();
		}
	}
	
	public void terminate(SwiftNetTool t) {
		scanning = false;
		System.out.println("Client close");
		System.exit(1000);
	}
}
