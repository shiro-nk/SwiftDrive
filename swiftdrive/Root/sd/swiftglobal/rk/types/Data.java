package sd.swiftglobal.rk.types;

import java.util.ArrayList;

import sd.swiftglobal.rk.Settings;

/*
 * This file is part of Swift Drive
 * Copyright (C) 2015 Ryan Kerr
 *
 * Swift Drive is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * Swift Drive is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of  MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Swift Drive. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * 
 * @author Ryan Kerr
 */
public abstract class Data implements Settings {
	private static int DATA_TYPE_ID;

	private int readPos = 0;
	
	protected ArrayList<String> data = null; 
	
	public abstract void toData();
	public abstract void fromData();
	
	private Data() {
		setTypeID(DAT_DATA);
	}
	
	public Data(String... info) {
		this();
		setData(info);
	}
	
	public Data(ArrayList<String> list) {
		this();
		data = list;
	}
	
	public void setData(String... info) {
		data = new ArrayList<String>();
		for(String s : info) data.add(s);
	}
	
	public void setData(ArrayList<String> list) {
		data = list;
	}
	
	public ArrayList<String> getData() {
		return data;
	}
	
	public String next() {
		return readPos <= data.size() ? data.get(readPos++) : "";
	}
	
	public void add(String info) {
		data.add(info);
	}
	
	public int getSize() {
		return data.size();
	}

	public static void setTypeID(int tid) {
		DATA_TYPE_ID = tid != DAT_DATA ? DATA_TYPE_ID : tid;
	}

	public static int getTypeID() {
		return DATA_TYPE_ID;
	}
}
