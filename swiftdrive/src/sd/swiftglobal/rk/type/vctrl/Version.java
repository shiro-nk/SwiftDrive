package sd.swiftglobal.rk.type.vctrl;

import java.io.File;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.type.handler.HandleType;
import sd.swiftglobal.rk.type.users.User;

/* This file is part of Swift Drive			  *
 * Copyright (c) 2015 Ryan Kerr				  *
 * Please refer to <http://gnu.org/licenses/> */

public class Version implements HandleType, Settings {
	private int id,
				major = 0,
				minor = 0,
				patch = 0;

	private String rawdata,
				   changes;

	private File file;
	private User[] userlist;

	public Version(String info) {
		rawdata = info;
		toData();
	}

	public Version(int major, int minor, int patch, String changes) {
		this.major = major;
		this.minor = minor;
		this.patch = patch;
		this.changes = changes;
	}

	private void reloadPath() {
		file = new File(LC_VERS + getName());
		if(file.exists() && file.isDirectory()) {
			//String[] users = file.listFiles();
			//for(int i = 0; i < users.length; i++) {
			//	if(!users[i].equals("master")) ;;
			//}
		}
	}

	private void appendUsers() {

	}

	@Override
	public int getID() {
		return id;
	}

	@Override
	public void setID(int id) {
		this.id = id;
	}

	@Override
	public void setInfo(int id, String data) {
		this.id = id;
		this.rawdata = data;
	}

	@Override
	public void toData() {
		String[] split = rawdata.split(";");

		if(split.length == 5) {
			major = Integer.parseInt(split[0]);
			minor = Integer.parseInt(split[1]);
			patch = Integer.parseInt(split[2]);
			changes = split[3];
		}
	}

	@Override
	public String getName() {
		return major + "." + minor + "r" + patch;
	}

	@Override
	public String toString() {
		return null;
	}

	public void setChanges(String changes) {
		this.changes = changes;
	}

	public String getChanges() {
		return changes;
	}
}
