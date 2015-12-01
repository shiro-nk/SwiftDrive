package sd.swiftglobal.rk.type.tasks;

import java.io.File;
import java.util.ArrayList;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.type.handler.Handler;
import sd.swiftglobal.rk.type.users.UserHandler;
import sd.swiftglobal.rk.util.Logging;
import sd.swiftglobal.rk.util.SwiftFront;

/* This file is part of Swift Drive			  
 * Copyright (c) 2015 Ryan Kerr				  *
 * Please refer to <http://gnu.org/licenses/> */

public class TaskHandler extends Handler<Task> implements Settings, Logging {
	private UserHandler userlist;

	public TaskHandler(UserHandler userlist) throws FileException {
		File path = new File(LC_TASK + "index");
		this.userlist = userlist;
		if(path.exists() && path.isFile()) {
			setSource(new SwiftFront(path));
			read();
		}
		else if(!path.exists()) {
			setSource(new SwiftFront(path));
		}
	}

	@Override
	public void convert(String[] data) throws FileException {
		ArrayList<Task> list = getList();
		list.clear();
		System.out.println("Creating data from stl");
		for(int i = 0; i < data.length; i++) {
			Task task = new Task(i, data[i], userlist);
			list.add(task);
		}
	}

	@Override
	public Task[] getArray() {
		return getList().toArray(new Task[getList().size()]);
	}

	@Override
	public void remove(int id) throws FileException {
		if(0 <= id && id < getList().size()) {
			getList().get(id).getSource().getFile().delete();
			getList().remove(id);
			write();
		}
	}

	@Override
	public void reload() throws FileException {
		if(getSource().exists() && getSource().getFile().isFile()) read();
	}
}
