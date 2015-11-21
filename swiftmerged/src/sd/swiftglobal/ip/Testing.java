package sd.swiftglobal.ip;

import java.io.IOException;


import sd.swiftglobal.rk.Settings;
import sd.swiftglobal.rk.expt.FileException;
import sd.swiftglobal.rk.type.SwiftFile;

public class Testing implements Settings {

	public static void main(String[] args) {
		
	try {
			SwiftFile file = new SwiftFile(LC_PATH + "sample.txt", true);
			String[] data = file.getArray();
			for(String s : data) System.out.print(s);
		} catch (FileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(IOException ix) {
			
		}
		
		
		

	}

}
