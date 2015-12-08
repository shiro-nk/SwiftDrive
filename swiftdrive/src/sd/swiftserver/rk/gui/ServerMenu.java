package sd.swiftserver.rk.gui;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.DisconnectException;
import sd.swiftserver.rk.net.Server;

/* This file is part of Swift Drive 		   *
 * Copyright (c) 2015 Ryan Kerr				   *
 * Please refer to <http://gnu.org/licenses/>  */

public class ServerMenu implements Initializable, Settings {

	private Server server;
	private int port = DEF_PORT;
	private boolean active = false;

	@FXML private Button info_btn;
	@FXML private Button ctrl_btn;
	@FXML private Button task_btn;
	@FXML private Button file_btn;
	@FXML private Button user_btn;
	@FXML private Button help_btn;
	@FXML private Button quit_btn;

	@FXML private Button srvstop_btn;
	@FXML private Button srvhalt_btn;
	@FXML private Button srvapply_btn;
	@FXML private Button srvstart_btn;

	@FXML private Label vers_lbl;
	@FXML private Label host_lbl;
	@FXML private Label addr_lbl;
	@FXML private Label port_lbl;
	@FXML private Label stat_lbl;
	@FXML private Label conn_lbl;
	@FXML private Label error_lbl;

	@FXML private ImageView back_img;
	@FXML private VBox ctrl_pnl;
	@FXML private VBox open_pnl;
	@FXML private VBox help_pnl;

	@FXML private TextField port_fld;

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
			}

			refreshInfo();
		}
		catch(IOException ix) {
			host_lbl.setText("Localhost");
			addr_lbl.setText("127.0.0.1");
		}
	}

	public void refreshInfo() {
		if(server == null || (server.dead() && server.closing())) {
			stat_lbl.setText("Offline");
			stat_lbl.setTextFill(Color.web("#FF0000"));
			active = false;
		}
		else if(server.closing()) {
			stat_lbl.setText("Closing");
			stat_lbl.setTextFill(Color.web("#FF8040"));
			active = true;
		}
		else {
			stat_lbl.setText("Active");
			stat_lbl.setTextFill(Color.web("#00FF00"));
			active = true;
		}
		
		port_lbl.setText(port + "");
	}

	public void showOpen() {
		hideAll();
		open_pnl.setVisible(true);
		back_img.setVisible(true);
	}

	public void setPort() {
		String input = port_fld.getText(),
			   blank = input.replaceAll("[0-9]", "");

		if(!active) {
			if(!blank.equals("") || input.equals("")) {
				port = DEF_PORT;
				error_lbl.setText("Illegal Value");
			}
			else {
				port = Integer.parseInt(input);
				
				if(100 < port && port <= 65535) {
					error_lbl.setText("");
				}
				else {
					error_lbl.setText("Port number out of range!");
					port = DEF_PORT;
				}
			}
		}
		else {
			error_lbl.setText("Cannot change port while active");
		}

		port_fld.setPromptText(port + "");
		port_fld.setText("");
		showInfo();
	}

	public void startServer() {
		if(!active) {
			try {
				server = new Server(port);
				error_lbl.setText("");
				showInfo();
			}
			catch(DisconnectException dx) {
				error_lbl.setText("Failed to start server: " + dx.getMessage());
			}
		}
		else {
			error_lbl.setText("Server still running");
		}
	}
	
	public void stopServer() {
		if(server != null) server.close();
		showInfo();
	}

	public void haltServer() {
		server.destroy();
		showInfo();
	}

	public void quit() {
		System.exit(1);
	}
}
