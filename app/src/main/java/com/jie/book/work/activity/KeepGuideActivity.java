package com.jie.book.work.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.jie.book.work.R;
import com.jie.book.work.application.BookActivityManager;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.utils.UIHelper;

/**
 * 养肥区引导界面
 * 
 * @author cwj
 * 
 */
public class KeepGuideActivity extends BaseActivity implements OnClickListener {

	public static void luanch(Activity content) {
		Intent intent = new Intent();
		intent.setClass(content, KeepGuideActivity.class);
		BookActivityManager.getInstance().goTo(content, intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.act_keep_guide);
		UIHelper.setTranStateBar(activity);
		Cookies.getUserSetting().setFristKeep(false);
		initUI();
	}

	@Override
	protected void initUI() {
		findViewById(R.id.chapter_list_back).setOnClickListener(this);
		findViewById(R.id.keep_guide_begin).setOnClickListener(this);
	}

	@Override
	protected void initData() {
	}

	@Override
	protected void initListener() {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.chapter_list_back:
			finishInAnim();
			break;
		case R.id.keep_guide_begin:
			BookKeepActivity.luanch(activity);
			finish();
			break;
		}
	}

}
