package com.jie.book.work.activity;

import android.view.View;

import com.bond.bookcatch.vo.BookCatalog;
import com.bond.bookcatch.vo.BookDesc;
import com.jie.book.work.activity.ReadActivity.ReadingGestureListener;
import com.jie.book.work.read.Document;
import com.jie.book.work.read.ReadChapter;
import com.jie.book.work.read.ReadMenu;
import com.jie.book.work.read.ReadPanle;
import com.jie.book.work.read.ReadSlidingView;
import com.jie.book.work.read.ReadingBoard;
import com.jie.book.work.utils.UIHelper;

public class ReadBaseActivity extends ReadRootActivity {
	protected static String LUANCH_BOOK_DESC = "luanch_book_desc";
	protected static String LUANCH_CHAPTER = "luanch_chapter";
	protected ReadingGestureListener readGestureListener;
	protected ReadMenu readMenu;
	protected ReadingBoard readBorad;
	protected ReadSlidingView rootSlidingView;
	protected ReadChapter readChapter;// 目录
	protected ReadPanle readPanle;// 阅读面板
	protected View readBoradLayout;
	protected Document doc;
	protected BookDesc bookDesc;
	protected BookCatalog catalog;
	protected boolean isLuanch = true;
	protected boolean isReady = false;

	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}

	public ReadingBoard getReadBorad() {
		return readBorad;
	}

	public BookDesc getBookDesc() {
		return bookDesc;
	}

	public void setBookDesc(BookDesc bookDesc) {
		this.bookDesc = bookDesc;
	}

	public ReadChapter getReadChapter() {
		return readChapter;
	}

	public ReadSlidingView getRootSlidingView() {
		return rootSlidingView;
	}

	public ReadPanle getReadPanle() {
		return readPanle;
	}

	public ReadMenu getReadMenu() {
		return readMenu;
	}

	public void setReadMenu(ReadMenu readMenu) {
		this.readMenu = readMenu;
	}

	public void setReadPanle(ReadPanle readPanle) {
		this.readPanle = readPanle;
	}

	public int getScrrenWidth() {
		return scrrenWidth;
	}

	public void setScrrenWidth(int scrrenWidth) {
		this.scrrenWidth = scrrenWidth;
	}

	public boolean isReady() {
		return isReady;
	}

	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}

	public void setAdMode() {
		readBorad.setAdMode();
	}

	public void setNomalMode() {
		readBorad.setNormalMode();
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

	@Override
	protected void receiverBettery(int precent) {
		readPanle.setBettery(precent);
	}

	@Override
	protected void guidePageFinish() {
		if (doc != null && !doc.isChapterBack()) {
			dialog = UIHelper.showProgressDialog(dialog, activity);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		readBorad.onDestroy();
	}
}