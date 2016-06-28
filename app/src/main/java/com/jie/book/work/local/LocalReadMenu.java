package com.jie.book.work.local;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.jie.book.work.R;
import com.jie.book.work.activity.CustomThemeActivity;
import com.jie.book.work.activity.TypefaceActivity;
import com.jie.book.work.application.BookApplication;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.download.DownloadManager;
import com.jie.book.work.read.ReadTheme;
import com.jie.book.work.utils.MiscUtils;
import com.jie.book.work.utils.StatisticUtil;
import com.jie.book.work.utils.StringUtil;
import com.jie.book.work.utils.UIHelper;

@SuppressLint("HandlerLeak")
public class LocalReadMenu implements View.OnClickListener, OnTouchListener, OnSeekBarChangeListener {
	private static final int MSG_TIME_RUN = 1001;
	private View readMenu, readMenuEmpty, readMenuText, readMenuSet, readMenuProgress;
	private View readMenuTop, readMenuIndex, readMenuAuto, autoBtnLayout;
	private ImageView ivTime1, ivTime2, ivTime3, ivTime4, ivTime5;
	private TextView tvTime1, tvTime2, tvTime3, tvTime4, tvTime5, tvTimeIndex;
	private Button btnNightSwitch, btnOrientationSwitch;
	private Button btnAutoStar, btnAutoStop;
	private List<ImageView> themeImageView;
	private List<ImageView> timeImageView;
	private List<TextView> timeTextView;
	private ImageView ivNightTab;
	private TextView tvNightTab;
	private TextView tvBookMark;
	private SeekBar brightSeekBar, progrssSeekBar;
	private LocalReaderActivity readActivity;
	private Timer timer;
	private ReadMode mode = ReadMode.STOP;
	private int mStartSeekbarOffset;
	private Runnable SLIDE_OUT_BOOKMARK_PROMPT_RUNNABLE;

