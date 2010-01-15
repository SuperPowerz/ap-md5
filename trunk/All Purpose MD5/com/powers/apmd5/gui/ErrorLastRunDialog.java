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

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.powers.apmd5.util.BareBonesBrowserLaunch;
import com.powers.apmd5.util.GUIUtil;
import com.powers.apmd5.util.StringUtil;
import com.swtdesigner.SWTResourceManager;

public class ErrorLastRunDialog extends Dialog {

	private static final String FINISH = "Finish";
	protected Object result;
	protected Shell shell;
	private Text stacktraceText;
	private final Shell parent;
	private final String stacktraceTextStr;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ErrorLastRunDialog(Shell parent, int style, String stacktraceTextStr) {
		super(parent, style);
		this.parent = parent;
		setText("Error Last Run");
		this.stacktraceTextStr = stacktraceTextStr;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		
		stacktraceText.setText(StringUtil.defaultString(stacktraceTextStr));
		
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM);
		shell.setImage(SWTResourceManager.getImage(ErrorLastRunDialog.class, "/images/mail.png"));
		shell.setSize(450, 459);
		shell.setText(getText());
		
		Label lblIEncounteredAn = new Label(shell, SWT.WRAP);
		lblIEncounteredAn.setBounds(10, 10, 424, 140);
		lblIEncounteredAn.setText("I encountered an issue during my last run. In order to make this program better, you have the option of entering an issue for this Open Source project. If you wish to enter the issue, please click Submit Issue and I'll launch your web browser. If not, then I'll leave you alone.\r\n\r\nThe following is the information you'll need in order to create an issue:\r\n\r\nTemplate: Defect report from user\r\nSummary: APMD5 Runtime Error\r\nDescription: (copy and paste the stack trace below)\r\n");
		
		stacktraceText = new Text(shell, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		stacktraceText.setBounds(10, 156, 424, 152);
		
		Label lblStatusNewOwner = new Label(shell, SWT.NONE);
		lblStatusNewOwner.setBounds(10, 343, 424, 30);
		lblStatusNewOwner.setText("Status: New\r\nOwner: (your google username)\r\n\r\n\r\nNow choose, Submit Issue.");
		
		Button copyButton = new Button(shell, SWT.NONE);
		copyButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				GUIUtil.copyToClipboard(parent.getDisplay(), stacktraceText.getText());
			}
		});
		copyButton.setBounds(10, 314, 68, 23);
		copyButton.setText("Copy");
		
		final Button launchBrowserButton = new Button(shell, SWT.NONE);
		launchBrowserButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if(FINISH.equals(launchBrowserButton.getText())){
					shell.close();
				} else {
					try {
						BareBonesBrowserLaunch.openURL(MD5Constants.ISSUE_URL);
						launchBrowserButton.setText(FINISH);
					} catch (Exception e1) {
						MessageBox mb = new MessageBox(parent);
						mb.setText("Failure To Launch");
						mb.setMessage("Unable to launch browser, please launch your browser manuallly");
						mb.open();
						e1.printStackTrace();
					}
				}
			}
		});
		launchBrowserButton.setBounds(117, 394, 87, 23);
		launchBrowserButton.setText("Launch Browser");
		
		Button noThanks = new Button(shell, SWT.NONE);
		noThanks.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
		noThanks.setBounds(210, 394, 87, 23);
		noThanks.setText("No Thanks");

	}
}
