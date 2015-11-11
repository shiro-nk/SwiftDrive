package sd.swiftglobal.rk.gui;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;

/* This file is part of Swift Drive				 *
 * Copyright (C) 2015 Ryan Kerr					 *
 * Please refer to <http://www.gnu.org/licenses> */

public class SwiftGreeter extends JPanel implements SwiftPanel {
	public static final long serialVersionUID = 1l;
	private SwiftContainer parent;

	public SwiftGreeter(SwiftContainer parent) {
		setParent(parent);
		setSize(750, 500);
		setLayout(null);
		setBackground(Color.RED);
		JLabel label = new JLabel("Welcome to SwiftDrive");
		label.setSize(250, 100);
		label.setLocation(100, 50);
		add(label);
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
}
