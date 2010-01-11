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

package com.powers.apmd5.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.powers.apmd5.util.SimpleIO;
import com.powers.apmd5.util.StringUtil;

public class MD5Constants {
	
	// final constants
	public static final String STRING_COLLECTION_REGEX = "(\\[)(.*)(\\])";
	public static final char NULL_CHAR = '\u0000';
	
	public static final String HOME_DIR = System.getProperty("user.home");
	public static final String LOCAL_APP_DATA = System.getenv("LocalAppData");
	public static final String APP_DATA_SUB_DIR = "All Purpose MD5";
	public static final String WRITE_DIR = StringUtil.isEmpty(LOCAL_APP_DATA) ? StringUtil.EMPTY_STRING : (LOCAL_APP_DATA+File.separatorChar+APP_DATA_SUB_DIR);
	
	
	// changable constants
	public static String MD5_EXT = ".md5";
	public static String SHA_EXT = ".sha";
	
	public static String PROPERTIES_FILE = "properties/APMD5.properties";
	public static String PROPERTIES_FILE_DEFULT = "properties/APMD5_DEFAULT.properties";
	public static String STATS_PROPERTIES_FILE = "properties/stats.properties";
	public static String READ_ME_FILE = "properties/README";
	
	public static String LOG_FILE_NAME = "APMD5.log";
	public static List<String> REGEX_PATTERN_LIST = new ArrayList<String>();

	public static String README_FILE_NAME = "properties/README";
	public static String REGEX_DELIM = ":";
	public static String MD5_SPACER = " ";
	
	public static String ISSUE_URL = "http://code.google.com/p/ap-md5/issues/entry";
	public static String README_URL = "http://sites.google.com/site/allpurposemd5/readme";
	public static String HOME_URL = "http://sites.google.com/site/allpurposemd5/home";
	
	// Other constants
	public static String PROGRAM_NAME = "All Purpose MD5";
	public static String AUTHORS = "Nick Powers";
	public static String VERSION = "2.0";
	public static String WEBSITE = "http://code.google.com/p/ap-md5/";
	
	// Error Data
	public static boolean errorLastRun;
	public static String errorLastRunStackTrace;
	
	
	// ========================= Methods ================================
	public static String getFilePath(String path){
		if(StringUtil.isEmpty(WRITE_DIR)) { return path; }
		return WRITE_DIR+File.separator+path;
	}
	
