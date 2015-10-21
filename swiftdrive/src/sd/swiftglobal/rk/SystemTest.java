package sd.swiftglobal.rk;

import java.io.File;
import java.io.IOException;

import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.type.SwiftFile;

public class SystemTest implements Settings {
	public static void main(String[] args) {
		new SystemTest();
	}
	
	public SystemTest() {
		String msg = "Hello world\nMy name is I. Montoya\nYou killed my father\nPrepare to die";
		byte[] byt = msg.getBytes();
		
		System.out.println(new String(byt));
		
		SwiftFile file = new SwiftFile(byt);
		
		for(byte b : file.getBytes()) System.out.println(b);
		
		for(String s : file.getArray()) {
			System.out.println(s);
		}
		
		try {
			file.write(new File(LC_PATH + "testoutpt").toPath(), true);
		}
		catch(IOException ix) {
			
		} catch (FileException e) {
		
		}
	}
}
