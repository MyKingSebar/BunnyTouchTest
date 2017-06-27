/*
 * Copyright (C) 2013 poster PCE YoungSee Inc. 
 * All Rights Reserved Proprietary and Confidential.
 * 
 * @author LiLiang-Ping
 */

package com.example.screenmanagertest.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.TextUtils;


import com.example.screenmanagertest.PosterApplication;
import com.example.screenmanagertest.screenmanager.MediaInfoRef;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint("DefaultLocale")
public class FileUtils
{
    public final static String    ENCODING = "UTF-8";
    
    private final static Object mKeyLock      = new Object();
    private final static Object mAuthcodeLock = new Object();
    
    private FileUtils()
    {
        /*
         * This Class is a single instance mode, and define a private constructor to avoid external use the 'new'
         * keyword to instantiate a objects directly.
         */
    }
    

    /*
     * 获取获取存储的路径
     */
    public static String getExternalStorage()
    {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }



    /**
     * 格式化文件路径，将其中不规范的分隔转换为标准的分隔符,并且去掉末尾的文件路径分隔符。 本方法操作系统自适应
     *
     * @param path
     *            文件路径
     * @return 格式化后的文件路径
     */
    public static String formatPath4File(String path)
    {
        String reg0 = "\\\\+";
        String reg = "\\\\+|/+";
        String temp = path.trim().replaceAll(reg0, "/");
        temp = temp.replaceAll(reg, "/");
        if (temp.length() > 1 && temp.endsWith("/"))
        {
            temp = temp.substring(0, temp.length() - 1);
        }
        temp = temp.replace('/', File.separatorChar);
        return temp;
    }

    /**
     * 格式化文件路径，将其中不规范的分隔转换为标准的分隔符 并且去掉末尾的"/"符号(适用于FTP远程文件路径或者Web资源的相对路径)。
     *
     * @param path
     *            文件路径
     * @return 格式化后的文件路径
     */
    public static String formatPath4FTP(String path)
    {
        String reg0 = "\\\\+";
        String reg = "\\\\+|/+";
        String temp = path.trim().replaceAll(reg0, "/");
        temp = temp.replaceAll(reg, "/");
        if (temp.length() > 1 && temp.endsWith("/"))
        {
            temp = temp.substring(0, temp.length() - 1);
        }
        return temp;
    }

    /**
     * Create a new directory
     *
     * @param dirName
     * @return
     * @throws IOException
     */
    public static void createDir(String dirName)
    {
        if (dirName != null)
        {
            File dir = new File(dirName);
            if (!dir.exists())
            {
                dir.mkdirs();
            }
        }
    }

    public static long getFileLength(String filePathName)
    {
        File file = new File(filePathName);

        if (!file.exists())
        {
            return 0L;
        }

        return file.length();
    }

    public static String getFileAbsolutePath(String fullPath)
    {
        String strPath = formatPath4File(fullPath);
        if(strPath.lastIndexOf(File.separator) != -1)
        {
            return strPath.substring(0, strPath.lastIndexOf(File.separator));
        }
        return null;
    }

    /**
     *
     * 从文件路径中获取文件名称
     *
     * @param path
     *            文件路径
     *
     * @return
     */
    public static String getFilename(String path)
    {
        String strPath = formatPath4File(path);
        int separatorIdx = strPath.lastIndexOf(File.separator);
        return (separatorIdx < 0) ? strPath : strPath.substring(separatorIdx + 1, strPath.length());
    }

