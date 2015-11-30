package sd.swiftglobal.rk.type.vctrl;

import java.io.File;
import java.util.ArrayList;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.type.handler.Handler;
import sd.swiftglobal.rk.util.SwiftFront;

/* This file is part of Swift Drive			  *
 * Copyright (c) 2015 Ryan Kerr				  *
 * Please refer to <http://gnu.org/licenses/> */

public class VersionHandler extends Handler<Version> implements Settings {
	
	public VersionHandler() throws FileException {
		File path = new File(LC_VERS + "index");
		if(path.exists() && path.isFile()) {
			setSource(new SwiftFront(path, false));
			read();
		}
		else if(!path.exists()) {
			setSource(new SwiftFront(path, false));
		}
	}

	@Override
	protected void convert(String[] data) {
		ArrayList<Version> list = getList();
		list.clear();

		for(int i = 0; i < data.length; i++) {
			Version ver = new Version(data[i]);
			ver.setID(i);
			list.add(ver);
		}
	}

	@Override
	protected Version[] getArray() {
		return getList().toArray(new Version[getList().size()]);
	}
}
