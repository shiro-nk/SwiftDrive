package sd.swiftglobal.rk.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetContainer;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetTool;

/* This file is part of Swift Drive				 *
 * Copyright (C) 2015 Ryan Kerr					 *
 * Please refer to <http://www.gnu.org/licenses> */

public class SwiftScreen extends JFrame implements Settings, SwiftContainer {
	public static final long serialVersionUID = 1l;
	private JPanel current;
	private SwiftPanel panel;
	private SwiftNetTool tool;
	private SwiftNetContainer container;

	public SwiftScreen(String title, SwiftNetContainer c) {
		setSize(GUI_FRAME_BORDER, GUI_FRAME_HEIGHT);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		setUndecorated(true);
		setResizable(false);
		setTitle(title);
		setVisible(true);
	}

	public void setPanel(SwiftPanel swiftpanel) {
		panel = swiftpanel;
		if(current != null) {
			current.setVisible(false);
			remove(current);
		}
		current = panel.getPanel();
		add(current);
		current.repaint();
		current.setVisible(true);
		current.repaint();
	}

	public void setNetContainer(SwiftNetContainer cont) {
		this.container = cont;
	}

	public SwiftNetContainer getNetContainer() {
		return container;
	}

	public void setNetTool(SwiftNetTool tool) {
		this.tool = tool;
	}

	public SwiftNetTool getNetTool() {
		return tool;
	}

	public SwiftContainer getSwiftParent() {
		return this;
	}

	public void close() {
		dispose();
	}
}
