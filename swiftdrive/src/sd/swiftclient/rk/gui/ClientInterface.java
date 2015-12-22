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
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
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
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetContainer;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetTool;

/* This file is part of swift drive *
 * Copyright (c) 2015 Ryan Kerr     */

public class ClientInterface implements Settings, Initializable, SwiftNetContainer {

	private Timer timer;
	private Client client = null;
	private TaskHandler tasks = null;
	private UserHandler users = null;
	private FullTaskController fullctrl = new FullTaskController(); 
	private ClientInterface This = this;

	private Object lock = new Object();
	private boolean locked = false,
					upload_stask = false,
					reload_task = false,
					update_task = false;

	// Global Elements
	@FXML private ImageView back_img;

	// Menu Elements
	@FXML private Label vers_lbl;
	@FXML private SplitPane menu_pnl;
	@FXML private Button task_btn;
	@FXML private Button file_btn;
	@FXML private Button lgot_btn;

	// Welcome
	@FXML private VBox open_pnl;

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

	public void initialize(URL a, ResourceBundle b) {
		hideAll();
		lgin_pnl.setVisible(true);
		menu_pnl.setVisible(false);
		
		fullctrl.setClientInterface(this);
	}

	public void login() {
		String host_input = host_fld.getText(),
			   port_input = port_fld.getText();

		String port_filter = port_input.replaceAll("[0-9]", "");

		lgin_lbl.setText("");

		if(!host_input.equals("")) {
			if(!port_input.equals("") && port_filter.equals("")) {
				int port_parse = Integer.parseInt(port_input);
				if(100 <= port_parse && port_parse <= 65535) {
					try {
						client = new Client(host_input, port_parse, this);
						if(client.login(user_fld.getText(), pass_fld.getText().getBytes())) {
							lgin_pnl.setVisible(false);
							menu_pnl.setVisible(true);
							open_pnl.setVisible(true);

							user_fld.clear();
							pass_fld.clear();
							host_fld.clear();
							port_fld.clear();

							downloadTasks();
							downloadUsers();
							try {
								tasks = new TaskHandler();
							}
							catch(FileException fx) {

							}

							timer = new Timer();
							timer.scheduleAtFixedRate(new TimerTask() {
								public void run() {
									if(((fullctrl != null && fullctrl.isVisible()) ||
									    (task_pnl != null && task_pnl.isVisible())) && (!locked && !upload_stask && !reload_task)) {
										System.out.println("Timer GO");
										refreshTasks();
									}
								}
							}, 15000, 15000);
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

	public void hideAll() {
		task_pnl.setVisible(false);
		list_pnl.setVisible(false);
		misc_pnl.setVisible(false);
		fullctrl.setVisible(false);
		open_pnl.setVisible(false);
	}

	public void hideMenu() {
		lgin_pnl.setVisible(true);
		menu_pnl.setVisible(false);
	}

	public void showOpen() {
		hideAll();
		open_pnl.setVisible(true);
	}

	public void showMenu() {
		lgin_pnl.setVisible(false);
		menu_pnl.setVisible(true);
	}

	public void showList() {
		refreshTasks();

		hideAll();
		list_pnl.setVisible(true);
		task_pnl.setVisible(true);
	}

	public void expandTask(Task t) {
		fullctrl.setTask(t);

		misc_pnl.getChildren().clear();
		misc_pnl.getChildren().add(fullctrl);

		list_pnl.setVisible(false);
		misc_pnl.setVisible(true);
		task_pnl.setVisible(true);
		fullctrl.setVisible(true);
	}

	public void logout() {
		try {
			if(locked) {
				synchronized(lock) {
					try {
						lock.wait();
					}
					catch(InterruptedException ix) {
					
					}
				}
			}
			System.out.println("Logging out");
			if(client != null) client.disconnect(0);
		}
		catch(DisconnectException dx) {
			System.out.println("Logging out failed, forcing error");
			dx.printStackTrace();
			client.kill(EXC_CONN);
		}

		list_pnl.getChildren().clear();

		hideAll();
		showOpen();
		hideMenu();
	}

	public void downloadTasks() {
		try {
			SwiftFile file = client.sfcmd(new ServerCommand(CMD_READ_FILE, "task/index"));
			file.setFile(new File(LC_TASK + "index"), false);
			file.write();
		}
		catch(SwiftException | IOException x) {
			terminate(client);
		}
	}

	public void downloadUsers() {
		try {
			SwiftFile file = client.sfcmd(new ServerCommand(CMD_READ_FILE, "users_public"));
			file.setFile(new File(LC_PATH + "users"), false);
			file.write();

			users = new UserHandler();
		}
		catch(SwiftException | IOException x) {
			x.printStackTrace();
			terminate(client);
		}

		if(users != null)
		for(User u : users.getArray()) System.out.println(u);
		else System.out.println("fail");
	}

	public void downloadTask(Task t) {
		t.setList(client.pullTask(t));
	}

	public void uploadSubtask(Task t, SubTask s) {	
		Thread upload = new Thread(new Runnable() {
			public void run() {
				if(locked) {
					synchronized(lock) {
						try {
							System.out.println("Waiting: Upload SubTask");
							lock.wait();
							System.out.println("GO: Upload SubTask");
						}
						catch(InterruptedException ix) {

						}
					}
				}
				upload_stask = true;
				client.pushSubtask(t, s);
				System.out.println("Upload SubTask Completed");
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

	public void updateTask(Task t) {
		Thread update = new Thread(new Runnable() {
			public void run() {
				while(upload_stask) {
					if(locked) {
						synchronized(lock) {
							try {
								System.out.println("Waiting: UpdateTask");
								lock.wait();
								System.out.println("GO: UpdateTask");
							}
							catch(InterruptedException ix) {
	
							}
						}
					}
				}
				update_task = true;
				locked = true;

				SubTask[] subtasks = client.pullTask(t);
				if(subtasks != null) t.setList(subtasks);

				synchronized(lock) { lock.notifyAll(); }
				locked = false;
				update_task = false;
				Platform.runLater(new Runnable() {
					public void run() {
						if(fullctrl.isVisible()) fullctrl.updateTask(t);
					}
				});
				System.out.println("UpdateTask completed");
			}
		});
		update.start();

		try {
			update.join();
		}
		catch(InterruptedException ix) {

		}
	}

	public void refreshTasks() {
		if(!reload_task) {
			reload_task = true;
			task_btn.disarm();
			new Thread(new Runnable() {
				public void run() {
					while(update_task || upload_stask) {
						if(locked) {
							synchronized(lock) {
								try {
									System.out.println("waiting: reload task");
									lock.wait();
									System.out.println("Reload Task GO");
								}
								catch(InterruptedException ix) {
		
								}
							}
						}
					}

					for(Task t : tasks.getArray()) {
						System.out.println("UPDATE: " + t);
						try { Thread.sleep(1000); }
						catch(InterruptedException ix) {}
						updateTask(t);
					}
		
					synchronized(lock) { lock.notifyAll(); }
					locked = false;

					Platform.runLater(new Runnable() {
						public void run() {
							list_pnl.getChildren().clear();
							for(Task t : tasks.getArray()) {
								TaskController tskctrl = new TaskController();
								tskctrl.setTask(t);
								tskctrl.setParent(This);
								list_pnl.getChildren().add(tskctrl);
							}
							task_btn.arm();
						}
					});
					System.out.println("Reload done");
					reload_task = false;
				}
			}).start();
		}
	}
	
	public Client getClient() {
		return client;
	}

	public UserHandler getUserlist() {
		return users;
	}

	public void quit() {
		try {
			client.disconnect(0);
		}
		catch(DisconnectException dx) {
			dx.printStackTrace();
		}

		System.exit(0);
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
				Platform.runLater(new Runnable() {
					public void run() {
						lgin_lbl.setText("Lost connection with the sever");
						hideMenu();
					}
				});
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
