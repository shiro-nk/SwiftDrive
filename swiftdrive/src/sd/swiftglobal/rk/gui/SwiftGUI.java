package sd.swiftglobal.rk.gui;

import javax.swing.JPanel;

import sd.swiftglobal.rk.util.SwiftNet.SwiftNetContainer;

/* This file is part of Swift Drive				 *
 * Copyright (C) 2015 Ryan Kerr					 *
 * Please refer to <http://www.gnu.org/licenses> */

public class SwiftGUI {
	public interface SwiftMaster {
		public void setContainer(SwiftContainer c);
		public SwiftContainer getContainer();
		public void setNetContainer(SwiftNetContainer s);
		public SwiftNetContainer getNetContainer();
	}
	
	public interface SwiftContainer {
		public void setPanel(SwiftPanel panel);
		public SwiftPanel getCurrentPanel();
		public SwiftContainer getParentContainer();
		public SwiftMaster getMaster();
	}

	public interface SwiftPanel {
		public JPanel getPanel();
		public void setParentContainer(SwiftContainer parent);
		public SwiftContainer getParentContainer();
	}
}
