package sd.swiftglobal.rk;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/* This file is part of Swift Drive                * 
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

/**
 * <b>Metadata Tags:</b><br>
 * I wrote this class just to help me keep track of changes and 
 * code functionality while writing the <b>Client</b> class
 *
 * @author Ryan Kerr
 */
public interface Meta {
	/**
	 * Modification Notice: The following method or line has been <br>
	 * modified by the specified author.
	 */
	@Deprecated
	public @interface Modified {
		public String name = "Ryan Kerr",
					  date = "";
	}
	
	/* The @name methods below are to be used in replacement <br>
	 * of the @Modified() tag. These tags should be used in instances <br>
	 * where the code written does not belong to the author of the class.
	 * <br><br>
	 * For example, if Isaiah writes a method in Mohan's GUI class, <br>
	 * he will tag his method with @Isaiah to mark that he wrote that method.
	 */

	/**
	 * The following code is written by Ryan Kerr.
	 */
	public @interface Ryan {

	}

	/**
	 * The following code is written by Mohan Pan
	 */
	public @interface Mohan {
		
	}

	/**
	 * The following code is written by Isaiah Power
	 */
	public @interface Isaiah {

	}

	/**
	 * This method should only be called by a method that suspends <br>
	 * the active ping utility. 
	 */
	@Target(ElementType.METHOD)
	public @interface RequiresPingHandler {
		
	}
	
	/**
	 * This method suspends the ping utility to allow for <br>
	 * the transfer of data over a socket without interruption
	 */
	@Target(ElementType.METHOD)
	public @interface PingHandler {
		
	}
	
	/**
	 * The following method calls a method which uses a timeout <br>
	 * utility. This method may result in a connection being killed
	 */
	@Target(ElementType.METHOD)
	public @interface IndirectTimeout {
		
	}
	
	/**
	 * The following method uses the timeout utility. <br>
	 * Calling this method may result in a connection being killed
	 */
	@Target(ElementType.METHOD)
	public @interface DirectTimeout {
		
	}
	
	/**
	 * The following method has been left blank intentionally: <br>
	 * Do not write anything inside this method
	 */
	@Target(ElementType.METHOD)
	public @interface LeaveBlank {
		
	}
	
	/**
	 *  The following method is capable of terminating connections <br>
	 *  directly in the event of an exception
	 */
	@Target(ElementType.METHOD)
	public @interface DirectKiller {
		
	}
	
	/**
	 * The following method calls a method that has the ability <br>
	 * of terminating a connection in the event of an exception
	 */
	@Target(ElementType.METHOD) @Deprecated
	public @interface IndirectKiller {
		
	}
	
	/**
	 * This parameter will be written over and will <br>
	 * be returned as a new variable. <br> <br>
	 * (This is used for the Data type to allow generic typing)
	 */
	@Target(ElementType.PARAMETER)
	public @interface Pointer {

	}
	
	/**
	 * This class has been intentionally left blank to reserve <br>
	 * a more specific name space. Similar to C typedef operator <br><br>
	 * (This is used to make generic types more specific)
	 */
	@Target(ElementType.TYPE) 
	public @interface Typedef {
		String value();
	}
}
