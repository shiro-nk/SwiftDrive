package sd.swiftglobal.rk.type.tasks;

import java.io.File;
import java.util.ArrayList;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.type.handler.HandleType;
import sd.swiftglobal.rk.type.handler.Handler;
import sd.swiftglobal.rk.util.Logging;
import sd.swiftglobal.rk.util.SwiftFront;

/* This file is part of Swift Drive                * 
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

/**
 * <b>Task and SubTask Handler:</b><br>
 * Handler compatible, subtask index. <br><br>
 *
 * <b>Note:</b> This class is both a Handler and HandleType to allow
 * the indexing of various tasks which further index subtasks.
 *
 * @author Ryan Kerr
 */
public class Task extends Handler<SubTask> implements HandleType, Settings, Logging {
	private File path;
	private String rawdata = "";
	private String name,
				   desc;
	private int id;

	/**
	 * <b>Constructor:</b><br>
	 * Create a new task with its index and filedata.
	 *
	 * @param id Array index
	 * @param rawdata Data read from file (or task.toString())
	 * @throws FileException if failed to read data from file
	 */
	public Task(int id, String rawdata) throws FileException {
		this.id = id;
		this.rawdata = rawdata;
		toData();
		reload();
	}

	/**
	 * <b>Constructor:</b>
	 * Creates and attempts to load a task based on its name. <br>
	 * 
	 * @param name String Identifier/Task Name
	 * @throws FileException if failed to read data from file
	 */
	public Task(String name) throws FileException {
		this.name = name;
		reload();
	}

	/**
	 * <b>Constructor:</b><br>
	 * Creates a new task with only a name and description. This
	 * will attempt to load information from a file if it exists.
	 *
	 * @param name String Identifier / Task Name
	 * @param desc Task Description
	 * @throws FileException if failed to read data from file
	 */
	public Task(String name, String desc) throws FileException {
		this.name = name;
		this.desc = desc;
		reload();
	}

	/**
	 * <b>Constructor:</b><br>
	 * Creates a new task using the given name, description, and
	 * subtasks.
	 *
	 * @param name String Identifier / Task Name
	 * @param desc Task Description
	 * @param subtasks Array of subtasks to be stored in task file
	 * @throws FileException is failed to read/write task file
	 */
	public Task(String name, String desc, SubTask[] subtasks) throws FileException {
		this.name = name;
		this.desc = desc;
		setList(subtasks);
		reload();
	}

	/**
	 * <b>Reload Path:</b><br>
	 * Changes the source file based on the string identifier (task name).
	 */
	private void reloadPath() {
		path = new File(LC_TASK + (!name.equals("") && name != null ? name : "temp") + ".stl");
		setSource(new SwiftFront(path));
	}

	/**
	 * <b>Set Name:</b><br>
	 * Sets the name of the task. Since the file path is based on the
	 * task name, the old source file is deleted and written to a new
	 * source if the contents of the file are stored in memory. If
	 * the file contents are non-existant, the source file is changed
	 * with ease.
	 *
	 * <b>Warning:</b> This method does not check for name collissions.
	 *
	 * @param name New String Identifier/Task Name
	 * @throws FileException if failed to delete or write the file
	 */
	public void setName(String name) throws FileException {
		if(getSource().getByteFlag()) {
			getSource().getFile().delete();
			this.name = name;
			getSource().setFile(new File(LC_TASK + name + ".stl"), false);
			getSource().write();
			reloadPath();
		}
		else {
			this.name = name;
			reloadPath();
		}
	}

	/**
	 * <b>Set Task Description:</b><br>
	 * Sets the description of the task
	 * 
	 * @param desc The new description
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}


	/**
	 * <b>Get Task Description:</b><br>
	 * @return Task description
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * <b>Calculate %Complete:</b><br>
	 * Goes through each subtask in it's index and takes it's status.
	 * If the status is <i>Complete</i> 1 is added to the average and
	 * number of tasks. If the status is <i>Pending</i> 1 is added to
	 * the task, but not the average. If the status is <i>Cancelled</i>
	 * the subtask is ignored.
	 *
	 * @return Percent complete (as a decimal)
	 */
	public double getPercent() {
		int average = 0,
			task = 0;

		for(SubTask s : getList()) { 
			if(s.getStatus() != SubTask.TASK_CANCELLED) {
				if(s.getStatus() == SubTask.TASK_COMPLETE) average++;
				task++;
			}
		}
		
		return (double) average / (task != 0 ? task : 1);
	}

	@Override
	public void convert(String[] data) throws FileException {
		ArrayList<SubTask> list = getList();
		list.clear();
		
		for(int i = 0; i < data.length; i++) {
			SubTask subtask = new SubTask(data[i]);
			subtask.setInfo(i, data[i]);
			add(subtask);
		}
	}

	@Override
	public SubTask[] getArray() {
		return getList().toArray(new SubTask[getList().size()]);
	}

	@Override
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
		if(split.length == 2) {
			name = split[0];
			desc = split[1];
		}
		else {
			name = "temp";
			desc = "null";
		}

		reloadPath();
	}

	@Override
	public String toString() {
		String d = ";";
		rawdata = name + d + desc; // + d + lead;
		return rawdata;
	}

	@Override
	public void setInfo(int id, String data) {
		setID(id);
		rawdata = data;
		toData();
	}

	@Override
	public void reload() throws FileException {
		reloadPath();
		if(path.exists() && path.isFile()) read();
	}
}
