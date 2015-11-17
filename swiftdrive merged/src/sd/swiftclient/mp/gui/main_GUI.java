package sd.swiftclient.mp.gui;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import sd.swiftglobal.rk.Meta.Ryan;
import sd.swiftglobal.rk.gui.SwiftContainer;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetContainer;

public class main_GUI extends JFrame implements SwiftContainer {
	
	/**
	 * @author Mohan Pan
	 * 
	 * The main control.
	 */
	private static final long serialVersionUID = 1L;
	
	JFrame main_GUI = new JFrame("---SWIFT DRIVE---");
	
	JPanel current;
	SwiftPanel panel;
	SwiftNetContainer cont;

	public main_GUI(@Ryan SwiftNetContainer cli) {
		cont = cli;
		setSize(1000, 530);
		setResizable(false);
		ImageIcon icon = new ImageIcon("icon_bigger_60_tran.png");
		setIconImage(icon.getImage());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
	}
	    
	public void showPanel(SwiftPanel gui) {
		if(current != null) {
			current.setVisible(false);
			remove(current);
		}
		panel = gui;
		current = panel.getPanel();
		current.repaint();
		current.setVisible(true);
		add(current);
		repaint();
		setVisible(true);
	}
	
	@Ryan
	public void setPanel(sd.swiftglobal.rk.gui.SwiftPanel parent) {
		
	}

	@Ryan
	public SwiftNetContainer getNetContainer() {
		return cont;
	}
}
