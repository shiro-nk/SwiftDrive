package sd.swiftserver;

import java.io.File;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.DisconnectException;
import sd.swiftserver.rk.gui.ServerMenu;
import sd.swiftserver.rk.net.Server;

public class SwiftServer extends Application implements Settings {
	public static void main(String[] args) {
		println("\n");
		println("Welcome to Swift Drive!");
		println("Version " + VER_VER + " Release " + VER_REL + " Patch " + VER_MIN);
		
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
	private ServerMenu menu;
	private Server server;

	@Override
	public void start(Stage stage) throws Exception {
		root = FXMLLoader.load(getClass().getResource("/gui/ServerMenu.fxml"));
		scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Swift Server");
		stage.show();
	}

	public void startServer(int port) throws DisconnectException {
		server = new Server(port);
	}
}
