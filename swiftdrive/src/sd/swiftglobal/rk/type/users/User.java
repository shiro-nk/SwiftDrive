package sd.swiftglobal.rk.type.users;

import sd.swiftglobal.rk.type.handler.HandleType;

/* This file is part of Swift Drive                * 
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

/**
 * <b>User:</b><br>
 * User information
 *
 * @author Ryan Kerr
 */
public class User implements HandleType {
	private String rawdata,
				   realname,
		   		   username,
		   		   password = "password";

	private int userid;

	/**
	 * <b>Constructor:</b><br>
	 * Create a server-side user (includes password)
	 *
	 * @param real The user's screen name or actual name
	 * @param user The user's username (log in name)
	 * @param pass The user's password
	 */
	public User(String real, String user, String pass) {
		realname = real;
		username = user;
		password = pass;
	}
	
	/**
	 * <b>Constructor:</b><br>
	 * Create a user based on its screen name and username
	 *
	 * @param real The user's screen name
	 * @param user The user's username
	 */
	public User(String real, String username) {
		this(real, username, "password");
	}

	/**
	 * <b>Constructor:</b><br>
	 * Create a user from raw file data
	 *
	 * @param rawdata Filedata to be converted
	 */
	public User(String rawdata) {
		this.rawdata = rawdata;
		toData();
	}

	/**
	 * <b>Get Real Name:</b><br>
	 * Get the user's screen name.
	 *
	 * @return Screen name
	 */
	public String getRealname() {
		return realname;
	}

	/**
	 * <b>Set Real Name:</b><br>
	 * Set the screen name of the user
	 *
	 * @param realname The user's real name
	 */
	public void setRealname(String realname) {
		this.realname = realname;
	}

	/**
	 * <b>Get Username:</b><br>
	 * @return User's username / primary string identifier
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * <b>Set Username:</b><br>
	 * @param username New username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * <b>Get Password:</b><br>
	 * @return Byte array of password characters
	 */
	public byte[] getPassword() {
		return password.getBytes();
	}

	/**
	 * <b>Set Password:</b><br>
	 * Set the password for the user.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public int getID() {
		return userid;
	}

	@Override
	public void setID(int id) {
		userid = id;
	}

	@Override
	public void toData() {
		String[] split = rawdata.split(";");
		
		if(split.length == 4) {
			userid   = Integer.parseInt(split[0]);
			realname = split[1];
			username = split[2];
			password = split[3];
		}
		else if(split.length == 2) {
			realname = split[0];
			username = split[1];
			password = "";
			userid   = 0;
		}
	}

	@Override
	public String toString() {
		return userid + ";" + realname + ";" + username + ";" + password;
	}

	@Override
	public String getName() {
		return getUsername();
	}

	@Override
	public void setInfo(int id, String data) {
		setID(id);
		rawdata = data;
	}
}
