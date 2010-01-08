package com.powers.apmd5.gui;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import com.powers.apmd5.checksum.ChecksumCalculator;
import com.powers.apmd5.exceptions.CanNotWriteToFileException;
import com.powers.apmd5.util.MD5Constants;
import com.powers.apmd5.util.ScreenLogger;
import com.powers.apmd5.util.SimpleIO;
import com.powers.apmd5.util.StringUtil;

public class GUIHelper {

	private static long MAX_CHECKSUM_WAIT = 300;
	private static TimeUnit MAX_CHECKSUM_WAIT_TIMEUNIT = TimeUnit.SECONDS;
	
	public static class ChecksumTestResult {
		public String fileToBeTestedResult;
		public String otherResult;
		public boolean matches;
		public String filename;
		
		public ChecksumTestResult(String fileToBeTestedResult, String otherResult, boolean matches, String filename){
			this.fileToBeTestedResult = fileToBeTestedResult;
			this.otherResult = otherResult;
			this.matches = matches;
			this.filename = filename;
		}
	}
	
	public static class TestAChecksum implements Callable<ChecksumTestResult> {

		private final ChecksumCalculator checkSum;
		private final String str;
		private final File file;
		private final File file2;

		public TestAChecksum(ChecksumCalculator checkSum, File file, String str) {
			this.checkSum = checkSum;
			this.str = str;
			this.file = file;
			this.file2 = null;
		}

		public TestAChecksum(ChecksumCalculator checkSum, File file, File file2) {
			this.checkSum = checkSum;
			this.file = file;
			this.file2 = file2;
			this.str = null;
		}

		public ChecksumTestResult call() throws Exception {

			String fileToBeTestedResult = checkSum.calculate(file);
			String otherChecksum = StringUtil.EMPTY_STRING;
			if (file2 == null) {
				otherChecksum = str;
			} else {
				otherChecksum = checkSum.calculate(file2);
			}
			
			boolean matches = fileToBeTestedResult.equalsIgnoreCase(otherChecksum);
			return new ChecksumTestResult(fileToBeTestedResult, otherChecksum, matches, file.getName());
		}

	}

	public static class PopulateChecksum implements Callable<Boolean>{
		private final Future<ChecksumTestResult> future;
		private final APMD5 apmd5;
		
		public PopulateChecksum(APMD5 apmd5, Future<ChecksumTestResult> future){
			this.future = future;
			this.apmd5 = apmd5;
		}
		public Boolean call() throws Exception {
			final ChecksumTestResult result = future.get(MAX_CHECKSUM_WAIT, MAX_CHECKSUM_WAIT_TIMEUNIT);
			
			final StringBuilder sb = new StringBuilder("File and checksum are ");
			if(result.matches){
				sb.append("equal!");
				apmd5.sLogger.log(sb.toString(), "dark-green");
			} else {
				sb.append("NOT equal!");
				apmd5.sLogger.log(sb.toString(),"red");
			}
			sb.append("\n");
			sb.append(result.fileToBeTestedResult).append(" (").append(result.filename).append(")").append("\n");
			sb.append(result.otherResult).append("\n");
			
			apmd5.display.syncExec(
					new Runnable() {
					public void run(){
			
				apmd5.testResultStyledText.setText(sb.toString());
				apmd5.setStatusIcon(result.matches);
						
			}});

			
			return result.matches;
		}
		
	}
	
	public static class ChecksumCalcResult {
		Map<String,String> checkSumPairs = new HashMap<String,String>();
		
	}
	
    public static class CalculateAChecksum implements Callable<ChecksumCalcResult> {
    	private final APMD5 apmd5;
    	private final ChecksumCalculator checksumCalculator;
    	private final String str;
    	private final File file;
    	private PrintWriter pw;
    	private final File toWrite;
    	private final boolean recurse;
    	
    	public CalculateAChecksum(APMD5 apmd5, ChecksumCalculator checksumCalculator, String str) {
    		this. apmd5 = apmd5;
    		this.checksumCalculator =checksumCalculator;
    		this.str = str;
    		this.file = null;
    		this.pw = null;
    		this.toWrite = null;
    		this.recurse = false;
    	}
    	
    	public CalculateAChecksum(APMD5 apmd5, ChecksumCalculator checksumCalculator, File file, File toWrite, boolean recurse) {
    		this. apmd5 = apmd5;
    		this.checksumCalculator =checksumCalculator;
    		this.file = file;
    		this.str = null;
    		this.toWrite = toWrite;
    		this.recurse = recurse;
    	}
    	
