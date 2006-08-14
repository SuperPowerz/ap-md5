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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class ScreenLogger {

	private StyledText logObject = null;
	
	private static final String WARNING = "WARNING: ";
	private static final String ERROR = "ERROR: ";
	
	private static final String WARNING_COLOR = "dark-yellow";
	private static final String ERROR_COLOR = "red";
	
	private static final String BACKGROUND_COLOR = "white";
	
	public ScreenLogger(StyledText textBox){
		logObject = textBox;
	}
	
	public void log(String message){
		if(message == null){
			return;
		}
		
		logObject.setText(message);
	}
	
	public void log(String message, String color){
		if(message == null || color == null){
			return;
		}
		
		logObject.setText(message);
		logObject.setStyleRange(new StyleRange(0, message.length(), getColor(color), getColor(BACKGROUND_COLOR), SWT.NONE));
	}
	
	public void log(String message, String color, int fontStyle){
		if(message == null || color == null){
			return;
		}
		
		logObject.setText(message);
		logObject.setStyleRange(new StyleRange(0, message.length(), getColor(color), getColor(BACKGROUND_COLOR), fontStyle));
	}
	
	public void logAppend(String message){
		if(message == null){
			return;
		}
		
		logObject.append(message);
	}
	
	public void logAppend(String message, String color){
		if(message == null || color == null){
			return;
		}
		
		int length = message.length() + logObject.getText().length();
		logObject.append(message);
		logObject.setStyleRange(new StyleRange(0, length, getColor(color), getColor(BACKGROUND_COLOR), SWT.NONE));
	}
	
	public void logAppend(String message, String color, int fontStyle){
		if(message == null || color == null){
			return;
		}
		
		int length = message.length() + logObject.getText().length();
		logObject.append(message);
		logObject.setStyleRange(new StyleRange(0, length, getColor(color), getColor(BACKGROUND_COLOR), fontStyle));
	}
	
	public void logWarn(String message){
		if(message == null ){
			return;
		}
		
		log(WARNING+message, WARNING_COLOR);
	}
	
	public void logError(String message){
		if(message == null){
			return;
		}
		
		log(ERROR+message, ERROR_COLOR);
	}
	
	public void clear(){
		logObject.setText("");
	}
	
	private Color getColor(String color){
		if(color == null){
			return Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
		}
		
		Color realColor = null;
		
		if(color.equalsIgnoreCase("red")){
			realColor = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
		} else if(color.equalsIgnoreCase("black")){
			realColor = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
		} else if(color.equalsIgnoreCase("yellow")){
			realColor = Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW);
		} else if(color.equalsIgnoreCase("dark-yellow")){
			realColor = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_YELLOW);
		} else if(color.equalsIgnoreCase("green")){
			realColor = Display.getCurrent().getSystemColor(SWT.COLOR_GREEN);
		} else if(color.equalsIgnoreCase("dark-green")){
			realColor = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN);
		} else if(color.equalsIgnoreCase("blue")){
			realColor = Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
		} else if(color.equalsIgnoreCase("white")){
			realColor = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
		} else {
			realColor = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
		}
		
		return realColor;
	}
	
}
