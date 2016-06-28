package com.jie.book.work.local;

import android.view.View;

import com.bond.bookcatch.local.vo.LocalBookDesc;
import com.jie.book.work.activity.ReadRootActivity;
import com.jie.book.work.local.LocalReaderActivity.ReadingGestureListener;
import com.jie.book.work.read.ReadSlidingView;

public abstract class LocalReaderBaseActivity extends ReadRootActivity {
	protected ReadingGestureListener readGestureListener;
	protected LocalBookDesc mBookInfo;
	protected LocalTxtDocument doc;
	protected LocalReadMenu readMenu;
	protected LocalReadingBoard readBorad;
	protected ReadSlidingView rootSlidingView;
	protected LocalReadChapter readChapter;// 目录
	protected LocalReadPanle readPanle;// 阅读面板
	protected View readBoradLayout;

	@Override
	protected void receiverBettery(int precent) {
		readPanle.setBettery(precent);
	}

	@Override
	protected void guidePageFinish() {
	}

	public LocalBookDesc getBookCase() {
		return mBookInfo;
	}

	public LocalReadingBoard getReadBorad() {
		return readBorad;
	}

	public LocalReadChapter getReadChapter() {
		return readChapter;
	}

	public ReadSlidingView getRootSlidingView() {
		return rootSlidingView;
	}

	public LocalReadPanle getReadPanle() {
		return readPanle;
	}

	public LocalReadMenu getReadMenu() {
		return readMenu;
	}

	public void setReadMenu(LocalReadMenu readMenu) {
		this.readMenu = readMenu;
	}

	public void setReadPanle(LocalReadPanle readPanle) {
		this.readPanle = readPanle;
	}

	public LocalTxtDocument getDoc() {
		return doc;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		readBorad.onDestroy();
	}
}
