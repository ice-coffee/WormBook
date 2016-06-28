package com.jie.book.work.read;

import android.annotation.SuppressLint;
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
import android.util.AttributeSet;
import android.view.View;

import com.bond.bookcatch.vo.BookCatalog;
import com.bond.bookcatch.vo.BookDesc;
import com.jie.book.work.R;
import com.jie.book.work.activity.ReadActivity;
import com.jie.book.work.activity.TypefaceActivity;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.utils.BitmapUtil;
import com.jie.book.work.utils.MiscUtils;
import com.jie.book.work.utils.StringUtil;
import com.jie.book.work.utils.UIHelper;

public class ReadingBoard extends View {
	private ReadActivity activity;
	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private int mCanvasPaddingLeft;
	private int mCanvasPaddingTop;
	private int mCanvasPaddingBottom;
	private int mRenderWidth;
	private int mRenderHeight;
	private int mStatusBarHeight;
	private float mLayoutPositions[] = new float[4096];
	private Bitmap mBackupPageBitmap;// 回退的页面
	private Bitmap mMainPageBitmap;// 主页面
	private Canvas mDrawableCanvas;
	private Document doc;
	private int mTurningPageDirection;// 0上一页，1下一页
	private boolean mAutoSlide;
	private boolean mDragging;
	private int mStartDraggingDirection;// 开始的滑动方向
	private float mDraggingBitmapX;
	private NinePatchDrawable mDragPageShadow;
	private int mDragPageShadowWidth;
	private ReadTheme readTheme;
	private Drawable bgDrawable;

	public ReadingBoard(Context context, AttributeSet attrs) throws Exception {
		super(context, attrs);
		activity = (ReadActivity) context;
		Resources res = getResources();
		// 底部状态栏的高度
		mStatusBarHeight = res.getDimensionPixelSize(R.dimen.reading_board_status_bar_height);
		// 左边的间隔
		mCanvasPaddingLeft = res.getDimensionPixelSize(R.dimen.reading_board_canvas_padding_left);
		// 与顶部的间隔
		mCanvasPaddingTop = res.getDimensionPixelSize(R.dimen.reading_board_canvas_padding_top);
		// 与底部的间隔
		mCanvasPaddingBottom = res.getDimensionPixelSize(R.dimen.reading_board_canvas_padding_bottom);
		mPaint.setTextSize(Cookies.getReadSetting().getTextSize());
		// 阴影的宽度
		mDragPageShadowWidth = (int) res.getDimension(R.dimen.reading_board_page_shadow_width);
		mDragPageShadow = (NinePatchDrawable) res.getDrawable(R.drawable.reading_board_page_shadow);
		mRenderWidth = UIHelper.getScreenPixWidth(context) - mCanvasPaddingLeft * 2;
		getReaderHeight();
		readTheme = activity.getReadThemeList().get(Cookies.getReadSetting().getThemeIndex());
		doc = new Document((ReadActivity) context, Cookies.getReadSetting().getLineSpacing(), Cookies.getReadSetting()
				.getParagraphSpacing(), mRenderWidth, mRenderHeight, mPaint);
		activity.setDoc(doc);
	}

	// 加载方式chapterId -1从缓存章节加载，其他，从某个位置开始加载
	public void init(BookDesc bookDesc, BookCatalog chapter) {
		doc.cleanAll();
		doc.setLastReadOffline(bookDesc.getLastReadOffline());
		doc.setBeaginPosition((int) bookDesc.getLastReadPosition());
		doc.initDocument(bookDesc, chapter);
	}

