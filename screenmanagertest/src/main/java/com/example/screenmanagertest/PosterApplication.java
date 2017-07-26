package com.example.screenmanagertest;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.text.format.Time;
import android.util.DisplayMetrics;

import com.example.screenmanagertest.common.DiskLruCache;
import com.example.screenmanagertest.common.FileUtils;
import com.example.screenmanagertest.common.Logger;
import com.example.screenmanagertest.power.Actions;
import com.example.screenmanagertest.power.RuntimeExec;
import com.example.screenmanagertest.power.SysParamManager;
import com.example.screenmanagertest.power.entity.SysOnOffTimeInfo;
import com.example.screenmanagertest.power.entity.SysParam;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

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
        PosterApplication.getInstance().initAppParam();
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

    public void initAppParam(){
        SysParamManager.getInstance().initSysParam();
    }


    public synchronized SysParam factoryRest()
    {
        SysParam sysParam = new SysParam();

        sysParam.netConn = new ConcurrentHashMap<String, String>();
        sysParam.netConn.put("mode", "DHCP");
        sysParam.netConn.put("ip", "0.0.0.0");

        sysParam.serverSet = new ConcurrentHashMap<String, String>();
/*
        sysParam.serverSet.put("weburl", getConfiguration().getDefualtServerUrl() + WsClient.SERVICE_URL_SUFFIX);
*/
        sysParam.serverSet.put("ftpip", "123.56.146.48");
        sysParam.serverSet.put("ftpport", "21");
        sysParam.serverSet.put("ftpname", "dn4");
        sysParam.serverSet.put("ftppasswd", "dn4");
        sysParam.serverSet.put("ntpip", "123.56.146.48");
        sysParam.serverSet.put("ntpport", "123");

        sysParam.sigOutSet = new ConcurrentHashMap<String, String>();
        sysParam.sigOutSet.put("mode", "3");
        sysParam.sigOutSet.put("value", "10");
        sysParam.sigOutSet.put("repratio", "100");

        sysParam.wifiSet = new ConcurrentHashMap<String, String>();
        sysParam.wifiSet.put("ssid", "");
        sysParam.wifiSet.put("wpapsk", "");
        sysParam.wifiSet.put("authmode", "");
        sysParam.wifiSet.put("encryptype", "");

        sysParam.onOffTime = new ConcurrentHashMap<String, String>();
        sysParam.onOffTime.put("group", "0");

        sysParam.setBit = 0;
        sysParam.getTaskPeriodtime = 30;
        sysParam.delFilePeriodtime = 30;
        sysParam.timeZonevalue = "-8";
        sysParam.passwdvalue = "";
        sysParam.syspasswdvalue = "123456";
        sysParam.brightnessvalue = 60;
        sysParam.volumevalue = 60;
        sysParam.hwVervalue = "1.0.0.0";
/*        sysParam.swVervalue = getVerName();
        sysParam.kernelvervalue = getKernelVersion();*/
        sysParam.cfevervalue = android.os.Build.VERSION.RELEASE;
        sysParam.certNumvalue = "";
        sysParam.termmodelvalue = "JWA-YS200";
        sysParam.termname = "悦视显示终端";
        sysParam.termGrpvalue = "无";
        sysParam.dispScalevalue = 2; /* 16:9 */

        return sysParam;
    }

    /**
     * Compare two Time objects and return a negative number if t1 is less than t2, a positive number if t1 is greater
     * than t2, or 0 if they are equal.
     *
     * @param t1
     *            first {@code Time} instance to compare (Format is "hh:mm:ss")
     * @param t2
     *            second {@code Time} instance to compare (Format is "hh:mm:ss")
     * @return 0:= >0�? <0:<
     */
    public static int compareTwoTime(String t1, String t2)
    {
        String strTime1[] = t1.split(":");
        String strTime2[] = t2.split(":");
        if (strTime1.length < 3 || strTime2.length < 3)
        {
            Logger.i("The given time format is invaild.");
            return -1;
        }
        Time currentTime = new Time("Asia/Shanghai");
        currentTime.setToNow();

        Time time1 = new Time("Asia/Shanghai");
        time1.set(Integer.parseInt(strTime1[2]), Integer.parseInt(strTime1[1]), Integer.parseInt(strTime1[0]), currentTime.monthDay, currentTime.month, currentTime.year);

        Time time2 = new Time("Asia/Shanghai");
        time2.set(Integer.parseInt(strTime2[2]), Integer.parseInt(strTime2[1]), Integer.parseInt(strTime2[0]), currentTime.monthDay, currentTime.month, currentTime.year);

        return Time.compare(time1, time2);
    }

    public SysOnOffTimeInfo[] getSysOnOffTime(){
        SysOnOffTimeInfo[] info = null;
        ConcurrentHashMap<String, String>  onOffTime = SysParamManager.getInstance().getOnOffTimeParam();
        if (onOffTime != null)
        {
            int group = Integer.parseInt(onOffTime.get("group"));
            if (group != 0)
            {
                info = new SysOnOffTimeInfo[group];
                String[] timearray = null;
                for (int i = 0; i < group; i++)
                {
                    info[i] = new SysOnOffTimeInfo();
                    info[i].week = Integer.parseInt(onOffTime.get("week" + (i + 1)));
                    timearray = onOffTime.get("on_time" + (i + 1)).split(":");
                    info[i].onhour = Integer.parseInt(timearray[0]);
                    info[i].onminute = Integer.parseInt(timearray[1]);
                    info[i].onsecond = Integer.parseInt(timearray[2]);
                    timearray = onOffTime.get("off_time" + (i + 1)).split(":");
                    info[i].offhour = Integer.parseInt(timearray[0]);
                    info[i].offminute = Integer.parseInt(timearray[1]);
                    info[i].offsecond = Integer.parseInt(timearray[2]);
                }
            }
        }

        return info;
    }

    public void rebootSystem()
    {
            Intent intent = new Intent(Actions.WATCHDOG_DISABLE_01_ACTION);
            PosterApplication.getInstance().sendBroadcast(intent);

        try
        {
            Logger.i("reboot system.......");
            RuntimeExec.getInstance().runRootCmd("reboot");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
