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

	
//	private static String convertToHex(byte[] data) {
//		StringBuffer buf = new StringBuffer();
//		for (int i = 0; i < data.length; i++) {
//			int halfbyte = (data[i] >>> 4) & 0x0F;
//			int two_halfs = 0;
//			do {
//				if ((0 <= halfbyte) && (halfbyte <= 9))
//					buf.append((char) ('0' + halfbyte));
//				else
//					buf.append((char) ('a' + (halfbyte - 10)));
//				halfbyte = data[i] & 0x0F;
//			} while (two_halfs++ < 1);
//		}
//		return buf.toString();
//	}

	public  String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		//FileInputStream fis = new FileInputStream(file);
	//	byte[] dataBytes = new byte[1024];
		//int nread = 0;
		
//		while((nread = fis.read(dataBytes)) != -1){
//			md.update(dataBytes, 0, nread);
//		}
		md.update(text.getBytes("iso-8859-1"), 0, text.length());
		byte[] mdbytes = md.digest();
		return byteToHex(mdbytes);
//		MessageDigest md;
//		md = MessageDigest.getInstance("SHA-1");
//		byte[] sha1hash = new byte[40];
//		md.update(text.getBytes("iso-8859-1"), 0, text.length());
//		sha1hash = md.digest();
//		return convertToHex(sha1hash);
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
		
		MessageDigest md = MessageDigest.getInstance("SHA-1");
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
