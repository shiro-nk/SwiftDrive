package sd.swiftglobal.rk.gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/* This file is part of Swift Drive				 *
 * Copyright (C) 2015 Ryan Kerr					 *
 * Please refer to <http://www.gnu.org/licenses> */

public class SwiftLogin extends JPanel implements SwiftPanel, ActionListener {
	public static final long serialVersionUID = 1l;
	private SwiftContainer parent;
	private JTextField userfield;
	private JPasswordField passfield;
	private JLabel login;
	private JButton submit,
					settings; 

	public SwiftLogin(SwiftContainer parent) {
		int fheight = 40,
			fwidth  = 400,
			fposx   = 100,
			fposy   = 200;
			
		setParent(parent);
		setSize(1000, 500);
		setLocation(0, 0);
		setLayout(null);
		
		userfield = new JTextField(25);
		userfield.setLocation(fposx, fposy);
		userfield.setSize(fwidth, fheight);
		userfield.addActionListener(this);
		userfield.requestFocus();
		add(userfield);

		passfield = new JPasswordField(25);
		passfield.setLocation(fposx, fposy + fheight + 25);
		passfield.setSize(fwidth, fheight);
		passfield.addActionListener(this);
		add(passfield);

		login = new JLabel("Login");
		login.setFont(new Font("Sans", Font.BOLD, 75));
		login.setLocation(100, -125);
		login.setSize(500, 500);
		add(login);
	
		settings = new JButton("Settings");
		settings.setLocation(fposx + fwidth + 25, fposy);
		settings.setSize(160, fheight);
		add(settings);

		submit = new JButton("Submit");
		submit.setLocation(fposx + fwidth + 25, fposy + fheight + 25); 
		submit.setSize(160, fheight);
		add(submit);
	}
	
	public void next() {
		SwiftMenu menu = new SwiftMenu(parent);
		SwiftGreeter greeter = new SwiftGreeter(menu);
		menu.setPanel(greeter);
		parent.setPanel(menu);
	}

	public JPanel getPanel() {
		return this;
	}

	public void setParent(SwiftContainer parent) {
		this.parent = parent;
	}

	public SwiftContainer getSwiftParent() {
		return parent;
	}

	public void actionPerformed(ActionEvent ae) {
		Object src = ae.getSource();

		if(src == userfield) passfield.requestFocus();
		if(src == passfield) next();
	}
}
