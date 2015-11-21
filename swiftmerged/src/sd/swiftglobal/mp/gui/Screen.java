package sd.swiftglobal.mp.gui;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import sd.swiftglobal.mp.gui.GraphicalInterface.SwiftContainer;
import sd.swiftglobal.mp.gui.GraphicalInterface.SwiftMaster;
import sd.swiftglobal.mp.gui.GraphicalInterface.SwiftPanel;

public class Screen extends JFrame implements SwiftContainer {
	private static final long serialVersionUID = 1l;

	private SwiftMaster master;
	private SwiftPanel panel;
	private JPanel active;

	public Screen(SwiftMaster m) {
		ImageIcon icon = new ImageIcon("icon_bigger_60_tran.png");
		
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
