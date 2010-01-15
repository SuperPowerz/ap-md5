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
