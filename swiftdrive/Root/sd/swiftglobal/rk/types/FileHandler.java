package sd.swiftglobal.rk.types;

import java.io.File;
import java.nio.file.Path;

@SuppressWarnings("unused")
public class FileHandler {
	
	File file = null;
	byte[] fi = null;

	public FileHandler(String path) {
		file = new File(path);
	}
	
	public FileHandler(Path path) {
		file = path.toFile();
	}
	
	public FileHandler(byte[] bytefile) {
		fi = bytefile;
	}
	
	public FileHandler() {
		
	}
	
	private void readfile() {
		
	}
	
	private void writefile() {
		
	}
}
