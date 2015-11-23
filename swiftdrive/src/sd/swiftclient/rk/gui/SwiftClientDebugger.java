package sd.swiftclient.rk.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;

import sd.swiftclient.rk.net.Client;
import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.CommandException;
import sd.swiftglobal.rk.expt.DisconnectException;
import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.expt.SwiftException;
import sd.swiftglobal.rk.gui.GraphicalInterface.SwiftContainer;
import sd.swiftglobal.rk.gui.GraphicalInterface.SwiftPanel;
import sd.swiftglobal.rk.type.Generic;
import sd.swiftglobal.rk.type.ServerCommand;
import sd.swiftglobal.rk.type.SwiftFile;

/* This file is part of Swift Drive				  *
 * Copyright (C) 2015 Ryan Kerr					  *
 * Please refer to <http://www.gnu.org/licenses/> */

public class SwiftClientDebugger extends JPanel implements SwiftPanel, Settings, ActionListener {
	public static final long serialVersionUID = 1l;
	private SwiftContainer parent;
	private Client client;

	private JTextField toserver,
					   filetoserver;

	private JTextArea fromserver;
	private JButton submit,
					browse;
	private JLabel in,
				   out,
				   upl;


	public SwiftClientDebugger(SwiftContainer parent, Client client) {
		this.client = client;
		this.parent = parent;
		setSize(GUI_PANEL_WIDTH, GUI_PANEL_HEIGHT);
		setLayout(null);

		int fheight = 25,
			fwidth  = 300,
			sheight = 5 * fheight,
			fposx   = 50,
			fposy   = 50,
			lwidth  = 60,
			bwidth  = 100,
			fspace  = 15;

		in = new JLabel("Server: ");
		in.setLocation(fposx, fposy);
		in.setSize(lwidth, fheight);
		add(in);

		fromserver = new JTextArea();
		fromserver.setEnabled(false);

		JScrollPane scroll = new JScrollPane(fromserver);
		scroll.setSize(fwidth, sheight);
		scroll.setLocation(fposx + lwidth + 10, fposy);
		add(scroll);

		toserver = new JTextField(25);
		toserver.setLocation(fposx + lwidth + 10, fposy + sheight + fspace);
		toserver.setSize(fwidth, fheight);
		add(toserver);
		
		out = new JLabel("Client: ");
		out.setLocation(fposx, fposy + sheight + fspace);
		out.setSize(lwidth, fheight);
		add(out);

		filetoserver = new JTextField(25);
		filetoserver.setLocation(fposx + lwidth + 10, fposy + fheight + sheight + fspace);
		filetoserver.setSize(fwidth, fheight);
		add(filetoserver);

		upl = new JLabel("File: ");
		upl.setLocation(fposx, fposy + sheight + fheight + fspace);
		upl.setSize(lwidth, fheight);
		add(upl);

		browse = new JButton("Upload");
		browse.setLocation(fposx + lwidth + fwidth + fspace, fposy + fheight + sheight + fspace);
		browse.setSize(bwidth, fheight);
		browse.addActionListener(this);
		add(browse);

		submit = new JButton("Send");
		submit.setLocation(fposx + lwidth + fwidth + fspace, fposy + sheight + fspace);
		submit.setSize(bwidth, fheight);
		submit.addActionListener(this);
		add(submit);
	}

	public JPanel getPanel() {
		return this;
	}

	public void setParentContainer(SwiftContainer parent) {
		this.parent = parent;
	}

	public SwiftContainer getParentContainer() {
		return parent;
	}
		
	public void actionPerformed(ActionEvent act) {
		Object src = act.getSource();

		if(src == submit) {
			Generic gen = new Generic();
			try {
				gen.add(toserver.getText());
				client.scmd(new ServerCommand(CMD_WRITE_DATA, "output.txt"), gen);
				gen.reset();
				client.scmd(new ServerCommand(CMD_SEND_DATA, ""), gen, DAT_DATA);
				fromserver.append(gen.next() + "\n");
			}
			catch(SwiftException sfx) {
				parent.setPanel(new SwiftPanel() {
					private JPanel panel = new JPanel();
					public JPanel getPanel() {
						setSize(GUI_PANEL_WIDTH, GUI_PANEL_HEIGHT);
						setBackground(Color.RED);
						return panel;
					}

					public void setParentContainer(SwiftContainer parent) {
					
					}

					public SwiftContainer getParentContainer() {
						return null;
					}
				});
			}
		}

		if(src == browse) {
			File f = new File(LC_PATH + filetoserver.getText());
			if(f.exists() && f.isFile()) {
				SwiftFile file;
				try {
					file = new SwiftFile(f, true);
					client.sfcmd(new ServerCommand(CMD_WRITE_FILE, "outputfile.txt"), file);
					filetoserver.setForeground(Color.WHITE);
					filetoserver.setText("Client: File sent!");
					new Timer(1000, new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							filetoserver.setText("");
						}
					});
				}
				catch(IOException | DisconnectException | CommandException x) {
					System.err.println("[ Debug  ] A non-fatal error occured when sending the file");
					System.out.println("[ Debug  ] Failed in non-essential debug class");
					x.printStackTrace();
					System.exit(100);
				}
				catch(FileException fx) {
					System.err.println("[ Debug  ] Bad file");
					filetoserver.setForeground(Color.RED);
				}
			}
			else {
				System.err.println("[ Debug  ] File not found");
			}
		}
	}
}
