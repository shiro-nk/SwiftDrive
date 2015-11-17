package sd.swiftglobal.rk.gui;

import javax.swing.JPanel;

/* This file is part of Swift Drive				 *
 * Copyright (C) 2015 Ryan Kerr					 *
 * Please refer to <http://www.gnu.org/licenses> */

public interface SwiftPanel {
	public JPanel getPanel();
	public void setParent(SwiftContainer parent);
	public SwiftContainer getSwiftParent();
}
