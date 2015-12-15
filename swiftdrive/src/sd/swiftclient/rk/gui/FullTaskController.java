package sd.swiftclient.rk.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import sd.swiftglobal.rk.type.tasks.SubTask;
import sd.swiftglobal.rk.type.tasks.Task;

public class FullTaskController extends VBox {

	private Task task;
	private ClientInterface parent;
	@FXML private Label name_lbl;
	@FXML private ProgressBar prog_bar;
	@FXML private Button open_btn;
	@FXML private Text desc_fld;
	@FXML private Accordion fold_acd;

	public FullTaskController() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ClientTaskFull.fxml"));
		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load();
		}
		catch(IOException ix) {
			ix.printStackTrace();
		}
	}

	public void setTask(Task t, ClientInterface c) {
		parent = c;
		task = t;
		fold_acd.getPanes().clear();

		for(SubTask s : t.getArray()) {
			SubTaskController stc = new SubTaskController();
			stc.setSubtask(s, this);
			fold_acd.getPanes().add(stc);
		}
	}

	public void collapse() {

	}

	public Task getTask() {
		return task;
	}

	public void refresh(boolean update) {
		name_lbl.setText(task.getName());
		prog_bar.setProgress(task.getPercent());
		desc_fld.setText(task.getDesc());

		if(!update) parent.updateTask(task);
	}
}
