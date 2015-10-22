package sd.swiftglobal.rk.util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.type.SwiftFile;

/* This file is part of Swift Drive				   *
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

/**
 * This class provides an IOException free implementation
 * for the SwiftFile class. Also allows for destroying the
 * object using the Try-With-Resources statement
 *
 * (This code is redundant, but it may help elsewhere)
 * (Please ignore this file...)
 *
 * @author Ryan Kerr
 */
public class SwiftFront extends SwiftFile implements Settings, Logging, Closeable {
	private SwiftFront() {
		super();
	}
	
	public SwiftFront(SwiftFile sfile) {
		this();
		setFile(sfile.getFile());
		setBytes(sfile.getBytes());
		setArray(sfile.getArray());
		checkFileFlag();
		checkByteFlag();
		if(!getByteFlag()) fromData();
	}
	
	public SwiftFront(int size) {
		super(size);
	}
	
	public SwiftFront(byte[] b) {
		super(b);
	}
	
	public SwiftFront(String[] string) {
		super(string);
	}
	
	public SwiftFront(File f, boolean read) throws FileException {
		super();
		setFile(f, read);
	}
	
	public SwiftFront(Path p, boolean read) throws FileException {
		super();
		setFile(p, read);
	}
	
	public void write(Path path, boolean append) throws FileException {
		try					  { super.write(path, append); }
		catch(IOException ix) { exc(EXC_WRITE, ix);        }
	}
	
	public void write() throws FileException {
		try                   { super.write();		}
		catch(IOException ix) {	exc(EXC_WRITE, ix); }
	}

	public void write(Path path) throws FileException {
		try					  { super.write(path, false);  }
		catch(IOException ix) { exc(EXC_WRITE, ix);        }		
	}
	
	public void append() throws FileException {
		try 				  { super.append();     }
		catch(IOException ix) { exc(EXC_WRITE, ix); }
	}
	
	public void append(Path path) throws FileException {
		write(path, true);
	}
	
	public void save() throws FileException {
		try                   { super.writeFromData(false);  }
		catch(IOException ix) { exc(EXC_WRITE, ix);          }
	}
	
	public void read() throws FileException {
		try 				  { super.read();      }
		catch(IOException ix) {	exc(EXC_READ, ix); }
	}
	
	public void setFile(File f, boolean read) throws FileException {
		try					  { super.setFile(f, read); }
		catch(IOException ix) { exc(EXC_READ, ix);      }
	}
	
	public void setFile(String path, boolean read) throws FileException {
		try 				  { super.setFile(path, read); }
		catch(IOException ix) { exc(EXC_READ, ix);         }
	}
	
	public void setFile(Path path, boolean read) throws FileException {
		try 				  { super.setFile(path, read); }
		catch(IOException ix) { exc(EXC_READ, ix);         }
	}
	
	private void exc(int ex, IOException ix) throws FileException {
		error("Swift Front Error:" + new FileException(ex).getMessage());
		throw new FileException(ex, ix);
	}
	
	public void send() {}
	public void close() {}
	public void receive() {}
}
