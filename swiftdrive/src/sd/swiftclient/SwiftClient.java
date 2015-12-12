package sd.swiftclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SwiftClient extends Application {
	public static void main(String[] args) {
		Application.launch(SwiftClient.class, (String[]) null);
	}

	private Parent root;
	private Scene scene;

	public void start(Stage stage) throws Exception {
		root = FXMLLoader.load(getClass().getResource("/gui/Client.fxml"));
		scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Swift Client");
		stage.setMinWidth(800);
		stage.setMinHeight(450);
		stage.setMaxWidth(1000);
		stage.setMaxHeight(600);
		stage.show();
	}
}
