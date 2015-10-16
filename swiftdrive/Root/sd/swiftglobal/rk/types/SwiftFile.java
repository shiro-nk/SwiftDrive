package sd.swiftglobal.rk.types;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SwiftFile {
	
	File file = null;
	byte[] fi = null;

	boolean fset = false,
			bset = false;
	
	public SwiftFile(String path) {
		file = new File(path);
		fset = true;
	}
	
	public SwiftFile(Path path) {
		file = path.toFile();
		fset = true;
	}
	
	public SwiftFile(byte[] bytefile) {
		fi = bytefile;
		fset = false;
		bset = true;
	}
	
	public SwiftFile() {
		
	}
	
	public void readFile() {
		if(fset && file.exists() && file.isFile()) {
			try {
				fi = Files.readAllBytes(file.toPath());
				bset = true;
			}
			catch(IOException ix) {
				System.err.println("Failed to read all bytes of file.");
				System.err.println(ix.getMessage());
			}
		}
	}
	
	public void readFrom(String path) {
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
		}
	}
	
	public void writeFile() {
		writeFile(false);
	}
	
	public void writeFile(boolean append) {
		if(fset && bset) {
			try(FileOutputStream fos = new FileOutputStream(file.getPath(), append)) {
				fos.write(fi);
			}
			catch(IOException ix) {
				System.err.println("Error while writing file:");
				System.err.println(ix.getMessage());
			}
		}
	}
	
	public void writeFile(String path, boolean append) {
		if(fset) {
			try(FileOutputStream fos = new FileOutputStream(path, append)) {
				if(!bset) readFile();
			}
			catch(IOException ix) {
				
			}
		}
		else if(bset) {
			
		}
	}
}
