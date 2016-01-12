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

/**
 * <b>Full Read-Write Task Controller:</b><br>
 * A controller similar to the Client's Full Read-Only Task Controller, however,
 * it provides the ability to modify the information present in tasks and does
 * not have the added requirement of having to reload the task and its subtasks
 * every few seconds.
 *
 * @author Ryan Kerr
 */
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

	/**
	 * <b>Constructor:</b><br>
	 * Loads the FullTaskController.fxml file into an object
	 */
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

	/**
	 * <b>Create New Subtask:</b><br>
	 * Create a new subtask with the name given in the stsk_fld (subtask field).
	 * This method will create append a new subtask to the task and task GUI as
	 * long as the subtask name given in stsk_fld only contains alphanumeric
	 * characters. In the event that the subtask name is not valid, an error
	 * message will be displayed at the bottom. In the event that the task file
	 * could not be opened for reading or writing, a different error will appear.
	 */
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

	/**
	 * <b>Save Task:</b><br>
	 * Saves the task the file. In the event that the task name (in name_fld) is
	 * invalid (contains non alphanumeric characters or spaces) an error will be
	 * displayed at the bottom of the screen. Similarity, if a task with that
	 * name already exists, the error will be shown.
	 */
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

	/**
	 * <b>Set Task:</b><br>
	 * Sets the task being shown to the given task.
	 * 
	 * @param t The task to be used
	 */
	public void setTask(Task t) {
		task = t;
		refresh();
	}

	/**
	 * <b>Delete Subtask:</b><br>
	 * Remove the specified subtask from the task list and reload the list.
	 * In the event that the file could not be saved, an error will be displayed
	 */
	public void deleteSubtask(SubTask s) {
		try {
			task.remove(s);
			refresh();
		}
		catch(FileException fx) {
			displayWarning("Failed to save file");
		}
	}

	/**
	 * <b>Delete Task:</b><br>
	 * Sends the delete request to the tasklist (tasklist controls the index of
	 * tasks)
	 */
	public void delete() {
		parent.getTasklist().deleteTask(task);
	}

	/**
	 * <b>Refresh / Reload:</b><br>
	 * Take all the task information and display it on the GUI. This method
	 * dynamically loads all the subtasks to this component to allow for the
	 * editing of the task's subtasks.
	 */
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

	/**
	 * <b>Suppress Messages:</b><br>
	 * Suppress all warning/error messages from the screen and display show
	 * the normal gui.
	 */
	public void suppressWarning() {
		serr_pnl.setVisible(false);
		stsk_pnl.setVisible(true);
	}

	/**
	 * <b>Display Messages:</b><br>
	 * Display the given message on screen in the place of the normal gui bar
	 * at the bottom. This error message can be dismissed by pressing the
	 * button located on the message bar.
	 *
	 * @param warning Warning/Error message to be displayed at the bottom
	 */
	public void displayWarning(String warning) {
		serr_lbl.setTextFill(Color.web("#FF0000"));
		serr_lbl.setText(warning);
		serr_pnl.setVisible(true);
		stsk_pnl.setVisible(false);
	}

	/** @param srv ServerInterface with references to all other components **/
	public void setParent(ServerInterface srv) {
		parent = srv;
	}

	/** @return The controlling server interface **/
	public ServerInterface getServerInterface() {
		return parent;
	}
}