	// 横屏的处理
	protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
		if (oldWidth == 0 || width == oldWidth)
			return;
		super.onSizeChanged(width, height, oldWidth, oldHeight);
		mRenderWidth = UIHelper.getScreenPixWidth(activity) - mCanvasPaddingLeft * 2;
		getReaderHeight();
		doc.restRender(mRenderWidth, mRenderHeight);
		BitmapUtil.recyledBitmap(mMainPageBitmap);
		BitmapUtil.recyledBitmap(mBackupPageBitmap);
		resetPageBitmapDrawable(width, height);
		System.gc();
	}

	// 设置广告阅读模式
	public void setAdMode() {
		Resources res = getResources();
		mCanvasPaddingBottom = res.getDimensionPixelSize(R.dimen.reading_board_canvas_padding_bottom_ad);
		mRenderWidth = UIHelper.getScreenPixWidth(activity) - mCanvasPaddingLeft * 2;
		getReaderHeight();
		doc.restRender(mRenderWidth, mRenderHeight);
		drawCurrentPageContent();
	}

	// 设置正常阅读模式
	public void setNormalMode() {
		Resources res = getResources();
		mCanvasPaddingBottom = res.getDimensionPixelSize(R.dimen.reading_board_canvas_padding_bottom);
		mRenderWidth = UIHelper.getScreenPixWidth(activity) - mCanvasPaddingLeft * 2;
		getReaderHeight();
		doc.restRender(mRenderWidth, mRenderHeight);
		drawCurrentPageContent();
		invalidate();
	}

	// 重新加载页面
	public void reSetLineSpace() {
		doc.restLineSpace(Cookies.getReadSetting().getLineSpacing());
		drawCurrentPageContent();
	}

	private void getReaderHeight() {
		if (Cookies.getUserSetting().isReadScreenFull()) {
			mRenderHeight = UIHelper.getScreenPixHeight(activity) - mCanvasPaddingTop - mCanvasPaddingBottom
					- mStatusBarHeight;
		} else {
			mRenderHeight = UIHelper.getScreenPixHeight(activity) - UIHelper.getStatusHeight(activity)
					- mCanvasPaddingTop - mCanvasPaddingBottom - mStatusBarHeight;
		}
	}

	// 设置字体大小
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
			doc.onResetTextSize();
		}
	}

	// 重新绘制界面
	@SuppressLint("NewApi")
	private void resetPageBitmapDrawable(int width, int height) {
		mBackupPageBitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
		mMainPageBitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
		mDrawableCanvas = new Canvas();
		drawCurrentPageContent();
		invalidate();
	}

	protected void onDraw(Canvas canvas) {
		if (mBackupPageBitmap == null)
			resetPageBitmapDrawable(canvas.getWidth(), canvas.getHeight());
		// 是否打开拖动效果
		if (Cookies.getUserSetting().isReadAnim()) {
			drawSmoothSlidePage(canvas);
		} else {
			canvas.drawBitmap(mMainPageBitmap, 0, 0, null);
		}
	}

	// 字体属性的测量工具
	private FontMetrics mFontMetrics = new FontMetrics();

	// 绘制当前页面
	@SuppressWarnings("deprecation")
	private void drawCurrentPageContent() {
		if (activity.isFinishing() || mBackupPageBitmap == null || mMainPageBitmap == null)
			return;
		mPaint.getFontMetrics(mFontMetrics);
		// 设置具体画布
		mDrawableCanvas.setBitmap(mBackupPageBitmap);
		mDrawableCanvas.drawBitmap(mMainPageBitmap, 0, 0, null);
		// 画图，并设置为背景
		mDrawableCanvas.setBitmap(mMainPageBitmap);
		Rect rect = RectObjectPool.getObject();
		getDrawingRect(rect);
		bgDrawable = readTheme.getReadingDrawable(activity);
		bgDrawable.setBounds(rect);
		bgDrawable.draw(mDrawableCanvas);
		RectObjectPool.freeObject(rect);
		mPaint.setColor(readTheme.getTextColorRes());
		mDrawableCanvas.save(Canvas.MATRIX_SAVE_FLAG);
		// 绘制拖动的页面
		mDrawableCanvas.translate(mCanvasPaddingLeft, mCanvasPaddingTop - mFontMetrics.ascent);
		doc.prepareGetLines();
		float textHeight = doc.getTextHeight();
		StringBuilder sb = MiscUtils.getThreadSafeStringBuilder();
		float contentHeight = 0;
		byte flags = 0;
		while (true) {
			flags = doc.getNextLine(sb);
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
		activity.getReadPanle().drawReadPanle(doc.getCurChapterName(), doc.getReadProgressStr());
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

	public void setTypeface(Typeface tf, boolean needRedraw) {
		mPaint.setTypeface(tf);
		if (needRedraw) {
			forceRedraw(false);
		}
	}

	public boolean turnPreviousPage() {
		mTurningPageDirection = 0;
		return doc.showPreviewPage();
	}

	public boolean turnNextPage() {
		mTurningPageDirection = 1;
		return doc.showNextPage();
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

	// 重新绘制界面,是否开启动画
	public void forceRedraw(boolean applyEffect) {
		if (applyEffect && Cookies.getUserSetting().isReadAnim()) {
			mAutoSlide = true;
			resetDraggingBitmapX();
		}
		drawCurrentPageContent();
		invalidate();
	}

	public boolean isDragging() {
		return mDragging;
	}

	// 改变滑动方向
	public void changeTurningPageDirection(int pageDirection) {
		mTurningPageDirection = pageDirection;
	}

	// 开始拖动
	public void startDragging() {
		mDragging = true;
		drawCurrentPageContent();
		resetDraggingBitmapX();
		mStartDraggingDirection = mTurningPageDirection;
	}

	// 重置拖动位置
	private void resetDraggingBitmapX() {
		mDraggingBitmapX = mTurningPageDirection == 1 ? 0 : -mDrawableCanvas.getWidth();
	}

	// 拖动
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

	// 自动拖动动画，放手后
	public void slideSmoothly(float velocityX) {
		mDragging = false;
		mAutoSlide = true;
		// 判断用户操作
		if (velocityX > 0) { // fling from left to right
			mTurningPageDirection = 0;
		} else if (velocityX < 0) { // fling from right to left
			mTurningPageDirection = 1;
		} else {
			if (mStartDraggingDirection == 1) {
				mTurningPageDirection = Math.abs(mDraggingBitmapX) > mDrawableCanvas.getWidth() * 0.02 ? 1 : 0;
			} else {
				mTurningPageDirection = mDrawableCanvas.getWidth() - Math.abs(mDraggingBitmapX) > mDrawableCanvas
						.getWidth() * 0.02 ? 0 : 1;
			}
		}

		if (mStartDraggingDirection != mTurningPageDirection) {
			turnPageByDirection();
			drawCurrentPageContent();
		}

		invalidate();
	}

	// 绘制拖动布局
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

	public void onDestroy() {
		BitmapUtil.recyledBitmap(mBackupPageBitmap);
		BitmapUtil.recyledBitmap(mMainPageBitmap);
		// BitmapUtil.recyledDrawable(bgDrawable);
	}
}
