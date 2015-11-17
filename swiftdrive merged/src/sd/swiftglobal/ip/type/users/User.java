package sd.swiftglobal.ip.type.users;

import sd.swiftglobal.rk.Settings;

public class User implements Settings{

	private String rawdata, realname, username, password;
	private int userid;
	
	
	
	public User(String real, String user, String pass, int uid){
		realname = real;
		username = user;
		password = pass;
		userid = uid;
	}
	
	public User(String real, String user, int uid){
		//this("","",);
	}
	
	public User(String rawdat){
		rawdata = rawdat;
		toData();
	}
	
	public String getRealname(){
		return realname;
	}
	
	public String getUsername(){
		return username;
	}
	
	public byte[] getPassword(){
		return password.getBytes();
	}
	
	public int getUID(){
		return userid;
	}
	
	public String getRawdata(){
		return rawdata;
	}
	
	public String toString()
	{
		return userid + ";" + realname + ";" + username + ";" + password + ";";
	}
	
	public void toData(){
		String rawdata = getRawdata();
		//System.out.println(rawdata); //this is just going to be here while testing so it's optional
		String[] User = rawdata.split(";");
		String[] userDisplayText = {"UserID: ", "Real name: ", "Username: ", "Password: "};
		realname = User[1];
		username = User[2];
		password = User[3];
		userid = Integer.parseInt(User[0]);
		for(int i = 0; i < User.length; i++){
			System.out.println(userDisplayText[i] + User[i]);
		}
	}
	
	
}
