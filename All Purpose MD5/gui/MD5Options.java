package gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import util.FileLogger;
import util.MD5Constants;
import util.ScreenLogger;

import com.swtdesigner.SWTResourceManager;

public class MD5Options {

	private StyledText defaultDirectoryStyledText;
	private CLabel textLabel;
	private CLabel foregroundLabel;
	private Text statsKeptText;
	protected Shell shell;
	private Display display = null;

	private ScreenLogger sLogger = null;
	private FileLogger fLogger = null;
	
	// Widgets
	private CLabel backgroundLabel = null;
	private Button foregroundButton = null;
	private Button statsEnabledButton = null;
	private Button recurseDirectoriesButton = null;
	private Button browseButton = null;

	
	public MD5Options(ScreenLogger sLogger, FileLogger fLogger){
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
		
		loadProperties();
		
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
		shell.setImage(SWTResourceManager.getImage(MD5Options.class, "/images/apmd5_icon.png"));
		shell.setSize(517, 418);
		shell.setText("APMD5 Options");

		final Composite composite = new Composite(shell, SWT.NONE);
		composite.setBounds(0, 0, 509, 384);

		final Group colorGroup = new Group(composite, SWT.NONE);
		colorGroup.setText("Color");
		colorGroup.setBounds(15, 25, 210, 146);

		final Button backgroundButton = new Button(colorGroup, SWT.NONE);
		backgroundButton.addMouseListener(new MouseAdapter() {
			public void mouseUp(final MouseEvent e) {
				ColorDialog cd = new ColorDialog(shell);
				cd.open();
				if(cd.getRGB() != null){
					backgroundLabel.setBackground(new Color(Display.getDefault(), cd.getRGB()));
				}
			}
		});
		backgroundButton.setText("Background");
		backgroundButton.setBounds(15, 25, 80, 20);

		final Button textButton = new Button(colorGroup, SWT.NONE);
		textButton.addMouseListener(new MouseAdapter() {
			public void mouseUp(final MouseEvent e) {
				ColorDialog cd = new ColorDialog(shell);
				cd.open();

				if(cd.getRGB() != null){
					textLabel.setBackground(new Color(Display.getDefault(), cd.getRGB()));
				}
			}
		});
		textButton.setBounds(15, 92, 80, 20);
		textButton.setText("Text");

		backgroundLabel = new CLabel(colorGroup, SWT.BORDER);
		backgroundLabel.setBackground(SWTResourceManager.getColor(234, 234, 234));
		backgroundLabel.setBounds(115, 25, 25, 20);

		foregroundButton = new Button(colorGroup, SWT.NONE);
		foregroundButton.addMouseListener(new MouseAdapter() {
			public void mouseUp(final MouseEvent e) {
				ColorDialog cd = new ColorDialog(shell);
				cd.open();
				if(cd.getRGB() != null){
					foregroundLabel.setBackground(new Color(Display.getDefault(), cd.getRGB()));
				}
			}
		});
		foregroundButton.setBounds(15, 58, 80, 20);
		foregroundButton.setText("Foreground");

		foregroundLabel = new CLabel(colorGroup, SWT.BORDER);
		foregroundLabel.setBounds(115, 58, 25, 20);
		foregroundLabel.setBackground(SWTResourceManager.getColor(234, 234, 234));

		textLabel = new CLabel(colorGroup, SWT.BORDER);
		textLabel.setBounds(115, 92, 25, 20);
		textLabel.setBackground(SWTResourceManager.getColor(234, 234, 234));

		final Label notCurrentlyLabel = new Label(colorGroup, SWT.NONE);
		notCurrentlyLabel.setText("** not currently supported");
		notCurrentlyLabel.setBounds(10, 123, 140, 18);

		final Group statisticsGroup = new Group(composite, SWT.NONE);
		statisticsGroup.setText("Statistics");
		statisticsGroup.setBounds(15, 177, 210, 91);

		statsEnabledButton = new Button(statisticsGroup, SWT.CHECK);
		statsEnabledButton.setText("Stats Enabled");
		statsEnabledButton.setBounds(28, 26, 120, 18);

		statsKeptText = new Text(statisticsGroup, SWT.BORDER);
		statsKeptText.setBounds(25, 55, 30, 20);

		final CLabel numberOfStatsLabel = new CLabel(statisticsGroup, SWT.NONE);
		numberOfStatsLabel.setText("Number of Stats Kept");
		numberOfStatsLabel.setBounds(59, 54, 120, 21);

		final Button saveOptionsButton = new Button(composite, SWT.NONE);
		saveOptionsButton.addMouseListener(new MouseAdapter() {
			public void mouseUp(final MouseEvent e) {
				// Save to variables
				MD5GUI.setEnableStats(statsEnabledButton.getSelection());
				MD5GUI.setDefaultDirectory(defaultDirectoryStyledText.getText());
				MD5GUI.setNumberOfStatsKept(Integer.parseInt(statsKeptText.getText()));
				MD5GUI.setRecurseDirectory(recurseDirectoriesButton.getSelection());
				
				// Save to properties file
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
				
				props.setProperty(MD5Constants.BACKGROUND_COLOR_R, Integer.toString(backgroundLabel.getBackground().getRed()));
				props.setProperty(MD5Constants.BACKGROUND_COLOR_G, Integer.toString(backgroundLabel.getBackground().getGreen()));
				props.setProperty(MD5Constants.BACKGROUND_COLOR_B, Integer.toString(backgroundLabel.getBackground().getBlue()));
				
				props.setProperty(MD5Constants.FOREGROUND_COLOR_R, Integer.toString(foregroundLabel.getBackground().getRed()));
				props.setProperty(MD5Constants.FOREGROUND_COLOR_G, Integer.toString(foregroundLabel.getBackground().getGreen()));
				props.setProperty(MD5Constants.FOREGROUND_COLOR_B, Integer.toString(foregroundLabel.getBackground().getBlue()));
				
				props.setProperty(MD5Constants.TEXT_COLOR_R, Integer.toString(textLabel.getBackground().getRed()));
				props.setProperty(MD5Constants.TEXT_COLOR_G, Integer.toString(textLabel.getBackground().getGreen()));
				props.setProperty(MD5Constants.TEXT_COLOR_B, Integer.toString(textLabel.getBackground().getBlue()));
				
				props.setProperty(MD5Constants.STATS_ON, Boolean.toString(statsEnabledButton.getSelection()));
				props.setProperty(MD5Constants.STATS_NUMBER_KEPT, statsKeptText.getText());
				
				props.setProperty(MD5Constants.RECURSE_DIRECTORY, Boolean.toString(recurseDirectoriesButton.getSelection()));
				
				props.setProperty(MD5Constants.DEFAULT_DIRECTORY, defaultDirectoryStyledText.getText());
				
				try {
					props.store(new FileOutputStream(new File(MD5Constants.PROPERTIES_FILE)), "Options are being saved");
				} catch (FileNotFoundException e1) {
					sLogger.logError("Unable to store properties file " + MD5Constants.PROPERTIES_FILE_DEFULT);
					fLogger.log("Unable to store properties file " + MD5Constants.PROPERTIES_FILE_DEFULT);
					fLogger.log("Exception Message: " + e1.getMessage());
				} catch (IOException e1) {
					sLogger.logError("Unable to store properties file " + MD5Constants.PROPERTIES_FILE_DEFULT);
					fLogger.log("Unable to store properties file " + MD5Constants.PROPERTIES_FILE_DEFULT);
					fLogger.log("Exception Message: " + e1.getMessage());
				}

				sLogger.log("Options saved");
				shell.dispose();
				
			}
		});
		saveOptionsButton.setText("Save Options");
		saveOptionsButton.setBounds(148, 335, 84, 25);

		final Button discardOptionsButton = new Button(composite, SWT.NONE);
		discardOptionsButton.addMouseListener(new MouseAdapter() {
			public void mouseUp(final MouseEvent e) {
				sLogger.log("Options discarded");
				shell.dispose();
			}
		});
		discardOptionsButton.setBounds(265, 335, 84, 25);
		discardOptionsButton.setText("Discard Options");

		final Button resetOptionsButton = new Button(composite, SWT.NONE);
		resetOptionsButton.addMouseListener(new MouseAdapter() {
			public void mouseUp(final MouseEvent e) {

				Properties defaultProps = new Properties();
				
				// Open default properties
				try {
					defaultProps.load(new FileInputStream(new File(MD5Constants.PROPERTIES_FILE_DEFULT)));
				} catch (FileNotFoundException e1) {
					sLogger.logError("Unable to find properties file " + MD5Constants.PROPERTIES_FILE_DEFULT);
					fLogger.log("Unable to find properties file " + MD5Constants.PROPERTIES_FILE_DEFULT);
					fLogger.log("Exception Message: " + e1.getMessage());
				} catch (IOException e1) {
					sLogger.logError("Unable to read properties file " + MD5Constants.PROPERTIES_FILE_DEFULT);
					fLogger.log("Unable to read properties file " + MD5Constants.PROPERTIES_FILE_DEFULT);
					fLogger.log("Exception Message: " + e1.getMessage());
				}
				
				MD5GUI.setEnableStats(defaultProps.getProperty(MD5Constants.STATS_ON).equalsIgnoreCase("true"));
				MD5GUI.setDefaultDirectory(defaultProps.getProperty(MD5Constants.DEFAULT_DIRECTORY));
				MD5GUI.setNumberOfStatsKept(Integer.parseInt(defaultProps.getProperty(MD5Constants.STATS_NUMBER_KEPT)));
				MD5GUI.setRecurseDirectory(defaultProps.getProperty(MD5Constants.RECURSE_DIRECTORY).equalsIgnoreCase("true"));
				
				try {
					defaultProps.store(new FileOutputStream(new File(MD5Constants.PROPERTIES_FILE)), "Reset to defaults");
				} catch (FileNotFoundException e1) {
					sLogger.logError("Unable to store properties file " + MD5Constants.PROPERTIES_FILE_DEFULT);
					fLogger.log("Unable to store properties file " + MD5Constants.PROPERTIES_FILE_DEFULT);
					fLogger.log("Exception Message: " + e1.getMessage());
				} catch (IOException e1) {
					sLogger.logError("Unable to store properties file " + MD5Constants.PROPERTIES_FILE_DEFULT);
					fLogger.log("Unable to store properties file " + MD5Constants.PROPERTIES_FILE_DEFULT);
					fLogger.log("Exception Message: " + e1.getMessage());
				}
				
				sLogger.log("Options reset");
				shell.dispose();

			}
		});
		resetOptionsButton.setBounds(10, 274, 84, 25);
		resetOptionsButton.setText("Reset Options");

		final Group miscGroup = new Group(composite, SWT.NONE);
		miscGroup.setText("Misc");
		miscGroup.setBounds(240, 25, 259, 184);

		recurseDirectoriesButton = new Button(miscGroup, SWT.CHECK);
		recurseDirectoriesButton.setText("Recurse Directories");
		recurseDirectoriesButton.setBounds(10, 27, 130, 25);

		defaultDirectoryStyledText = new StyledText(miscGroup, SWT.BORDER);
		defaultDirectoryStyledText.setWordWrap(true);
		defaultDirectoryStyledText.setBounds(10, 127, 239, 45);

		final CLabel defaultDirectoryLabel = new CLabel(miscGroup, SWT.NONE);
		defaultDirectoryLabel.setText("Default Directory");
		defaultDirectoryLabel.setBounds(10, 77, 89, 22);

		browseButton = new Button(miscGroup, SWT.NONE);
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
		browseButton.setBounds(10, 101, 65, 20);
		//
	}

