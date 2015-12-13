package sd.swiftclient.rk.gui;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;

public class TaskController extends HBox {
	public TaskController() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ClientTask.fxml"));
		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load();
		}
		catch(IOException ix) {
			ix.printStackTrace();
		}
	}
}
