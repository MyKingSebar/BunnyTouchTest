package com.ys.powerservice.poweronoff;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.format.Time;
import android.view.Gravity;
import android.widget.Toast;


import com.ys.powerservice.BaseApplication;
import com.ys.powerservice.R;
import com.ys.powerservice.common.Actions;
import com.ys.powerservice.common.CommonActions;
import com.ys.powerservice.common.Contants;
import com.ys.powerservice.entity.SysOnOffTimeInfo;
import com.ys.powerservice.utils.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ld019890217 on 2017/4/24.
 */

public class PowerOnOffManager1 {
    private static PowerOnOffManager1 INSTANCE = null;
    private Handler handler;

    private final long MILLISECOND_DAY = 24*60*60*1000;
    private final long MILLISECOND_HOUR = 60*60*1000;
    private final long MILLISECOND_MINUTE = 60*1000;
    private final long MILLISECOND_SECOND = 1000;

    public static final int STATUS_IDLE = 0;
    public static final int STATUS_ONLINE = 1;
    public static final int STATUS_STANDBY = 2;

    public static final int AUTOSCREENOFF_IMMEDIATE = 0;
    public static final int AUTOSCREENOFF_COMMON = 1;

    private final int COMMON_AUTOSCREENOFF_MINUTE = 1;
    private final int COMMON_AUTOSCREENOFF_MILLISECOND = COMMON_AUTOSCREENOFF_MINUTE*60*1000;

    private int mCurrentStatus = STATUS_IDLE;
    private Dialog dlgAutoScreenOff = null;

    private boolean mIsScreenoff         = false;
    private Timer mPowerOnOffTimer =  null;

