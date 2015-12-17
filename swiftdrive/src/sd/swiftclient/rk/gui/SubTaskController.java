package sd.swiftclient.rk.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.paint.Color;

import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.type.tasks.SubTask;

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

	public SubTaskController() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ClientSubTask.fxml"));
		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load();
		}
		catch(IOException ix) {
			ix.printStackTrace();
		}
	}

	public void setSubtask(SubTask s, FullTaskController c) {
		parent = c;
		subtask = s;
		setText(subtask.getName());
		refresh(false);
	}

	public void ignore() {
		if(ignore.isSelected())	subtask.setStatus(SubTask.TASK_CANCELLED);
		else subtask.setStatus(SubTask.TASK_PENDING);
		refresh(true);
	}

	public void complete() {
		if(complete.isSelected()) subtask.setStatus(SubTask.TASK_COMPLETE);
		else subtask.setStatus(SubTask.TASK_PENDING);
		refresh(true);
	}

	public void refresh(boolean update) {
		parent.refresh(update, subtask);

		desc_lbl.setText(subtask.getDesc());
		lead_lbl.setText(subtask.getLead());
		stat_lbl.setText(subtask.getStatus() + "");

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
	}
}
