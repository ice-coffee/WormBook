package com.jie.book.work.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.jie.book.work.R;
import com.jie.book.work.application.BookActivityManager;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.utils.UIHelper;

public class DisclaimerActivity extends BaseActivity implements OnClickListener {

	public static void luanch(Activity content) {
		Intent intent = new Intent();
		intent.setClass(content, DisclaimerActivity.class);
		BookActivityManager.getInstance().goTo(content, intent);
	}

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(Cookies.getReadSetting().isNightMode() ? R.layout.act_set_disclaimer_ef
				: R.layout.act_set_disclaimer);
		UIHelper.setTranStateBar(activity);
		initListener();
	}

	@Override
	protected void initUI() {
	}

	@Override
	protected void initData() {
	}

	@Override
	protected void initListener() {
		findViewById(R.id.book_search_back).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.book_search_back:
			finishInAnim();
			break;
		}
	}

}