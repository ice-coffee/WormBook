package com.jie.book.work.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bond.bookcatch.vo.BookCatalog;
import com.bond.bookcatch.vo.BookDesc;
import com.jie.book.work.R;
import com.jie.book.work.application.BookActivityManager;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.read.BookCatchManager.DefaultInterface;
import com.jie.book.work.read.BookCatchManager.RangeInterface;
import com.jie.book.work.utils.StatisticUtil;
import com.jie.book.work.utils.UIHelper;
import com.jie.book.work.view.EmptyPage;
import com.jie.book.work.view.EmptyPage.onReLoadListener;

public class ChapterListActivity extends BaseActivity implements OnClickListener {
	private static String LUANCH_BOOK_DESC = "luanch_book_desc";
	private ListView listView;
	private EmptyPage emptyPage;
	private BookDesc bookDesc;
	private BookChapterListAdapter adapter;
	private List<BookCatalog> catalogList = new ArrayList<BookCatalog>();
	private int[] offlineRange = new int[] { 0, 0 };

	public static void luanch(Activity content, BookDesc bookDesc) {
		Intent intent = new Intent();
		intent.putExtra(LUANCH_BOOK_DESC, bookDesc);
		intent.setClass(content, ChapterListActivity.class);
		BookActivityManager.getInstance().goTo(content, intent);
	}

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(Cookies.getReadSetting().isNightMode() ? R.layout.act_chapter_list_ef
				: R.layout.act_chapter_list);
		UIHelper.setTranStateBar(activity);
		initUI();
		initListener();
		initData();
	}

	@Override
	protected void initUI() {
		bookDesc = (BookDesc) getIntent().getSerializableExtra(LUANCH_BOOK_DESC);
		chatchManager.refreshBookDesc(bookDesc);
		listView = (ListView) findViewById(R.id.pull_refresh_list);
		emptyPage = (EmptyPage) findViewById(R.id.empty_page);
		emptyPage.setTextView("暂无目录");
		adapter = new BookChapterListAdapter();
		listView.setAdapter(adapter);
		emptyPage.setOnReLoadListener(new onReLoadListener() {

			@Override
			public void onload() {
				getData(false);
			}
		});
		findViewById(R.id.chapter_list_top_btn).setOnClickListener(this);
		findViewById(R.id.chapter_list_buttom_btn).setOnClickListener(this);
	}

	@Override
	protected void initListener() {
		findViewById(R.id.chapter_list_back).setOnClickListener(this);
		findViewById(R.id.chapter_list_baidu_bar).setOnClickListener(this);
	}

	@Override
	protected void initData() {
		getData(true);
	}

	private void getData(boolean showLoading) {
		if (bookDesc != null) {
			activity.chatchManager.refreshBookDesc(bookDesc);
			getOnlineCatalog(showLoading);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (bookDesc != null)
			activity.chatchManager.refreshBookDesc(bookDesc);
	}

	protected void getOnlineCatalog(boolean showLoading) {
		if (bookDesc != null) {
			if (showLoading)
				dialog = UIHelper.showProgressDialog(dialog, activity);
			chatchManager.loadBookCatalog(bookDesc, new DefaultInterface() {

				@Override
				public void getDefault(boolean haBack) {
					if (bookDesc != null && bookDesc.getCatalogs() != null) {
						emptyPage.setVisibility(View.GONE);
						catalogList.clear();
						catalogList.addAll(bookDesc.getCatalogs());
						adapter.notifyDataSetChanged();
						BookCatalog lastBookCatalog = bookDesc.getLastReadCatalog();
						if (lastBookCatalog != null)
							listView.setSelection(lastBookCatalog.getIndex());
						activity.chatchManager.getOfflineCatalogRange(bookDesc, new RangeInterface() {

							@Override
							public void getRange(int[] range) {
								offlineRange = range;
								adapter.notifyDataSetChanged();
								UIHelper.cancleProgressDialog(dialog);
							}
						});
					} else {
						emptyPage.setVisibility(View.VISIBLE);
						emptyPage.onReloadComplete();
						UIHelper.cancleProgressDialog(dialog);
					}

				}
			});
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.chapter_list_back:
			finishInAnim();
			break;
		case R.id.chapter_list_baidu_bar:
			StatisticUtil.sendEvent(activity, StatisticUtil.BAIDU_BAR);
			if (bookDesc != null)
				WebActivity.launcher2(activity, bookDesc.getBookName());
			break;
		case R.id.chapter_list_top_btn:
			if (catalogList.size() > 0)
				listView.setSelection(0);
			break;
		case R.id.chapter_list_buttom_btn:
			if (catalogList.size() > 0)
				listView.setSelection(listView.getCount() - 1);
			break;
		}
	}

	private class BookChapterListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return catalogList.size();
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
						Cookies.getReadSetting().isNightMode() ? R.layout.item_chapter_list_ef
								: R.layout.item_chapter_list, null);
				holder.ivBookChapterIndex = (ImageView) convertView.findViewById(R.id.item_chapter_list_index);
				holder.ivBookChapterRound = (ImageView) convertView.findViewById(R.id.item_chapter_list_ruond);
				holder.tvBookChapterName = (TextView) convertView.findViewById(R.id.item_chapter_list_name);
				holder.tvBookChapterNum = (TextView) convertView.findViewById(R.id.item_chapter_list_num);
				convertView.setTag(holder);
			} else {
				holder = (ViewHoldr) convertView.getTag();
			}

			final BookCatalog catalog = catalogList.get(position);
			holder.tvBookChapterName.setText(catalog.getTitle());
			holder.tvBookChapterNum.setText(catalog.getIndex() + 1 + ".");

			if (offlineRange[1] != 0) {// 有缓存记录
				if (catalog.getIndex() >= offlineRange[0] && catalog.getIndex() < offlineRange[1]) {
					holder.ivBookChapterIndex.setVisibility(View.GONE);
					holder.ivBookChapterRound.setBackgroundResource(R.drawable.icon_chapter_list_round_cache);
					holder.tvBookChapterName.setTextColor(Cookies.getReadSetting().isNightMode() ? getResources()
							.getColor(R.color.text_ef_light_gray) : getResources().getColor(R.color.text_light_black));
					holder.tvBookChapterNum.setTextColor(Cookies.getReadSetting().isNightMode() ? getResources()
							.getColor(R.color.text_ef_light_gray) : getResources().getColor(R.color.text_light_black));
				} else {
					holder.ivBookChapterIndex.setVisibility(View.GONE);
					holder.ivBookChapterRound.setBackgroundResource(R.drawable.icon_book_chapter_not_now);
					holder.tvBookChapterName.setTextColor(Cookies.getReadSetting().isNightMode() ? getResources()
							.getColor(R.color.text_ef_light_gray) : getResources().getColor(R.color.text_light_black));
					holder.tvBookChapterNum.setTextColor(Cookies.getReadSetting().isNightMode() ? getResources()
							.getColor(R.color.text_ef_light_gray) : getResources().getColor(R.color.text_light_black));
				}
			} else {// 没有缓存记录
				holder.ivBookChapterIndex.setVisibility(View.GONE);
				holder.ivBookChapterRound.setBackgroundResource(R.drawable.icon_book_chapter_not_now);
				holder.tvBookChapterName.setTextColor(Cookies.getReadSetting().isNightMode() ? getResources().getColor(
						R.color.text_ef_light_gray) : getResources().getColor(R.color.text_light_black));
				holder.tvBookChapterNum.setTextColor(Cookies.getReadSetting().isNightMode() ? getResources().getColor(
						R.color.text_ef_light_gray) : getResources().getColor(R.color.text_light_black));
			}

			if (bookDesc.getLastReadCatalog() != null && bookDesc.getLastReadCatalog().getIndex() == catalog.getIndex()) {
				holder.ivBookChapterIndex.setVisibility(View.VISIBLE);
				holder.ivBookChapterRound.setBackgroundResource(R.drawable.icon_book_chapter_now);
				holder.tvBookChapterName.setTextColor(activity.getResources().getColor(R.color.book_default_red));
				holder.tvBookChapterNum.setTextColor(activity.getResources().getColor(R.color.book_default_red));
			}

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					bookDesc.setLastReadPosition(0);
					// bookDesc.setLastReadOffline(isCahce ? 1 : 0);
					ReadActivity.luanch(activity, bookDesc, catalog);
				}
			});
			return convertView;
		}
	}

	private class ViewHoldr {
		ImageView ivBookChapterIndex;
		ImageView ivBookChapterRound;
		TextView tvBookChapterName;
		TextView tvBookChapterNum;
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		bookDesc = chatchManager.reLoadBookDesc(bookDesc);
		getData(false);
	}

}