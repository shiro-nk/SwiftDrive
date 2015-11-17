package sd.swiftclient.mp.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import sd.swiftclient.SwiftClient;
import sd.swiftclient.rk.net.Client;
import sd.swiftglobal.rk.Meta.Ryan;
import sd.swiftglobal.rk.expt.DisconnectException;

public class login_page extends JPanel implements ActionListener, SwiftPanel{

	/**
	 * 
	 * @author Mohan Pan
	 */
	private static final long serialVersionUID = 1L;

			//WindowManager.getDefault().getMainWindow();
	//private JLabel background;
	private JTextField 	   userfield;
	private JPasswordField passfield;
	private JButton login_button;
	private JLabel background, titleBar;
	private final main_GUI parent;
	
	@Ryan private SwiftClient cont;

	public login_page(main_GUI parent, @Ryan SwiftClient cont)
	{
		this.cont = cont;
		this.parent = parent;
		setSize(1000, 530);
        setLayout(null);
        
        login_button = new JButton();
		login_button.setBounds(886, 270, 72, 40);
		login_button.setVisible(true);  
		//login_button.setFont(new Font("Georgia", Font.BOLD, 12));
		login_button.setOpaque(false);  
		login_button.setContentAreaFilled(false);
		login_button.setBorderPainted(false);    
		//Three of them make the button transparent :D
		login_button.addActionListener(this);
		add(login_button);

		userfield = new JTextField(25);
		userfield.setBounds(513, 270, 170, 36);
		userfield.setFont(new Font("Arial", Font.PLAIN, 18));
    	userfield.setForeground(Color.white);
		userfield.setOpaque(false);
		userfield.setBorder(null);
		userfield.addActionListener(this);
		userfield.grabFocus();
		//
		add(userfield);
		
		passfield = new JPasswordField(25);
		passfield.setBounds(700, 270, 170, 36);
		passfield.setFont(new Font("Arial", Font.PLAIN, 18));
    	passfield.setForeground(Color.white);
		passfield.setOpaque(false);
		passfield.setBorder(null);
		passfield.addActionListener(this);
		add(passfield);
		
		setVisible(true);
		
        titleBar = before_bgpic();
        titleBar.setBounds(400, 70, 600, 100);
        add(titleBar);
        
        background = bgpic();
        background.setBounds(0, 0, 1000, 530);
        add(background);
        
        
	} // end constructor
	
	private JLabel before_bgpic(){
		try {
            return new JLabel(new ImageIcon(ImageIO.read(new File("Title_Bar_Swift_Drive.png"))));
        }
        catch(IOException ix) {
            return new JLabel("Failed to load background image.");
        }
	}
	
	private JLabel bgpic() {
        try {
            return new JLabel(new ImageIcon(ImageIO.read(new File("login_page.png"))));
        }
        catch(IOException ix) {
            return new JLabel("Failed to load background image.");
        }
    }
	
	public JPanel getPanel() {
        return this;
    }
	
	public void act(ActionEvent e) {
	//public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if(src == userfield) passfield.grabFocus();
		System.out.println("Hello, " + userfield.getText() + "!");
		SwiftMenu_Apart menu = new SwiftMenu_Apart(parent);
		menu.showPanel(new Swift_FileView(parent));
		parent.showPanel(menu);
		//run PartA_PartB();
		//showPanel
	}

	@Ryan
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if(src == userfield) passfield.grabFocus();
		if(src == login_button || src == passfield) {
			try {
				System.out.println("Logging in");
				Client cli = new Client("192.168.1.214", 3141, parent.getNetContainer());
				if(cli.login(userfield.getText(), passfield.getPassword())) {
					cont.setClient(cli);
					SwiftMenu_Apart menu = new SwiftMenu_Apart(parent);
					menu.showPanel(new Swift_FileView(parent));
					parent.showPanel(menu);
				}
				else {
					System.err.println("Failed login attempted");
				}
			}
			catch(DisconnectException dx) {
				dx.printStackTrace();
			}
		}
	}
}

