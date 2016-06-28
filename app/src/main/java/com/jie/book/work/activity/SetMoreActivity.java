package com.jie.book.work.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.jie.book.work.R;
import com.jie.book.work.application.BookActivityManager;
import com.jie.book.work.application.BookApplication;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.utils.MiscUtils;
import com.jie.book.work.utils.ShareUtil;
import com.jie.book.work.utils.StatisticUtil;
import com.jie.book.work.utils.StringUtil;
import com.jie.book.work.utils.UIHelper;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

public class SetMoreActivity extends BaseActivity implements OnClickListener {
	private ImageView ivCheckNew;
	private TextView tvVersion;

	public static void luanch(Activity content) {
		Intent intent = new Intent();
		intent.setClass(content, SetMoreActivity.class);
		BookActivityManager.getInstance().goTo(content, intent);
	}

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(Cookies.getReadSetting().isNightMode() ? R.layout.act_set_more_ef : R.layout.act_set_more);
		UIHelper.setTranStateBar(activity);
		initUI();
		initListener();
	}

	@Override
	protected void initUI() {
		tvVersion = (TextView) findViewById(R.id.book_shelf_new_version);
		ivCheckNew = (ImageView) findViewById(R.id.book_shelf_check_new);
		ivCheckNew.setVisibility(BookApplication.check_new ? View.VISIBLE : View.GONE);
		String version = MiscUtils.getVersionName() != null ? MiscUtils.getVersionName() : StringUtil.EMPTY;
		tvVersion.setText("当前版本:" + version);
	}

	@Override
	protected void initData() {
	}

	@Override
	protected void initListener() {
		findViewById(R.id.book_search_back).setOnClickListener(this);
		findViewById(R.id.set_more_new).setOnClickListener(this);
		findViewById(R.id.set_more_disclaimer).setOnClickListener(this);
		findViewById(R.id.set_more_good).setOnClickListener(this);
		findViewById(R.id.set_more_feedback).setOnClickListener(this);
		findViewById(R.id.set_more_share).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.book_search_back:
			finishInAnim();
			break;
		case R.id.set_more_new:
			StatisticUtil.sendEvent(activity, StatisticUtil.SET_CHECK_NEW);
			checkUpdate();
			break;
		case R.id.set_more_good:
			StatisticUtil.sendEvent(activity, StatisticUtil.SET_GOOD);
			try {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				Uri uri = Uri.parse("market://details?id=" + "com.jie.book.work");
				intent.setData(uri);
				startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.set_more_disclaimer:
			StatisticUtil.sendEvent(activity, StatisticUtil.SET_DISCLAIMER);
			DisclaimerActivity.luanch(activity);
			break;
		case R.id.set_more_feedback:
			StatisticUtil.sendEvent(activity, StatisticUtil.SET_UMBACK);
			FeedbackAgent agent = new FeedbackAgent(activity);
			agent.startFeedbackActivity();
			break;
		case R.id.set_more_share:
			StatisticUtil.sendEvent(activity, StatisticUtil.SET_SHARE);
			ShareUtil.showSharePop(activity, findViewById(R.id.book_shelf_set_more));
			break;
		}
	}

	// 检查更新
	private void checkUpdate() {
		UmengUpdateListener updateListener = new UmengUpdateListener() {

			@Override
			public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
				switch (updateStatus) {
				case 0:
					ivCheckNew.setVisibility(View.VISIBLE);
					UIHelper.showUpdateDialog(activity, updateInfo, true);
					break;
				case 1:
					showToast("您的应用已经是最新版本");
					break;
				case 2: // none wifi
					break;
				case 3:
					showToast("版本更新超时,请检查网络");
					break;
				}

			}
		};
		UmengUpdateAgent.setUpdateListener(updateListener);
		UmengUpdateAgent.update(activity);
	}

}