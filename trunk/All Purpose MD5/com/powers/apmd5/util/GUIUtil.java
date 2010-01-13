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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.swtdesigner.SWTResourceManager;

public class GUIUtil {

	public static void setImage(final Label l, final String url){
		l.setImage(SWTResourceManager.getImage(GUIUtil.class, url));
		l.setVisible(true);
	}

	public static String getStackTrace(Throwable aThrowable) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		printWriter.close();
		return result.toString();
	}

	public static void copyToClipboard(final Display display, final String s){
		if(StringUtil.isBlank(s)){ return; }
		Clipboard clipboard = new Clipboard(display);
        TextTransfer textTransfer = TextTransfer.getInstance();
        clipboard.setContents(new String[]{s}, new Transfer[]{textTransfer});
        clipboard.dispose();
	}
	
	public static String getFromClipboard(final Display display){
		Clipboard clipboard = new Clipboard(display);
        String text = (String)clipboard.getContents(TextTransfer.getInstance());
        clipboard.dispose();
        return StringUtil.isNotEmpty(text) ? text : StringUtil.EMPTY_STRING;
	}
	
	public static void setEnabled(final Display display, final Control control, final boolean enable){
		display.syncExec(new Runnable() {
			public void run() {
				control.setEnabled(enable);
			}
		});
		
	}
	
	public static void displayMessageBox(final Shell parent, final int style, final String message, final String title){
		parent.getDisplay().syncExec(new Runnable() {
			public void run() {
				MessageBox mb = new MessageBox(parent, style);
				mb.setMessage(message);
				mb.setText(title);
				mb.open();
			}
		});
	}
}
