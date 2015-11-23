package sd.swiftglobal.rk.gui;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.gui.GraphicalInterface.SwiftContainer;
import sd.swiftglobal.rk.gui.GraphicalInterface.SwiftMaster;
import sd.swiftglobal.rk.gui.GraphicalInterface.SwiftPanel;

/* This file is part of Swift Drive				 *
 * Copyright (C) 2015 Ryan Kerr					 *
 * Please refer to <http://www.gnu.org/licenses> */

public class SwiftScreen extends JFrame implements Settings, SwiftContainer {
	public static final long serialVersionUID = 1l;
	private JPanel current;
	private SwiftPanel panel;
	private SwiftMaster master;

	public SwiftScreen(String title, SwiftMaster m) {
		master = m;
		setSize(GUI_FRAME_BORDER, GUI_FRAME_HEIGHT);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		setUndecorated(true);
		setIconImage(getResource("res/icon.png").getImage());
		setResizable(false);
		setTitle(title);
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

	public void activate() {
		setVisible(true);
	}

	public void deactivate() {
		setVisible(false);
	}

	public SwiftContainer getParentContainer() {
		return this;
	}
	
	public SwiftPanel getCurrentPanel() {
		return panel;
	}

	public void setMaster(SwiftMaster m) {
		master = m;
	}

	public SwiftMaster getMaster() {
		return master;
	}
	
	public void focus(JComponent component) {
		component.requestFocus();
	}
	
	private ImageIcon getResource(String path) {
		try {
			return new ImageIcon(
				ImageIO.read(
					Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(path)
				)
			);
		}
		catch(IOException ix) {
			return null;
		}
	}

	public void close() {
		dispose();
	}
}
