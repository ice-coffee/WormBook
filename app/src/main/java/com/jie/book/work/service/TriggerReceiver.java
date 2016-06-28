package com.jie.book.work.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import com.jie.book.work.utils.LogUtil;

/**
 * 接收相关广播 触发服务 开启服务
 */
public class TriggerReceiver extends BroadcastReceiver {
	private static final String TAG = TriggerReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		LogUtil.i(TAG, action);
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			LogUtil.i(TAG, "开机连接开启service....");
		} else if (action.equals(Intent.ACTION_POWER_CONNECTED)) {
			LogUtil.i(TAG, "充电器连接开启service....");
		} else if (action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
			LogUtil.i(TAG, "充电器断开开启service....");
		} else if (action.equals(Intent.ACTION_BATTERY_LOW)) {
			LogUtil.i(TAG, "电量过低开启service....");
		} else if (action.equals(Intent.ACTION_BATTERY_OKAY)) {
			LogUtil.i(TAG, "电量ok开启service....");
		} else if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
			LogUtil.i(TAG, "判断wifi是否打开广播 开启service....");
		} else if (action.equals("android.provider.Telephony.SMS_RECEIVE")) {
			LogUtil.i(TAG, "接收到短信打开广播 开启service....");
		}

		RemindService.startService(context);
	}

}
