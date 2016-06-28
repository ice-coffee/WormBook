package com.jie.book.work.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bond.bookcatch.local.vo.LocalBookDesc;
import com.bond.bookcatch.vo.BookDesc;
import com.google.gson.Gson;
import com.jie.book.work.R;
import com.jie.book.work.activity.BookDetailActivity;
import com.jie.book.work.activity.BookKeepActivity;
import com.jie.book.work.activity.KeepGuideActivity;
import com.jie.book.work.activity.RandomReadActivity;
import com.jie.book.work.activity.ReadActivity;
import com.jie.book.work.application.BookApplication;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.entity.App;
import com.jie.book.work.local.LocalBookManager;
import com.jie.book.work.local.LocalReaderActivity;
import com.jie.book.work.read.BookCatchManager.BookDescListInterface;
import com.jie.book.work.read.BookCatchManager.DefaultInterface;
import com.jie.book.work.service.BookDownloadService;
import com.jie.book.work.service.DownloadService;
import com.jie.book.work.utils.Config;
import com.jie.book.work.utils.DownloadUtil;
import com.jie.book.work.utils.HttpDownloader;
import com.jie.book.work.utils.HttpDownloader.httpDownloadCallBack;
import com.jie.book.work.utils.ImageLoadUtil;
import com.jie.book.work.utils.ImageLoadUtil.ImageType;
import com.jie.book.work.utils.MiscUtils;
import com.jie.book.work.utils.StatisticUtil;
import com.jie.book.work.utils.StringUtil;
import com.jie.book.work.utils.TimeUtil;
import com.jie.book.work.utils.UIHelper;
import com.jie.book.work.utils.UIHelper.OnDialogClickListener;
import com.jie.book.work.view.pullrefresh.PullToRefreshBase;
import com.jie.book.work.view.pullrefresh.PullToRefreshBase.OnRefreshListener;
import com.jie.book.work.view.pullrefresh.PullToRefreshListView;

