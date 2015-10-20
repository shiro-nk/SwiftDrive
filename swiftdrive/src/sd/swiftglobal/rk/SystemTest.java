package sd.swiftglobal.rk;

import sd.swiftglobal.rk.type.Data;

public class SystemTest implements Settings {
	public static void main(String[] args) {
		new SystemTest();
	}
	
	public SystemTest() {
		Data dat = new Data(DAT_DATA, 0) {
			public void toData() {
				
			}

			public void fromData() {
				
			}

			public void convert(Data dat) {
				
			}
		};
		
		System.out.println("Start");
		
		for(int i = 0; i != 10; i++) {
			dat.add("This is " + i);
			System.out.println(dat.getSize());
		}
		
		for(int i = 0; i != 10; i++) {
			System.out.println(dat.next());
		}
		
		for(int i = 0; i != 10; i++) {
			System.out.println(dat.filo());
		}
		
		System.out.println("END");
	}
}
