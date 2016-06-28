package com.jie.book.work.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

import com.jie.book.work.R;
import com.jie.book.work.activity.BookMainActivity;
import com.jie.book.work.activity.MyInfoActivity;
import com.jie.book.work.activity.SetMoreActivity;
import com.jie.book.work.activity.SetSystemActivity;
import com.jie.book.work.activity.SquareActivity;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.entity.UserInfo;
import com.jie.book.work.utils.ImageLoadUtil;
import com.jie.book.work.utils.LoginUtil;
import com.jie.book.work.utils.LoginUtil.onSignUpListener;
import com.jie.book.work.utils.StatisticUtil;

public class BookMainMenuFragment extends BaseFragment implements OnClickListener {
	private ImageView ivIcon;
	private TextView tvName;
	private LoginUtil loginUtil;
	private RelativeLayout rootLayout;
	private View contentView;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.view_book_shelf_menu, null);
	}

	@SuppressLint("InflateParams")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	protected void initUI() {
		rootLayout = (RelativeLayout) activity.findViewById(R.id.book_shelf_menu_root);
		loginUtil = new LoginUtil(activity);
		SwitchModel();
	}

	@Override
	protected void initData() {
		setUserInfo();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.book_shelf_notifi:
			StatisticUtil.sendEvent(activity, StatisticUtil.SET_NOTIFI);
			SetSystemActivity.luanch(activity);
			break;
		case R.id.book_shelf_square:
			SquareActivity.luanch(activity);
			break;
		case R.id.book_shelf_more:
			SetMoreActivity.luanch(activity);
			break;
		case R.id.book_shelf_head:
			if (loginUtil.isLogin()) {
				MyInfoActivity.luanch(activity);
			} else {
				loginUtil.showlogin(new onSignUpListener() {

					@Override
					public void onSignUp() {
						setUserInfo();
						BookMainActivity.getInstance().getDataAfterSync();
					}
				});
			}
			break;
		}
	}

	public void setUserInfo() {
		UserInfo userInfo = loginUtil.getUserInfo();
		if (userInfo != null) {
			tvName.setText(userInfo.getUserName());
			ImageLoadUtil.loadRoundImage(ivIcon, R.drawable.icon_menu_head, userInfo.getUserIcon());
		}
	}

	public void setLogout() {
		tvName.setText("未登录");
		ivIcon.setImageResource(R.drawable.icon_menu_head);
	}

	public void SwitchModel() {
		rootLayout.removeAllViews();
		contentView = LayoutInflater.from(activity).inflate(
				Cookies.getReadSetting().isNightMode() ? R.layout.view_book_shelf_menu_content_ef
						: R.layout.view_book_shelf_menu_content, null);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		rootLayout.addView(contentView, params);
		activity.findViewById(R.id.book_shelf_notifi).setOnClickListener(this);
		activity.findViewById(R.id.book_shelf_square).setOnClickListener(this);
		activity.findViewById(R.id.book_shelf_more).setOnClickListener(this);
		activity.findViewById(R.id.book_shelf_head).setOnClickListener(this);
		ivIcon = (ImageView) activity.findViewById(R.id.book_shelf_head);
		tvName = (TextView) activity.findViewById(R.id.book_shelf_name);
		setUserInfo();
	}
}
