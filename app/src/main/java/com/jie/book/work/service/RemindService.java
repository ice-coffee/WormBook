package com.jie.book.work.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.bond.bookcatch.BookCatcher;
import com.bond.bookcatch.vo.BookDesc;
import com.bond.common.exception.BadNetworkException;
import com.jie.book.work.R;
import com.jie.book.work.activity.BookMainActivity;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.utils.Config;
import com.jie.book.work.utils.LogUtil;
import com.jie.book.work.utils.MiscUtils;
import com.jie.book.work.utils.PhoneUtil;
import com.jie.book.work.utils.StringUtil;
import com.jie.book.work.utils.TaskExecutor;
import com.jie.book.work.utils.TimeUtil;

public class RemindService extends Service {
	private static final String TAG = RemindService.class.getSimpleName();
	private static final long TOP_RUN_TIME_PRODUCE = 15 * 60 * 1000;
	private static final long BACK_RUN_TIME_PRODUCE = 30 * 60 * 1000;
	private static final long TOP_RUN_TIME_DEBUG = 5 * 1000;
	private static final long BACK_RUN_TIME_DEBUG = 10 * 1000;
	private static long top_run_time = TOP_RUN_TIME_PRODUCE;
	private static long back_run_time = BACK_RUN_TIME_PRODUCE;
	private static long nowTime1;
	private boolean isrun = false;
	private boolean statue = false;// 是否运行在前台
	private BookCatcher bookChatcher;
	private int index = 100;
	private long lastTime = 0;

	public static void startService(Context context) {
		Intent it = new Intent();
		it.setClass(context, RemindService.class);
		context.startService(it);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		LogUtil.i(TAG, "服务oncreate....");
		if (Config.SERVICE_DEBUG) {
			top_run_time = TOP_RUN_TIME_DEBUG;
			back_run_time = BACK_RUN_TIME_DEBUG;
		} else {
			top_run_time = TOP_RUN_TIME_PRODUCE;
			back_run_time = BACK_RUN_TIME_PRODUCE;
		}
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		nowTime1 = Calendar.getInstance().getTimeInMillis();
		remind();

	}

	void remind() {
		if (!isrun) {
			// 提醒服务
			TaskExecutor.getInstance().executeTask(new Runnable() {
				public void run() {
					while (true) {
						// 是否关闭了提醒
						if (!Cookies.getUserSetting().isNotifiOpen()) {
							return;
						}
						// 判断是否是24点 到 7点是否提醒
						if (!Config.SERVICE_DEBUG) {
							int hours = new Date().getHours();
							if (0 <= hours && hours <= 7) {
								return;
							}
						}
						// 是否前台运行，如果前台运行则更新圈子未读消息字数，如果后台运行则通知栏提醒
						statue = PhoneUtil.isTopActivity(RemindService.this);
						if (statue) {
							if (System.currentTimeMillis() - Long.valueOf(nowTime1) > top_run_time) {
								nowTime1 = System.currentTimeMillis();
								getUpdateChapter();
							}
						} else {
							if (System.currentTimeMillis() - Long.valueOf(nowTime1) > back_run_time) {
								nowTime1 = System.currentTimeMillis();
								getUpdateChapter();
							}
						}
						try {
							if (!Config.SERVICE_DEBUG)
								Thread.sleep(60 * 1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						} // 睡眠10s
					}
				}
			});
			isrun = true;
		}
	}

	@SuppressWarnings("static-access")
	private void getUpdateChapter() {
		LogUtil.writeFile("update.txt", TimeUtil.longToTime(System.currentTimeMillis()));
		List<BookDesc> books = bookChatcher.findBookInStore(this);
		if (books != null && books.size() > 0 && MiscUtils.isNetwork(this)) {
			List<BookDesc> newDescList = new ArrayList<BookDesc>();
			for (BookDesc bookDesc : books) {
				if (StringUtil.isEmpty(bookDesc.getStatus())) {
					newDescList.add(bookDesc);
				} else {
					if (!bookDesc.getStatus().contains("完结") && !bookDesc.getStatus().contains("全本"))
						newDescList.add(bookDesc);
				}
			}
			try {
				BookCatcher.updateBookOnline(newDescList, this);
			} catch (BadNetworkException e) {
				e.printStackTrace();
			}

			if (newDescList != null && newDescList.size() > 0) {
				for (BookDesc bookDesc : newDescList) {
					if (bookDesc.getHasUpdated() == 1)
						addNotification(bookDesc.getBookName(), bookDesc.getLastChapterTitle());
				}
			}
		}
	}

	/*
	 * 发送通知
	 */
	@SuppressWarnings("deprecation")
	private void addNotification(String bookName, String content) {
		if (!StringUtil.isEmpty(content) && !Cookies.getUserSetting().hasNotifiString(content)) {
			index++;
			Cookies.getUserSetting().setNotifiString(content);
			String title = StringUtil.isEmpty(bookName) ? "亲,有更新啦！" : "《" + bookName + "》" + "有更新啦！";
			Notification notification = new Notification(R.drawable.logo, title, System.currentTimeMillis());
			NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			if (System.currentTimeMillis() - lastTime > 60 * 1000) {
				lastTime = System.currentTimeMillis();
				if (Cookies.getUserSetting().isNotifiVoiceOpen())// b) 发出提示音，如：
					notification.defaults |= Notification.DEFAULT_SOUND;
				if (Cookies.getUserSetting().isNotifiShakeOpen()) {// c) 手机振动，如：
					notification.defaults |= Notification.DEFAULT_VIBRATE;
					long[] vibrate = { 0, 100, 200, 300 };
					notification.vibrate = vibrate;
				}
			}
			Intent it = new Intent(this, BookMainActivity.class);
			it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// 这行代码会解决此问题
			PendingIntent pi = PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_UPDATE_CURRENT);
			notification.setLatestEventInfo(this, title, "  " + content, pi);
			nm.notify(index, notification);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		flags = START_STICKY;
		LogUtil.i(TAG, "服务onStartCommand...");
		LogUtil.writeFile("update.txt", "服务onStartCommand...");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Intent it = new Intent();
		it.setClass(this, RemindService.class);
		this.startService(it);
		LogUtil.i(TAG, "服务onDestroy....");
		LogUtil.writeFile("update.txt", "服务onDestroy....");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
