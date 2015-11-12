package sd.swiftglobal.rk.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/* This file is part of Swift Drive				 *
 * Copyright (C) 2015 Ryan Kerr					 *
 * Please refer to <http://www.gnu.org/licenses> */

public class SwiftGreeter extends JPanel implements SwiftPanel, ActionListener {
	public static final long serialVersionUID = 1l;
	private SwiftContainer parent;

	public SwiftGreeter(SwiftContainer parent) {
		setParent(parent);
		setSize(750, 500);
		setLayout(null);
		JLabel label = new JLabel("Welcome to SwiftDrive");
		label.setSize(250, 100);
		label.setLocation(100, 50);
		add(label);

		JButton button = new JButton("Debug");
		button.setSize(100, 50);
		button.setLocation(250, 150);
		button.addActionListener(this);
		add(button);
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

	public void actionPerformed(ActionEvent act) {
	
	}
}
