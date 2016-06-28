package com.jie.book.work.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.jie.book.work.R;
import com.jie.book.work.application.BookActivityManager;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.utils.UIHelper;

public class RankMoreFActivity extends BaseActivity implements OnClickListener {
	private TextView tvBack;
	private TextView tvItem1, tvItem2, tvItem3, tvItem4, tvItem5;

	public static void luanch(Activity content) {
		Intent intent = new Intent();
		intent.setClass(content, RankMoreFActivity.class);
		BookActivityManager.getInstance().goTo(content, intent);
	}

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(Cookies.getReadSetting().isNightMode() ? R.layout.act_rank_more_f_ef : R.layout.act_rank_more_f);
		UIHelper.setTranStateBar(activity);
		initUI();
		initListener();
		initData();
	}

	@Override
	protected void initUI() {
		tvBack = (TextView) findViewById(R.id.book_search_back);
		tvItem1 = (TextView) findViewById(R.id.rank_more_text1);
		tvItem2 = (TextView) findViewById(R.id.rank_more_text2);
		tvItem3 = (TextView) findViewById(R.id.rank_more_text3);
		tvItem4 = (TextView) findViewById(R.id.rank_more_text4);
		tvItem5 = (TextView) findViewById(R.id.rank_more_text5);
	}

	@Override
	protected void initData() {

	}

	@Override
	protected void initListener() {
		tvBack.setOnClickListener(this);
		findViewById(R.id.rank_more_f1).setOnClickListener(this);
		findViewById(R.id.rank_more_f2).setOnClickListener(this);
		findViewById(R.id.rank_more_f3).setOnClickListener(this);
		findViewById(R.id.rank_more_f4).setOnClickListener(this);
		findViewById(R.id.rank_more_f5).setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.book_search_back:
			finishInAnim();
			break;
		case R.id.rank_more_f1:
			BookRankResultActivity.luanch(activity, 21, tvItem1.getText().toString());
			break;
		case R.id.rank_more_f2:
			BookRankResultActivity.luanch(activity, 22, tvItem2.getText().toString());
			break;
		case R.id.rank_more_f3:
			BookRankResultActivity.luanch(activity, 23, tvItem3.getText().toString());
			break;
		case R.id.rank_more_f4:
			BookRankResultActivity.luanch(activity, 24, tvItem4.getText().toString());
			break;
		case R.id.rank_more_f5:
			BookRankResultActivity.luanch(activity, 25, tvItem5.getText().toString());
			break;
		}
	}
}