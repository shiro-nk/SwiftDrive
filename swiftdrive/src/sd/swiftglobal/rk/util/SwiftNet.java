package sd.swiftglobal.rk.util;

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
public class SwiftNet {
	
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
		public void kill();
		public void setParent(SwiftNetContainer c);
		public SwiftNetContainer getParent();
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
		public void terminate(SwiftNetTool t);
	}
}
