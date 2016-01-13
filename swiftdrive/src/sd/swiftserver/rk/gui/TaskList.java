package sd.swiftserver.rk.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.type.tasks.Task;
import sd.swiftglobal.rk.type.tasks.TaskHandler;

/* This file is part of Swift Drive *
 * Copyright (c) 2015 Ryan Kerr     */

/**
 * <b>Task Menu:</b><br>
 * Provides a list of available tasks, the ability to add or remove tasks, and
 * a panel for <b>FullTaskControllers</b> to be displayed on
 *
 * @author Ryan Kerr
 */
public class TaskList extends VBox {

	private TaskHandler tasks;
	private FullTaskController fullctrl;
	private ServerInterface parent;

	@FXML private Pane misc_pnl;
	@FXML private VBox task_pnl;
	@FXML private VBox list_pnl;
	@FXML private Button ntsk_btn;

	/**
	 * <b>Constructor:</b><br>
	 * Builds the GUI by loading the FXML file
	 */
	public TaskList() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/Server/TaskList.fxml"));
		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load();
			tasks = new TaskHandler(false);
		}
		catch(IOException ix) {
			ix.printStackTrace();
		}
		catch(FileException fx) {
			fx.printStackTrace();
		}

	}

	/**
	 * <b>Hide All Panels:</b><br>
	 * Hides all the panels on screen
	 */
	public void hideAll() {
		misc_pnl.setVisible(false);
		list_pnl.setVisible(false);
		ntsk_btn.setVisible(false);
	}

	/**
	 * <b>Reload Tasks:</b><br>
	 * Reload the task index from file, clear all the information already on
	 * screen, and then dynamically load the <b>TaskController</b> for each
	 * task found in the file.
	 */
	public void reloadTasks() {
		try {
			tasks = new TaskHandler(false);
		}
		catch(FileException fx) {
			fx.printStackTrace();
		}

		if(tasks != null) {
			list_pnl.getChildren().clear();

			for(Task t : tasks.getArray()) {
				TaskController tctrl = new TaskController();
				tctrl.setTask(t);
				tctrl.setParent(parent);
				list_pnl.getChildren().add(tctrl);
			}
		}
	}

	/**
	 * <b>Show Task List:</b><br>
	 * Show the list of tasks on screen
	 */
	public void showList() {
		hideAll();
		ntsk_btn.setVisible(true);
		list_pnl.setVisible(true);
	}

	/**
	 * <b>Hide the full task controller:</b><br>
	 * Hides the current FullTaskController and replaces it with the list view 
	 */
	public void hideFullController() {
		showList();
		fullctrl = null;
	}
	
	/**
	 * <b>Create New Task:</b><br>
	 * Creates a new task (Name: "New Task", Description: "This is a new task")
	 * and displays it in the task list.
	 */
	public void createTask() {
		try {
			Task ntsk = new Task("New Task", "This is a new task");
			if(tasks.add(ntsk)) expandTask(ntsk);
		}
		catch(FileException ix) {

		}
	}

	/**
	 * <b>Open / Expand Task:</b><br>
	 * Create a new <b>FullTaskController</b> for the given task and display
	 * the controller on screen using the blank panel.
	 */
	public void expandTask(Task t) {
		fullctrl = new FullTaskController();
		fullctrl.setParent(parent);
		fullctrl.setTask(t);
		misc_pnl.getChildren().clear();
		misc_pnl.getChildren().add(fullctrl);
		hideAll();
		misc_pnl.setVisible(true);
	}

	/**
	 * <b>Delete Task:</b><br>
	 * Remove the given task from the <b>TaskHandler</b>, reload, and display.
	 */
	public void deleteTask(Task t) {
		try {
			tasks.remove(t);
		}
		catch(FileException fx) {

		}
		
		reloadTasks();
		showList();
	}

	/** @return The Task Index used in the controller **/
	public TaskHandler getTaskHandler() {
		return tasks;
	}

	/** @param srv with references to all other required components **/
	public void setParent(ServerInterface srv) {
		parent = srv;
	}
}
