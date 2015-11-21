package sd.swiftglobal.mp.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sd.swiftglobal.mp.gui.GraphicalInterface.SwiftContainer;
import sd.swiftglobal.mp.gui.GraphicalInterface.SwiftPanel;

public class FilePanel extends JPanel implements SwiftPanel, ActionListener {
	public static final long serialVersionUID = 1l;

	private SwiftContainer parent;

	private JLabel background;

	public FilePanel(SwiftContainer c) {
		setParentContainer(c);
		setSize(750, 530);
		setLayout(null);

		background = loadBackground();
		background.setBounds(0, 0, 1000, 530);
		add(background);
	}

	private JLabel loadBackground() {
        try {
            return new JLabel(new ImageIcon(ImageIO.read(new File("48.jpg"))));
        }
        catch(IOException ix) {
            return new JLabel("Failed to load background image.");
        }
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
