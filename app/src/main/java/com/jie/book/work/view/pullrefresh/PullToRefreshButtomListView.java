package com.jie.book.work.view.pullrefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jie.book.work.R;
import com.jie.book.work.application.BookApplication.Cookies;

public class PullToRefreshButtomListView extends PullToRefreshListView implements
		PullToRefreshBase.OnLastItemVisibleListener {
	static final Interpolator ANIMATION_INTERPOLATOR = new LinearInterpolator();
	static final int ROTATION_ANIMATION_DURATION = 1200;
	private View footView;
	private ImageView footImage;
	private Animation mRotateAnimation;
	private onRefreshLoadListener listener;
	private boolean canLoadMore = true;
	private boolean isRefersh = true;
	private View rootView;
	private TextView pullText;

	public PullToRefreshButtomListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PullToRefreshButtomListView(Context context, PullToRefreshBase.Mode mode,
			PullToRefreshBase.AnimationStyle style) {
		super(context, mode, style);
		init(context);
	}

	public PullToRefreshButtomListView(Context context, PullToRefreshBase.Mode mode) {
		super(context, mode);
		init(context);
	}

	public PullToRefreshButtomListView(Context context) {
		super(context);
		init(context);
	}

	public void setonRefreshLoadListener(onRefreshLoadListener listener) {
		this.listener = listener;
	}

	@Override
	public void onLastItemVisible() {
		if (canLoadMore) {
			isRefersh = false;
			footView.setVisibility(View.VISIBLE);
			footImage.startAnimation(mRotateAnimation);
			listener.onLoadMore();
		}
	}

	public void setCanLoadMore(boolean canLoadMore) {
		this.canLoadMore = canLoadMore;
		if (!canLoadMore)
			footView.setVisibility(View.GONE);
	}

	public boolean isRefersh() {
		return isRefersh;
	}

	public void setRefersh(boolean isRefersh) {
		this.isRefersh = isRefersh;
	}

	private void init(Context context) {
		ListView cotentListView = getRefreshableView();
		mRotateAnimation = new RotateAnimation(0, 720, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
		mRotateAnimation.setDuration(ROTATION_ANIMATION_DURATION);
		mRotateAnimation.setRepeatCount(Animation.INFINITE);
		mRotateAnimation.setRepeatMode(Animation.RESTART);
		footView = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_buttom, null);
		footImage = (ImageView) footView.findViewById(R.id.pullrefresh_buttom_image);
		rootView = footView.findViewById(R.id.pullrefresh_buttom);
		pullText = (TextView) footView.findViewById(R.id.pullrefresh_buttom_text);
		SwitchModel();
		footView.setVisibility(View.GONE);
		cotentListView.addFooterView(footView);
		setOnLastItemVisibleListener(this);
		setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (listener != null) {
					isRefersh = true;
					setCanLoadMore(true);
					listener.onRefreshing();
				}
			}
		});
	}

	public void SwitchModel() {
		if (Cookies.getReadSetting().isNightMode()) {
			pullText.setTextColor(getResources().getColor(R.color.text_ef_light_gray));
			rootView.setBackgroundResource(R.drawable.selector_btn_pullrefreshing_buttom_ef);
		} else {
			pullText.setTextColor(getResources().getColor(R.color.text_pullrefreshing_buttom));
			rootView.setBackgroundResource(R.drawable.selector_btn_pullrefreshing_buttom);
		}
	}

	public interface onRefreshLoadListener {
		public void onLoadMore();

		public void onRefreshing();
	}

	public void onRefreshCompleteAll() {
		onRefreshComplete();
		footView.setVisibility(View.GONE);
	}

}
