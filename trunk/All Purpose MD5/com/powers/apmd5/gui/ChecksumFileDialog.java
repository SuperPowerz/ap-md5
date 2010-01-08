package com.powers.apmd5.gui;

import static com.powers.apmd5.util.MD5Constants.DEFAULT_DIRECTORY;
import static com.powers.apmd5.util.MD5Constants.PROPERTIES_FILE;
import static com.powers.apmd5.util.MD5Constants.RECURSE_DIRECTORY;
import static com.powers.apmd5.util.MD5Constants.getFilePath;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.powers.apmd5.util.FileLogger;
import com.powers.apmd5.util.MD5Constants;
import com.powers.apmd5.util.ScreenLogger;
import com.swtdesigner.SWTResourceManager;

public class ChecksumFileDialog {
	private StyledText defaultDirectoryStyledText;
	protected Shell shell;
	private Display display = null;

	private ScreenLogger sLogger = null;
	private FileLogger fLogger = null;
	
	// Widgets
	private Button recurseDirectoriesButton = null;
	private Button browseButton = null;

	
	public ChecksumFileDialog(ScreenLogger sLogger, FileLogger fLogger){
		this.sLogger = sLogger;
		this.fLogger = fLogger;
	}
	
	/**
	 * Open the window
	 */
	public void open() {
		display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		
		setOptions();
		
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
		shell = new Shell(SWT.TITLE);
		shell.setImage(SWTResourceManager.getImage("images/apmd5.ico"));
		shell.setSize(389, 251);
		shell.setText("Choose a location");

		final Composite composite = new Composite(shell, SWT.NONE);
		composite.setBounds(0, 0, 381, 221);
		//composite.setBounds(location.x, location.y, WIDTH, HEIGHT);

		final Group group = new Group(composite, SWT.NONE);
		group.setBounds(10, 10, 361, 205);

		recurseDirectoriesButton = new Button(group, SWT.CHECK);
		recurseDirectoriesButton.setText("Make default directory");
		recurseDirectoriesButton.setBounds(10, 137, 130, 25);

		defaultDirectoryStyledText = new StyledText(group, SWT.BORDER);
		defaultDirectoryStyledText.setWordWrap(true);
		defaultDirectoryStyledText.setBounds(10, 44, 239, 45);

		final CLabel defaultDirectoryLabel = new CLabel(group, SWT.NONE);
		defaultDirectoryLabel.setText("Choose a location to save the checksum file.");
		defaultDirectoryLabel.setBounds(10, 22, 271, 22);

		browseButton = new Button(group, SWT.NONE);
		browseButton.addMouseListener(new MouseAdapter() {
			public void mouseUp(final MouseEvent e) {
				
				DirectoryDialog dd = new DirectoryDialog(shell, SWT.OPEN);
				dd.open();
				
				if(dd.getFilterPath() != null && !dd.getFilterPath().equalsIgnoreCase("")){
					defaultDirectoryStyledText.setText(dd.getFilterPath());
				}
				
			}
		});
		browseButton.setText("Browse");
		browseButton.setBounds(10, 111, 65, 20);
		//
	}

	private void setOptions(){
		recurseDirectoriesButton.setSelection(RECURSE_DIRECTORY);
		defaultDirectoryStyledText.setText(DEFAULT_DIRECTORY);
	}
}
