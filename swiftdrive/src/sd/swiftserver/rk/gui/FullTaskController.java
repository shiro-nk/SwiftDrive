package sd.swiftserver.rk.gui;

import java.io.IOException;
import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.type.tasks.SubTask;
import sd.swiftglobal.rk.type.tasks.Task;

/* This file is part of Swift Drive *
 * Copyright (c) 2015 Ryan Kerr     */

public class FullTaskController extends VBox implements Settings {

	private Task task;
	private ServerInterface parent;
	private SubTaskController[] sctrl;

	@FXML private Accordion fold_acd;

	@FXML private ProgressBar prog_bar;

	@FXML private TextArea desc_fld;
	
	@FXML private TextField stsk_fld;
	@FXML private TextField name_fld;

	public FullTaskController() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ServerTaskFull.fxml"));
		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load();
		}
		catch(IOException ix) {

		}
	}

	public void hideAll() {

	}

	public void createSubtask() {
		if(!stsk_fld.getText().trim().equals("") && task.get(stsk_fld.getText()) == null) {
			try {
				task.add(new SubTask(stsk_fld.getText().trim(), "", "", DATE_FORM.format(LocalDate.now()), DATE_FORM.format(LocalDate.now()), 0, 1));
				refresh();
			}
			catch(FileException fx) {

			}
			stsk_fld.setText("");
		}
	}

	public void save() {
		try {
			SubTask[] subtasks = new SubTask[sctrl.length];

			int i = 0; for(SubTaskController s : sctrl) subtasks[i++] = s.getSubtask();
			task.setList(subtasks);
			task.write();
		}
		catch(FileException fx) {
			fx.printStackTrace();
		}

		refresh();
	}

	public void setTask(Task t) {
		task = t;
		refresh();
	}

	public void deleteSubtask(SubTask s) {
		try {
			task.remove(s);
			refresh();
		}
		catch(FileException fx) {
			
		}
	}

	public void delete() {
		parent.getTasklist().deleteTask(task);
	}

	public void refresh() {
		name_fld.setText(task.getName());
		desc_fld.setText(task.getDesc());
		prog_bar.setProgress(task.getPercent());
		fold_acd.getPanes().clear();
		sctrl = new SubTaskController[task.getArray().length];

		int i = 0;
		for(SubTask s : task.getArray()) {
			sctrl[i] = new SubTaskController();
			sctrl[i].setFullController(this);
			sctrl[i].setSubtask(s);
			fold_acd.getPanes().add(sctrl[i++]);
		}
	}

	public void setParent(ServerInterface srv) {
		parent = srv;
	}

	public ServerInterface getServerInterface() {
		return parent;
	}
}
