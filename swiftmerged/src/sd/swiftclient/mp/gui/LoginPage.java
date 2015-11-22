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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import sd.swiftclient.ClientMaster;
import sd.swiftclient.rk.net.Client;
import sd.swiftglobal.mp.gui.FilePanel;
import sd.swiftglobal.mp.gui.GraphicalInterface;
import sd.swiftglobal.mp.gui.GraphicalInterface.SwiftContainer;
import sd.swiftglobal.mp.gui.GraphicalInterface.SwiftPanel;
import sd.swiftglobal.rk.Meta.Ryan;
import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.DisconnectException;
import sd.swiftglobal.rk.util.Logging;

public class LoginPage extends JPanel implements Logging, Settings, SwiftPanel, ActionListener {
	public static final long serialVersionUID = 1l;

	private SwiftContainer parent;
	private ClientMaster climaster;
	
	private JButton submit, apply;
	private JLabel background, titlebar, hostlabel, portlabel;
	private JDialog dialog;
	
	private JTextField userfield, hostfield, portfield;
	private JPasswordField passfield;
	
	public String host = "127.0.0.1";
	public int port = DEF_PORT;

	public LoginPage(SwiftContainer s, ClientMaster c) {
		setParentContainer(s);
		climaster = c;
		
		setSize(1000, 530);
        setLayout(null);
        
        submit = new JButton();
		submit.setBounds(886, 270, 72, 40);
		submit.setVisible(true);  

		//Three of them make the button transparent :D
		submit.setOpaque(false);  
		submit.setContentAreaFilled(false);
		submit.setBorderPainted(false);
		submit.addActionListener(this);
		add(submit);

		userfield = new JTextField(25);
		userfield.setBounds(513, 270, 170, 36);
		userfield.setFont(new Font("Arial", Font.PLAIN, 18));
    	userfield.setForeground(Color.white);
		userfield.setOpaque(false);
		userfield.setBorder(null);
		userfield.addActionListener(this);
		userfield.grabFocus();
		add(userfield);
		
		passfield = new JPasswordField(25);
		passfield.setBounds(700, 270, 170, 36);
		passfield.setFont(new Font("Arial", Font.PLAIN, 18));
    	passfield.setForeground(Color.white);
		passfield.setOpaque(false);
		passfield.setBorder(null);
		add(passfield);
		
        titlebar = GraphicalInterface.load("res/Title_Bar_Swift_Drive.png");
        titlebar.setBounds(400, 70, 600, 100);
        add(titlebar);
        
        background = GraphicalInterface.load("res/login_page.png");
        background.setBounds(0, 0, 1000, 530);
        add(background);
		
		@Ryan
		int fposx = 10;
		int fposy = 10;

		int fwidth = 150;
		int fheight = 25;
		
		dialog = new JDialog();
		dialog.setLocationRelativeTo(null);
		dialog.setTitle("Connection Settings");
		dialog.setSize((fwidth * 3) + 50 + (2 * fposx), fheight + 70 + fposy);
		dialog.setLayout(null);

		hostlabel = new JLabel("Host");
		hostlabel.setSize(100, 50);
		hostlabel.setLocation(fposx, fposy);
		dialog.add(hostlabel);

		hostfield = new JTextField(25);
		hostfield.setText(host);
		hostfield.setSize(fwidth, fheight);
		hostfield.setLocation(fposx, fposy + 35);
		hostfield.requestFocus();
		dialog.add(hostfield);

		portlabel = new JLabel("Port");
		portlabel.setSize(100, 50);
		portlabel.setLocation(fposx + fwidth + 25, fposy);
		dialog.add(portlabel);

		portfield = new JTextField(25);
		portfield.setText("" + port);
		portfield.setSize(fwidth, fheight);
		portfield.setLocation(fposx + fwidth + 25, fposy + 35);
		dialog.add(portfield);

		apply = new JButton("Apply");
		apply.setSize(fwidth, fheight);
		apply.setLocation(fposx + (2 * fwidth) + 50, fposx + 35);
		apply.addActionListener(this);
		dialog.add(apply);
	
		setVisible(true);
	}

	private JLabel getTitlebarLabel() {
		try {
			return new JLabel(new ImageIcon(ImageIO.read(new File("res/Title_Bar_Swift_Drive.png"))));
		}
		catch(IOException ix) {
			return new JLabel("Failed to load background image.");
		}
	}
	
	private JLabel getBackgroundLabel() {
		try {
			return new JLabel(new ImageIcon(ImageIO.read(new File("res/login_page.png"))));
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
		Object src = act.getSource();
		if(src == apply || src == passfield || src == submit) ryAct(act);
	}

	@Ryan
	private void ryAct(ActionEvent act) {
		try {
			Client cli = new Client(host, port, climaster);
			if(cli.login(userfield.getText(), passfield.getPassword())) {
				climaster.setClient(cli);
				next();
			}
			else {
				echo("Incorrect username or password", LOG_PRI);
			}
		}
		catch(DisconnectException dx) {
			echo("Invalid connection settings", LOG_PRI);
		}
	}

	private void next() {
		MenuContainer menu = new MenuContainer(climaster, parent);
		FilePanel file = new FilePanel(menu);
		menu.setPanel(file);
		parent.setPanel(menu);
	}

	public void echo(Object o, int level) {
		print("[ Login  ] " + o.toString() + "\n", level);
	}
}
