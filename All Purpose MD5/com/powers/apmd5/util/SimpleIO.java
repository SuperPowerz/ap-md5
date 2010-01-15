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

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import com.powers.apmd5.exceptions.CanNotWriteToFileException;


public class SimpleIO {

	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	
	/**
	 * 
	 * @param filename
	 * @return
	 */
	public static PrintWriter openFileForOutput(File file) throws CanNotWriteToFileException {
		if(file == null){
			return null;
		}
		
		PrintWriter pw = null;
		if(file.exists()){
			if(!file.canWrite()){
				throw new CanNotWriteToFileException("Unable to write to file " + file.getName());
			}
		}
		
		try {
			pw = new PrintWriter(new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			return null;
		}
		
		return pw;
	}
	
	public static PrintWriter openFileForOutput(String filename) throws CanNotWriteToFileException {
		return openFileForOutput(new File(filename));
	}
	
	/**
	 * 
	 * @param filename
	 * @return
	 */
	public static BufferedReader openFileForInput(String filename){
		if(filename == null){ return null; }
		
		return openFileForInput(new File(filename));
	}
	
	/**
	 * 
	 * @param file
	 * @return
	 */
	public static BufferedReader openFileForInput(File file){
		if(file == null){ return null;}
		
		BufferedReader br = null;
		
        try {
			br = new BufferedReader(new FileReader(file));
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
	
	public static void closeQuietly(Closeable stream){
		if(stream == null){ return; }
		try {
			stream.close();
		} catch (IOException e) {
			// ignore
		}
	}
	
	public static boolean exists(String filename){
		File file = new File(filename);
		return(file.exists());
	}
	

    /**
     * Copies a file to a new location.
     * <p>
     * This method copies the contents of the specified source file
     * to the specified destination file.
     * The directory holding the destination file is created if it does not exist.
     * If the destination file exists, then this method will overwrite it.
     *
     * @param srcFile  an existing file to copy, must not be <code>null</code>
     * @param destFile  the new file, must not be <code>null</code>
     * @param preserveFileDate  true if the file date of the copy
     *  should be the same as the original
     *
     * @throws NullPointerException if source or destination is <code>null</code>
     * @throws IOException if source or destination is invalid
     * @throws IOException if an IO error occurs during copying
     * @see #copyFileToDirectory(File, File, boolean)
     * <br><br>Code taken from http://commons.apache.org/io/
     */
	public static void copyFile(File srcFile, File destFile) throws IOException {
		assert srcFile != null;
		assert destFile != null;
		
		 if (srcFile.exists() == false) {
	            throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
	        }
	        if (srcFile.isDirectory()) {
	            throw new IOException("Source '" + srcFile + "' exists but is a directory");
	        }
	        if (srcFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {
	            throw new IOException("Source '" + srcFile + "' and destination '" + destFile + "' are the same");
	        }
	        if (destFile.getParentFile() != null && destFile.getParentFile().exists() == false) {
	            if (destFile.getParentFile().mkdirs() == false) {
	                throw new IOException("Destination '" + destFile + "' directory cannot be created");
	            }
	        }
	        if (destFile.exists() && destFile.canWrite() == false) {
	            throw new IOException("Destination '" + destFile + "' exists but is read-only");
	        }
	        
	        doCopyFile(srcFile, destFile, true);
	}
	
    /**
     * Internal copy file method.
     * 
     * @param srcFile  the validated source file, must not be <code>null</code>
     * @param destFile  the validated destination file, must not be <code>null</code>
     * @param preserveFileDate  whether to preserve the file date
     * @throws IOException if an error occurs
     * <br><br>Code taken from http://commons.apache.org/io/
     */
    private static void doCopyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
        if (destFile.exists() && destFile.isDirectory()) {
            throw new IOException("Destination '" + destFile + "' exists but is a directory");
        }

        FileInputStream input = new FileInputStream(srcFile);
        try {
            FileOutputStream output = new FileOutputStream(destFile);
            try {
            	copyLarge(input, output);
            } finally {
            	closeQuietly(output);
            }
        } finally {
            closeQuietly(input);
        }

        if (srcFile.length() != destFile.length()) {
            throw new IOException("Failed to copy full contents from '" +
                    srcFile + "' to '" + destFile + "'");
        }
        if (preserveFileDate) {
            destFile.setLastModified(srcFile.lastModified());
        }
    }
    
    /**
     * Copy bytes from a large (over 2GB) <code>InputStream</code> to an
     * <code>OutputStream</code>.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     * 
     * @param input  the <code>InputStream</code> to read from
     * @param output  the <code>OutputStream</code> to write to
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException if an I/O error occurs
     * @since Commons IO 1.3
     * <br><br>Code taken from http://commons.apache.org/io/
     */
    private static long copyLarge(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
    
	/**
	 * Gets all the text from the given file.
	 * @param f File to get the text from
	 * @return the contents of the file
	 */
	public static String getText(File f){
		StringBuffer sb = new StringBuffer();
		String line = null;
		
		try {
		BufferedReader reader = new BufferedReader(new FileReader(f));
		while ((line=reader.readLine()) != null) {
			sb.append(line);
			sb.append("\n");
		}
		} catch(Exception e){
			e.printStackTrace();
			return null;
		}

		return sb.toString();
	}
}
