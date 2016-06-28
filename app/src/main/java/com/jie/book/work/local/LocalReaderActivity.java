package com.jie.book.work.local;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewConfigurationCompat;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.bond.bookcatch.local.vo.LocalBookDesc;
import com.jie.book.work.R;
import com.jie.book.work.activity.CustomThemeActivity;
import com.jie.book.work.application.BookActivityManager;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.read.MultiTouchPointCollector;
import com.jie.book.work.read.ReadSlidingView;
import com.jie.book.work.utils.AdUtil;
import com.jie.book.work.utils.UIHelper;

public class LocalReaderActivity extends LocalReaderBaseActivity {
	public static final String LUANCH_BOOK_DESC = "luanch_book_desc";
	public static final int MSG_HANDEL_SUCCEED = 0;
	public static final int MSG_HANDEL_ERRO = 1;

	public static void Luanch(Activity context, LocalBookDesc bookDesc) {
		Intent intent = new Intent();
		intent.putExtra(LUANCH_BOOK_DESC, bookDesc);
		intent.setClass(context, LocalReaderActivity.class);
		BookActivityManager.getInstance().goTo(context, intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_read_local);

	}

	protected void onResume() {
		super.onResume();
		initUI();
		initGesture();
		initGuide();
		initData();
	}

	@Override
	protected void initUI() {
		readBorad = (LocalReadingBoard) findViewById(R.id.readingBoard);
		readPanle = new LocalReadPanle(this, readBorad);
		readBoradLayout = findViewById(R.id.readborad_layout);
		rootSlidingView = (ReadSlidingView) findViewById(R.id.layout_readborad_root_sliding);
		readChapter = new LocalReadChapter(this, findViewById(R.id.layout_readborad_chapterlist));
		readMenu = new LocalReadMenu(this);
		LinearLayout.LayoutParams paramReadBorad = (LinearLayout.LayoutParams) readBoradLayout
				.getLayoutParams();
		LayoutParams paramRoot = (LayoutParams) rootSlidingView.getLayoutParams();
		paramRoot.width = scrrenWidth * 2;
		paramReadBorad.width = scrrenWidth;
		rootSlidingView.requestLayout();
		readBoradLayout.requestLayout();
		rootSlidingView.scrollToNow(scrrenWidth, 0);
		readBorad.invalidate();
		new AdUtil(this);
	}

	@SuppressWarnings("deprecation")
	private void initGesture() {
		touchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(ViewConfiguration.get(this));
		readGestureListener = new ReadingGestureListener();
		mGestureDetector = new GestureDetector(readGestureListener);
		mPointCollector = new MultiTouchPointCollector();
	}

	@Override
	protected void initData() {
		mBookInfo = (LocalBookDesc) getIntent().getSerializableExtra(LUANCH_BOOK_DESC);
		if (mBookInfo != null && new File(mBookInfo.getFilePath()).exists()) {
			try {
				doc = new LocalTxtDocument(mBookInfo, Cookies.getReadSetting().getLineSpacing(), Cookies
						.getReadSetting().getParagraphSpacing());
				readMenu.setBookName();
				readBorad.setDocument(doc);
				LocalBookManager.recordBookOper(mBookInfo);
			} catch (Exception e) {
				showToast("文件不存在！");
			}
		} else {
			showToast("文件不存在！");
		}
	}

	@Override
	protected void initListener() {

	}

