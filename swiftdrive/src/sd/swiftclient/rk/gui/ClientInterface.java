package sd.swiftclient.rk.gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import sd.swiftclient.rk.net.Client;
import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.DisconnectException;
import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.type.tasks.Task;
import sd.swiftglobal.rk.type.tasks.TaskHandler;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetContainer;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetTool;

public class ClientInterface implements Settings, Initializable, SwiftNetContainer {
	
	private Client client;

	// Global Elements
	@FXML private ImageView back_img;

	// Menu Elements
	@FXML private Label vers_lbl;
	@FXML private SplitPane menu_pnl;
	@FXML private Button task_btn;
	@FXML private Button file_btn;
	@FXML private Button lgot_btn;
	
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

	public void initialize(URL a, ResourceBundle b) {
		lgin_pnl.setVisible(true);
		menu_pnl.setVisible(false);
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

							user_fld.clear();
							pass_fld.clear();
							host_fld.clear();
							port_fld.clear();
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

	public void logout() {
		try {
			System.out.println("DIs");
			if(client != null) client.disconnect(0);
		}
		catch(DisconnectException dx) {
			System.out.println("STA");
			dx.printStackTrace();
			client.close();
		}

		hideMenu();
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
		TaskHandler tasks = null;

		try {
			tasks = new TaskHandler();
		}
		catch(FileException fx) {

		}

		for(Task t : tasks.getArray()) {
			TaskController tskctrl = new TaskController();
			tskctrl.setTask(t);
			list_pnl.getChildren().add(tskctrl);
		}
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
			case EXC_SAFE:
				Platform.runLater(new Runnable() {
					public void run() {
						lgin_lbl.setText("Server Shutdown, Goodbye!");
						hideMenu();
					}
				});
		}
	}
}
