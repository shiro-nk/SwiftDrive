package sd.swiftserver.rk.gui;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import sd.swiftglobal.rk.Settings;
import sd.swiftserver.rk.net.Server;

/* This file is part of Swift Drive 		   *
 * Copyright (c) 2015 Ryan Kerr				   *
 * Please refer to <http://gnu.org/licenses/>  */

public class ServerMenu implements Initializable, Settings {

	private Server server;

	@FXML private Button info_btn;
	@FXML private Button ctrl_btn;
	@FXML private Button task_btn;
	@FXML private Button file_btn;
	@FXML private Button user_btn;
	@FXML private Button help_btn;
	@FXML private Button quit_btn;

	@FXML private Label vers_lbl;
	@FXML private Label host_lbl;
	@FXML private Label addr_lbl;
	@FXML private Label port_lbl;
	@FXML private Label stat_lbl;
	@FXML private Label conn_lbl;

	@FXML private ImageView back_img;
	@FXML private VBox ctrl_pnl;
	@FXML private VBox open_pnl;
	@FXML private VBox help_pnl;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		vers_lbl.setText("Version " + VER_MAJOR + "." + VER_MINOR + "r" + VER_PATCH);
	}

	public void hideAll() {
		back_img.setOpacity(0.10);
		ctrl_pnl.setVisible(false);
		open_pnl.setVisible(false);
		help_pnl.setVisible(false);
	}

	public void showInfo() {
		hideAll();
		ctrl_pnl.setVisible(true);

		try {
			String[] info = InetAddress.getLocalHost().toString().split("/");
			
			if(info.length == 2) {
				host_lbl.setText(info[0]);
				addr_lbl.setText(info[1]);
				
				if(server == null) {
					stat_lbl.setText("Offline");
					stat_lbl.setTextFill(Color.web("#FF0000"));
				}
				else if(!server.closing()) {
					stat_lbl.setText("Active");
					stat_lbl.setTextFill(Color.web("#00FF00"));
				}
			}
		}
		catch(IOException ix) {
			host_lbl.setText("Localhost");
			addr_lbl.setText("127.0.0.1");
		}
	}

	public void showOpen() {
		hideAll();
		open_pnl.setVisible(true);
		back_img.setVisible(true);
	}

	public void quit() {
		System.exit(1000);
	}
}
