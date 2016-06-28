package com.jie.book.work.activity;

import java.util.LinkedList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jie.book.work.R;
import com.jie.book.work.application.BookApplication;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.read.MotionEventPointCollector;
import com.jie.book.work.read.MultiTouchPointCollector;
import com.jie.book.work.read.ReadTheme;
import com.jie.book.work.utils.BitmapUtil;
import com.jie.book.work.utils.MiscUtils;
import com.jie.book.work.utils.UIHelper;
import com.jie.book.work.view.CirclePageIndicator;
import com.jie.book.work.view.CirclePageIndicator.LastPageSlidingListener;
import com.jie.book.work.view.LimitViewPager;

public abstract class ReadRootActivity extends BaseActivity {
	private LinkedList<ReadTheme> readThemeList;
	private ReadTheme nightTheme;
	protected GestureDetector mGestureDetector;
	protected MotionEventPointCollector mPointCollector;
	protected SlidingMode mSlidingMode = SlidingMode.SLIDING_RESET;
	protected BatteryLifeBroadcastReceiver mBatteryLifeBroadcastReceiver;
	protected DragMode mDragMode = DragMode.DRAG_REST;
	protected RelativeLayout guideLyaout;
	protected LimitViewPager guideViewPager;
	protected CirclePageIndicator guideIndicator;
	protected Bitmap[] guideRes;
	protected long benginTime = 0;
	protected int scrrenWidth = 0;
	protected int scrrenheight = 0;
	protected int touchSlop;
	protected boolean isNight = false;

	protected enum SlidingMode {
		SLIDING_X, SLIDING_Y, SLIDING_RESET;
	}

