package com.powers.apmd5.gui;



import static com.powers.apmd5.util.MD5Constants.*;
import static com.powers.apmd5.util.StringUtil.*;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
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

import com.powers.apmd5.checksum.ChecksumCalculator;
import com.powers.apmd5.checksum.md5.MD5;
import com.powers.apmd5.checksum.sha.SHA;
import com.powers.apmd5.exceptions.CanNotWriteToFileException;
import com.powers.apmd5.gui.GUIHelper.*;
import com.powers.apmd5.util.FileLogger;
import com.powers.apmd5.util.GUIUtil;
import com.powers.apmd5.util.MD5Constants;
import com.powers.apmd5.util.ScreenLogger;
import com.powers.apmd5.util.SimpleIO;
import com.powers.apmd5.util.StringUtil;
import com.swtdesigner.SWTResourceManager;

public class APMD5 {

	

	private Label fileToBeTestedHelpLabel_3;
	private Label fileToBeTestedHelpLabel_2;
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
	private StyledText calculateBrowseStyledText;
	private StyledText calculateStringStyledText;
	private Button calculateButton;
	private Group calculateInputGroup;
	private StyledText messageStyledText;
	private Button testButton;
	private StyledText testPasteTypeMd5styledText;
	private StyledText testMd5FileStyledText;
	private StyledText testFileToBeTestedStyledText;
	MenuItem md5MenuItem;
	MenuItem sha1MenuItem;

	// Public Members
	public APMD5 apmd5 = this;
	public Shell shell;
	public StyledText testResultStyledText;
	public Display display = null;
	public Label testStatusIcon;
	public ProgressBar progressBar;
	public StyledText caculateResultStyledText;
	public Button recurseDirectoriesButton;
	public Button createFileButton;
	
	// Loggers
	public ScreenLogger sLogger = null;
	public FileLogger fLogger = null;
	
	
	
