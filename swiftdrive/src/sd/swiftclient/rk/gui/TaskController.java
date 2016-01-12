package sd.swiftclient.rk.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;

import sd.swiftglobal.rk.type.tasks.Task;

/* This file is part of Swift Drive *
 * Copyright (C) 2015 Ryan Kerr		*/

/**
 * <b>Read Only Task Controller</b><br>
 * Provides the ability to load tasks in more detail
 *
 * @author Ryan Kerr
 */
public class TaskController extends HBox {

	private Task task;
	private ClientInterface parent;

	@FXML private Label name_lbl;
	@FXML private ProgressBar prog_bar;

	/**
	 * <b>Constructor</b><br>
	 * Load the GUI from the Task.fxml file
	 */
	public TaskController() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/Client/Task.fxml"));
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
	 * <b>Set Parent</b><br>
	 * The Client interface reference for this controller is simply to provide
	 * access to the parent.expandTask() function.
	 *
	 * @param cli Client Interface to which the tasklist belongs to
	 */
	public void setParent(ClientInterface cli) {
		parent = cli;
	}

	/**
	 * <b>Set Task</b><br>
	 * @param t The task to be displayed
	 */
	public void setTask(Task t) {
		task = t;
		name_lbl.setText(task.getName());
		prog_bar.setProgress(task.getPercent());
	}

	/**
	 * <b>Expand Task to Full Task Controller</b><br>
	 * Forward the request to the main screen
	 */
	public void expand() {
		parent.expandTask(task);
	}
}
