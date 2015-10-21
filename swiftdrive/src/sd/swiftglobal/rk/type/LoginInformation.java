package sd.swiftglobal.rk.type;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/* This file is part of Swift Drive				   *
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

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
		fromData();
		return username;
	}
	
	public byte[] getPassword() {
		fromData();
		return password;
	}
	
	public void toData() {
		
	}

	public void fromData() {
		
	}
	
	public void convert(Data dat) {
		
	}
}