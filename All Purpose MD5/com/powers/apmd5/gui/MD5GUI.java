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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;


import com.powers.apmd5.checksum.md5.MD5Functions;
import com.powers.apmd5.exceptions.CanNotWriteToFileException;
import com.powers.apmd5.exceptions.InvalidPropertyException;
import com.powers.apmd5.util.FileLogger;
import com.powers.apmd5.util.MD5Constants;
import com.powers.apmd5.util.ScreenLogger;
import com.powers.apmd5.util.SimpleIO;
import com.powers.apmd5.util.StringUtil;
import com.swtdesigner.SWTResourceManager;

public class MD5GUI {

	protected Shell shell;
	private Display display = null;
	
	private List<String> regExPatterns = new ArrayList<String>();
	private String tempText = null;
	private boolean msgBoxBool = false;
	
	// Loggers
	private ScreenLogger sLogger = null;
	private FileLogger fLogger = null;
	
	// Progress Bar
	private static ProgressBar progressBar = null;
	
	// Widgets
	private StyledText messageStyledText = null;
	private StyledText calculateBrowseStyledText = null;
	private StyledText testFileToBeTestedStyledText = null;
	private StyledText testMd5FileStyledText = null;
	private StyledText caculateResultStyledText = null;
	private StyledText calculateStringStyledText = null;
	private Button chooseAFileRadioButton = null;
	private Button chooseADirectoryRadioButton = null;
	private StyledText testPasteTypeMd5styledText = null;
	private StyledText testResultStyledText = null;
	private Table statsTable = null;
	private TableColumn numberColumn = null;
	private TableColumn filenameColumn = null;
	private TableColumn timeElapsedColumn = null;
	private TableColumn timeStartedColumn = null;
	private TableColumn timeStoppedColumn = null;
	private Group calculateInputGroup = null;
	private Button calculateButton = null;
	private Button testButton = null;
	// Options
	public static boolean recurseDirectory = false;
	
	public static boolean enableStats = false;
	public static int numberOfStatsKept = 0;
	
	public static int statsNumber = 1;
	
