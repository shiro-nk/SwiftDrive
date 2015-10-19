package sd.swiftglobal.rk.type;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.FileException;

public class SwiftFile extends Data implements Settings {
	
	File   file  = null;
	byte[] bytes = null;
	
	boolean bset   = false,
			fset   = false,
			input  = false,
			output = false;
	
	public SwiftFile(int size) {
		super(DAT_FILE, size);
	}

	public void getSocketFile(DataInputStream dis) throws IOException {
		if(dis != null) {
			int    size = dis.readInt();
			String path = dis.readUTF();
			
			file  = new File(path);
			bytes = new byte[size];
			fset  = true;
			bset  = true;
			
			for(int i = 0; i < size; i++) bytes[i] = dis.readByte();
		}
		else throw new IOException("Bad Socket");
	}
	
	public void sendSocketFile(DataOutputStream dos) throws IOException, FileException {
		if(dos != null) {
			if(fset && !bset) read();
			if(bset) for(int i = 0; i < bytes.length; i++) dos.writeByte(bytes[i]);
		}
		else throw new IOException("Bad Socket");
	}
	
	public void read() {
		if(fset && file.exists() && file.isFile()) {
		}
	}
	
	public void write(Path path, boolean append) throws IOException {
		if(bset) {
			
		}
	}
	
	public void write() {
		
	}
	
	public void toData() {

	}

	public void fromData() {

	}
}