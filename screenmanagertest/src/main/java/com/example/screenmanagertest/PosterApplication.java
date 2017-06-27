package com.example.screenmanagertest;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;

import com.example.screenmanagertest.common.DiskLruCache;
import com.example.screenmanagertest.common.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by admin on 2017-06-23.
 */

public class PosterApplication extends Application{
    private static String                   mStandbyScreenImgFullPath      = null;

    private static int ScreenHeight=0;
    private static int ScreenWidth=0;
    private static PosterApplication        INSTANCE                       = null;

    // Define the image cache (per APP instance)
    private static LruCache<String, Bitmap> mImgMemoryCache          = null;

    // Define the image disk cache (per APP instance)
    private static DiskLruCache             mImgDiskCache                  = null;
    private static final int                DISK_CACHE_SIZE                = 1024 * 1024 * 40; // 40MB

    public static int getScreenHeight() {
        return ScreenHeight;
    }

    public static void setScreenHeight(int screenHeight) {
        ScreenHeight = screenHeight;
    }

    public static int getScreenWidth() {
        return ScreenWidth;
    }

    public static void setScreenWidth(int screenWidth) {
        ScreenWidth = screenWidth;
    }


    public static PosterApplication getInstance()
    {
        return INSTANCE;
    }

    public static String getProgramPath() {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getPath());
        sb.append(File.separator);
        sb.append("pgm");
        String path = sb.toString();

        if (!FileUtils.isExist(path)) {
            FileUtils.createDir(path);
        }
        return path;
    }

    /*
    * 获取系统参数文件存储的路�?注：或有外部存储设备则优先选用外部存储 (外部存储-->私有空间)
    */
    public static String getStandbyScreenImgPath()
    {
        if (mStandbyScreenImgFullPath == null)
        {
            StringBuilder sb = new StringBuilder();
            sb.append(Environment.getExternalStorageDirectory().getPath());
            sb.append(File.separator);
            sb.append("bgImg");
            sb.append(File.separator);

            // 创建目录
            if (!FileUtils.isExist(sb.toString()))
            {
                FileUtils.createDir(sb.toString());
            }

            mStandbyScreenImgFullPath = sb.append("background.jpg").toString();
        }

        return mStandbyScreenImgFullPath;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE=this;

        // Allocate memory for the image cache space to normal program
        int cacheSize = (int) Runtime.getRuntime().maxMemory() / 10;
        mImgMemoryCache = new LruCache<String, Bitmap>(cacheSize)
        {
            @Override
            protected int sizeOf(String key, Bitmap bitmap)
            {
                int nSize = 0;
                if (bitmap != null)
                {
                    nSize = bitmap.getByteCount();
                }
                return nSize;
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldBitmap, Bitmap newBitmap)
            {
                if (evicted)
                {
                    if ((oldBitmap != null) && (!oldBitmap.isRecycled()))
                    {
                        oldBitmap.recycle();
                        oldBitmap = null;
                    }
                }
            }
        };
    }
    public static void addBitmapToMemoryCache(String key, Bitmap bitmap)
    {
        if (mImgMemoryCache != null)
        {
            mImgMemoryCache.put(key, bitmap);
        }
    }

    public static Bitmap getBitmapFromMemoryCache(String key)
    {
        if (mImgMemoryCache != null)
        {
            Bitmap bmp = mImgMemoryCache.get(key);
            if (bmp != null && !bmp.isRecycled())
            {
                return bmp;
            }
        }
        return null;
    }

    public static void clearMemoryCache()
    {
        if (mImgMemoryCache != null)
        {
            mImgMemoryCache.evictAll();
            System.gc();
        }
    }

    public static void addBitmapToDiskCache(String key, Bitmap bitmap)
    {
        if (mImgDiskCache == null)
        {
            return;
        }

        // if cache didn't have the bitmap, then save it.
        if (mImgDiskCache.get(key) == null)
        {
            mImgDiskCache.put(key, bitmap);
        }
    }

    public static Bitmap getBitmapFromDiskCache(String key)
    {
        if (mImgDiskCache == null)
        {
            return null;
        }

        return mImgDiskCache.get(key);
    }

    public static void clearDiskCache()
    {
        if (mImgDiskCache != null)
        {
            mImgDiskCache.clearCache();
        }
    }

    public static String getGifImagePath(String subDirName)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(FileUtils.getExternalStorage());
        sb.append(File.separator);
        sb.append("Gif");
        if (subDirName != null)
        {
            sb.append(File.separator);
            sb.append(subDirName);
        }
        if (!FileUtils.isExist(sb.toString()))
        {
            FileUtils.createDir(sb.toString());
        }

        return sb.toString();
    }

    public static int resizeImage(Bitmap bitmap, String destPath, int width, int height)
    {
        int swidth = bitmap.getWidth();
        int sheight = bitmap.getHeight();
        float scaleWidht = (float) width / swidth;
        float scaleHeight = (float) height / sheight;
        Matrix matrix = new Matrix();
        matrix.setScale(scaleWidht, scaleHeight);
        Bitmap newbm = Bitmap.createBitmap(bitmap, 0, 0, swidth, sheight, matrix, true);
        File saveFile = new File(destPath);
        FileOutputStream fileOutputStream = null;

        try
        {
            saveFile.createNewFile();
            fileOutputStream = new FileOutputStream(saveFile);
            if (fileOutputStream != null)
            {
                // 把位图的压缩信息写入到一个指定的输出流中
                // 第一个参数format为压缩的格式
                // 第二个参数quality为图像压缩比的值,0-100.0 意味着小尺寸压缩,100意味着高质量压缩
                // 第三个参数stream为输出流
                newbm.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            }
            fileOutputStream.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return 1;
        }
        finally
        {
            if (fileOutputStream != null)
            {
                try
                {
                    fileOutputStream.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        return 0;
    }

}
