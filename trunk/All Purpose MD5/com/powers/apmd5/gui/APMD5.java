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



import static com.powers.apmd5.util.StringUtil.*;
import static com.powers.apmd5.gui.GUIHelper.*;
import static com.powers.apmd5.gui.MD5Constants.*;


import java.io.File;
import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.powers.apmd5.checksum.ChecksumCalculator;
import com.powers.apmd5.checksum.md5.MD5;
import com.powers.apmd5.checksum.sha.SHA;
import com.powers.apmd5.gui.GUIHelper.CalculateAChecksum;
import com.powers.apmd5.gui.GUIHelper.ChecksumTestResult;
import com.powers.apmd5.gui.GUIHelper.PopulateChecksum;
import com.powers.apmd5.gui.GUIHelper.TestAChecksum;
import com.powers.apmd5.util.FileLogger;
import com.powers.apmd5.util.GUIUtil;
import com.powers.apmd5.util.ScreenLogger;
import com.powers.apmd5.util.StringUtil;
import com.powers.apmd5.util.SystemOutLogger;
import com.swtdesigner.SWTResourceManager;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.GridLayout;

public class APMD5 {

	public static boolean DEBUG = false;
	private static SHA Sha = new SHA();
	
	private Label fileToBeTestedHelpLabel_3;
	private Label chooseAFileDirHelpLabel;
	private Label fileToBeTestedHelpLabel_1;
	private Label checkSumStringHelpLabel;
	private Label checkSumFileHelpLabel;
	private Label fileToBeTestedHelpLabel;
	public static final String CHECK_IMAGE = "/images/check.png";
	public static final String X_IMAGE = "/images/x.png";
	public static final String QUESTION_IMAGE = "/images/question.png";
	
	private ChecksumCalculator checksumCalculator = new MD5();
	
	private Button chooseAFileRadioButton;
	private Button chooseADirectoryRadioButton;
	private Text calculateBrowseStyledText;
	private Text calculateStringStyledText;
	
	private Group calculateInputGroup;
	private StyledText messageStyledText;
	private Text testPasteTypeMd5styledText;
	private Text testMd5FileStyledText;
	private Text testFileToBeTestedStyledText;
	MenuItem md5MenuItem;
	MenuItem sha1MenuItem;

	// Public Members
	public APMD5 apmd5 = this;
	public Shell shell;
	public Text testResultStyledText;
	public Display display = null;
	public Label testStatusIcon;
	public ProgressBar progressBar;
	public Text caculateResultStyledText;
	public Button recurseDirectoriesButton;
	public Button createFileButton;
	public Button testButton;
	public Button calculateButton;
	
	// Loggers
	public ScreenLogger sLogger = null;
	public FileLogger fLogger = null;
	
	public AtomicBoolean isExecuting = new AtomicBoolean();
	
	private static final int THREAD_POOL_SIZE = 15;
	private final ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(THREAD_POOL_SIZE);
	
