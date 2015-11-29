package sd.swiftglobal.rk.type.handler;

/* This file is part of Swift Drive				  *
 * Copyright (C) 2015 Ryan Kerr					  *
 * Please refer to <http://www.gnu.org/licenses/> */

public interface HandleType {
	public void setInfo(int id, String data);
	public void setID(int id);
	public int getID();
	public void toData();
	public String toString();
	public String getName();
}
