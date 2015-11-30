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
import javafx.scene.layout.BorderPane;

import sd.swiftserver.rk.net.Server;

/* This file is part of Swift Drive 		   *
 * Copyright (c) 2015 Ryan Kerr				   *
 * Please refer to <http://gnu.org/licenses/>  */

public class ServerMenu implements Initializable {

	private Server server;

	@FXML private Button info_btn;
	@FXML private Button ctrl_btn;
	@FXML private Button task_btn;
	@FXML private Button file_btn;
	@FXML private Button user_btn;
	@FXML private Button help_btn;
	@FXML private Button quit_btn;

	@FXML private Label ip_lbl;
	@FXML private Label port_lbl;

	@FXML private ImageView back_img;
	@FXML private BorderPane info_pnl;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

	}

	public void showInfo() {
		try {
			ip_lbl.setText(InetAddress.getLocalHost().toString());
		}
		catch(IOException ix) {
			ip_lbl.setText("127.0.0.1");
		}
		back_img.setVisible(false);
		info_pnl.setVisible(true);
	}

}
