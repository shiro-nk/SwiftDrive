package sd.swiftclient.rk.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import sd.swiftclient.rk.net.Client;
import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.DisconnectException;
import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.expt.SwiftException;
import sd.swiftglobal.rk.type.ServerCommand;
import sd.swiftglobal.rk.type.SwiftFile;
import sd.swiftglobal.rk.type.tasks.SubTask;
import sd.swiftglobal.rk.type.tasks.Task;
import sd.swiftglobal.rk.type.tasks.TaskHandler;
import sd.swiftglobal.rk.type.users.User;
import sd.swiftglobal.rk.type.users.UserHandler;
import sd.swiftglobal.rk.util.SwiftFront;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetContainer;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetTool;

/* This file is part of swift drive *
 * Copyright (c) 2015 Ryan Kerr     */

/**
 * <b>Client Front End GUI Controller:</b><br>
 * Provides the system with a working GUI front end. All Client menu
 * functions, such as switching tabs, are located here
 *
 * @author Ryan Kerr
 */
public class ClientInterface implements Settings, Initializable, SwiftNetContainer {

	private Timer timer;
	private Client client = null;
	private TaskHandler tasks = null;
	private UserHandler users = null;
	private FullTaskController fullctrl = new FullTaskController(); 
	private ClientInterface This = this;

	private Object lock = new Object();
	private boolean locked = false,
					logout = false,
					upload_stask = false,
					reload_task = false,
					update_task = false;
	private int cue;

	// Global Elements
	@FXML private ImageView back_img;

	// Menu Elements
	@FXML private Label vers_lbl;
	@FXML private SplitPane menu_pnl;
	@FXML private Button task_btn;

	// Welcome
	@FXML private VBox open_pnl;
	@FXML private Label user_lbl;
	@FXML private Label conn_lbl;

	// Login Elements
	@FXML private TextField user_fld;
	@FXML private TextField host_fld;
	@FXML private TextField port_fld;
	@FXML private PasswordField pass_fld;
	@FXML private Button conn_btn;
	@FXML private Label lgin_lbl;
	@FXML private VBox lgin_pnl;

	// Task Elements
	@FXML private VBox task_pnl;
	@FXML private VBox list_pnl;
	@FXML private Pane misc_pnl;
	@FXML private HBox prog_pnl;
	@FXML private ProgressIndicator prog_bar;

	private String host;
	private int port;

	/**
	 * <b>Initialize</b><br>
	 * Initialize the Client Interface
	 */
	public void initialize(URL a, ResourceBundle b) {
		hideAll();
		lgin_pnl.setVisible(true);
		menu_pnl.setVisible(false);
		prog_bar.setProgress(-5);
		
		fullctrl.setClientInterface(this);
	}

	/**
	 * <b>Login</b><br>
	 * Takes all the information from the login screen and checks to see the
	 * given connection information is valid/meets the criteria (port only
	 * contains numbers which reside between 101 and 65535 inclusive). If all
	 * the information checks out, the username and password are sent to the
	 * given address and port. <br><br>
	 *
	 * If the port is invalid, the error "invalid port" will be shown <br>
	 * If the host destination could not be reached or a server was not present,
	 * the error "Invalid connection settings" will be displayed <br>
	 * If the username or password was rejected by the server, the error "invalid
	 * username or password" will be displayed. <br>
	 * Finally, if everything checks out, the GUI will reset itself, download
	 * new information, and then display the welcome screen.
	 */
	public void login() {
		String host_input = host_fld.getText(),
			   port_input = port_fld.getText();

		String port_filter = port_input.replaceAll("[0-9]", "");

		lgin_lbl.setText("");

		if(!host_input.equals("")) {
			if(!port_input.equals("") && port_filter.equals("")) {
				int port_parse = Integer.parseInt(port_input);
				if(100 < port_parse && port_parse <= 65535) {
					try {
						client = new Client(host_input, port_parse, this);
						if(client.login(user_fld.getText(), pass_fld.getText().getBytes())) {
							host = host_input;
							port = port_parse;
							
							logout = false;
							lgin_pnl.setVisible(false);
							hideAll();
							menu_pnl.setVisible(true);
							list_pnl.getChildren().clear();
							showOpen();

							user_fld.clear();
							pass_fld.clear();
							host_fld.clear();
							port_fld.clear();

							downloadTasks();
							downloadUsers();	

							try {
								tasks = new TaskHandler();
								tasks.setSource(new SwiftFront(new File(LC_TASK + "public_index")));
							}
							catch(FileException fx) {

							}

							timer = new Timer();
							timer.scheduleAtFixedRate(new TimerTask() {
								public void run() {
									if(((fullctrl != null && !fullctrl.isVisible()) ||
									    (task_pnl != null && task_pnl.isVisible())) && (!locked && !upload_stask && !reload_task && !logout)) {
										refreshTasks();
									}
								}
							}, 10000, 10000);
						}
						else {
							lgin_lbl.setText("Invalid username or password");
						}
					}
					catch(DisconnectException dx) {
						lgin_lbl.setText("Invalid Connection Settings");
					}
				}
				else {
					lgin_lbl.setText("Port out of range (100-65535)");
				}
			}
			else {
				lgin_lbl.setText("Invalid Port");
			}
		}
		else {
			lgin_lbl.setText("Invalid Hostname");
		}
	}