	public static boolean loadOptions(String filename) {

		Properties props = new Properties();

		try {
			FileInputStream fis = new FileInputStream(new File(filename));
			props.load(fis);
			SimpleIO.closeQuietly(fis);

			String name;
			int mod;
			
			Field[] fields = MD5Constants.class.getDeclaredFields();
			for (Field field : fields) {
				if(field != null){
					name = field.getName();
					mod = field.getModifiers();
					
					if (Modifier.isPublic(mod) && !Modifier.isFinal(mod) && Modifier.isStatic(mod)
							&& props.containsKey(name)
							&& StringUtil.isNotEmpty((String)props.get(name))) {
						try {
							setFieldValue(field, (String)props.get(name), null);
						} catch (IllegalAccessException e) {
							e.printStackTrace();
							return false;
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
							return false;
						}
					}
					
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	
	public static void saveOptions(String filename){
		
		Properties props = new Properties();
		
		Field[] fields = MD5Constants.class.getDeclaredFields();
		String name;
		int mod;
		
		for (Field field : fields) {
			name = field.getName();
			mod = field.getModifiers();
			try {
				
				if(Modifier.isPublic(mod) && !Modifier.isFinal(mod) && Modifier.isStatic(mod)){
					Object o = field.get(null);
					if(o != null){
						props.put(name, o.toString());
					}
				}
				
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		try {
			
			final File file = new File(filename);
			if(!file.exists()){
				file.createNewFile();
			}
			
			FileOutputStream fos = new FileOutputStream(file);
		
			if(!props.isEmpty()){
				props.store(fos, "");
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static char convertStringToChar(String string) {
		char ch = NULL_CHAR;

		if (StringUtil.isNotEmpty(string)) {

			char[] c = string.toCharArray();
			if (c.length > 0) {
				ch = c[0];
			}
		}

		return ch;
	}

	@SuppressWarnings("unchecked")
	public static void setFieldValue(Field field, String value, Object obj) throws IllegalAccessException, IllegalArgumentException {
		String str = null;
		try {
			if (field.get(null) instanceof Collection) {
				Object o = field.get(null);
				str = getGenericTypeFromCollection(field);
				
				// Collection<Boolean>
				if (str.equals(Boolean.class.toString())) {
					Collection<Boolean> c = (Collection) o;
					c.add(Boolean.parseBoolean(value));
					// Collection<Character>
				} else if (str.equals(Character.class.toString())) {
					Collection<Character> c = (Collection) o;
					c.add(convertStringToChar(value));
					// Collection<Character>
				} else if (str.equals(Double.class.toString())) {
					Collection<Double> c = (Collection) o;
					c.add(Double.parseDouble(value));
					// Collection<Float>
				} else if (str.equals(Float.class.toString())) {
					Collection<Float> c = (Collection) o;
					c.add(Float.parseFloat(value));
					// Collection<Integer>
				} else if (str.equals(Integer.class.toString())) {
					Collection<Integer> c = (Collection) o;
					c.add(Integer.parseInt(value));
					// Collection<Long>
				} else if (str.equals(Long.class.toString())) {
					Collection<Long> c = (Collection) o;
					c.add(Long.parseLong(value));
					// Collection<Short>
				} else if (str.equals(Short.class.toString())) {
					Collection<Short> c = (Collection) o;
					c.add(Short.parseShort(value));
					// Collection<String> OR Collection
				} else {
					Collection<String> c = (Collection) o;
					getCollectionFromString(c, value);
				}
				// END Collection code
				
				// BEGIN Primitive/Class code
			} else if(field.get(null) instanceof Map){
				// KeyCharColors={"\=Color {0, 255, 0}, '\=Color {0, 255, 0}}
				// java.util.Map<java.lang.String, org.eclipse.swt.graphics.Color>
				List<String> types = getGenericTypesFromMap(field);
				putInMap(field, (Map)field.get(null), types, value);
			}
			
			// boolean or Boolean
			else if (field.getType().equals(Boolean.TYPE)
					|| field.getType().equals(Boolean.class)) {
				field.set(obj, Boolean.parseBoolean(value));
				// char or Character
			} else if (field.getType().equals(Character.TYPE)
					|| field.getType().equals(Character.class)) {
				field.set(obj, convertStringToChar(value));
				// double or Double
			} else if (field.getType().equals(Double.TYPE)
					|| field.getType().equals(Double.class)) {
				field.set(obj, Double.parseDouble(value));
				// float or Float
			} else if (field.getType().equals(Float.TYPE)
					|| field.getType().equals(Float.class)) {
				field.set(obj, Float.parseFloat(value));
				// int or Integer
			} else if (field.getType().equals(Integer.TYPE)
					|| field.getType().equals(Integer.class)) {
				field.set(obj, Integer.parseInt(value));
				// long or Long
			} else if (field.getType().equals(Long.TYPE)
					|| field.getType().equals(Long.class)) {
				field.set(obj, Long.parseLong(value));
				// short or Short
			} else if (field.getType().equals(Short.TYPE)
					|| field.getType().equals(Short.class)) {
				field.set(obj, Short.parseShort(value));
				// String
			} else if (field.getType().equals(String.class)) {
				field.set(obj, value);
			} else if(field.getType().equals(Color.class)) {
				Color c = getColorFromString(value);
				field.set(obj, c);
			} else {
				throw new IllegalArgumentException(
						"The constant "
								+ field.getName()
								+ " could not be updated to the value of "
								+ value
								+ ".  This may be due to the constant being an uninitialized Collection, or not being a supported datatype.");
			}

		} catch (IllegalAccessException e) {
			throw new IllegalAccessException(
					"An IllegalAccessException occurred while processing variable "
							+ field.getName() + ":  " + e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(
					"An IllegalArgumentException occurred while processing variable "
							+ field.getName() + ":  " + e.getMessage());
		}
	}
	
	private static Collection<String> getCollectionFromString(Collection<String> c, String value){
		if(StringUtil.isEmpty(value)) { return c; }
		
		Pattern pattern = Pattern.compile(STRING_COLLECTION_REGEX);
		Matcher matcher = pattern.matcher(value);
		
		if(matcher.matches() && matcher.groupCount() > 1){
			String v = matcher.group(2);
			if(StringUtil.isNotEmpty(v)){
				String[] split = v.split(",");
				for(String s : split){
					c.add(StringUtil.trim(s));
				}
			}
		}
		
		return c;
	}
	
	private static String getGenericTypeFromCollection(Field field){
		String str = "";
		try {
			// get generic type as a string
			str = field.getGenericType().toString();
			// split to pull out generic type only
			str = str.split("<")[1].split(">")[0];
			// add class to the beginning to match .class methods below
			str = "class " + str;
		} catch (ArrayIndexOutOfBoundsException e) {
			// if the passed Collection field has no generic type, it
			// will be treated as a string
			str = "";
		}
		
		return str;
	}
	
	// java.util.Map<java.lang.String, org.eclipse.swt.graphics.Color>
	private static List<String> getGenericTypesFromMap(Field field){
		if(field == null) { return null; }
		List<String> l = new LinkedList<String>();
		
		String typeAsString = field.getGenericType().toString();
		typeAsString = typeAsString.split("<")[1].split(">")[0];
		String[] classes = typeAsString.split(",");
		for(String s : classes){
			if(StringUtil.isNotBlank(s)){
				l.add("class "+StringUtil.trim(s));
			}
		}
		
		return l;
	}
	
	@SuppressWarnings("unchecked")
	private static void putInMap(Field field, Map m, List<String> types, String value){
		//String type;
		
		// KeyCharColors={"\=Color {0, 255, 0}, '\=Color {0, 255, 0}}
		//Object o = new Object();
		Pattern p = Pattern.compile("([{])(.*)([}}])");
		Matcher matcher = p.matcher(value);
		if(!matcher.matches() && matcher.groupCount() < 3){
			return;
		}

		String values = matcher.group(2);
		String[] keyValuePairs = values.split("}, ");
		for(String keyValuePair : keyValuePairs){
			String[] keyAndValue = keyValuePair.split("=");
			if(keyAndValue.length > 1){
				String key = keyAndValue[0];
				String v = keyAndValue[1];
				
				Object obj = getObjectInProperType(types.get(1), v);
				if(obj != null){
					m.put(key, obj);
				}
			}
		}
		
		try {
			field.set(null, m);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	private static Object getObjectInProperType(String type, String value){
		Object o = null;
		
		if (type.equals(Boolean.class.toString())) {
			o = Boolean.parseBoolean(value);
		} else if (type.equals(Character.class.toString())) {
			o = convertStringToChar(value);
		} else if (type.equals(Double.class.toString())) {
			o = Double.parseDouble(value);
		} else if (type.equals(Float.class.toString())) {
			o = Float.parseFloat(value);
		} else if (type.equals(Integer.class.toString())) {
			o = Integer.parseInt(value);
		} else if (type.equals(Long.class.toString())) {
			o = Long.parseLong(value);
		} else if (type.equals(Short.class.toString())) {
			o = Short.parseShort(value);
		} else if (type.equals(Color.class.toString())){
			o = getColorFromString(value);
		} else {
			o = value;
		}
		
		return o;
	}
	
	private static Color getColorFromString(String value){
		value = value.replace("Color {","");
		value = value.replace("}","");
		String[] split = value.split(",");
		int red = Integer.parseInt(StringUtil.trim(split[0]));
		int green = Integer.parseInt(StringUtil.trim(split[1]));
		int blue = Integer.parseInt(StringUtil.trim(split[2]));
		RGB rgb = new RGB(red, green, blue);
		Color c = new Color(Display.getCurrent(), rgb);
		
		return c;
	}
}
