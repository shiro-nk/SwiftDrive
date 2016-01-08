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

public class TaskController extends HBox {

	private Task task;
	private ServerInterface parent;

	@FXML private Label name_lbl;
	@FXML private ProgressBar prog_bar;

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

	public void expand() {
		parent.getTasklist().expandTask(task);
	}

	public void setParent(ServerInterface srv) {
		parent = srv;
	}

	public void setTask(Task t) {
		task = t;
		name_lbl.setText(task.getName());
		prog_bar.setProgress(task.getPercent());
	}
}
