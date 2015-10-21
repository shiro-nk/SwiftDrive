package sd.swiftglobal.rk.type;

import java.io.IOException;

/* This file is part of Swift Drive				   *
 * Copyright (C) 2015 Ryan Kerr                    *
 * Please refer to <http://www.gnu.org/licenses/>. */

public interface SpecialNetworkType {
	public void send() 	  throws IOException;
	public void receive() throws IOException;
}
