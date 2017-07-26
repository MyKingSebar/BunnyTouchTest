package com.example.screenmanagertest.power;

import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.SystemClock;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.example.screenmanagertest.PosterApplication;
import com.example.screenmanagertest.R;
import com.example.screenmanagertest.common.DialogUtil;
import com.example.screenmanagertest.common.Logger;
import com.example.screenmanagertest.power.entity.SysOnOffTimeInfo;

import java.util.Timer;
import java.util.TimerTask;

public class PowerOnOffManager {
	private final static String TAG = "SysCtrlService";

	private static PowerOnOffManager INSTANCE = null;


	private PowerManager mPowerManager = null;
	private DevicePolicyManager mDevicePolicyManager = null;
	private PowerManager.WakeLock mWakeLock = null;


	private final long MILLISECOND_DAY = 24*60*60*1000;
	private final long MILLISECOND_HOUR = 60*60*1000;
	private final long MILLISECOND_MINUTE = 60*1000;
	private final long MILLISECOND_SECOND = 1000;
	
	public static final int STATUS_IDLE = 0;
	public static final int STATUS_ONLINE = 1;
	public static final int STATUS_STANDBY = 2;
	
	public static final int AUTOSCREENOFF_IMMEDIATE = 0;
	public static final int AUTOSCREENOFF_COMMON = 1;
	public static final int AUTOSCREENOFF_URGENT = 2;
	
	private final int COMMON_AUTOSCREENOFF_MINUTE = 1;
	private final int COMMON_AUTOSCREENOFF_MILLISECOND = COMMON_AUTOSCREENOFF_MINUTE*60*1000;
	private final int URGENT_AUTOSCREENOFF_MINUTE = 1;
	private final int URGENT_AUTOSCREENOFF_MILLISECOND = URGENT_AUTOSCREENOFF_MINUTE*60*1000;
	
    private final int DEFAULT_ALERTDIALOG_TIMEOUT = 60*1000;
	
	private int mCurrentStatus = STATUS_IDLE;
	private Dialog dlgAutoScreenOff = null;
	
	private boolean mIsScreenoff         = false;
	private Timer mPowerOnOffTimer =  null;
	
	public static PowerOnOffManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new PowerOnOffManager();
		}
		return INSTANCE;
	}
	
	private PowerOnOffManager() {
//		mPowerManager = (PowerManager)PosterApplication.getSystemService(Context.POWER_SERVICE);
//		mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		setCurrentStatus(STATUS_ONLINE);
	}
	