    public static PowerOnOffManager1 getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PowerOnOffManager1();
        }
        return INSTANCE;
    }

    private PowerOnOffManager1() {
        setCurrentStatus(STATUS_ONLINE);
        handler = new Handler(BaseApplication.getInstance().getBaseContext().getMainLooper()) ;

    }

    private void PowerOnOffAction()
    {
        setScreenOff(mIsScreenoff);
        Time currTime = new Time(Contants.TIME_ZONE_CHINA);
        currTime.setToNow();
        Logger.i("PowerOnOffAction() current time is: " + currTime.hour + ":" + currTime.minute + ":" + currTime.second);
        SysOnOffTimeInfo[] systimeinfo = BaseApplication.getInstance().getSysOnOffTime();
        if (mIsScreenoff)
        {
            setCurrentStatus(STATUS_STANDBY);
            long ontime = getNextScreenOnTime(currTime, systimeinfo);

            if (ontime > 0)
            {
                cancelPowerOnAction();
                mIsScreenoff  = false;
                Date date1 = new Date(System.currentTimeMillis());
                Logger.i("Current long time " + date1.toString());
                long PowerOnTimelongFormat = System.currentTimeMillis() + ontime;
                Date date2 = new Date(PowerOnTimelongFormat);
                Logger.i("PowerOnTime longFormat "+ date2.toString());
                String bootTime = stampToDateString(PowerOnTimelongFormat);
                setPowerRestartAction(bootTime);
                //startPowerOnOffTimer(ontime);
            }
        }
        else
        {
            setCurrentStatus(STATUS_ONLINE);
            long offtime = getNextScreenOffTime(currTime, systimeinfo);
            if (offtime > 0)
            {
                mIsScreenoff = true;
                if (offtime > 1000*60*3) {
                    Logger.i("提前3分钟");
                    startPowerOnOffTimer(offtime - 1000 * 60 * 3);
                }else {
                    Logger.i("立刻执行");
                    startPowerOnOffTimer(100);
                }
            }
        }
    }

    private void cancelPowerOnOffTimer()
    {
        if (mPowerOnOffTimer != null)
        {
            mPowerOnOffTimer.cancel();
            mPowerOnOffTimer = null;
        }
    }

    private void startPowerOnOffTimer(long delayMillis)
    {
        cancelPowerOnOffTimer();
        mPowerOnOffTimer = new Timer("PowerOnOffTimer");
        mPowerOnOffTimer.schedule(new TimerTask() {
            @Override
            public void run()
            {
                PowerOnOffAction();
            }
        }, delayMillis);
        Logger.i("startPowerOnOffTimer(): mIsScreenoff is : " + mIsScreenoff + " delayMillis is: " + delayMillis);
    }


    public int getCurrentStatus() {
        synchronized (this) {
            return mCurrentStatus;
        }
    }

    public void setCurrentStatus(int status) {
        synchronized (this) {
            mCurrentStatus = status;
        }
    }

    private int getNextWeekDay(final int currWeekDay) {
        return (currWeekDay < 6) ? currWeekDay+1 : 0;
    }

    private long getMillisFromTime(int day, int hour, int minute, int second) {
        return MILLISECOND_DAY*day+MILLISECOND_HOUR*hour
                +MILLISECOND_MINUTE*minute+MILLISECOND_SECOND*second;
    }

    private long getNextScreenOffTime(Time currtime, SysOnOffTimeInfo[] systimeinfo) {
        long nextOffTime = -1;

        if (systimeinfo != null) {
            long currTimeMillis = getMillisFromTime(0, currtime.hour, currtime.minute, currtime.second);
            int i, j, k;
            long tmpTimeMillis = -1;
            long latestNextOffTime = -1;
            for (i = 0; i < systimeinfo.length; i++) {
                tmpTimeMillis = -1;
                latestNextOffTime = -1;
                if (systimeinfo[i].offhour == 0xFF)
                {
                    Logger.d("getNextScreenOffTime(): the sys time is invaild, i = " + i);
                    continue;
                }

                for (j = currtime.weekDay, k = 0; k < 7; j = getNextWeekDay(j), k++) {
                    if ((systimeinfo[i].week&(1<<j)) != 0) {
                        tmpTimeMillis = getMillisFromTime(k, systimeinfo[i].offhour,
                                systimeinfo[i].offminute, systimeinfo[i].offsecond);
                        if ((j == currtime.weekDay) && (currTimeMillis > tmpTimeMillis)) {
                            latestNextOffTime = (MILLISECOND_DAY*7)-(currTimeMillis-tmpTimeMillis);
                        } else { // Found the next off time for this group.
                            latestNextOffTime = tmpTimeMillis-currTimeMillis;
                            break;
                        }
                    }
                }

                if ((latestNextOffTime != -1) &&
                        ((nextOffTime == -1) || (nextOffTime > latestNextOffTime))) {
                    nextOffTime = latestNextOffTime;
                }
            }
        }

        return nextOffTime;
    }

    private long getNextScreenOnTime(Time currtime, SysOnOffTimeInfo[] systimeinfo) {
        long nextOnTime = -1;
        if (systimeinfo != null) {
            long currTimeMillis = getMillisFromTime(0, currtime.hour, currtime.minute, currtime.second);
            int i, j, k;
            long tmpTimeMillis = -1;
            long latestNextOnTime = -1;
            for (i = 0; i < systimeinfo.length; i++) {
                tmpTimeMillis = -1;
                latestNextOnTime = -1;
                if  (systimeinfo[i].onhour == 0xFF)
                {
                    Logger.d("getNextScreenOnTime(): the sys time is invaild, i = " + i);
                    continue;
                }

                for (j = currtime.weekDay, k = 0; k < 7; j = getNextWeekDay(j), k++) {
                    if ((systimeinfo[i].week&(1<<j)) != 0) {
                        tmpTimeMillis = getMillisFromTime(k, systimeinfo[i].onhour,
                                systimeinfo[i].onminute, systimeinfo[i].onsecond);
                        if ((j == currtime.weekDay) && (currTimeMillis > tmpTimeMillis)) {
                            latestNextOnTime = (MILLISECOND_DAY*7)-(currTimeMillis-tmpTimeMillis);
                        } else {
                            // Found the next on time for this group.
                            latestNextOnTime = tmpTimeMillis-currTimeMillis;
                            break;
                        }
                    }
                }

                if ((latestNextOnTime != -1) &&
                        ((nextOnTime == -1) || (nextOnTime > latestNextOnTime))) {
                    nextOnTime = latestNextOnTime;
                }

            }
        }

        return nextOnTime;
    }

    public void checkAndSetOnOffTime(int type) {
        cancelPowerOnOffTimer();
        Time currTime = new Time(Contants.TIME_ZONE_CHINA);
        currTime.setToNow();
        Logger.i("checkAndSetOnOffTime() current time is: " + currTime.hour + ":" + currTime.minute + ":" + currTime.second);
        SysOnOffTimeInfo[] systimeinfo = BaseApplication.getInstance().getSysOnOffTime();
        if ((systimeinfo != null) && (systimeinfo.length != 0)) {
            long nextOnTime = getNextScreenOnTime(currTime, systimeinfo);
            if (nextOnTime > 0) {
                long nextOffTime = getNextScreenOffTime(currTime, systimeinfo);
                if (nextOffTime > 0) {
                    if (nextOnTime > nextOffTime) {
                        if (getCurrentStatus() == STATUS_STANDBY) {
                            mIsScreenoff  = false;
                            PowerOnOffAction(); // Screen on immediately
                        } else {
                            mIsScreenoff  = true;
                            startPowerOnOffTimer(nextOffTime);
                        }
                    } else if (nextOffTime > nextOnTime) {
                        if (getCurrentStatus() == STATUS_ONLINE) {
                            switch (type) {
                                case AUTOSCREENOFF_IMMEDIATE:
                                    mIsScreenoff  = true;
                                    PowerOnOffAction(); // Screen off immediately
                                    break;
                                case AUTOSCREENOFF_COMMON:
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(BaseApplication.getInstance().getBaseContext(),
                                                    BaseApplication.getInstance().getBaseContext().getResources().getString(R.string.autoscreenoff_prompt_msg),
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    mIsScreenoff  = true;
                                    startPowerOnOffTimer(COMMON_AUTOSCREENOFF_MILLISECOND);
                                    break;
                                default:
                                    break;
                            }
                        } else {
                            mIsScreenoff  = false;
                            startPowerOnOffTimer(nextOnTime);
                        }
                    }
                }
            }
        } else {
            Logger.i("System PowerOnoffTime is Null");
            cancelPowerOffAction();
            cancelPowerOnAction();
            if (getCurrentStatus() == STATUS_STANDBY) {
                mIsScreenoff  = false;
                PowerOnOffAction();    // Screen on  immediately
            }
        }
    }

    @SuppressWarnings("unused")
    private void showToast(String msg) {
        Toast tst = Toast.makeText(BaseApplication.getInstance(), msg, Toast.LENGTH_LONG);
        tst.setGravity(Gravity.CENTER, 0, 0);
        tst.show();
    }

//    private void setScreenOff(boolean off) {
//        if (off){
//            final Context context = BaseApplication.getInstance().getBaseContext();
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(BaseApplication.getInstance().getBaseContext(),"准备关机大约需要三分钟",Toast.LENGTH_LONG).show();
//                    //关机动作延迟三分钟保证定时开关机动作完成
//                    String shutdownTime = stampToDateString(System.currentTimeMillis()+1000*60*3);
//                    Logger.i("PowerOnOff: power off time" +shutdownTime);
//                    setmPowerOffAction(shutdownTime);
//                }
//            });
//
//        }else {
//            Logger.i("PowerOnOff: power on time.");
//            Logger.i("reboot system.......");
//        }
//    }
private void setScreenOff(boolean off) {
    Intent intent = new Intent(CommonActions.SCREEN_ACTION);
    intent.putExtra("screenoff", off);
    BaseApplication.getInstance().sendBroadcast(intent);
}

    /**
     * 取消定时关机
     */

    private void cancelPowerOffAction(){
        Logger.i("CancelPowerOff");
        Intent cancelShutIntent = new Intent("com.example.jt.shutdowntime");
        cancelShutIntent.putExtra("message ", "cancel");
        BaseApplication.getInstance().getBaseContext().sendBroadcast(cancelShutIntent);
        Logger.i("Success CancelPowerOn");
    }

    /**
     * 取消定时开机
     */

    private void cancelPowerOnAction(){
        Logger.i("CancelPowerOn");
        Intent cancelBootIntent = new Intent("com.example.jt.boottime");
        cancelBootIntent.putExtra("message","0,0,0,0,0,0,0,0");
        BaseApplication.getInstance().getBaseContext().sendBroadcast(cancelBootIntent);
        Logger.i("Success CancelPowerOn");
    }

    /**
     * 设置定时关机
     * 发送广播后至少需要三分使其生效
     * @param shutDownTime
     */

    private void setmPowerOffAction(String shutDownTime){
        Logger.i("Set Parameter for PowerShutDown"+ shutDownTime);
        Intent shutDownIntent = new Intent("com.example.jt.shutdowntime");
        shutDownIntent.putExtra("message", shutDownTime);
        BaseApplication.getInstance().getBaseContext().sendBroadcast(shutDownIntent);
        Logger.i("SetPowerShutDown Success");
    }

    /**
     * 设置定时开机
     * 发送广播后至少需要三分钟使其生效
     * @param bootTime
     */

    private void setPowerRestartAction(String bootTime){
        Logger.i("Set Parameter for PowerRestartOn"+ bootTime);
        Intent bootIntent = new Intent("com.example.jt.boottime");
        bootIntent.putExtra("message", bootTime);
        BaseApplication.getInstance().getBaseContext().sendBroadcast(bootIntent);
        Logger.i("SetPowerOn Success");

    }

    /**
     *
     * @param s
     * 时间戳 转 SimpleDateFormat
     * return Format is "1,0,0,HH,mm,yy,MM,dd"
     * 当时间数值不足两位时去除其十位的0 Example（1,0,0,09,08,17,04,08）-> (1,0,0,9,8,17,4,8)
     * @return
     */

    private String stampToDateString(long s){
        SimpleDateFormat format = new SimpleDateFormat("HH,mm,yy,MM,dd");
        String OriTime = format.format(s);
        String uploadTime = "";
        if (OriTime!= null){
            String[] spliteInfo = OriTime.split(",");
            int[] convertTime = new int[spliteInfo.length];
            String[] convertInfo = new String[spliteInfo.length];
            String convertStartTime = "";
            for(int i = 0; i < spliteInfo.length; i++){
                convertTime[i] = Integer.parseInt(spliteInfo[i]);
                if(convertTime[i] <10){
                    convertInfo[i] =convertTime[i]+"";
                    convertStartTime =  convertStartTime+","+convertInfo[i];
                }else{
                    convertInfo[i] =convertTime[i]+"";
                    convertStartTime = convertStartTime+","+spliteInfo[i];
                }
            }
            if (uploadTime!= null){
                uploadTime ="1,0,0"+convertStartTime;
            }
            Logger.i("UploadTime"+uploadTime);
        }

        return uploadTime;
    }
}
