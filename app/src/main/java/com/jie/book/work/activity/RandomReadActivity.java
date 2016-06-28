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
import android.widget.TextView;

import com.bond.bookcatch.vo.BookDesc;
import com.bond.bookcatch.vo.SearchResult;
import com.jie.book.work.R;
import com.jie.book.work.application.BookActivityManager;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.read.BookCatchManager.BookDescInterface;
import com.jie.book.work.read.BookCatchManager.SearchResultInterface;
import com.jie.book.work.utils.ImageLoadUtil;
import com.jie.book.work.utils.ImageLoadUtil.ImageType;
import com.jie.book.work.utils.UIHelper;
import com.jie.book.work.view.filpView.FlipView;
import com.jie.book.work.view.filpView.OverFlipMode;

public class RandomReadActivity extends BaseActivity implements OnClickListener {
	private static final String LUANCH_TYPE = "luanch_type";
	private List<SearchResult> resultList = new ArrayList<SearchResult>();
	private List<BookDesc> bookDescList = new ArrayList<BookDesc>();
	private TextView tvBack;
	private FlipView mFlipView;
	private RandomAdapter adapter;
	private int position = -1;
	private long lastTime = 0;

	public static void luanch(Activity content, int type) {
		Intent intent = new Intent();
		intent.putExtra(LUANCH_TYPE, type);
		intent.setClass(content, RandomReadActivity.class);
		BookActivityManager.getInstance().goTo(content, intent);
	}

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(Cookies.getReadSetting().isNightMode() ? R.layout.act_random_read_ef : R.layout.act_random_read);
		UIHelper.setTranStateBar(activity);
		initUI();
		initListener();
		initData();
	}

	@Override
	protected void initUI() {
		tvBack = (TextView) findViewById(R.id.book_detail_back);
		mFlipView = (FlipView) findViewById(R.id.flip_view);
		adapter = new RandomAdapter();
		mFlipView.setAdapter(adapter);
		mFlipView.setTouchEnable(false);
		mFlipView.setOverFlipMode(OverFlipMode.GLOW);
		int type = getIntent().getIntExtra(LUANCH_TYPE, 0);
		tvBack.setText(type == 0 ? "推荐书籍" : "随机看书");
	}

	@Override
	protected void initData() {
		getData();
	}

	public void getData() {
		dialog = UIHelper.showProgressDialog(dialog, activity);
		chatchManager.getBaiduCommRank(1, new SearchResultInterface() {

			@Override
			public void getSearchResultList(List<SearchResult> searchResult) {
				if (searchResult != null && searchResult.size() > 0) {
					resultList.addAll(searchResult);
					showNextBook(false);
				} else {
					showToast("获取书籍失败");
					UIHelper.cancleProgressDialog(dialog);
				}
			}
		});
	}

	public void showNextBook(final boolean showProgress) {
		int nextPosition = position + 1;
		if (resultList != null && resultList.size() > 0 && nextPosition < resultList.size()) {
			position = nextPosition;
			if (showProgress)
				dialog = UIHelper.showProgressDialog(dialog, activity);
			chatchManager.getBookDescBySearchResult(resultList.get(position), new BookDescInterface() {

				@Override
				public void getBookDesc(BookDesc desc) {
					UIHelper.cancleProgressDialog(dialog);
					if (desc != null) {
						bookDescList.add(desc);
						adapter.notifyDataSetChanged();
						if (position > 0)
							mFlipView.smoothFlipTo(position);
					} else {
						showToast("获取书籍失败");
					}
				}
			});
		} else {
			getData();
		}
	}

	@Override
	protected void initListener() {
		tvBack.setOnClickListener(this);
		findViewById(R.id.book_random_read_try).setOnClickListener(this);
		findViewById(R.id.book_random_read_next).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.book_random_read_try:
			if (bookDescList != null && bookDescList.size() > 0) {
				BookDesc desc = bookDescList.get(position);
				chatchManager.refreshBookDesc(desc);
				ReadActivity.luanch(activity, desc, null);
			}
			break;
		case R.id.book_random_read_next:
			if (System.currentTimeMillis() - lastTime > 1000) {
				lastTime = System.currentTimeMillis();
				showNextBook(true);
			}
			break;
		case R.id.book_detail_back:
			finishInAnim();
			break;
		}
	}

	private class RandomAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return bookDescList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;

			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(activity).inflate(
						Cookies.getReadSetting().isNightMode() ? R.layout.item_random_read_ef
								: R.layout.item_random_read, parent, false);
				holder.tvBookDesc = (TextView) convertView.findViewById(R.id.item_random_read_desc);
				holder.tvBookStarus = (TextView) convertView.findViewById(R.id.item_random_read_satus);
				holder.tvBookName = (TextView) convertView.findViewById(R.id.item_random_read_name);
				holder.bookIocn = (ImageView) convertView.findViewById(R.id.item_random_read_icon);
				holder.starView = convertView.findViewById(R.id.item_random_read);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			BookDesc bookDesc = bookDescList.get(position);
			SearchResult result = resultList.get(position);
			setStar(holder.starView, result);
			UIHelper.setText(holder.tvBookStarus, bookDesc.getStatus());
			UIHelper.setText(holder.tvBookName, bookDesc.getBookName());
			UIHelper.setText(holder.tvBookDesc, bookDesc.getDesc(), "暂无详情");
			ImageLoadUtil.loadImage(holder.bookIocn, bookDesc.getImageUrl(), ImageType.BOOK);
			return convertView;
		}
	}

	private void setStar(View viewStar, SearchResult result) {
		int count = result.getReadingCount();
		ImageView image1 = (ImageView) viewStar.findViewById(R.id.item_random_read_star1);
		ImageView image2 = (ImageView) viewStar.findViewById(R.id.item_random_read_star2);
		ImageView image3 = (ImageView) viewStar.findViewById(R.id.item_random_read_star3);
		ImageView image4 = (ImageView) viewStar.findViewById(R.id.item_random_read_star4);
		ImageView image5 = (ImageView) viewStar.findViewById(R.id.item_random_read_star5);
		if (count < 2500) {
			image1.setImageResource(R.drawable.icon_random_star_read);
			image2.setImageResource(R.drawable.icon_random_star_gray);
			image3.setImageResource(R.drawable.icon_random_star_gray);
			image4.setImageResource(R.drawable.icon_random_star_gray);
			image5.setImageResource(R.drawable.icon_random_star_gray);
		} else if (count > 2500 && count < 3500) {
			image1.setImageResource(R.drawable.icon_random_star_read);
			image2.setImageResource(R.drawable.icon_random_star_read);
			image3.setImageResource(R.drawable.icon_random_star_gray);
			image4.setImageResource(R.drawable.icon_random_star_gray);
			image5.setImageResource(R.drawable.icon_random_star_gray);
		} else if (count > 3500 && count < 4500) {
			image1.setImageResource(R.drawable.icon_random_star_read);
			image2.setImageResource(R.drawable.icon_random_star_read);
			image3.setImageResource(R.drawable.icon_random_star_read);
			image4.setImageResource(R.drawable.icon_random_star_gray);
			image5.setImageResource(R.drawable.icon_random_star_gray);
		} else if (count > 4500 && count < 5500) {
			image1.setImageResource(R.drawable.icon_random_star_read);
			image2.setImageResource(R.drawable.icon_random_star_read);
			image3.setImageResource(R.drawable.icon_random_star_read);
			image4.setImageResource(R.drawable.icon_random_star_read);
			image5.setImageResource(R.drawable.icon_random_star_gray);
		} else {
			image1.setImageResource(R.drawable.icon_random_star_read);
			image2.setImageResource(R.drawable.icon_random_star_read);
			image3.setImageResource(R.drawable.icon_random_star_read);
			image4.setImageResource(R.drawable.icon_random_star_read);
			image5.setImageResource(R.drawable.icon_random_star_read);
		}

	}

	private class ViewHolder {
		TextView tvBookDesc;
		TextView tvBookStarus;
		TextView tvBookName;
		ImageView bookIocn;
		View starView;
	}

}