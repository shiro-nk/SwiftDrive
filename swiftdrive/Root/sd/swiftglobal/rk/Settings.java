package sd.swiftglobal.rk;

public interface Settings {
	
	public static final int DAT_OFF  = 0,
							DAT_PING = 1,
							DAT_FILE = 2,
							DAT_DATA = 3;
	
	public static final String LC_DIV  = System.getProperty("file.separator"),
							   LC_USER = System.getProperty("user.home"),
							   LC_PATH = LC_USER + LC_DIV + "swift" + LC_DIV;

	public static final int DEF_PORT = 3141;
}
