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

/**
 * <b>Server FX Controller:</b><br>
 * Provides an interface for manipulating the server and its information (tasks
 * and users). 
 *
 * @author Ryan Kerr
 */
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
	@FXML private VBox srry_pnl;

	@FXML private TextField port_fld;

	private TaskList tasklist;
	private UserList userlist;

	/**
	 * <b>Initialize:</b><br>
	 * Loads all the required panel components before displaying the screen.
	 * <br> This method also schedules the refresh() function to execute every
	 * 1 second.
	 */
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

	/**
	 * <b>Reload Server Information:</b><br>
	 * This method is called periodically in order to update information on
	 * the screen. <br><br>
	 * At the moment, this method will refresh the connection information as
	 * long as the connection control panel is visible.
	 */
	public void refresh() {
		if(ctrl_pnl.isVisible()) refreshInfo();
	}

	/**
	 * <b>Hide All Panels:</b><br>
	 * Hide all panels from screen, leaving a blank screen to the right of the
	 * menu panel. This method is called whenever another panel from the stack
	 * is about to be displayed or when the GUI is being reset to its default
	 * state.
	 */
	public void hideAll() {
		back_img.setOpacity(0.10);
		ctrl_pnl.setVisible(false);
		open_pnl.setVisible(false);
		srry_pnl.setVisible(false);

		tasklist.setVisible(false);
		userlist.setVisible(false);
	}

	/**
	 * <b>Show Connection Information:</b><br>
	 * Finds and displays all the host information (hostname, address, and port)
	 * on the control screen. If the host is not connected to the internet, the
	 * control panel will display a deafult of "localhost" and 127.0.0.1 as the
	 * hostname and address. <br><br>
	 *
	 * The reason address information is handled here rather than in the actual
	 * refreshInfo() function is because getting the ip address takes longer on
	 * some networks. Due to this variable timing, it can't be used in the
	 * periodic refreshing of the screen as it might cause the GUI to lock up.
	 */
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

	/**
	 * <b>Show Task Screen:</b><br>
	 * If the server is not active, this method will reload the tasklist and
	 * then display the resulting list to the screen. In the event that the
	 * server is running, the error screen will appear to prevent any changes
	 * to the content while clients are accessing.
	 */
	public void showTasks() {
		if((server != null && !active) || server == null) {
			hideAll();

			tasklist.reloadTasks();
			tasklist.showList();
			tasklist.setVisible(true);
		}
		else {
			hideAll();
			srry_pnl.setVisible(true);
		}
	}

	/**
	 * <b>Show User Screen:</b><br>
	 * This method will reload the userlist and then display the list to the
	 * screen as long as the server is offline. If the server is active, the
	 * error screen will appear to prevent the admin from making changes.
	 * Changes made while clients are still accessing it will cause some
	 * glitches either on the client or server and data will be lost.
	 */
	public void showUsers() {
		if((server != null && !active) || server == null) {
			hideAll();

			userlist.reload();
			userlist.setVisible(true);
		}
		else {
			hideAll();
			srry_pnl.setVisible(true);
		}
	}

	/**
	 * <b>Refresh Information:</b><br>
	 * This method takes the server status and stylizes it for output on the
	 * screen. Hostname and address information will not be updated here due to
	 * the uncertain load times. The information displayed includes server
	 * run status, number of connected clients, and port.
	 */
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

	/**
	 * <b>Show Welcome Message:</b><br>
	 * Shows the welcome message screen
	 */
	public void showOpen() {
		hideAll();
		open_pnl.setVisible(true);
		back_img.setVisible(true);
	}

	/**
	 * <b>Set the Host Connection Port:</b><br>
	 * This method takes information from the GUI to change the port to which
	 * clients can connect to. The port input must meet the following criteria
	 * (checked below): Not blank/null, must only contain numbers, and must
	 * be between 101 and 65535 (inclusive). If the port does not meet one or
	 * more of these criteria, the appropriate error message will be displayed
	 */
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

	/**
	 * <b>Initialize Server:</b><br>
	 * The following method will attempt initialize the method as long as it
	 * meets the following criteria: the server isn't already online, and there
	 * is at least 1 task configured in the task list. The reason for the task
	 * list requiring at least 1 task is to ensure the admin doesn't forget to
	 * configure the task, and to prevent the client from hanging while waiting
	 * for a non-existent task (a problem that occurred with older versions of
	 * SwiftDrive due to for loops starting at 0 inclusive)
	 */
	public void startServer() {
		if(!active) {
			if(0<tasklist.getTaskHandler().getArray().length) {
				try {
					server = new Server(port);
					server.setTasklist(tasklist.getTaskHandler());
					server.setUserlist(userlist.getUserHandler());
					error_lbl.setText("");
					refreshInfo();
				}
				catch(DisconnectException dx) {
					error_lbl.setText("Failed to start server: ");
				}
			}
			else {
				error_lbl.setText("You must have at least one task");
			}
		}
		else {
			error_lbl.setText("Server still running");
		}
	}
	
	/**
	 * <b>Stop Server:</b><br>
	 * Closes the server using the "nice" method. This method will call the
	 * server's "nice" shutdown method which waits for all the clients to
	 * disconnect before halting. For the alternative where the server responds
	 * immediately, use haltServer().
	 */
	public void stopServer() {
		if(server != null) server.close();
		refreshInfo();
	}

	/**
	 * <b>Halt Server:</b><br>
	 * Stop the server immediately. This method will disregard all clients and
	 * shutdown the server.
	 */
	public void haltServer() {
		if(server != null) server.destroy();
		refreshInfo();
	}

	/**
	 * <b>Get Server:</b><br>
	 * @return The server linked to this controller
	 */
	public Server getServer() {
		return server;
	}

	/**
	 * <b>Get User Handler:</b><br>
	 * TODO: Retractor this method! (Handler and List are not the same)
	 * @return The user handler used by the userlist controller
	 */
	public UserHandler getUserlist() {
		return userlist.getUserHandler();
	}

	/**
	 * <b>Get Task List Controller:</b><br>
	 * @return The tasklist controller used by the main controller 
	 */
	public TaskList getTasklist() {
		return tasklist;
	}

	/**
	 * <b>Quit</b><br>
	 * Wraps the System.exit(0) to a parameterless method for use with a
	 * button
	 */
	public void quit() {
		System.exit(0);
	}
}