	protected enum DragMode {
		DRAG_REST, DRAG_TO_CHAPTER, DRAG_TO_READ;
	}

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		if (Cookies.getUserSetting().isReadScreenFull()) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			MiscUtils.setSceenFull(true, activity);
			MiscUtils.goneSoft(activity);
		}
		if (Cookies.getUserSetting().isReadScreenLight())
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		initTheme();
		initAnimation();
		isNight = Cookies.getReadSetting().isNightMode();
		mPointCollector = new MultiTouchPointCollector();
		scrrenWidth = UIHelper.getScreenPixWidth(this);
		scrrenheight = UIHelper.getScreenPixHeight(this);
		benginTime = System.currentTimeMillis();
	}

	protected void onPause() {
		super.onPause();
		unregisterReceiver(mBatteryLifeBroadcastReceiver);
	}

	protected void onResume() {
		super.onResume();
		if (mBatteryLifeBroadcastReceiver == null)
			mBatteryLifeBroadcastReceiver = new BatteryLifeBroadcastReceiver();
		registerReceiver(mBatteryLifeBroadcastReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	}

	@Override
	protected void initUI() {
	}

	@Override
	protected void initData() {
	}

	@Override
	protected void initListener() {
	}

	protected abstract void receiverBettery(int precent);

	protected abstract void guidePageFinish();

	class BatteryLifeBroadcastReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
				int level = intent.getIntExtra("level", 0);
				int scale = intent.getIntExtra("scale", 100);
				int precent = (int) (((float) level / scale) * 100);
				receiverBettery(precent);
			}
		}
	}

	public LinkedList<ReadTheme> getReadThemeList() {
		return readThemeList;
	}

	public void addReadTheme() {
		if (Cookies.getReadSetting().getReadThemeBg() != 0 && readThemeList.size() == 4) {
			ReadTheme theme = new ReadTheme();
			readThemeList.add(theme);
		} else {
			readThemeList.removeLast();
			ReadTheme theme = new ReadTheme();
			readThemeList.add(theme);
		}
	}

	public ReadTheme getNightTheme() {
		return nightTheme;
	}

	public long getBenginTime() {
		return benginTime;
	}

	public void setBenginTime(long benginTime) {
		this.benginTime = benginTime;
	}

	// 初始化主题
	private void initTheme() {
		readThemeList = new LinkedList<ReadTheme>();

		// 时尚白
		ReadTheme theme1 = new ReadTheme(0, getResources().getColor(R.color.theme_read_text_wite), getResources()
				.getColor(R.color.theme_read_bg_wite), R.drawable.view_chapter_list_day_divider,
				R.drawable.icon_book_chapter_not_now, R.drawable.icon_book_chapter_arrow, R.drawable.bg_battery_black,
				R.drawable.icon_mark_indica_withe);

		// 靓之粉
		ReadTheme theme2 = new ReadTheme(1, getResources().getColor(R.color.theme_read_text_pink), getResources()
				.getColor(R.color.theme_read_bg_pink), R.drawable.view_chapter_list_day_divider,
				R.drawable.icon_chapter_list_round_pink, R.drawable.icon_book_chapter_arrow,
				R.drawable.bg_battery_black, R.drawable.icon_mark_indica_pink);

		// 羊皮纸
		ReadTheme theme3 = new ReadTheme(2, getResources().getColor(R.color.theme_read_text_sheep),
				R.drawable.theme_read_bg_sheep, R.drawable.view_chapter_list_sheep_divider,
				R.drawable.icon_chapter_list_round_sheep, R.drawable.icon_book_chapter_arrow,
				R.drawable.bg_battery_black, R.drawable.icon_mark_indica_sheep);
		// 护眼绿
		ReadTheme theme4 = new ReadTheme(3, getResources().getColor(R.color.theme_read_text_green), getResources()
				.getColor(R.color.theme_read_bg_green), R.drawable.view_chapter_list_day_divider,
				R.drawable.icon_chapter_list_round_green, R.drawable.icon_book_chapter_arrow,
				R.drawable.bg_battery_black, R.drawable.icon_mark_indica_green);

		readThemeList.add(theme1);
		readThemeList.add(theme2);
		readThemeList.add(theme3);
		readThemeList.add(theme4);

		// 自定义
		if (Cookies.getReadSetting().getReadThemeBg() != 0) {
			ReadTheme theme5 = new ReadTheme();
			readThemeList.add(theme5);
		}

		// 夜间模式
		nightTheme = new ReadTheme(5, getResources().getColor(R.color.theme_read_text_night), getResources().getColor(
				R.color.theme_read_bg_night), R.drawable.view_chapter_list_black_divider,
				R.drawable.icon_book_chapter_not_now, R.drawable.icon_book_chapter_light_arrow,
				R.drawable.bg_battery_gray, R.drawable.icon_mark_indica_night);

		if (readThemeList.size() < 5 && Cookies.getReadSetting().getThemeIndex() == 4) {
			Cookies.getReadSetting().setThemeIndex(2);
		}
	}

	protected Animation mSlideInLeftAnim;
	protected Animation mSlideOutLeftAnim;

	private void initAnimation() {
		mSlideInLeftAnim = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
		mSlideOutLeftAnim = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
	}

	public void slideInLeftAnimation(View view) {
		animate(view, mSlideInLeftAnim, View.VISIBLE);
	}

	public void slideOutLeftAnimation(View view) {
		animate(view, mSlideOutLeftAnim, View.INVISIBLE);
	}

	private void animate(View view, Animation anim, int visibility) {
		view.setVisibility(visibility);
		view.setAnimation(anim);
		anim.start();
	}

	protected void initGuide() {
		if (!Cookies.getReadSetting().isShowReadingGuide()) {
			return;
		}
		guideLyaout = (RelativeLayout) findViewById(R.id.guide_layout);
		guideViewPager = (LimitViewPager) findViewById(R.id.guide_viewpager);
		guideIndicator = (CirclePageIndicator) findViewById(R.id.guide_indicator);
		guideRes = new Bitmap[] { BitmapFactory.decodeResource(getResources(), R.drawable.reading_guide_0),
				BitmapFactory.decodeResource(getResources(), R.drawable.reading_guide_1),
				BitmapFactory.decodeResource(getResources(), R.drawable.reading_guide_2) };
		guideViewPager.setOffscreenPageLimit(guideRes.length);
		guideLyaout.setVisibility(View.VISIBLE);
		guideViewPager.setAdapter(new GuidePageAdapter());
		guideIndicator.setViewPager(guideViewPager);
		guideIndicator.setLastPageSlidingListener(new LastPageSlidingListener() {

			@Override
			public void onLastPageSlidding() {// 释放内存
				goneGuide();
			}
		});
	}

	protected void goneGuide() {
		Cookies.getReadSetting().setShowReadingGuide(false);
		guideLyaout.setVisibility(View.GONE);
		guideViewPager = null;
		for (Bitmap bitmap : guideRes) {
			BitmapUtil.recyledBitmap(bitmap);
		}

		guidePageFinish();

	}

	private class GuidePageAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return guideRes.length;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			View contentView = LayoutInflater.from(ReadRootActivity.this).inflate(R.layout.item_guide_page, null);
			ImageView itemView = (ImageView) contentView.findViewById(R.id.item_guide_image);
			itemView.setImageBitmap(guideRes[position]);
			container.addView(contentView);
			contentView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (position == 2)
						goneGuide();
				}
			});
			return contentView;
		}
	}

	public void setNightModel() {
		BookApplication.getInstance().swithNightModel = isNight != Cookies.getReadSetting().isNightMode();
	}

}