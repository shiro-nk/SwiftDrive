package sd.swiftglobal.rk.gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import sd.swiftclient.SwiftClient;
import sd.swiftclient.rk.net.Client;
import sd.swiftglobal.rk.Meta.LeaveBlank;
import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.DisconnectException;

/* This file is part of Swift Drive				 *
 * Copyright (C) 2015 Ryan Kerr					 *
 * Please refer to <http://www.gnu.org/licenses> */

public class SwiftLogin extends JPanel implements Settings, SwiftPanel, ActionListener {
	public static final long serialVersionUID = 1l;
	private SwiftScreen screen;
	private SwiftClient clientHandler;
	private JTextField userfield;
	private JPasswordField passfield;
	private JLabel login;
	private JButton submit,
					settings; 

	public SwiftLogin(SwiftScreen screen, SwiftClient net) {
		int fheight = 40,
			fwidth  = 400,
			fposx   = 100,
			fposy   = 200;
		
		this.screen = screen;
		clientHandler = net;
		setSize(GUI_FRAME_BORDER, GUI_FRAME_HEIGHT);
		setLocation(0, 0);
		setLayout(null);
		
		userfield = new JTextField(25);
		userfield.setLocation(fposx, fposy);
		userfield.setSize(fwidth, fheight);
		userfield.addActionListener(this);
		userfield.requestFocus();
		add(userfield);

		passfield = new JPasswordField(25);
		passfield.setLocation(fposx, fposy + fheight + 25);
		passfield.setSize(fwidth, fheight);
		passfield.addActionListener(this);
		add(passfield);

		login = new JLabel("Login");
		login.setFont(new Font("Sans", Font.BOLD, 75));
		login.setLocation(100, -125);
		login.setSize(500, 500);
		add(login);
	
		settings = new JButton("Settings");
		settings.setLocation(fposx + fwidth + 25, fposy);
		settings.setSize(160, fheight);
		settings.addActionListener(this);
		add(settings);

		submit = new JButton("Submit");
		submit.setLocation(fposx + fwidth + 25, fposy + fheight + 25); 
		submit.setSize(160, fheight);
		submit.addActionListener(this);
		add(submit);
	}

	public void settings() {
		
	}

	//TODO DEBUG
	public void next() {
		SwiftMenu menu = new SwiftMenu(screen);
		SwiftClientDebugger scd = new SwiftClientDebugger(menu, clientHandler.getClient());
		menu.setPanel(scd);
		screen.setPanel(menu);
	}

	public void next(int i) {
		SwiftMenu menu = new SwiftMenu(screen);
		SwiftGreeter greeter = new SwiftGreeter(menu);
		menu.setPanel(greeter);
		screen.setPanel(menu);
	}

	public JPanel getPanel() {
		return this;
	}

	@LeaveBlank
	public void setParent(SwiftContainer parent) {
	
	}

	public SwiftContainer getSwiftParent() {
		return screen;
	}

	public void actionPerformed(ActionEvent ae) {
		Object src = ae.getSource();

		if(src == userfield) passfield.requestFocus();
	
		if(src == passfield || src == submit) {
			try {
				Client cli = new Client("192.168.5.94", 3141, clientHandler);
				Thread.sleep(500);
				screen.setNetTool(cli);
				clientHandler.setClient(cli);
				if(cli.login(userfield.getText(), passfield.getPassword())) 
					next();
			}
			catch(InterruptedException | DisconnectException ex) {
				ex.printStackTrace();
			}
		}
	}
}
