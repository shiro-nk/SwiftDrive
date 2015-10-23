package sd.swiftglobal.rk.type;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.DisconnectException;
import sd.swiftglobal.rk.util.Logging;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetTool;

/* This file is part of Swift Drive				   *
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

/**
 * 
 * @author Ryan Kerr
 */
public abstract class Data implements Settings, Logging {
	private String[] data;
	
	private int readPos = 0;
	
	private static int TYPE_ID;

	public abstract void toData();
	public abstract void fromData();
	public abstract void convert(Data dat);
	
	public Data(int type, int size) {
		setTypeID(type);
		data = new String[size];
	}
	
	public Data(int type, String... s) {
		data = s;
	}
	
	public void add(String a) {
		String[] swap = new String[data.length + 1];
		for(int i = 0; i < data.length; i++) swap[i] = data[i];
		swap[data.length] = a;
		data = swap;
	}
	
	public String fifo() {
		String[] swap = new String[data.length - 1];
		String   rtns = data[0];
		for(int i = 1; i < data.length; i++) swap[i - 1] = data[i];
		data = swap;
		return rtns;
	}
	
	public String filo() {
		String[] swap = new String[data.length - 1];
		String   rtns = data[data.length - 1];
		for(int i = 0; i < data.length - 1; i++) swap[i] = data[i];
		data = swap;
		return rtns;
	}
	
	public Data receive(Data template, SwiftNetTool tool, DataInputStream dis) throws DisconnectException {
		if(tool != null) {
			template.reset();
			try {
				int size = dis.readInt();
				for(int i = 0; i < size; i++) template.add(dis.readUTF());
				return template;
			}
			catch(IOException ix) {
				tool.kill();
				throw new DisconnectException(EXC_NREAD, ix);
			}
		}
		return null;
	}
	
	public <Type extends Data> void send(Type data, SwiftNetTool tool, DataOutputStream dos) throws DisconnectException {
		if(tool != null) {
			try {
				dos.writeInt(Type.getTypeID());
				dos.writeInt(data.getSize());
				for(String s : data.getArray()) dos.writeUTF(s);
			}
			catch(IOException ix) {
				tool.kill();
				throw new DisconnectException(EXC_CONN, ix);
			}
		}
	}
	
	public String next() {
		return inRange(readPos) ? data[readPos++] : "";
	}
	
	public void resetPos() {
		readPos = 0;
	}
	
	public void rm(int i) {
		if(inRange(i)) data[i] = null;
	}
	
	public String get(int i) {
		return (inRange(i)) ? data[i] : null;
	}
	
	public void set(int i, String s) {
		if(inRange(i)) data[i] = s;
	}
	
	public int getIndex(String s) {
		for(int i = 0; i < data.length; i++) if(data[i].equals(s)) return i;
		return -1;
	}
	
	public void setArray(String[] s) {
		data = s;
	}
	
	public String[] getArray() {
		return data;
	}
	
	public void clear() {
		data = new String[0];
	}
	
	public void reset() {
		clear();
		resetPos();
	}
	
	public int getSize() {
		return data.length;
	}
	
	private boolean inRange(int i) {
		return (0 <= i && i < data.length) ? true : false;
	}
	
	private static void setTypeID(int id) {
		TYPE_ID = id != DAT_DATA ? TYPE_ID : id;
	}

	public static int getTypeID() {
		return TYPE_ID;
	}
}
