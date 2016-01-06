package sd.swiftserver.rk.gui;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.DisconnectException;
import sd.swiftglobal.rk.type.users.UserHandler;
import sd.swiftserver.rk.net.Server;

/* This file is part of Swift Drive 		   *
 * Copyright (c) 2015 Ryan Kerr				   *
 * Please refer to <http://gnu.org/licenses/>  */

public class ServerInterface implements Initializable, Settings {

	private Server server;
	private int port = DEF_PORT;
	private boolean active = false;

	@FXML private StackPane stack_pnl;

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

	private TaskList tasklist;
	private UserList userlist;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		vers_lbl.setText("Version " + VER_MAJOR + "." + VER_MINOR + "r" + VER_PATCH);
		
		tasklist = new TaskList();
		tasklist.setVisible(false);
		tasklist.setParent(this);	
		stack_pnl.getChildren().add(tasklist);
			
		userlist = new UserList();
		userlist.setParent(this);
		userlist.setVisible(false);
		stack_pnl.getChildren().add(userlist);

		Timer timer = new Timer();

		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				Platform.runLater(new Runnable() {
					public void run() {
						refresh();
					}
				});
			}
		}, 0, 1000);


		showOpen();
	}

	public void refresh() {
		if(ctrl_pnl.isVisible()) refreshInfo();
	}

	public void hideAll() {
		back_img.setOpacity(0.10);
		ctrl_pnl.setVisible(false);
		open_pnl.setVisible(false);
		help_pnl.setVisible(false);

		tasklist.setVisible(false);
		userlist.setVisible(false);
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

	public void showTasks() {
		hideAll();

		tasklist.reloadTasks();
		tasklist.showList();
		tasklist.setVisible(true);
	}

	public void showUsers() {
		hideAll();

		userlist.reload();
		userlist.setVisible(true);
	}

	public void refreshInfo() {
		if(server == null || (server.dead() && server.closing())) {
			stat_lbl.setText("Offline");
			stat_lbl.setTextFill(Color.web("#FF0000"));
			active = false;
			conn_lbl.setText("0");
		}
		else if(server.closing()) {
			stat_lbl.setText("Closing");
			stat_lbl.setTextFill(Color.web("#FF8040"));
			active = true;
			conn_lbl.setText(server.getNumConnections() + "");
		}
		else {
			stat_lbl.setText("Active");
			stat_lbl.setTextFill(Color.web("#00FF00"));
			conn_lbl.setText(server.getNumConnections() + "");
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
		refreshInfo();
	}

	public void startServer() {
		if(!active) {
			try {
				server = new Server(port);
				server.setTasklist(tasklist.getTaskHandler());
				server.setUserlist(userlist.getUserHandler());
				error_lbl.setText("");
				refreshInfo();
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
		refreshInfo();
	}

	public void haltServer() {
		if(server != null) server.destroy();
		refreshInfo();
	}

	public Server getServer() {
		return server;
	}

	public UserHandler getUserlist() {
		return userlist.getUserHandler();
	}

	public TaskList getTasklist() {
		return tasklist;
	}

	public void quit() {
		System.exit(1);
	}
}
