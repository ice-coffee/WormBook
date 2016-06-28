package com.jie.book.work.read;

import java.util.Date;

import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.jie.book.work.R;
import com.jie.book.work.activity.ReadActivity;
import com.jie.book.work.activity.TypefaceActivity;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.utils.StringUtil;

//阅读面板
public class ReadPanle {
	private ReadActivity readActivity;
	private TextView tvBattery;
	private TextView tvProgress;
	private TextView tvChapter;
	private TextView tvTime;
	private TextView tvSourceTitle;
	private ReadingBoard readBoard;
	private Button ibChangeSource, ibReload;
	private View viewEmpty;

	public ReadPanle(ReadActivity readActivity, ReadingBoard readBoard) {
		this.readBoard = readBoard;
		this.readActivity = readActivity;
		initUI();
	}

	private void initUI() {
		tvChapter = (TextView) readActivity.findViewById(R.id.tv_read_chapter_name);
		tvProgress = (TextView) readActivity.findViewById(R.id.read_panle_progress);
		tvTime = (TextView) readActivity.findViewById(R.id.read_panle_time);
		tvBattery = (TextView) readActivity.findViewById(R.id.read_panle_battery);
		tvSourceTitle = (TextView) readActivity.findViewById(R.id.read_main_change_source_title);
		viewEmpty = readActivity.findViewById(R.id.read_main_change_source_layout);
		ibReload = (Button) readActivity.findViewById(R.id.read_main_change_source_reload);
		ibChangeSource = (Button) readActivity.findViewById(R.id.read_main_change_source_btn);
		ibChangeSource.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				readActivity.getReadMenu().showBookSourceDialog();
			}
		});
		ibReload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (readActivity.getDoc().getCurChapter() != null
						&& readActivity.getDoc().getCurChapter().getCatalog() != null) {
					readActivity.reloadData(readActivity.getDoc().getCurChapter().getCatalog(), false);
				}
			}
		});
	}

	public void drawReadPanle(String chapter, String readProgress) {
		if (Cookies.getUserSetting().getUserTypeface().equals(TypefaceActivity.TYPEPFACE_FT)) {// 繁体字
			chapter = StringUtil.changeTraditional(chapter);
		}
		String timeStr = StringUtil.dfTime.format(new Date());
		tvChapter.setText(StringUtil.isEmpty(chapter) == true ? StringUtil.EMPTY : chapter);
		tvTime.setText(StringUtil.isEmpty(timeStr) == true ? StringUtil.EMPTY : timeStr);
		if (readProgress != null) {
			setReadProgress(readProgress);
		}
	}

	public void setReadProgress(String progress) {
		tvProgress.setText(progress);
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

	public void setViewEmptyVisble() {
		if (readActivity.getDoc() != null && readActivity.getDoc().getCurChapter() != null) {
			viewEmpty.setVisibility(readActivity.getDoc().getCurChapter().getParseError() ? View.VISIBLE : View.GONE);
		}
	}

	public void setTypeface(Typeface tf) {
		readBoard.setTypeface(tf, true);
		tvChapter.setTypeface(tf);
		tvSourceTitle.setTypeface(tf);
	}

	public void setThemeColor(ReadTheme theme) {
		tvProgress.setTextColor(theme.getTextColorRes());
		tvChapter.setTextColor(theme.getTextColorRes());
		tvTime.setTextColor(theme.getTextColorRes());
		tvBattery.setTextColor(theme.getTextColorRes());
		tvSourceTitle.setTextColor(theme.getTextColorRes());
		tvBattery.setBackgroundResource(theme.getBatteryDrawble());
	}

	public void setBettery(int betteryPrecent) {
		tvBattery.setText(String.valueOf(betteryPrecent));
	}

}
