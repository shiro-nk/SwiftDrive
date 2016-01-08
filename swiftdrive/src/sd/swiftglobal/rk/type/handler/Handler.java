package sd.swiftglobal.rk.type.handler;

import java.util.ArrayList;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.util.SwiftFront;

/* This file is part of Swift Drive                * 
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

/**
 * <b>Handler / File + Text Format Object Array:</b><br>
 * Handles the reading and writing of <b>HandleType</b> compatible
 * objects to and from A file. This is similar to <b>Data</b> by
 * providing a way to add, remove, get, set, and find elements in an
 * array. However, rather than strings, <b>Handlers</b> works with
 * <b>HandleTypes</b> and is centered around file I/O.
 *
 * @author Ryan Kerr
 */
public abstract class Handler<Type extends HandleType> implements Settings {
	private ArrayList<Type> list = new ArrayList<Type>();
	protected SwiftFront source;

	/**
	 * <b>Reload file into memory:</b><br>
	 * Run any functions required to load and parse strings from a file
	 * 
	 * @throws FileException When the file can't be read
	 */
	public abstract void reload() throws FileException;

	/**
	 * <b>Convert Strings into Objects:</b><br>
	 * Converts lines from a file into objects
	 *
	 * @throws FileException When the file can't be read
	 */
	public abstract void convert(String[] data) throws FileException;

	/**
	 * <b>Get Array:</b><br>
	 * @return Array of <b>Handle Types</b>
	 */
	public abstract Type[] getArray();

	/**
	 * <b>Add Element:</b><br>
	 * Adds <b>type</b> to the array as long as the no other element
	 * shares the same identifier. 
	 *
	 * @param type Element to add to the array
	 * @return True if added, false if already exists
	 * @throws FileException if writing to the file has failed
	 */
	public boolean add(Type type) throws FileException {
		boolean rtn = true;
		for(Type t : list) {
			if(type.getName().equals(t.getName())) {
				rtn = false;
				break;
			}
		}
		
		if(rtn) {
			list.add(type);
			resetIndex();
			write();
		}
		return rtn;
	}

	/**
	 * <b>Get Index of Element:</b><br>
	 * Searches array for a matching identifier and returns the index.
	 *
	 * @param name Element identifier
	 * @return Index of element; -1 if no match found
	 */
	public int getIndex(String name) {
		for(int i = 0; i < list.size(); i++)
			if(name.equals(list.get(i).getName())) return i;
		return -1;
	}

	/**
	 * <b>Remove Element at Index:</b><br>
	 * Removes the element at index <b>id</b> as long as <b>id</b> is
	 * within array bounds.
	 *
	 * @param id Index to be removed
	 * @throws FileException if writing to the file has failed
	 */
	public void remove(int id) throws FileException {
		if(0 <= id && id < list.size()) list.remove(id);
		write();
	}

	/**
	 * <b>Remove Element:</b><br>
	 * Gets the index of the element and removes it.
	 *
	 * @param t Element to be removed
	 * @throws FileException if writing to the file has failed
	 */
	public void remove(Type t) throws FileException {
		remove(getIndex(t.getName()));
	}

	 /** @return File used to store array elements **/
	public SwiftFront getSource() {
		return source;
	}

	/**
	 * <b>Set Source File:</b><br>
	 * Sets the source file to <b>file</b>
	 *
	 * @param file File to use for storage/retrieval of arrays
	 */
	public void setSource(SwiftFront file) {
		source = file;
	}

	/**
	 * <b>Get Element By String Identifier:</b><br>
	 * 
	 * @param reference Element String Identifier
	 * @return Element with matching identifier; null if no match found
	 */
	public Type get(String reference) {
		int index = getIndex(reference);
		if(index < 0) return null;
		else return list.get(index);
	}

	/**
	 * <b>Write Array to File:</b><br>
	 * Writes the string-formatted objects to the given source file. <br>
	 * This function will delete files if they are empty.
	 *
	 * @throws FileException If writing or deleting the file has failed
	 */
	public void write() throws FileException {
		source.reset();
		for(Type t : list) source.add(t.toString() + "\n");
		resetIndex();
		source.toData();
		if(0 < source.getArray().length) source.write();
		else source.getFile().delete();
	}

	/** @return The underlying ArrayList **/
	public ArrayList<Type> getList() {
		return list;
	}

	/**
	 * <b>Set List:</b><br>
	 * Set the array list to a new list. The previous list will be
	 * deleted and any elements with the same identifer will be
	 * removed.
	 *
	 * @param array ArrayList to use
	 */
	public void setList(Type[] array) {
		list.clear();
		for(Type t : array) list.add(t);
	}

	/**
	 * <b>Reset Array Index Identifiers:</b><br>
	 * Sets all index-based identifiers to their current array indexes.
	 */
	protected void resetIndex() {
		for(int i = 0; i < list.size(); i++) list.get(i).setID(i);
	}

	/**
	 * <b>Read File:</b><br>
	 * Reads the source file (if the source file exists) and converts
	 * (which should initialize) the parsed data.
	 *
	 * @throws FileException if the file could not be read properly
	 */
	public void read() throws FileException {
		if(source.exists()) {
			source.read();
			source.fromData();
		}
		if(source.getArray().length != 0) convert(source.getArray());
	}
}
