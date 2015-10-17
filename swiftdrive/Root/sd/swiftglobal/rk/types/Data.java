package sd.swiftglobal.rk.types;

import java.util.ArrayList;

import sd.swiftglobal.rk.Settings;

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
