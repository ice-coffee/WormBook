package com.jie.book.work.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.bond.bookcatch.callback.ProgressCallback;
import com.bond.bookcatch.vo.BookCatalog;
import com.bond.bookcatch.vo.BookDesc;
import com.jie.book.work.R;
import com.jie.book.work.activity.BookMainActivity;
import com.jie.book.work.application.BookApplication;
import com.jie.book.work.entity.DownloadInfo;
import com.jie.book.work.read.BookCatchManager;

@SuppressLint("HandlerLeak")
public class BookDownloadService extends Service {
	public static final String ACTION_NAME = "com.jie.book.work.DOWNLOAD_SERVICE";
	public static final String DOWLOAD_INFO = "download_info";
	public static final String DOWLOAD_STATUS = "download_status";// 0进行中，1完成，2错误,3UI开始,4下载开始
	private static final String BOOK_DESC = "book_desc";
	private static final String BOOK_CATALOG = "book_catalog";
	private static final String DOWLOAD_SIZE = "download_size";
	private static final String DOWLOAD_TYPE = "download_type";

	private final static int HANDLER_DOWNLOAD_COMPLETE_1 = 0;
	private final static int HANDLER_DOWNLOAD_COMPLETE_2 = 1;
	private final static int HANDLER_DOWNLOAD_PRECENT_1 = 2;
	private final static int HANDLER_DOWNLOAD_PRECENT_2 = 3;
	private final static int HANDLER_DOWNLOAD_ERROR_1 = 4;
	private final static int HANDLER_DOWNLOAD_ERROR_2 = 5;
	private final static int HANDLER_UI_BEGIN = 6;
	private final static int HANDLER_DOWNLOAD_BEGIN = 7;
	private LocalBroadcastManager manager;
	private NotificationManager notificationManager;
	private BookCatchManager catchManager;
	private long currentTime = 0;

