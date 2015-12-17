package sd.swiftglobal.rk.type.handler;

import java.util.ArrayList;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.util.SwiftFront;

/* This file is part of Swift Drive                * 
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

public abstract class Handler<Type extends HandleType> implements Settings {
	private ArrayList<Type> list = new ArrayList<Type>();
	protected SwiftFront source;

	public abstract void reload() throws FileException;
	public abstract void convert(String[] data) throws FileException;
	public abstract Type[] getArray();

	public boolean add(Type type) throws FileException {
		boolean rtn = true;
		for(Type t : list) {
			if(type.getName().equals(t.getName())) {
				rtn = false;
				break;
			}
		}
		
		if(rtn) {
			System.out.println(type);
			list.add(type);
			resetIndex();
			write();
		}
		return rtn;
	}

	public int getIndex(String name) {
		for(int i = 0; i < list.size(); i++) {
			if(name.equals(list.get(i).getName())) return i;
		}
		return -1;
	}

	public void remove(int id) throws FileException {
		if(0 <= id && id < list.size()) list.remove(id);
		write();
	}

	public void remove(Type t) throws FileException {
		remove(getIndex(t.getName()));
	}

	public SwiftFront getSource() {
		return source;
	}

	public void setSource(SwiftFront file) {
		source = file;
	}

	public Type get(String reference) {
		int index = getIndex(reference);
		if(index < 0) return null;
		else return list.get(index);
	}

	public void write() throws FileException {
		source.reset();
		for(Type t : list) source.add(t.toString() + "\n");
		resetIndex();
		source.toData();
		if(0 < source.getArray().length) source.write();
		else source.getFile().delete();
	}

	public ArrayList<Type> getList() {
		return list;
	}

	public void setList(Type[] array) {
		list.clear();
		System.out.println(array);
		for(Type t : array) System.out.println(t);
		for(Type t : array) list.add(t);
	}

	protected void resetIndex() {
		for(int i = 0; i < list.size(); i++) list.get(i).setID(i);
	}

	public void read() throws FileException {
		if(source.exists()) {
			source.read();
			source.fromData();
		}
		if(source.getArray().length != 0) convert(source.getArray());
	}
}
