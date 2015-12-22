package sd.swiftglobal.rk.type.users;

import java.io.File;
import java.util.ArrayList;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.type.handler.Handler;
import sd.swiftglobal.rk.util.Logging;
import sd.swiftglobal.rk.util.SwiftFront;

/* This file is part of Swift Drive                * 
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

public class UserHandler extends Handler<User> implements Settings, Logging {

	public UserHandler() throws FileException {
		File path = new File(LC_PATH + "users");
		if(path.exists() && path.isFile()) {
			setSource(new SwiftFront(path, false));
			read();
		}
		else if(!path.exists()) {
			setSource(new SwiftFront(path, false));
			add(new User("default", "username", "password"));
		}

		try(SwiftFront file = new SwiftFront(new File(LC_PATH + "users_public"))) {
			for(User u : getArray()) file.add(u.getRealname() + ";" + u.getUsername() + "\n");
			file.toData();
			file.write();
		}
		catch(FileException fx) {
			fx.printStackTrace();
		}
	}

	public User getByName(String realname) {
		int index = -1;
		User[] ar = getArray();
		for(int i = 0; i < ar.length; i++) {
			if(ar[i].getRealname().equals(realname)) index = i;
		}
		return 0 <= index ? ar[index] : null;
	}

	public void echo(Object o, int level) {
		print("[ Users  ] " + o.toString() + "\n", level);
	}

	@Override
	public void convert(String[] data) {
		ArrayList<User> list = getList();
		list.clear();
		
		for(int i = 0; i < data.length; i++) {
			User user = new User(data[i]);
			user.setID(i);
			list.add(user);
		}
	}

	@Override
	public User[] getArray() {
		return getList().toArray(new User[getList().size()]);
	}

	@Override
	public void reload() throws FileException {
		if(getSource().exists() && getSource().getFile().isFile()) read();
	}
}
