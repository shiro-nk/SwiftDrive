package sd.swiftclient.rk.gui;

/* This file is part of Swift Drive *
 * Copyright (C) 2015 Ryan Kerr		*/

public class UserList extends VBox {
	
	private UserHandler users;

	@FXML private VBox list_pnl;
	@FXML private HBox uerr_pnl;
	
	@FXML private TextField name_fld;
	@FXML private TextField user_fld;
	@FXML private PasswordField pass_fld;

	public UserList() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/UserList.fxml"));
		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load();
		}
		catch(IOException ix) {
			ix.printStackTrace();
		}
	}

	public void reload() {

	}

	public void createUser() {

	}

	public UserHandler getUserHandler() {

	}
}
