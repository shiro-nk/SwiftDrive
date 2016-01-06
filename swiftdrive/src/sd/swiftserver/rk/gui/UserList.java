package sd.swiftserver.rk.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.type.users.User;
import sd.swiftglobal.rk.type.users.UserHandler;

/* This file is part of Swift Drive *
 * Copyright (C) 2015 Ryan Kerr		*/

public class UserList extends VBox {
	
	private UserHandler users;
	private ServerInterface parent;

	@FXML private VBox list_pnl;
	@FXML private HBox uerr_pnl;
	@FXML private HBox info_pnl;
	
	@FXML private Label uerr_lbl;

	@FXML private TextField name_fld;
	@FXML private TextField user_fld;
	@FXML private PasswordField pass_fld;

	public UserList() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/UserList.fxml"));
		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load();
			suppressWarning();
		}
		catch(IOException ix) {
			ix.printStackTrace();
		}
	}

	public void reload() {
		list_pnl.getChildren().clear();

		try {
			users = new UserHandler();
			
			for(User u : users.getArray()) {
				UserController uctrl = new UserController();
				uctrl.setParent(this);
				uctrl.setUser(u);
				list_pnl.getChildren().add(uctrl);
			}

			if(parent.getServer() != null) parent.getServer().setUserlist(users);
		}
		catch(FileException fx) {
			displayWarning("Failed to load userlist");
		}
	}

	public void createUser() {
		String real = name_fld.getText(),
			   user = user_fld.getText(),
			   pass = pass_fld.getText();

		if(users.getByName(real) == null && users.get(user) == null) {
			try {
				users.add(new User(real, user, pass));
				reload();
			}
			catch(FileException fx) {
				displayWarning("Failed to write userlist");
			}
		}
		else {
			displayWarning("Name or username already taken!");
		}
	}

	public void displayWarning(String err) {
		uerr_lbl.setText(err);
		uerr_pnl.setVisible(true);
		info_pnl.setVisible(false);
	}

	public void suppressWarning() {
		uerr_pnl.setVisible(false);
		info_pnl.setVisible(true);
	}

	public void setParent(ServerInterface si) {
		parent = si;
		reload();
	}

	public UserHandler getUserHandler() {
		return users;
	}
}
