package sd.swiftglobal.rk.type;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.util.Logging;

/* This file is part of Swift Drive				   *
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

/**
 * <b>Data:</b><br>
 * Provides methods for manipulating a stack/array of Strings. <br>
 * This is similar to an array list (dynamic array sizing methods),
 * however, it is built from scratch in order to increase control.
 *
 * @author Ryan Kerr
 */
public abstract class Data implements Settings, Logging {
	private String[] data;
	
	private int readPos = 0;
	
	private static int TYPE_ID;

	/**
	 * <b>To Data:</b><br>
	 * Convert Strings to speicalized type (ex: byte array)
	 */
	public abstract void toData();

	/**
	 * <b>From Data:</b><br>
	 * Convert specialized type (ex: byte array) to Strings
	 */
	public abstract void fromData();

	/**
	 * <b>Convert:</b><br>
	 * Convert another object of type <b>data</b> to another object
	 */
	public abstract void convert(Data dat);

	/**
	 * <b>Initialize Ping:</b><br>
	 * Creates a new data type with identifier <b>type</b> of initial
	 * size of <b>size</b>
	 *
	 * @param type Unique type identifier (verifies two types of data
	 * 				are of the same type.
	 * @param size The initial size of the array;
	 */
	public Data(int type, int size) {
		setTypeID(type);
		data = new String[size];
	}

	/**
	 * <b>Initialize Ping:</b><br>
	 * Creates a new data type with identifier <b>type</b> with an
	 * initial value.
	 *
	 * @param type Unique type idenfier
	 * @param s Initial data array
	 */
	public Data(int type, String... s) {
		setTypeID(type);
		data = s;
	}
	
	/**
	 * <b>Add String:</b><br>
	 * Add a string to the top of the stack
	 *
	 * @param a String to add
	 */
	public void add(String a) {
		String[] swap = new String[data.length + 1];
		for(int i = 0; i < data.length; i++) swap[i] = data[i];
		swap[data.length] = a;
		data = swap;
	}
	
	/**
	 * <b>Push bottom of stack (First in First out):</b><br>
	 * Removes element from bottom (first index) from data array
	 *
	 * @return The first index element
	 */
	public String fifo() {
		String[] swap = new String[data.length - 1];
		String   rtns = data[0];
		for(int i = 1; i < data.length; i++) swap[i - 1] = data[i];
		data = swap;
		return rtns;
	}
	
	/**
	 * <b>Push top of stack (First in Last out):</b>
	 * Removes element from top (last index) from data array
	 *
	 * @return The last index element
	 */
	public String filo() {
		String[] swap = new String[data.length - 1];
		String   rtns = data[data.length - 1];
		for(int i = 0; i < data.length - 1; i++) swap[i] = data[i];
		data = swap;
		return rtns;
	}
	
	/**
	 * <b>Get Next Element in Array:</b><br>
	 * Returns element at pointer index then incrememnts the index.
	 *
	 * @return Element at current index (null string if out of range)
	 */
	public String next() {
		return inRange(readPos) ? data[readPos++] : "";
	}
	
	/**
	 * <b>Reset Position:</b><br>
	 * Resets pointer index to 0
	 */
	public void resetPos() {
		readPos = 0;
	}
	
	/**
	 * <b>Remove:</b><br>
	 * Remove element at index without changing array length
	 *
	 * @param i Array index
	 */
	public void rm(int i) {
		if(inRange(i)) data[i] = null;
	}
	
	/**
	 * <b>Get:</b><br>
	 * Get element at index
	 *
	 * @param i Array index
	 * @return Element at position (null if index out of bounds)
	 */
	public String get(int i) {
		return (inRange(i)) ? data[i] : null;
	}
	
	/**
	 * <b>Set:</b><br>
	 * Set element at index to <b>s</b>
	 *
	 * @param i Array index
	 * @param s Value to set
	 */
	public void set(int i, String s) {
		if(inRange(i)) data[i] = s;
	}
	
	/**
	 * <b>Get First Index of String:</b><br>
	 * Searches the array for a string that matches <b>s</b> and 
	 * returns the first instance of that string.
	 *
	 * @param s String to search for
	 * @return Index of matching element (-1 if no match found)
	 */
	public int getIndex(String s) {
		for(int i = 0; i < data.length; i++) if(data[i].equals(s)) return i;
		return -1;
	}
	
	/**
	 * <b>Set Array:</b><br>
	 * Sets the data array to <b>s</b>
	 *
	 * @param s Array to be set
	 */
	public void setArray(String[] s) {
		reset();
		data = s;
	}
	
	/**
	 * <b>Get Array:</b><br>
	 * @return Array
	 */
	public String[] getArray() {
		return data;
	}
	
	/**
	 * <b>Clear Array:</b><br>
	 * Sets the array to null
	 */
	public void clear() {
		data = new String[0];
	}
	
	/**
	 * <b>Reset:</b><br>
	 * Clears the array and moves pointer index to front
	 */
	public void reset() {
		clear();
		resetPos();
	}
	
	/**
	 * <b>Get Size:</b><br>
	 * @return Array size
	 */
	public int getSize() {
		return data.length;
	}
	
	/**
	 * <b>Out of Bounds Check:</b><br>
	 * Checks if given index is within bounds
	 *
	 * @return True if in range, false if out of bounds
	 */
	private boolean inRange(int i) {
		return (0 <= i && i < data.length) ? true : false;
	}
	
	private static void setTypeID(int id) {
		TYPE_ID = TYPE_ID != DAT_NULL ? TYPE_ID : id;
	}

	public static int getTypeID() {
		return TYPE_ID;
	}
}
