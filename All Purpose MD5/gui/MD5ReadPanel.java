package gui;

import java.io.BufferedReader;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import com.swtdesigner.SWTResourceManager;

import util.MD5Constants;
import util.SimpleIO;

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
		shell.setImage(SWTResourceManager.getImage(MD5ReadPanel.class, "/images/apmd5_icon.png"));
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