	/**
	 * <b>Hide All Panels</b><br>
	 * Hides all subpanels to allow for another panel to be displayed on screen
	 */
	public void hideAll() {
		task_pnl.setVisible(false);
		list_pnl.setVisible(false);
		misc_pnl.setVisible(false);
		fullctrl.setVisible(false);
		open_pnl.setVisible(false);
		prog_pnl.setVisible(false);
	}

	/**
	 * <b>Hide Menu / Show Login</b><br>
	 * Hides the main panel and displays the login screen
	 */
	public void hideMenu() {
		lgin_pnl.setVisible(true);
		menu_pnl.setVisible(false);
	}

	/**
	 * <b>Display Welcome Message</b><br>
	 * Customizes the welcome screen to display the users realname, the address
	 * and port they are connected. Once this is complete, the welcome screen
	 * is displayed as a subpanel
	 */
	public void showOpen() {
		hideAll();
		user_lbl.setText(client.getUser().getRealname() + "!");
		conn_lbl.setText("Connected to " + host + ":" + port);
		open_pnl.setVisible(true);
	}

	/**
	 * <b>Show Menu / Hide Login</b><br>
	 * Hides the login panel and reveals the main/menu panel
	 */
	public void showMenu() {
		lgin_pnl.setVisible(false);
		menu_pnl.setVisible(true);
	}

	/**
	 * <b>Show Task List</b><br>
	 * Updates and displays the task list from the server.
	 */
	public void showList() {
		refreshTasks();

		hideAll();
		fullctrl.setVisible(false);
		list_pnl.setVisible(true);
		task_pnl.setVisible(true);
	}

	/**
	 * <b>Expand / Open Task</b><br>
	 * Replaces the list panel with a full, read-limited-write task panel with
	 * full task and subtask information.
	 */
	public void expandTask(Task t) {
		fullctrl.setTask(t);

		misc_pnl.getChildren().clear();
		misc_pnl.getChildren().add(fullctrl);

		list_pnl.setVisible(false);
		misc_pnl.setVisible(true);
		task_pnl.setVisible(true);
		fullctrl.setVisible(true);
	}

	/**
	 * <b>Logout</b><br>
	 * Waits for any operations to complete, then informs the server of the
	 * disconnection before returning to the login screen
	 */
	public void logout() {
		logout = true;
		try {
			while(upload_stask || reload_task || update_task) {
				if(locked) {
					synchronized(lock) {
						try {
							lock.wait();
							Thread.sleep(1000);
						}
						catch(InterruptedException ix) {
		
						}
					}
				}
				else {
					reload_task = false;
					update_task = false;
				}
			}
			if(client != null) client.disconnect(0);
		}
		catch(DisconnectException dx) {
			dx.printStackTrace();
			client.kill(EXC_CONN);
		}

		timer.cancel();

		list_pnl.getChildren().clear();

		hideAll();
		showOpen();
		hideMenu();
	}

	/**
	 * <b>Download Task Index File</b><br>
	 * Download the task index directly from the source. The index file is
	 * written to the local hard drive and is loaded to be used as a reference
	 * to download each task file.
	 */
	public void downloadTasks() {
		try {
			SwiftFile file = client.sfcmd(new ServerCommand(CMD_READ_FILE, "task/index"));
			file.setFile(new File(LC_TASK + "public_index"), false);
			file.write();

			tasks = new TaskHandler();
			tasks.setSource(new SwiftFront(new File(LC_TASK + "public_index")));
		}
		catch(SwiftException | IOException x) {
			terminate(client);
		}
	}

	/**
	 * <b>Download Public User Index File</b><br>
	 * Downloads the public user index directly from the source. This index file
	 * is written to the local file system as a reference. <br>
	 *
	 * <b>Note:</b> The difference between public and standard is public does 
	 * not include the users' passwords
	 */
	public void downloadUsers() {
		try {
			SwiftFile file = client.sfcmd(new ServerCommand(CMD_READ_FILE, "users_public"));
			file.setFile(new File(LC_PATH + "users_public"), false);
			file.write();

			users = new UserHandler();
			users.setSource(new SwiftFront(new File(LC_PATH + "users_public")));
		}
		catch(SwiftException | IOException x) {
			x.printStackTrace();
			terminate(client);
		}

		if(users != null)
		for(User u : users.getArray()) System.out.println(u);
		else System.out.println("fail");
	}

	/**
	 * <b>Download Specific Task</b><br>
	 * Requests to pull a task from the server's memory (not from the file
	 * system). In this case, it is not the task itself being downloaded, but
	 * rather, all of the task's subtasks that make up the task
	 *
	 * @param t Task to download from server
	 */
	public void downloadTask(Task t) {
		t.setList(client.pullTask(t));
	}