    /**
     *
     * 获取文件扩展名
     *
     * @param filename
     *            文件名
     *
     * @return
     */
    public static String getFileExtensionName(String filename)
    {
        if ((filename != null) && (filename.length() > 0))
        {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1)))
            {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /**
     *
     * 获取文件夹中所有的文件列表
     *
     *            文件名
     *
     * @return
     */
    public static boolean getFileList(HashMap<String, String> outFileLists, String filePath, boolean subFolderFlag)
    {
        if (outFileLists == null)
        {
            outFileLists = new HashMap<String, String>();
        }

        File file = new File(filePath);
        if (file.exists())
        {
            StringBuilder sb = new StringBuilder();
            File files[] = file.listFiles();
            for (int i = 0; i < files.length; i++)
            {
                sb.setLength(0);
                sb.append(filePath).append("/").append(files[i].getName());

                if (files[i].isFile())
                {
                    outFileLists.put(files[i].getName(), sb.toString());
                }
                else if (files[i].isDirectory() && subFolderFlag)
                {
                    getFileList(outFileLists, sb.toString(), subFolderFlag);
                }
            }
        }
        else
        {
            return false;
        }

        return true;
    }

    /**
     *
     * 获取文件夹中所有的文件列表,并组成相应String返回服务器
     *
     *
     * @return
     */
    public static String getFileListString(String filePath, boolean subFolderFlag)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"").append(filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length())).append("\":[");
        File file = new File(filePath);
        if (file.exists())
        {
            File files[] = file.listFiles();
            for (int i = 0; i < files.length; i++)
            {
                if (files[i].isFile())
                {
                    sb.append("\"").append(files[i].getName()).append("\",");
                }
                else if (files[i].isDirectory() && subFolderFlag)
                {
                    sb.append(getFileListString(filePath + "/" + files[i].getName(), subFolderFlag));
                }
            }
            sb.append("]},");
        }
        return sb.toString();
    }

    public static boolean isGifFile(String strPath)
    {
        String strFileName = getFilename(strPath);
        String strExtensionName = getFileExtensionName(strFileName);
        return("gif".equals(strExtensionName.toLowerCase()));
    }

    public static boolean setFileLastTime(String fileName, long millis)
    {
        File f = new File(fileName);
        return f.setLastModified(millis);
    }

    /**
     * 删除一个目录（可以是非空目录）
     *
     */
    public static boolean delDir(String dirName)
    {
        File dir = new File(dirName);

        if (dir == null || !dir.exists() || !dir.isDirectory())
        {
            return false;
        }

        for (File file : dir.listFiles())
        {
            if (file.isFile())
            {
                file.delete();
            }
            else if (file.isDirectory())
            {
                delDir(file.getAbsolutePath());// 递归
            }
        }

        try
        {
            dir.delete();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 清空指定目录
     *
     * @param dir
     */
    public static boolean cleanupDir(String dir) {
        File dirfile = new File(dir);

        if (dirfile == null || !dirfile.exists() || !dirfile.isDirectory()) {
            return false;
        }

        for (File file : dirfile.listFiles()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                delDir(file.getAbsolutePath());
            }
        }

        return true;
    }

    /**
     * 拷贝目录下的所有文件到指定目录
     *
     * @return
     * @throws IOException
     */
    public static boolean copyDirFilesTo(String strSrcDir, String strDestDir) throws IOException
    {
        if (strSrcDir == null || strDestDir == null)
        {
            return false;
        }
        File srcDir = new File(strSrcDir);
        File destDir = new File(strDestDir);

        if (!srcDir.isDirectory())
        {
            return false;// 判断是否是目录
        }

        if (!destDir.exists())
        {
            createDir(strDestDir);
        }

        File destFile = null;
        File[] srcFiles = srcDir.listFiles();
        for (int i = 0; i < srcFiles.length; i++)
        {
            destFile = new File(destDir.getAbsolutePath() + File.separator + srcFiles[i].getName());
            if (srcFiles[i].isFile())
            {
                if (!copyFileTo(srcFiles[i], destFile))
                {
                    return false;
                }
            }
            else if (srcFiles[i].isDirectory())
            {
                if (!copyDirFilesTo(srcFiles[i].getAbsolutePath(), destFile.getAbsolutePath()))
                {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 移动目录下的所有文件到指定目录
     *
     * @return
     * @throws IOException
     */
    public static boolean moveDirFilesTo(String strSrcDir, String strDestDir) throws IOException
    {
        if (strSrcDir == null || strDestDir == null)
        {
            return false;
        }
        File srcDir = new File(strSrcDir);
        File destDir = new File(strDestDir);

        if (!destDir.exists())
        {
            createDir(strDestDir);
        }
        if (!srcDir.isDirectory() || !destDir.isDirectory())
        {
            return false;
        }

        File[] srcDirFiles = srcDir.listFiles();
        for (int i = 0; i < srcDirFiles.length; i++)
        {
            File oneDestFile = new File(destDir.getPath() + File.separator + srcDirFiles[i].getName());
            if (srcDirFiles[i].isFile())
            {
                moveFileTo(srcDirFiles[i], oneDestFile);
            }
            else if (srcDirFiles[i].isDirectory())
            {
                moveDirFilesTo(srcDirFiles[i].getAbsolutePath(), oneDestFile.getAbsolutePath());
                srcDirFiles[i].delete();
            }
        }

        return true;
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

    /**
     * Delete a file
     *
     * @return
     * @throws IOException
     */
    public static boolean delFile(File file)
    {
        if (file == null || !file.exists() || file.isDirectory())
        {
            return false;
        }

        return file.delete();
    }

    public static boolean delFile(String path)
    {
    	if (!isExist(path))
    	{
    		return false;
    	}

    	File file = new File(path);
    	return file.delete();
    }

    public static boolean isEmpty(String dir)
    {
    	File dirfile = new File(dir);

        if (dirfile == null || !dirfile.exists() || !dirfile.isDirectory()) {
            return false;
        }

        if (dirfile.listFiles().length > 0) {
        	return false;
        }

        return true;
    }

    /**
     * 拷贝一个文件,srcFile源文件，destFile目标文件
     *
     * @param srcFile
     * @param destFile
     * @throws IOException
     */
    public static boolean copyFileTo(File srcFile, File destFile) throws IOException
    {
        if (srcFile.isDirectory() || destFile.isDirectory())
        {
            return false;
        }

        FileInputStream fis = new FileInputStream(srcFile);
        FileOutputStream fos = new FileOutputStream(destFile);

        int readLen = 0;
        byte[] buf = new byte[1024];
        while ((readLen = fis.read(buf)) != -1)
        {
            fos.write(buf, 0, readLen);
        }
        fos.flush();

        fos.close();
        fis.close();
        return true;
    }

    /**
     * 移动一个文件
     *
     * @param srcFile
     * @param destFile
     * @return
     * @throws IOException
     */
    public static boolean moveFileTo(File srcFile, File destFile) throws IOException
    {
        boolean iscopy = copyFileTo(srcFile, destFile);
        if (!iscopy)
        {
            return false;
        }

        delFile(srcFile);
        return true;
    }

    /**
     * Determine whether a file exists
     *
     * @param fileName
     * @return
     */
    public static boolean isExist(String fileName)
    {
        if (fileName == null)
        {
            return false;
        }

        File file = new File(fileName);
        return file.exists();
    }

    /**
     * Determine whether SD card is mount
     *
     * @return
     */
    public static boolean isSDCardMount()
    {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * Write text data to the SD card file
     *
     * @param fileName
     * @param text
     * @return
     */
    public static File writeSDFileData(String fileName, String text, boolean isAppend)
    {
        try
        {
            return writeSDFileData(fileName, text.getBytes(), isAppend);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Write byte data to the SD card file
     *
     * @param fileName
     * @param data
     * @return
     */
    public static File writeSDFileData(String fileName, byte[] data, boolean isAppend)
    {
        File file = null;
        OutputStream output = null;

        try
        {
            file = createFile(fileName);
            if (isAppend)
            {
                output = new FileOutputStream(file, true); // Append to the file
            }
            else
            {
                output = new FileOutputStream(file);
            }
            output.write(data);
            output.flush();
        }
        catch (Exception e)
        {
            file = null;
            Logger.e("writeSDFileData() has error.");
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (output != null) output.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return file;
    }

    /**
     * Write a input stream to the SD card file
     *
     * @param fileName
     * @param input
     * @return
     */
    public static File writeSDFileData(String fileName, InputStream input, boolean isAppend)
    {
        File file = null;
        OutputStream output = null;

        try
        {
            file = createFile(fileName);
            if (isAppend)
            {
                output = new FileOutputStream(file, true); // Append to the file
            }
            else
            {
                output = new FileOutputStream(file);
            }
            byte[] buffer = new byte[1024];
            int nLength = 0;

            while ((nLength = (input.read(buffer))) > 0)
            {
                output.write(buffer, 0, nLength);
            }

            output.flush();
        }
        catch (Exception e)
        {
            file = null;
            Logger.e("writeSDFileData() has error.");
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (input != null) input.close();
                if (output != null) output.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return file;
    }

    /**
     * Read byte data from the SD card file
     *
     * @param strFileName
     * @return
     */
    public static byte[] readSDFile(String strFileName)
    {
        byte[] buffer = null;
        FileInputStream fIn = null;

        try
        {
            fIn = new FileInputStream(strFileName);
            int length = fIn.available(); // 获取文件长度
            buffer = new byte[length]; // 创建byte数组用于读入数据
            fIn.read(buffer); // 将文件内容读入到byte数组中
        }
        catch (Exception e)
        {
            Logger.e("readSDFileData() has error.");
            e.printStackTrace();// 捕获异常并打印
        }
        finally
        {
            try
            {
                if (fIn != null) fIn.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return buffer;
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

    /**
     * Write byte data to the private file (which is in the 'data/data/' file)
     *
     * @param fileName
     * @return
     */
    public static File wirteDataFile(String fileName, String message)
    {
        File file = null;
        OutputStream output = null;
        Context context = (Context) PosterApplication.getInstance();

        try
        {
            file = createFile(fileName);
            output = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            byte[] bytes = message.getBytes();
            output.write(bytes);
            output.flush();
        }
        catch (Exception e)
        {
            file = null;
            Logger.e("wirteDataFile() has error.");
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (output != null) output.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return file;
    }


    public static String readTextFile(String filePath)
    {
        String dest = "";
        InputStream is = null;
        BufferedReader reader = null;
        try
        {
            String str = "";
            StringBuffer sb = new StringBuffer();
            is = new FileInputStream(filePath);
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            while ((str = reader.readLine()) != null)
            {
                sb.append(str + "\n");
            }

            // 去掉非法字符
            Pattern p = Pattern.compile("(\ufeff)");
            Matcher m = p.matcher(sb.toString());
            dest = m.replaceAll("");
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (is != null)
                {
                    is.close();
                }

                if (reader != null)
                {
                    reader.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return dest;
    }

    /**
     * 获取服务器文件
     * @param remoteurl 远端文件（URL）
     * @param localfile 本地文件（全路径）
     * @return file 本地文件
     */
    public static File getServerFile(String remoteurl, String localfile)
    {
    	if ((remoteurl == null) || (localfile == null)) {
    		return null;
    	}
        File file = null;
        HttpURLConnection con = null;
        InputStream in = null;
        System.setProperty("http.keepAlive", "false");
        try
        {
            URL url = new URL(remoteurl);
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(60 * 1000);
            con.setReadTimeout(90 * 1000);
            con.setDoInput(true);
            in = con.getInputStream();
            file = FileUtils.writeSDFileData(localfile, in, false);
        }
        catch (IOException e)
        {
            Logger.e("Failed to get the server file: " + remoteurl);
            e.printStackTrace();

        }
        finally
        {
        	if (in != null)
        	{
        		try
        		{
					in.close();
				}
        		catch (IOException e)
				{
					e.printStackTrace();
				}
        	}

            if (con != null)
            {
                con.disconnect();
            }
        }

        return file;
    }

    public static boolean mediaIsGifFile(MediaInfoRef mediaInfo)
    {
        return ("File".equals(mediaInfo.source) && isGifFile(mediaInfo.filePath));
    }

    public static boolean mediaIsPicFromFile(MediaInfoRef mediaInfo)
    {
        return ("File".equals(mediaInfo.source) && "Image".equals(mediaInfo.mediaType));
    }

    public static boolean mediaIsPicFromNet(MediaInfoRef mediaInfo)
    {
        return (!"File".equals(mediaInfo.source) && "Image".equals(mediaInfo.mediaType));
    }

    public static boolean mediaIsTextFromFile(MediaInfoRef mediaInfo)
    {
        return ("File".equals(mediaInfo.source) && "Text".equals(mediaInfo.mediaType));
    }

    public static boolean mediaIsTextFromNet(MediaInfoRef mediaInfo)
    {
        return (!"File".equals(mediaInfo.source) && "Text".equals(mediaInfo.mediaType));
    }

    public static boolean mediaIsVideo(MediaInfoRef mediaInfo)
    {
        return ("Video".equals(mediaInfo.mediaType));
    }

    public static boolean mediaIsFile(MediaInfoRef mediaInfo)
    {
        return ("File".equals(mediaInfo.source));
    }





}