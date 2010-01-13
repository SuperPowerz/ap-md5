package com.powers.apmd5.util;

import com.powers.apmd5.gui.APMD5;

public class SystemOutLogger {

	
	private SystemOutLogger () {}
	
	
	public static void log(String s){
		System.out.println(s);
	}
	
	public static void log(boolean debug, String s){
		if(debug){
			System.out.println(s);
		}
	}
	
	public static void debug(String s){
		if(APMD5.DEBUG){
			System.out.println(s);
		}
	}
}
