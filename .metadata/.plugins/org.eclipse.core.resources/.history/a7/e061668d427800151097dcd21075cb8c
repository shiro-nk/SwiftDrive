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

		byte[] bx = { 00, 15, 25, 67, 80, 80, 70, 56 };
		SwiftFile ax = new SwiftFile(bx);
		ax.setFile(new File(LC_PATH + "output4"));
		
		String[] cx = {
				"Dear Compiler,",
				//"Sometimes I feel you ignore my comments",
				"this message is a test message to test the swift file write functionality!",
				"Sincerely, Plaintext Java Code"
		};
		SwiftFile zx = new SwiftFile(cx);
		zx.setFile(new File(LC_PATH + "output3"));
		
		try {
			SwiftFile nx = new SwiftFile(new File(LC_PATH + "output").toPath(), false);
			nx.setBytes(new byte[]{0});
			nx.write();
		} 
		catch (FileException | IOException e2) {
			e2.printStackTrace();
			System.err.println(e2.getMessage());
		}
		
		try {
			SwiftFile fx = new SwiftFile(LC_PATH + "input", true);
			SwiftFile rx = new SwiftFile(fx.getBytes());
			
			for(String s : fx.getArray()) System.out.println(s);
			for(byte   b : rx.getBytes()) System.out.println(b);
			rx.setFile(new File(LC_PATH + "output2"));
			rx.write();
		} 
		catch (FileException | IOException e1) {
			e1.printStackTrace();
			System.err.println(e1.getMessage());
		}
		
		try {
			file.write(new File(LC_PATH + "testoutpt").toPath(), true);
			ax.write();
			zx.append();
		}
		catch(IOException ix) {
			
		} catch (FileException e) {
		
		}
	}
}
