package sd.swiftglobal.rk.type.handler;

import java.io.File;
import java.util.ArrayList;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.type.SwiftFile;
import sd.swiftglobal.rk.util.SwiftFront;

/* This file is part of Swift Drive                * 
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

public abstract class Handler<Type extends HandleType> implements Settings {
	private ArrayList<Type> list = new ArrayList<Type>();
	private SwiftFront source;

	public abstract int getIndex(String reference);
	public abstract boolean add(Type type) throws FileException;
	public abstract void remove(Type type) throws FileException;
	public abstract void read();

	public Handler(String path) {
		try {
			source = new SwiftFront(new File(LC_PATH + path), false);
			read();
		}
		catch(FileException fx) {
			fx.printStackTrace();
		}
	}

	public void remove(int id) throws FileException {
		if(0 <= id && id < list.size()) list.remove(id);
		write();
	}

	public SwiftFile getSource() {
		return source;
	}

	public Type get(String reference) {
		int index = getIndex(reference);
		if(index < 0) return null;
		else return list.get(index);
	}

	public void write() throws FileException {
		source.reset();
		for(Type t : list) source.add(t.toString());
		source.toData();
		source.write();
	}
}
