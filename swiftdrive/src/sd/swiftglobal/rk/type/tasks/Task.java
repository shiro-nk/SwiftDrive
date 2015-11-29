package sd.swiftglobal.rk.type.tasks;

import java.io.File;
import java.util.ArrayList;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.type.handler.HandleType;
import sd.swiftglobal.rk.type.handler.Handler;
import sd.swiftglobal.rk.type.users.User;
import sd.swiftglobal.rk.type.users.UserHandler;
import sd.swiftglobal.rk.util.Logging;
import sd.swiftglobal.rk.util.SwiftFront;

/* This file is part of Swift Drive                * 
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

public class Task extends Handler<SubTask> implements HandleType, Settings, Logging {
	private File path;
	private String rawdata = "";
	private String name,
				   desc,
				   lead;
	private int id;
	private User user;
	private UserHandler userlist;

	public Task(int id, String rawdata, UserHandler userlist) {
		this.userlist = userlist;
		this.id = id;
		this.rawdata = rawdata;
		toData();
	}

	public Task(String name, UserHandler userlist) throws FileException {
		this.userlist = userlist;
		this.name = name;
		reloadPath();
		read();
	}

	public Task(String name, String desc, String lead, UserHandler userlist) throws FileException {
		this.name = name;
		this.desc = desc;
		this.lead = lead;
		this.user = findUser();
		this.userlist = userlist;
		reloadPath();
		read();
	}

	public Task(String name, String desc, User user, SubTask[] subtasks, UserHandler userlist) throws FileException {
		this.name = name;
		this.desc = desc;
		this.user = user;
		this.lead = user.getUsername();
		this.userlist = userlist;
		setList(subtasks);
		reloadPath();
	}

	protected void convert(String[] data) {
		ArrayList<SubTask> list = getList();
		list.clear();
		
		for(int i = 0; i < data.length; i++) {
			SubTask subtask = new SubTask(data[i]);
			subtask.setInfo(i, data[i]);
			list.add(subtask);
		}
	}

	public SubTask[] getArray() {
		return getList().toArray(new SubTask[getList().size()]);
	}

	private User findUser() {
		return userlist.get(lead);
	}

	private void reloadPath() {
		path = new File(LC_TASK + (!name.equals("") && name != null ? name : "temp") + ".stl");
		setSource(new SwiftFront(path));
	}
	
	public void echo(Object o, int level) {
		print("[ Tasks  ] " + o.toString() + "\n", level);
	}

	@Override
	public int getID() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setID(int id) {
		this.id = id;
	}

	@Override
	public void toData() {
		String[] split = rawdata.split(";");
		if(split.length == 3) {
			name = split[0];
			desc = split[1];
			lead = split[2];
			user = findUser();
			reloadPath();
		}
	}

	@Override
	public String toString() {
		String d = ";";
		rawdata = name + d + desc + d + lead;
		return rawdata;
	}

	@Override
	public void setInfo(int id, String data) {
		setID(id);
		rawdata = data;
		toData();
	}

	public void setDescription(String desc) {
		this.desc = desc;
	}

	public String getDescription() {
		return desc;
	}

	public void setLead(User lead) {
		user = lead;
	}

	public User getLead() {
		return user;
	}

	public int getPercent() {
		int average = 0,
			task = 0;

		for(SubTask s : getList()) { 
			if(s.getStatus() != SubTask.TASK_CANCELLED) {
				if(s.getStatus() == SubTask.TASK_COMPLETE) average++;
				task++;
			}
		}

		return average / task;
	}
}
