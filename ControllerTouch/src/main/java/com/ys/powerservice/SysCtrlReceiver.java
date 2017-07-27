package com.ys.powerservice;

import com.ys.powerservice.common.CommonActions;
import com.ys.powerservice.utils.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SysCtrlReceiver extends BroadcastReceiver {	
	@Override
	public void onReceive(Context context, Intent intent) {
		Logger.i("start broadcast poweronoff");

		// TODO Auto-generated method stub
		if (intent.getAction().equals(CommonActions.BOOT_ACTION)) {
			CommonActions.mIsBootCompleted = true;
			Logger.i("start broadcast poweronoff");
			context.startService(new Intent(CommonActions.SYSCTRL_SERVICE_ACTION));
		}
	}
}
