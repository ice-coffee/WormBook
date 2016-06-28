package com.jie.book.work.read;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.jie.book.work.R;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.utils.UIHelper;

//阅读样式
public class ReadTheme {

	private int id;
	// 字体颜色
	private int textColorRes;

	private int bgColorRes;

	private int bgDivider;

	// 阅读界面目录相关
	private int chapterRoundDrawble;

	private int chapterArrowDrawble;

	// 电池相关
	private int batteryDrawble;

	// 书签指示器
	private int markIndicator;

	public ReadTheme(int id, int textColorRes, int bgColorRes, int bgDivider, int chapterRoundDrawble,
			int chapterArrowDrawble, int batteryDrawble, int markIndicator) {
		this.id = id;
		this.textColorRes = textColorRes;
		this.bgColorRes = bgColorRes;
		this.bgDivider = bgDivider;
		this.batteryDrawble = batteryDrawble;
		this.chapterRoundDrawble = chapterRoundDrawble;
		this.chapterArrowDrawble = chapterArrowDrawble;
		this.markIndicator = markIndicator;
	}

	public ReadTheme() {
		this.id = 4;
		this.textColorRes = Cookies.getReadSetting().getReadThemeText();
		this.bgColorRes = Cookies.getReadSetting().getReadThemeBg();
		this.bgDivider = R.drawable.view_chapter_list_black_divider;
		this.batteryDrawble = R.drawable.bg_battery_gray;
		this.chapterRoundDrawble = R.drawable.icon_book_chapter_not_now;
		this.chapterArrowDrawble = R.drawable.icon_book_chapter_light_arrow;
		this.markIndicator = R.drawable.icon_mark_indica_night;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTextColorRes() {
		return textColorRes;
	}

	public void setTextColorRes(int textColorRes) {
		this.textColorRes = textColorRes;
	}

	public int getBgColorRes() {
		return bgColorRes;
	}

	public void setBgColorRes(int bgColorRes) {
		this.bgColorRes = bgColorRes;
	}

	public int getBgDivider() {
		return bgDivider;
	}

	public void setBgDivider(int bgDivider) {
		this.bgDivider = bgDivider;
	}

	public int getChapterRoundDrawble() {
		return chapterRoundDrawble;
	}

	public void setChapterRoundDrawble(int chapterRoundDrawble) {
		this.chapterRoundDrawble = chapterRoundDrawble;
	}

	public int getChapterArrowDrawble() {
		return chapterArrowDrawble;
	}

	public void setChapterArrowDrawble(int chapterArrowDrawble) {
		this.chapterArrowDrawble = chapterArrowDrawble;
	}

	public int getBatteryDrawble() {
		return batteryDrawble;
	}

	public void setBatteryDrawble(int batteryDrawble) {
		this.batteryDrawble = batteryDrawble;
	}

	public int getMarkIndicator() {
		return markIndicator;
	}

	public void setMarkIndicator(int markIndicator) {
		this.markIndicator = markIndicator;
	}

	private Drawable readingDrawable;

	@SuppressWarnings("deprecation")
	public Drawable getReadingDrawable(Context context) {
		if (readingDrawable == null) {
			if (id == 2) {
				readingDrawable = context.getResources().getDrawable(getBgColorRes());
			} else {
				readingDrawable = UIHelper.color2Drawble(getBgColorRes());
			}
		}
		return readingDrawable;
	}
}
