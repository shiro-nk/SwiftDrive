package sd.swiftclient.rk.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.gui.SwiftGUI.SwiftContainer;
import sd.swiftglobal.rk.gui.SwiftGUI.SwiftMaster;
import sd.swiftglobal.rk.gui.SwiftGUI.SwiftPanel;

/* This file is part of Swift Drive				 *
 * Copyright (C) 2015 Ryan Kerr					 *
 * Please refer to <http://www.gnu.org/licenses> */

public class SwiftClientMenu extends JPanel implements Settings, SwiftContainer, SwiftPanel, ActionListener {
	private static final long serialVersionUID = 1l;
	private SwiftContainer parent;
	private JPanel current;
	private SwiftPanel active;

	public SwiftClientMenu(SwiftContainer parent) {
		this.parent = parent;
		setSize(250, 500);
		setLocation(0, 0);
		setLayout(null);
	}

	public JPanel getPanel() {
		JPanel output = new JPanel();
		output.setSize(GUI_FRAME_BORDER, GUI_FRAME_HEIGHT);
		output.setLocation(0, 0);
		output.setLayout(null);
		output.setBackground(Color.CYAN);
		output.add(this);
		output.add(current);
		return output;
	}

	public SwiftPanel getCurrentPanel() {
		return active;
	}

	public void setPanel(SwiftPanel panel) {
		if(current != null) current.setVisible(false);
		current = panel.getPanel();
		current.setLocation(252, 0);
		current.setVisible(true);
	}

	public void setParentContainer(SwiftContainer parent) {
		this.parent = parent;
	}
	
	public SwiftContainer getParentContainer() {
		return parent;
	}

	public SwiftMaster getMaster() {
		return parent.getMaster();
	}

	public void actionPerformed(ActionEvent e) {

	}
}
