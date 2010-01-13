/*

All Purpose MD5 is a simple, fast, and easy-to-use GUI for calculating and testing MD5s.
Copyright (C) 2006  Nick Powers

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
or go to http://www.gnu.org/licenses/gpl.html.

*/
package com.powers.apmd5.util;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * <b>Bare Bones Browser Launch for Java</b><br>
 * Utility class to open a web page from a Swing application
 * in the user's default browser.<br>
 * Supports: Mac OS X, GNU/Linux, Unix, Windows XP/Vista<br>
 * Example Usage:<code><br> &nbsp; &nbsp;
 *    String url = "http://www.google.com/";<br> &nbsp; &nbsp;
 *    BareBonesBrowserLaunch.openURL(url);<br></code>
 * Latest Version: <a href="http://www.centerkey.com/java/browser/">www.centerkey.com/java/browser</a><br>
 * Author: Dem Pilafian<br>
 * Public Domain Software -- Free to Use as You Like
 * @version 2.0, May 26, 2009
 * Original Source: http://www.centerkey.com/java/browser/myapp/real/BareBonesBrowserLaunch.java
 */
public class BareBonesBrowserLaunch {
	static final String[] browsers = { "firefox", "opera", "konqueror",
			"epiphany", "seamonkey", "galeon", "kazehakase", "mozilla",
			"netscape" };

	/**
	 * Opens the specified web page in a web browser
	 * 
	 * @param url
	 *            A web address (URL) of a web page (ex:
	 *            "http://www.google.com/")
	 */
	public static void openURL(String url) throws Exception {
		String osName = System.getProperty("os.name");
		if (osName.startsWith("Mac OS")) {
			Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
			Method openURL = fileMgr.getDeclaredMethod("openURL",
					new Class[] { String.class });
			openURL.invoke(null, new Object[] { url });
		} else if (osName.startsWith("Windows"))
			Runtime.getRuntime().exec(
					"rundll32 url.dll,FileProtocolHandler " + url);
		else { // assume Unix or Linux
			boolean found = false;
			for (String browser : browsers)
				if (!found) {
					found = Runtime.getRuntime().exec(
							new String[] { "which", browser }).waitFor() == 0;
					if (found)
						Runtime.getRuntime()
								.exec(new String[] { browser, url });
				}
			if (!found)
				throw new Exception(Arrays.toString(browsers));
		}
	}

}