	private List<String> regExPatterns = new ArrayList<String>();
	private String tempText = null;
	private boolean msgBoxBool = false;
	

	
	private static final int THREAD_POOL_SIZE = 15;
	private final ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(THREAD_POOL_SIZE);
	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args) {
		try {
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
	}

	public void runInputLoop(){
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
	
	public void createAndShowGUI(){
		try {
			boolean ok = true;
        	open();
        	// Setup the screen logger, status bar at bottom of page
        	sLogger = new ScreenLogger(messageStyledText, display);
        	// Setup the file logger to log errors
        	fLogger = new FileLogger();
        	
        	MD5Constants.loadOptions(getFilePath(PROPERTIES_FILE));
        	
        	if(ok){
        		sLogger.log("Initialization Complete");
        	}

        	// Start Input Loop
        	runInputLoop();
        	
    	} catch (Exception e){
    		StringBuilder sb = new StringBuilder();
    		fLogger.log("Caught Exception " + e.getMessage());
    		StackTraceElement[] ste = e.getStackTrace();
    		
    		fLogger.log("Stack Trace");
    		for(int i=0; i<ste.length; i++){
    			if(ste[i] != null){
    				sb.append("Class Name: ");
    				sb.append(ste[i].getClassName());
    				sb.append(" File Name: ");
    				sb.append(ste[i].getFileName());
    				sb.append(" Method Name: ");
    				sb.append(ste[i].getMethodName());
    				fLogger.log(sb.toString());
    				sb.setLength(0);
    			}
    		}// end for
  
    	} finally {
    		if(fLogger != null){
    			fLogger.close();
    		}
    	}// end finally

	}// end createAndShowGUI()
	
//	private void loadRegexs() {
//		String[] split = REGEX_PATTERNS.split(REGEX_DELIM);
//		if(split == null){ return; }
//		for(String s : split){
//			REGEX_PATTERN_LIST.add(s);
//		}
//	}

	/**
	 * Create contents of the window
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setImage(SWTResourceManager.getImage(APMD5.class, "/images/apmd5.ico"));
		shell.setLayout(new FormLayout());
		shell.setSize(555, 605);
		shell.setText("All Purpose MD5");
		shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(final DisposeEvent e) {

				saveOptions(getFilePath(PROPERTIES_FILE));
				threadPool.shutdownNow();
				
			}
		});
		
		final Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);

		final MenuItem fileMenuItem = new MenuItem(menu, SWT.CASCADE);
		fileMenuItem.setText("File");
		

		final Menu menu_1 = new Menu(fileMenuItem);
		fileMenuItem.setMenu(menu_1);

		final MenuItem optionsMenuItem = new MenuItem(menu_1, SWT.NONE);
		optionsMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				MD5Options optionsWidget = new MD5Options(sLogger, fLogger);		
				optionsWidget.open();
			}
		});
		optionsMenuItem.setText("Options");

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
				MD5ReadPanel readMe = new MD5ReadPanel();
				readMe.open();
			}
		});
		viewReadmeMenuItem.setText("View README");
		
		final MenuItem viewLogMenuItem = new MenuItem(menu_2, SWT.NONE);
		viewLogMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				// close the log to view the latest changes
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
				sb.append("\n");
				sb.append("Author(s) ");
				sb.append(MD5Constants.AUTHORS);
				sb.append("\n");
				sb.append("Version ");
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

		final Composite composite = new Composite(tabFolder, SWT.NONE);
		testTabItem.setControl(composite);

		final CLabel calculateAMd5Label_1 = new CLabel(composite, SWT.NONE);
		calculateAMd5Label_1.setBounds(111, 10, 308, 31);
		calculateAMd5Label_1.setFont(SWTResourceManager.getFont("Arial Unicode MS", 16, SWT.NONE));
		calculateAMd5Label_1.setText("Test a File Against a Checksum");

		final Group testInputGroup = new Group(composite, SWT.NONE);
		testInputGroup.setBounds(10, 36, 525, 277);
		testInputGroup.setText("Test Input");

		fileToBeTestedHelpLabel = new Label(testInputGroup, SWT.NONE);
		fileToBeTestedHelpLabel.setToolTipText("Choose a file for which you wish to calculate a checksum. \nThe resulting checksum will be tested against either a checksum file\n or a checksum you paste below.");
		fileToBeTestedHelpLabel.setImage(SWTResourceManager.getImage(APMD5.class, "/images/question-16.png"));
		fileToBeTestedHelpLabel.setBounds(140, 29, 16, 16);

		checkSumStringHelpLabel = new Label(testInputGroup, SWT.NONE);
		checkSumStringHelpLabel.setToolTipText("You may paste or type in a checksum to be tested against\n the generated checksum from the \"File to be Tested\" above.");
		checkSumStringHelpLabel.setImage(SWTResourceManager.getImage(APMD5.class, "/images/question-16.png"));
		checkSumStringHelpLabel.setBounds(231, 150, 16, 16);

		checkSumFileHelpLabel = new Label(testInputGroup, SWT.NONE);
		checkSumFileHelpLabel.setToolTipText("You may choose a text file that includes a checksum.\nThat file will be opened and read and tested against\nthe file chosen above.");
		checkSumFileHelpLabel.setImage(SWTResourceManager.getImage(APMD5.class, "/images/question-16.png"));
		checkSumFileHelpLabel.setBounds(130, 91, 16, 16);

		final CLabel browseToALabel_4 = new CLabel(testInputGroup, SWT.NONE);
		browseToALabel_4.setFont(SWTResourceManager.getFont("Arial Unicode MS", 10, SWT.NONE));
		browseToALabel_4.setBounds(36, 23, 185, 20);
		browseToALabel_4.setText("File to be Tested");

		testFileToBeTestedStyledText = new StyledText(testInputGroup, SWT.BORDER);
		testFileToBeTestedStyledText.setBounds(38, 49, 337, 25);

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
		testFileToBeTestedbrowseButton.setBounds(381, 47, 75, 20);
		testFileToBeTestedbrowseButton.setText("Browse");

		testMd5FileStyledText = new StyledText(testInputGroup, SWT.BORDER);
		testMd5FileStyledText.setBounds(38, 112, 337, 25);

		final CLabel browseToALabel_4_1 = new CLabel(testInputGroup, SWT.NONE);
		browseToALabel_4_1.setFont(SWTResourceManager.getFont("Arial Unicode MS", 10, SWT.NONE));
		browseToALabel_4_1.setBounds(38, 86, 185, 20);
		browseToALabel_4_1.setText("Checksum File");

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
		testMd5FileBrowseButton.setBounds(381, 110, 75, 20);
		testMd5FileBrowseButton.setText("Browse");

		testPasteTypeMd5styledText = new StyledText(testInputGroup, SWT.BORDER);
		testPasteTypeMd5styledText.setBounds(38, 170, 284, 45);

		final CLabel browseToALabel_4_1_1 = new CLabel(testInputGroup, SWT.NONE);
		browseToALabel_4_1_1.setFont(SWTResourceManager.getFont("Arial Unicode MS", 10, SWT.NONE));
		browseToALabel_4_1_1.setBounds(36, 146, 211, 20);
		browseToALabel_4_1_1.setText("Or, Paste or type the Checksum");

		testButton = new Button(testInputGroup, SWT.NONE);
		testButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				
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
				
				
				TestAChecksum worker = null;
				if(isNotEmpty(checksumFilePath)){
					worker = new TestAChecksum(getChecksumCalculator(), fileToBeTested, new File(checksumFilePath));
				} else {
					worker = new TestAChecksum(getChecksumCalculator(), fileToBeTested, md5String);
				}
				
				Future<ChecksumTestResult> future = threadPool.submit(worker);
				threadPool.submit(new PopulateChecksum(apmd5, future));
			}
		});
		testButton.setBounds(165, 228, 115, 25);
		testButton.setText("Test");

		testStatusIcon = new Label(testInputGroup, SWT.NONE);
		testStatusIcon.setBounds(286, 226, 32, 32);

		final Group testResultGroup = new Group(composite, SWT.NONE);
		testResultGroup.setBounds(10, 334, 525, 173);
		testResultGroup.setText("Test Result");

		testResultStyledText = new StyledText(testResultGroup, SWT.V_SCROLL | SWT.BORDER);
		testResultStyledText.setFont(SWTResourceManager.getFont("Courier New", 10, SWT.NONE));
		testResultStyledText.setBounds(10, 19, 505, 144);

		final TabItem calculateTabItem = new TabItem(tabFolder, SWT.NONE);
		calculateTabItem.setText("Calculate");

		final Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		calculateTabItem.setControl(composite_1);

		final CLabel calculateAMd5Label = new CLabel(composite_1, SWT.NONE);
		calculateAMd5Label.setBounds(112, 10, 249, 30);
		calculateAMd5Label.setFont(SWTResourceManager.getFont("Arial Unicode MS", 16, SWT.NONE));
		calculateAMd5Label.setText("Calculate A Check Sum");

		calculateInputGroup = new Group(composite_1, SWT.NONE);
		calculateInputGroup.setBounds(10, 46, 525, 264);
		calculateInputGroup.setText("Calculate Input");

		fileToBeTestedHelpLabel_3 = new Label(calculateInputGroup, SWT.NONE);
		fileToBeTestedHelpLabel_3.setImage(SWTResourceManager.getImage(APMD5.class, "/images/question-16.png"));
		fileToBeTestedHelpLabel_3.setBounds(239, 126, 16, 16);
		fileToBeTestedHelpLabel_3.setToolTipText("Choose a file for which you wish to calculate a checksum. \nThe resulting checksum will be tested against either a checksum file\n or a checksum you paste below.");

		fileToBeTestedHelpLabel_2 = new Label(calculateInputGroup, SWT.NONE);
		fileToBeTestedHelpLabel_2.setImage(SWTResourceManager.getImage(APMD5.class, "/images/question-16.png"));
		fileToBeTestedHelpLabel_2.setBounds(150, 22, 16, 16);
		fileToBeTestedHelpLabel_2.setToolTipText("Choose a file for which you wish to calculate a checksum. \nThe resulting checksum will be tested against either a checksum file\n or a checksum you paste below.");

		fileToBeTestedHelpLabel_1 = new Label(calculateInputGroup, SWT.NONE);
		fileToBeTestedHelpLabel_1.setImage(SWTResourceManager.getImage(APMD5.class, "/images/question-16.png"));
		fileToBeTestedHelpLabel_1.setBounds(208, 66, 16, 16);
		fileToBeTestedHelpLabel_1.setToolTipText("Choose a file for which you wish to calculate a checksum. \nThe resulting checksum will be tested against either a checksum file\n or a checksum you paste below.");

		calculateButton = new Button(calculateInputGroup, SWT.NONE);
		calculateButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				String calculateFileOrDirPath = trim(calculateBrowseStyledText.getText());
				String string = trim(calculateStringStyledText.getText());
				
				boolean calcAString = isNotEmpty(string);
				boolean calcAFile = chooseAFileRadioButton.getSelection() && !calcAString;
				
				if(isEmpty(calculateFileOrDirPath) && isEmpty(string)){
					sLogger.logWarn("You did not choose a file or enter a string");
					return;
				}
				
				final File calculateFileOrDirFile = new File(calculateFileOrDirPath);
				if(isNotEmpty(calculateFileOrDirPath) && !calculateFileOrDirFile.exists()){
					sLogger.logWarn("The file you chose does not exist");
					return;
				}
				
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
				
				
				threadPool.submit(worker);
				//threadPool.submit(new PopulateCalcChecksum(future, apmd5));

			}
		});
		calculateButton.setBounds(175, 214, 115, 25);
		calculateButton.setText("Calculate");

		calculateStringStyledText = new StyledText(calculateInputGroup, SWT.BORDER);
		calculateStringStyledText.setBounds(38, 148, 284, 54);

		final CLabel browseToALabel_1 = new CLabel(calculateInputGroup, SWT.NONE);
		browseToALabel_1.setFont(SWTResourceManager.getFont("Arial Unicode MS", 10, SWT.NONE));
		browseToALabel_1.setBounds(38, 122, 242, 20);
		browseToALabel_1.setText("Or, Type in a string of characters");

		calculateBrowseStyledText = new StyledText(calculateInputGroup, SWT.BORDER);
		calculateBrowseStyledText.setBounds(38, 88, 337, 21);

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
		calculateBrowseButton.setBounds(381, 89, 75, 20);
		calculateBrowseButton.setText("Browse");

		final CLabel browseToALabel = new CLabel(calculateInputGroup, SWT.NONE);
		browseToALabel.setFont(SWTResourceManager.getFont("Arial Unicode MS", 10, SWT.NONE));
		browseToALabel.setBounds(38, 62, 185, 20);
		browseToALabel.setText("Browse to a file or Directory");

		chooseADirectoryRadioButton = new Button(calculateInputGroup, SWT.RADIO);
		chooseADirectoryRadioButton.setBounds(38, 38, 120, 15);
		chooseADirectoryRadioButton.setText("Choose a Directory");

		chooseAFileRadioButton = new Button(calculateInputGroup, SWT.RADIO);
		chooseAFileRadioButton.setSelection(true);
		chooseAFileRadioButton.setBounds(38, 21, 120, 15);
		chooseAFileRadioButton.setText("Choose a File");

		recurseDirectoriesButton = new Button(calculateInputGroup, SWT.CHECK);
		recurseDirectoriesButton.setToolTipText("Recursively look at each folder and calculate a checksum for every file.");
		recurseDirectoriesButton.setText("Recurse Directories?");
		recurseDirectoriesButton.setBounds(341, 20, 115, 16);

		createFileButton = new Button(calculateInputGroup, SWT.CHECK);
		createFileButton.setBounds(341, 42, 115, 16);
		createFileButton.setToolTipText("Create a file of the generated checksums with checksum and file name.");
		createFileButton.setText("Create a file?");

		final Group calculateOutputGroup = new Group(composite_1, SWT.NONE);
		calculateOutputGroup.setBounds(10, 316, 525, 181);
		calculateOutputGroup.setText("Calculate Result");

		caculateResultStyledText = new StyledText(calculateOutputGroup, SWT.V_SCROLL | SWT.BORDER);
		caculateResultStyledText.setFont(SWTResourceManager.getFont("Courier New", 10, SWT.NONE));
		caculateResultStyledText.setBounds(10, 22, 505, 149);
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
	
	public void setStatusIcon(boolean success){
		if(success){
			GUIUtil.setImage(testStatusIcon, CHECK_IMAGE);
		} else {
			GUIUtil.setImage(testStatusIcon, X_IMAGE);
		}
	}

	public ChecksumCalculator getChecksumCalculator(){

		if(sha1MenuItem.getSelection() && checksumCalculator instanceof MD5){
			checksumCalculator = new SHA();
		} else if(md5MenuItem.getSelection() && checksumCalculator instanceof SHA){
			checksumCalculator = new MD5();
		}
		
		return checksumCalculator;
	}
}
