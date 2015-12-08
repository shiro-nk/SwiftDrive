package sd.swiftclient.rk.gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import sd.swiftclient.SwiftClient;
import sd.swiftclient.rk.net.Client;
import sd.swiftglobal.rk.expt.DisconnectException;

public class ClientLogin implements Initializable {

	private Client client;

	@FXML private Label err_lbl;
	@FXML private PasswordField pass_fld;
	@FXML private TextField user_fld;
	@FXML private TextField host_fld;
	@FXML private TextField port_fld;

	@Override
	public void initialize(URL arg, ResourceBundle res) {

	}

	public void connect() {
		try {
			String user = user_fld.getText(),
				   host = host_fld.getText(),
				   pass = pass_fld.getText(),
				   port = port_fld.getText();

			client = new Client(host, 3141, new SwiftClient());
		}
		catch(DisconnectException dx) {

		}
	}
}