		@Override
		public ChecksumCalcResult call() throws Exception {
	
    		try {
    			if(toWrite != null) {
    				pw = SimpleIO.openFileForOutput(toWrite);
    			}
    		} catch (CanNotWriteToFileException ex){
    			apmd5.sLogger.logError("Can not write to file " + toWrite.getName() +". Did not create checksum(s)");	
    			throw ex;
    		}
			
			
			try {
				ChecksumCalcResult result = new ChecksumCalcResult();
				
				String checksum = StringUtil.EMPTY_STRING;
				if(file == null){
					checksum = checksumCalculator.calculate(str);
					updateResult(checksum+ MD5Constants.MD5_SPACER +"("+str+")\n");
					if(pw != null) { pw.println(checksum+ MD5Constants.MD5_SPACER +file.getName());}
					
				} else {
					if(file.isDirectory()){
						if(recurse){
							recurseDirectory(file, pw);
						} else {
							singleDirectory(file, pw);
						}
						
					} else {
						updateStatus("Calculating checksum for "+file.getName()+"...");
						checksum = checksumCalculator.calculate(file);
						updateResult(checksum+ MD5Constants.MD5_SPACER +"("+file.getName()+")\n");
						if(pw != null) { pw.println(checksum+ MD5Constants.MD5_SPACER +file.getName());}
					}
				}

				
				updateStatus("Finished. ");
				if(toWrite != null){
					appendStatus("Successfully created file "+toWrite.getName()+"!");
				}
				
				return result;
			} finally {
				SimpleIO.closeQuietly(pw);
			}
		}
		
		public void updateStatus(final String s){
			apmd5.display.asyncExec(
					new Runnable() {
					public void run(){		        				
						apmd5.sLogger.log(s);
		    		}});
		}
		public void appendStatus(final String s){
			apmd5.display.asyncExec(
					new Runnable() {
					public void run(){		        				
						apmd5.sLogger.logAppend(s);
		    		}});
		}
		
		public void updateResult(final String s){
			apmd5.display.asyncExec(
					new Runnable() {
					public void run(){		        				
						apmd5.caculateResultStyledText.append(s);
		    		}});
		}
		
//		public void updateProgressBar(){
//			apmd5.display.syncExec(
//					new Runnable() {
//					public void run(){
//						apmd5.progressBar.setSelection(apmd5.progressBar.getSelection()+1);
//		    		}});
//		}
		
		public void recurseDirectory(final File file, final PrintWriter pw){
			
			if(file.isDirectory()){
				File files[] = file.listFiles();
	    		for(int i=0; i<files.length; i++){
	    			if(files[i] != null){
	    				recurseDirectory(files[i], pw);
	    			}
	    		}
	    		
			} else {
				updateStatus("Calculating MD5 for file " + file.getName() + "...");	  
				
				final String hash = checksumCalculator.calculate(file);
				
				if(pw != null) { pw.println(hash+ MD5Constants.MD5_SPACER +file.getName());}
				updateResult(hash+ MD5Constants.MD5_SPACER +"("+file.getName()+")\n");
			}
		}// end recurseFiles
		
		public void singleDirectory(final File file, PrintWriter pw){
			
			File files[] = file.listFiles();
			
			for(int i=0; i<files.length; i++){
				if(files[i] != null){
					if(!files[i].isDirectory()){
						updateStatus("Calculating MD5 for file " + files[i].getName() + "...");
						
						final String hash = checksumCalculator.calculate(files[i]);
						
		    			if(pw != null) { pw.println(hash+ MD5Constants.MD5_SPACER +files[i].getName());}
		    			updateResult(hash+ MD5Constants.MD5_SPACER +"("+files[i].getName()+")\n");
		        		
					}// end if not directory
				}// end if != null 
			}// end for 
		}// end singleDirectory
		
		
    	
    }
	
    public static class PopulateCalcChecksum implements Callable<Boolean> {
    	private final Future<ChecksumCalcResult> result;
    	private final APMD5 apmd5;
    	
    	public PopulateCalcChecksum(Future<ChecksumCalcResult> result, APMD5 apmd5){
    		this.result = result;
    		this.apmd5 = apmd5;
    	}
    	
		@Override
		public Boolean call() throws Exception {
			// TODO Auto-generated method stub
			
			//caculateResultStyledText
			return null;
		}
    	
    }
    
	public static class IndeterminentProgressBar implements Callable<String> {

		ProgressBar pb = null;
		Shell shell = null;
		AtomicBoolean isExecuting = null;

		public IndeterminentProgressBar(Shell shell, ProgressBar pb,
				AtomicBoolean isExecuting) {
			this.shell = shell;
			this.pb = pb;
			this.isExecuting = isExecuting;
		}

		public String call() {
			final int inc = 10;
			final long sleepTime = 250;

			isExecuting.set(true);

			shell.getDisplay().syncExec(new Runnable() {
				public void run() {
					pb.setSelection(0);
				}
			});

			while (isExecuting.get()) {
				try {
					shell.getDisplay().syncExec(new Runnable() {
						public void run() {
							if (pb.getSelection() >= 100) {
								pb.setSelection(0);
							} else {
								pb.setSelection(pb.getSelection() + inc);
							}
						}
					});
					Thread.sleep(sleepTime);

				} catch (InterruptedException e) {
					isExecuting.set(false);
				}

			}
			shell.getDisplay().syncExec(new Runnable() {
				public void run() {
					pb.setSelection(100);
				}
			});

			isExecuting.set(false);

			return "done";

		}

	}
	/*
	 * 			display.asyncExec(
					new Runnable() {
					public void run(){
						
					}});
	 */
}
