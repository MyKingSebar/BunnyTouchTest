package com.ys.powerservice.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileUtils {
	/**
	 * Determine whether a file exists
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean isExist(String fileName) {
		if (fileName == null) {
			return false;
		}

		File file = new File(fileName);
		return file.exists();
	}
	
	 /**
     * Create a new file
     * 
     * @param fileName
     * @return
     * @throws IOException
     */
    public static File createFile(String fileName) throws IOException
    {
        File file = new File(fileName);
        if (!file.exists())
        {
            file.createNewFile();
        }
        return file;
    }
    
    public static void writeTextToFile(String file, String txt) {
		if ((file == null) || (txt == null)) {
			Logger.i("file or txt is null.");
			return;
		}
		OutputStreamWriter output = null;
		try {
			output = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
			output.write(txt);
			output.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
    
    public static String readTextFromFile(String file) {
		if ((file == null) || !isExist(file)) {
			Logger.i("file is null or doesn't exist.");
			return null;
		}
		String result = null;
        InputStreamReader intput = null;
        try {
        	char[] buffer = new char[1024];
            int bytesRead = 0;
        	StringBuffer sb = new StringBuffer();
			intput = new InputStreamReader(new FileInputStream(file), "UTF-8");
			while ((bytesRead = intput.read(buffer)) != -1) {
				sb.append(buffer, 0, bytesRead);
			}
			result = sb.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (intput != null) {
				try {
					intput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
        return result;
	}
}
