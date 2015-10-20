package sd.swiftglobal.rk.type;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.FileException;

/* This file is part of Swift Drive				   *
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

/**
 * 
 * @author Ryan Kerr
 */
public class SwiftFile extends Data implements Settings {
	
	private File   file  = null;
	private byte[] bytes = null;
	
	private boolean bset   = false,
					fset   = false;
	
	private SwiftFile() {
		super(DAT_FILE, 0);
	}
	
	public SwiftFile(int size) {
		super(DAT_FILE, size);
	}
	
	public SwiftFile(File f) throws IOException, FileException {
		this();
		file = f;
		checkFileFlag();
		read();
	}
	
	public SwiftFile(DataInputStream dis) throws IOException, FileException {
		this();
		getSocketFile(dis);
	}
	
	public void getSocketFile(DataInputStream dis) throws IOException {
		if(dis != null) {
			int    size = dis.readInt();
			String path = dis.readUTF();
			file  = new File(path);
			bytes = new byte[size];
			for(int i = 0; i < size; i++) bytes[i] = dis.readByte();
			checkFileFlag();
			checkByteFlag();
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
	
	public void read() throws IOException, FileException {
		if(fset && file.exists() && file.isFile()) {
			if(file.length() < Integer.MAX_VALUE) {
				bytes = Files.readAllBytes(file.toPath());
				checkByteFlag();
				toData();
			}
			else throw new FileException(EXC_SIZE);
		}
		else if(!fset) throw new FileException(EXC_MISS);
		else throw new FileException(EXC_F404);
	}
	
	public void write(Path path, boolean append) throws IOException, FileException {
		if(bset) {
			try(FileOutputStream fos = new FileOutputStream(path.toString())) {
				fos.write(bytes);
			}
		}
		else throw new FileException(EXC_MISS);
	}
	
	public void write() throws IOException, FileException {
		if(fset) write(file.toPath(), false);
	}
	
	public void writeFromData(boolean print) throws IOException, FileException {
		if(print && fset) {
			if(0 < getSize()) {
				try(PrintWriter writer = new PrintWriter(new FileWriter(file, false))) {
					for(String s : getArray()) writer.println(s);
					writer.flush();
				}
			}
			else throw new FileException(EXC_MISS);
		}
		else {
			fromData();
			if(bset) write(); 
			else throw new FileException(EXC_MISS);
		}
	}
	
	public void toData() {
		if(bset) {
			String   split = bytes.toString();
			String[] build = split.split("\n");
			setArray(build);
		}
	}
	
	public void fromData() {
		String build = "";
		for(String s : getArray()) build += s;
		bytes = build.getBytes(CHARSET);
		checkByteFlag();
	}
	
	public void convert(Data dat) {
		
	}
	
	public void setBytes(byte[] b) {
		bytes = b;
		checkByteFlag();
	}
	
	public byte[] getBytes() {
		return bytes;
	}
	
	public void setFile(File f) {
		file = f;
		checkFileFlag();
	}
	
	public File getFile() {
		return file;
	}
	
	private void checkByteFlag() {
		bset = bytes == null ? false : 0 < bytes.length ? true : false;
	}
	
	private void checkFileFlag() {
		fset = file != null ? true : false;
	}
}