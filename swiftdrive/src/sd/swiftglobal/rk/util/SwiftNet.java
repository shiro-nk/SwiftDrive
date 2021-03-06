package sd.swiftglobal.rk.util;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.type.users.User;

/* This file is part of Swift Drive				   *
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

/**
 * Identifies classes that are part of the network <br>
 * and classes that control the network class <br><br>
 * 
 * These interfaces allow for the controlled destruction <br>
 * of network interfaces
 *
 * @author Ryan Kerr
 */
public class SwiftNet implements Settings {
	
	/**
	 * Any classes that control network interaction (ServerConnection, Client)
	 * @author Ryan Kerr
	 */
	public interface SwiftNetTool {
		/**
		 * 
		 * @return Identifier for 
		 */
		public int getID();
		public void setParent(SwiftNetContainer c);
		public SwiftNetContainer getParent();
		public User getUser();
		public int getErrID();
		
		public default void kill(int errid) {
			getParent().dereference(this);
		}

		public default void kill() {
			kill(EXC_UNKN);
		}
	}
	
	/**
	 * Any classes that control access to network classes
	 * @author Ryan Kerr
	 */
	public interface SwiftNetContainer {
		/**
		 * Removes the SwiftNetTool from assignment. <br>
		 * This prevents methods from being called after the resources is closed.
		 * @param t 
		 */
		public void dereference(SwiftNetTool t);
		public default void terminate(SwiftNetTool t) {
			t.kill();
		}
	}
}
