package sd.swiftglobal.rk.types;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.util.Logging;


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
public class SwiftFile extends Data implements Logging {
	
	File file = null;
	byte[] fi = null;

	//TODO: Fix Update Logic for BSET and FSET
	boolean fset = false,
			bset = false;

	public SwiftFile(File f) throws FileException {
		file = f;
		fset = true;
		
		readFile();
	}

	public SwiftFile(String path) {
		file = new File(path);
		fset = true;
	}
	
	public SwiftFile(Path path) {
		file = path.toFile();
		fset = true;
	}

	public SwiftFile(byte[] bytefile) {
		if(bytefile.length < Integer.MAX_VALUE) {
			fi = bytefile;
			fset = false;
			bset = true;
		}
	}

	public SwiftFile(String path, int size, DataInputStream in) throws FileException {
		getSocketFile(path, size, in);
	}
	
	public void readFile() throws FileException {
		if(fset && file.exists() && file.isFile()) {
			System.out.println("Reading file from " + file.getPath());
			try {
				if(Integer.MAX_VALUE <= file.length()) throw new IOException("File too large");
				fi = Files.readAllBytes(file.toPath());
				bset = true;
			}
			catch(IOException ix) {
				error("Error reading bytes from " + file.getPath());
				error(ix.getMessage());
				bset = false;
				throw new FileException(ix.getMessage());
			}
		}
		else throw new FileException("404 File Not Found");
	}
	
	public void readFrom(String path) throws FileException {
		try {
			File in = new File(path);
			
			if(in.exists()) {
				fi   = Files.readAllBytes(file.toPath());
				bset = true;
				toData();
			}
		}
		catch(IOException ix) {
			error("Error reading from: " + path);
			error(ix.getMessage());
			throw new FileException(ix.getMessage());
		}
	}
	
	private void write(String path, boolean readFirst, boolean append) throws IOException {
		try(FileOutputStream fos = new FileOutputStream(path, append)) {
			if(readFirst) 
				try { readFile(); }
				catch(FileException fx) { throw new IOException(fx.getMessage()); }
			if(fset && bset) fos.write(fi);
			else throw new IOException("Missing information");
		}
	}
	
	public void writeFile() throws FileException {
		writeFile(false);
	}
	
	public void writeFile(boolean append) throws FileException {
		if(fset && bset) {
			try {
				write(file.getPath(), false, append);
			}
			catch(IOException ix) {
				error("Writing to " + file.getPath());
				error(ix.getMessage());
				throw new FileException(ix.getMessage());
			}
		}
	}
	
	public void writeFile(String path, boolean append) throws FileException {
		if(fset) {
			try {
				write(path, bset ? false : true, append);
			}
			catch(IOException ix) {
				error("Failed to write to " + path + " from " + file.getPath());
				error(ix.getMessage());
				throw new FileException(ix.getMessage());
			}
		}
		else throw new FileException("No file specified!");
	}
	
	public int getFileSize() {
		return (int) (bset ? fi.length : fset ? file.length() : 0);
	}
	
	public void getSocketFile(String path, int size, DataInputStream dis) throws FileException {
		try {
			file = new File(path);
			fset = true;
			
			fi = new byte[size];

			echo("Reading file of " + size + "bytse");
			for(int i = 0; i < size; i++) {
				fi[i] = dis.readByte();
				echo(fi[i], LOG_SEC);
			}
			
			bset = true;
		}
		catch(IOException ix) {
			error("Error while reading bytes from socket");
			error(ix.getMessage());
			throw new FileException(ix.getMessage());
		}
	}
	
	public void sendSocketFile(DataOutputStream dos) {
		
	}
	
	public void setBytes(byte[] fbytes) {
		bset = true;
		fi = fbytes;
	}
	
	public byte[] getBytes() {
		return fi;
	}
	
	public void toData() {
		ArrayList<String> build = new ArrayList<String>();
		
		if(!bset) {
			try {
				readFile();
			}
			catch(FileException fx) {
				error(fx.getMessage());
			}
		}
			
		if(bset) {
			String   convert = new String(fi, CHARSET);
			String[] broken  = convert.split("\n");
			
			for(String f : broken) build.add(f);
			
			setData(build);
		}
	}
	
	public void fromData() {
		String build = "";
		for(String s : getData()) build += s;
		fi = build.getBytes(CHARSET);
	}
}
