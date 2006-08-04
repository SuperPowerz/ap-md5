package util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class FileLogger {

	//private PrintStream out = null;
	private FileWriter fWriter = null;
	private boolean isClosed = true;
	private static final String DEFAULT_FILENAME = MD5Constants.LOG_FILE_NAME;
	private final StringBuffer logBuffer = new StringBuffer();
	private boolean willAppend = true;
	
	public FileLogger(){
		if(fWriter == null){
			openLog(DEFAULT_FILENAME);
		}
		
		isClosed = false;
	}
	
	public FileLogger(String logName){
		if(logName == null){
			logName = DEFAULT_FILENAME;
		}
		openLog(logName);
		isClosed = false;
	}
	
	public void log(String message){
		if(message == null){
			return;
		}
		
		logBuffer.append(getDate());
		logBuffer.append(" | ");
		logBuffer.append(message);
		logBuffer.append("\n");
		
		try {
			fWriter.write(logBuffer.toString());
		} catch (IOException e) {
			System.out.println("Had IO problems trying to write message " + message);
			e.printStackTrace();
		}
		logBuffer.setLength(0);
	}
	
	public void log(String method, String message){
		if(method == null || message == null){
			return;
		}
		
		logBuffer.append(new Date());
		logBuffer.append(" | ");
		logBuffer.append("Method=");
		logBuffer.append(method);
		logBuffer.append(" | ");
		logBuffer.append(message);
		logBuffer.append("\n");
		
		try {
			fWriter.write(logBuffer.toString());
		} catch (IOException e) {
			System.out.println("Had IO problems trying to write message " + message);
			e.printStackTrace();
		}
		logBuffer.setLength(0);
	}
	
	public void close(){
		if(fWriter != null){
			try {
				fWriter.close();
			} catch (IOException e) {
				System.out.println("Had problems trying to close log file");
				e.printStackTrace();
			}
			isClosed = true;
		}
	}
	
	public boolean isOpen(){
		return (!isClosed);
	}
	
	
	private void openLog(String filename){
		try {
			fWriter = new FileWriter(filename, willAppend);
		} catch (IOException e) {
			System.out.println("Had IO problems writing to file " + filename);
			e.printStackTrace();
		}
	}
	
	public void setWillAppend(boolean bool){
		willAppend = bool;
	}
	
	private Date getDate(){
		return new Date();
	}
	
}
