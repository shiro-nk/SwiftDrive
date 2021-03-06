package sd.swiftglobal.rk.type.tasks;

import java.io.File;
import java.util.ArrayList;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.type.handler.Handler;
import sd.swiftglobal.rk.util.Logging;
import sd.swiftglobal.rk.util.SwiftFront;

/* This file is part of Swift Drive			  
 * Copyright (c) 2015 Ryan Kerr				  *
 * Please refer to <http://gnu.org/licenses/> */

/**
 * <b>Task Handler:</b><br>
 * Handler for the indexing of tasks (which index subtasks)
 *
 * @author Ryan Kerr
 */
public class TaskHandler extends Handler<Task> implements Settings, Logging {

	/**
	 * <b>Constructor:</b><br>
	 * Sets source to LC_TASK/index and reads the index if it exists.
	 *
	 * @throws FileException in the event the file couldn't be read
	 */
	public TaskHandler(boolean isClient) throws FileException {
		File path = new File(LC_TASK + (isClient ? "public_" : "") + "index");
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
		for(int i = 0; i < data.length; i++) {
			Task task = new Task(i, data[i]);
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
