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

public class RankMoreMActivity extends BaseActivity implements OnClickListener {
	private TextView tvBack;
	private TextView tvItem1, tvItem2, tvItem3, tvItem4, tvItem5, tvItem6, tvItem7;

	public static void luanch(Activity content) {
		Intent intent = new Intent();
		intent.setClass(content, RankMoreMActivity.class);
		BookActivityManager.getInstance().goTo(content, intent);
	}

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(Cookies.getReadSetting().isNightMode() ? R.layout.act_rank_more_m_ef : R.layout.act_rank_more_m);
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
		tvItem6 = (TextView) findViewById(R.id.rank_more_text6);
		tvItem7 = (TextView) findViewById(R.id.rank_more_text7);

	}

	@Override
	protected void initData() {

	}

	@Override
	protected void initListener() {
		tvBack.setOnClickListener(this);
		findViewById(R.id.rank_more_m1).setOnClickListener(this);
		findViewById(R.id.rank_more_m2).setOnClickListener(this);
		findViewById(R.id.rank_more_m3).setOnClickListener(this);
		findViewById(R.id.rank_more_m4).setOnClickListener(this);
		findViewById(R.id.rank_more_m5).setOnClickListener(this);
		findViewById(R.id.rank_more_m6).setOnClickListener(this);
		findViewById(R.id.rank_more_m7).setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.book_search_back:
			finishInAnim();
			break;
		case R.id.rank_more_m1:
			BookRankResultActivity.luanch(activity, 11, tvItem1.getText().toString());
			break;
		case R.id.rank_more_m2:
			BookRankResultActivity.luanch(activity, 12, tvItem2.getText().toString());
			break;
		case R.id.rank_more_m3:
			BookRankResultActivity.luanch(activity, 13, tvItem3.getText().toString());
			break;
		case R.id.rank_more_m4:
			BookRankResultActivity.luanch(activity, 14, tvItem4.getText().toString());
			break;
		case R.id.rank_more_m5:
			BookRankResultActivity.luanch(activity, 15, tvItem5.getText().toString());
			break;
		case R.id.rank_more_m6:
			BookRankResultActivity.luanch(activity, 16, tvItem6.getText().toString());
			break;
		case R.id.rank_more_m7:
			BookRankResultActivity.luanch(activity, 17, tvItem7.getText().toString());
			break;
		}
	}
}