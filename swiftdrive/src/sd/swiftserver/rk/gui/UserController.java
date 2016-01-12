package sd.swiftserver.rk.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.type.users.User;

/* This file is part of Swift Drive *
 * Copyright (C) 2015 Ryan Kerr		*/

/**
 * <b>User Controller:</b><br>
 * Provides the ability to view user information, toggle the visibility of
 * the user password, and the ability to delete the user from the GUI.
 */
public class UserController extends HBox {

	private User user;
	private UserList parent;

	@FXML private Label name_lbl;
	@FXML private Label user_lbl;

	@FXML private Button pass_btn;

	/**
	 * <b>Constructor</b><br>
	 * Create GUI from fxml file
	 */
	public UserController() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/Server/User.fxml"));
		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load();
		}
		catch(IOException ix) {
			ix.printStackTrace();
		}
	}

	/**
	 * <b>Set User</b><br>
	 * Sets and makes the GUI reflect the information about the user
	 */
	public void setUser(User u) {
		user = u;
		name_lbl.setText(user.getRealname());
		user_lbl.setText(user.getUsername());
	}

	/** @param u Userlist which displays this user **/
	public void setParent(UserList u) {
		parent = u;
	}

	/**
	 * <b>Toggle Password Visibility</b><br>
	 * Shows the password on the button when clicked when "Reveal Password" is
	 * on the button. Inversely, sets the text to "Reveal Password" when click
	 * if the password is displayed on the button.
	 */
	public void showPassword() {
		pass_btn.setText (
			pass_btn.getText().equals(new String(user.getPassword())) ?
			"Reveal Password" : new String(user.getPassword())
		);
	}

	/**
	 * <b>Delete User</b><br>
	 * Deletes the user by sending the delete request to the User index
	 */
	public void delete() {
		if(parent != null) {
			try {
				parent.getUserHandler().remove(user);
				parent.reload();
			}
			catch(FileException fx) {
				parent.displayWarning("Failed to write userlist");
			}
		}
	}
}
