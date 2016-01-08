package sd.swiftserver.rk.gui;

import java.io.IOException;
import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

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

	@FXML private Label serr_lbl;

	@FXML private HBox stsk_pnl;
	@FXML private HBox serr_pnl;

	public FullTaskController() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/Server/FullTask.fxml"));
		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load();
			suppressWarning();
		}
		catch(IOException ix) {

		}
	}

	public void hideAll() {

	}

	public void createSubtask() {
		if(!stsk_fld.getText().trim().equals("") && task.get(stsk_fld.getText()) == null) {
			if(stsk_fld.getText().replaceAll("[A-Za-z0-9]", "").trim().equals("")) {
				try {
					String name = name_fld.getText(),
						   desc = desc_fld.getText();
	
					task.setName(name);
					task.setDesc(desc_fld.getText());
					task.add(new SubTask(stsk_fld.getText().trim(), "", "", DATE_FORM.format(LocalDate.now()), DATE_FORM.format(LocalDate.now()), 0, 1));
					refresh();
					name_fld.setText(name);
					desc_fld.setText(desc);
				}
				catch(FileException fx) {
					displayWarning("Failed to save file");
				}
				stsk_fld.setText("");
			}
			else {
				displayWarning("Name contains invalid characters (Valid: A-Z, a-z, 0-9)");
			}
		}
		else {
			displayWarning("Invalid name or subtask already exists. Please enter a valid name.");
		}
	}

	public void save() {
		try {
			Task t = parent.getTasklist().getTaskHandler().get(name_fld.getText().trim());
			if(t == null || t.getName().equals(task.getName())) {
				if(name_fld.getText().replaceAll("[A-Za-z0-9]", "").trim().equals("")) {
					SubTask[] subtasks = new SubTask[sctrl.length];

					task.setDesc(desc_fld.getText());
					task.setName(name_fld.getText().trim());
					parent.getTasklist().getTaskHandler().write();
					
					int i = 0; for(SubTaskController s : sctrl) subtasks[i++] = s.getSubtask();
					task.setList(subtasks);
					task.write();

					parent.getTasklist().reloadTasks();
					if(parent.getServer() != null) parent.getServer().setTasklist(parent.getTasklist().getTaskHandler());
					parent.getTasklist().showList();
				}
				else {
					displayWarning("Task name contains invalid characters (Valid: A-Z, a-z, 0-9)");
				}
			}
			else {
				displayWarning("A task with that name already exists!");
			}
		}
		catch(FileException fx) {
			displayWarning("Failed to save file");
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
			displayWarning("Failed to save file");
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

	public void suppressWarning() {
		serr_pnl.setVisible(false);
		stsk_pnl.setVisible(true);
	}

	public void displayWarning(String warning) {
		serr_lbl.setTextFill(Color.web("#FF0000"));
		serr_lbl.setText(warning);
		serr_pnl.setVisible(true);
		stsk_pnl.setVisible(false);
	}

	public void setParent(ServerInterface srv) {
		parent = srv;
	}

	public ServerInterface getServerInterface() {
		return parent;
	}
}
