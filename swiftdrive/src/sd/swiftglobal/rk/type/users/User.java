package sd.swiftglobal.rk.type.users;

public class User {
	private String rawdata,
				   realname,
		   		   username,
		   		   password;

	private int userid;

	public User(String real, String user, String pass, int id) {
		realname = real;
		username = user;
		password = pass;
		userid   = id;
	}
	
	public User(String real, String username, int id) {
		this(real, username, "", id);
	}

	public User(String rawdata) {
		this.rawdata = rawdata;
		toData();
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public byte[] getPassword() {
		return password.getBytes();
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getID() {
		return userid;
	}

	public void setID(int id) {
		userid = id;
	}

	public void toData() {
		String[] split = rawdata.split(";");
		
		if(split.length == 4) {
			userid   = Integer.parseInt(split[0]);
			realname = split[1];
			username = split[2];
			password = split[3];
		}
	}

	public String toString() {
		return userid + ";" + realname + ";" + username + ";" + password;
	}
}
