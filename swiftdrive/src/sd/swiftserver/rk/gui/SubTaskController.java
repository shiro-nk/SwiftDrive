package sd.swiftserver.rk.gui;

import java.io.IOException;
import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.type.tasks.SubTask;
import sd.swiftglobal.rk.type.users.User;

/* This file is part of Swift Drive *
 * Copyright (c) 2015 Ryan Kerr     */

public class SubTaskController extends TitledPane implements Settings {

	private SubTask subtask;
	private FullTaskController parent;


	@FXML private TextField desc_fld;

	@FXML private ChoiceBox<String> stat_sel;
	@FXML private ChoiceBox<String> lead_sel;
	@FXML private ChoiceBox<String> prio_sel;
	
	@FXML private DatePicker start_dsl;
	@FXML private DatePicker finish_dsl;

	public SubTaskController() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ServerSubTask.fxml"));
		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load();

			stat_sel.getItems().addAll(new String[] {"Pending", "Completed", "Dropped"});
			prio_sel.getItems().addAll(new String[] {"Low", "Normal", "High"});
		}
		catch(IOException ix) {

		}
	}
	
	public void delete() {
		parent.deleteSubtask(subtask);
	}

	public void setSubtask(SubTask s) {
		subtask = s;

		setText(s.getName());
		desc_fld.setText(s.getDesc());
		lead_sel.setValue(s.getLead());
		stat_sel.setValue(s.getStatus() == 0 ? "Completed" : s.getStatus() == 1 ? "Pending" : "Dropped");
		prio_sel.setValue(s.getPriority() == 1 ? "Low" : s.getPriority() == 0 ? "Normal" : "High");

		start_dsl.setValue(LocalDate.parse(s.getStartDate(), DATE_FORM));
		finish_dsl.setValue(LocalDate.parse(s.getFinishDate(), DATE_FORM));

		lead_sel.getItems().clear();

		for(User u : parent.getServerInterface().getUserlist().getArray()) {
			lead_sel.getItems().addAll(u.getRealname());
		}
	}

	public SubTask getSubtask() {
		subtask.setDesc(desc_fld.getText());
		subtask.setLead(lead_sel.getValue());
		subtask.setStatus(stat_sel.getValue().equals("Completed") ? 0 : stat_sel.getValue().equals("Pending") ? 1 : 0);
		subtask.setPriority(prio_sel.getValue().equals("High") ? 2 : prio_sel.getValue().equals("Low") ? 1 : 0);
		subtask.setStartDate(DATE_FORM.format(start_dsl.getValue()));
		subtask.setFinishDate(DATE_FORM.format(finish_dsl.getValue()));
		return subtask;
	}

	public void setFullController(FullTaskController f) {
		parent = f;
	}
}
