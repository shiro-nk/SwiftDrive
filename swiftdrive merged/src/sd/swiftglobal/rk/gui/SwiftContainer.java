package sd.swiftglobal.rk.gui;

import sd.swiftglobal.rk.util.SwiftNet.SwiftNetContainer;

/* This file is part of Swift Drive				 *
 * Copyright (C) 2015 Ryan Kerr					 *
 * Please refer to <http://www.gnu.org/licenses> */

public interface SwiftContainer {
	public void setPanel(SwiftPanel panel);
	public SwiftNetContainer getNetContainer();
}
