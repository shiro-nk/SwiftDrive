package sd.swiftclient.rk.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;

import sd.swiftglobal.rk.type.tasks.Task;
import sd.swiftglobal.rk.type.tasks.SubTask;

public class FullTaskController extends HBox {

	private Task task;
	@FXML private Label name_lbl;
	@FXML private ProgressBar prog_bar;
	@FXML private Button open_btn;

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

	public void setTask(Task t) {
		task = t;
		name_lbl.setText(task.getName());
		prog_bar.setProgress(task.getPercent());
	}
}
