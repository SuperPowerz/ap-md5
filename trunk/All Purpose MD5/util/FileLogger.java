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

package util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class FileLogger {

	//private PrintStream out = null;
	private FileWriter fWriter = null;
	private boolean isClosed = true;
	private static final String DEFAULT_FILENAME = MD5Constants.LOG_FILE_NAME;
	private String filename = null;
	private final StringBuffer logBuffer = new StringBuffer();
	private boolean willAppend = true;
	
	public FileLogger(){
		if(fWriter == null){
			openLog(DEFAULT_FILENAME);
		}
		
		isClosed = false;
		filename = DEFAULT_FILENAME;
	}
	
	public FileLogger(String logName){
		if(logName == null){
			logName = DEFAULT_FILENAME;
		}
		openLog(logName);
		isClosed = false;
		filename = logName;
	}
	
	public void log(String message){
		if(message == null){
			return;
		}
		
		if(isClosed){
			reopen();
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
		
		if(isClosed){
			reopen();
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
	
	public void reopen(){
		openLog(filename);
		isClosed = false;
	}
	
	public boolean isOpen(){
		return (!isClosed);
	}
	
	public void setWillAppend(boolean bool){
		willAppend = bool;
	}
	
	private void openLog(String filename){
		try {
			fWriter = new FileWriter(filename, willAppend);
		} catch (IOException e) {
			System.out.println("Had IO problems writing to file " + filename);
			e.printStackTrace();
		}
	}
	
	private Date getDate(){
		return new Date();
	}
	
}