	public static void luanch(Context context, BookDesc bookDesc, BookCatalog catalog, int size, int type) {
		Intent intent = new Intent();
		intent.setClass(context, BookDownloadService.class);
		intent.putExtra(BOOK_DESC, bookDesc);
		intent.putExtra(BOOK_CATALOG, catalog);
		intent.putExtra(DOWLOAD_SIZE, size);
		intent.putExtra(DOWLOAD_TYPE, type);
		context.startService(intent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (intent != null) {
			catchManager = new BookCatchManager(this);
			manager = LocalBroadcastManager.getInstance(getApplicationContext());
			BookDesc bookDesc = (BookDesc) intent.getSerializableExtra(BOOK_DESC);
			BookCatalog startCatalog = (BookCatalog) intent.getSerializableExtra(BOOK_CATALOG);
			int type = intent.getIntExtra(DOWLOAD_TYPE, 0);
			int size = intent.getIntExtra(DOWLOAD_SIZE, 0);
			if (bookDesc != null) {
				DownloadInfo info = new DownloadInfo();
				info.setType(type);
				info.setSize(size);
				info.setBookDesc(bookDesc);
				info.setBookCatalog(startCatalog);
				downloadBook(info);
			}
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopSelf();
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			DownloadInfo bookInfo = (DownloadInfo) msg.obj;
			switch (msg.what) {
			case HANDLER_DOWNLOAD_COMPLETE_1:
				showToast("《" + bookInfo.getBookDesc().getBookName() + "》" + "缓存完成");
				if (bookInfo.getRemoteView() != null)
					bookInfo.getRemoteView().setTextViewText(R.id.name,
							"《" + bookInfo.getBookDesc().getBookName() + "》" + "缓存完成");
				if (bookInfo.getNotifi() != null)
					notificationManager.notify(bookInfo.getNotifiId(), bookInfo.getNotifi());
				if (BookApplication.getInstance().downLoadNotifiMap.containsKey(bookInfo.getBookDesc().getGid()))
					BookApplication.getInstance().downLoadNotifiMap.remove(bookInfo.getBookDesc().getGid());
				Intent intent5 = new Intent(ACTION_NAME);
				intent5.putExtra(DOWLOAD_STATUS, 1);
				intent5.putExtra(DOWLOAD_INFO, bookInfo);
				manager.sendBroadcast(intent5);
				break;
			case HANDLER_DOWNLOAD_PRECENT_1:
				if (bookInfo.getRemoteView() != null) {
					bookInfo.getRemoteView().setProgressBar(R.id.pb, 100, bookInfo.getPrecent(), false);
					bookInfo.getRemoteView().setTextViewText(R.id.down_tx,
							bookInfo.getIndex() + "/" + bookInfo.getTotal());
				}
				if (bookInfo.getNotifi() != null)
					notificationManager.notify(bookInfo.getNotifiId(), bookInfo.getNotifi());
				break;
			case HANDLER_DOWNLOAD_COMPLETE_2:
				if (BookApplication.getInstance().downLoadNotifiMap.containsKey(bookInfo.getBookDesc().getGid()))
					BookApplication.getInstance().downLoadNotifiMap.remove(bookInfo.getBookDesc().getGid());
				showToast("《" + bookInfo.getBookDesc().getBookName() + "》" + "缓存完成");
				Intent intent = new Intent(ACTION_NAME);
				intent.putExtra(DOWLOAD_STATUS, 1);
				intent.putExtra(DOWLOAD_INFO, bookInfo);
				manager.sendBroadcast(intent);
				break;
			case HANDLER_DOWNLOAD_PRECENT_2:
				Intent intent1 = new Intent(ACTION_NAME);
				intent1.putExtra(DOWLOAD_STATUS, 0);
				intent1.putExtra(DOWLOAD_INFO, bookInfo);
				manager.sendBroadcast(intent1);
				break;
			case HANDLER_DOWNLOAD_ERROR_1:
				if (BookApplication.getInstance().downLoadNotifiMap.containsKey(bookInfo.getBookDesc().getGid()))
					BookApplication.getInstance().downLoadNotifiMap.remove(bookInfo.getBookDesc().getGid());
				showToast("《" + bookInfo.getBookDesc().getBookName() + "》" + "缓存失败，请重新缓存!");
				Intent intent2 = new Intent(ACTION_NAME);
				intent2.putExtra(DOWLOAD_STATUS, 2);
				intent2.putExtra(DOWLOAD_INFO, bookInfo);
				manager.sendBroadcast(intent2);
				break;
			case HANDLER_DOWNLOAD_ERROR_2:
				if (BookApplication.getInstance().downLoadNotifiMap.containsKey(bookInfo.getBookDesc().getGid()))
					BookApplication.getInstance().downLoadNotifiMap.remove(bookInfo.getBookDesc().getGid());
				showToast("《" + bookInfo.getBookDesc().getBookName() + "》" + "缓存失败，请重新缓存!");
				Intent intent6 = new Intent(ACTION_NAME);
				intent6.putExtra(DOWLOAD_STATUS, 2);
				intent6.putExtra(DOWLOAD_INFO, bookInfo);
				manager.sendBroadcast(intent6);
				break;
			case HANDLER_UI_BEGIN:
				showDownloadNotif(bookInfo);
				Intent intent3 = new Intent(ACTION_NAME);
				intent3.putExtra(DOWLOAD_STATUS, 3);
				intent3.putExtra(DOWLOAD_INFO, bookInfo);
				manager.sendBroadcast(intent3);
				break;
			case HANDLER_DOWNLOAD_BEGIN:
				Intent intent4 = new Intent(ACTION_NAME);
				intent4.putExtra(DOWLOAD_STATUS, 4);
				intent4.putExtra(DOWLOAD_INFO, bookInfo);
				manager.sendBroadcast(intent4);
				break;
			}
		}

	};

	// 下载书籍
	public void downloadBook(final DownloadInfo bookinfo) {
		if (bookinfo != null) {
			Message msg = Message.obtain();
			msg.what = HANDLER_UI_BEGIN;
			msg.obj = bookinfo;
			mHandler.sendMessage(msg);
			catchManager.downloadBook(bookinfo.getBookDesc(), bookinfo.getBookCatalog(), bookinfo.getSize(),
					new ProgressCallback() {

						@Override
						public void progress(int index, int last, int percent) {
							if (bookinfo.getType() == 1) {
								bookinfo.setPrecent(percent);
								bookinfo.setIndex(index);
								bookinfo.setTotal(last);
								Intent intent1 = new Intent(ACTION_NAME);
								intent1.putExtra(DOWLOAD_STATUS, 0);
								intent1.putExtra(DOWLOAD_INFO, bookinfo);
								manager.sendBroadcast(intent1);
							} else {
								if (index == last) {
									bookinfo.setPrecent(percent);
									bookinfo.setIndex(index);
									bookinfo.setTotal(last);
									Message msg = Message.obtain();
									msg.what = HANDLER_DOWNLOAD_PRECENT_1;
									msg.obj = bookinfo;
									mHandler.sendMessage(msg);
								} else {
									if (System.currentTimeMillis() - currentTime > 2 * 1000) {
										currentTime = System.currentTimeMillis();
										bookinfo.setPrecent(percent);
										bookinfo.setIndex(index);
										bookinfo.setTotal(last);
										Message msg = Message.obtain();
										msg.what = HANDLER_DOWNLOAD_PRECENT_1;
										msg.obj = bookinfo;
										mHandler.sendMessage(msg);
									}
								}
							}
						}

						@Override
						public void begin() {
							Message msg = Message.obtain();
							msg.what = HANDLER_DOWNLOAD_BEGIN;
							msg.obj = bookinfo;
							mHandler.sendMessage(msg);
						}

						@Override
						public void success() {

						}

						@Override
						public void failure(Exception e) {
							if (bookinfo.getType() == 1) {
								Message msg = Message.obtain();
								msg.what = HANDLER_DOWNLOAD_ERROR_2;
								msg.obj = bookinfo;
								mHandler.sendMessage(msg);
							} else {
								Message msg = Message.obtain();
								msg.what = HANDLER_DOWNLOAD_ERROR_1;
								msg.obj = bookinfo;
								mHandler.sendMessage(msg);
							}

						}

						@Override
						public void complete(int size) {
							if (bookinfo.getType() == 1) {
								Message msg = Message.obtain();
								msg.what = HANDLER_DOWNLOAD_COMPLETE_2;
								msg.obj = bookinfo;
								mHandler.sendMessage(msg);
							} else {
								Message msg = Message.obtain();
								msg.what = HANDLER_DOWNLOAD_COMPLETE_1;
								msg.obj = bookinfo;
								mHandler.sendMessage(msg);
							}

						}

					});
		}
	}

	@SuppressWarnings("deprecation")
	public void showDownloadNotif(DownloadInfo bookInfo) {
		int nitifiId = BookApplication.getInstance().downLoadNotifiId;
		String title = "《" + bookInfo.getBookDesc().getBookName() + "》" + "缓存中";
		showToast(title);
		if (bookInfo.getType() == 0) {
			RemoteViews remoteView = new RemoteViews(getPackageName(), R.layout.view_notification_download);
			Notification notification = new Notification(R.drawable.logo, title, System.currentTimeMillis());
			notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			remoteView.setTextViewText(R.id.name, title);
			remoteView.setImageViewResource(R.id.icon, R.drawable.logo);
			Intent updateIntent = new Intent(this, BookMainActivity.class);
			updateIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);// 这行代码会解决此问题
			PendingIntent updatePendingIntent = PendingIntent.getActivity(this, 0, updateIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			notification.contentView = remoteView;
			notification.contentIntent = updatePendingIntent;
			notificationManager.notify(nitifiId, notification);
			bookInfo.setNotifiId(nitifiId);
			bookInfo.setNotifi(notification);
			bookInfo.setRemoteView(remoteView);
		}
		BookApplication.getInstance().downLoadNotifiMap.put(bookInfo.getBookDesc().getGid(),
				BookApplication.getInstance().downLoadNotifiId);
		BookApplication.getInstance().downLoadNotifiId++;
	}

	private void showToast(String content) {
		Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
	}
}
