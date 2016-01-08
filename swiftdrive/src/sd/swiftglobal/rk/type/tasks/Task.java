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

	public Task(int id, String rawdata) throws FileException {
		this.id = id;
		this.rawdata = rawdata;
		toData();
		reload();
	}

	public Task(String name) throws FileException {
		this.name = name;
		reload();
	}

	public Task(String name, String desc) throws FileException {
		this.name = name;
		this.desc = desc;
		reload();
	}

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
	 *
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}

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
