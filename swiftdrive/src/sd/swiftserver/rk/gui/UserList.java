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

/**
 * <b>User List:</b><br>
 * Provides the ability to view, add, and remove users using the GUI.
 *
 * @author Ryan Kerr
 */
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

	/**
	 * <b>Constructor:</b>
	 * Loads the fxml file into an object
	 */
	public UserList() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/Server/UserList.fxml"));
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

	/**
	 * <b>Reload Users:</b><br>
	 * Reload the user index from file, clear all the information already on
	 * screen, and then dynamically load the <b>UserController</b> for each
	 * user found in the file.
	 */
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

	/**
	 * <b>Create New User:</b><br>
	 * Create a new user with the given information (screen name, user name, and
	 * password) as long as it meets the following criteria: not blank/null and
	 * does not share a screen/user name with another user.
	 */
	public void createUser() {
		String real = name_fld.getText(),
			   user = user_fld.getText(),
			   pass = pass_fld.getText();

		if(users.getByName(real) == null && users.get(user) == null) {
			try {
				users.add(new User(real, user, pass));
				name_fld.setText("");
				user_fld.setText("");
				pass_fld.setText("");
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

	/**
	 * <b>Display a Message:</b><br>
	 * Display the given error/warning message on screen in the place of the
	 * normal GUI
	 */
	public void displayWarning(String err) {
		uerr_lbl.setText(err);
		uerr_pnl.setVisible(true);
		info_pnl.setVisible(false);
	}

	/**
	 * <b>Hide Messages:</b><br>
	 * Hide all error/warning messages from the screen and show the normal
	 * GUI forms instead.
	 */
	public void suppressWarning() {
		uerr_pnl.setVisible(false);
		info_pnl.setVisible(true);
	}

	/** @param si Server interface with references to all required information **/
	public void setParent(ServerInterface si) {
		parent = si;
		reload();
	}

	/** @return The <b>UserHandler</b> used to display the list **/
	public UserHandler getUserHandler() {
		return users;
	}
}
