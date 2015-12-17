package sd.swiftclient.rk.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;

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
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetContainer;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetTool;

/* This file is part of swift drive *
 * Copyright (c) 2015 Ryan Kerr     */

public class ClientInterface implements Settings, Initializable, SwiftNetContainer {

	private Timer timer;
	private Client client = null;
	private TaskHandler tasks = null;
	private FullTaskController fullctrl = new FullTaskController(); 
	private ClientInterface This = this;

	private Object lock = new Object();
	private boolean locked = false;

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
							try {
								tasks = new TaskHandler();
							}
							catch(FileException fx) {

							}
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

	public void reset() {

	}

	public void logout() {
		try {
			System.out.println("Logging out");
			if(client != null) client.disconnect(0);
		}
		catch(DisconnectException dx) {
			System.out.println("Logging out failed, forcing error");
			dx.printStackTrace();
			client.kill(EXC_CONN);
		}

		hideMenu();
	}

	public void downloadTasks() {
		try {
			SwiftFile file = client.sfcmd(new ServerCommand(CMD_READ_FILE, "task/index"));
			file.setFile(new File(LC_TASK + "index"), false);
			file.write();
		}
		catch(SwiftException | IOException x) {
			x.printStackTrace();
		}
	}

	public void downloadTask(Task t) {
		System.out.println(client.pullTask(t));
		t.setList(client.pullTask(t));
	}

	public void uploadSubtask(Task t, SubTask s) {	
		Thread upload = new Thread(new Runnable() {
			public void run() {
				if(locked) {
					synchronized(lock) {
						try {
							System.out.println("Waiting");
							lock.wait();
						}
						catch(InterruptedException ix) {

						}
					}
				}
				client.pushSubtask(t, s);
			}
		});
		upload.start();
		try {
			upload.join();
		}
		catch(InterruptedException ix) {

		}
	}

	public void updateTask(Task t) {
		new Thread(new Runnable() {
			public void run() {
				System.out.println("Update Task Waiting");
				if(locked) {
					synchronized(lock) {
						try {
							System.out.println("Waiting");
							lock.wait();
						}
						catch(InterruptedException ix) {

						}
					}
				}
				System.out.println("Update Task GO!");
				locked = true;

				SubTask[] subtasks = client.pullTask(t);

				if(subtasks != null) {
					for(SubTask s : subtasks) {
						System.out.println(s);
					}

					t.setList(client.pullTask(t));
				}

				synchronized(lock) { lock.notifyAll(); }
				locked = false;

				Platform.runLater(new Runnable() {
					public void run() {
						if(fullctrl.isVisible()) fullctrl.updateTask(t);
					}
				});
			}
		}).start();
	}

	public void refreshTasks() {
		task_btn.disarm();
		new Thread(new Runnable() {
			public void run() {
				if(locked) {
					synchronized(lock) {
						try {
							System.out.println("waiting");
							lock.wait();
						}
						catch(InterruptedException ix) {

						}
					}
				}

				for(Task t : tasks.getArray()) updateTask(t);
		
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
					}
				});
			}
		}).start();
		task_btn.arm();
	}
	
	public Client getClient() {
		return client;
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
		System.out.println("Dereference : SAFE");
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
			case EXC_SAFE:
				Platform.runLater(new Runnable() {
					public void run() {
						lgin_lbl.setText("Server Shutdown, Goodbye!");
						hideMenu();
					}
				});
		}
		
		if(timer != null) timer.cancel();
	}
}
