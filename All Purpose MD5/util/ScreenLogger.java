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
		logObject.setText(message);
	}
	
	public void log(String message, String color){
		logObject.setText(message);
		logObject.setStyleRange(new StyleRange(0, message.length(), getColor(color), getColor(BACKGROUND_COLOR), SWT.NONE));
	}
	
	public void log(String message, String color, int fontStyle){
		logObject.setText(message);
		logObject.setStyleRange(new StyleRange(0, message.length(), getColor(color), getColor(BACKGROUND_COLOR), fontStyle));
	}
	
	public void logAppend(String message){
		logObject.append(message);
	}
	
	public void logAppend(String message, String color){
		int length = message.length() + logObject.getText().length();
		logObject.append(message);
		logObject.setStyleRange(new StyleRange(0, length, getColor(color), getColor(BACKGROUND_COLOR), SWT.NONE));
	}
	
	public void logAppend(String message, String color, int fontStyle){
		int length = message.length() + logObject.getText().length();
		logObject.append(message);
		logObject.setStyleRange(new StyleRange(0, length, getColor(color), getColor(BACKGROUND_COLOR), fontStyle));
	}
	
	public void logWarn(String message){
		log(WARNING+message, WARNING_COLOR);
	}
	
	public void logError(String message){
		log(ERROR+message, ERROR_COLOR);
	}
	
	public void clear(){
		logObject.setText("");
	}
	
	private Color getColor(String color){
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
