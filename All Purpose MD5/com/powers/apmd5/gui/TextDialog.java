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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.powers.apmd5.util.GUIUtil;
import com.swtdesigner.SWTResourceManager;

public class TextDialog extends Dialog {

	private Text text_1;
	protected Object result;
	protected Shell shell;
	String text = null;
	String title = null;
	boolean centered = true;
	
	/**
	 * Create the dialog
	 * @param parent
	 * @param style
	 */
	public TextDialog(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * Create the dialog
	 * @param parent
	 */
	public TextDialog(Shell parent) {
		this(parent, SWT.NONE);
	}

	/**
	 * Open the dialog
	 * @return the result
	 */
	public Object open() {
		createContents();
		
		if(centered){ GUIUtil.center(shell);}
		
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return result;
	}

	/**
	 * Create contents of the dialog
	 */
	protected void createContents() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setLayout(new GridLayout());
		shell.setSize(669, 625);
		shell.setText(title);

		text_1 = new Text(shell, SWT.V_SCROLL | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);
		text_1.setFont(SWTResourceManager.getFont("Courier New", 10, SWT.NONE));
		final GridData gd_text_1 = new GridData(SWT.FILL, SWT.FILL, false, false);
		gd_text_1.heightHint = 532;
		gd_text_1.widthHint = 632;
		text_1.setLayoutData(gd_text_1);

		
		text_1.setText(text);
		final Button okButton = new Button(shell, SWT.NONE);
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				
				shell.close();
				
			}
		});
		final GridData gd_okButton = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		gd_okButton.widthHint = 72;
		okButton.setLayoutData(gd_okButton);
		okButton.setText("OK");
		//
	}

	public void setText(String t){
		this.text = t;
	}
	
	public void setTitle(String title){
		this.title = title;
	}

	public void isCentered(boolean centered){
		this.centered = centered;
	}

}
