package sd.swiftserver.rk.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import sd.swiftglobal.rk.type.tasks.Task;

public class FullTaskController extends VBox {

	private Task task;
	private ServerInterface parent;

	@FXML private Accordion fold_acd;

	@FXML private ProgressBar prog_bar;

	@FXML private TextArea desc_fld;
	
	@FXML private TextField stsk_fld;
	@FXML private TextField name_fld;

	public FullTaskController() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ServerTaskFull.fxml"));
		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load();
		}
		catch(IOException ix) {

		}
	}

	public void hideAll() {

	}

	public void createSubtask() {

	}

	public void save() {

	}

	public void setTask(Task t) {
		task = t;
		name_fld.setText(t.getName());
		desc_fld.setText(t.getDesc());
		prog_bar.setProgress(t.getPercent());

		fold_acd.getChildren().clear();

		for(SubTask s : task.getArray()) {
		
		}
	}

	public void updateTask(Task t) {

	}

	public void setParent(ServerInterface srv) {
		parent = srv;
	}
}
