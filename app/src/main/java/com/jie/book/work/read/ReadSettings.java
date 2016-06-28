package com.jie.book.work.read;

import android.content.Context;
import android.content.pm.ActivityInfo;

import com.jie.book.work.R;
import com.jie.book.work.utils.SharedPreferenceUtil;

public class ReadSettings {
	public final static int MAX_TEXT_SIZE = 100;
	public final static int MIN_TEXT_SIZE = 10;
	public static final String LOCK_SCREEN_BRIGHT = "lockScreenBright";
	public static final String SCREEN_ORIENTAION = "screenOrientaion";
	public static final String THEME_INDEX = "themeIndex";
	public static final String NIGHT_MODE = "isNightMode";
	public static final String TEXT_SIZE = "textSize";
	public static final String BRIGHTNESS = "brightness";
	public static final String TYPEFACE = "typeface";
	public static final String LINE_SPACING = "lineSpacing";
	public static final String PARAGRAPH_SPACING = "paragraphSpacing";
	public static final String SHOW_READING_GUIDE = "showReadingGuide";
	public static final String REMIND_BOOKS = "remind_books";
	public static final String AUTO_BOOK_TIME = "auto_book_time";
	public static final String READ_THEME_BG = "read_theme_bg";
	public static final String READ_THEME_TEXT = "read_theme_text";

	private Context context;
	private SharedPreferenceUtil spUtil;

	public ReadSettings(Context paramContext) {
		spUtil = SharedPreferenceUtil.getInstance(paramContext);
		context = paramContext;
	}

	public int getThemeIndex() {
		return spUtil.getInt(THEME_INDEX, 2);
	}

	public void setThemeIndex(int paramInt) {
		spUtil.putInt(THEME_INDEX, paramInt);
	}

	public int getScreenOrientaion() {
		return spUtil.getInt(SCREEN_ORIENTAION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	public void setScreenOrientaion(int screenOrientaion) {
		spUtil.putInt(SCREEN_ORIENTAION, screenOrientaion);
	}

	public void setTextSize(int paramInt) {
		spUtil.putInt(TEXT_SIZE, paramInt);
	}

	public int getTextSize() {
		return spUtil.getInt(TEXT_SIZE, context.getResources().getDimensionPixelSize(R.dimen.default_read_text_size));
	}

	public boolean isLockScreenBright() {
		return spUtil.getBoolean(LOCK_SCREEN_BRIGHT, false);
	}

	public void setLockScreenBright(boolean paramBoolean) {
		spUtil.putBoolean(LOCK_SCREEN_BRIGHT, paramBoolean);
	}

	public boolean isNightMode() {
		return spUtil.getBoolean(NIGHT_MODE, false);
	}

	public void setNightMode(boolean paramBoolean) {
		spUtil.putBoolean(NIGHT_MODE, paramBoolean);
	}

	public float getBrightness() {
		return spUtil.getFloat(BRIGHTNESS, 0.7f);
	}

	public void setBrightness(float brightness) {
		spUtil.putFloat(BRIGHTNESS, brightness);
	}

	public int getLineSpacing() {
		return spUtil.getInt(LINE_SPACING,
				context.getResources().getDimensionPixelSize(R.dimen.reading_board_line_spacing));
	}

	public void setLineSpacing(int lineSpacing) {
		spUtil.putInt(LINE_SPACING, lineSpacing);
	}

	public int getParagraphSpacing() {
		return spUtil.getInt(PARAGRAPH_SPACING,
				context.getResources().getDimensionPixelSize(R.dimen.reading_board_paragraph_spacing));
	}

	public void setParagraphSpacing(int paragraphSpacing) {
		spUtil.putInt(PARAGRAPH_SPACING, paragraphSpacing);
	}

	public boolean isShowReadingGuide() {
		return spUtil.getBoolean(SHOW_READING_GUIDE, true);
	}

	public void setShowReadingGuide(boolean showReadingGuide) {
		spUtil.putBoolean(SHOW_READING_GUIDE, showReadingGuide);
	}

	public int getAutoTime() {
		return spUtil.getInt(AUTO_BOOK_TIME, 15000);
	}

	public void setAutoTime(int autoTime) {
		spUtil.putInt(AUTO_BOOK_TIME, autoTime);
	}

	public int getReadThemeBg() {
		return spUtil.getInt(READ_THEME_BG, 0);
	}

	public void setReadThemeBg(int bg) {
		spUtil.putInt(READ_THEME_BG, bg);
	}

	public int getReadThemeText() {
		return spUtil.getInt(READ_THEME_TEXT, 0);
	}

	public void setReadThemeText(int text) {
		spUtil.putInt(READ_THEME_TEXT, text);
	}
}