package com.jie.book.work.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jie.book.work.R;
import com.jie.book.work.utils.StringUtil;

public class EmptyPage extends RelativeLayout {
	static final Interpolator ANIMATION_INTERPOLATOR = new LinearInterpolator();
	static final int ROTATION_ANIMATION_DURATION = 600;
	private onReLoadListener listener;
	private ImageView emptyImage;
	private TextView emptyTextview;
	private Animation mRotateAnimation;
	private boolean isRefreshing;
	private String content;

	public EmptyPage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public EmptyPage(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public EmptyPage(Context context) {
		super(context);
		init(context);
	}

	public void setOnReLoadListener(onReLoadListener listener) {
		this.listener = listener;
	}

	public void setTextView(String content) {
		this.content = content;
		emptyTextview.setText(content);
	}

	private void init(Context context) {
		mRotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
		mRotateAnimation.setDuration(ROTATION_ANIMATION_DURATION);
		mRotateAnimation.setRepeatCount(Animation.INFINITE);
		mRotateAnimation.setRepeatMode(Animation.RESTART);
		View emptylayout = LayoutInflater.from(context).inflate(R.layout.view_empty_page, null);
		emptyImage = (ImageView) emptylayout.findViewById(R.id.empty_page_image);
		emptyTextview = (TextView) emptylayout.findViewById(R.id.empty_page_text);
		LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		param.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		addView(emptylayout, param);
		emptylayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return isRefreshing;
			}
		});
		emptylayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (listener != null) {
					isRefreshing = true;
					emptyTextview.setText("加载中...");
					emptyImage.startAnimation(mRotateAnimation);
					listener.onload();
				}
			}
		});
	}

	public interface onReLoadListener {
		public void onload();
	}

	public void onReloadComplete() {
		isRefreshing = false;
		emptyImage.clearAnimation();
		emptyTextview.setText(StringUtil.isEmpty(content) ? "点击重新加载" : content);
	}
}
