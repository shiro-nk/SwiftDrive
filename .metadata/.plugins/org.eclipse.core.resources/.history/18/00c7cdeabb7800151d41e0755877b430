package sd.swiftglobal.rk.util;

/* This file is part of Swift Drive				   *
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

public class SwiftNet {
	public interface SwiftNetTool {
		public int getID();
		public void kill();
		public void setParent(SwiftNetContainer c);
		public SwiftNetContainer getParent();
	}
	
	public interface SwiftNetContainer {
		public void terminate(SwiftNetTool t);
	}
}