	IndeterminentProgressBar progressBarWorker = null;
	
	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args) {
		
		for(String arg : args){
			SystemOutLogger.debug("Argument: "+arg);
			if(equalIgnoreCase(DEBUG_ARG,trim(arg))){
				DEBUG = true;
			}
		}
		
		SystemOutLogger.debug("Staring application (main)\n");
		SystemOutLogger.debug("Current Workign Directory: "+CWD);
		SystemOutLogger.debug("Home Directory: "+HOME_DIR);
		SystemOutLogger.debug("Local Application Data Directory: "+LOCAL_APP_DATA);
		SystemOutLogger.debug("Wireable Directory: "+WRITE_DIR+"\n");
		
		SystemOutLogger.debug("Error Last Run? "+errorLastRun);
		SystemOutLogger.debug("Error Last Run Stacktrace "+errorLastRunStackTrace+"\n");
		
		try {
			SystemOutLogger.debug("Creating instance of APMD5()");
			APMD5 window = new APMD5();
			window.createAndShowGUI();
			//window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window
	 */
	public void open() {
		display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		SystemOutLogger.debug("Shell opened and layout called");
	}

	public void runInputLoop(){
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
	
	public void createAndShowGUI(){
		try {
			
			
        	open();
        	
        	SystemOutLogger.debug("Checking if the proper config folders/files exist");
        	
        	try {
        		checkIfWritableFoldersExist();
        	} catch(Exception e){
        		MessageBox mb = new MessageBox(shell, SWT.ICON_ERROR);
        		mb.setText("Configuration File Error");
        		mb.setMessage("I failed to locate and create the proper configuration files. Please redownload the application and reinstall! Stacktrace: \n\n"+GUIUtil.getStackTrace(e));
        		mb.open();
        	}
        	
        	SystemOutLogger.debug("Setting up logging");
        	// Setup the screen logger, status bar at bottom of page
        	sLogger = new ScreenLogger(messageStyledText, display);
        	SystemOutLogger.debug("Screen logger done.");
        	// Setup the file logger to log errors
        	fLogger = new FileLogger();
        	SystemOutLogger.debug("File logger done.");

        	SystemOutLogger.debug("Loading options...");
        	MD5Constants.loadOptions(getFilePath(PROPERTIES_FILE));
        	SystemOutLogger.debug("Done loading options.");
        	
        	progressBarWorker = new IndeterminentProgressBar(shell, progressBar, isExecuting);
        	
        	checkLastRun();
        	// Start Input Loop
        	runInputLoop();
        	
    	} catch (Exception e){
    		errorLastRunStackTrace = GUIUtil.getStackTrace(e);
    		fLogger.log(errorLastRunStackTrace);
    		errorLastRun = true;
    	} finally {
    		if(fLogger != null){
    			fLogger.close();
    		}
    	}// end finally

	}// end createAndShowGUI()

	private void checkIfWritableFoldersExist() throws IOException {
		
		// check properties file
		String propPath = getFilePath(PROPERTIES_FILE);
		File propFile = new File(propPath);
		boolean status;
		SystemOutLogger.debug("Property file location: "+propFile);
		
		if(!propFile.exists()){
			SystemOutLogger.log("File "+propFile.getAbsolutePath()+" does not exist, attempting to create it.");
			status = propFile.getParentFile().mkdirs();
			SystemOutLogger.log("Status of making directories: "+status);
			status = status && (status = propFile.createNewFile());
			SystemOutLogger.log("Status of creating a new file: "+status);
			
			if(status){
				SystemOutLogger.log("Successfully created file "+propFile.getAbsolutePath());
			} else {
				SystemOutLogger.log("Failed to create file "+propFile.getAbsolutePath());
				throw new IOException("Failed to create file "+propFile.getAbsolutePath());
			}
		} else {
			SystemOutLogger.debug("Property file is fine.");
		}
		
		// check log file
		String logPath = getFilePath(LOG_FILE_NAME);
		File logFile = new File(logPath);
		
		SystemOutLogger.debug("Log file: "+logFile);
		
		File parent = logFile.getParentFile();
		SystemOutLogger.debug("Parent file: "+parent);
		
		if(parent == null){
			SystemOutLogger.debug("Parent file is null, this may be a problem!");
		} else {
		
			if(!parent.exists()){
				SystemOutLogger.log("Logfile path '"+parent.getAbsolutePath()+"' does not exist, attempting to create it.");
				status = parent.mkdirs();
				SystemOutLogger.log("Status of making directories: "+status);
				if(status){
					SystemOutLogger.log("Successfully created directory "+parent.getAbsolutePath());
				} else {
					SystemOutLogger.log("Failed to create directory "+parent.getAbsolutePath());
					throw new IOException("Failed to create directory "+parent.getAbsolutePath());
				}
			} else {
				SystemOutLogger.debug("Log file parent directory is fine.");
			}
		}
	}
	
	/**
	 * Create contents of the window
	 */
	protected void createContents() {
		SystemOutLogger.debug("Creating contents...");
		shell = new Shell();
		shell.setImage(SWTResourceManager.getImage(APMD5.class, "/images/checksum.ico"));
		shell.setLayout(new FormLayout());
		shell.setSize(555, 605);
		shell.setText("All Purpose MD5");
		shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(final DisposeEvent e) {

				SystemOutLogger.debug("Shutting down...");
				SystemOutLogger.debug("Saving options...");
				saveOptions(getFilePath(PROPERTIES_FILE));
				SystemOutLogger.debug("Options Saved.");
				SystemOutLogger.debug("Calling shutdown now...");
				threadPool.shutdownNow();
				SystemOutLogger.debug("Application Terminated.");
				
			}
		});
		
		final Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);

		final MenuItem fileMenuItem = new MenuItem(menu, SWT.CASCADE);
		fileMenuItem.setText("File");
		

		final Menu menu_1 = new Menu(fileMenuItem);
		fileMenuItem.setMenu(menu_1);

		final MenuItem exitMenuItem = new MenuItem(menu_1, SWT.NONE);
		exitMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				shell.dispose();
			}
		});
		exitMenuItem.setText("Exit");

		// ========= Hash Type Menu =============
		
		final MenuItem hashTypeMenuItem = new MenuItem(menu, SWT.CASCADE);
		hashTypeMenuItem.setText("Hash Type");

		final Menu hashTypeSubMenu = new Menu(hashTypeMenuItem);
		hashTypeMenuItem.setMenu(hashTypeSubMenu);
		md5MenuItem = new MenuItem(hashTypeSubMenu, SWT.RADIO);
		md5MenuItem.setSelection(true);
		md5MenuItem.setText("MD5");
		sha1MenuItem = new MenuItem(hashTypeSubMenu, SWT.RADIO);
		sha1MenuItem.setText("SHA-1");
		// ========= Help Menu =================
		
		
		final MenuItem helpMenuItem = new MenuItem(menu, SWT.CASCADE);
		helpMenuItem.setText("Help");

		final Menu menu_2 = new Menu(helpMenuItem);
		helpMenuItem.setMenu(menu_2);

		final MenuItem viewReadmeMenuItem = new MenuItem(menu_2, SWT.NONE);
		viewReadmeMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				BrowserDialog bd = new BrowserDialog(shell, SWT.NONE, README_URL);
				bd.open();
			}
		});
		viewReadmeMenuItem.setText("View README (online)");
		
		final MenuItem viewLogMenuItem = new MenuItem(menu_2, SWT.NONE);
		viewLogMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				// close the log to view the latest changes (it will reopen the file automatically)
				fLogger.close();
				MD5ReadPanel readMe = new MD5ReadPanel();
				readMe.open(MD5Constants.LOG_FILE_NAME);
			}
		});
		viewLogMenuItem.setText("View Log");

		final MenuItem aboutMenuItem = new MenuItem(menu_2, SWT.NONE);
		aboutMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				StringBuilder sb = new StringBuilder();
				MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_INFORMATION);
				mb.setText("About");
				
				sb.append(MD5Constants.PROGRAM_NAME);
				sb.append("\nAuthor(s) ");
				sb.append(MD5Constants.AUTHORS);
				sb.append("\nVersion ");
				sb.append(MD5Constants.VERSION);
				sb.append("\n");
				sb.append(MD5Constants.WEBSITE);
				
				mb.setMessage(sb.toString());
				mb.open();
				
			}
		});
		aboutMenuItem.setText("About");

		TabFolder tabFolder;
		Composite statusBarComposite;
		tabFolder = new TabFolder(shell, SWT.NONE);
		final FormData fd_tabFolder = new FormData();
		fd_tabFolder.bottom = new FormAttachment(100, -19);
		fd_tabFolder.top = new FormAttachment(0, -1);
		fd_tabFolder.left = new FormAttachment(0, -4);
		fd_tabFolder.right = new FormAttachment(100, 2);
		tabFolder.setLayoutData(fd_tabFolder);

		final TabItem testTabItem = new TabItem(tabFolder, SWT.NONE);
		testTabItem.setText("Test");

		final Composite testTabComposite = new Composite(tabFolder, SWT.NONE);
		testTabItem.setControl(testTabComposite);
		testTabComposite.setLayout(new FormLayout());

		final CLabel calculateAMd5Label_1 = new CLabel(testTabComposite, SWT.NONE);
		FormData formData_5 = new FormData();
		formData_5.right = new FormAttachment(0, 476);
		formData_5.top = new FormAttachment(0);
		formData_5.left = new FormAttachment(0, 88);
		calculateAMd5Label_1.setLayoutData(formData_5);
		calculateAMd5Label_1.setFont(SWTResourceManager.getFont("Arial Unicode MS", 16, SWT.NONE));
		calculateAMd5Label_1.setText("Test a File Against a Checksum");

		final Group testInputGroup = new Group(testTabComposite, SWT.NONE);
		FormData formData_4 = new FormData();
		formData_4.left = new FormAttachment(0, 10);
		testInputGroup.setLayoutData(formData_4);
		testInputGroup.setText("Test Input");

		testFileToBeTestedStyledText = new Text(testInputGroup, SWT.BORDER);
		testFileToBeTestedStyledText.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NORMAL));
		testFileToBeTestedStyledText.setBounds(38, 49, 396, 25);

		final Button testFileToBeTestedbrowseButton = new Button(testInputGroup, SWT.NONE);
		testFileToBeTestedbrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				FileDialog fd = new FileDialog(shell);
				String path = fd.open();
				if(StringUtil.isNotEmpty(path)){
					testFileToBeTestedStyledText.setText(path);
				}
			}
		});
		testFileToBeTestedbrowseButton.setBounds(440, 47, 75, 25);
		testFileToBeTestedbrowseButton.setText("Browse");

		testMd5FileStyledText = new Text(testInputGroup, SWT.BORDER);
		testMd5FileStyledText.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NORMAL));
		testMd5FileStyledText.setBounds(38, 112, 396, 25);

		final Button testMd5FileBrowseButton = new Button(testInputGroup, SWT.NONE);
		testMd5FileBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				FileDialog fd = new FileDialog(shell);
				String path = fd.open();
				if(StringUtil.isNotEmpty(path)){
					testMd5FileStyledText.setText(path);
				}
			}
		});
		testMd5FileBrowseButton.setBounds(440, 110, 75, 25);
		testMd5FileBrowseButton.setText("Browse");

		testPasteTypeMd5styledText = new Text(testInputGroup, SWT.BORDER);
		testPasteTypeMd5styledText.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NORMAL));
		testPasteTypeMd5styledText.setBounds(38, 170, 396, 45);

		testButton = new Button(testInputGroup, SWT.NONE);
		testButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				
				if(isExecuting.get()){
					sLogger.logAppend(" (can't stop now)");
					return;
				}
				
				SystemOutLogger.debug("Testing a checksum...");
				
				testResultStyledText.setText(StringUtil.EMPTY_STRING);
				setStatusIcon(null);
				
				final String checksumFilePath = trim(testMd5FileStyledText.getText());
				
				final String filePathToBeTested = trim(testFileToBeTestedStyledText.getText());
				final String md5String = trim(testPasteTypeMd5styledText.getText());
				
				if(isEmpty(filePathToBeTested)){
					sLogger.logWarn("No file specified to test");
					return;
				}
				
				final File fileToBeTested = new File(filePathToBeTested);
				if(!fileToBeTested.exists()){
					sLogger.logWarn("The file to be tested does not exist");
					return;
				}
				
				if(isEmpty(checksumFilePath) && isEmpty(md5String)){	
					sLogger.logWarn("No MD5 specified to test the file");
					return;
				}
				
				isExecuting.set(true);
				
				threadPool.submit(progressBarWorker);
				
				SystemOutLogger.debug("Checksum Type:"+getChecksumCalculator());
				
				TestAChecksum worker = null;
				if(isNotEmpty(checksumFilePath)){
					worker = new TestAChecksum(apmd5, getChecksumCalculator(), fileToBeTested, new File(checksumFilePath));
				} else {
					worker = new TestAChecksum(apmd5, getChecksumCalculator(), fileToBeTested, md5String);
				}
				
				Future<ChecksumTestResult> future = threadPool.submit(worker);
				threadPool.submit(new PopulateChecksum(apmd5, future));
			}
		});
		testButton.setBounds(165, 228, 115, 25);
		testButton.setText("Test");

		testStatusIcon = new Label(testInputGroup, SWT.NONE);
		testStatusIcon.setBounds(286, 226, 32, 32);

		final Group testResultGroup = new Group(testTabComposite, SWT.NONE);
		formData_4.right = new FormAttachment(testResultGroup, 0, SWT.RIGHT);
		formData_4.bottom = new FormAttachment(testResultGroup, -6);
		
		Composite composite = new Composite(testInputGroup, SWT.NONE);
		composite.setBounds(38, 143, 396, 25);
		composite.setLayout(new RowLayout(SWT.HORIZONTAL));
		
				final CLabel browseToALabel_4_1_1 = new CLabel(composite, SWT.NONE);
				browseToALabel_4_1_1.setFont(SWTResourceManager.getFont("Arial Unicode MS", 10, SWT.NONE));
				browseToALabel_4_1_1.setText("Or, Paste or type the Checksum");
				
						checkSumStringHelpLabel = new Label(composite, SWT.NONE);
						checkSumStringHelpLabel.setToolTipText("You may paste or type in a checksum to be tested against\n the generated checksum from the \"File to be Tested\" above.");
						checkSumStringHelpLabel.setImage(SWTResourceManager.getImage(APMD5.class, "/images/question-16.png"));
						
						Composite composite_1 = new Composite(testInputGroup, SWT.NONE);
						composite_1.setBounds(38, 81, 396, 25);
						composite_1.setLayout(new RowLayout(SWT.HORIZONTAL));
						
								final CLabel browseToALabel_4_1 = new CLabel(composite_1, SWT.NONE);
								browseToALabel_4_1.setFont(SWTResourceManager.getFont("Arial Unicode MS", 10, SWT.NONE));
								browseToALabel_4_1.setText("Checksum File");
								
										checkSumFileHelpLabel = new Label(composite_1, SWT.NONE);
										checkSumFileHelpLabel.setToolTipText("You may choose a text file that includes a checksum.\r\nThat file will be opened and read and tested against\r\nthe file chosen above.");
										checkSumFileHelpLabel.setImage(SWTResourceManager.getImage(APMD5.class, "/images/question-16.png"));
										
										Composite composite_2 = new Composite(testInputGroup, SWT.NONE);
										composite_2.setBounds(38, 22, 396, 25);
										composite_2.setLayout(new RowLayout(SWT.HORIZONTAL));
										
												final CLabel browseToALabel_4 = new CLabel(composite_2, SWT.NONE);
												browseToALabel_4.setFont(SWTResourceManager.getFont("Arial Unicode MS", 10, SWT.NONE));
												browseToALabel_4.setText("File to be Tested");
												
														fileToBeTestedHelpLabel = new Label(composite_2, SWT.NONE);
														fileToBeTestedHelpLabel.setToolTipText("Choose a file for which you wish to calculate a checksum. \r\nThe resulting checksum will be tested against either a checksum file\r\n or a checksum you paste below.");
														fileToBeTestedHelpLabel.setImage(SWTResourceManager.getImage(APMD5.class, "/images/question-16.png"));
		FormData formData_3 = new FormData();
		formData_3.top = new FormAttachment(0, 320);
		formData_3.bottom = new FormAttachment(100, -10);
		formData_3.right = new FormAttachment(100, -10);
		formData_3.left = new FormAttachment(0, 10);
		testResultGroup.setLayoutData(formData_3);
		testResultGroup.setText("Test Result");
		FillLayout fillLayout_1 = new FillLayout(SWT.HORIZONTAL);
		fillLayout_1.marginHeight = 4;
		fillLayout_1.marginWidth = 4;
		testResultGroup.setLayout(fillLayout_1);

		testResultStyledText = new Text(testResultGroup, SWT.V_SCROLL | SWT.BORDER);
		testResultStyledText.setFont(SWTResourceManager.getFont("Courier New", 10, SWT.NONE));

		final TabItem calculateTabItem = new TabItem(tabFolder, SWT.NONE);
		calculateTabItem.setText("Calculate");

		final Composite calculateTabComposite = new Composite(tabFolder, SWT.NONE);
		calculateTabItem.setControl(calculateTabComposite);
		calculateTabComposite.setLayout(new FormLayout());

		final CLabel calculateAMd5Label = new CLabel(calculateTabComposite, SWT.NONE);
		FormData formData_2 = new FormData();
		formData_2.top = new FormAttachment(0);
		formData_2.left = new FormAttachment(0, 157);
		formData_2.right = new FormAttachment(100, -78);
		calculateAMd5Label.setLayoutData(formData_2);
		calculateAMd5Label.setFont(SWTResourceManager.getFont("Arial Unicode MS", 16, SWT.NONE));
		calculateAMd5Label.setText("Calculate A Checksum");

		calculateInputGroup = new Group(calculateTabComposite, SWT.NONE);
		FormData formData = new FormData();
		formData.left = new FormAttachment(0, 10);
		calculateInputGroup.setLayoutData(formData);
		calculateInputGroup.setText("Calculate Input");

		calculateButton = new Button(calculateInputGroup, SWT.NONE);
		calculateButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				
				if(isExecuting.get()){
					sLogger.logAppend(" (can't stop now)");
					return;
				}
				
				String calculateFileOrDirPath = trim(calculateBrowseStyledText.getText());
				String string = trim(calculateStringStyledText.getText());
				
				boolean calcAString = isNotEmpty(string);
				//boolean calcAFile = chooseAFileRadioButton.getSelection() && !calcAString;
				
				if(isEmpty(calculateFileOrDirPath) && isEmpty(string)){
					sLogger.logWarn("You did not choose a file or enter a string");
					return;
				}
				
				final File calculateFileOrDirFile = new File(calculateFileOrDirPath);
				if(isNotEmpty(calculateFileOrDirPath) && !calculateFileOrDirFile.exists()){
					sLogger.logWarn("The file you chose does not exist");
					return;
				}
				
				SystemOutLogger.debug("Checksum Type:"+getChecksumCalculator());
				
				caculateResultStyledText.setText(""); // clear results
				
				CalculateAChecksum worker = null;
				if(calcAString){
					worker = new CalculateAChecksum(apmd5, getChecksumCalculator(),string);
				} else {

					File saveFile = null;
					if(createFileButton.getSelection()){
						FileDialog saveDialog = new FileDialog(shell, SWT.SAVE);
						String savePath = saveDialog.open();
						if(isEmpty(savePath)){
							sLogger.logWarn("You did not choose a file to output the checksum(s)");
							return;
						}
						
						saveFile = new File(savePath);
						if(saveFile.exists()){
							MessageBox mb = new MessageBox(shell, SWT.YES | SWT.NO);
							mb.setText("File Already Exists");	
							mb.setMessage("File " + saveFile.getName() + " already exists\n Overwrite?");
							if(mb.open() == SWT.NO){
					        	sLogger.logWarn("Chose not to overwrite the file. Did not create checksum(s)");
					        	return;
							}
						}
						

					}
					
					worker = new CalculateAChecksum(apmd5, getChecksumCalculator(),calculateFileOrDirFile,saveFile, recurseDirectoriesButton.getSelection());
				}
				isExecuting.set(true);
				threadPool.submit(progressBarWorker);
				threadPool.submit(worker);
				//threadPool.submit(new PopulateCalcChecksum(future, apmd5));

			}
		});
		calculateButton.setBounds(175, 225, 115, 25);
		calculateButton.setText("Calculate");

		calculateStringStyledText = new Text(calculateInputGroup, SWT.BORDER);
		calculateStringStyledText.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NORMAL));
		calculateStringStyledText.setBounds(41, 165, 389, 54);

		calculateBrowseStyledText = new Text(calculateInputGroup, SWT.BORDER);
		calculateBrowseStyledText.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NORMAL));
		calculateBrowseStyledText.setBounds(41, 103, 392, 25);

		final Button calculateBrowseButton = new Button(calculateInputGroup, SWT.NONE);
		calculateBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				
				String path = EMPTY_STRING;
				if(chooseAFileRadioButton.getSelection()){
					FileDialog fd = new FileDialog(shell);
					path = fd.open();
				} else {
					DirectoryDialog dd = new DirectoryDialog(shell);
					path = dd.open();
				}
				
				if(StringUtil.isNotEmpty(path)){
					calculateBrowseStyledText.setText(path);
				}
			}
		});
		calculateBrowseButton.setBounds(439, 102, 75, 25);
		calculateBrowseButton.setText("Browse");
		
				final Group calculateOutputGroup = new Group(calculateTabComposite, SWT.NONE);
				formData_2.bottom = new FormAttachment(calculateOutputGroup, -272);
				formData.bottom = new FormAttachment(calculateOutputGroup, -6);
				formData.right = new FormAttachment(calculateOutputGroup, 0, SWT.RIGHT);
				FillLayout fillLayout = new FillLayout(SWT.HORIZONTAL);
				fillLayout.marginWidth = 4;
				fillLayout.marginHeight = 4;
				calculateOutputGroup.setLayout(fillLayout);
				FormData formData_1 = new FormData();
				formData_1.bottom = new FormAttachment(100, -10);
				formData_1.top = new FormAttachment(0, 306);
				formData_1.right = new FormAttachment(100, -14);
				formData_1.left = new FormAttachment(0, 10);
				
				Composite composite_3 = new Composite(calculateInputGroup, SWT.NONE);
				composite_3.setBounds(41, 135, 389, 25);
				composite_3.setLayout(new RowLayout(SWT.HORIZONTAL));
				
						final CLabel browseToALabel_1 = new CLabel(composite_3, SWT.NONE);
						browseToALabel_1.setFont(SWTResourceManager.getFont("Arial Unicode MS", 10, SWT.NONE));
						browseToALabel_1.setText("Or, Type in a string of characters");
						
								fileToBeTestedHelpLabel_3 = new Label(composite_3, SWT.NONE);
								fileToBeTestedHelpLabel_3.setImage(SWTResourceManager.getImage(APMD5.class, "/images/question-16.png"));
								fileToBeTestedHelpLabel_3.setToolTipText("Type in a string in order to get a checksum for it.\n This is useful for getting a hash of a password for testing or storage.");
								
								Composite composite_4 = new Composite(calculateInputGroup, SWT.NONE);
								composite_4.setBounds(41, 73, 389, 25);
								composite_4.setLayout(new RowLayout(SWT.HORIZONTAL));
								
										final CLabel browseToALabel = new CLabel(composite_4, SWT.NONE);
										browseToALabel.setFont(SWTResourceManager.getFont("Arial Unicode MS", 10, SWT.NONE));
										browseToALabel.setText("Browse to a file or Directory");
										
												fileToBeTestedHelpLabel_1 = new Label(composite_4, SWT.NONE);
												fileToBeTestedHelpLabel_1.setImage(SWTResourceManager.getImage(APMD5.class, "/images/question-16.png"));
												fileToBeTestedHelpLabel_1.setToolTipText("Select either a file or a directory. If a directory is chosen, consider recusivley \ncalculating checksums by checking the box to the right.");
												
												Composite composite_5 = new Composite(calculateInputGroup, SWT.NONE);
												composite_5.setBounds(41, 20, 389, 50);
												composite_5.setLayout(new GridLayout(6, false));
														
																chooseAFileRadioButton = new Button(composite_5, SWT.RADIO);
																chooseAFileRadioButton.setSelection(true);
																chooseAFileRadioButton.setText("Choose a File");
																				
																						chooseAFileDirHelpLabel = new Label(composite_5, SWT.NONE);
																						chooseAFileDirHelpLabel.setImage(SWTResourceManager.getImage(APMD5.class, "/images/question-16.png"));
																						chooseAFileDirHelpLabel.setToolTipText("Choose either a file or a directory to calculate a checksum\nor a list of checksums (if a directory is chosen).");
																				new Label(composite_5, SWT.NONE);
																				new Label(composite_5, SWT.NONE);
																				new Label(composite_5, SWT.NONE);
																				
																						recurseDirectoriesButton = new Button(composite_5, SWT.CHECK);
																						recurseDirectoriesButton.setToolTipText("Recursively look at each folder and calculate a checksum for every file.");
																						recurseDirectoriesButton.setText("Recurse Directories?");
																		
																				chooseADirectoryRadioButton = new Button(composite_5, SWT.RADIO);
																				chooseADirectoryRadioButton.setText("Choose a Directory");
																						new Label(composite_5, SWT.NONE);
																								new Label(composite_5, SWT.NONE);
																								new Label(composite_5, SWT.NONE);
																								new Label(composite_5, SWT.NONE);
																								
																										createFileButton = new Button(composite_5, SWT.CHECK);
																										createFileButton.setToolTipText("Create a file of the generated checksums with checksum and file name.");
																										createFileButton.setText("Create a file?");
				calculateOutputGroup.setLayoutData(formData_1);
				calculateOutputGroup.setText("Calculate Result");
				
						caculateResultStyledText = new Text(calculateOutputGroup, SWT.V_SCROLL | SWT.BORDER);
						caculateResultStyledText.setFont(SWTResourceManager.getFont("Courier New", 10, SWT.NONE));
		statusBarComposite = new Composite(shell, SWT.NONE);
		statusBarComposite.setLayout(new FormLayout());
		final FormData fd_statusBarComposite = new FormData();
		fd_statusBarComposite.top = new FormAttachment(0, 528);
		fd_statusBarComposite.bottom = new FormAttachment(100, -1);
		fd_statusBarComposite.left = new FormAttachment(0, -2);
		fd_statusBarComposite.right = new FormAttachment(100, 2);
		statusBarComposite.setLayoutData(fd_statusBarComposite);

		messageStyledText = new StyledText(statusBarComposite, SWT.BORDER);
		final FormData fd_messageStyledText = new FormData();
		fd_messageStyledText.bottom = new FormAttachment(100, -1);
		fd_messageStyledText.top = new FormAttachment(0, 3);
		fd_messageStyledText.right = new FormAttachment(100, -143);
		fd_messageStyledText.left = new FormAttachment(0, 3);
		messageStyledText.setLayoutData(fd_messageStyledText);
		messageStyledText.setEditable(false);
		progressBar = new ProgressBar(statusBarComposite, SWT.SMOOTH);
		final FormData fd_progressBar = new FormData();
		fd_progressBar.bottom = new FormAttachment(100, -1);
		fd_progressBar.top = new FormAttachment(0, 3);
		fd_progressBar.right = new FormAttachment(100, -5);
		fd_progressBar.left = new FormAttachment(messageStyledText, 3, SWT.DEFAULT);
		progressBar.setLayoutData(fd_progressBar);
		//
	}
	
	public void setStatusIcon(Boolean success){
		if(success == null){
			testStatusIcon.setVisible(false);
		} else if(success){
			GUIUtil.setImage(testStatusIcon, CHECK_IMAGE);
		} else {
			GUIUtil.setImage(testStatusIcon, X_IMAGE);
		}
	}

	public ChecksumCalculator getChecksumCalculator(){

		if(sha1MenuItem.getSelection() && checksumCalculator instanceof MD5){
			checksumCalculator = Sha;
		} else if(md5MenuItem.getSelection() && checksumCalculator instanceof SHA){
			checksumCalculator = new MD5();
		}
		
		return checksumCalculator;
	}
	
	private void checkLastRun(){
		if(errorLastRun){
			ErrorLastRunDialog dialog = new ErrorLastRunDialog(shell, SWT.NONE, MD5Constants.errorLastRunStackTrace);
			dialog.open();
			errorLastRun = false;
		}
	}
}
