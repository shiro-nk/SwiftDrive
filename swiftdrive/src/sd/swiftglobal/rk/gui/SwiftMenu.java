package sd.swiftglobal.rk.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetContainer;

/* This file is part of Swift Drive				 *
 * Copyright (C) 2015 Ryan Kerr					 *
 * Please refer to <http://www.gnu.org/licenses> */

public class SwiftMenu extends JPanel implements Settings, SwiftContainer, SwiftPanel, ActionListener {
	private static final long serialVersionUID = 1l;
	private SwiftContainer parent;
	private JPanel current;

	public SwiftMenu(SwiftContainer parent) {
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

	public void setPanel(SwiftPanel panel) {
		if(current != null) current.setVisible(false);
		current = panel.getPanel();
		current.setLocation(252, 0);
		current.setVisible(true);
	}

	public void setParent(SwiftContainer parent) {
		this.parent = parent;
	}
	
	public SwiftNetContainer getNetContainer() {
		return parent.getNetContainer();
	}

	public SwiftContainer getSwiftParent() {
		return parent;
	}

	public void actionPerformed(ActionEvent e) {
		
	}
}
