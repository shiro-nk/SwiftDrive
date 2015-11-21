package sd.swiftglobal.ip.type.users;

import sd.swiftglobal.rk.Settings;

public class UserTester implements Settings {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		/*try {
			SwiftFile file = new SwiftFile(LC_PATH + "UserTester.txt", true);
			String[] data = file.getArray();
			for(String s : data) System.out.println(s);
		} catch (FileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(IOException ix) {
			
		}*/
	
	
		new UserHandler();
	}

}
