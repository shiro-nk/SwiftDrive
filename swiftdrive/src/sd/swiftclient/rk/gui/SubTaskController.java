package sd.swiftclient.rk.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.paint.Color;

import sd.swiftglobal.rk.type.tasks.SubTask;
import sd.swiftglobal.rk.type.users.User;

/* This file is part of Swift Drive *
 * Copyright (C) 2015 Ryan Kerr 	*/

/**
 * <b>Read + Partial Write Subtask Controller</b><br>
 * Allows for the conditional manipulation of a subtask.
 */
public class SubTaskController extends TitledPane {

	private SubTask subtask;
	private FullTaskController parent;
	@FXML private Label desc_lbl;
	@FXML private Label lead_lbl;
	@FXML private Label stat_lbl;
	@FXML private Label prio_lbl;
	@FXML private Label start_lbl;
	@FXML private Label finish_lbl;

	@FXML private CheckBox ignore;
	@FXML private CheckBox complete;

	/**
	 * <b>Constructor</b><br>
	 * Create GUI controller from fxml file
	 */
	public SubTaskController() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/Client/SubTask.fxml"));
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
	 * <b>Set Subtask</b><br>
	 * Set subtask to be displayed along with its parent 
	 *
	 * @param s Subtask to be displayed
	 * @param c Controller to which this subtask controller belongs to
	 */
	public void setSubtask(SubTask s, FullTaskController c) {
		parent = c;
		subtask = s;
		setText(subtask.getName());
		refresh(false);
	}

	/**
	 * <b>Update Subtask Information</b><br>
	 * @see setSubtask(SubTask, FullTaskController)
	 * @param s Subtask to update
	 */
	public void updateSubtask(SubTask s) {
		setSubtask(s);
	}

	/**
	 * <b>Set Status to Ignore</b><br>
	 * Marks the given subtask as dropped / ignored if the checkbox isn't
	 * selected. If the checkbox is selected, the status is marked as pending.
	 */
	public void ignore() {
		if(ignore.isSelected())	subtask.setStatus(SubTask.TASK_CANCELLED);
		else subtask.setStatus(SubTask.TASK_PENDING);
		refresh(true);
	}

	/**
	 * <b>Set Status to Complete</b><br>
	 * Marks the given subtask as completed if the checkbox isn't selected. 
	 * If the checkbox is selected, the status is marked as pending.
	 */
	public void complete() {
		if(complete.isSelected()) subtask.setStatus(SubTask.TASK_COMPLETE);
		else subtask.setStatus(SubTask.TASK_PENDING);
		refresh(true);
	}

	/**
	 * <b>Refresh Subtask Information</b><br>
	 * This function displays all the information given by the subtask to the
	 * screen. The checkboxes are enabled and disabled depending on the status
	 * of the subtask. For example, if the status is COMPLETE, the checbox
	 * "complete" will be checked and the "ignore" checkbox will be disabled
	 * (and unchecked). <br>
	 *
	 * This function also disables both checkboxes in the event that the task
	 * lead user is not the currently logged in user. Subtask statuses may
	 * only be modified by the user who is in charge of that subtask
	 *
	 * @param update If true, synchronizes information with the server
	 */
	public void refresh(boolean update) {
		parent.refresh(update, subtask);

		desc_lbl.setText(" " + subtask.getDesc());
		lead_lbl.setText(subtask.getLead());
		stat_lbl.setText(subtask.getStatus() + "");
		start_lbl.setText(subtask.getStartDate());
		finish_lbl.setText(subtask.getFinishDate());

		int p = subtask.getPriority();
		prio_lbl.setText(p==1?"Low":p==0?"Normal":"High");
		prio_lbl.setTextFill(Color.web(p==1?"#FF0000":p==0?"#0000FF":"#00FF00"));

		switch(subtask.getStatus()) {
			case 0:
				setText(subtask.getName() + " [Completed]");
				ignore.setDisable(true);
				ignore.setSelected(false);
				complete.setDisable(false);
				complete.setSelected(true);
				stat_lbl.setTextFill(Color.web("#00FF00"));
				stat_lbl.setText("Complete");
				break;
			case 1:
				setText(subtask.getName() + " [Pending]");
				ignore.setDisable(false);
				ignore.setSelected(false);
				complete.setDisable(false);
				complete.setSelected(false);
				stat_lbl.setTextFill(Color.web("#0000FF"));
				stat_lbl.setText("Pending");
				break;
			case 2:
				setText(subtask.getName() + " [Dropped]");
				ignore.setDisable(false);
				ignore.setSelected(true);
				complete.setDisable(true);
				complete.setSelected(false);
				stat_lbl.setTextFill(Color.web("#FF0000"));
				stat_lbl.setText("Dropped");
				break;
		}

		String name = subtask.getLead();
		User subtaskuser = parent.getClientInterface().getUserlist().getByName(name);
		User clientuser = parent.getClientInterface().getClient().getUser();
		
		if(subtaskuser != null && clientuser != null && !subtaskuser.getName().equals(clientuser.getName())) {
			complete.setDisable(true);
			ignore.setDisable(true);
		}
	}
}