//	private void PowerOnOffAction()
//	{
//		dismissPromptDialog();
//    	setScreenOff(mIsScreenoff);
//    	Time currTime = new Time(Contants.TIME_ZONE_CHINA);
//        currTime.setToNow();
//        Logger.i("PowerOnOffAction() current time is: " + currTime.hour + ":" + currTime.minute + ":" + currTime.second);
//        SysOnOffTimeInfo[] systimeinfo = PosterApplication.getInstance().getSysOnOffTime();
//
//    	if (mIsScreenoff)
//    	{
//    		setCurrentStatus(STATUS_STANDBY);
//    		long ontime = getNextScreenOnTime(currTime, systimeinfo);
//    		if (ontime > 0)
//    		{
//    			mIsScreenoff  = false;
//    			startPowerOnOffTimer(ontime);
//    		}
//    	}
//    	else
//    	{
//    		setCurrentStatus(STATUS_ONLINE);
//    		long offtime = getNextScreenOffTime(currTime, systimeinfo);
//    		if (offtime > 0)
//    		{
//    			mIsScreenoff = true;
//    			startPowerOnOffTimer(offtime);
//    		}
//    	}
//	}

	private void PowerOnOffAction()
	{
		dismissPromptDialog();
		setScreenOff(mIsScreenoff);
		Time currTime = new Time(Contants.TIME_ZONE_CHINA);
		currTime.setToNow();
		Logger.i("PowerOnOffAction() current time is: " + currTime.hour + ":" + currTime.minute + ":" + currTime.second);
		SysOnOffTimeInfo[] systimeinfo = PosterApplication.getInstance().getSysOnOffTime();
		if (mIsScreenoff)
		{
			setCurrentStatus(STATUS_STANDBY);
			long ontime = getNextScreenOnTime(currTime, systimeinfo);
			if (ontime > 0)
			{
				mIsScreenoff  = false;
				startPowerOnOffTimer(ontime);
			}
		}
		else
		{
			setCurrentStatus(STATUS_ONLINE);
			long offtime = getNextScreenOffTime(currTime, systimeinfo);
			if (offtime > 0)
			{
				mIsScreenoff = true;
				startPowerOnOffTimer(offtime);
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
	 
	public void destroy() {
		cancelPowerOnOffTimer();
		dismissPromptDialog();
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
                    	} else { // Found the next on time for this group.
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
		dismissPromptDialog();
		Time currTime = new Time(Contants.TIME_ZONE_CHINA);
        currTime.setToNow();
        Logger.i("checkAndSetOnOffTime() current time is: " + currTime.hour + ":" + currTime.minute + ":" + currTime.second);
        SysOnOffTimeInfo[] systimeinfo = PosterApplication.getInstance().getSysOnOffTime();
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
								Log.i("jialei","dialog当前时间为关机时间，系统将在%1$d分钟后自动关机！");
								showToast(String.format(PosterApplication.getInstance().getResources().getString(R.string.autoscreenoff_prompt_msg),COMMON_AUTOSCREENOFF_MINUTE));
//								showPromptDialog(
//										getCurrentContext(type),
//										String.format(PosterApplication
//										.getInstance().getResources()
//										.getString(R.string.autoscreenoff_prompt_msg),
//										COMMON_AUTOSCREENOFF_MINUTE));
								mIsScreenoff  = true;
								startPowerOnOffTimer(COMMON_AUTOSCREENOFF_MILLISECOND);
								break;
							case AUTOSCREENOFF_URGENT:
								Log.i("jialei","dialog当前时间为关机时间，系统将在%1$d分钟后自动关机！");
//								showPromptDialog(
//										getCurrentContext(type),
//										String.format(PosterApplication
//										.getInstance().getResources()
//										.getString(R.string.autoscreenoff_prompt_msg),
//										URGENT_AUTOSCREENOFF_MINUTE));
								mIsScreenoff  = true;
								startPowerOnOffTimer(URGENT_AUTOSCREENOFF_MILLISECOND);
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
        	if (getCurrentStatus() == STATUS_STANDBY) {
        		mIsScreenoff  = false;
        		PowerOnOffAction();    // Screen on  immediately
        	}
        }
	}
	
	@SuppressWarnings("unused")
    private void showToast(String msg) {
		Toast tst = Toast.makeText(PosterApplication.getInstance(), msg, Toast.LENGTH_LONG);
		tst.setGravity(Gravity.CENTER, 0, 0);
		tst.show();
	}
	
	private Context getCurrentContext(int screenofftype) {
		return PosterApplication.getInstance().getBaseContext();
	}
	
	private void showPromptDialog(Context context, String msg) {
		if (context != null) {

			dlgAutoScreenOff = DialogUtil.showTipsDialog(context, context.getString(R.string.autoscreenoff_prompt_title), msg, context.getString(R.string.autoscreenoff_prompt_positive), new DialogUtil.DialogSingleButtonListener() {
				
				@Override
				public void onSingleClick(Context context , View v , int which) {
					if (dlgAutoScreenOff != null) {
						dlgAutoScreenOff.dismiss();
						dlgAutoScreenOff = null;
					}
				}
			}, false);

			dlgAutoScreenOff.show();

			DialogUtil.dialogTimeOff(dlgAutoScreenOff, DEFAULT_ALERTDIALOG_TIMEOUT);
		}
	}
	
	public void dismissPromptDialog() {
		if ((dlgAutoScreenOff != null) && dlgAutoScreenOff.isShowing()) {
			dlgAutoScreenOff.dismiss();
			dlgAutoScreenOff = null;
    	}
	}
	
	public void wakeUp() {
		dismissPromptDialog();
		if (getCurrentStatus() == STATUS_STANDBY) {
			mIsScreenoff  = false;
    		PowerOnOffAction();    // Screen on  immediately
		}
	}
	
	public void shutDown() {
		dismissPromptDialog();
		if (getCurrentStatus() == STATUS_ONLINE) {
			mIsScreenoff  = true;
			PowerOnOffAction(); // Screen off immediately
		}
	}

//	private void setScreenOff(boolean off) {
//
//	        if (off)
//	        {
//	        	// Close HDMI\VGA\LVDS out
////	        	try {
////					RuntimeExec.getInstance().runRootCmd("echo mem > /sys/power/state");
////				} catch (IOException e) {
////					e.printStackTrace();
////				} catch (InterruptedException e) {
////					e.printStackTrace();
////				}
//				if (mPowerManager.isScreenOn() == off) {
//					if (off) {
//						mWakeLock.acquire();
//						mPowerManager.goToSleep(SystemClock.uptimeMillis());
//						DevicePolicyManager.
//					} else {
//						mPowerManager.reboot("Power timer is on time, re-start system......");
//					}
//				}
//	            Logger.i("PowerOnOff: power off time.");
//	        }
//	        else
//	        {
//	        	// Stop feed watchdog,  system will be re-start , then power on
//	        	Logger.i("PowerOnOff: power on time.");
//	        	PosterApplication.getInstance().rebootSystem();
//	        }
//
//    }

	private void setScreenOff(boolean off) {
		Log.i("jialei","setScreenOff:"+off);
		Intent intent = new Intent(Actions.SCREEN_ACTION);
		intent.putExtra("screenoff", off);
		PosterApplication.getInstance().sendBroadcast(intent);
	}
}
