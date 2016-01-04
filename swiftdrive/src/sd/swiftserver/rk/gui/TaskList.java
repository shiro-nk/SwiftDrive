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

public class TaskList extends VBox {

	private TaskHandler tasks;
	private FullTaskController fullctrl;
	private ServerInterface parent;

	@FXML private Pane misc_pnl;
	@FXML private VBox task_pnl;
	@FXML private VBox list_pnl;
	@FXML private Button ntsk_btn;

	public TaskList() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/TaskList.fxml"));
		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load();
			tasks = new TaskHandler();
		}
		catch(IOException ix) {
			ix.printStackTrace();
		}
		catch(FileException fx) {
			fx.printStackTrace();
		}

	}

	public void hideAll() {
		misc_pnl.setVisible(false);
		list_pnl.setVisible(false);
		ntsk_btn.setVisible(false);
	}

	public void reloadTasks() {
		System.out.println("Task: " + tasks + "; Parent: " + parent);

		try {
			tasks = new TaskHandler();
		}
		catch(FileException fx) {

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

	public void showList() {
		hideAll();
		ntsk_btn.setVisible(true);
		list_pnl.setVisible(true);
	}

	public void hideFullController() {
		hideAll();
		list_pnl.setVisible(true);
		ntsk_btn.setVisible(true);
	}
	
	public void createTask() {
		try {
			Task ntsk = new Task("New Task", "This is a new task");
			if(tasks.add(ntsk)) expandTask(ntsk);
		}
		catch(FileException ix) {

		}
	}

	public void expandTask(Task t) {
		fullctrl = new FullTaskController();
		fullctrl.setParent(parent);
		fullctrl.setTask(t);
		misc_pnl.getChildren().clear();
		misc_pnl.getChildren().add(fullctrl);
		hideAll();
		misc_pnl.setVisible(true);
	}

	public void deleteTask(Task t) {
		try {
			tasks.remove(t);
		}
		catch(FileException fx) {

		}
		
		reloadTasks();
		showList();
	}

	public TaskHandler getTaskHandler() {
		return tasks;
	}

	public void setParent(ServerInterface srv) {
		parent = srv;
	}
}
