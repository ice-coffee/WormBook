package com.jie.book.work.utils;

import java.io.File;
import java.util.List;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bond.bookcatch.BookCatcher;
import com.jie.book.work.R;
import com.jie.book.work.activity.BaseActivity;
import com.jie.book.work.activity.LuanchActivity;
import com.jie.book.work.application.BookApplication;
import com.jie.book.work.application.BookApplication.Cookies;

//公用的函数类
public class MiscUtils {

	// 打电话
	public static void doPhone(Context context, String phone) {
		// Intent.ACTION_CALL 直接拨打
		Intent myIntentDial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
		context.startActivity(myIntentDial);
	}

	// 发短信
	public static boolean sendSMS(Context context, String content) {
		Uri smsToUri = Uri.parse("smsto:");// 联系人地址
		Intent mIntent = new Intent(Intent.ACTION_SENDTO, smsToUri);
		mIntent.putExtra("sms_body", content);// 短信的内容
		context.startActivity(mIntent);
		return true;
	}

	// 修改媒体音量（increment > 0为增加，increment < 0为减少，每次只会增加或减少1）
	public static void changeMediaVolume(Context context, int increment) {
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		int curVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);

		if (increment > 0) {
			am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,
					AudioManager.FX_FOCUS_NAVIGATION_UP);
		} else if (increment < 0) {
			am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER,
					AudioManager.FX_FOCUS_NAVIGATION_UP);
		}
		LogUtil.d("System.out", "curVol = " + curVol);
	}

	// 获取SD卡剩余空间
	public static long getAvailaleSize() {
		try {
			File path = Environment.getExternalStorageDirectory(); // 取得sdcard文件路径
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			return (availableBlocks * blockSize) / 1024 / 1024; // 获取可用大小
		} catch (Exception e) {
			return 100;
		}
	}

	// 读取meta-data数据
	public static Object getMetaData(String metaName) {
		ApplicationInfo appInfo;
		try {
			appInfo = BookApplication.getInstance().getPackageManager()
					.getApplicationInfo(BookApplication.getInstance().getPackageName(), PackageManager.GET_META_DATA);
			Object metaValue = appInfo.metaData.get(metaName);
			return metaValue;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

	// 判断手机是否联网
	public static boolean isNetwork(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null) {
			return false;
		}
		if (connectivityManager.getActiveNetworkInfo() == null) {
			return false;
		}
		return connectivityManager.getActiveNetworkInfo().isAvailable();
	}

	// 判断是否有wifi
	public static boolean isWifiConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	private final static ThreadLocal<StringBuilder> threadSafeStrBuilder = new ThreadLocal<StringBuilder>();

	public static StringBuilder getThreadSafeStringBuilder() {
		StringBuilder sb = threadSafeStrBuilder.get();
		if (sb == null) {
			sb = new StringBuilder();
			threadSafeStrBuilder.set(sb);
		}
		sb.setLength(0);
		return sb;
	}

	public static String getVersionName() {
		try {
			PackageManager packageManager = BookApplication.getInstance().getPackageManager();
			PackageInfo packInfo = packageManager.getPackageInfo(BookApplication.getInstance().getPackageName(), 0);
			String version = packInfo.versionName;
			return version;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static int getVersionCode() {
		try {
			PackageManager packageManager = BookApplication.getInstance().getPackageManager();
			PackageInfo packInfo;
			packInfo = packageManager.getPackageInfo(BookApplication.getInstance().getPackageName(), 0);
			int versionCode = packInfo.versionCode;
			return versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

	// 创建快捷方式
	public static void addSelfShortcut(final BaseActivity context) {
		boolean never_check_shortCut = SharedPreferenceUtil.getInstance(context).getBoolean("never_check_shortCut",
				false);
		// 如果没有生成快捷方式，则弹出对话框提醒用户生成快捷方式
		if (!never_check_shortCut) {
			Intent localIntent1 = new Intent("android.intent.action.MAIN");
			localIntent1.setClass(context, LuanchActivity.class);
			localIntent1.addCategory("android.intent.category.LAUNCHER");
			localIntent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			localIntent1.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			Intent localIntent6 = new Intent();
			localIntent6.putExtra("android.intent.extra.shortcut.INTENT", localIntent1);
			String str2 = context.getResources().getString(R.string.app_name);
			localIntent6.putExtra("android.intent.extra.shortcut.NAME", str2);
			Intent.ShortcutIconResource localShortcutIconResource = Intent.ShortcutIconResource.fromContext(context,
					R.drawable.logo);// your icon file
			localIntent6.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", localShortcutIconResource);
			localIntent6.putExtra("duplicate", false);
			localIntent6.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
			context.sendBroadcast(localIntent6);
			SharedPreferenceUtil.getInstance(context).putBoolean("never_check_shortCut", true);
		}
	}

	// 设置屏幕是否全屏
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public static void setSceenFull(boolean enable, BaseActivity activity) {
		Window window = activity.getWindow();
		final View rootView = window.getDecorView();
		WindowManager.LayoutParams lp = window.getAttributes();
		if (enable) {
			lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
				rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
			window.setAttributes(lp);
		} else {
			lp.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
				rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
			window.setAttributes(lp);
		}
	}

	public static void goneSoft(final BaseActivity activity) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			new Handler() {

				@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
				}

			}.sendEmptyMessageDelayed(0, 1000);
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void goneSoftKey(final BaseActivity activity) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		}
	}

	// 判断某个APP是否存在
	public static boolean checkApkExist(Context context, String packageName) {
		context.getPackageName();
		if (packageName == null || "".equals(packageName)) {
			return false;
		}
		try {
			context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	// 判断应用是否正在运行
	public static boolean isRunning(Context context, String packageName) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
		for (RunningAppProcessInfo appProcess : list) {
			String processName = appProcess.processName;
			if (processName != null && processName.equals(packageName)) {
				return true;
			}
		}
		return false;
	}

	// 获取网络时间
	public static long getOnlineTime(Context context) {
		long time = 0;
		if (MiscUtils.isNetwork(context)) {
			time = BookCatcher.getNetworkTime();
			if (time <= 0) {
				time = System.currentTimeMillis();
			}
		}
		return time;
	}

	// 是否安装了杀广告软件
	public static boolean recShelfApp(Context context) {
		if (Cookies.getUserSetting().getOpenTime() > Cookies.getUserSetting().getMaxOpenAdTime()) {
			if (SharedPreferenceUtil.getInstance(context).getInt(Config.OPEN_SHELF_REC, Config.OPEN_SHELF_REC_DEFAULT) == 1) {
				return true;
			} else {
				if (isRoot()) {
					if (checkApkExist(context, "com.qihoo360.mobilesafe")
							|| checkApkExist(context, "com.tencent.qqpimsecure")
							|| checkApkExist(context, "com.ijinshan.mguard")
							|| checkApkExist(context, "com.lbe.security")) {
						return true;
					}
				}
			}
		}
		return false;
	}

	// 是否安装了adsafe
	public static boolean istallAdsafe(Context context) {
		return checkApkExist(context, "com.adsafe");
	}

	// 判断是否有root权限
	public static boolean isRoot() {
		try {
			if (new File("/system/bin/su").exists() || new File("/system/xbin/su").exists())
				return true;
		} catch (Exception e) {
			return false;
		}
		return false;
	}
}