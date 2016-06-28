package com.jie.book.work.activity;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.bond.bookcatch.vo.BookDesc;
import com.jie.book.work.R;
import com.jie.book.work.application.BookActivityManager;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.read.BookCatchManager.DefaultInterface;
import com.jie.book.work.utils.ImageLoadUtil;
import com.jie.book.work.utils.UIHelper;
import com.jie.book.work.utils.ImageLoadUtil.ImageType;
import com.jie.book.work.utils.TimeUtil;
import com.jie.book.work.view.pullrefresh.PullToRefreshBase;
import com.jie.book.work.view.pullrefresh.PullToRefreshBase.OnRefreshListener;
import com.jie.book.work.view.pullrefresh.PullToRefreshListView;

public class BookKeepActivity extends BaseActivity implements OnClickListener {
	private List<BookDesc> keepBookList = new ArrayList<BookDesc>();
	private List<View> timeView = new ArrayList<View>();
	private PullToRefreshListView listView;
	private BookKeepAdapter adapter;
	private Dialog timeDialog;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			adapter.notifyDataSetChanged();
			listView.onRefreshComplete();
		}

	};

	public static void luanch(Activity content) {
		Intent intent = new Intent();
		intent.setClass(content, BookKeepActivity.class);
		BookActivityManager.getInstance().goTo(content, intent);
	}

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(Cookies.getReadSetting().isNightMode() ? R.layout.act_book_keep_ef : R.layout.act_book_keep);
		UIHelper.setTranStateBar(activity);
		initUI();
		initListener();
		initData();
	}

	@Override
	protected void initUI() {
		listView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		adapter = new BookKeepAdapter();
		listView.setAdapter(adapter);
		listView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				getData(true);
			}
		});
	}

	@Override
	protected void initListener() {
		findViewById(R.id.chapter_list_back).setOnClickListener(this);
		findViewById(R.id.book_keep_add).setOnClickListener(this);
		findViewById(R.id.book_keep_time).setOnClickListener(this);
	}

	@Override
	protected void initData() {
		listView.setRefreshing();
	}

	private void getData(boolean update) {
		keepBookList = chatchManager.findBookInKeep();
		if (keepBookList != null && keepBookList.size() > 0) {
			if (update) {
				chatchManager.updateAllBookOnline(keepBookList, new DefaultInterface() {

					@Override
					public void getDefault(boolean haBack) {
						if (haBack)
							mHandler.sendEmptyMessage(0);
						else
							listView.onRefreshComplete();
					}
				});
			} else {
				mHandler.sendEmptyMessage(0);
			}
		} else {
			mHandler.sendEmptyMessage(0);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.chapter_list_back:
			finishInAnim();
			break;
		case R.id.book_keep_add:
			KeepAddActivity.luanch(activity);
			break;
		case R.id.book_keep_time:
			showChooseKeepTime();
			break;
		case R.id.timer_stop_layout1:
			chooseStopTime(3);
			break;
		case R.id.timer_stop_layout2:
			chooseStopTime(5);
			break;
		case R.id.timer_stop_layout3:
			chooseStopTime(10);
			break;
		case R.id.timer_stop_layout4:
			chooseStopTime(20);
			break;
		case R.id.timer_stop_layout5:
			chooseStopTime(30);
			break;
		}
	}

	private void showChooseKeepTime() {
		if (timeDialog == null) {
			timeDialog = new Dialog(activity, R.style.CustomDialog);
			View contentView = LayoutInflater.from(activity)
					.inflate(
							Cookies.getReadSetting().isNightMode() ? R.layout.view_keep_time_ef
									: R.layout.view_keep_time, null);
			View timeView1 = contentView.findViewById(R.id.timer_stop_layout1);
			View timeView2 = contentView.findViewById(R.id.timer_stop_layout2);
			View timeView3 = contentView.findViewById(R.id.timer_stop_layout3);
			View timeView4 = contentView.findViewById(R.id.timer_stop_layout4);
			View timeView5 = contentView.findViewById(R.id.timer_stop_layout5);
			timeView.add(timeView1);
			timeView.add(timeView2);
			timeView.add(timeView3);
			timeView.add(timeView4);
			timeView.add(timeView5);
			timeView1.setOnClickListener(this);
			timeView2.setOnClickListener(this);
			timeView3.setOnClickListener(this);
			timeView4.setOnClickListener(this);
			timeView5.setOnClickListener(this);
			if (Cookies.getUserSetting().getBookKeepTime() == 3) {
				setCheckTime(R.id.timer_stop_layout1);
			} else if (Cookies.getUserSetting().getBookKeepTime() == 5) {
				setCheckTime(R.id.timer_stop_layout2);
			} else if (Cookies.getUserSetting().getBookKeepTime() == 10) {
				setCheckTime(R.id.timer_stop_layout3);
			} else if (Cookies.getUserSetting().getBookKeepTime() == 20) {
				setCheckTime(R.id.timer_stop_layout4);
			} else if (Cookies.getUserSetting().getBookKeepTime() == 30) {
				setCheckTime(R.id.timer_stop_layout5);
			}
			timeDialog.setContentView(contentView);
			timeDialog.show();
		} else {
			if (Cookies.getUserSetting().getBookKeepTime() == 3) {
				setCheckTime(R.id.timer_stop_layout1);
			} else if (Cookies.getUserSetting().getBookKeepTime() == 5) {
				setCheckTime(R.id.timer_stop_layout2);
			} else if (Cookies.getUserSetting().getBookKeepTime() == 10) {
				setCheckTime(R.id.timer_stop_layout3);
			} else if (Cookies.getUserSetting().getBookKeepTime() == 20) {
				setCheckTime(R.id.timer_stop_layout4);
			} else if (Cookies.getUserSetting().getBookKeepTime() == 30) {
				setCheckTime(R.id.timer_stop_layout5);
			}
			timeDialog.show();
		}
	}

	public void chooseStopTime(int time) {
		if (Cookies.getUserSetting().getBookKeepTime() != time) {
			timeDialog.dismiss();
			Cookies.getUserSetting().setBookKeepTime(time);
			if (keepBookList != null && keepBookList.size() > 0) {
				chatchManager.updateBookKeepTime(keepBookList, time);
				getData(false);
			}
		}
	}

	private void setCheckTime(int viewId) {
		for (View view : timeView) {
			view.findViewById(R.id.timer_stop_check).setVisibility(view.getId() == viewId ? View.VISIBLE : View.GONE);
		}
	}

	private class BookKeepAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return keepBookList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHoldr holder = null;
			if (convertView == null) {
				holder = new ViewHoldr();
				convertView = LayoutInflater.from(activity).inflate(
						Cookies.getReadSetting().isNightMode() ? R.layout.item_book_keep_ef : R.layout.item_book_keep,
						null);
				holder.ivBookIcon = (ImageView) convertView.findViewById(R.id.item_book_icon);
				holder.tvKeepName = (TextView) convertView.findViewById(R.id.item_book_name);
				holder.tvKeepDay = (TextView) convertView.findViewById(R.id.item_book_day);
				holder.btnRemove = (Button) convertView.findViewById(R.id.item_book_remove);
				holder.tvBookChapter = (TextView) convertView.findViewById(R.id.item_book_chapter);
				holder.tvChapterTime = (TextView) convertView.findViewById(R.id.item_book_chapter_time);
				convertView.setTag(holder);
			} else {
				holder = (ViewHoldr) convertView.getTag();
			}
			final BookDesc desc = keepBookList.get(position);
			ImageLoadUtil.loadImage(holder.ivBookIcon, desc.getImageUrl(), ImageType.BOOK);
			holder.tvKeepName
					.setTextColor(System.currentTimeMillis() > desc.getKeepExpireTime() ? getResources().getColor(
							R.color.bg_shelf_download) : getResources().getColor(
							Cookies.getReadSetting().isNightMode() ? R.color.text_ef_light_gray
									: R.color.book_shelf_item_name));
			holder.tvKeepName.setText(desc.getBookName());
			holder.tvChapterTime.setText(TimeUtil.fmttoCN(desc.getLastUpdateTime()) + "更新：");
			holder.tvBookChapter.setText(desc.getLastChapterTitle());
			holder.tvKeepDay.setText("养肥了"
					+ TimeUtil.getDayBySecond(System.currentTimeMillis() - desc.getKeepPutTime()));
			holder.btnRemove.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					chatchManager.moveBookToStore(desc);
					keepBookList.remove(desc);
					notifyDataSetChanged();
				}
			});
			return convertView;
		}
	}

	private class ViewHoldr {
		ImageView ivBookIcon;
		TextView tvKeepName;
		TextView tvKeepDay;
		TextView tvBookChapter;
		TextView tvChapterTime;
		Button btnRemove;
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		getData(false);
	}

}