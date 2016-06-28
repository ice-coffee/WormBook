package com.jie.book.work.local;

import java.util.Date;

import android.graphics.Typeface;
import android.widget.TextView;

import com.jie.book.work.R;
import com.jie.book.work.activity.TypefaceActivity;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.read.ReadTheme;
import com.jie.book.work.utils.StringUtil;

//阅读面板
public class LocalReadPanle {
	private LocalReaderActivity readActivity;
	private TextView tvBattery;
	private TextView tvCurProgress;
	private TextView tvProgress;
	private TextView tvChapter;
	private TextView tvTime;
	private LocalReadingBoard readBoard;

	public LocalReadPanle(LocalReaderActivity readActivity, LocalReadingBoard readBoard) {
		this.readBoard = readBoard;
		this.readActivity = readActivity;
		initUI();
	}

	private void initUI() {
		tvChapter = (TextView) readActivity.findViewById(R.id.tv_read_chapter_name);
		tvProgress = (TextView) readActivity.findViewById(R.id.read_panle_progress);
		tvCurProgress = (TextView) readActivity.findViewById(R.id.tvCurProgress);
		tvTime = (TextView) readActivity.findViewById(R.id.read_panle_time);
		tvBattery = (TextView) readActivity.findViewById(R.id.read_panle_battery);
	}

	public void drawReadPanle(String chapter, String readProgress) {
		if (Cookies.getUserSetting().getUserTypeface().equals(TypefaceActivity.TYPEPFACE_FT)) {// 繁体字
			chapter = StringUtil.changeTraditional(chapter);
		}
		String timeStr = StringUtil.dfTime.format(new Date());
		tvChapter.setText(StringUtil.isEmpty(chapter) == true ? StringUtil.EMPTY : chapter);
		tvTime.setText(StringUtil.isEmpty(timeStr) == true ? StringUtil.EMPTY : timeStr);
		if (readProgress != null)
			setReadProgress(readProgress);
	}

	public void setReadProgress(String progress) {
		tvProgress.setText(progress);
		tvCurProgress.setText(progress);
	}

	public String getReadProgress() {
		return tvProgress.getText().toString();
	}

	public String getChapterStr() {
		return tvChapter.getText().toString();
	}

	public TextView getTvProgress() {
		return tvProgress;
	}

	public void setTypeface(Typeface tf) {
		readBoard.setTypeface(tf, true);
		tvChapter.setTypeface(tf);
	}

	public void setThemeColor(ReadTheme theme) {
		tvProgress.setTextColor(theme.getTextColorRes());
		tvChapter.setTextColor(theme.getTextColorRes());
		tvTime.setTextColor(theme.getTextColorRes());
		tvBattery.setTextColor(theme.getTextColorRes());
		tvBattery.setBackgroundResource(theme.getBatteryDrawble());
	}

	public void setBettery(int betteryPrecent) {
		tvBattery.setText(String.valueOf(betteryPrecent));
	}

}
