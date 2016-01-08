package sd.swiftserver;

import java.io.File;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import sd.swiftglobal.rk.Settings;

/**
 * <b>Start SwiftServer:</b><br>
 * Starts the SwiftServer Application
 *
 * @author Ryan Kerr
 */
public class SwiftServer extends Application implements Settings {
	public static void main(String[] args) {
		println("\n");
		println("Welcome to Swift Drive!");
		println("Version " + VER_MAJOR + " Release " + VER_MINOR + " Patch " + VER_PATCH);
		
		File file = new File("LC_PATH");
		if(!file.exists()) {
			println("Creating " + LC_PATH);
		}
		else if(file.exists() && file.isDirectory()) {
			println("Failed! A file named swift exists in the home folder.");
			System.exit(1);
		}
		Application.launch(SwiftServer.class, (String[]) null);
	}

	public static void println(String s) {
		System.out.println("[ Start  ] " + s);
	}

	private Parent root;
	private Scene scene;

	@Override
	public void start(Stage stage) throws Exception {
		root = FXMLLoader.load(getClass().getResource("/gui/Server/Server.fxml"));
		scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Swift Server");
		stage.setMinWidth(800);
		stage.setMinHeight(450);
		stage.setMaxWidth(1000);
		stage.setMaxHeight(600);
		stage.setResizable(false);
		stage.getIcons().add(new Image("/res/icon.png"));
		stage.show();

		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent we) {
				Platform.exit();
				System.exit(0);
			}
		});
	}
}
