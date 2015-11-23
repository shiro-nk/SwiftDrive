package sd.swiftglobal.rk.gui;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sd.swiftglobal.rk.util.SwiftNet.SwiftNetContainer;

/* This file is part of Swift Drive			  *
 * Copyright (c) 2015 Ryan Kerr				  *
 * Please refer to <http://gnu.org/licenses/> */

public class GraphicalInterface {
	public interface SwiftMaster {
		public void setContainer(SwiftContainer c);
		public SwiftContainer getContainer();

		public void setNetContainer(SwiftNetContainer c);
		public SwiftNetContainer getNetContainer();
	}

	public interface SwiftContainer {
		public void setPanel(SwiftPanel p);
		public SwiftPanel getCurrentPanel();

		public void setMaster(SwiftMaster m);
		public SwiftMaster getMaster();
	}

	public interface SwiftPanel {
		public void setParentContainer(SwiftContainer c);
		public SwiftContainer getParentContainer();

		public abstract JPanel getPanel();
	}

	public static JLabel load(String path) {
		try {
			return new JLabel(getIcon(path));
		}
		catch(IOException ix) {
			return new JLabel("Failed to load image");
		}
	}

	public static ImageIcon getIcon(String path) throws IOException {
		return new ImageIcon(
			ImageIO.read(
				Thread.currentThread().getContextClassLoader().getResourceAsStream(path)
			)
		);
	}
}