public class BookShelfFragment extends BaseFragment implements OnClickListener {
	private List<BookDesc> storeBookList = new ArrayList<BookDesc>();
	private PullToRefreshListView listView;
	private BookSelfAdapter adapter;
	private DownloadBroadcastReceiver broadcastReceiver;
	private Dialog dialog;
	private View keepView, viewRec, addView, viewRoot;
	private TextView tvCount, tvKeepNeed;
	private TextView tvAppName, tvAppDis, tvAdd;
	private ImageView ivAppIcon;
	private Button btnAdd;
	private View keepParentView;
	private boolean isFirst = true;
	private boolean removeRec = false;
	private App recApp;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fg_book_shelf, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initBroadcastReceive();
	}

	@SuppressLint("InflateParams")
	@Override
	protected void initUI() {
		listView = (PullToRefreshListView) getView().findViewById(R.id.pull_refresh_list);
		LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, UIHelper.dipToPx(20));
		View footView = new View(activity);
		footView.setLayoutParams(param);
		listView.getRefreshableView().addFooterView(footView);
		viewRoot = getView().findViewById(R.id.fg_book_shelf_root);
		adapter = new BookSelfAdapter();
		listView.setAdapter(adapter);
		initUIReal();
	}

	private void initUIReal() {
		viewRoot.setBackgroundColor(Cookies.getReadSetting().isNightMode() ? getResources().getColor(
				R.color.color_ef_black) : getResources().getColor(R.color.book_shelf_bg));
		keepParentView = LayoutInflater.from(activity).inflate(
				Cookies.getReadSetting().isNightMode() ? R.layout.view_shelf_keep_ef : R.layout.view_shelf_keep, null);
		addView = keepParentView.findViewById(R.id.book_shelf_add_layout);
		addView.setVisibility(storeBookList.size() > 0 ? View.GONE : View.VISIBLE);
		viewRec = keepParentView.findViewById(R.id.book_rec_view);
		tvCount = (TextView) keepParentView.findViewById(R.id.item_book_count);
		keepView = keepParentView.findViewById(R.id.book_keep_view);
		tvKeepNeed = (TextView) keepParentView.findViewById(R.id.self_keep_need);
		tvAppName = (TextView) keepParentView.findViewById(R.id.item_book_rec_name);
		tvAppDis = (TextView) keepParentView.findViewById(R.id.item_book_rec_dis);
		ivAppIcon = (ImageView) keepParentView.findViewById(R.id.item_book_rec_book_icon);
		tvAdd = (TextView) keepParentView.findViewById(R.id.book_shelf_add_tv);
		btnAdd = (Button) keepParentView.findViewById(R.id.book_shelf_add_btn);
		viewRec.setVisibility(View.GONE);
		keepView.setVisibility(Cookies.getUserSetting().isGoneKeep() ? View.GONE : View.VISIBLE);
		listView.getRefreshableView().addHeaderView(keepParentView);
		initListener();
	}

	public void SwitchModel() {
		listView.getRefreshableView().removeHeaderView(keepParentView);
		initUIReal();
		adapter.notifyDataSetChanged();
	}

	private void initBroadcastReceive() {
		if (broadcastReceiver == null) {
			broadcastReceiver = new DownloadBroadcastReceiver();
			LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(activity);
			IntentFilter mIntentFilter = new IntentFilter();
			mIntentFilter.addAction(BookDownloadService.ACTION_NAME);
			lbm.registerReceiver(broadcastReceiver, mIntentFilter);
		} else {
			LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(activity);
			IntentFilter mIntentFilter = new IntentFilter();
			mIntentFilter.addAction(BookDownloadService.ACTION_NAME);
			lbm.registerReceiver(broadcastReceiver, mIntentFilter);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!isFirst)
			getData(false);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(activity).unregisterReceiver(broadcastReceiver);
	}

	@Override
	protected void initData() {
		listView.setRefreshing();
	}

	private void initListener() {
		tvAdd.setOnClickListener(this);
		btnAdd.setOnClickListener(this);
		viewRec.setOnClickListener(this);
		keepView.setOnClickListener(this);
		listView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				getData(true);
			}
		});
		viewRec.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				UIHelper.showTowButtonDialog(activity, "是否删除？", "取消", "确定", true, null, new OnDialogClickListener() {

					@Override
					public void onClick() {
						removeRec = true;
						viewRec.setVisibility(View.GONE);
					}
				});
				return true;
			}
		});
		keepView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				if (chatchManager.findBookCountInKeep() == 0)
					UIHelper.showTowButtonDialog(activity, "是否隐藏养肥区", "取消", "确定", true, null,
							new OnDialogClickListener() {

								@Override
								public void onClick() {
									Cookies.getUserSetting().setGoneKeep(true);
									keepView.setVisibility(View.GONE);
								}
							});
				return true;
			}
		});
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.book_rec_view:
			if (recApp != null) {
				String size = recApp.getSize() == 0 ? "" : recApp.getSize() + "M";
				UIHelper.showTowButtonDialog(activity, recApp.getName() + "," + size + "\n是否立即下载？", "取消", "确定", true,
						null, new OnDialogClickListener() {

							@Override
							public void onClick() {
								DownloadService.luanch(activity, recApp.getName(), recApp.getUrl());
							}
						});
			}
			break;
		case R.id.book_keep_view:
			if (Cookies.getUserSetting().isFristKeep()) {
				KeepGuideActivity.luanch(activity);
			} else {
				BookKeepActivity.luanch(activity);
			}
			break;
		case R.id.book_shelf_add_btn:
			RandomReadActivity.luanch(activity, 0);
			break;
		case R.id.book_shelf_add_tv:
			RandomReadActivity.luanch(activity, 0);
			break;
		}
	}

	public void getData(final boolean update) {
		initKeep();
		initRecApp();
		chatchManager.getBookShelfList(new BookDescListInterface() {

			@Override
			public void getBookDescList(List<BookDesc> list) {
				isFirst = false;
				storeBookList.clear();
				if (list != null && list.size() > 0) {
					addView.setVisibility(View.GONE);
					storeBookList.addAll(list);
					adapter.notifyDataSetChanged();
					if (update)
						updateBookChapter();
					else
						listView.onRefreshComplete();
				} else {
					addView.setVisibility(View.VISIBLE);
					adapter.notifyDataSetChanged();
					listView.onRefreshComplete();
				}
			}
		});
	}

	// 初始化养肥区
	private void initKeep() {
		final int count = chatchManager.findBookCountInKeep();
		if (count > 0) {
			keepView.setVisibility(View.VISIBLE);
			Cookies.getUserSetting().setGoneKeep(false);
			String removeBooks = chatchManager.findKeepTimeExpireBookTitle();
			if (StringUtil.isEmpty(removeBooks)) {
				tvKeepNeed.setVisibility(View.GONE);
				tvCount.setText(count + "本书正在养肥");
			} else {
				tvKeepNeed.setVisibility(View.VISIBLE);
				tvCount.setText(removeBooks + "已经养肥了，赶快去宰杀吧！");
			}
		} else {
			tvCount.setText("暂时没有养肥书籍");
			tvKeepNeed.setVisibility(View.GONE);
		}
	}

	// 初始化推荐APP
	private void initRecApp() {
		if (removeRec)
			return;
		if (!MiscUtils.recShelfApp(activity))
			return;
		if (recApp != null) {
			setRecApp();
		} else {
			try {
				HttpDownloader.download(activity, Config.SHELF_APP_URL, new httpDownloadCallBack() {

					@Override
					public void onResult(String result) {
						if (!StringUtil.isEmpty(result)) {
							recApp = new Gson().fromJson(result, App.class);
							setRecApp();
						}
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 设置APP信息
	private void setRecApp() {
		if (recApp != null && !MiscUtils.checkApkExist(activity, recApp.getPackageName())) {
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					viewRec.setVisibility(View.VISIBLE);
					tvAppName.setText(recApp.getName());
					tvAppDis.setText(recApp.getShortDepict());
					ImageLoadUtil.loadImage(ivAppIcon, recApp.getLogo(), ImageType.APP_ICON);
				}
			});
		}
	}

	// 检查书架书籍更新
	public void updateBookChapter() {
		chatchManager.updateAllBookOnline(storeBookList, new DefaultInterface() {

			@Override
			public void getDefault(boolean haBack) {
				if (haBack) {
					showUpdateToast();
					adapter.notifyDataSetChanged();
				}
				listView.onRefreshComplete();
			}
		});
	}

	// 检查是否有更新
	private boolean setHasUpdate() {
		for (BookDesc book : storeBookList) {
			if (book.getHasUpdated() == 1) {
				return true;
			}
		}
		return false;
	}

	// 更新提示语
	private void showUpdateToast() {
		if (storeBookList != null && storeBookList.size() > 0 && activity.isReusem)
			showToast(setHasUpdate() ? "小说已更新" : "你追的小说暂无更新");
	}

	private class BookSelfAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return storeBookList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHoldr holder = null;
			if (convertView == null) {
				holder = new ViewHoldr();
				convertView = LayoutInflater.from(activity).inflate(R.layout.item_book_shelf, null);
				holder.viewlayout = convertView.findViewById(R.id.item_book_shelf_layout);
				holder.ivBookIcon = (ImageView) convertView.findViewById(R.id.item_book_shelf_book_icon);
				holder.ivBookTop = (ImageView) convertView.findViewById(R.id.book_shelf_top_icon);
				holder.tvBookName = (TextView) convertView.findViewById(R.id.item_book_shelf_name);
				holder.ivBookUpdate = (ImageView) convertView.findViewById(R.id.item_book_shelf_update);
				holder.tvBookChapter = (TextView) convertView.findViewById(R.id.item_book_shelf_chapter);
				holder.tvBookDownloaded = (TextView) convertView.findViewById(R.id.item_book_shelf_downloaded);
				holder.tvChapterTime = (TextView) convertView.findViewById(R.id.item_book_shelf_chapter_time);
				holder.tvBookDownloading = (ImageView) convertView.findViewById(R.id.item_book_shelf_downloading);
				holder.ivDivider = (ImageView) convertView.findViewById(R.id.item_book_shelf_divider);
				convertView.setTag(holder);
			} else {
				holder = (ViewHoldr) convertView.getTag();
			}
			final BookDesc desc = storeBookList.get(position);
			holder.viewlayout
					.setBackgroundResource(Cookies.getReadSetting().isNightMode() ? R.drawable.selector_book_shelf_item_ef
							: R.drawable.selector_book_shelf_item);
			holder.tvBookName.setTextColor(Cookies.getReadSetting().isNightMode() ? getResources().getColor(
					R.color.text_ef_light_gray) : getResources().getColor(R.color.book_shelf_item_name));
			holder.tvBookChapter.setTextColor(Cookies.getReadSetting().isNightMode() ? getResources().getColor(
					R.color.text_ef_gray) : getResources().getColor(R.color.book_shelf_item_chapter));
			holder.tvChapterTime.setTextColor(Cookies.getReadSetting().isNightMode() ? getResources().getColor(
					R.color.text_ef_gray) : getResources().getColor(R.color.book_shelf_item_chapter));
			holder.tvBookName.setText(desc.getBookName());
			holder.tvBookChapter.setText(desc.getLastChapterTitle());
			holder.ivBookTop.setVisibility(desc.getIsTop() == 1 ? View.VISIBLE : View.GONE);
			holder.ivBookUpdate.setVisibility(desc.getHasUpdated() == 1 ? View.VISIBLE : View.GONE);
			holder.ivDivider.setBackgroundColor(Cookies.getReadSetting().isNightMode() ? getResources().getColor(
					R.color.color_ef_divider) : getResources().getColor(R.color.book_shelf_item_divider));
			if (BookApplication.getInstance().downLoadNotifiMap.containsKey(desc.getGid())) {
				holder.tvBookDownloading.setVisibility(View.VISIBLE);
				holder.tvBookDownloaded.setVisibility(View.GONE);
				AnimationDrawable animationDrawable = (AnimationDrawable) holder.tvBookDownloading.getBackground();
				animationDrawable.start();
			} else {
				holder.tvBookDownloading.setVisibility(View.GONE);
				holder.tvBookDownloaded.setVisibility(desc.getHasOffline() == 1 ? View.VISIBLE : View.GONE);
				AnimationDrawable animationDrawable = (AnimationDrawable) holder.tvBookDownloading.getBackground();
				if (animationDrawable.isRunning())
					animationDrawable.stop();
			}
			if (desc.isLocalBook()) {
				LocalBookDesc localBookDesc = (LocalBookDesc) desc;
				holder.ivBookIcon.setImageResource(R.drawable.bg_txt);
				holder.tvChapterTime.setText("当前阅读进度：" + localBookDesc.getProgress() + "%");
			} else {
				ImageLoadUtil.loadImage(holder.ivBookIcon, desc.getImageUrl(), ImageType.BOOK);
				holder.tvChapterTime.setText(TimeUtil.fmttoCN(desc.getLastUpdateTime()) + "更新：");
			}
			convertView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					if (desc.isLocalBook()) {
						final LocalBookDesc localBookDesc = (LocalBookDesc) desc;
						UIHelper.showChooseDialog(activity, "删除", desc.getIsTop() == 0 ? "置顶" : "取消置顶", null, null,
								null, new OnDialogClickListener() {

									@Override
									public void onClick() {
										StatisticUtil.sendEvent(activity, StatisticUtil.MODIFI_DELETE);
										LocalBookManager.deleteBook(localBookDesc);
										showToast("删除成功");
										getData(false);
									}
								}, new OnDialogClickListener() {

									@Override
									public void onClick() {
										LocalBookManager.setBookTop(localBookDesc, desc.getIsTop() == 0);
										getData(false);
										showToast(desc.getIsTop() == 1 ? "置顶成功" : "取消置顶成功");
									}

								}, null, null, null);
					} else {
						UIHelper.showChooseDialog(activity, desc.getIsTop() == 0 ? "置顶" : "取消置顶", "缓存全本",
								desc.getHasOffline() == 1 ? "清理缓存" : null, "移入养肥区", "删除", new OnDialogClickListener() {

									@Override
									public void onClick() {
										StatisticUtil.sendEvent(activity, StatisticUtil.MODIFI_TOP);
										chatchManager.setBookDescTop(desc, desc.getIsTop() == 0,
												new DefaultInterface() {

													@Override
													public void getDefault(boolean haBack) {
														if (haBack) {
															showToast(desc.getIsTop() == 1 ? "置顶成功" : "取消置顶成功");
															getData(false);
														} else {
															showToast(desc.getIsTop() == 1 ? "置顶失败" : "取消置顶失败");
														}
													}
												});
									}
								}, new OnDialogClickListener() {

									@Override
									public void onClick() {
										DownloadUtil downloadUtil = new DownloadUtil(activity, activity
												.findViewById(R.id.fg_book_shelf_root), desc, 0);
										downloadUtil.downloadAllFromShelf();
									}
								}, desc.getHasOffline() == 1 ? new OnDialogClickListener() {

									@Override
									public void onClick() {
										StatisticUtil.sendEvent(activity, StatisticUtil.MODIFI_CLEAN);
										if (BookApplication.getInstance().downLoadNotifiMap.containsKey(desc.getGid())) {
											showToast(desc.getBookName() + "正在缓存中，请稍后清理");
										} else {
											dialog = UIHelper.showProgressDialog(dialog, activity, "清理中，请稍后");
											chatchManager.deleteOfferLineData(desc, new DefaultInterface() {

												@Override
												public void getDefault(boolean haBack) {
													dialog.dismiss();
													getData(false);
													showToast("清理成功");
												}
											});
										}
									}
								}
										: null, new OnDialogClickListener() {

									@Override
									public void onClick() {
										chatchManager.moveBookToKeepTime(desc, Cookies.getUserSetting()
												.getBookKeepTime());
										getData(false);
									}
								}, new OnDialogClickListener() {

									@Override
									public void onClick() {
										StatisticUtil.sendEvent(activity, StatisticUtil.MODIFI_DELETE);
										if (BookApplication.getInstance().downLoadNotifiMap.containsKey(desc.getGid())) {
											showToast(desc.getBookName() + "正在缓存中，请稍后删除");
										} else {
											chatchManager.deleteBookDesc(desc, new DefaultInterface() {

												@Override
												public void getDefault(boolean haBack) {
													if (haBack) {
														showToast("删除成功");
														getData(false);
													} else {
														showToast("删除失败");
													}
												}
											});
										}

									}
								});
					}
					return true;
				}
			});
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (desc.isLocalBook()) {
						LocalBookDesc localBookDesc = (LocalBookDesc) desc;
						if (new File(localBookDesc.getFilePath()).exists()) {
							LocalReaderActivity.Luanch(activity, localBookDesc);
						} else {
							LocalBookManager.deleteBook(localBookDesc);
							getData(false);
						}
					} else {
						ReadActivity.luanch(activity, desc, null);
					}
				}
			});
			holder.ivBookIcon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!desc.isLocalBook()) {
						BookDetailActivity.luanch(activity, desc);
					}
				}
			});
			return convertView;
		}
	}

	private class ViewHoldr {
		View viewlayout;
		ImageView ivBookIcon;
		ImageView ivBookTop;
		ImageView ivBookUpdate;
		ImageView ivDivider;
		TextView tvBookName;
		TextView tvBookChapter;
		TextView tvBookDownloaded;
		TextView tvChapterTime;
		ImageView tvBookDownloading;
	}

	private class DownloadBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, final Intent intent) {
			int status = intent.getIntExtra(BookDownloadService.DOWLOAD_STATUS, 1);
			if (status == 1 || status == 2 || status == 4) {
				getData(false);
			}
		}
	}

	public void setTopSelection() {
		if (storeBookList != null && storeBookList.size() > 0)
			listView.getRefreshableView().setSelection(0);
	}

	public List<BookDesc> getStoreBookList() {
		return storeBookList;
	}

}
