package sd.swiftglobal.rk.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

	public SwiftLogin(SwiftContainer parent) {
		setParent(parent);
		setSize(1000, 500);
		setLocation(0, 0);
		setLayout(null);
		
		userfield = new JTextField(25);
		userfield.setLocation(200, 50);
		userfield.setSize(250, 25);
		userfield.addActionListener(this);
		userfield.requestFocus();
		add(userfield);

		passfield = new JPasswordField(25);
		passfield.setLocation(200, 100);
		passfield.setSize(250, 25);
		passfield.addActionListener(this);
		add(passfield);
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
