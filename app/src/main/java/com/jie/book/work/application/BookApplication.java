package com.jie.book.work.application;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.jie.book.work.R;
import com.jie.book.work.activity.BookMainActivity;
import com.jie.book.work.read.ReadSettings;
import com.jie.book.work.utils.Config;
import com.jie.book.work.utils.ImageLoadUtil;
import com.jie.book.work.utils.MiscUtils;
import com.jie.book.work.utils.StringUtil;
import com.jie.book.work.utils.UserSettings;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

public class BookApplication extends Application {
	public final static String BROAD_CAST_ACTION_TIMER = "broad_cast_action_timer";
	private static final int USER_NOTIFICATION = 1526355476;
	public static final int UMENG_TIME = 1 * 60 * 1000;
	public static boolean check_new = false;// 是否有新版
	private static BookApplication mInstance;
	private LocalBroadcastManager manager;
	public Map<String, Integer> downLoadNotifiMap;
	public int downLoadNotifiId = 0;
	private long lastTime = 0;
	public long lauchTime = 0;
	public float lunchBrightness = 0;
	public String secretPackageName;
	public Timer stopTimer;
	public int reaminTime = 180;
	public boolean timerRun = false;
	public boolean swithNightModel = false;

	public static BookApplication getInstance() {
		return mInstance;
	}

	public void onCreate() {
		mInstance = this;
		reaminTime = Cookies.getUserSetting().getSecretCompeletTime();
		manager = LocalBroadcastManager.getInstance(this);
		lauchTime = System.currentTimeMillis();
		downLoadNotifiMap = new HashMap<String, Integer>();
		MobclickAgent.setDebugMode(false);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.setUpdateAutoPopup(false);
		MobclickAgent.updateOnlineConfig(this);
		ImageLoadUtil.initImageLoader(mInstance);
		initUmengParams();
		getUserNotification();
	}

	// 获取友盟在线参数
	public void initUmengParams() {
		StringUtil.getIntUmengParams(Config.BANNER_AD, 5, Config.BANNER_AD_DEFAULT);
		StringUtil.getIntUmengParams(Config.OPEN_SHELF_REC, 1, Config.OPEN_SHELF_REC_DEFAULT);
		StringUtil.getIntUmengParams(Config.OPEN_APP_LIST, 1, Config.OPEN_APP_LIST_DEFAULT);
		StringUtil.getStringUmengParams(Config.SEARCH_BOOK_CHANNEL, Config.SEARCH_BOOK_CHANNEL_DEFAULT);
		StringUtil.getStringUmengParams(Config.TYPE_BOOK_CHANNEL, Config.TYPE_BOOK_CHANNEL_DEFAULT);
		StringUtil.getIntUmengParams(Config.OPEN_AD_TIME, 100, Config.OPEN_AD_TIME_DEFAULT);
		StringUtil.getStringUmengParams(Config.SEARCH_BOOK, Config.SEARCH_BOOK_DEFAULT);
		StringUtil.getStringUmengParams(Config.ZH_USER_AGENT, Config.ZH_USER_AGENT_DEFAULT);
		StringUtil.getStringUmengParams(Config.BAIDU_ID, Config.BAIDU_ID_DEFAULT);
		StringUtil.getIntUmengParams(Config.UPDATE_VERSION, 1000, Config.UPDATE_VERSION_DEFAULT);
		StringUtil.getStringUmengParams(Config.SOGOU_VERSION, Config.SOGOU_VERSION_DEFAULT);
		StringUtil.getIntUmengParams(Config.SPLASH_SPACE, 100, Config.SPLASH_SPACE_DEFAULT);
	}

	// 获取广告数据
	public void resumeUmengParams() {
		if (System.currentTimeMillis() - lastTime > UMENG_TIME) {
			lastTime = System.currentTimeMillis();
			MobclickAgent.updateOnlineConfig(this);
			StringUtil.getIntUmengParams(Config.BANNER_AD, 5, 2);
		}
	}

	// 获取用户通知
	@SuppressWarnings("deprecation")
	private void getUserNotification() {
		MobclickAgent.updateOnlineConfig(this);
		String umengParams = MobclickAgent.getConfigParams(this, Config.USER_NOTIFICATION);
		if (!StringUtil.isEmpty(umengParams) && !umengParams.equals(Config.USER_NOTIFICATION_DEFAULT)
				&& !Cookies.getUserSetting().hasNotifiString(umengParams)) {
			Cookies.getUserSetting().setNotifiString(umengParams);
			String title = "棒棒书城提醒您";
			Notification notification = new Notification(R.drawable.logo, title, System.currentTimeMillis());
			NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			if (Cookies.getUserSetting().isNotifiVoiceOpen())// b) 发出提示音，如：
				notification.defaults |= Notification.DEFAULT_SOUND;
			if (Cookies.getUserSetting().isNotifiShakeOpen()) {// c) 手机振动，如：
				notification.defaults |= Notification.DEFAULT_VIBRATE;
				long[] vibrate = { 0, 100, 200, 300 };
				notification.vibrate = vibrate;
			}
			Intent it = new Intent(this, BookMainActivity.class);
			it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// 这行代码会解决此问题
			PendingIntent pi = PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_UPDATE_CURRENT);
			notification.setLatestEventInfo(this, title, umengParams, pi);
			nm.notify(USER_NOTIFICATION, notification);
		}
	}

	public void starTimer() {
		stopTimer();
		stopTimer = new Timer();
		stopTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (!StringUtil.isEmpty(secretPackageName) && MiscUtils.isRunning(mInstance, secretPackageName)) {
					reaminTime--;
					Cookies.getUserSetting().setSecretCompeletTime(reaminTime);
					Intent intent = new Intent(BROAD_CAST_ACTION_TIMER);
					manager.sendBroadcast(intent);
					if (reaminTime <= 0)
						stopTimer();
				}
			}
		}, 1000, 1000);
		timerRun = true;
	}

	public void stopTimer() {
		if (stopTimer != null) {
			stopTimer.cancel();
			stopTimer = null;
			timerRun = false;
		}
	}

	public static class Cookies {
		private static ReadSettings readSetting;
		private static UserSettings useSettings;

		public static ReadSettings getReadSetting() {
			if (readSetting == null)
				readSetting = new ReadSettings(BookApplication.getInstance());
			return readSetting;
		}

		public static UserSettings getUserSetting() {
			if (useSettings == null)
				useSettings = new UserSettings(BookApplication.getInstance());
			return useSettings;
		}

	}
}