package com.example.screenmanagertest.power.onoffserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.screenmanagertest.common.Logger;
import com.example.screenmanagertest.power.common.CommonActions;


public class SysCtrlReceiver extends BroadcastReceiver {	
	@Override
	public void onReceive(Context context, Intent intent) {
		Logger.i("start broadcast poweronoff");

		// TODO Auto-generated method stub
		if (intent.getAction().equals(CommonActions.BOOT_ACTION)) {
			CommonActions.mIsBootCompleted = true;
			Logger.i("start broadcast poweronoff");
//			context.startService(new Intent(CommonActions.SYSCTRL_SERVICE_ACTION));
		}
	}
}
