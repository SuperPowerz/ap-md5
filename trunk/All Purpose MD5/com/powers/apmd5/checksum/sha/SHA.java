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
package com.powers.apmd5.checksum.sha;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.powers.apmd5.checksum.ChecksumCalculator;
import com.powers.apmd5.util.StringUtil;

public class SHA implements ChecksumCalculator {
	private static final String SHA1 = "SHA-1";
	private static final String CHAR_SET = "iso-8859-1";
	


	public  String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance(SHA1);
		md.update(text.getBytes(CHAR_SET), 0, text.length());
		byte[] mdbytes = md.digest();
		return byteToHex(mdbytes);

	}
	
	/**
	 * 
	 * @param file
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * <br><br>Reference: http://www.mkyong.com/java/how-to-generate-a-file-checksum-value-in-java/
	 */
	public  String SHA1(File file) throws NoSuchAlgorithmException, IOException {
		if (!file.exists()) throw new FileNotFoundException(file.toString());
		
		MessageDigest md = MessageDigest.getInstance(SHA1);
		FileInputStream fis = new FileInputStream(file);
		byte[] dataBytes = new byte[1024];
		int nread = 0;
		
		while((nread = fis.read(dataBytes)) != -1){
			md.update(dataBytes, 0, nread);
		}
		byte[] mdbytes = md.digest();
		
		return byteToHex(mdbytes);
	}
	
	/**
	 * Converts bytes into a hex format.
	 * @param data
	 * @return
	 */
	private  String byteToHex(final byte[] data){
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<data.length; i++){
			sb.append(Integer.toString((data[i] & 0xff) + 0x100, 16).substring(1));
		}

		return sb.toString();
	}

	public String calculate(String str) {
		try {
			return SHA1(str);
		} catch (Exception e) {
			e.printStackTrace();
			return StringUtil.EMPTY_STRING;
		}
	}

	public String calculate(File file) {
		try {
			return SHA1(file);
		} catch (Exception e) {
			e.printStackTrace();
			return StringUtil.EMPTY_STRING;
		}
		
	}
}