	/**
	 * <b>Upload Subtask</b><br>
	 * Upload a subtask from client memory and push it into server memory for
	 * use.
	 *
	 * @param t Task which owns the subtask. (This is required as a reference)
	 * @param s Subtask to update
	 */
	public void uploadSubtask(Task t, SubTask s) {	
		Thread upload = new Thread(new Runnable() {
			public void run() {
				cue++;
				if(locked) {
					synchronized(lock) {
						try {
							lock.wait();
						}
						catch(InterruptedException ix) {

						}
					}
				}
				setProgressVisible(true);
				upload_stask = true;
				client.pushSubtask(t, s);
				setProgressVisible(false);
				
				synchronized(lock) { lock.notifyAll(); cue--; }
			}
		});
		upload.start();
		try {
			upload.join();
		}
		catch(InterruptedException ix) {

		}
		upload_stask = false;
	}

	/**
	 * <b>Update Task</b><br>
	 * Downloads (updates) information on the given task from the server. This
	 * function differs from downloadTask by displaying the information on the
	 * current full task controller
	 *
	 * @param t Task to download
	 */
	public void updateTask(Task t) {
		System.out.println("" + upload_stask + reload_task + update_task);
		if(!logout) {
			Thread update = new Thread(new Runnable() {
				public void run() {
					cue++;
					while(upload_stask) {
						if(locked) {
							synchronized(lock) {
								try {
									lock.wait();
									}
								catch(InterruptedException ix) {
		
								}
							}
						}
					}

					setProgressVisible(true);

					update_task = true;
					locked = true;
	
					SubTask[] subtasks = client.pullTask(t);
					if(subtasks != null) t.setList(subtasks);
	
					synchronized(lock) { lock.notifyAll(); cue--; }
					locked = false;
					update_task = false;
					Platform.runLater(new Runnable() {
						public void run() {
							if(fullctrl.isVisible()) fullctrl.updateTask(t);
						}
					});
					
					setProgressVisible(false);
				}
			});
			update.start();

			try {
				update.join();
			}
			catch(InterruptedException ix) {
	
			}
			System.out.println(update_task);
		}
	}

	/**
	 * <b>Refresh All Task Information</b><br>
	 * Repeats the download process for all tasks known to the client
	 */
	public void refreshTasks() {
		System.out.println("\n\n\n" + reload_task + "\n\n\n");
		if(!reload_task && !logout) {
			reload_task = true;
			task_btn.disarm();
			new Thread(new Runnable() {
				public void run() {
					cue++;
					System.out.println(update_task);
					System.out.println(upload_stask);
					while(locked || update_task || upload_stask) {
					System.out.println(update_task);
					System.out.println(upload_stask);
						if(locked) {
							synchronized(lock) {
								try {
									lock.wait();
								}
								catch(InterruptedException ix) {
		
								}
							}
						}
					}
					
					setProgressVisible(true);
					for(Task t : tasks.getArray()) {
						try { Thread.sleep(500); }
						catch(InterruptedException ix) {}
						updateTask(t);
					}
		
					synchronized(lock) { lock.notifyAll(); cue--; }
					System.out.println(locked + "FFSDF");
					locked = false;

					Platform.runLater(new Runnable() {
						public void run() {
							System.out.println("runlater");
							list_pnl.getChildren().clear();
							for(Task t : tasks.getArray()) {
								TaskController tskctrl = new TaskController();
								tskctrl.setTask(t);
								tskctrl.setParent(This);
								list_pnl.getChildren().add(tskctrl);
							}
							task_btn.arm();
							System.out.println("xrunlater");
						}
					});
					System.out.println("\n\n\t\t RELOAD DONE \n\n\t\t");
					setProgressVisible(false);
					reload_task = false;
				}
			}).start();
		}
	}
	
	/** @return The client used for socket I/O **/
	public Client getClient() {
		return client;
	}

	/** @return Index of users allowed on the server **/
	public UserHandler getUserlist() {
		return users;
	}

	/**
	 * <b>Quit</b><br>
	 * Disconnect from server
	 */
	public void quit() {
		try {
			client.disconnect(0);
		}
		catch(DisconnectException dx) {
			dx.printStackTrace();
		}

		System.exit(0);
	}

	public void setProgressVisible(boolean visibility) {
		prog_pnl.setVisible(visibility);
	}

	@Override
	public void dereference(SwiftNetTool t) {
		switch(t.getErrID()) {
			case EXC_INIT:	
			case EXC_LOGOUT:
				break;
			case EXC_CONN:
			case EXC_NWRITE:
			case EXC_NREAD:
				if(!lgin_pnl.isVisible()) {
					Platform.runLater(new Runnable() {
						public void run() {
							lgin_lbl.setText("Lost connection with the sever");
							hideMenu();
						}
					});
				}
				break;
			case EXC_SAFE:
				Platform.runLater(new Runnable() {
					public void run() {
						lgin_lbl.setText("Server Shutdown, Goodbye!");
						hideMenu();
					}
				});
				break;
			case EXC_VER:
				Platform.runLater(new Runnable() {
					public void run() {
						lgin_lbl.setText("Client-Server version don't match");
						hideMenu();
					}
				});
				break;	
		}
		
		if(timer != null) timer.cancel();
	}
}
