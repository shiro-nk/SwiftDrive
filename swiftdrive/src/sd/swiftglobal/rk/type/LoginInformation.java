package sd.swiftglobal.rk.type;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
public class LoginInformation extends Data {

	private String username = "";
	private byte[] password = new byte[0];
	
	public LoginInformation(String username, String password) {
		super(DAT_LGIN, username, password);
	}
	
	public LoginInformation(Data dat) {
		super(DAT_LGIN);
		convert(dat);
	}
	
	public void send(DataInputStream dis) throws IOException {
		if(dis != null) {
			int size = dis.readInt();
			username = dis.readUTF();
			password = new byte[size];
			for(int i = 0; i < size; i++) password[i] = dis.readByte();
		}
		else throw new IOException("Bad Socket");
	}
	
	public void receive(DataOutputStream dos) throws IOException {
		if(dos != null) {
			dos.writeInt(password.length);
			dos.writeUTF(username);
			for(byte b : password) dos.writeByte(b);
		}
		else throw new IOException("Bad Socket");
	}
	
	public String getUsername() {
		return username;
	}
	
	public byte[] getPassword() {
		return password;
	}
	
	public void toData() {
		
	}

	public void fromData() {
		
	}
	
	public void convert(Data dat) {
		
	}
}