	private void loadProperties(){
		
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
		
		int br = Integer.parseInt(props.getProperty(MD5Constants.BACKGROUND_COLOR_R));
		int bg = Integer.parseInt(props.getProperty(MD5Constants.BACKGROUND_COLOR_G));
		int bb = Integer.parseInt(props.getProperty(MD5Constants.BACKGROUND_COLOR_B));
		
		backgroundLabel.setBackground(new Color(Display.getDefault(), new RGB(br, bg, bb)));
		
		int fr = Integer.parseInt(props.getProperty(MD5Constants.FOREGROUND_COLOR_R));
		int fg = Integer.parseInt(props.getProperty(MD5Constants.FOREGROUND_COLOR_G));
		int fb = Integer.parseInt(props.getProperty(MD5Constants.FOREGROUND_COLOR_B));
		
		foregroundLabel.setBackground(new Color(Display.getDefault(), new RGB(fr, fg, fb)));
		
		int tr = Integer.parseInt(props.getProperty(MD5Constants.TEXT_COLOR_R));
		int tg = Integer.parseInt(props.getProperty(MD5Constants.TEXT_COLOR_G));
		int tb = Integer.parseInt(props.getProperty(MD5Constants.TEXT_COLOR_B));
		
		textLabel.setBackground(new Color(Display.getDefault(), new RGB(tr, tg, tb)));

		
		statsEnabledButton.setSelection(("true").equalsIgnoreCase(props.getProperty(MD5Constants.STATS_ON)));
		statsKeptText.setText(props.getProperty(MD5Constants.STATS_NUMBER_KEPT));
		
		recurseDirectoriesButton.setSelection(("true").equalsIgnoreCase(props.getProperty(MD5Constants.RECURSE_DIRECTORY)));
		
		defaultDirectoryStyledText.setText(props.getProperty(MD5Constants.DEFAULT_DIRECTORY));
		
	}
}
