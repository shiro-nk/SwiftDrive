package sd.swiftglobal.rk.type.users;

import java.io.File;
import java.util.ArrayList;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.util.SwiftFront;

/* This file is part of Swift Drive                * 
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

public class UserHandler implements Settings {
	private ArrayList<User> userlist = new ArrayList<User>();
	private SwiftFront source;

	public UserHandler() {
		try {
			source = new SwiftFront(new File(LC_PATH + "users"), false);
			readUsers();
		}
		catch(FileException fx) {
			fx.printStackTrace();
		}
	}

	public boolean addUser(User user) {
		boolean rtn = true;
		for(User u : userlist.toArray(new User[userlist.size()])) {
			if(u.getUsername().equals(user.getUsername())) rtn = false;
		}
			
		if(rtn) {
			userlist.add(user);
			writeUser();
		}

		return rtn;
	}

	public void removeUser(User user) {
		removeUser(user.getID());
	}

	public void removeUser(int id) {
		if(0 <= id && id < userlist.size()) userlist.remove(id);
		writeUser();
	}

	public int getIndex(String username) {
		for(int i = 0; i < userlist.size(); i++) {
			if(username.equals(userlist.get(i).getUsername())) return i;
		}
		return -1;
	}

	public User getUser(String username) {
		int index = getIndex(username);
		if(index < 0) return null;
		else return userlist.get(index);
	}
	
	private void readUsers() {
		try {
			source.read();
			userlist.clear();
			source.fromData();
			String[] ulist = source.getArray();
			User[]   list  = new User[ulist.length];
			for(int i = 0; i < list.length; i++) {
				list[i] = new User(ulist[i]);
				list[i].setID(i);
			}
			for(User u : list) userlist.add(u);
		}
		catch(FileException fx) {
		
		}
	}

	private void writeUser() {
		try {
			source.reset();
			for(User u : userlist.toArray(new User[userlist.size()]))
				source.add(u.toString());
			source.toData();
			source.write();
		}
		catch(FileException fx) {
			fx.printStackTrace();
		}
	}
}
