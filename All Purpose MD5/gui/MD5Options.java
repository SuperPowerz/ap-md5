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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import util.FileLogger;
import util.MD5Constants;
import util.ScreenLogger;

import com.swtdesigner.SWTResourceManager;

public class MD5Options {

	private StyledText defaultDirectoryStyledText;
	private Text statsKeptText;
	protected Shell shell;
	private Display display = null;

	private ScreenLogger sLogger = null;
	private FileLogger fLogger = null;
	
	// Widgets
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
		shell.setImage(SWTResourceManager.getImage("images/apmd5.ico"));
		shell.setSize(517, 332);
		shell.setText("APMD5 Options");

		final Composite composite = new Composite(shell, SWT.NONE);
		composite.setBounds(0, 0, 509, 305);
		//composite.setBounds(location.x, location.y, WIDTH, HEIGHT);
		
		final Group statisticsGroup = new Group(composite, SWT.NONE);
		statisticsGroup.setText("Statistics");
		statisticsGroup.setBounds(10, 25, 210, 91);

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
		saveOptionsButton.setBounds(136, 269, 84, 25);

		final Button discardOptionsButton = new Button(composite, SWT.NONE);
		discardOptionsButton.addMouseListener(new MouseAdapter() {
			public void mouseUp(final MouseEvent e) {
				sLogger.log("Options discarded");
				shell.dispose();
			}
		});
		discardOptionsButton.setBounds(240, 269, 84, 25);
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
		resetOptionsButton.setBounds(136, 122, 84, 25);
		resetOptionsButton.setText("Reset Options");

		final Group miscGroup = new Group(composite, SWT.NONE);
		miscGroup.setText("Misc");
		miscGroup.setBounds(240, 25, 259, 205);

		recurseDirectoriesButton = new Button(miscGroup, SWT.CHECK);
		recurseDirectoriesButton.setText("Recurse Directories");
		recurseDirectoriesButton.setBounds(10, 27, 130, 25);

		defaultDirectoryStyledText = new StyledText(miscGroup, SWT.BORDER);
		defaultDirectoryStyledText.setWordWrap(true);
		defaultDirectoryStyledText.setBounds(10, 137, 239, 45);

		final CLabel defaultDirectoryLabel = new CLabel(miscGroup, SWT.NONE);
		defaultDirectoryLabel.setText("Default Directory");
		defaultDirectoryLabel.setBounds(10, 83, 89, 22);

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
		browseButton.setBounds(10, 111, 65, 20);
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
			return;

		} catch (IOException e1) {
			sLogger.logError("Unable to read properties file " + MD5Constants.PROPERTIES_FILE);
			fLogger.log("Unable to read properties file " + MD5Constants.PROPERTIES_FILE);
			fLogger.log("Exception Message: " + e1.getMessage());
			return;
		}
		
		statsEnabledButton.setSelection(("true").equalsIgnoreCase(props.getProperty(MD5Constants.STATS_ON)));
		statsKeptText.setText(props.getProperty(MD5Constants.STATS_NUMBER_KEPT));
		
		recurseDirectoriesButton.setSelection(("true").equalsIgnoreCase(props.getProperty(MD5Constants.RECURSE_DIRECTORY)));
		
		defaultDirectoryStyledText.setText(props.getProperty(MD5Constants.DEFAULT_DIRECTORY));
		
	}
}
