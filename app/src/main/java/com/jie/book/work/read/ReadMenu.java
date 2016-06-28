package com.jie.book.work.read;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.bond.bookcatch.vo.BookChapter;
import com.bond.bookcatch.vo.BookSource;
import com.jie.book.work.R;
import com.jie.book.work.activity.CustomThemeActivity;
import com.jie.book.work.activity.ReadActivity;
import com.jie.book.work.activity.TypefaceActivity;
import com.jie.book.work.activity.WebActivity;
import com.jie.book.work.application.BookApplication;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.download.DownloadManager;
import com.jie.book.work.entity.DownloadInfo;
import com.jie.book.work.read.BookCatchManager.BookSourceListInterface;
import com.jie.book.work.read.BookCatchManager.DefaultInterface;
import com.jie.book.work.utils.DownloadUtil;
import com.jie.book.work.utils.MiscUtils;
import com.jie.book.work.utils.StatisticUtil;
import com.jie.book.work.utils.StringUtil;
import com.jie.book.work.utils.UIHelper;
import com.jie.book.work.utils.UIHelper.OnDialogClickListener;

@SuppressLint("HandlerLeak")
public class ReadMenu implements OnClickListener, OnTouchListener, OnSeekBarChangeListener {
	private static final int MSG_TIME_RUN = 1001;
	private View readMenu, readMenuEmpty, readMenuText, readMenuSet, readDowload;
	private View readDownload, readUrl, readMore;
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
	private TextView tvDownloadName;
	private TextView tvDownloadPrecent;
	private TextView tvBookMark;
	private SeekBar seekBar;
	private ReadActivity readActivity;
	private Runnable SLIDE_OUT_BOOKMARK_PROMPT_RUNNABLE;
	private Timer timer;
	private ReadMode mode = ReadMode.STOP;

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

	public ReadMenu(Activity paramActivity) {
		readActivity = ((ReadActivity) paramActivity);
		initUI();
		intReadSetting();
		initListner();
		initBookmark();
	}

	private void initUI() {
		btnAutoStar = (Button) readActivity.findViewById(R.id.read_menu_star);
		btnAutoStop = (Button) readActivity.findViewById(R.id.read_menu_pause);
		readMore = readActivity.findViewById(R.id.readmenu_more_layout);
		readMenuIndex = readActivity.findViewById(R.id.layout_read_menu_bottom_index);
		readMenuAuto = readActivity.findViewById(R.id.layout_read_menu_auto);
		readMenuTop = readActivity.findViewById(R.id.layout_read_menu_top);
		autoBtnLayout = readActivity.findViewById(R.id.read_menu_auto_btn_layout);
		readMenu = readActivity.findViewById(R.id.layout_read_menu);
		readMenuEmpty = readActivity.findViewById(R.id.read_menu_empty);
		readMenuText = readActivity.findViewById(R.id.read_menu_text);
		readMenuSet = readActivity.findViewById(R.id.read_menu_set);
		readDownload = readActivity.findViewById(R.id.rea_menu_tab_download);
		readUrl = readActivity.findViewById(R.id.readmenu_chapter_url);
		readDowload = readActivity.findViewById(R.id.readmenu_chapter_downlod);
		tvDownloadName = (TextView) readActivity.findViewById(R.id.readmenu_download_name);
		tvDownloadPrecent = (TextView) readActivity.findViewById(R.id.readmenu_download_precent);
		seekBar = (SeekBar) readActivity.findViewById(R.id.subMenuProgressSeekbar);
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
		readUrl.setVisibility(View.GONE);
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
		readDownload.setOnClickListener(this);
		btnOrientationSwitch.setOnClickListener(this);
		readActivity.findViewById(R.id.readmenu_change_source).setOnClickListener(this);
		readActivity.findViewById(R.id.readmenu_mark).setOnClickListener(this);
		readActivity.findViewById(R.id.readmenu_Listen).setOnClickListener(this);
		readActivity.findViewById(R.id.readmenu_more).setOnClickListener(this);
		readActivity.findViewById(R.id.readmenu_bar).setOnClickListener(this);
		readActivity.findViewById(R.id.readmenu_back).setOnClickListener(this);
		readActivity.findViewById(R.id.read_menu_size_up).setOnClickListener(this);
		readActivity.findViewById(R.id.read_menu_size_down).setOnClickListener(this);
		readActivity.findViewById(R.id.rea_menu_tab_night).setOnClickListener(this);
		readActivity.findViewById(R.id.rea_menu_tab_text).setOnClickListener(this);
		readActivity.findViewById(R.id.rea_menu_tab_chapter).setOnClickListener(this);
		readActivity.findViewById(R.id.rea_menu_tab_set).setOnClickListener(this);
		readActivity.findViewById(R.id.read_menu_theme_wite).setOnClickListener(this);
		readActivity.findViewById(R.id.read_menu_theme_sheep).setOnClickListener(this);
		readActivity.findViewById(R.id.read_menu_theme_green).setOnClickListener(this);
		readActivity.findViewById(R.id.read_menu_theme_blue).setOnClickListener(this);
		readActivity.findViewById(R.id.read_menu_theme_pink).setOnClickListener(this);
		readActivity.findViewById(R.id.read_menu_typeface).setOnClickListener(this);
		readActivity.findViewById(R.id.read_menu_space_add).setOnClickListener(this);
		readActivity.findViewById(R.id.read_menu_space_dele).setOnClickListener(this);
		seekBar.setEnabled(!Cookies.getReadSetting().isLockScreenBright());
		seekBar.setOnSeekBarChangeListener(this);
	}

