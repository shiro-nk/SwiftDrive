package sd.swiftglobal.rk.types;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import sd.swiftglobal.rk.expt.FileException;

public class SwiftFile extends Data {
	
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
		try {
			file = new File(path);
			fi = new byte[size];
			
			
			System.out.println("Reading file of " + size + "bytse");
			for(byte b : fi) {
				b = in.readByte();
				System.out.println(b);
			}
			
			bset = true;
		}
		catch(IOException ix) {
			System.err.println("Error while reading bytes from socket");
			System.err.println(ix.getMessage());
			throw new FileException(ix.getMessage());
		}
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
				System.err.println("Error reading bytes from " + file.getPath());
				System.err.println(ix.getMessage());
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
			}
		}
		catch(IOException ix) {
			System.err.println("Error reading from: " + path);
			System.err.println(ix.getMessage());
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
				System.err.println("Writing to " + file.getPath());
				System.err.println(ix.getMessage());
				throw new FileException(ix.getMessage());
			}
		}
	}
	
	public void writeFile(String path, boolean append) throws FileException {
		if(fset) {
			try {
				write(path, true, append);
			}
			catch(IOException ix) {
				System.err.println("Failed to write to " + path + " from " + file.getPath());
				System.err.println(ix.getMessage());
				throw new FileException(ix.getMessage());
			}
		}
		else throw new FileException("No file specified!");
	}
	
	public int getFileSize() {
		return (int) (bset ? fi.length : fset ? file.length() : 0);
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
				System.err.println(fx.getMessage());
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