	private enum ReadMode {
		STAR, PAUSE, STOP
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_TIME_RUN:
				readActivity.getReadBorad().turnNextPageAndRedraw();
				break;
			}
		}
	};

	public LocalReadMenu(Activity paramActivity) {
		readActivity = ((LocalReaderActivity) paramActivity);
		initUI();
		intReadSetting();
		initListner();
		initBookmark();
	}

	private void initUI() {
		btnAutoStar = (Button) readActivity.findViewById(R.id.read_menu_star);
		btnAutoStop = (Button) readActivity.findViewById(R.id.read_menu_pause);
		readMenuIndex = readActivity.findViewById(R.id.layout_read_menu_bottom_index);
		readMenuAuto = readActivity.findViewById(R.id.layout_read_menu_auto);
		readMenuTop = readActivity.findViewById(R.id.layout_read_menu_top);
		autoBtnLayout = readActivity.findViewById(R.id.read_menu_auto_btn_layout);
		readMenu = readActivity.findViewById(R.id.layout_read_menu);
		readMenuEmpty = readActivity.findViewById(R.id.read_menu_empty);
		readMenuText = readActivity.findViewById(R.id.read_menu_text);
		readMenuSet = readActivity.findViewById(R.id.read_menu_set);
		readMenuProgress = readActivity.findViewById(R.id.read_menu_progress);
		brightSeekBar = (SeekBar) readActivity.findViewById(R.id.subMenuProgressSeekbar);
		progrssSeekBar = (SeekBar) readActivity.findViewById(R.id.localProgressSeekbar);
		ivNightTab = (ImageView) readActivity.findViewById(R.id.read_menu_night_iv);
		tvNightTab = (TextView) readActivity.findViewById(R.id.read_menu_night_tv);
		btnNightSwitch = (Button) readActivity.findViewById(R.id.read_menu_night_switch);
		btnOrientationSwitch = (Button) readActivity.findViewById(R.id.read_menu_orientation_switch);
		ivTime1 = (ImageView) readActivity.findViewById(R.id.read_menu_auto_image1);
		ivTime2 = (ImageView) readActivity.findViewById(R.id.read_menu_auto_image2);
		ivTime3 = (ImageView) readActivity.findViewById(R.id.read_menu_auto_image3);
		ivTime4 = (ImageView) readActivity.findViewById(R.id.read_menu_auto_image4);
		ivTime5 = (ImageView) readActivity.findViewById(R.id.read_menu_auto_image5);
		tvTime1 = (TextView) readActivity.findViewById(R.id.read_menu_auto_text1);
		tvTime2 = (TextView) readActivity.findViewById(R.id.read_menu_auto_text2);
		tvTime3 = (TextView) readActivity.findViewById(R.id.read_menu_auto_text3);
		tvTime4 = (TextView) readActivity.findViewById(R.id.read_menu_auto_text4);
		tvTime5 = (TextView) readActivity.findViewById(R.id.read_menu_auto_text5);
		tvTimeIndex = (TextView) readActivity.findViewById(R.id.read_menu_auto_time_index);
		timeImageView = new ArrayList<ImageView>();
		timeImageView.add(ivTime1);
		timeImageView.add(ivTime2);
		timeImageView.add(ivTime3);
		timeImageView.add(ivTime4);
		timeImageView.add(ivTime5);
		timeTextView = new ArrayList<TextView>();
		timeTextView.add(tvTime1);
		timeTextView.add(tvTime2);
		timeTextView.add(tvTime3);
		timeTextView.add(tvTime4);
		timeTextView.add(tvTime5);
		ImageView ivThemeWite = (ImageView) readActivity.findViewById(R.id.read_menu_theme_wite_iv);
		ImageView ivThemeSheep = (ImageView) readActivity.findViewById(R.id.read_menu_theme_sheep_iv);
		ImageView ivThemeGreen = (ImageView) readActivity.findViewById(R.id.read_menu_theme_green_iv);
		ImageView ivThemeBule = (ImageView) readActivity.findViewById(R.id.read_menu_theme_bule_iv);
		ImageView ivThemePink = (ImageView) readActivity.findViewById(R.id.read_menu_theme_pink_iv);
		themeImageView = new ArrayList<ImageView>();
		themeImageView.add(ivThemeWite);
		themeImageView.add(ivThemePink);
		themeImageView.add(ivThemeSheep);
		themeImageView.add(ivThemeGreen);
		themeImageView.add(ivThemeBule);
		LayoutParams param = (LayoutParams) readMenuTop.getLayoutParams();
		if (Cookies.getUserSetting().isReadScreenFull()) {
			int topMagin = UIHelper.getStatusHeight(readActivity);
			param.topMargin = topMagin;
		} else {
			param.topMargin = 0;
		}
		readMenuTop.requestLayout();
		if (Cookies.getReadSetting().getAutoTime() == 5000) {
			setAutoTime(5000, R.id.read_menu_auto_image1, R.id.read_menu_auto_text1);
		} else if (Cookies.getReadSetting().getAutoTime() == 10000) {
			setAutoTime(10000, R.id.read_menu_auto_image2, R.id.read_menu_auto_text2);
		} else if (Cookies.getReadSetting().getAutoTime() == 15000) {
			setAutoTime(15000, R.id.read_menu_auto_image3, R.id.read_menu_auto_text3);
		} else if (Cookies.getReadSetting().getAutoTime() == 20000) {
			setAutoTime(20000, R.id.read_menu_auto_image4, R.id.read_menu_auto_text4);
		} else if (Cookies.getReadSetting().getAutoTime() == 30000) {
			setAutoTime(30000, R.id.read_menu_auto_image5, R.id.read_menu_auto_text5);
		}
		tvTimeIndex.setText("当前翻页速度  " + Cookies.getReadSetting().getAutoTime() / 1000 + "秒");
	}

	private void initListner() {
		btnAutoStar.setOnClickListener(this);
		btnAutoStop.setOnClickListener(this);
		ivTime1.setOnClickListener(this);
		ivTime2.setOnClickListener(this);
		ivTime3.setOnClickListener(this);
		ivTime4.setOnClickListener(this);
		ivTime5.setOnClickListener(this);
		readMenuEmpty.setOnTouchListener(this);
		btnNightSwitch.setOnClickListener(this);
		btnOrientationSwitch.setOnClickListener(this);
		readActivity.findViewById(R.id.readmenu_mark).setOnClickListener(this);
		readActivity.findViewById(R.id.readmenu_back).setOnClickListener(this);
		readActivity.findViewById(R.id.read_menu_size_up).setOnClickListener(this);
		readActivity.findViewById(R.id.read_menu_size_down).setOnClickListener(this);
		readActivity.findViewById(R.id.rea_menu_tab_night).setOnClickListener(this);
		readActivity.findViewById(R.id.rea_menu_tab_text).setOnClickListener(this);
		readActivity.findViewById(R.id.rea_menu_tab_chapter).setOnClickListener(this);
		readActivity.findViewById(R.id.rea_menu_tab_progress).setOnClickListener(this);
		readActivity.findViewById(R.id.rea_menu_tab_set).setOnClickListener(this);
		readActivity.findViewById(R.id.read_menu_theme_wite).setOnClickListener(this);
		readActivity.findViewById(R.id.read_menu_theme_sheep).setOnClickListener(this);
		readActivity.findViewById(R.id.read_menu_theme_green).setOnClickListener(this);
		readActivity.findViewById(R.id.read_menu_theme_blue).setOnClickListener(this);
		readActivity.findViewById(R.id.read_menu_theme_pink).setOnClickListener(this);
		readActivity.findViewById(R.id.read_menu_typeface).setOnClickListener(this);
		readActivity.findViewById(R.id.read_menu_space_add).setOnClickListener(this);
		readActivity.findViewById(R.id.read_menu_space_dele).setOnClickListener(this);
		brightSeekBar.setEnabled(!Cookies.getReadSetting().isLockScreenBright());
		brightSeekBar.setOnSeekBarChangeListener(this);
		progrssSeekBar.setOnSeekBarChangeListener(this);
	}

	public void setBookName() {
		TextView tvBookName = (TextView) readActivity.findViewById(R.id.readmenu_bookname);
		if (readActivity.getBookCase() != null && !StringUtil.isEmpty(readActivity.getBookCase().getBookName()))
			tvBookName.setText(readActivity.getBookCase().getBookName());
	}

	private void intReadSetting() {
		if (!UIHelper.isAutoBrightness(readActivity)) {
			setBrightness(Cookies.getReadSetting().getBrightness(), false);
		} else {
			if (BookApplication.getInstance().lunchBrightness != Cookies.getReadSetting().getBrightness()) {
				setBrightness(Cookies.getReadSetting().getBrightness(), false);
			}
		}
		setTypeface(Cookies.getUserSetting().getUserTypeface(), true);
		if (Cookies.getReadSetting().isNightMode())
			toggoleNightMode(true);
		else
			setTheme(Cookies.getReadSetting().getThemeIndex());
		btnNightSwitch
				.setBackgroundResource(Cookies.getReadSetting().isLockScreenBright() == true ? R.drawable.btn_read_menu_open
						: R.drawable.btn_read_menu_close);
		btnOrientationSwitch
				.setBackgroundResource(Cookies.getReadSetting().getScreenOrientaion() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT ? R.drawable.btn_read_menu_close
						: R.drawable.btn_read_menu_open);
		readActivity.setRequestedOrientation(Cookies.getReadSetting().getScreenOrientaion());
	}

	public boolean isVisible() {
		return (this.readMenu.getVisibility() == View.VISIBLE);
	}

	private boolean changeProgress = false;

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.readmenu_back:
			readActivity.finishInAnim();
			break;
		case R.id.read_menu_size_up:
			StatisticUtil.sendEvent(readActivity, StatisticUtil.TEXT_SIZE);
			readActivity.getReadBorad().adjustTextSize(1);
			break;
		case R.id.read_menu_size_down:
			StatisticUtil.sendEvent(readActivity, StatisticUtil.TEXT_SIZE);
			readActivity.getReadBorad().adjustTextSize(-1);
			break;
		case R.id.rea_menu_tab_night:
			StatisticUtil.sendEvent(readActivity, StatisticUtil.LIGHT_MODE);
			readMenuText.setVisibility(View.GONE);
			readMenuSet.setVisibility(View.GONE);
			boolean isNightMode = Cookies.getReadSetting().isNightMode();
			toggoleNightMode(!isNightMode);
			readActivity.setNightModel();
			break;
		case R.id.rea_menu_tab_text:
			if (readMenuText.getVisibility() == View.VISIBLE) {
				readMenuText.setVisibility(View.GONE);
				readMenuSet.setVisibility(View.GONE);
				autoBtnLayout.setVisibility(View.VISIBLE);
				readMenuProgress.setVisibility(View.GONE);
			} else {
				readMenuText.setVisibility(View.VISIBLE);
				readMenuSet.setVisibility(View.GONE);
				autoBtnLayout.setVisibility(View.GONE);
				readMenuProgress.setVisibility(View.GONE);
			}
			break;
		case R.id.rea_menu_tab_progress:
			if (readMenuProgress.getVisibility() == View.VISIBLE) {
				readMenuProgress.setVisibility(View.GONE);
				readMenuText.setVisibility(View.GONE);
				readMenuSet.setVisibility(View.GONE);
				autoBtnLayout.setVisibility(View.VISIBLE);
			} else {
				if (readActivity.getBookCase() != null)
					progrssSeekBar.setProgress(readActivity.getBookCase().getProgress());
				readMenuProgress.setVisibility(View.VISIBLE);
				readMenuText.setVisibility(View.GONE);
				readMenuSet.setVisibility(View.GONE);
				autoBtnLayout.setVisibility(View.GONE);
			}
			break;
		case R.id.rea_menu_tab_chapter:
			if (Cookies.getUserSetting().isReadScreenFull())
				MiscUtils.setSceenFull(true, readActivity);
			readMenu.setVisibility(View.GONE);
			readMenuText.setVisibility(View.GONE);
			readMenuSet.setVisibility(View.GONE);
			if (readActivity.getBookCase() != null) {
				readActivity.getReadChapter().getChapterList();
				readActivity.toggoleChapterList();
			}
			break;
		case R.id.rea_menu_tab_set:
			if (readMenuSet.getVisibility() == View.VISIBLE) {
				readMenuText.setVisibility(View.GONE);
				readMenuSet.setVisibility(View.GONE);
				readMenuProgress.setVisibility(View.GONE);
				autoBtnLayout.setVisibility(View.VISIBLE);
			} else {
				if (UIHelper.isAutoBrightness(readActivity))
					changeProgress = false;
				brightSeekBar.setProgress((int) (Cookies.getReadSetting().getBrightness() * 100));
				readMenuText.setVisibility(View.GONE);
				readMenuSet.setVisibility(View.VISIBLE);
				autoBtnLayout.setVisibility(View.GONE);
				readMenuProgress.setVisibility(View.GONE);
			}
			break;
		case R.id.read_menu_theme_wite:
			StatisticUtil.sendEvent(readActivity, StatisticUtil.BG_WHITE);
			swithTheme(0);
			break;
		case R.id.read_menu_theme_sheep:
			StatisticUtil.sendEvent(readActivity, StatisticUtil.BG_SHEP);
			swithTheme(2);
			break;
		case R.id.read_menu_theme_blue:
			StatisticUtil.sendEvent(readActivity, StatisticUtil.BG_BLUE);
			if (Cookies.getReadSetting().isNightMode()) {
				readActivity.showToast("亲，夜间模式下不能切换主题哦！");
			} else {
				CustomThemeActivity.luanch(readActivity);
			}
			break;
		case R.id.read_menu_theme_green:
			StatisticUtil.sendEvent(readActivity, StatisticUtil.BG_GREEN);
			swithTheme(3);
			break;
		case R.id.read_menu_theme_pink:
			StatisticUtil.sendEvent(readActivity, StatisticUtil.BG_PINK);
			swithTheme(1);
			break;
		case R.id.read_menu_night_switch:
			StatisticUtil.sendEvent(readActivity, StatisticUtil.READ_LOCK);
			toggoleBrightness();
			break;
		case R.id.read_menu_orientation_switch:
			StatisticUtil.sendEvent(readActivity, StatisticUtil.READ_SCREEN);
			toggoleOrientation();
			break;
		case R.id.read_menu_typeface:
			StatisticUtil.sendEvent(readActivity, StatisticUtil.TEXT_TYPEFACE);
			TypefaceActivity.luanch(readActivity);
			toggoleMenu();
			break;
		case R.id.readmenu_mark:
			addBookmark();
			break;
		case R.id.read_menu_space_dele:
			StatisticUtil.sendEvent(readActivity, StatisticUtil.TEXT_SPACE_DELE);
			setTextSpace(false);
			break;
		case R.id.read_menu_space_add:
			StatisticUtil.sendEvent(readActivity, StatisticUtil.TEXT_SPACE_ADD);
			setTextSpace(true);
			break;
		case R.id.read_menu_auto_image1:
			setAutoTime(5000, view.getId(), R.id.read_menu_auto_text1);
			break;
		case R.id.read_menu_auto_image2:
			setAutoTime(10000, view.getId(), R.id.read_menu_auto_text2);
			break;
		case R.id.read_menu_auto_image3:
			setAutoTime(15000, view.getId(), R.id.read_menu_auto_text3);
			break;
		case R.id.read_menu_auto_image4:
			setAutoTime(20000, view.getId(), R.id.read_menu_auto_text4);
			break;
		case R.id.read_menu_auto_image5:
			setAutoTime(30000, view.getId(), R.id.read_menu_auto_text5);
			break;
		case R.id.read_menu_star:
			starAutoRead();
			toggoleMenu();
			break;
		case R.id.read_menu_pause:
			stopAutoRead();
			toggoleMenu();
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.read_menu_empty:
			toggoleMenu();
			break;
		}
		return false;
	}

	private void setAutoTime(int time, int ivTimeId, int tvTimeId) {
		Cookies.getReadSetting().setAutoTime(time);
		tvTimeIndex.setText("当前翻页速度  " + Cookies.getReadSetting().getAutoTime() / 1000 + "秒");
		for (ImageView imageview : timeImageView)
			imageview.setImageResource(imageview.getId() == ivTimeId ? R.drawable.icon_auto_time_choosed
					: R.drawable.icon_auto_time_unchoose);
		for (TextView textview : timeTextView)
			textview.setBackgroundResource(textview.getId() == tvTimeId ? R.drawable.bg_auto_time
					: R.drawable.bg_auto_time_normal);
	}

	// 开始自动阅读
	public void starAutoRead() {
		mode = ReadMode.STAR;
		readActivity.showToast("自动阅读开启");
		readMenuIndex.setVisibility(View.GONE);
		readMenuAuto.setVisibility(View.VISIBLE);
		btnAutoStar.setVisibility(View.GONE);
		btnAutoStop.setVisibility(View.VISIBLE);
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				Message msg = new Message();
				msg.what = MSG_TIME_RUN;
				mHandler.sendMessage(msg);
			}
		}, Cookies.getReadSetting().getAutoTime(), Cookies.getReadSetting().getAutoTime());
	}

	// 关闭自动阅读
	public void stopAutoRead() {
		if (mode == ReadMode.STAR || mode == ReadMode.PAUSE && timer != null) {
			mode = ReadMode.STOP;
			readActivity.showToast("自动阅读关闭");
			timer.cancel();
			readMenuIndex.setVisibility(View.VISIBLE);
			readMenuAuto.setVisibility(View.GONE);
			btnAutoStar.setVisibility(View.VISIBLE);
			btnAutoStop.setVisibility(View.GONE);
		}
	}

	// 暂停自动阅读
	public void pauseAutoRead() {
		if (mode == ReadMode.STAR && timer != null) {
			mode = ReadMode.PAUSE;
			timer.cancel();
			// readActivity.showToast("自动阅读暂停");
		}
	}

	// 锁定亮度开关
	public void toggoleBrightness() {
		if (Cookies.getReadSetting().isLockScreenBright()) {
			Cookies.getReadSetting().setLockScreenBright(false);
			btnNightSwitch.setBackgroundResource(R.drawable.btn_read_menu_close);
		} else {
			Cookies.getReadSetting().setLockScreenBright(true);
			btnNightSwitch.setBackgroundResource(R.drawable.btn_read_menu_open);
		}
		brightSeekBar.setEnabled(!Cookies.getReadSetting().isLockScreenBright());
	}

	// 设置行距
	public void setTextSpace(boolean isAdd) {
		int currentSpace = Cookies.getReadSetting().getLineSpacing();
		int afterSpace = isAdd ? currentSpace + 2 : currentSpace - 2;
		if (afterSpace > 50) {
			afterSpace = 50;
		}
		if (afterSpace < 10) {
			afterSpace = 10;
		}
		Cookies.getReadSetting().setLineSpacing(afterSpace);
		readActivity.getReadBorad().reSetLineSpace();
	}

	// 切换横竖屏
	public void toggoleOrientation() {
		toggoleMenu();
		readActivity
				.setRequestedOrientation(Cookies.getReadSetting().getScreenOrientaion() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
						: ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		Cookies.getReadSetting().setScreenOrientaion(readActivity.getRequestedOrientation());
		btnOrientationSwitch
				.setBackgroundResource(Cookies.getReadSetting().getScreenOrientaion() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT ? R.drawable.btn_read_menu_close
						: R.drawable.btn_read_menu_open);
	}

	// 显示或者隐藏菜单
	public void toggoleMenu() {
		if (readMenu.getVisibility() == View.VISIBLE) {
			if (Cookies.getUserSetting().isReadScreenFull())
				MiscUtils.setSceenFull(true, readActivity);
			readMenu.setVisibility(View.GONE);
			readMenuSet.setVisibility(View.GONE);
			readMenuText.setVisibility(View.GONE);
			readMenuProgress.setVisibility(View.GONE);
			if (mode == ReadMode.PAUSE)
				starAutoRead();
		} else {
			if (Cookies.getUserSetting().isReadScreenFull())
				MiscUtils.setSceenFull(false, readActivity);
			autoBtnLayout.setVisibility(View.VISIBLE);
			readMenu.setVisibility(View.VISIBLE);
			if (mode == ReadMode.STAR)
				pauseAutoRead();
		}
	}

	// 切换夜间模式
	@SuppressWarnings("deprecation")
	public void toggoleNightMode(boolean isNightMode) {
		Cookies.getReadSetting().setNightMode(isNightMode);
		if (!isNightMode) {
			readActivity.findViewById(R.id.adLayout_shade).setVisibility(View.GONE);
			ivNightTab.setBackgroundResource(R.drawable.icon_read_menu_night);
			tvNightTab.setText("夜间");
			setTheme(Cookies.getReadSetting().getThemeIndex());
		} else {
			readActivity.findViewById(R.id.adLayout_shade).setVisibility(
					readActivity.isShowAd() ? View.VISIBLE : View.GONE);
			ivNightTab.setBackgroundResource(R.drawable.icon_read_menu_day);
			tvNightTab.setText("正常");
			ReadTheme theme = readActivity.getNightTheme();
			readActivity.getReadChapter().setTheme(theme);
			readActivity.getReadPanle().setThemeColor(theme);
			readActivity.getReadBorad().setColorScheme(theme, true);
			for (int i = 0; i < themeImageView.size(); i++) {
				themeImageView.get(i).setBackgroundDrawable(null);
			}
		}

	}

	// 切换主题，是否是初始化
	public void flingTheme() {
		if (Cookies.getReadSetting().isNightMode()) {
			readActivity.showToast("亲，夜间模式下不能切换主题哦！");
			return;
		}
		int prothemeIndex = 1 + Cookies.getReadSetting().getThemeIndex();
		int themeIndex = prothemeIndex > readActivity.getReadThemeList().size() - 1 ? 0 : prothemeIndex;
		setTheme(themeIndex);
		Cookies.getReadSetting().setThemeIndex(themeIndex);
	}

	// 设置主题
	public void swithTheme(int themeIndex) {
		if (Cookies.getReadSetting().isNightMode()) {
			readActivity.showToast("亲，夜间模式下不能切换主题哦！");
			return;
		}
		if (themeIndex == 4) {
			setTheme(themeIndex);
			Cookies.getReadSetting().setThemeIndex(themeIndex);
		} else {
			if (themeIndex != Cookies.getReadSetting().getThemeIndex()) {
				setTheme(themeIndex);
				Cookies.getReadSetting().setThemeIndex(themeIndex);
			}
		}

	}

	@SuppressWarnings("deprecation")
	private void setTheme(int themeIndex) {
		ReadTheme theme = readActivity.getReadThemeList().get(themeIndex);
		readActivity.getReadChapter().setTheme(theme);
		readActivity.getReadPanle().setThemeColor(theme);
		readActivity.getReadBorad().setColorScheme(theme, true);
		for (int i = 0; i < themeImageView.size(); i++) {
			if (i == themeIndex)
				themeImageView.get(i).setBackgroundResource(R.drawable.btn_read_menu_select);
			else
				themeImageView.get(i).setBackgroundDrawable(null);
		}
	}

	// 设置亮度
	public void setBrightness(float f, boolean accumulate) {
		WindowManager.LayoutParams lp = readActivity.getWindow().getAttributes();
		if (accumulate) {
			if (lp.screenBrightness + f > 1) {
				lp.screenBrightness = 1;
			} else if (lp.screenBrightness + f < 0.01) {
				lp.screenBrightness = 0.01f;
			} else {
				lp.screenBrightness += f;
			}
		} else {
			if (f < 0.01)
				lp.screenBrightness = 0.01f;
			else if (f > 1)
				lp.screenBrightness = 1f;
			else
				lp.screenBrightness = f;
		}
		readActivity.getWindow().setAttributes(lp);
		Cookies.getReadSetting().setBrightness(lp.screenBrightness);
	}

	// 设置字体
	public void setTypeface(String typeface, boolean init) {
		if (!init && typeface.equals(Cookies.getUserSetting().getUserTypeface())) {
			return;
		}
		if (typeface.equals(TypefaceActivity.TYPEPFACE_JT)) {// 简体
			Typeface tf = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
			readActivity.getReadPanle().setTypeface(tf);
		} else if (typeface.equals(TypefaceActivity.TYPEPFACE_FT)) {// 繁体
			Typeface tf = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL);
			readActivity.getReadPanle().setTypeface(tf);
		} else {// 其他字体
			File file = DownloadManager.getDownloadManager(readActivity).getFile(typeface + ".ttf");
			if (file.exists()) {
				Typeface tf = Typeface.createFromFile(DownloadManager.getDownloadManager(readActivity).getRootPath()
						+ typeface + ".ttf");
				if (tf != null) {
					readActivity.getReadPanle().setTypeface(tf);
				} else {
					Cookies.getUserSetting().setUserTypeface(TypefaceActivity.TYPEPFACE_JT);
					setTypeface(Cookies.getUserSetting().getUserTypeface(), true);
				}
			} else {
				Cookies.getUserSetting().setUserTypeface(TypefaceActivity.TYPEPFACE_JT);
				setTypeface(Cookies.getUserSetting().getUserTypeface(), true);
			}
		}
	}

	private void initBookmark() {
		tvBookMark = (TextView) readActivity.findViewById(R.id.bookmarkPrompt);
		SLIDE_OUT_BOOKMARK_PROMPT_RUNNABLE = new Runnable() {
			public void run() {
				readActivity.slideOutLeftAnimation(tvBookMark);
			}
		};
	}

	private void addBookmark() {
		String summary = readActivity.getReadBorad().addBookmark();
		if (summary != null) {
			tvBookMark.setText(summary);
			readActivity.slideInLeftAnimation(tvBookMark);
			tvBookMark.postDelayed(SLIDE_OUT_BOOKMARK_PROMPT_RUNNABLE, 1500);
		} else {
			readActivity.showToast("书签已存在，请查看目录书签!");
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (seekBar == brightSeekBar) {
			float f = progress / 100f;
			if (changeProgress)
				setBrightness(f, false);
			changeProgress = true;
		} else if (seekBar == progrssSeekBar) {
			String progressStr = StringUtil.NO_DECIMAL_POINT_DF.format((progress * 100f / seekBar.getMax())) + '%';
			readActivity.getReadPanle().setReadProgress(progressStr);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		if (seekBar == progrssSeekBar) {
			mStartSeekbarOffset = seekBar.getProgress();
		}
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		if (seekBar == progrssSeekBar) {
			if (mStartSeekbarOffset == seekBar.getProgress())
				return;
			readActivity.getReadBorad()
					.adjustReadingProgressByPrecent((float) seekBar.getProgress() / seekBar.getMax());
		}
	}

}