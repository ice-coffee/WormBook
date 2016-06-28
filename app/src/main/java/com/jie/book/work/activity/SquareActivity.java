package com.jie.book.work.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.baidu.ops.appunion.sdk.AppUnionSDK;
import com.jie.book.work.R;
import com.jie.book.work.application.BookActivityManager;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.game.Game2048Activity;
import com.jie.book.work.utils.Config;
import com.jie.book.work.utils.SharedPreferenceUtil;
import com.jie.book.work.utils.UIHelper;

public class SquareActivity extends BaseActivity implements OnClickListener {
	private View viewAppList;
	private SharedPreferenceUtil spUtil;

	public static void luanch(Activity content) {
		Intent intent = new Intent();
		intent.setClass(content, SquareActivity.class);
		BookActivityManager.getInstance().goTo(content, intent);
	}

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(Cookies.getReadSetting().isNightMode() ? R.layout.act_square_ef : R.layout.act_square);
		UIHelper.setTranStateBar(activity);
		AppUnionSDK.getInstance(this).initSdk();
		initUI();
		initListener();
	}

	@Override
	protected void initUI() {
		spUtil = SharedPreferenceUtil.getInstance(activity);
		viewAppList = findViewById(R.id.square_app);
        int isOpenApp = spUtil.getInt(Config.OPEN_APP_LIST, Config.OPEN_APP_LIST_DEFAULT);
		viewAppList.setVisibility(isOpenApp == 1 ? View.VISIBLE : View.GONE);
	}

	@Override
	protected void initData() {
	}

	@Override
	protected void initListener() {
		findViewById(R.id.book_search_back).setOnClickListener(this);
		findViewById(R.id.square_2048).setOnClickListener(this);
		findViewById(R.id.square_random).setOnClickListener(this);
		findViewById(R.id.square_app).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.book_search_back:
			finishInAnim();
			break;
		case R.id.square_2048:
			Game2048Activity.luanch(activity);
			break;
		case R.id.square_random:
			RandomReadActivity.luanch(activity, 1);
			break;
		case R.id.square_app:
			AppUnionSDK.getInstance(activity).showAppList();
			break;
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		initUI();
	}
}