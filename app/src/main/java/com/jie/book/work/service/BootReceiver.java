package com.jie.book.work.service;

import com.jie.book.work.application.BookApplication;
import com.jie.book.work.utils.StringUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 接收相关广播 触发服务 开启服务
 */
public class BootReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		// 接收安装广播
		if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
			String packageName = intent.getDataString().split(":")[1];
			if (!StringUtil.isEmpty(BookApplication.getInstance().secretPackageName)
					&& BookApplication.getInstance().secretPackageName.equals(packageName)) {
				BookApplication.getInstance().starTimer();
			}
		}
		// 接收卸载广播
		if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
			String packageName = intent.getDataString();
			if (!StringUtil.isEmpty(BookApplication.getInstance().secretPackageName)
					&& BookApplication.getInstance().secretPackageName.equals(packageName)) {
				BookApplication.getInstance().stopTimer();
			}
		}
	}

}
