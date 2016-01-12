package sd.swiftserver.rk.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;

import sd.swiftglobal.rk.type.tasks.Task;

/* This file is part of Swift Drive *
 * Copyright (c) 2015 Ryan Kerr     */

/**
 * <b>Short Read-Only Subtask Controller</b><br>
 * Provides a short summary of a task and triggers the <b>Full Task Controller</b> 
 * to be shown with the corresponding task
 *
 * @author Ryan Kerr
 */
public class TaskController extends HBox {

	private Task task;
	private ServerInterface parent;

	@FXML private Label name_lbl;
	@FXML private ProgressBar prog_bar;

	/**
	 * <b>Constructor:</b><br>
	 * Builds the GUI.
	 */
	public TaskController() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/Server/Task.fxml"));
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
	 * <b>Open/Expand Task:</b><br>
	 * Opens the given task in the full task view.
	 */
	public void expand() {
		parent.getTasklist().expandTask(task);
	}

	/** @param srv The server interface with references to the tasklist **/ 
	public void setParent(ServerInterface srv) {
		parent = srv;
	}

	/**
	 * <b>Set Task:</b><br> 
	 * Display the given task
	 *
	 * @param t The task to be shown
	 */
	public void setTask(Task t) {
		task = t;
		name_lbl.setText(task.getName());
		prog_bar.setProgress(task.getPercent());
	}
}
