package sd.swiftserver.rk.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import sd.swiftglobal.rk.type.tasks.SubTask;

public class SubTaskController extends VBox {

	private SubTask s;

	@FXML private TextField desc_fld;

	@FXML private ChoiceBox stat_sel;
	@FXML private ChoiceBox lead_sel;
	@FXML private ChoiceBox prio_sel;
	
	@FXML private DatePicker start_dsl;
	@FXML private DatePicker finish_dsl;

	@FXML private CheckBox completed;
	@FXML private CheckBox ignore;

	public SubTaskController() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ServerSubTask.fxml"));
		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load();
		}
		catch(IOException ix) {

		}
	}

	public void setSubtask(SubTask s) {
		
	}
}
