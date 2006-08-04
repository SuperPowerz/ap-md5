package util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class SimpleIO {

	/**
	 * 
	 * @param filename
	 * @return
	 */
	public static PrintWriter openFileForOutput(String filename){
		if(filename == null){
			return null;
		}
		
		PrintWriter pw = null;
		
		try {
			pw = new PrintWriter(new FileOutputStream(filename));
		} catch (FileNotFoundException e) {
			return null;
		}
		
		return pw;
	}
	
	/**
	 * 
	 * @param filename
	 * @return
	 */
	public static BufferedReader openFileForInput(String filename){
		if(filename == null){
			return null;
		}
		
		BufferedReader br = null;
		
        try {
			br = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			return null;
		}
		
		return br;
	}
	
	/**
	 * 
	 * @param pw
	 */
	public static void close(PrintWriter pw){
		if(pw != null){
			pw.close();
		}
	}
	
	public static void close(BufferedReader br){
		if(br != null){
			try {
				br.close();
			} catch (IOException e) {
			}
		}
	}
}
