package com.jie.book.work.service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.jie.book.work.R;
import com.jie.book.work.activity.BookMainActivity;
import com.jie.book.work.download.DownLoadCallback;
import com.jie.book.work.download.DownloadManager;

public class DownloadService extends Service {
	public static final String ACTION_NAME = "com.jie.book.work.service.DownloadService";
	// 下载状态
	private final static int DOWNLOAD_COMPLETE = 0;
	private final static int DOWNLOAD_FAIL = 1;
	private final static int DOWNLOAD_LOADING = 2;
	private static final String APK_NAME = "apk_name";
	private static final String APK_PATH = "apk_path";

	// 通知栏
	private Map<String, DownLoadItem> downloadMap = new HashMap<String, DownLoadItem>();
	private Intent updateIntent = null;
	private PendingIntent updatePendingIntent = null;
	private NotificationManager updateNotificationManager = null;
	private LocalBroadcastManager manager;
	private DownloadManager downloadManager;
	private int index = 0;
	private long currentTime = 0;

	@SuppressLint("HandlerLeak")
	private Handler updateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			DownLoadItem downloadItem = (DownLoadItem) msg.obj;
			RemoteViews remoteView = downloadItem.getRemoteView();
			Notification notification = downloadItem.getNotification();

			switch (msg.what) {
			case DOWNLOAD_COMPLETE:
				File file = downloadManager.getFile(downloadItem.getFileName() + ".apk");
				Uri uri = Uri.fromFile(file);
				Intent installIntent = new Intent(Intent.ACTION_VIEW);
				installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
				remoteView.setProgressBar(R.id.pb, 100, 100, false);
				remoteView.setViewVisibility(R.id.pb, View.GONE);
				remoteView.setTextViewText(R.id.down_tx, "下载完成点击安装");
				updatePendingIntent = PendingIntent.getActivity(DownloadService.this, 0, installIntent, 0);
				notification.contentIntent = updatePendingIntent;
				Intent intent = new Intent(Intent.ACTION_VIEW); //
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
				DownloadService.this.startActivity(intent);
				Intent intent2 = new Intent(ACTION_NAME);
				manager.sendBroadcast(intent2);
				break;
			case DOWNLOAD_FAIL:
				remoteView.setTextViewText(R.id.down_tx, "下载失败");
				break;
			case DOWNLOAD_LOADING:
				int downloadPercent = (int) (downloadItem.getCurrentSize() * 100 / downloadItem.getTotalSize());
				remoteView.setProgressBar(R.id.pb, 100, downloadPercent, false);
				remoteView.setTextViewText(R.id.down_tx, "已下载" + downloadPercent + "%");
				break;
			}
			updateNotificationManager.notify(downloadItem.getTaskId(), notification);
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public static void luanch(Context context, String apkName, String apkUrl) {
		Intent updateIntent = new Intent();
		updateIntent.setClass(context, DownloadService.class);
		updateIntent.putExtra(APK_NAME, apkName);
		updateIntent.putExtra(APK_PATH, apkUrl);
		context.startService(updateIntent);
	}

	public void addTask(String url, String fileName) {
		downloadManager.addHandler(url, fileName + ".apk");
		if (!downloadMap.containsKey(url)) {
			addItem(url, fileName);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		if (intent != null) {
			manager = LocalBroadcastManager.getInstance(getApplicationContext());
			downloadManager = DownloadManager.getDownloadManager(this);
			updateNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			String apkname = intent.getStringExtra(APK_NAME);
			String apkpath = intent.getStringExtra(APK_PATH);
			addTask(apkpath, apkname);
			downloadManager.setDownLoadCallback(new DownLoadCallback() {

				@Override
				public void onFailure(String url, String strMsg) {
					super.onFailure(url, strMsg);
					Toast.makeText(DownloadService.this, "下载错误，请重新下载", Toast.LENGTH_SHORT).show();
					DownLoadItem downloadItem = downloadMap.get(url);
					Message msg = Message.obtain();
					msg.what = DOWNLOAD_COMPLETE;
					msg.obj = downloadItem;
					updateHandler.sendMessage(msg);
				}

				@Override
				public void onSuccess(String url) {
					Toast.makeText(DownloadService.this, "下载完成", Toast.LENGTH_SHORT).show();
					DownLoadItem downloadItem = downloadMap.get(url);
					Message msg = Message.obtain();
					msg.what = DOWNLOAD_COMPLETE;
					msg.obj = downloadItem;
					updateHandler.sendMessage(msg);
				}

				@Override
				public void onLoading(String url, long totalSize, long currentSize, long speed) {
					super.onLoading(url, totalSize, currentSize, speed);
					if (System.currentTimeMillis() - currentTime > 2 * 1000) {
						currentTime = System.currentTimeMillis();
						DownLoadItem downloadItem = downloadMap.get(url);
						downloadItem.setCurrentSize(currentSize);
						downloadItem.setTotalSize(totalSize);
						Message msg = Message.obtain();
						msg.what = DOWNLOAD_LOADING;
						msg.obj = downloadItem;
						updateHandler.sendMessage(msg);
					}
				}
			});
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopSelf();
	}

	private void addItem(String url, String name) {
		Toast.makeText(DownloadService.this, name + "正在下载中,完成后提示安装", Toast.LENGTH_SHORT).show();
		RemoteViews remoteView = new RemoteViews(getPackageName(), R.layout.view_notification_download);
		Notification updateNotification = new Notification();
		updateNotification.icon = R.drawable.logo;
		updateNotification.tickerText = "正在下载" + name;
		updateNotification.when = System.currentTimeMillis();
		remoteView.setTextViewText(R.id.name, name);
		// remoteView.setImageViewResource(R.id.icon, R.drawable.icon_logo);
		// 设置下载过程中，点击通知栏，回到主界面
		updateIntent = new Intent(this, BookMainActivity.class);
		updateIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);// 这行代码会解决此问题
		updatePendingIntent = PendingIntent.getActivity(this, 0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		updateNotification.contentView = remoteView;
		updateNotification.contentIntent = updatePendingIntent;
		// 发出通知
		updateNotificationManager.notify(index, updateNotification);
		DownLoadItem downloadItem = new DownLoadItem(index, name, remoteView, updateNotification);
		downloadMap.put(url, downloadItem);
		index++;
	}

	public class DownLoadItem {
		private String fileName;
		private long totalSize;
		private long currentSize;
		private int taskId;
		private RemoteViews remoteView;
		private Notification notification;

		public DownLoadItem(int taskId, String fileName, RemoteViews remoteView, Notification notification) {
			super();
			this.fileName = fileName;
			this.taskId = taskId;
			this.remoteView = remoteView;
			this.notification = notification;
		}

		public int getTaskId() {
			return taskId;
		}

		public void setTaskId(int taskId) {
			this.taskId = taskId;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public long getTotalSize() {
			return totalSize;
		}

		public void setTotalSize(long totalSize) {
			this.totalSize = totalSize;
		}

		public long getCurrentSize() {
			return currentSize;
		}

		public void setCurrentSize(long currentSize) {
			this.currentSize = currentSize;
		}

		public RemoteViews getRemoteView() {
			return remoteView;
		}

		public void setRemoteView(RemoteViews remoteView) {
			this.remoteView = remoteView;
		}

		public Notification getNotification() {
			return notification;
		}

		public void setNotification(Notification notification) {
			this.notification = notification;
		}

	}

}
