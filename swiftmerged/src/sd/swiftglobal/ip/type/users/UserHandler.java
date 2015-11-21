package sd.swiftglobal.ip.type.users;
import java.io.File;
import java.util.ArrayList;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.util.SwiftFront;


public class UserHandler implements Settings {
// LC_PATH + "sample.txt"
	//LC_PATH + "Task" + LC_DIV
	private SwiftFront source;
	ArrayList<User> userlist = new ArrayList<User>();
	
	public UserHandler(){
		try {
			source = new SwiftFront(new File(LC_PATH + "Users.txt"), false);
			readUsers();
		}
		catch (FileException e){
			e.printStackTrace();
		}
	}
	
	private void readUsers(){
		try {
			source.read();
			userlist.clear();
			source.fromData();
			for(String s : source.getArray()) {             
			    userlist.add(new User(s));
			    System.out.println(s);
			}
			source.getArray();
		} catch (FileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}
