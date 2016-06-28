package com.jie.book.work.local;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.bond.bookcatch.local.vo.LocalBookCatalog;
import com.jie.book.work.R;
import com.jie.book.work.activity.TypefaceActivity;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.read.Document;
import com.jie.book.work.read.LayoutUtil;
import com.jie.book.work.read.ReadSettings;
import com.jie.book.work.read.ReadTheme;
import com.jie.book.work.read.RectObjectPool;
import com.jie.book.work.utils.BitmapUtil;
import com.jie.book.work.utils.MiscUtils;
import com.jie.book.work.utils.StringUtil;
import com.jie.book.work.utils.TaskExecutor;
import com.jie.book.work.utils.UIHelper;

public class LocalReadingBoard extends View {
	private static final int MSG_HANDLER_SCUCCESS = 0;
	private static final int MSG_HANDLER_ERRO = 1;
	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private int mCanvasPaddingLeft;
	private int mCanvasPaddingTop;
	private int mCanvasPaddingBottom;
	private int mRenderWidth;
	private int mRenderHeight;
	private int mStatusBarHeight;
	private float mLayoutPositions[] = new float[4096];
	private LocalReaderActivity mReaderActivity;
	private LocalDocument mDoc;
	private ReadTheme readTheme;
	private Dialog dialog;
	private boolean needShowStartupPage;
	private Drawable bgDrawable;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_HANDLER_SCUCCESS:
				if (turnNextPage()) {
					forceRedraw(false);
					needShowStartupPage = false;
				}
				UIHelper.cancleProgressDialog(dialog);
				break;
			case MSG_HANDLER_ERRO:
				UIHelper.cancleProgressDialog(dialog);
				mReaderActivity.showToast("无法处理文件！");
				mReaderActivity.finishInAnim();
				break;
			}
		}

	};

	@SuppressWarnings("deprecation")
	public LocalReadingBoard(Context context, AttributeSet attrs) throws Exception {
		super(context, attrs);
		Resources res = getResources();
		mReaderActivity = (LocalReaderActivity) context;
		mStatusBarHeight = res.getDimensionPixelSize(R.dimen.reading_board_status_bar_height);
		mCanvasPaddingLeft = res.getDimensionPixelSize(R.dimen.reading_board_canvas_padding_left);
		mCanvasPaddingTop = res.getDimensionPixelSize(R.dimen.reading_board_canvas_padding_top);
		mCanvasPaddingBottom = res.getDimensionPixelSize(R.dimen.reading_board_canvas_padding_bottom);
		mPaint.setTextSize(Cookies.getReadSetting().getTextSize());
		mDragPageShadowWidth = (int) res.getDimension(R.dimen.reading_board_page_shadow_width);
		mDragPageShadow = (NinePatchDrawable) res.getDrawable(R.drawable.reading_board_page_shadow);
		mRenderWidth = UIHelper.getScreenPixWidth(context) - mCanvasPaddingLeft * 2;
		getReaderHeight();
		readTheme = mReaderActivity.getReadThemeList().get(Cookies.getReadSetting().getThemeIndex());
	}

	private void getReaderHeight() {
		if (Cookies.getUserSetting().isReadScreenFull()) {
			mRenderHeight = UIHelper.getScreenPixHeight(mReaderActivity) - mCanvasPaddingTop - mCanvasPaddingBottom
					- mStatusBarHeight;
		} else {
			mRenderHeight = UIHelper.getScreenPixHeight(mReaderActivity) - UIHelper.getStatusHeight(mReaderActivity)
					- mCanvasPaddingTop - mCanvasPaddingBottom - mStatusBarHeight;
		}
	}

	@SuppressWarnings("deprecation")
	public void setDocument(LocalDocument doc) {
		Display display = ((WindowManager) mReaderActivity.getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		mRenderWidth = display.getWidth() - mCanvasPaddingLeft * 2;
		mRenderHeight = display.getHeight() - mCanvasPaddingTop - mCanvasPaddingBottom - mStatusBarHeight;
		mDoc = doc.initDocument(mRenderWidth, mRenderHeight, mPaint);
		dialog = UIHelper.showProgressDialog(dialog, mReaderActivity);
		TaskExecutor.getInstance().executeTask(new Runnable() {

			@Override
			public void run() {
				int retryCount = 0;
				do {
					if (mDoc.watchingToRead()) {
						mHandler.sendEmptyMessage(MSG_HANDLER_SCUCCESS);
						return;
					}
					if (mDoc.isAlwayWaitProcess()) {
						mHandler.sendEmptyMessage(MSG_HANDLER_ERRO);
						return;
					}
					if (mDoc.getChapterCount() == 0 && ++retryCount == 40) {
						mHandler.sendEmptyMessage(MSG_HANDLER_ERRO);
						return;
					}
					try {
						Thread.sleep(100);
					} catch (Exception e) {
					}
				} while (true);
			}
		});
		needShowStartupPage = mDoc.canRead();

	}

	// 设置广告阅读模式
	public void setAdMode() {
		Resources res = getResources();
		mCanvasPaddingBottom = res.getDimensionPixelSize(R.dimen.reading_board_canvas_padding_bottom_ad);
		mRenderWidth = UIHelper.getScreenPixWidth(mReaderActivity) - mCanvasPaddingLeft * 2;
		getReaderHeight();
		mDoc.restRender(mRenderWidth, mRenderHeight);
		drawCurrentPageContent();
	}

	// 设置正常阅读模式
	public void setNormalMode() {
		Resources res = getResources();
		mCanvasPaddingBottom = res.getDimensionPixelSize(R.dimen.reading_board_canvas_padding_bottom);
		mRenderWidth = UIHelper.getScreenPixWidth(mReaderActivity) - mCanvasPaddingLeft * 2;
		getReaderHeight();
		mDoc.restRender(mRenderWidth, mRenderHeight);
		drawCurrentPageContent();
		invalidate();
	}

	@SuppressWarnings("deprecation")
	protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
		if (oldWidth == 0 || width == oldWidth)
			return;
		super.onSizeChanged(width, height, oldWidth, oldHeight);

		Display display = ((WindowManager) mReaderActivity.getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		mRenderWidth = display.getWidth() - mCanvasPaddingLeft * 2;
		mRenderHeight = display.getHeight() - mCanvasPaddingTop - mCanvasPaddingBottom - mStatusBarHeight;
		mDoc.switchOrientation(mRenderWidth, mRenderHeight);
		mMainPageBitmap.recycle();
		mBackupPageBitmap.recycle();
		resetPageBitmapDrawable(width, height);
		System.gc();
	}

	public boolean adjustReadingProgressByPrecent(float percentage) {
		needShowStartupPage = false;
		if (mDoc.adjustReadingProgressByPrecent(percentage)) {
			forceRedraw(false);
			return true;
		}
		return false;
	}

	public void adjustReadingProgressByPosition(long beginPosition) {
		needShowStartupPage = false;
		if (mDoc.adjustReadingProgressByPosition(beginPosition)) {
			forceRedraw(false);
		}
	}

	public float calculateReadingProgress() {
		if (needShowStartupPage)
			return 0;
		float percentage = mDoc.calculateReadingProgress();
		return percentage > 100 ? 100 : percentage;
	}

	public boolean adjustTextSize(int adjustSize) {
		if ((Cookies.getReadSetting().getTextSize() <= ReadSettings.MIN_TEXT_SIZE && adjustSize < 0)
				|| (Cookies.getReadSetting().getTextSize() >= ReadSettings.MAX_TEXT_SIZE && adjustSize > 0)) {
			return false;
		}
		setTextSize(Cookies.getReadSetting().getTextSize() + adjustSize);
		forceRedraw(false);
		return true;
	}

	public void setTextSize(int textSize) {
		Cookies.getReadSetting().setTextSize(textSize);
		if (mPaint.getTextSize() != textSize) {
			mPaint.setTextSize(textSize);
			mDoc.onResetTextSize();
		}
	}

	private Bitmap mBackupPageBitmap;
	private Bitmap mMainPageBitmap;
	private Canvas mDrawableCanvas;

	private void resetPageBitmapDrawable(int width, int height) {
		mBackupPageBitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
		mMainPageBitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
		mDrawableCanvas = new Canvas();
		drawCurrentPageContent();
	}

	protected void onDraw(Canvas canvas) {
		if (mBackupPageBitmap == null)
			resetPageBitmapDrawable(canvas.getWidth(), canvas.getHeight());
		if (Cookies.getUserSetting().isReadAnim()) {
			drawSmoothSlidePage(canvas);
		} else {
			canvas.drawBitmap(mMainPageBitmap, 0, 0, null);
		}
	}

	private FontMetrics mFontMetrics = new FontMetrics();

	@SuppressWarnings("deprecation")
	private void drawCurrentPageContent() {
		if (mBackupPageBitmap == null)
			return;
		mPaint.getFontMetrics(mFontMetrics);
		// 设置具体画布
		mDrawableCanvas.setBitmap(mBackupPageBitmap);
		mDrawableCanvas.drawBitmap(mMainPageBitmap, 0, 0, null);
		// 画图，并设置为背景
		mDrawableCanvas.setBitmap(mMainPageBitmap);
		Rect rect = RectObjectPool.getObject();
		getDrawingRect(rect);
		bgDrawable = readTheme.getReadingDrawable(mReaderActivity);
		bgDrawable.setBounds(rect);
		bgDrawable.draw(mDrawableCanvas);
		RectObjectPool.freeObject(rect);
		mPaint.setColor(readTheme.getTextColorRes());
		mDrawableCanvas.save(Canvas.MATRIX_SAVE_FLAG);
		// 绘制拖动的页面
		mDrawableCanvas.translate(mCanvasPaddingLeft, mCanvasPaddingTop - mFontMetrics.ascent);
		mDoc.prepareGetLines();
		float textHeight = mDoc.getTextHeight();
		StringBuilder sb = MiscUtils.getThreadSafeStringBuilder();
		float contentHeight = 0;
		byte flags = 0;
		while (true) {
			flags = mDoc.getNextLine(sb);
			if (flags == 0)
				break;
			if (Cookies.getUserSetting().getUserTypeface().equals(TypefaceActivity.TYPEPFACE_FT)) {// 繁体
				String content = sb.toString();
				sb.delete(0, sb.length());
				sb.append(StringUtil.changeTraditional(content));
			}
			if ((flags & Document.GET_NEXT_LINE_FLAG_SHOULD_JUSTIFY) > 0) {
				LayoutUtil.layoutTextJustified(sb, mRenderWidth, mPaint, 0, contentHeight, mLayoutPositions);
				mDrawableCanvas.drawPosText(sb.toString(), mLayoutPositions, mPaint);
			} else {
				mDrawableCanvas.drawText(sb, 0, sb.length(), 0, contentHeight, mPaint);
			}
			sb.delete(0, sb.length());
			contentHeight += textHeight;
			if ((flags & Document.GET_NEXT_LINE_FLAG_PARAGRAPH_ENDS) > 0)
				contentHeight += Cookies.getReadSetting().getParagraphSpacing();
			else
				contentHeight += Cookies.getReadSetting().getLineSpacing();
		}
		mDrawableCanvas.restore();
		if (mReaderActivity.getBookCase() != null && mReaderActivity.getDoc() != null) {
			mReaderActivity.getReadPanle().drawReadPanle(mReaderActivity.getDoc().getCurrentChapterTitle(),
					mReaderActivity.getBookCase().getProgress() + "%");
		}
	}

	public boolean turnPreviousPage() {
		mTurningPageDirection = 0;
		if (mDoc.turnPreviousPage()) {
			return true;
		}
		if (mDoc.hasPreviousChapter())
			return false;
		if (!needShowStartupPage) {
			needShowStartupPage = true;
			return true;
		}
		return false;
	}

	public boolean turnNextPage() {
		mTurningPageDirection = 1;

		if (needShowStartupPage) {
			needShowStartupPage = false;
			return true;
		}
		if (mDoc.turnNextPage()) {
			return true;
		}
		return false;
	}

	public boolean turnPageByDirection() {
		if (mTurningPageDirection == 0)
			return turnPreviousPage();
		return turnNextPage();
	}

	public boolean turnPreviousPageAndRedraw() {
		if (turnPreviousPage()) {
			forceRedraw(true);
			return true;
		}
		return false;
	}

	public boolean turnNextPageAndRedraw() {
		if (turnNextPage()) {
			forceRedraw(true);
			return true;
		}
		return false;
	}

	public boolean turnNextChapter() {
		if (needShowStartupPage && turnNextPage()) {
			forceRedraw(false);
			return true;
		}

		if (mDoc.turnNextChapter()) {
			forceRedraw(false);
			return true;
		}
		return false;
	}

	public boolean turnPreviousChapter() {
		if (mDoc.turnPreviousChapter()) {
			needShowStartupPage = false;
			forceRedraw(false);
			return true;
		}
		return false;
	}

	public boolean hasNextChapter() {
		return mDoc.hasNextChapter();
	}

	public boolean hasPreviousChapter() {
		return mDoc.hasPreviousChapter();
	}

	public LocalBookCatalog getCurrentReadingChapter() {
		return mDoc.getCurrentReadingChapter();
	}

	public List<LocalBookCatalog> getDocChapterList() {
		return mDoc.getChapterList();
	}

	public void mergeAdditionalChapter(List<LocalBookCatalog> additionalChapterList) {
		mDoc.mergeAdditionalChapter(additionalChapterList);
	}

	public void forceRedraw(boolean applyEffect) {
		if (applyEffect && Cookies.getUserSetting().isReadAnim()) {
			mAutoSlide = true;
			resetDraggingBitmapX();
		}
		calculateReadingPosition();
		drawCurrentPageContent();
		invalidate();
	}

	private int mTurningPageDirection;

	private boolean mAutoSlide;
	private boolean mDragging;

	public boolean isDragging() {
		return mDragging;
	}

	public void changeTurningPageDirection(int pageDirection) {
		mTurningPageDirection = pageDirection;
	}

	private int mStartDraggingDirection;
	private float mDraggingBitmapX;
	private NinePatchDrawable mDragPageShadow;
	private int mDragPageShadowWidth;

	public void startDragging() {
		mDragging = true;
		drawCurrentPageContent();
		resetDraggingBitmapX();
		mStartDraggingDirection = mTurningPageDirection;
	}

	private void resetDraggingBitmapX() {
		mDraggingBitmapX = mTurningPageDirection == 1 ? 0 : -mDrawableCanvas.getWidth();
	}

	public void dragRedraw(float pixels) {
		pixels = Math.abs(pixels);
		if (mTurningPageDirection == 1) {
			mDraggingBitmapX -= pixels;
			if (mDraggingBitmapX < -mDrawableCanvas.getWidth())
				mDraggingBitmapX = -mDrawableCanvas.getWidth();
		} else {
			mDraggingBitmapX += pixels;
			if (mDraggingBitmapX > 0)
				mDraggingBitmapX = 0;
		}
		invalidate();
	}

	public void slideSmoothly(float velocityX) {
		mDragging = false;
		mAutoSlide = true;

		// adjust page direction
		if (velocityX > 0) { // fling from left to right
			mTurningPageDirection = 0;
		} else if (velocityX < 0) { // fling from right to left
			mTurningPageDirection = 1;
		} else {
			if (mStartDraggingDirection == 1) {
				mTurningPageDirection = Math.abs(mDraggingBitmapX) > mDrawableCanvas.getWidth() * 0.2 ? 1 : 0;
			} else {
				mTurningPageDirection = mDrawableCanvas.getWidth() - Math.abs(mDraggingBitmapX) > mDrawableCanvas
						.getWidth() * 0.2 ? 0 : 1;
			}
		}

		if (mStartDraggingDirection != mTurningPageDirection) {
			turnPageByDirection();
			drawCurrentPageContent();
		}

		invalidate();
	}

	private void drawSmoothSlidePage(Canvas canvas) {
		if (mAutoSlide) {
			mAutoSlide = autoSlideDraw(canvas);
			if (!mAutoSlide)
				mDraggingBitmapX = 0;
		} else {
			staticDragDraw(canvas);
		}
	}

	private static float DIVIDER = 2f;

	private boolean autoSlideDraw(Canvas canvas) {
		if (mTurningPageDirection == 1) {
			float dragDistance = canvas.getWidth() + mDraggingBitmapX;
			dragDistance /= dragDistance < DIVIDER ? dragDistance / 2 : DIVIDER;
			mDraggingBitmapX -= dragDistance;

			canvas.drawBitmap(mMainPageBitmap, 0, 0, null);
			if (mDraggingBitmapX > -canvas.getWidth()) {
				canvas.drawBitmap(mBackupPageBitmap, mDraggingBitmapX, 0, null);
				showPageShadow(canvas);
				invalidate();
				return true;
			}
		} else {
			float dragDistance = Math.abs(mDraggingBitmapX);
			dragDistance /= dragDistance < DIVIDER ? dragDistance / 2 : DIVIDER;
			mDraggingBitmapX += dragDistance;

			if (mDraggingBitmapX < 0) {
				canvas.drawBitmap(mBackupPageBitmap, 0, 0, null);
				canvas.drawBitmap(mMainPageBitmap, mDraggingBitmapX, 0, null);
				showPageShadow(canvas);
				invalidate();
				return true;
			}
			canvas.drawBitmap(mMainPageBitmap, 0, 0, null);
		}
		return false;
	}

	private void staticDragDraw(Canvas canvas) {
		// show其他阅读组件时，ReadingBoard的OnDraw会被调用，这里做处理
		if (mDraggingBitmapX == 0) {
			canvas.drawBitmap(mDragging ? mBackupPageBitmap : mMainPageBitmap, 0, 0, null);
			return;
		}

		// 用户一开始想向后翻页(下一页)，此时，mainBitmap居底，backupBitmap居顶并滑动，座标从0开始呈递减趋势
		if (mStartDraggingDirection == 1) {
			// 当用户在滑动过程中改变方向时，backupBitmap的座标最终有可能会复位到0(不会大于，因为上面会判断)
			// 当backupBitmap被完全移入屏幕时，没必要再显示mainBitmap及阴影
			if (mDraggingBitmapX < 0) {
				canvas.drawBitmap(mMainPageBitmap, 0, 0, null);
				showPageShadow(canvas);
			}
			canvas.drawBitmap(mBackupPageBitmap, mDraggingBitmapX, 0, null);
			// invalidate();
		}
		// 用户一开始想向前翻页(上一页)，此时，backupBitmap居底，mainBitmap居顶并滑动，座标从-canvasWidth开始呈递增趋势
		else {
			canvas.drawBitmap(mBackupPageBitmap, 0, 0, null);
			// 当用户在滑动过程中改变方向时，mainBitmap的座标最终有可能会复位到-canvasWidth(不会小于，因为上面会判断)
			// 当mainBitmap被完全移出屏幕时，没必要再显示mainBitmap及阴影
			if (mDraggingBitmapX > -canvas.getWidth()) {
				canvas.drawBitmap(mMainPageBitmap, mDraggingBitmapX, 0, null);
				showPageShadow(canvas);
			}
			// invalidate();
		}
	}

	private void showPageShadow(Canvas canvas) {
		int left = (int) (canvas.getWidth() - Math.abs(mDraggingBitmapX));
		mDragPageShadow.setBounds(left, 0, left + mDragPageShadowWidth, canvas.getHeight());
		mDragPageShadow.draw(canvas);
	}

	public String addBookmark() {
		long beginPosition = mDoc.getPageBeginPosition();
		int chapterIndex = mDoc.getReadingChapterIndex();
		String summary = StringUtil.removeEmptyChar(mDoc.getCurrentPageFrontText(40));
		if (summary == null || summary.length() == 0)
			return null;
		if (mDoc.getChapterList().size() > 0 && mDoc.getChapterList().size() > chapterIndex) {
			LocalBookManager.saveBookMark(mReaderActivity.getBookCase(), mDoc.getChapterList().get(chapterIndex),
					beginPosition, summary);
		}
		return summary;
	}

	private boolean mBookReadable;

	public boolean bookIsReadable() {
		return mBookReadable;
	}

	public void calculateReadingPosition() {
		if (mDoc != null) {
			mReaderActivity.getBookCase().setProgress((int) calculateReadingProgress());
			mDoc.calculatePagePosition();
			mReaderActivity.getBookCase().setLastReadPosition(mDoc.getPageBeginPosition());
			mReaderActivity.getBookCase().setLastReadIndex(mDoc.getReadingChapterIndex());
		}
	}

	public void onStop() {
		mDoc.onStop();
	}

	public void setTypeface(Typeface tf, boolean needRedraw) {
		mPaint.setTypeface(tf);
		if (needRedraw) {
			forceRedraw(false);
		}
	}

	public void reSetLineSpace() {
		mDoc.restLineSpace(Cookies.getReadSetting().getLineSpacing());
		drawCurrentPageContent();
	}

	public boolean setColorScheme(ReadTheme colorScheme, boolean needRedraw) {
		if (readTheme != colorScheme) {
			readTheme = colorScheme;
			mPaint.setColor(colorScheme.getTextColorRes());
			if (needRedraw) {
				forceRedraw(false);
				return true;
			}
		}
		return false;
	}

	public void onDestroy() {
		BitmapUtil.recyledBitmap(mBackupPageBitmap);
		BitmapUtil.recyledBitmap(mMainPageBitmap);
		// BitmapUtil.recyledDrawable(bgDrawable);
	}

}