	@Override
	protected void receiverBettery(int precent) {
		readPanle.setBettery(precent);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (Cookies.getUserSetting().isReadVoice()) {
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				if (event.getAction() == KeyEvent.ACTION_UP) {
					readBorad.turnNextPageAndRedraw();
				}
				return true;
			case KeyEvent.KEYCODE_VOLUME_UP:
				if (event.getAction() == KeyEvent.ACTION_UP) {
					readBorad.turnPreviousPageAndRedraw();
				}
				return true;
			}
		}
		return super.dispatchKeyEvent(event);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (isChapterListPage()) {
				toggoleChapterList();
				return true;
			}
			finishInAnim();
			return true;
		case KeyEvent.KEYCODE_MENU:
			readMenu.toggoleMenu();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean onTouchEvent(MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			mSlidingMode = SlidingMode.SLIDING_RESET;
			if (readBorad.isDragging()) {
				readBorad.slideSmoothly(0f);
			} else {
				if (mDragMode == DragMode.DRAG_TO_CHAPTER) {
					rootSlidingView.scrollToTime(0, 0);
					mDragMode = DragMode.DRAG_REST;
				} else if (mDragMode == DragMode.DRAG_TO_READ) {
					rootSlidingView.scrollToTime(scrrenWidth, 0);
					mDragMode = DragMode.DRAG_REST;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (arg0 == CustomThemeActivity.REQUEST_CODE && arg1 == RESULT_OK) {
			addReadTheme();
			readMenu.swithTheme(4);
		}
	}

	class ReadingGestureListener extends SimpleOnGestureListener {
		private boolean inDownAction;
		private float[] pts = new float[4];
		private float twoPointXDistance;
		private float twoPointYDistance;
		private float xScrollSum;
		private float yScrollSum;

		// 获取X轴上滚动的距离
		public float getXScrollSum() {
			return xScrollSum;
		}

		public boolean onDown(MotionEvent paramMotionEvent) {
			this.xScrollSum = 0.0F;
			this.yScrollSum = 0.0F;
			this.twoPointXDistance = 0.0F;
			this.twoPointYDistance = 0.0F;
			this.inDownAction = true;
			return true;
		}

		private void checkInMoving(float xDiff, float yDiff) {
			boolean xMoved = xDiff > touchSlop * 2;
			boolean yMoved = yDiff > touchSlop;

			if (xMoved && mSlidingMode == SlidingMode.SLIDING_RESET) {
				mSlidingMode = SlidingMode.SLIDING_X;
			}
			if (yMoved && mSlidingMode == SlidingMode.SLIDING_RESET) {
				mSlidingMode = SlidingMode.SLIDING_Y;
			}
		}

		private void handleMultiTouchScroll(float distanceX, float distanceY, float lastTwoPointXDistance,
				float lastTwoPointYDistance) {

			if (!inDownAction)
				return;
			if (yScrollSum >= xScrollSum) {
				if (yScrollSum >= UIHelper.getScreenPixHeight(LocalReaderActivity.this) / 12
						&& Math.abs(lastTwoPointYDistance - twoPointYDistance) < scrrenWidth / 24) {
					if (distanceY < 0) {
						readMenu.flingTheme();
					} else {
						// 双指上滑
					}
					inDownAction = false;
				}
			} else if (xScrollSum >= scrrenWidth / 12
					&& Math.abs(lastTwoPointXDistance - twoPointXDistance) < scrrenWidth / 24) {
				// 先判断是不是滑动目录
				if (!readBorad.isDragging() && mDragMode == DragMode.DRAG_REST && mBookInfo != null) {
					if (distanceX < 0 && !isChapterListPage()) {
						mDragMode = DragMode.DRAG_TO_CHAPTER;
						readChapter.getChapterList();
					}
					if (distanceX > 0 && isChapterListPage()) {
						mDragMode = DragMode.DRAG_TO_READ;
					}
				}
				// 这是就是正式的滑动了
				if (mDragMode == DragMode.DRAG_TO_CHAPTER || mDragMode == DragMode.DRAG_TO_READ) {
					if (rootSlidingView.getXposition() >= 0 && rootSlidingView.getXposition() <= scrrenWidth) {
						rootSlidingView.scrollByNow((int) distanceX, 0);
					}
				}
			}
		}

		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			if (LocalReaderActivity.this.readMenu.isVisible())
				LocalReaderActivity.this.readMenu.toggoleMenu();
			xScrollSum += Math.abs(distanceX);
			yScrollSum += Math.abs(distanceY);
			if (xScrollSum < 5 && yScrollSum < 5)
				return true;
			float lastTwoPointXDistance = 0;
			float lastTwoPointYDistance = 0;
			if (mPointCollector != null) {
				int pointCount = mPointCollector.collectPoints(e2, pts);
				if (pointCount >= 2) {
					if (twoPointXDistance == 0) {
						twoPointXDistance = Math.abs(pts[0] - pts[2]);
						twoPointYDistance = Math.abs(pts[1] - pts[3]);
					} else {
						lastTwoPointXDistance = Math.abs(pts[0] - pts[2]);
						lastTwoPointYDistance = Math.abs(pts[1] - pts[3]);
					}
				}
			}

			if (twoPointXDistance > 0 || twoPointYDistance > 0) {
				handleMultiTouchScroll(distanceX, distanceY, lastTwoPointXDistance, lastTwoPointYDistance);
			} else {
				if (isChapterListPage())
					return true;
				checkInMoving(yScrollSum, xScrollSum);
				if (mSlidingMode == SlidingMode.SLIDING_X) {
					// 上下滑动调节亮度
					if (!Cookies.getReadSetting().isLockScreenBright())
						readMenu.setBrightness((distanceY % 2500) / 600f, true);
				} else if (mSlidingMode == SlidingMode.SLIDING_Y) {
					if (readBorad.isDragging()) {
						readBorad.changeTurningPageDirection(distanceX > 0 ? 1 : 0);
						readBorad.dragRedraw(distanceX);
					} else {
						if (distanceX > 0) {
							if (readBorad.turnNextPage())
								readBorad.startDragging();
						} else {
							if (readBorad.turnPreviousPage())
								readBorad.startDragging();
						}
					}
				}
			}
			return true;
		}

		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (readMenu.isVisible()) {
				readMenu.toggoleMenu();
			} else {
				if (isChapterListPage())
					return true;
				if (readBorad.isDragging()) {
					readBorad.slideSmoothly(velocityX);
				}
			}
			return true;
		}

		public boolean onSingleTapUp(MotionEvent e) {
			float x = e.getX();
			float y = e.getY();
			if (readMenu.isVisible()) {
				readMenu.toggoleMenu();
			} else {
				if (isChapterListPage())
					return true;
				float portionWidth = scrrenWidth / 5;
				if (x < portionWidth * 2) {// 上一页/
					readBorad.turnPreviousPageAndRedraw();
				} else if (x > portionWidth * 3) {// 下一页
					readBorad.turnNextPageAndRedraw();
				} else {
					float portionHeight = scrrenheight / 4;
					if (y < portionHeight) {
						readBorad.turnPreviousPageAndRedraw();
					} else if (y > portionHeight * 3) {
						readBorad.turnNextPageAndRedraw();
					} else {
						readMenu.toggoleMenu();
					}
				}
			}
			return true;
		}
	}

	// 是不是章节列表界面
	private boolean isChapterListPage() {
		return rootSlidingView.getXFinal() == 0;
	}

	// 阅读界面和目录之间切换
	public void toggoleChapterList() {
		if (isChapterListPage())
			rootSlidingView.scrollToTime(scrrenWidth, 0);
		else
			rootSlidingView.scrollToTime(0, 0);
	}

	// 从某一章重新加载
	public void reloadData(final long beginPosition) {
		toggoleChapterList();
		readBorad.adjustReadingProgressByPosition(beginPosition);
	}

	public void setAdMode() {
		readBorad.setAdMode();
	}

	public void setNomalMode() {
		readBorad.setNormalMode();
	}

}
