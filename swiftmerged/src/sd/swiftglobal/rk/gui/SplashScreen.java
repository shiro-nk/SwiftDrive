package sd.swiftglobal.rk.gui;

import java.awt.Color;
import java.io.Closeable;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;

/* This file is part of Swift Drive			 *
 * Copyright (c) 2015 Ryan Kerr				 *
 * Please refer to <http://gnu.org/licenses> */

public class SplashScreen implements Closeable {
	private JDialog screen;
	private JLabel splash;

	public SplashScreen() {
		screen = new JDialog();
		screen.setSize(400, 275);
		screen.setLocationRelativeTo(null);
		screen.setUndecorated(true);
		screen.setBackground(new Color(0, 0, 0, 0));
		screen.toFront();
	
		splash = getBackground();
		splash.setSize(400, 275);
		splash.setLocation(0, 0);
		screen.add(splash);

		screen.setVisible(true);
	}
	
	public void pause() {
		try {
			Thread.sleep(3000);
		}
		catch(InterruptedException ix) {

		}
	}

	public void close() {
		screen.setVisible(false);
		screen.dispose();
	}

	private JLabel getBackground() {
		try {
			return new JLabel(
				new ImageIcon(
					ImageIO.read(
						Thread.currentThread().getContextClassLoader()
						.getResourceAsStream("res/splash.png")
					)
				)
			);
		}
		catch(IOException ix) {
			return new JLabel();
		}
	}
}
