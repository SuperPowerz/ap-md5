package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import exceptions.CanNotWriteToFileException;

public class SimpleIO {

	/**
	 * 
	 * @param filename
	 * @return
	 */
	public static PrintWriter openFileForOutput(String filename) throws CanNotWriteToFileException {
		if(filename == null){
			return null;
		}
		
		PrintWriter pw = null;
		File file = new File(filename);
		if(!file.canWrite()){
			throw new CanNotWriteToFileException("Unable to write to file " + filename);
		}
		
		try {
			pw = new PrintWriter(new FileOutputStream(file));
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
	
	public static boolean exists(String filename){
		File file = new File(filename);
		return(file.exists());
	}
	
}
