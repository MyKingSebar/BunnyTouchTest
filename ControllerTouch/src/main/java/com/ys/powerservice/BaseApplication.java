package com.ys.powerservice;

import android.app.Application;
import android.content.Context;
import android.text.format.Time;


import com.ys.powerservice.common.Contants;
import com.ys.powerservice.common.SysParamManager;
import com.ys.powerservice.entity.SysOnOffTimeInfo;
import com.ys.powerservice.entity.SysParam;
import com.ys.powerservice.utils.Logger;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ld019890217 on 2017/4/19.
 */

public class BaseApplication extends Application {

    private static BaseApplication INSTANCE = null;

    private Context context;

    public static BaseApplication getInstance(){
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        context = this;
        initAppParam();
    }

    public  Context getAppContext() {
        return context;
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
        Time currentTime = new Time(Contants.TIME_ZONE_CHINA);
        currentTime.setToNow();

        Time time1 = new Time(Contants.TIME_ZONE_CHINA);
        time1.set(Integer.parseInt(strTime1[2]), Integer.parseInt(strTime1[1]), Integer.parseInt(strTime1[0]), currentTime.monthDay, currentTime.month, currentTime.year);

        Time time2 = new Time(Contants.TIME_ZONE_CHINA);
        time2.set(Integer.parseInt(strTime2[2]), Integer.parseInt(strTime2[1]), Integer.parseInt(strTime2[0]), currentTime.monthDay, currentTime.month, currentTime.year);

        return Time.compare(time1, time2);
    }
}
