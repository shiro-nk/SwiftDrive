package sd.swiftglobal.rk.type.handler;

/* This file is part of Swift Drive				  *
 * Copyright (C) 2015 Ryan Kerr					  *
 * Please refer to <http://www.gnu.org/licenses/> */

public abstract class HandleType {
	public abstract void setID();
	public abstract void getID();
	public abstract void toData();
	public abstract String toString();
}
