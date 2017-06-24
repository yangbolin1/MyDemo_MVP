package com.ccdt.app.tv.mvpdemo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ccdt.app.tv.mvpdemo.service.MyService;

public class BootCompletedReceiver extends BroadcastReceiver {

	private static final String TAG="BootCompletedReceiver";
	private static final String ACTION = "android.intent.action.BOOT_COMPLETED";

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		Log.e(TAG, "BroadcastReceiver");
		if (arg1.getAction().equals(ACTION)) {
			Intent i = new Intent(arg0, MyService.class);
			arg0.startService(i);
		}
	}

}
