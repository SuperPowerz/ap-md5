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

package com.powers.apmd5.gui;

import java.io.BufferedReader;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.powers.apmd5.util.SimpleIO;
import com.swtdesigner.SWTResourceManager;


public class MD5ReadPanel {

	private StyledText readMeStyledText = null;
	protected Shell shell;
	private Display display = null;


	/**
	 * Open the window
	 */
	public void open() {
		display = Display.getDefault();
		createContents();
		
		setTextFromFile(MD5Constants.READ_ME_FILE);
		
		shell.open();
		shell.layout();
		start();
	}
	
	public void open(String filename) {
		display = Display.getDefault();
		createContents();
		
		setTextFromFile(filename);
		
		shell.open();
		shell.layout();
		start();
	}
	
	public void start(){
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	/**
	 * Create contents of the window
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setImage(SWTResourceManager.getImage(APMD5.class, "/images/apmd5.ico"));
		shell.setLayout(new FillLayout());
		shell.setSize(766, 526);

		final Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new FillLayout());

		readMeStyledText = new StyledText(composite, SWT.V_SCROLL | SWT.BORDER | SWT.H_SCROLL);
		readMeStyledText.setEditable(false);
		readMeStyledText.setFont(SWTResourceManager.getFont("Courier New", 10, SWT.NONE));
		//
	}
	
	public void setText(String text){
	}
	
	public void appendText(String text){
	}
	
	public void setTextFromFile(String filename){
		BufferedReader br = SimpleIO.openFileForInput(filename);
		String line = null;
		
		try {
			if(br != null){
				while((line = br.readLine()) != null){
					readMeStyledText.append(line + "\n");
				}
			} else {
				readMeStyledText.setText("Oops, loading of file " + filename +" failed");
			}
			
		} catch (IOException e) {
			return;
		} finally {
			SimpleIO.close(br);
		}
	}

}
