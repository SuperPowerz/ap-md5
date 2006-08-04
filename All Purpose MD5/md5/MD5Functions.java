package md5;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;

import util.FileLogger;
import util.ScreenLogger;

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
			md5.Update(string, null);
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
			
			pw.println(hash+" "+file.getName());
			
    		display.asyncExec(
			new Runnable() {
			public void run(){		
				resultText.append(hash+" "+file.getName()+"\n");
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
	    			pw.println(hash+" "+file.getName());
	    			
	        		display.asyncExec(
        			new Runnable() {
        			public void run(){		
        				resultText.append(hash+" "+file.getName());
	        		}});
	        		
					
				}// end if not directory
			}// end if != null 
		}// end for 
	}// end singleDirectory
}
