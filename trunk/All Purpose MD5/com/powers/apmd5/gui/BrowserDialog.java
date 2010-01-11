package com.powers.apmd5.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import com.swtdesigner.SWTResourceManager;

public class BrowserDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	private String url;
	private Browser browser;
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public BrowserDialog(Shell parent, int style, final String url) {
		super(parent, SWT.SHELL_TRIM | SWT.BORDER);
		this.url = url;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		
		browser.setUrl(url);
		
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
		shell = new Shell(getParent(), getStyle());
		shell.setImage(SWTResourceManager.getImage(BrowserDialog.class, "/images/world.png"));
		shell.setSize(1024, 600);
		shell.setText("Browser");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		browser = new Browser(shell, SWT.NONE);

	}
}
