package sd.swiftclient.mp.gui;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class Swift_FileView extends JPanel implements SwiftPanel, ActionListener{

	/**
	 * @author Mohan Pan
	 * 
	 * The user control after login.
	 */
	private static final long serialVersionUID = 1L;

	public JPanel getPanel(){
		return this;
	}
	
	private final main_GUI parent;
	private JLabel BPartimage;
	final JFileChooser fc = new JFileChooser();
	
	public Swift_FileView(main_GUI parent){
		
		this.parent = parent;
		setSize(750, 530);
        setLayout(null);
		BPartimage = BPart_bg();
		BPartimage.setBounds(0, 0, 750, 530);
        add(BPartimage);
		
		
	}
	private JLabel BPart_bg() {
        try {
            return new JLabel(new ImageIcon(ImageIO.read(new File("48.jpg"))));
        }
        catch(IOException ix) {
            return new JLabel("Failed to load background image.");
        }
    }
	@Override
	public void actionPerformed(ActionEvent e) {
		
		
	}
}
