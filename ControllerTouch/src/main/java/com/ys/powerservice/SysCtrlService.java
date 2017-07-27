package com.ys.powerservice;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.ys.powerservice.common.Actions;
import com.ys.powerservice.common.CommonActions;
import com.ys.powerservice.poweronoff.PowerOnOffManager1;
import com.ys.powerservice.utils.Logger;


public class SysCtrlService extends Service {
	private final static String TAG = "SysCtrlService";
	private Handler handler;

	private final static int DEFAULT_MONITOR_PERIOD = 100;

	private MonitorThread mMonitorThread = null;

	private Context context;

	private PowerManager mPowerManager = null;
	private PowerManager.WakeLock mWakeLock = null;
	private InternalReceiver mInternalReceiver = null;
	private ActivityManager mActivityManager = null;

	@Override
	public void onCreate() {
		super.onCreate();
		Logger.i("start poweronoff service");
		mPowerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
		mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		initReceiver();
//		startMonitorThread();
		context =this;
		handler = new Handler(Looper.getMainLooper());

		handler.post(new Runnable() {
			@Override
			public void run() {
				Log.i("jialei","定时开关机服务已启动");
				Toast.makeText(getApplicationContext(), "定时开关机服务已启动",Toast.LENGTH_LONG).show();

			}
		});
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mInternalReceiver);
	}

	private void initReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Actions.UPDATE_POWERONOFF_ACTION);
		mInternalReceiver = new InternalReceiver();
		registerReceiver(mInternalReceiver, filter);
	}

	private void startMonitorThread() {
		stopMonitorThread();
		mMonitorThread = new MonitorThread();
		mMonitorThread.start();

	}

	private void stopMonitorThread() {
		if (mMonitorThread != null){
			mMonitorThread.cancel();
			mMonitorThread = null;
		}
	}

	private final class MonitorThread extends Thread {

		private int count = 0;
		private boolean mIsRun = true;

		public void cancel() {
			mIsRun = false;
			interrupt();
		}

		@Override
		public void run() {
			while (mIsRun) {
				try{
					count++;
					PowerOnOffManager1.getInstance().checkAndSetOnOffTime(PowerOnOffManager1.AUTOSCREENOFF_COMMON);
					Thread.sleep(1000*90*1);
					Logger.i("Check OnOffTime" + count);
				}catch (InterruptedException e) {
					return;
				}
			}
		}
	}

//	private class InternalReceiver extends BroadcastReceiver{
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			String action = intent.getAction();
//			if (action.equals(Actions.UPDATE_POWERONOFF_ACTION)){
//				Logger.i("BroadCast Update PowerOnOFF ACTION");
//				startMonitorThread();
//			}
//		}
//	}


	// 定时开关机、触摸事件响应等处理
	@SuppressLint("Wakelock")
	private class InternalReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("InternalReceiver","onReceive");
			String action = intent.getAction();
			if (action.equals(CommonActions.SCREEN_ACTION)) {
				boolean screenOff = intent.getBooleanExtra("screenoff", false);
				Log.i("InternalReceiver","screenOff:"+screenOff);
				if (mPowerManager.isScreenOn() == screenOff) {
					if (screenOff) {
						mWakeLock.acquire();
						mPowerManager.goToSleep(SystemClock.uptimeMillis());
					} else {
						mPowerManager.reboot("Power timer is on time, re-start system......");
					}
				}
			}
		}
	}
}
