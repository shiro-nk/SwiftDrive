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

public class FullTaskController extends VBox {

	private Task task;
	private ClientInterface parent;

	private SubTaskController[] subtasks;

	@FXML private Label name_lbl;
	@FXML private ProgressBar prog_bar;
	@FXML private Text desc_fld;
	@FXML private Accordion fold_acd;

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

	public void updateTask(Task t) {
		task = t;
		
		if(t.getArray().length == subtasks.length) {
			for(int i = 0; i < subtasks.length; i++) {
				subtasks[i].updateSubtask(t.getArray()[i]);
			}
		}
	}

	public Task getTask() {
		return task;
	}

	public void setClientInterface(ClientInterface c) {
		parent = c;
	}

	public ClientInterface getClientInterface() {
		return parent;
	}

	public void done() {
		parent.showList();
	}

	public void refresh(boolean update, SubTask subtask) {
		if(update) {
			parent.uploadSubtask(task, subtask);
			parent.updateTask(task);
		}

		prog_bar.setProgress(task.getPercent());
	}

	public void reload() {
		prog_bar.setProgress(task.getPercent());
		name_lbl.setText(task.getName());
		desc_fld.setText(task.getDesc());
	}
}
