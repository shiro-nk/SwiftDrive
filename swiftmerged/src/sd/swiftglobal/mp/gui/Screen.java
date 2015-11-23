package sd.swiftglobal.mp.gui;

import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import sd.swiftglobal.rk.gui.GraphicalInterface;
import sd.swiftglobal.rk.gui.GraphicalInterface.SwiftContainer;
import sd.swiftglobal.rk.gui.GraphicalInterface.SwiftMaster;
import sd.swiftglobal.rk.gui.GraphicalInterface.SwiftPanel;

public class Screen extends JFrame implements SwiftContainer {
	private static final long serialVersionUID = 1l;

	private SwiftMaster master;
	private SwiftPanel panel;
	private JPanel active;

	public Screen(SwiftMaster m) {
		setMaster(m);
		ImageIcon icon = null;
		try { icon = GraphicalInterface.getIcon("res/icon.png"); }
		catch(IOException ix) {}

		setTitle("--- SWIFT DRIVE ---");
		setSize(1000, 530);
		setResizable(false);
		setLayout(null);
		setLocationRelativeTo(null);
		setIconImage(icon.getImage());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void setPanel(SwiftPanel p) {
		if(active != null) {
			active.setVisible(false);
			remove(active);
		}

		panel = p;
		active = p.getPanel();
		active.setLocation(0, 0);
		active.repaint();
		active.setVisible(true);
		add(active);
	}
	
	public void activate() {
		setVisible(true);
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
}
