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

public interface MD5Constants {
	
	public static String HOME_DIR = System.getProperty("user.home");
	
	public static String MD5_EXT = ".md5";
	
	public static String PROPERTIES_FILE = "properties/APMD5.properties";
	public static String PROPERTIES_FILE_DEFULT = "properties/APMD5_DEFAULT.properties";
	public static String STATS_PROPERTIES_FILE = "properties/stats.properties";
	public static String READ_ME_FILE = "properties/README";
	
	public static String LOG_FILE_NAME = "APMD5.log";
	
	public static String RECURSE_DIRECTORY = "recurse.directory";

	public static String STATS_ON = "stats.on";
	public static String STATS_NUMBER_KEPT = "stats.number.kept";
	public static String STATS_PROP_BASE_NAME = "stats.";
	
	public static String DEFAULT_DIRECTORY = "default.directory";
	
	public static int STATS_NUM_COLS = 5;
	
	public static String REGEX_PATTERNS = "regex.patterns";
	
	public static String README_FILE_NAME = "properties/README";
	
	public static String REGEX_DELIM = ":";
	
	public static String TIME_FORMAT = "mm:ss:SS";
	public static String TIME_FORMAT_SHORT = "ss:SS";
	public static String TIME_FORMAT_START_END = "hh:mm:ss:SS";
	
	public static String MD5_SPACER = " ";
	
	// Other constants
	public static String PROGRAM_NAME = "All Purpose MD5";
	public static String AUTHORS = "Nick Powers";
	public static String VERSION = "1.0";
	public static String WEBSITE = "http://powers.nick.googlepages.com/home";
	
}
