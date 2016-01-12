package sd.swiftclient.rk.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import sd.swiftglobal.rk.type.tasks.SubTask;
import sd.swiftglobal.rk.type.tasks.Task;

/* This file is part of Swift Drive *
 * Copyright (C) 2015 Ryan Kerr		*/

/**
 * <b>Full Read-Only Task Controller</b><br>
 * Provides a method of reading task and displaying subtask information
 *
 * @author Ryan Kerr
 */
public class FullTaskController extends VBox {

	private Task task;
	private ClientInterface parent;

	private SubTaskController[] subtasks;

	@FXML private Label name_lbl;
	@FXML private ProgressBar prog_bar;
	@FXML private Text desc_fld;
	@FXML private Accordion fold_acd;

	/**
	 * <b>Initialize</b><br>
	 * Load fxml file into the controller
	 */
	public FullTaskController() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/Client/FullTask.fxml"));
		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load();
		}
		catch(IOException ix) {
			ix.printStackTrace();
		}
	}

	/**
	 * <b>Set Task</b><br>
	 * Set the task to be displayed by the controller to the given task.
	 * This method will also load all the task's subtasks
	 *
	 * @param t Task to use
	 */
	public void setTask(Task t) {
		task = t;
		fold_acd.getPanes().clear();
		subtasks = new SubTaskController[t.getArray().length];

		int c = 0;
		for(SubTask s : t.getArray()) {
			subtasks[c] = new SubTaskController();
			subtasks[c].setSubtask(s, this);
			fold_acd.getPanes().add(subtasks[c++]);
		}

		reload();
		refresh(false, null);
	}

	/**
	 * <b>Update Task Information:</b><br>
	 * Update all the subtask information without changing task information and
	 * reloading subtask information.
	 *
	 * @param t Task to use for update
	 */
	public void updateTask(Task t) {
		task = t;
		
		if(t.getArray().length == subtasks.length) {
			for(int i = 0; i < subtasks.length; i++) {
				subtasks[i].updateSubtask(t.getArray()[i]);
			}
		}
	}

	/** @return task on display **/
	public Task getTask() {
		return task;
	}

	/** @param c Set client interface to parent **/
	public void setClientInterface(ClientInterface c) {
		parent = c;
	}

	/** @return Client interface to which this controller belongs **/
	public ClientInterface getClientInterface() {
		return parent;
	}

	/** Return to menu **/
	public void done() {
		parent.showList();
	}

	/**
	 * <b>Refresh Subtask</b><br>
	 * Forward the request to the Client Interface for transfer with the server.
	 * Also changes the progress bar status
	 *
	 * @param update Sends information to server if true; modifies progress otherwise.
	 * 				 The reason update exists is to prevent the subtasks uploading and
	 * 				 downloading redundantly while they are being created by the
	 * 				 controller
	 * @param subtask subtask to be sent to the server
	 */
	public void refresh(boolean update, SubTask subtask) {
		if(update) {
			parent.uploadSubtask(task, subtask);
			parent.updateTask(task);
		}

		prog_bar.setProgress(task.getPercent());
	}

	/** Reload all information about the task (without updating subtasks) **/
	public void reload() {
		prog_bar.setProgress(task.getPercent());
		name_lbl.setText(task.getName());
		desc_fld.setText(task.getDesc());
	}
}
