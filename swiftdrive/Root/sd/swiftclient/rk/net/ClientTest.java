package sd.swiftclient.rk.net;

import java.util.Scanner;

import sd.swiftglobal.rk.expt.DisconnectException;

public class ClientTest {
	public static void main(String[] args) {
		try(Client c = new Client("localhost", 3141)) {
			//c.fileOut("input");
			
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
			dx.printStackTrace();
		}
	}
}
