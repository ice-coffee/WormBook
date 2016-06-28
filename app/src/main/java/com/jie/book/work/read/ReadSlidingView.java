package com.jie.book.work.read;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;

//上层可以的拖动的View
public class ReadSlidingView extends LinearLayout {
	public static final int ANIM_TIME = 350;
	private SlidingStopListener listener;
	private Scroller mScroller;
	private boolean isMove = false;

	public ReadSlidingView(Context context) {
		this(context, null);
		init(context);
	}

	public ReadSlidingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mScroller = new Scroller(context, new LinearInterpolator());
		setOrientation(LinearLayout.HORIZONTAL);
	}

	public void setSlidingStopListener(SlidingStopListener listener) {
		this.listener = listener;
	}

	// 获取当前的X轴位置
	public float getXposition() {
		return mScroller.getCurrX();
	}

	// 获取X轴最终位置
	public float getXFinal() {
		return mScroller.getFinalX();
	}

	public boolean isMove() {
		return isMove;
	}

	// 立刻滑到某个位置
	public void scrollToNow(int fx, int fy) {
		int dx = fx - mScroller.getFinalX();
		int dy = fy - mScroller.getFinalY();
		scrollByNow(dx, dy);
	}

	// 一定时间内滑到某个位置
	public void scrollToTime(int fx, int fy) {
		int dx = fx - mScroller.getFinalX();
		int dy = fy - mScroller.getFinalY();
		scrollByTime(dx, dy);
	}

	// 立刻滑动某个距离
	public void scrollByNow(int dx, int dy) {
		mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy, 0);
		invalidate();
	}

	// 一定时间内滑动某个距离
	public void scrollByTime(int dx, int dy) {
		mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy, ANIM_TIME);
		invalidate();
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			isMove = true;
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		} else {
			// 滑动停止
			isMove = false;
			if (listener != null) {
				listener.onSlidingStop();
			}
		}
		super.computeScroll();
	}

	// 监听滑动停止
	public interface SlidingStopListener {
		public void onSlidingStop();
	}

}