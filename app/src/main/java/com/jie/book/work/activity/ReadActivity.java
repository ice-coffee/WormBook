package com.jie.book.work.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewConfigurationCompat;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.bond.bookcatch.vo.BookCatalog;
import com.bond.bookcatch.vo.BookDesc;
import com.jie.book.work.R;
import com.jie.book.work.application.BookActivityManager;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.entity.DownloadInfo;
import com.jie.book.work.read.BookCatchManager.DefaultInterface;
import com.jie.book.work.read.MultiTouchPointCollector;
import com.jie.book.work.read.ReadChapter;
import com.jie.book.work.read.ReadMenu;
import com.jie.book.work.read.ReadPanle;
import com.jie.book.work.read.ReadSlidingView;
import com.jie.book.work.read.ReadingBoard;
import com.jie.book.work.service.BookDownloadService;
import com.jie.book.work.utils.AdUtil;
import com.jie.book.work.utils.UIHelper;
import com.jie.book.work.utils.UIHelper.OnDialogClickListener;

public class ReadActivity extends ReadBaseActivity {
	private DownloadBroadcastReceiver broadcastReceiver;
	public static ReadActivity instance;

	public static void luanch(Activity content, BookDesc bookDesc, BookCatalog catalog) {
		Intent intent = new Intent();
		intent.putExtra(LUANCH_BOOK_DESC, bookDesc);
		intent.putExtra(LUANCH_CHAPTER, catalog);
		intent.setClass(content, ReadActivity.class);
		BookActivityManager.getInstance().goTo(content, intent);
	}

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		instance = this;
		initBroadcastReceive();
		setContentView(R.layout.act_read_main);
		if (getIntent().hasExtra(LUANCH_BOOK_DESC))
			bookDesc = (BookDesc) getIntent().getSerializableExtra(LUANCH_BOOK_DESC);
		if (getIntent().hasExtra(LUANCH_CHAPTER))
			catalog = (BookCatalog) getIntent().getSerializableExtra(LUANCH_CHAPTER);
		isLuanch = true;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		if (getIntent().hasExtra(LUANCH_BOOK_DESC))
			bookDesc = (BookDesc) getIntent().getSerializableExtra(LUANCH_BOOK_DESC);
		if (getIntent().hasExtra(LUANCH_CHAPTER))
			catalog = (BookCatalog) getIntent().getSerializableExtra(LUANCH_CHAPTER);
		isLuanch = true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (bookDesc != null) {
			outState.putSerializable(LUANCH_BOOK_DESC, bookDesc);
			if (bookDesc.getLastReadCatalog() != null)
				outState.putSerializable(LUANCH_CHAPTER, bookDesc.getLastReadCatalog());
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState.containsKey(LUANCH_BOOK_DESC))
			bookDesc = (BookDesc) savedInstanceState.getSerializable(LUANCH_BOOK_DESC);
		if (savedInstanceState.containsKey(LUANCH_CHAPTER))
			catalog = (BookCatalog) savedInstanceState.getSerializable(LUANCH_CHAPTER);
	}

	@SuppressLint("HandlerLeak")
	@Override
	protected void onResume() {
		super.onResume();
		if (bookDesc == null) {
			BookMainActivity.luanch(activity);
			finishInAnim();
		} else {
			setReady(false);
			if (doc != null && doc.getCurChapter() != null && doc.getCurChapter().getCatalog() != null
					&& bookDesc.getLastReadCatalog() != null) {
				if (bookDesc.getLastReadCatalog().getUrl().equals(doc.getCurChapter().getCatalog().getUrl())) {
					readMenu.setTypeface(Cookies.getUserSetting().getUserTypeface(), true);
					return;
				}
			}
			initUI();
			initGesture();
			initData();
			initGuide();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
	}

	@Override
	protected void initUI() {
		setShowAd(false);
		readBorad = (ReadingBoard) findViewById(R.id.readborad);
		readPanle = new ReadPanle(this, readBorad);
		readBoradLayout = findViewById(R.id.readborad_layout);
		rootSlidingView = (ReadSlidingView) findViewById(R.id.layout_readborad_root_sliding);
		readChapter = new ReadChapter(this, findViewById(R.id.layout_readborad_chapterlist));
		readMenu = new ReadMenu(this);
		LinearLayout.LayoutParams paramReadBorad = (LinearLayout.LayoutParams) readBoradLayout
				.getLayoutParams();
		LayoutParams paramRoot = (LayoutParams) rootSlidingView.getLayoutParams();
		paramRoot.width = scrrenWidth * 2;
		paramReadBorad.width = scrrenWidth;
		rootSlidingView.requestLayout();
		readBoradLayout.requestLayout();
		if (isLuanch) {
			rootSlidingView.scrollToNow(scrrenWidth, 0);
		} else {
			catalog = null;
			if (isChapterListPage()) {
				rootSlidingView.scrollToNow(0, 0);
				readChapter.getChapterList();
			} else {
				rootSlidingView.scrollToNow(scrrenWidth, 0);
			}
		}
		readBorad.invalidate();
		isLuanch = false;
		new AdUtil(this);
	}

	@Override
	protected void initData() {
		if (bookDesc != null) {
			readMenu.setBookName();
			readBorad.init(bookDesc, catalog);
		}
	}

	public void changeSourceLoad(BookDesc bookDesc, BookCatalog catalog) {
		if (bookDesc != null && catalog != null) {
			if (bookDesc.isMixedChannel()) {// 如果是神器数据
				doc.reloadDataAfterChangeSource();
			} else {
				doc.reloadData(catalog);
			}
		}
	}

	// 从某一章重新加载
	@SuppressLint("HandlerLeak")
	public void reloadData(final BookCatalog catalog, boolean toggoleChapterList) {
		if (toggoleChapterList)
			toggoleChapterList();
		Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				doc.reloadData(catalog);
			}
		};
		handler.sendEmptyMessageDelayed(0, 500);
	}

	// 从某一章重新加载
	@SuppressLint("HandlerLeak")
	public void reloadData(final int catalogPosition, final int benginPosition) {
		toggoleChapterList();
		if (bookDesc.getCatalogs() != null && bookDesc.getCatalogs().size() > 0
				&& bookDesc.getCatalogs().size() > catalogPosition) {
			Handler handler = new Handler() {

				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					doc.reloadData(bookDesc.getCatalogs().get(catalogPosition), benginPosition);
				}
			};
			handler.sendEmptyMessageDelayed(0, 500);
		}
	}

	@SuppressWarnings("deprecation")
	private void initGesture() {
		touchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(ViewConfiguration.get(this));
		readGestureListener = new ReadingGestureListener();
		mGestureDetector = new GestureDetector(readGestureListener);
		mPointCollector = new MultiTouchPointCollector();
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
			ifSaveBook();
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
				if (yScrollSum >= UIHelper.getScreenPixHeight(ReadActivity.this) / 12
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
				if (!readBorad.isDragging() && mDragMode == DragMode.DRAG_REST && bookDesc != null) {
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
			if (ReadActivity.this.readMenu.isVisible())
				ReadActivity.this.readMenu.toggoleMenu();
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

	// 返回时是否保存书籍
	public void ifSaveBook() {
		if (bookDesc != null) {
			if (bookDesc.getIsStore() == 0) {// 如果没有加入书架
				UIHelper.showTowButtonDialog(activity, "是否加入书架？", "取消", "确定", true, new OnDialogClickListener() {

					@Override
					public void onClick() {
						finish();
					}
				}, new OnDialogClickListener() {

					@Override
					public void onClick() {
						chatchManager.addBookDescAndSave(doc, bookDesc, new DefaultInterface() {

							@Override
							public void getDefault(boolean haBack) {
								activity.showToast("添加成功");
								finish();
							}
						});
					}
				});
			} else {
				finishInAnim();
			}
		} else {
			finish();
		}
	}

	private void initBroadcastReceive() {
		broadcastReceiver = new DownloadBroadcastReceiver();
		LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
		IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(BookDownloadService.ACTION_NAME);
		lbm.registerReceiver(broadcastReceiver, mIntentFilter);
	}

	private class DownloadBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, final Intent intent) {
			int status = intent.getIntExtra(BookDownloadService.DOWLOAD_STATUS, 1);
			DownloadInfo downloadInfo = (DownloadInfo) intent.getSerializableExtra(BookDownloadService.DOWLOAD_INFO);
			if (status == 0) {
				readMenu.setDownloadPrecent(downloadInfo);
			} else if (status == 1) {
				readMenu.setDownloadScucess();
			} else if (status == 2) {
				readMenu.setDownloadError();
			} else if (status == 3) {
				readMenu.setDownloadBegin(downloadInfo);
			} else if (status == 4) {
				if (bookDesc != null && downloadInfo.getBookDesc().getGid().equals(bookDesc.getGid())) {
					readMenu.setDownloadBegin(downloadInfo);
					doc.setLastReadOffline(1);
					chatchManager.addBookDesc(bookDesc, null);
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (arg0 == CustomThemeActivity.REQUEST_CODE && arg1 == RESULT_OK) {
			addReadTheme();
			readMenu.swithTheme(4);
		}
	}

}