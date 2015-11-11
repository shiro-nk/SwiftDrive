package sd.swiftglobal.rk.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

/* This file is part of Swift Drive				 *
 * Copyright (C) 2015 Ryan Kerr					 *
 * Please refer to <http://www.gnu.org/licenses> */

public class SwiftMenu extends JPanel implements SwiftContainer, SwiftPanel, ActionListener {
	private static final long serialVersionUID = 1l;
	private SwiftContainer parent;
	private JPanel current;

	public SwiftMenu(SwiftContainer parent) {
		this.parent = parent;
		setSize(250, 500);
		setLocation(0, 0);
		setLayout(null);
		setBackground(Color.CYAN);
	}

	public JPanel getPanel() {
		JPanel output = new JPanel();
		output.setSize(1000, 500);
		output.setLocation(0, 0);
		output.setLayout(null);
		output.add(this);
		output.add(current);
		return output;
	}

	public void setPanel(SwiftPanel panel) {
		if(current != null) current.setVisible(false);
		current = panel.getPanel();
		current.setLocation(250, 0);
		current.setVisible(true);
	}

	public void setParent(SwiftContainer parent) {
		this.parent = parent;
	}

	public SwiftContainer getSwiftParent() {
		return parent;
	}

	public void actionPerformed(ActionEvent e) {
		
	}
}
