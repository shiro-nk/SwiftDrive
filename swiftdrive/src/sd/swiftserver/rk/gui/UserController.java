package sd.swiftserver.rk.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.type.users.User;

public class UserController extends HBox {

	private User user;
	private UserList parent;

	@FXML private Label name_lbl;
	@FXML private Label user_lbl;

	@FXML private Button pass_btn;

	public UserController() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/User.fxml"));
		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load();
		}
		catch(IOException ix) {
			ix.printStackTrace();
		}
	}

	public void setUser(User u) {
		user = u;
		System.out.println(user);
		System.out.println(name_lbl);
		System.out.println(user.getRealname());
		name_lbl.setText(user.getRealname());
		user_lbl.setText(user.getUsername());
	}

	public void setParent(UserList u) {
		parent = u;
	}

	public void showPassword() {
		pass_btn.setText (
			pass_btn.getText().equals(new String(user.getPassword())) ?
			"Reveal Password" : new String(user.getPassword())
		);
	}

	public void delete() {
		if(parent != null) {
			try {
				System.out.println(parent);
				System.out.println(parent.getUserHandler());
				parent.getUserHandler().remove(user);
				parent.reload();
			}
			catch(FileException fx) {
				parent.displayWarning("Failed to write userlist");
			}
		}
	}
}
