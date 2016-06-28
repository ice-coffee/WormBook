package com.jie.book.work.utils;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.PopupWindow;

import com.bond.bookcatch.vo.BookCatalog;
import com.bond.bookcatch.vo.BookDesc;
import com.jie.book.work.R;
import com.jie.book.work.activity.BaseActivity;
import com.jie.book.work.activity.ReadActivity;
import com.jie.book.work.application.BookApplication;
import com.jie.book.work.read.BookCatchManager;
import com.jie.book.work.read.BookCatchManager.DefaultInterface;
import com.jie.book.work.service.BookDownloadService;
import com.jie.book.work.utils.UIHelper.OnDialogClickListener;
import com.jie.book.work.view.PopupHelper;
import com.jie.book.work.view.PopupHelper.PopGravity;
import com.jie.book.work.view.PopupHelper.PopStyle;

@SuppressLint("HandlerLeak")
public class DownloadUtil {
	private BaseActivity context;
	private ReadActivity readActiviy;
	private View view;
	private BookDesc bookDesc;
	private PopupWindow sharePop;
	private BookCatalog currentCatalog;
	private BookCatalog startCatalog;
	private BookCatchManager chatchManager;
	private int type = 1;// 1无通知栏，0有通知栏

	public DownloadUtil(BaseActivity context, View view, BookDesc bookDesc, int type) {
		this.view = view;
		this.type = type;
		this.context = context;
		this.bookDesc = bookDesc;
		this.chatchManager = new BookCatchManager(context);
		this.currentCatalog = bookDesc.getLastReadChapter() == null ? null : bookDesc.getLastReadChapter().getCatalog();
		if (context instanceof ReadActivity)
			readActiviy = (ReadActivity) context;
	}

	// 下载对话框
	public void showDownloadCheckDialog() {
		if (MiscUtils.isNetwork(context)) {
			if (!MiscUtils.isWifiConnected(context)) {
				UIHelper.showTowButtonDialog(context, "当前不是wifi网络，是否要继续缓存", "取消", "确定", true, null,
						new OnDialogClickListener() {

							@Override
							public void onClick() {
								showDownloadDialog();
							}
						});
				return;
			} else {
				if (BookApplication.getInstance().downLoadNotifiMap.containsKey(bookDesc.getGid())) {
					context.showToast("《" + bookDesc.getBookName() + "》" + "正在缓存中，请等候缓存完成");
					return;
				} else {
					if (BookApplication.getInstance().downLoadNotifiMap.size() >= 3) {
						context.showToast("同时缓存书籍数量不能超过三本");
					} else {
						showDownloadDialog();
					}
				}
			}
		} else {
			context.showToast("网络异常，请检查网络！");
			return;
		}
	}

	// 下载对话框
	@SuppressLint("InflateParams")
	public void showDownloadDialog() {
		chatchManager.refreshBookDesc(bookDesc);
		sharePop = PopupHelper.newBasicPopupWindow(context, PopStyle.MATCH_PARENT);
		View popupView = LayoutInflater.from(context).inflate(R.layout.dialog_download, null);
		sharePop.setContentView(popupView);
		PopupHelper.showLocationPop(sharePop, view, PopGravity.TOP);
		View touchView = popupView.findViewById(R.id.view_touch);
		touchView.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				sharePop.dismiss();
				if (type == 1 && readActiviy != null)
					readActiviy.getReadMenu().toggoleMenu();
				return true;
			}
		});
		popupView.findViewById(R.id.dialog_choose_tv1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sharePop.dismiss();
				if (bookDesc.getHasOffline() == 1) {
					reDownloadDialog(0);
				} else {
					download100();
				}
			}
		});
		popupView.findViewById(R.id.dialog_choose_tv2).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sharePop.dismiss();
				if (bookDesc.getHasOffline() == 1) {
					reDownloadDialog(1);
				} else {
					downloadLastAll();
				}
			}
		});
		popupView.findViewById(R.id.dialog_choose_tv3).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sharePop.dismiss();
				if (bookDesc.getHasOffline() == 1) {
					reDownloadDialog(-1);
				} else {
					downloadAll();
				}
			}
		});
		popupView.findViewById(R.id.share_dis).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sharePop.dismiss();
				if (type == 1 && readActiviy != null)
					readActiviy.getReadMenu().toggoleMenu();
			}
		});
	}

	// 如果有缓存目录，重新确认是否缓存，0缓存100章，1后面全部 -1全部
	public void reDownloadDialog(final int size) {
		chatchManager.downloadCheck(bookDesc, currentCatalog, size, context, new DefaultInterface() {

			@Override
			public void getDefault(boolean haBack) {
				if (haBack) {
					switch (size) {
					case 0:
						download100();
						break;
					case 1:
						downloadLastAll();
						break;
					case -1:
						downloadAll();
						break;
					}
				} else {
					String lastOfflineChapter = chatchManager.findLastOfflineCatalog(bookDesc, context);
					String content = StringUtil.isEmpty(lastOfflineChapter) ? "已缓存" : "已经缓存至 " + lastOfflineChapter;
					UIHelper.showTowButtonDialog(context, "《" + bookDesc.getBookName() + "》" + content + "，是否重新缓存？",
							"取消", "确定", true, null, new OnDialogClickListener() {

								@Override
								public void onClick() {
									switch (size) {
									case 0:
										download100();
										break;
									case 1:
										downloadLastAll();
										break;
									case -1:
										downloadAll();
										break;
									}
								}
							});
				}
			}
		});
	}

	// 缓存后面100章
	private void download100() {
		StatisticUtil.sendEvent(context, StatisticUtil.DOWNLOAD_100);
		startCatalog = currentCatalog;
		BookDownloadService.luanch(context, bookDesc, startCatalog, 100, type);
	}

	// 缓存后面全部章节
	private void downloadLastAll() {
		StatisticUtil.sendEvent(context, StatisticUtil.DOWNLOAD_LAST_TOTAL);
		startCatalog = currentCatalog;
		BookDownloadService.luanch(context, bookDesc, startCatalog, -1, type);
	}

	// 缓存全部
	private void downloadAll() {
		StatisticUtil.sendEvent(context, StatisticUtil.DOWNLOAD_TOTAL);
		startCatalog = null;
		BookDownloadService.luanch(context, bookDesc, startCatalog, -1, type);
	}

	public void downloadAllFromShelf() {
		if (MiscUtils.isNetwork(context)) {
			if (!MiscUtils.isWifiConnected(context)) {
				UIHelper.showTowButtonDialog(context, "当前不是wifi网络，是否要继续缓存", "取消", "确定", true, null,
						new OnDialogClickListener() {

							@Override
							public void onClick() {
								downloadLastAll();
							}
						});
				return;
			} else {
				if (BookApplication.getInstance().downLoadNotifiMap.containsKey(bookDesc.getGid())) {
					context.showToast("《" + bookDesc.getBookName() + "》" + "正在缓存中，请等候缓存完成");
					return;
				} else {
					if (BookApplication.getInstance().downLoadNotifiMap.size() >= 3) {
						context.showToast("同时缓存书籍数量不能超过三本");
					} else {
						downloadLastAll();
					}
				}
			}
		} else {
			context.showToast("网络异常，请检查网络！");
			return;
		}
	}

}
