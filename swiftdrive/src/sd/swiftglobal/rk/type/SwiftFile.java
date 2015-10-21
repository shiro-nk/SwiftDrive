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
import sd.swiftglobal.rk.util.Logging;

/* This file is part of Swift Drive				   *
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

/**
 * 
 * @author Ryan Kerr
 */
public class SwiftFile extends Data implements Settings, Logging {
	
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
	
	public SwiftFile(byte[] b) {
		super(DAT_FILE, 0);
		bytes = b;
		checkByteFlag();
		toData();
	}
	
	public SwiftFile(Path p, boolean read) throws IOException, FileException {
		this(p.toFile(), read);
	}
	
	public SwiftFile(String path, boolean read) throws IOException, FileException {
		this(new File(path), read);
	}

	public SwiftFile(File f, boolean read) throws IOException, FileException {
		this();
		setFile(f, read);
	}
	
	public SwiftFile(String[] string) {
		this();
		for(String s : string) add(s);
		fromData();
	}
	
	public SwiftFile(DataInputStream dis) throws IOException, FileException {
		this();
		receive(dis);
	}
	
	public void receive(DataInputStream dis) throws IOException {
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
	
	public void send(DataOutputStream dos) throws IOException, FileException {
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
			File parent = path.toFile().getParentFile();
			if((!parent.exists() || !parent.isDirectory()) && !parent.isFile())
				if(!parent.mkdirs()) throw new IOException("Failed to make parent directories");
			
			try(FileOutputStream fos = new FileOutputStream(path.toString(), append)) {
				fos.write(bytes);
			}
		}
		else throw new FileException(EXC_MISS);
	}
	
	public void write() throws IOException, FileException {
		if(fset) write(file.toPath(), false);
		else throw new FileException(EXC_MISS);
	}
	
	public void append() throws IOException, FileException {
		if(fset) write(file.toPath(), true);
		else throw new FileException(EXC_MISS);
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
			String   split = new String(bytes);
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
	
	public void setFile(Path p, boolean read) throws IOException, FileException {
		setFile(p.toFile(), read);
	}
	
	public void setFile(String path, boolean read) throws IOException, FileException {
		setFile(new File(path));
	}
	
	public void setFile(File f, boolean read) throws IOException, FileException {
		setFile(f);
		if(read) read();
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
	
	public boolean getByteFlag() {
		return bset;
	}
	
	public boolean getFileFlag() {
		return fset;
	}
}