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

package com.powers.apmd5.checksum.md5;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;

import com.powers.apmd5.gui.MD5Constants;
import com.powers.apmd5.util.FileLogger;
import com.powers.apmd5.util.ScreenLogger;


public class MD5Functions {
	private ScreenLogger sLogger = null;
	private FileLogger fLogger = null;
	private Display display = null;
	private StyledText resultText = null;
	
	public MD5Functions(ScreenLogger sLogger, FileLogger fLogger, Display display){
		this.sLogger = sLogger;
		this.fLogger = fLogger;
		this.display = display;
	}
	
	public MD5Functions(ScreenLogger sLogger, FileLogger fLogger, Display display, StyledText resultText ){
		this.sLogger = sLogger;
		this.fLogger = fLogger;
		this.display = display;
		this.resultText = resultText;
	}
	
	public String calculateMd5(final File file){
		String hash = null;
		
		// calculate as file
		try {
			hash = MD5.asHex(MD5.getHash(file));
		} catch (final IOException e) {
			
			display.asyncExec(
			new Runnable() {
			public void run(){
				sLogger.logError("Hash Calculation Failed for file " + file.getName() + ". Please check log for more detail");
				fLogger.log("calculateButton.mouseUp", "Unable to create hash for file " + file.getName());
				fLogger.log("Exception Message: " + e.getMessage());
			}});
			
		}
		
		if(hash == null){
			sLogger.logError("Failed to create hash for file " + file.getName());
		}
		
		return hash;
	}// end calculateMd5(File)
	
	public String calculateMd5(final String string){
		String hash = null;
		
		MD5 md5 = new MD5();
	    try {
			md5.update(string, null);
		} catch (final UnsupportedEncodingException e) {
			
			display.asyncExec(
			new Runnable() {
			public void run(){
				sLogger.logError("UnsupportedEncodingException, please check log for more detail");
				fLogger.log("UnsupportedEncodingException while calculating MD5 for string " + string);
				fLogger.log("Exception Message: " + e.getMessage());
			}});
		}
		
		hash = md5.asHex();
		
		if(hash == null){
			sLogger.logError("Failed to create hash for string " + string);
		}
		
		return hash;
	}// end calculateMd5(String)
	
	public void recurseDirectory(final File file, PrintWriter pw){
		
		if(file.isDirectory()){
			File files[] = file.listFiles();
    		for(int i=0; i<files.length; i++){
    			if(files[i] != null){
    				recurseDirectory(files[i], pw);
    			}
    		}
    		
		} else {
    		display.asyncExec(
			new Runnable() {
			public void run(){		        				
				sLogger.log("Calculating MD5 for file " + file.getName() + "...");
    		}});
    					    			
			final String hash = calculateMd5(file);
			
			pw.println(hash+ MD5Constants.MD5_SPACER +file.getName());
			
    		display.asyncExec(
			new Runnable() {
			public void run(){		
				resultText.append(hash+ MD5Constants.MD5_SPACER +file.getName()+"\n");
    		}});
			
		}
	}// end recurseFiles
	
	public void singleDirectory(final File file, PrintWriter pw){
		
		File files[] = file.listFiles();
		
		for(int i=0; i<files.length; i++){
			if(files[i] != null){
				if(!files[i].isDirectory()){
	        		display.asyncExec(
        			new Runnable() {
        			public void run(){		
        				sLogger.log("Calculating MD5 for file " + file.getName() + "...");
	        		}});

	    			final String hash = calculateMd5(file);
	    			pw.println(hash+ MD5Constants.MD5_SPACER +file.getName());
	    			
	        		display.asyncExec(
        			new Runnable() {
        			public void run(){		
        				resultText.append(hash+ MD5Constants.MD5_SPACER +file.getName());
	        		}});
	        		
					
				}// end if not directory
			}// end if != null 
		}// end for 
	}// end singleDirectory
}
