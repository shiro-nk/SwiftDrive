package sd.swiftclient.rk.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import sd.swiftclient.SwiftClient;
import sd.swiftclient.rk.net.Client;
import sd.swiftglobal.rk.Meta.LeaveBlank;
import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.DisconnectException;
import sd.swiftglobal.rk.gui.SwiftGreeter;
import sd.swiftglobal.rk.gui.SwiftGUI.SwiftContainer;
import sd.swiftglobal.rk.gui.SwiftGUI.SwiftPanel;
import sd.swiftglobal.rk.gui.SwiftScreen;

/* This file is part of Swift Drive				 *
 * Copyright (C) 2015 Ryan Kerr					 *
 * Please refer to <http://www.gnu.org/licenses> */

public class SwiftLogin extends JPanel implements Settings, SwiftPanel, ActionListener {
	public static final long serialVersionUID = 1l;
	private SwiftScreen screen;
	private SwiftClient clientHandler;
	private JDialog dialog;
	private JTextField userfield,
					   hostfield,
					   portfield;
	private JPasswordField passfield;
	private JLabel login,
				   inform,
				   hostlabel,
				   portlabel;
	private JButton submit,
					settings,
					apply;

	private String hostname = "127.0.0.1";
	private int port = DEF_PORT;

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
		
		login = new JLabel("Login");
		login.setFont(new Font("Sans", Font.BOLD, 75));
		login.setLocation(100, -125);
		login.setSize(500, 500);
		add(login);
		
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

		inform = new JLabel("");
		inform.setLocation(fposx, fposy + (2 * fheight) + 25);
		inform.setSize(fwidth * 3, fheight);
		inform.setForeground(Color.RED);
		add(inform);
		
		fposx = 10;
		fposy = 10;

		fwidth = 150;
		fheight = 25;
		
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
		hostfield.setText(hostname);
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

		screen.focus(userfield);
	}

	public void inform(String msg) {
		inform.setText(msg);
		inform.repaint();
	}

	//TODO DEBUG
	public void next() {
		SwiftClientMenu menu = new SwiftClientMenu(screen);
		SwiftClientDebugger scd = new SwiftClientDebugger(menu, clientHandler.getClient());
		menu.setPanel(scd);
		screen.setPanel(menu);
	}

	public void next(int i) {
		SwiftClientMenu menu = new SwiftClientMenu(screen);
		SwiftGreeter greeter = new SwiftGreeter(menu);
		menu.setPanel(greeter);
		screen.setPanel(menu);
	}

	public JPanel getPanel() {
		return this;
	}

	@LeaveBlank
	public void setParentContainer(SwiftContainer parent) {
	
	}

	public SwiftContainer getParentContainer() {
		return screen;
	}

	public void actionPerformed(ActionEvent ae) {
		Object src = ae.getSource();

		if(src == userfield) passfield.requestFocus();
	
		if(src == passfield || src == submit) {
			try {
				Client cli = new Client(hostname, port, clientHandler);
				Thread.sleep(500);
				if(cli.login(userfield.getText(), passfield.getPassword())) {
					screen.getMaster().getNetContainer().setTool(cli);
					next();
				}
				else {
					inform("Incorrect username or password");
				}
			}
			catch(InterruptedException | DisconnectException ex) {
				inform("Invalid connection settings");
			}
		}

		if(src == settings) dialog.setVisible(true);

		if(src == apply) {
			hostname = hostfield.getText().trim();
			port = Integer.parseInt(portfield.getText().replaceAll("[^0-9]", "").trim());
			dialog.setVisible(false);
		}
	}
}
