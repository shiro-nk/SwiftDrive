package sd.swiftclient;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/* This file is part of Swift Drive *
 * Copyright (C) 2015 Ryan Kerr     */

/**
 * <b>Start SwiftClient:</b><br>
 * Launches the client application
 *
 * @author Ryan Kerr
 */
public class SwiftClient extends Application {
	public static void main(String[] args) {
		Application.launch(SwiftClient.class, (String[]) null);
	}

	private Parent root;
	private Scene scene;

	@Override
	public void start(Stage stage) throws Exception {
		root = FXMLLoader.load(getClass().getResource("/gui/Client/Client.fxml"));
		scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Swift Client");
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
