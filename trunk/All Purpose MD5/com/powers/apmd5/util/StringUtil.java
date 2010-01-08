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

package com.powers.apmd5.util;

public class StringUtil {

	private StringUtil(){}
	
	public static final String EMPTY_STRING = "";
		
	public static boolean equalIgnoreCase(String s1, String s2){
		boolean isEqual = true;
		
		if(s1 == null){
			if(s2 != null){
				isEqual = false;
			}
		} else {
			isEqual = s1.equalsIgnoreCase(s2);
		}
		
		return isEqual;
	}
	
	public static String trim(String str){
		if(isNotEmpty(str)){
			return str.trim();
		}
		return str;
	}
	
	public static boolean isEmpty(String str){
		return (str == null || EMPTY_STRING.equals(str));
	}
	
	public static boolean isNotEmpty(String str){
		return !isEmpty(str);
	}
	
	public static boolean isBlank(String str){
		if(!(str == null || EMPTY_STRING.equals(str))){
			String strTemp = str.trim();
			return (strTemp == null || EMPTY_STRING.equals(strTemp));
		}
		
		return true;
	}
	
	public static boolean isNotBlank(String str){
		return !isBlank(str);
	}
	
	public static String defaultString(String str, String defaultStr){
		if(isEmpty(str)){
			return defaultStr;
		}
		
		return str;
	}
	
	public static String defaultString(String str){
		return defaultString(str, EMPTY_STRING);
	}
}

