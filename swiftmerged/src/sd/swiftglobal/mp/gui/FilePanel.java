package sd.swiftglobal.mp.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import sd.swiftglobal.rk.gui.GraphicalInterface;
import sd.swiftglobal.rk.gui.GraphicalInterface.SwiftContainer;
import sd.swiftglobal.rk.gui.GraphicalInterface.SwiftPanel;

public class FilePanel extends JPanel implements SwiftPanel, ActionListener {
	public static final long serialVersionUID = 1l;

	private SwiftContainer parent;

	private JLabel background;

	public FilePanel(SwiftContainer c) {
		setParentContainer(c);
		setSize(750, 530);
		setLayout(null);

		background = GraphicalInterface.load("res/48.jpg");
		background.setBounds(0, 0, 1000, 530);
		add(background);
	}

	public void setParentContainer(SwiftContainer c) {
		parent = c;
	}

	public SwiftContainer getParentContainer() {
		return parent;
	} 
	public JPanel getPanel() {
		return this;
	}

	public void actionPerformed(ActionEvent act) {

	}
}