	public static String defaultDirectory = null;
	

	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args) {
		MD5GUI md5Gui = new MD5GUI();
		md5Gui.createAndShowGUI();
	}
	
	public void createAndShowGUI(){
		try {
			boolean ok = true;
        	open();
        	// Setup the screen logger, status bar at bottom of page
        	sLogger = new ScreenLogger(messageStyledText, display);
        	// Setup the file logger to log errors
        	fLogger = new FileLogger();
        	
        	try {
        		getAndSetProperties();
        	} catch(InvalidPropertyException e){
        		sLogger.logError("Could not load properties. " + e.getMessage());
        		ok = false;
        	}
        	
        	if(ok){
        		sLogger.log("Initialization Complete");
        	}
        	// load stats from properties file
        	if(enableStats){
        		loadStats();
        	}

        	// Start Input Loop
        	start();
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


	/**
	 * Open the window
	 */
	public void open() {
		display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
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
		shell = new Shell(SWT.MIN | SWT.TITLE);
		shell.setImage(SWTResourceManager.getImage("images/apmd5.ico"));
		shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(final DisposeEvent e) {

				storeStats();
				
			}
		});
		
		shell.setMinimumSize(new Point(0, 0));
		shell.setSize(551, 576);
		shell.setText("All Purpose MD5");

		final TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
		tabFolder.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		tabFolder.setBounds(0, 0, 545, 507);

		// Set the test tab before the calculate tab
		final TabItem testTabItem = new TabItem(tabFolder, SWT.NONE);
		testTabItem.setToolTipText("Test a file for integrity");
		testTabItem.setText("Test");
		
		final TabItem calculateTabItem = new TabItem(tabFolder, SWT.NONE);
		calculateTabItem.setToolTipText("Calculate a MD5 Sum");
		calculateTabItem.setText("Calculate");

		final Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setFont(SWTResourceManager.getFont("Arial Unicode MS", 10, SWT.NONE));
		composite.setLocation(4, 24);
		calculateTabItem.setControl(composite);

		final CLabel calculateAMd5Label = new CLabel(composite, SWT.NONE);
		calculateAMd5Label.setFont(SWTResourceManager.getFont("Arial Unicode MS", 16, SWT.NONE));
		calculateAMd5Label.setText("Calculate A MD5 Sum");
		calculateAMd5Label.setBounds(157, 10, 249, 30);

		calculateInputGroup = new Group(composite, SWT.NONE);
		calculateInputGroup.setText("Calculate Input");
		calculateInputGroup.setBounds(38, 46, 466, 264);

		calculateButton = new Button(calculateInputGroup, SWT.NONE);
		calculateButton.addMouseListener(new MouseAdapter() {
			public void mouseUp(final MouseEvent e) {
// #######################################  Calculate Button Mouse UP #########################################
				setButtonEnabled(display, calculateButton, false);
				setProgressEnabled(display, progressBar, true);
				
				calculateMouseEvent();
				
				setButtonEnabled(display, calculateButton, true);
				setProgressEnabled(display, progressBar, false);
 
		}});
		calculateButton.setBounds(175, 214,115, 25);
		calculateButton.setText("Calculate");

		calculateStringStyledText = new StyledText(calculateInputGroup, SWT.BORDER);
		calculateStringStyledText.setBounds(38, 148,284, 54);

		final CLabel browseToALabel_1 = new CLabel(calculateInputGroup, SWT.NONE);
		browseToALabel_1.setBounds(38, 122,242, 20);
		browseToALabel_1.setFont(SWTResourceManager.getFont("Arial Unicode MS", 10, SWT.NONE));
		browseToALabel_1.setText("Or, Type in a string of characters");

		calculateBrowseStyledText = new StyledText(calculateInputGroup, SWT.BORDER);
		calculateBrowseStyledText.setBounds(38, 88,337, 21);

		final Button calculateBrowseButton = new Button(calculateInputGroup, SWT.NONE);
		calculateBrowseButton.addMouseListener(new MouseAdapter() {
			public void mouseUp(final MouseEvent e) {
				if(chooseAFileRadioButton.getSelection()){
					FileDialog fd = new FileDialog(shell, SWT.OPEN);
					
					fd.open();
					if(fd.getFileName() != null && !fd.getFileName().equalsIgnoreCase("")){
						calculateBrowseStyledText.setText(fd.getFilterPath() + "\\" + fd.getFileName());
						sLogger.log("Found File " + fd.getFileName());
					} else {
						sLogger.logWarn("Did not choose a file");
					}
				} else
				
				if(chooseADirectoryRadioButton.getSelection()){
				DirectoryDialog dd = new DirectoryDialog(shell, SWT.OPEN);
				dd.open();
				
				if(dd.getFilterPath() != null && !dd.getFilterPath().equalsIgnoreCase("")){
					calculateBrowseStyledText.setText(dd.getFilterPath());
					sLogger.log("Found Directory " + dd.getFilterPath());
				} else {
					sLogger.logWarn("Did not choose a directory");
				}
			}
			}
		});
		calculateBrowseButton.setBounds(381, 89,75, 20);
		calculateBrowseButton.setText("Browse");

		final CLabel browseToALabel = new CLabel(calculateInputGroup, SWT.NONE);
		browseToALabel.setBounds(38, 62,185, 20);
		browseToALabel.setFont(SWTResourceManager.getFont("Arial Unicode MS", 10, SWT.NONE));
		browseToALabel.setText("Browse to a file or Directory");

		chooseADirectoryRadioButton = new Button(calculateInputGroup, SWT.RADIO);
		chooseADirectoryRadioButton.setBounds(38, 38,120, 15);
		chooseADirectoryRadioButton.setText("Choose a Directory");

		chooseAFileRadioButton = new Button(calculateInputGroup, SWT.RADIO);
		chooseAFileRadioButton.setBounds(38, 21,120, 15);
		chooseAFileRadioButton.setSelection(true);
		chooseAFileRadioButton.setText("Choose a File");

		final Group calculateOutputGroup = new Group(composite, SWT.NONE);
		calculateOutputGroup.setText("Calculate Result");
		calculateOutputGroup.setBounds(38, 329, 465, 135);

		caculateResultStyledText = new StyledText(calculateOutputGroup, SWT.V_SCROLL | SWT.BORDER);
		caculateResultStyledText.setBounds(25, 22,418, 103);

		

		final Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		testTabItem.setControl(composite_1);

		final CLabel calculateAMd5Label_1 = new CLabel(composite_1, SWT.NONE);
		calculateAMd5Label_1.setBounds(143, 10, 268, 31);
		calculateAMd5Label_1.setFont(SWTResourceManager.getFont("Arial Unicode MS", 16, SWT.NONE));
		calculateAMd5Label_1.setText("Test a File Against a MD5");

		final Group testInputGroup = new Group(composite_1, SWT.NONE);
		testInputGroup.setText("Test Input");
		testInputGroup.setBounds(38, 46, 466, 277);

		final CLabel browseToALabel_4 = new CLabel(testInputGroup, SWT.NONE);
		browseToALabel_4.setBounds(36, 23,185, 20);
		browseToALabel_4.setFont(SWTResourceManager.getFont("Arial Unicode MS", 10, SWT.NONE));
		browseToALabel_4.setText("File to be Tested");

		testFileToBeTestedStyledText = new StyledText(testInputGroup, SWT.BORDER);
		testFileToBeTestedStyledText.setBounds(38, 49,337, 16);

		final Button testFileToBeTestedbrowseButton = new Button(testInputGroup, SWT.NONE);
		testFileToBeTestedbrowseButton.addMouseListener(new MouseAdapter() {
			public void mouseUp(final MouseEvent e) {
				// Open a file dialog and put the path in the text box
				FileDialog fd = new FileDialog(shell, SWT.OPEN);
				fd.open();
				if(fd.getFileName() != null && !fd.getFileName().equalsIgnoreCase("")){
					testFileToBeTestedStyledText.setText(fd.getFilterPath() + "\\" + fd.getFileName());
					sLogger.log("Found File " + fd.getFileName());
				} else {
					sLogger.logWarn("Did not choose a file");
				}
			}
		});
		testFileToBeTestedbrowseButton.setBounds(381, 47,75, 20);
		testFileToBeTestedbrowseButton.setText("Browse");

		testMd5FileStyledText = new StyledText(testInputGroup, SWT.BORDER);
		testMd5FileStyledText.setBounds(38, 112,337, 16);

		final CLabel browseToALabel_4_1 = new CLabel(testInputGroup, SWT.NONE);
		browseToALabel_4_1.setBounds(38, 86,185, 20);
		browseToALabel_4_1.setFont(SWTResourceManager.getFont("Arial Unicode MS", 10, SWT.NONE));
		browseToALabel_4_1.setText("MD5 File");

		final Button testMd5FileBrowseButton = new Button(testInputGroup, SWT.NONE);
		testMd5FileBrowseButton.addMouseListener(new MouseAdapter() {
			public void mouseUp(final MouseEvent e) {
				// Open a file dialog and put the path in the text box
				FileDialog fd = new FileDialog(shell, SWT.OPEN);
				fd.open();
				if(fd.getFileName() != null && !fd.getFileName().equalsIgnoreCase("")){
					testMd5FileStyledText.setText(fd.getFilterPath() + "\\" + fd.getFileName());
					sLogger.log("Found File " + fd.getFileName());
				} else {
					sLogger.logWarn("Did not choose a file");
				}
			}
		});
		testMd5FileBrowseButton.setBounds(381, 110,75, 20);
		testMd5FileBrowseButton.setText("Browse");

		testPasteTypeMd5styledText = new StyledText(testInputGroup, SWT.BORDER);
		testPasteTypeMd5styledText.setBounds(38, 170,284, 45);

		final CLabel browseToALabel_4_1_1 = new CLabel(testInputGroup, SWT.NONE);
		browseToALabel_4_1_1.setBounds(36, 146,185, 20);
		browseToALabel_4_1_1.setFont(SWTResourceManager.getFont("Arial Unicode MS", 10, SWT.NONE));
		browseToALabel_4_1_1.setText("Or, Paste or type the MD5");

		testButton = new Button(testInputGroup, SWT.NONE);
		testButton.addMouseListener(new MouseAdapter() {
			public void mouseUp(final MouseEvent e) {
// #######################################  Test Button Mouse UP #########################################
				setButtonEnabled(display, testButton, false);
				setProgressEnabled(display, progressBar, true);
			
				testMouseButtonEvent();
				
				setButtonEnabled(display, testButton, true);
				setProgressEnabled(display, progressBar, false);
							
			}});
		testButton.setBounds(165, 228,115, 25);
		testButton.setText("Test");

		final Group testResultGroup = new Group(composite_1, SWT.NONE);
		testResultGroup.setText("Test Result");
		testResultGroup.setBounds(38, 341, 470, 130);

		testResultStyledText = new StyledText(testResultGroup, SWT.V_SCROLL | SWT.BORDER);
		testResultStyledText.setBounds(25, 19,424, 101);

		final TabItem statisticsTabItem = new TabItem(tabFolder, SWT.NONE);
		statisticsTabItem.setText("Statistics");

		final Composite composite_2 = new Composite(tabFolder, SWT.NONE);
		statisticsTabItem.setControl(composite_2);

		statsTable = new Table(composite_2, SWT.BORDER);
		statsTable.setLinesVisible(true);
		statsTable.setHeaderVisible(true);
		statsTable.setBounds(10, 51, 505, 372);

		numberColumn = new TableColumn(statsTable, SWT.NONE);
		numberColumn.setMoveable(true);
		numberColumn.setWidth(22);
		numberColumn.setText("#");

		filenameColumn = new TableColumn(statsTable, SWT.NONE);
		filenameColumn.setMoveable(true);
		filenameColumn.setWidth(217);
		filenameColumn.setText("File or Text");

		timeElapsedColumn = new TableColumn(statsTable, SWT.NONE);
		timeElapsedColumn.setMoveable(true);
		timeElapsedColumn.setWidth(100);
		timeElapsedColumn.setText("Time Elapsed");

		timeStartedColumn = new TableColumn(statsTable, SWT.NONE);
		timeStartedColumn.setMoveable(true);
		timeStartedColumn.setWidth(78);
		timeStartedColumn.setText("Started");

		timeStoppedColumn = new TableColumn(statsTable, SWT.NONE);
		timeStoppedColumn.setMoveable(true);
		timeStoppedColumn.setWidth(83);
		timeStoppedColumn.setText("Stopped");

		final CLabel statisticsLabel = new CLabel(composite_2, SWT.NONE);
		statisticsLabel.setFont(SWTResourceManager.getFont("Arial Unicode MS", 16, SWT.NONE));
		statisticsLabel.setText("Statistics");
		statisticsLabel.setBounds(205, 10, 105, 26);

		final Button clearStatsButton = new Button(composite_2, SWT.NONE);
		clearStatsButton.addMouseListener(new MouseAdapter() {
			public void mouseUp(final MouseEvent e) {	
				statsTable.removeAll();
			}
		});
		
		clearStatsButton.setText("Clear Stats");
		clearStatsButton.setBounds(205, 438, 118, 27);

		final Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);

		final MenuItem fileMenuItem = new MenuItem(menu, SWT.CASCADE);
		fileMenuItem.setText("File");

		final Menu menu_1 = new Menu(fileMenuItem);
		fileMenuItem.setMenu(menu_1);

		final MenuItem saveMd5ToMenuItem = new MenuItem(menu_1, SWT.NONE);
		saveMd5ToMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				FileDialog fd = new FileDialog(shell, SWT.SAVE);
				fd.open();
				
				if(fd.getFileName() != null && !fd.getFileName().equalsIgnoreCase("")){
					final String filePath = fd.getFilterPath() + "\\" + fd.getFileName();
					PrintWriter pw = null;
					
					try {
						pw = SimpleIO.openFileForOutput(filePath);
					} catch (CanNotWriteToFileException e1){
					    sLogger.logError("Can not write to file " + filePath +". Did not save file");
	        			return;
	        		}
					
        			display.syncExec(
    				new Runnable() {
    				public void run(){
    					tempText = caculateResultStyledText.getText();
    				}});
        			
					pw.println(tempText);
					SimpleIO.close(pw);
					
    				sLogger.log("MD5 Result saved to file " + filePath);

				} else {
    				sLogger.logWarn("A file was not chosen to Save As");
				}
			}
		});
		saveMd5ToMenuItem.setText("Save MD5 As...");

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

		messageStyledText = new StyledText(shell, SWT.BORDER);
		messageStyledText.setEditable(false);
		//messageStyledText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		messageStyledText.setBounds(0, 506, 466, 20);

		// Progress Bar
		progressBar = new ProgressBar(shell, SWT.NONE | SWT.INDETERMINATE);
		progressBar.setBounds(470, 510, 70, 15);
		progressBar.setVisible(false);
		
	}
	
	public void getAndSetProperties() throws InvalidPropertyException {
		Properties properties = new Properties();
		String prop = null;
		
		try {
			properties.load(new FileInputStream(MD5Constants.PROPERTIES_FILE));
		} catch (FileNotFoundException e) {
			sLogger.logError("Unable to find properties file " + MD5Constants.PROPERTIES_FILE);
			fLogger.log("Unable to find properties file " + MD5Constants.PROPERTIES_FILE);
			fLogger.log("Exception Message: " + e.getMessage());
			throw new InvalidPropertyException("Unable to find Properties File " + MD5Constants.PROPERTIES_FILE);
			
		} catch (IOException e) {
			sLogger.logError("Unable to read properties file " + MD5Constants.PROPERTIES_FILE);
			fLogger.log("Unable to read properties file " + MD5Constants.PROPERTIES_FILE);
			fLogger.log("Exception Message: " + e.getMessage());
			throw new InvalidPropertyException("Unable to read Properties file " + MD5Constants.PROPERTIES_FILE);
		}
		
		// Recurse Directory?
		prop = properties.getProperty(MD5Constants.RECURSE_DIRECTORY);
		checkProperty(prop, MD5Constants.RECURSE_DIRECTORY);
		recurseDirectory = prop.equalsIgnoreCase("true");
		
		// Enable Stats?
		prop = properties.getProperty(MD5Constants.STATS_ON);
		checkProperty(prop, MD5Constants.STATS_ON);
		enableStats = prop.equalsIgnoreCase("true");
		
		// Number Of Stats To Keep
		prop = properties.getProperty(MD5Constants.STATS_NUMBER_KEPT);
		checkProperty(prop, MD5Constants.STATS_NUMBER_KEPT);
		numberOfStatsKept = Integer.parseInt(prop);
		
		// Default Directory
		prop = properties.getProperty(MD5Constants.DEFAULT_DIRECTORY);
		try {
		checkProperty(prop, MD5Constants.DEFAULT_DIRECTORY);
		defaultDirectory = prop;
		} catch(InvalidPropertyException e){
			DirectoryDialog dd = new DirectoryDialog(shell, SWT.OPEN);
			dd.open();
			
			if(dd.getFilterPath() != null && !dd.getFilterPath().equalsIgnoreCase("")){
				defaultDirectory = dd.getFilterPath();
			} else {
				defaultDirectory = MD5Constants.HOME_DIR;
			}
			setSingleProperty(MD5Constants.DEFAULT_DIRECTORY, defaultDirectory);
		}
		
		// Regular Expressions for getting the MD5 out of a file
		prop = properties.getProperty(MD5Constants.REGEX_PATTERNS);
		checkProperty(prop, MD5Constants.REGEX_PATTERNS);
		StringTokenizer st = new StringTokenizer(prop, MD5Constants.REGEX_DELIM);
		while(st.hasMoreTokens()){
			regExPatterns.add(st.nextToken());
		}
	}
	
	public void checkProperty(String property, String name) throws InvalidPropertyException {
		if(property == null || property.equalsIgnoreCase("")){
			throw new InvalidPropertyException("Property " + name + " is null or empty");
		}
	}
	
	public void addTableItem(final String filename, final String timeElapsed, final String startTime, final String stopTime, final int index){
		if(filename == null || timeElapsed == null || startTime == null || stopTime == null){
			return;
		}

		display.asyncExec(
		new Runnable() {
		public void run(){
					
			int i = index;
			
			// find and remove the last element if > # of stats defined
			int itemCount = statsTable.getItemCount();
			if(itemCount >= numberOfStatsKept){
				if(itemCount > 0){
					statsTable.getItem(--itemCount).dispose();
				}
			}
			
			// Add the new table item
			if(numberOfStatsKept > 0){
				final TableItem ti = new TableItem(statsTable, SWT.BORDER, index);
				ti.setText(4, stopTime);
				ti.setText(3, startTime);
				ti.setText(2, timeElapsed);
				ti.setText(1, filename);
				ti.setText(0, Integer.toString(++i));
				
				changeExistingRowsNumber(i);
			}
		}});
	}
	
	// No need to be UI safe, it is called by the UI thread
	public void addTableItem(String filename, String timeElapsed, String startTime, String stopTime){
		if(filename == null || timeElapsed == null || startTime == null || stopTime == null){
			return;
		}
		
		// Add the new table item at end
		final TableItem ti = new TableItem(statsTable, SWT.BORDER);
		ti.setText(4, stopTime);
		ti.setText(3, startTime);
		ti.setText(2, timeElapsed);
		ti.setText(1, filename);
		ti.setText(0, Integer.toString(statsNumber));
		statsNumber++;
	}
	
	public void storeStats(){
		Properties props = new Properties();
		String text = null;
		
		if(statsTable == null){
			sLogger.logError("Unable to store the stats. Stats Table is null");
			return;
		}
		
		// stores stats stats.1.0
		//              stats.1.1 ... etc
		final TableItem[] tis = statsTable.getItems();
		for(int i=0; i<tis.length; i++){
			if(tis[i] != null){
				for(int j=0; j<MD5Constants.STATS_NUM_COLS; j++){
					text = tis[i].getText(j);
					if(text != null){
						props.setProperty(MD5Constants.STATS_PROP_BASE_NAME + i + "." + j, text);
					}
				}
			}
		}
		
		try {
			props.store(new FileOutputStream(new File(MD5Constants.STATS_PROPERTIES_FILE)), "Stats storage");
		} catch (FileNotFoundException e1) {
			sLogger.logError("Unable to store properties file " + MD5Constants.STATS_PROPERTIES_FILE);
			fLogger.log("Unable to store properties file " + MD5Constants.STATS_PROPERTIES_FILE);
			fLogger.log("Exception Message: " + e1.getMessage());
		} catch (IOException e1) {
			sLogger.logError("Unable to store properties file " + MD5Constants.STATS_PROPERTIES_FILE);
			fLogger.log("Unable to store properties file " + MD5Constants.STATS_PROPERTIES_FILE);
			fLogger.log("Exception Message: " + e1.getMessage());
		}
		
	}// end storeStats
	
	public void loadStats(){
		Properties props = new Properties();
		int index = 0;
		
		try {
			props.load(new FileInputStream(MD5Constants.STATS_PROPERTIES_FILE));
		} catch (FileNotFoundException e1) {
			sLogger.logError("Unable to find properties file " + MD5Constants.STATS_PROPERTIES_FILE);
			fLogger.log("Unable to find properties file " + MD5Constants.STATS_PROPERTIES_FILE);
			fLogger.log("Exception Message: " + e1.getMessage());

		} catch (IOException e1) {
			sLogger.logError("Unable to read properties file " + MD5Constants.STATS_PROPERTIES_FILE);
			fLogger.log("Unable to read properties file " + MD5Constants.STATS_PROPERTIES_FILE);
			fLogger.log("Exception Message: " + e1.getMessage());
		}
		
		String filename = null;
		String timeElapsed = null;
		String startTime = null;
		String stopTime = null;
		
		while(getNextProperty(props, index) != null){
			
			filename    = props.getProperty(MD5Constants.STATS_PROP_BASE_NAME + index + ".1");
			timeElapsed = props.getProperty(MD5Constants.STATS_PROP_BASE_NAME + index + ".2");
			startTime   = props.getProperty(MD5Constants.STATS_PROP_BASE_NAME + index + ".3");
			stopTime    = props.getProperty(MD5Constants.STATS_PROP_BASE_NAME + index + ".4");

			addTableItem(filename, timeElapsed, startTime, stopTime);
			index++;
		}
		
		statsNumber = 1;
		
	}
	
	public String getNextProperty(Properties props, int index){
		String value = null;
		
		// don't load more than what is declared in options
		if(index >= numberOfStatsKept){
			return null;
		}
		
		value = props.getProperty(MD5Constants.STATS_PROP_BASE_NAME + index + ".0");
		if(value == null || StringUtil.equalIgnoreCase(value, "")){
			return null;
		}
		
		return value;
	}
	
	public String getTime(long time1, long time2, String formatType){
		String time = null;
		
		Date date = null;
		if(time1 <= 0){
			date = new Date(time2);
		} else if(time2 <= 0){
			date = new Date(time1);
		} else {
			date = new Date(time2 - time1);
		}
		
		SimpleDateFormat formatter = new SimpleDateFormat(formatType); 
		time = formatter.format(date);
		
		return time;
	}
	
	public void changeExistingRowsNumber(int index){
		TableItem[] tableItems = statsTable.getItems();
		
		for(int i=index; i< tableItems.length; i++){
			if(tableItems[i] != null){
				tableItems[i].setText(0, Integer.toString(++index));
			}
		}
	}
	
	public void setSingleProperty(String name, String value){
		Properties props = new Properties();
		
		try {
			props.load(new FileInputStream(MD5Constants.PROPERTIES_FILE));
		} catch (FileNotFoundException e1) {
			sLogger.logError("Unable to find properties file " + MD5Constants.PROPERTIES_FILE);
			fLogger.log("Unable to find properties file " + MD5Constants.PROPERTIES_FILE);
			fLogger.log("Exception Message: " + e1.getMessage());

		} catch (IOException e1) {
			sLogger.logError("Unable to read properties file " + MD5Constants.PROPERTIES_FILE);
			fLogger.log("Unable to read properties file " + MD5Constants.PROPERTIES_FILE);
			fLogger.log("Exception Message: " + e1.getMessage());
		}
		
		props.setProperty(name, value);
		
		try {
			props.store(new FileOutputStream(new File(MD5Constants.PROPERTIES_FILE)), "Stats storage");
		} catch (FileNotFoundException e1) {
			sLogger.logError("Unable to store properties file " + MD5Constants.PROPERTIES_FILE);
			fLogger.log("Unable to store properties file " + MD5Constants.PROPERTIES_FILE);
			fLogger.log("Exception Message: " + e1.getMessage());
		} catch (IOException e1) {
			sLogger.logError("Unable to store properties file " + MD5Constants.PROPERTIES_FILE);
			fLogger.log("Unable to store properties file " + MD5Constants.PROPERTIES_FILE);
			fLogger.log("Exception Message: " + e1.getMessage());
		}
	}
	
	public static boolean getEnableStats(){
		return enableStats;
	}
	
	public static int getNumberOfStatsKept(){
		return numberOfStatsKept;
	}
	
	public static String getDefaultDirectory() {
		return defaultDirectory;
	}

	public static void setDefaultDirectory(String defaultDirectory) {
		MD5GUI.defaultDirectory = defaultDirectory;
	}

	public static boolean isRecurseDirectory() {
		return recurseDirectory;
	}

	public static void setRecurseDirectory(boolean recurseDirectory) {
		MD5GUI.recurseDirectory = recurseDirectory;
	}

	public static void setEnableStats(boolean enableStats) {
		MD5GUI.enableStats = enableStats;
	}

	public static void setNumberOfStatsKept(int numberOfStatsKept) {
		MD5GUI.numberOfStatsKept = numberOfStatsKept;
	}
	
	public static void updateProgressBar(int amount){
		progressBar.setSelection(amount);
	}
	
	public static ProgressBar getProgressBar(){
		return progressBar;
	}
	
	/**
	 * Wrapper to update the UI without a invalid thread access exception.
	 * Sets the enabled flag to true or false.
	 * 
	 * @param d Display
	 * @param b Button
	 * @param v Boolean value
	 */
	public void setButtonEnabled(Display d, final Button b, final boolean v){
		d.asyncExec(
		new Runnable() {
		public void run(){
			b.setEnabled(v);
		}});
	}
	
	/**
	 * Sets the progress bar to enabled or disabled.
	 * @param d
	 * @param p
	 * @param v
	 */
	public void setProgressEnabled(Display d, final ProgressBar p, final boolean v){
		d.asyncExec(new Runnable() {
			public void run() {
				p.setVisible(v);
			}
		});
	}
	
	/**
	 * Changes the button text to the specified. I.e. to change Test --> Cancel.
	 * @param d
	 * @param b
	 * @param text
	 */
	public void changeButtonText(Display d, final Button b, final String text){
		d.asyncExec(new Runnable() {
			public void run() {
				b.setText(text);
			}
		});
	}
	
	/**
	 * Wrapper to update the UI without a invalid thread access exception.
	 * Sets the text or appends the text to the StyledText widget
	 * 
	 * @param d Display
	 * @param s StyledText
	 * @param m Message
	 * @param append Append or replace text
	 */
	public void setStyledText(Display d, final StyledText s, final String m, final boolean append){
		d.asyncExec(
		new Runnable() {
		public void run(){
			if(append){
				s.append(m);
			} else {
				s.setText(m);
			}
		}});		
	}
	
	/**
	 * Wrapper to update the UI without a invalid thread access exception.
	 * Displays a msgbox without a return value.
	 * 
	 * @param d Display
	 * @param s Shell
	 * @param buttons Buttons (OK, CANCEL, etc)
	 * @param text Header
	 * @param message Message to be displayed in the body of the msgbox
	 */
	public void displayMsgBox(Display d, final Shell s, final int buttons,final String text, final String message ){
		
		
		d.asyncExec(
		new Runnable() {
		public void run(){	    		
			MessageBox mb = new MessageBox(s, buttons);
			mb.setText(text);	
			mb.setMessage(message);
			mb.open();
		  }});
	}
	

	
	/**
	 * Does the actual testing calculations.  
	 * Encapsuled here to ease of use to exit the method.
	 */
	public void testMouseButtonEvent(){
		final MD5Functions md5Functions = new MD5Functions(sLogger, fLogger, display);
		final String md5Filename = testMd5FileStyledText.getText().trim();
		final boolean hasFile = !testFileToBeTestedStyledText.getText().equalsIgnoreCase("");
		final boolean hasMd5File = !md5Filename.equalsIgnoreCase("");
		final boolean hasMd5String = !testPasteTypeMd5styledText.getText().equalsIgnoreCase("");

		if(!hasFile){
			sLogger.logWarn("No file specified to test");
			return;
		}
		
		if(!hasMd5File && !hasMd5String){	
			sLogger.logWarn("No MD5 specified to test the file");
			return;
		}
		
		final String filename = testFileToBeTestedStyledText.getText().trim();
		final String md5String = testPasteTypeMd5styledText.getText().trim();
		
		//testButton.setEnabled(false);
		//progressBar.setVisible(true);
		
		final Thread testWorker = new Thread() {
	    	public void run() {
	    		final long timeAtStart = System.currentTimeMillis();
	    		String hash2 = null;
	    		
	    		// calculate MD5 for file
	    		final File file = new File(filename);
        		if(!file.exists()){
					sLogger.logError("Unable to open file " + file.getName());
					//setButtonEnabled(display, testButton, true);
					return;
				}
        		
				sLogger.log("Testing...");

        		final String hash = md5Functions.calculateMd5(file);

				if(hash == null){
					sLogger.logError("Failed to create hash for file " + filename);
					//setButtonEnabled(display, testButton, true);
					return;
				}
									
				if(hasMd5String){
					hash2 = md5String;
					
				} else { //if(hasMd5File)
					BufferedReader br = SimpleIO.openFileForInput(md5Filename);
					String line = null;
					Pattern pattern = null;
					Matcher matcher = null;
					
					if(br != null){
					
						try {
							while((line = br.readLine()) != null){
								for(String regex : regExPatterns){
									if(regex != null){
										pattern = Pattern.compile(regex);
										matcher = pattern.matcher(line);
										
										if(matcher.matches()){
											hash2 = matcher.group(1);
											break;
										}
									}
								}// end for
								
							}// end while
							
							final String hashFromFile = hash2;
							
							if(hashFromFile == null){
								StringBuilder sb = new StringBuilder();
								sb.append("The has file does not match the standard format.\n");
								sb.append("The standard format is as follows:\n");
								sb.append("32 characters plus optional characters");
								sb.append("\n");
								sb.append("Please be sure your MD5 file is properly formatted");
								
								displayMsgBox(display,shell,SWT.OK|SWT.ICON_WARNING,"MD5 File Format Problem", sb.toString());
								
							}
							
						} catch (final IOException e) {
							sLogger.logError("Unable to open MD5 file.  See log file for more info.");
							fLogger.log("Unable to open MD5 file. Error Message: " + e.getMessage());
						}
					} else { // br is null (can't open file)
							sLogger.logError("Unable to open file " + md5Filename);
					}
								
				}// end else
				
				final String hashDisplay = hash2;
				long currentTime = System.currentTimeMillis();
				final String timeElapsed = getTime(timeAtStart, currentTime, MD5Constants.TIME_FORMAT_SHORT);
				final String startTime = getTime(0, timeAtStart, MD5Constants.TIME_FORMAT_START_END);
				final String endTime = getTime(0, currentTime, MD5Constants.TIME_FORMAT_START_END);
				
					
				if(hash2 != null) {
							
					if(StringUtil.equalIgnoreCase(hash, hash2)){

						addTableItem(file.getName(), timeElapsed, startTime, endTime, 0);
													
						sLogger.log("File and MD5 hash are equal! " + timeElapsed + " seconds","dark-green");
						setStyledText(display,testResultStyledText,"Hashs are equal!",false);
						setStyledText(display,testResultStyledText,"\n"+hash+ MD5Constants.MD5_SPACER +file.getName(),true);
						setStyledText(display,testResultStyledText,"\n"+hashDisplay+" MD5 Hash",true);								
						
					} else {									
						sLogger.log("File and MD5 hash are NOT equal! " + timeElapsed + " seconds", "red");
						setStyledText(display,testResultStyledText,"Hashs are NOT equal!",false);
						setStyledText(display,testResultStyledText,"\n"+hash+ MD5Constants.MD5_SPACER +file.getName(),true);
						setStyledText(display,testResultStyledText,"\n"+hashDisplay+" MD5 Hash",true);
						
					}
				}
				
	    	}
		};
		
		testWorker.start();
	}
	
	/**
	 * Does the actual creation of the md5 calculations.  
	 * Encapsuled here to ease of use to exit the method.
	 *
	 */
	public void calculateMouseEvent(){
		final MD5Functions md5Functions = new MD5Functions(sLogger, fLogger, display, caculateResultStyledText);
		final String filename = calculateBrowseStyledText.getText().trim();
    	final String string = calculateStringStyledText.getText().trim();
    	
    	//calculateButton.setEnabled(false);
    	
	    final Thread worker = new Thread() {
	    	public void run() {

	        	final boolean hasFile = !(filename == null || filename.equalsIgnoreCase(""));
	        	final boolean hasString = !(string == null || string.equalsIgnoreCase(""));
	        	boolean isDirectory = false;
	        	final long timeAtStart = System.currentTimeMillis();
	        	
	        	// Check both fields, if empty, log msg to screen
	        	if(!hasFile && !hasString){
	        		sLogger.logWarn("No file, directory or String is specified. Please choose a file, directory or enter a String");
	        		//setButtonEnabled(display,calculateButton,true);
	        		
	        		return;
	        	}
	        	
	        	
	        	if(hasFile){
	        		
	        		final File file = new File(filename);
	        		// check if file exists
	        		if(!file.exists()){
	        			sLogger.logError("File does not exist! Not calculating MD5");
	        			//setButtonEnabled(display,calculateButton,true);
		        		
		        		return;
	        		}
	        		
	        		sLogger.log("Calculating MD5 for file " + file.getName() + "...");

		        	isDirectory = file.isDirectory();
		        	
		        	if(isDirectory){
		        		// Calculate for all files in this directory
		        		final String md5File = defaultDirectory+"\\"+ file.getName() + MD5Constants.MD5_EXT;
		        		
		        		if(SimpleIO.exists(md5File)){
			        		display.syncExec(
		        			new Runnable() {
		        			public void run(){
		        				msgBoxBool = false;
								MessageBox mb = new MessageBox(shell, SWT.YES | SWT.NO);
								mb.setText("File Already Exists");	
								mb.setMessage("File " + md5File + " already exists\n Overwrite?");
								if(mb.open() == SWT.NO){
						        	sLogger.logWarn("Chose not to overwrite the file. Did not create MD5(s)");
						        	msgBoxBool = true;
								}
				        	}});
							
			        		if(msgBoxBool){
			        			//setButtonEnabled(display,calculateButton,true);	
			        			return;
			        		}
		        		}
		        		
		        		PrintWriter pw = null;
		        		try {
		        			pw = SimpleIO.openFileForOutput(md5File);
		        		} catch (CanNotWriteToFileException e){
		        			sLogger.logError("Can not write to file " + md5File +". Did not create MD5(s)");
		        			//setButtonEnabled(display,calculateButton,true);
	        			
		        			return;
		        		}
		        		
		        		setStyledText(display,caculateResultStyledText,"",false);
		        		
		        		// recurse directories or not?
		        		if(recurseDirectory){
		        			md5Functions.recurseDirectory(file, pw);
		        		} else {
		        			md5Functions.singleDirectory(file, pw);
		        		}
		        		
		        	
        				long currentTime = System.currentTimeMillis();
						String timeElapsed = getTime(timeAtStart, currentTime, MD5Constants.TIME_FORMAT_SHORT);
						String startTime = getTime(0, timeAtStart, MD5Constants.TIME_FORMAT_START_END);
						String endTime = getTime(0, currentTime, MD5Constants.TIME_FORMAT_START_END);
						
						addTableItem(file.getName(), timeElapsed, startTime, endTime, 0);
						
        				sLogger.log("File Hash(s) created in file " + md5File + ". " + timeElapsed + " seconds");
        				//setButtonEnabled(display,calculateButton,true);	
		        		
		        		// close the file
		        		if(pw != null){
		        			pw.close();
		        		}
		        		
		        	} else {
		        		
		        		final String hash = md5Functions.calculateMd5(file);
		        		
						if(hash == null){
							sLogger.logError("Failed to create hash for file " + file.getName());
							//setButtonEnabled(display,calculateButton,true);
							return;
						}
						
        				long currentTime = System.currentTimeMillis();
						String timeElapsed = getTime(timeAtStart, currentTime, MD5Constants.TIME_FORMAT_SHORT);
						String startTime = getTime(0, timeAtStart, MD5Constants.TIME_FORMAT_START_END);
						String endTime = getTime(0, currentTime, MD5Constants.TIME_FORMAT_START_END);
						
						setStyledText(display,caculateResultStyledText,hash+MD5Constants.MD5_SPACER +file.getName(),false);
						
						addTableItem(file.getName(), timeElapsed, startTime, endTime, 0);
						
						sLogger.log("File Hash created. " + timeElapsed + " seconds");
						//setButtonEnabled(display,calculateButton,true);																		
		        	}
	        	}// end if(hasFile)
	        	
	        	if(hasString){
	        		final String hash = md5Functions.calculateMd5(string);			        		
    				
	        		sLogger.log("Calculating MD5 for string " + string + "...");

	        		if(hash == null){
	        			sLogger.logError("Failed to create hash for string " + string);
	        			//setButtonEnabled(display,calculateButton,true);
	        			return;
	        		}
	        		
    				long currentTime = System.currentTimeMillis();
					String timeElapsed = getTime(timeAtStart, currentTime, MD5Constants.TIME_FORMAT_SHORT);
					String startTime = getTime(0, timeAtStart, MD5Constants.TIME_FORMAT_START_END);
					String endTime = getTime(0, currentTime, MD5Constants.TIME_FORMAT_START_END);
					
	        		if(hasFile){
	        			setStyledText(display,caculateResultStyledText,"\n"+hash+ MD5Constants.MD5_SPACER +string,true);
	        		} else {
	        			setStyledText(display,caculateResultStyledText,hash+ MD5Constants.MD5_SPACER +string,false);
	        		}
					
					addTableItem(string, timeElapsed, startTime, endTime, 0);
					
	        		sLogger.log("Hash(s) created. " + timeElapsed + " seconds");	
	        		
	        		//setButtonEnabled(display,calculateButton,true);	
    				
	        	}// end if hasString	
	        }// end run()	
	    };
	    
	    worker.start();
	}
	
}
