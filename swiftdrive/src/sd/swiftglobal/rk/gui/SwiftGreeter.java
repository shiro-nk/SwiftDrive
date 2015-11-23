package sd.swiftglobal.rk.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sd.swiftglobal.rk.gui.GraphicalInterface.SwiftContainer;
import sd.swiftglobal.rk.gui.GraphicalInterface.SwiftPanel;

/* This file is part of Swift Drive				 *
 * Copyright (C) 2015 Ryan Kerr					 *
 * Please refer to <http://www.gnu.org/licenses> */

public class SwiftGreeter extends JPanel implements SwiftPanel, ActionListener {
	public static final long serialVersionUID = 1l;
	private SwiftContainer parent;

	public SwiftGreeter(SwiftContainer parent) {
		setParentContainer(parent);
		setSize(750, 500);
		setLayout(null);
		
		//String username = "server";
		//if(parent.getNetContainer().hasTool()) 
		//	username = parent.getNetContainer().getTool().getUser().getUsername();

		JLabel label = new JLabel("Sorry! The client has disconnected!");
		label.setSize(250, 100);
		label.setLocation(100, 50);
		add(label);

		JButton button = new JButton("Exit");
		button.setSize(100, 50);
		button.setLocation(250, 150);
		button.addActionListener(this);
		add(button);
	}

	public JPanel getPanel() {
		return this;
	}

	public void setParentContainer(SwiftContainer parent) {
		this.parent = parent;
	}

	public SwiftContainer getParentContainer() {
		return parent;
	}

	public void actionPerformed(ActionEvent act) {
		System.exit(1000);	
	}
}
