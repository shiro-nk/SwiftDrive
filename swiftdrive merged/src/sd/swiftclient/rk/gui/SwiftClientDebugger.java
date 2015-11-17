package sd.swiftclient.rk.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import sd.swiftclient.rk.net.Client;
import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.SwiftException;
import sd.swiftglobal.rk.gui.SwiftContainer;
import sd.swiftglobal.rk.gui.SwiftPanel;
import sd.swiftglobal.rk.type.Generic;
import sd.swiftglobal.rk.type.ServerCommand;

/* This file is part of Swift Drive				  *
 * Copyright (C) Ryan Kerr 2015					  *
 * Please refer to <http://www.gnu.org/licenses/> */

public class SwiftClientDebugger extends JPanel implements SwiftPanel, Settings, ActionListener {
	public static final long serialVersionUID = 1l;
	private SwiftContainer parent;
	private Client client;

	private JTextField toserver,
					   fromserver;

	private JLabel in,
				   out;

	private JButton submit;

	public SwiftClientDebugger(SwiftContainer parent, Client client) {
		this.client = client;
		this.parent = parent;
		setSize(GUI_PANEL_WIDTH, GUI_PANEL_HEIGHT);
		setLayout(null);

		int fheight = 25,
			fwidth  = 200,
			fposx   = 50,
			fposy   = 50,
			lwidth  = 60,
			bwidth  = 100,
			fspace  = 15;

		in = new JLabel("Output: ");
		in.setLocation(fposx, fposy);
		in.setSize(lwidth, fheight);
		add(in);

		toserver = new JTextField(25);
		toserver.setLocation(fposx + lwidth + 10, fposy);
		toserver.setSize(fwidth, fheight);
		add(toserver);
		
		out = new JLabel("Input: ");
		out.setLocation(fposx, fposy + fheight + fspace);
		out.setSize(lwidth, fheight);
		add(out);

		fromserver = new JTextField(25);
		fromserver.setLocation(fposx + lwidth + 10, fposy + fheight + fspace);
		fromserver.setSize(fwidth, fheight);
		fromserver.setEnabled(false);
		add(fromserver);

		submit = new JButton("Submit");
		submit.setLocation(fposx + lwidth + fwidth + fspace, fposy);
		submit.setSize(bwidth, fheight);
		submit.addActionListener(this);
		add(submit);
	}

	public JPanel getPanel() {
		return this;
	}

	public void setParent(SwiftContainer parent) {
		this.parent = parent;
	}

	public SwiftContainer getSwiftParent() {
		return parent;
	}
		
	public void actionPerformed(ActionEvent act) {
		Object src = act.getSource();

		if(src == submit) {
			Generic gen = new Generic();
			try {
				gen.add(toserver.getText());
				client.scmd(new ServerCommand(CMD_WRITE_DATA, LC_PATH + "output"), gen);
				gen.reset();
				client.scmd(new ServerCommand(CMD_SEND_DATA, ""), gen, DAT_DATA);
				fromserver.setText(gen.next());
			}
			catch(SwiftException sfx) {
				sfx.printStackTrace();
			}
		}
	}
}