	// 设置当前章节链接地址
	public void setChapterUrl() {
		final BookChapter chapter = readActivity.getDoc().getCurChapter();
		if (chapter != null) {
			if (!StringUtil.isEmpty(chapter.getUrl())) {
				TextView tvUrl = (TextView) readUrl.findViewById(R.id.readmenu_url);
				tvUrl.setText(chapter.getUrl());
				readUrl.setVisibility(View.VISIBLE);
			} else {
				readUrl.setVisibility(View.GONE);
			}
			readUrl.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					StatisticUtil.sendEvent(readActivity, StatisticUtil.BOOK_SOURCE);
					if (!StringUtil.isEmpty(chapter.getUrl())) {
						Intent it = new Intent(Intent.ACTION_VIEW);
						it.setData(Uri.parse(chapter.getUrl()));
						readActivity.startActivity(it);
					}
				}
			});
		}
	}

	public void setBookName() {
		TextView tvBookName = (TextView) readActivity.findViewById(R.id.readmenu_bookname);
		if (readActivity.getBookDesc() != null && !StringUtil.isEmpty(readActivity.getBookDesc().getBookName()))
			tvBookName.setText(readActivity.getBookDesc().getBookName());
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
			readActivity.ifSaveBook();
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
				readDowload.setVisibility(View.GONE);
				readMenuText.setVisibility(View.GONE);
				readMenuSet.setVisibility(View.GONE);
				autoBtnLayout.setVisibility(View.VISIBLE);
			} else {
				readDowload.setVisibility(View.GONE);
				readMenuText.setVisibility(View.VISIBLE);
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
			if (readActivity.getBookDesc() != null) {
				readActivity.getReadChapter().getChapterList();
				readActivity.toggoleChapterList();
			}
			break;
		case R.id.rea_menu_tab_set:
			if (readMenuSet.getVisibility() == View.VISIBLE) {
				readDowload.setVisibility(View.GONE);
				readMenuText.setVisibility(View.GONE);
				readMenuSet.setVisibility(View.GONE);
				autoBtnLayout.setVisibility(View.VISIBLE);
			} else {
				if (UIHelper.isAutoBrightness(readActivity))
					changeProgress = false;
				seekBar.setProgress((int) (Cookies.getReadSetting().getBrightness() * 100));
				readDowload.setVisibility(View.GONE);
				readMenuText.setVisibility(View.GONE);
				readMenuSet.setVisibility(View.VISIBLE);
				autoBtnLayout.setVisibility(View.GONE);
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
		case R.id.rea_menu_tab_download:
			readMenuText.setVisibility(View.GONE);
			readMenuSet.setVisibility(View.GONE);
			autoBtnLayout.setVisibility(View.GONE);
			if (readActivity.getBookDesc() != null) {
				DownloadUtil downloadUtil = new DownloadUtil(readActivity,
						readActivity.findViewById(R.id.layout_readborad_main), readActivity.getBookDesc(), 1);
				downloadUtil.showDownloadCheckDialog();
			}
			break;
		case R.id.readmenu_mark:
			readMore.setVisibility(View.GONE);
			saveAndMark();
			break;
		case R.id.readmenu_bar:
			toggoleMenu();
			readMore.setVisibility(View.GONE);
			if (readActivity.getBookDesc() != null)
				WebActivity.launcher2(readActivity, readActivity.getBookDesc().getBookName());
			break;
		case R.id.readmenu_change_source:
			toggoleMenu();
			showBookSourceDialog();
			break;
		case R.id.readmenu_Listen:
			toggoleMenu();
			readMore.setVisibility(View.GONE);
			StatisticUtil.sendEvent(readActivity, StatisticUtil.BOOK_LISETN);
			UIHelper.luanchListen(readActivity.getBookDesc(), readActivity);
			break;
		case R.id.readmenu_more:
			readMore.setVisibility(readMore.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
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
		seekBar.setEnabled(!Cookies.getReadSetting().isLockScreenBright());
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
			readMore.setVisibility(View.GONE);
			readMenu.setVisibility(View.GONE);
			readMenuSet.setVisibility(View.GONE);
			readMenuText.setVisibility(View.GONE);
			if (mode == ReadMode.PAUSE)
				starAutoRead();
		} else {
			if (Cookies.getUserSetting().isReadScreenFull())
				MiscUtils.setSceenFull(false, readActivity);
			autoBtnLayout.setVisibility(View.VISIBLE);
			readMenu.setVisibility(View.VISIBLE);
			setChapterUrl();
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

	// 返回时是否保存书籍
	private void saveAndMark() {
		if (readActivity.getBookDesc() != null) {
			if (readActivity.getBookDesc().getIsStore() == 0) {// 如果没有加入书架
				UIHelper.showTowButtonDialog(readActivity, "本书还未加入书架，是否加入？", "取消", "确认", true,
						new OnDialogClickListener() {

							@Override
							public void onClick() {
								toggoleMenu();
							}
						}, new OnDialogClickListener() {

							@Override
							public void onClick() {
								readActivity.chatchManager.addBookDescAndSave(readActivity.getDoc(),
										readActivity.getBookDesc(), new DefaultInterface() {

											@Override
											public void getDefault(boolean haBack) {
												readActivity.showToast("添加成功");
											}
										});
							}
						});
			} else {
				addMark();
				toggoleMenu();
			}
		} else {
			readActivity.showToast("书籍加载失败，请重新加载");
			toggoleMenu();
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

	// 添加书签
	private void addMark() {
		if (readActivity.getDoc() != null && readActivity.getDoc().getCurChapter() != null) {
			String content = readActivity.getDoc().getCurChapter().getContent();
			if (!StringUtil.isEmpty(content) && content.length() > readActivity.getDoc().getLastReadPosition()) {
				readActivity.chatchManager.addMark(readActivity.getBookDesc(), readActivity.getDoc().getCurChapter(),
						readActivity.getDoc().getLastReadPosition());
				String title = content.length() > readActivity.getDoc().getLastReadPosition() ? content
						.substring(readActivity.getDoc().getLastReadPosition()) : content;
				tvBookMark.setText(title);
				readActivity.slideInLeftAnimation(tvBookMark);
				tvBookMark.postDelayed(SLIDE_OUT_BOOKMARK_PROMPT_RUNNABLE, 1500);
			}
		}
	}

	public void setDownloadPrecent(DownloadInfo downloadInfo) {
		if (readActivity.getBookDesc() != null
				&& downloadInfo.getBookDesc().getGid().equals(readActivity.getBookDesc().getGid())
				&& readMenuText.getVisibility() == View.GONE && readMenuSet.getVisibility() == View.GONE) {
			if (downloadInfo.getIndex() % 2 == 0) {
				String precent = "(" + downloadInfo.getIndex() + "/" + downloadInfo.getTotal() + ")";
				readDowload.setVisibility(View.VISIBLE);
				tvDownloadName.setText(readActivity.getBookDesc().getBookName() + " 正在缓存中");
				tvDownloadPrecent.setText(precent);
			}
		}
	}

	public void setDownloadBegin(DownloadInfo downloadInfo) {
		if (downloadInfo.getBookDesc().getGid().equals(readActivity.getBookDesc().getGid())
				&& readMenuText.getVisibility() == View.GONE && readMenuSet.getVisibility() == View.GONE) {
			String precent = "(" + downloadInfo.getIndex() + "/" + downloadInfo.getTotal() + ")";
			readDowload.setVisibility(View.VISIBLE);
			tvDownloadName.setText(readActivity.getBookDesc().getBookName() + " 正在缓存中");
			tvDownloadPrecent.setText(precent);
		}
	}

	public void setDownloadScucess() {
		readDowload.setVisibility(View.GONE);
	}

	public void setDownloadError() {
		readDowload.setVisibility(View.GONE);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		float f = progress / 100f;
		if (changeProgress)
			setBrightness(f, false);
		changeProgress = true;
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}

	public void showBookSourceDialog() {
		if (MiscUtils.isNetwork(readActivity)) {
			if (readActivity.getBookDesc() != null && readActivity.getDoc().getCurChapter() != null
					&& readActivity.getDoc().getCurChapter().getCatalog() != null) {
				final List<BookSource> bookSources = new ArrayList<BookSource>();
				final Dialog dialog = new Dialog(readActivity, R.style.CustomDialog);
				View contentView = LayoutInflater.from(readActivity).inflate(
						Cookies.getReadSetting().isNightMode() ? R.layout.view_book_source_ef
								: R.layout.view_book_source, null);
				final TextView tvCount = (TextView) contentView.findViewById(R.id.book_source_count);
				final TextView tvLoad = (TextView) contentView.findViewById(R.id.book_source_load);
				ImageButton ibClose = (ImageButton) contentView.findViewById(R.id.book_source_close);
				ListView listView = (ListView) contentView.findViewById(R.id.pull_refresh_list);
				View view = new View(readActivity);
				view.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, UIHelper.dipToPx(100)));
				listView.addFooterView(view);
				int height = (int) (UIHelper.getScreenPixHeight(readActivity) * 0.8);
				int width = (int) (UIHelper.getScreenPixWidth(readActivity) * 0.9);
				LayoutParams params = new LayoutParams(width, height);
				final SourceListAdapter adapter = new SourceListAdapter(dialog, bookSources);
				listView.setAdapter(adapter);
				dialog.setContentView(contentView, params);
				dialog.show();
				readActivity.chatchManager.catchBookSource(readActivity.getBookDesc(), readActivity.getDoc()
						.getCurChapter().getCatalog(), new BookSourceListInterface() {

					@Override
					public void getBookSourceList(List<BookSource> bookSourceList) {
						if (bookSourceList != null && bookSourceList.size() > 0) {
							tvLoad.setVisibility(View.GONE);
							bookSources.addAll(bookSourceList);
							adapter.notifyDataSetChanged();
							tvCount.setText("共" + bookSourceList.size() + "个来源");
						} else {
							tvLoad.setVisibility(View.VISIBLE);
							tvLoad.setText("暂无书籍来源");
						}
					}
				});
				ibClose.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						dialog.cancel();
					}
				});
			}
		} else {
			readActivity.showToast("网络异常，请检查网络");
		}
	}

	private class SourceListAdapter extends BaseAdapter {
		List<BookSource> bookSources;
		Dialog dialog;

		public SourceListAdapter(Dialog dialog, List<BookSource> bookSources) {
			this.dialog = dialog;
			this.bookSources = bookSources;
		}

		@Override
		public int getCount() {
			return bookSources.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(readActivity).inflate(
						Cookies.getReadSetting().isNightMode() ? R.layout.item_book_source_ef
								: R.layout.item_book_source, null);
				holder.tvChapterName = (TextView) convertView.findViewById(R.id.book_source_chapter);
				holder.tvSourceUrl = (TextView) convertView.findViewById(R.id.book_source_url);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final BookSource bookSource = bookSources.get(position);
			if (readActivity.getBookDesc().isMixedChannel()) {
				holder.tvChapterName.setText("最新章节：" + bookSource.getChapterTitle());
			} else {
				holder.tvChapterName.setText(bookSource.getChapterTitle());
			}
			holder.tvSourceUrl.setText(bookSource.getFromWeb());
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (bookSource.getIsCurrentChoose() != 1) {
						if (readActivity.getDoc().getCurChapter() != null
								&& readActivity.getDoc().getCurChapter().getCatalog() != null) {
							readActivity.chatchManager.changeBookSource(readActivity.getBookDesc(), readActivity
									.getDoc().getCurChapter().getCatalog(), bookSource);
							readActivity.changeSourceLoad(readActivity.getBookDesc(), readActivity.getDoc()
									.getCurChapter().getCatalog());
						}
					}
					dialog.cancel();
				}
			});
			return convertView;
		}
	}

	private class ViewHolder {
		private TextView tvChapterName;
		private TextView tvSourceUrl;
	}
}