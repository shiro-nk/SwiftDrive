package sd.swiftglobal.rk.type;

import sd.swiftglobal.rk.Settings;

public abstract class Data implements Settings {
	private String[] data;
	
	private int readPos = 0;
	
	private static int TYPE_ID;

	public abstract void toData();
	public abstract void fromData();
	
	public Data(int type, int size) {
		setTypeID(type);
		data = new String[size];
	}
	
	public Data(int type, String... s) {
		data = s;
	}
	
	public void add(String a) {
		String[] swap = new String[data.length];
		for(int i = 0; i < data.length; i++) swap[i] = data[i];
		swap[data.length - 1] = a;
		data = swap;
	}
	
	public String fifo() {
		String[] swap = new String[data.length - 1];
		String   rtns = data[0];
		for(int i = 1; i < data.length; i++) swap[i] = data[i];
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
	
	public int getSize() {
		return data.length;
	}
	
	private boolean inRange(int i) {
		return (0 < i && i < data.length) ? true : false;
	}
	
	private static void setTypeID(int id) {
		TYPE_ID = id != DAT_DATA ? TYPE_ID : id;
	}

	public static int getTypeID() {
		return TYPE_ID;
	}
}