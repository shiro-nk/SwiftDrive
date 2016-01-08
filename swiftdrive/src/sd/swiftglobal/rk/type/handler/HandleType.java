package sd.swiftglobal.rk.type.handler;

/* This file is part of Swift Drive				  *
 * Copyright (C) 2015 Ryan Kerr					  *
 * Please refer to <http://www.gnu.org/licenses/> */

/**
 * <b>HandleType Interface:</b><br>
 * This interface outlines the requirements for a data type to be
 * compatible with a corresponding <b>Handler</b> data type.
 *
 * @author Ryan Kerr
 */
public interface HandleType {

	/**
	 * <b>Secondary Constructor:</b><br>
	 * Allows for an object to be given all their information (using
	 * their raw filedata) since java does not allow for the construction
	 * of a generic type (which <b>Handler</b> uses for <b>HandleType</b>)
	 *
	 * @param id Index in the <b>Handler's</b> array
	 * @param data Raw file data to be parsed into object data
	 */
	public void setInfo(int id, String data);

	/**
	 * <b>Set Index Identifier:</b><br>
	 * Set the index of this object in the array
	 *
	 * @param id New index of object in <b>Handler</b> object
	 */
	public void setID(int id);

	/**
	 * <b>Get Index Identifier:</b><br>
	 * Get the index of this object in the array
	 *
	 * @return Index of object in <b>Handler</b> array
	 */
	public int getID();

	/**
	 * <b>Convert to Object Data:</b><br>
	 * Converts the raw filedata into useable object data.
	 */
	public void toData();

	/**
	 * <b>Convert to String:</b><br>
	 * Converts object data into file-friendly data
	 */
	public String toString();

	/**
	 * <b>Get String Identifier:</b><br>
	 * Get the identifier of the object
	 *
	 * @return String identifier
	 */
	public String getName();
}
