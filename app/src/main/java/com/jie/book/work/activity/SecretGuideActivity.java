package com.jie.book.work.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jie.book.work.R;
import com.jie.book.work.application.BookActivityManager;
import com.jie.book.work.application.BookApplication;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.entity.App;
import com.jie.book.work.service.DownloadService;
import com.jie.book.work.utils.Config;
import com.jie.book.work.utils.HttpDownloader;
import com.jie.book.work.utils.HttpDownloader.httpDownloadCallBack;
import com.jie.book.work.utils.ImageLoadUtil;
import com.jie.book.work.utils.ImageLoadUtil.ImageType;
import com.jie.book.work.utils.MiscUtils;
import com.jie.book.work.utils.StringUtil;
import com.jie.book.work.utils.UIHelper;
import com.jie.book.work.utils.UIHelper.OnDialogClickListener;

//神秘功能引导
public class SecretGuideActivity extends BaseActivity implements OnClickListener {
	public static final int REQUEST_CODE = 1001;
	private App secretApp;
	private ImageView ivIcon;
	private Button btnDownload, btnOpen;
	private TextView tvName, tvSize, tvTime;
	private View viewStar, viewApp, viewTime;
	private TimeBroadcastReceiver broadcastReceiver;

	public static void luanch(Activity content) {
		Intent intent = new Intent();
		intent.setClass(content, SecretGuideActivity.class);
		BookActivityManager.getInstance().goFoResult(content, intent, REQUEST_CODE);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.act_secret_guide);
		UIHelper.setTranStateBar(activity);
		initUI();
		initListener();
		initData();
		initBroadcastReceive();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(activity).unregisterReceiver(broadcastReceiver);
	}

	private void initBroadcastReceive() {
		if (broadcastReceiver == null) {
			broadcastReceiver = new TimeBroadcastReceiver();
			LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(activity);
			IntentFilter mIntentFilter = new IntentFilter();
			mIntentFilter.addAction(BookApplication.BROAD_CAST_ACTION_TIMER);
			lbm.registerReceiver(broadcastReceiver, mIntentFilter);
		} else {
			LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(activity);
			IntentFilter mIntentFilter = new IntentFilter();
			mIntentFilter.addAction(BookApplication.BROAD_CAST_ACTION_TIMER);
			lbm.registerReceiver(broadcastReceiver, mIntentFilter);
		}
	}

	@Override
	protected void initUI() {
		btnDownload = (Button) findViewById(R.id.secret_guide_app_download);
		btnOpen = (Button) findViewById(R.id.secret_guide_app_open);
		tvName = (TextView) findViewById(R.id.secret_guide_app_name);
		tvSize = (TextView) findViewById(R.id.secret_guide_app_size);
		tvTime = (TextView) findViewById(R.id.secret_guide_time);
		ivIcon = (ImageView) findViewById(R.id.secret_guide_app_icon);
		viewStar = findViewById(R.id.secret_guide_star_layout);
		viewApp = findViewById(R.id.secret_guide_app);
		viewTime = findViewById(R.id.secret_guide_time_layout);
	}

	@Override
	protected void initData() {
		try {
			HttpDownloader.download(activity, Config.SECRET_APP_URL, new httpDownloadCallBack() {

				@Override
				public void onResult(String result) {
					if (!StringUtil.isEmpty(result)) {
						secretApp = new Gson().fromJson(result, App.class);
						if (secretApp != null) {
							BookApplication.getInstance().secretPackageName = secretApp.getPackageName();
							tvName.setText(secretApp.getName());
							tvSize.setText(secretApp.getSize() + "M");
							ImageLoadUtil.loadImage(ivIcon, secretApp.getLogo(), ImageType.APP_ICON);
							if (MiscUtils.checkApkExist(activity, secretApp.getPackageName())) {
								btnOpen.setVisibility(View.VISIBLE);
								btnDownload.setVisibility(View.GONE);
							} else {
								btnOpen.setVisibility(View.GONE);
								btnDownload.setVisibility(View.VISIBLE);
							}
							starSecretTimer();
						}
					} else {
						showToast("网络异常,请检查网络!");
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 开始启动计时
	private void starSecretTimer() {
		if (!StringUtil.isEmpty(BookApplication.getInstance().secretPackageName)
				&& Cookies.getUserSetting().getSecretCompeletTime() > 0 && !BookApplication.getInstance().timerRun) {
			BookApplication.getInstance().starTimer();
		}
	}

	@Override
	protected void initListener() {
		findViewById(R.id.chapter_list_back).setOnClickListener(this);
		findViewById(R.id.secret_guide_star_btn).setOnClickListener(this);
		btnDownload.setOnClickListener(this);
		btnOpen.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.chapter_list_back:
			finishInAnim();
			setResult(RESULT_OK);
			break;
		case R.id.secret_guide_star_btn:
			RandomReadActivity.luanch(activity, 1);
			finish();
			break;
		case R.id.secret_guide_app_download:
			if (secretApp != null) {
				if (MiscUtils.isNetwork(activity)) {
					UIHelper.showTowButtonDialog(activity, "确定下载" + secretApp.getName() + "?", "取消", "确定", true, null,
							new OnDialogClickListener() {

								@Override
								public void onClick() {
									DownloadService.luanch(activity, secretApp.getName(), secretApp.getUrl());
								}
							});
				} else {
					showToast("网络异常,请检查网络!");
				}
			} else {
				showToast("网络异常,请检查网络!");
			}
			break;
		case R.id.secret_guide_app_open:
			Intent intent = new Intent();
			intent = getPackageManager().getLaunchIntentForPackage(BookApplication.getInstance().secretPackageName);
			startActivity(intent);
			break;
		}
	}

	private class TimeBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, final Intent intent) {
			if (BookApplication.getInstance().reaminTime <= 0) {
				viewStar.setVisibility(View.VISIBLE);
				viewApp.setVisibility(View.GONE);
				viewTime.setVisibility(View.GONE);
			} else {
				viewStar.setVisibility(View.GONE);
				viewApp.setVisibility(View.GONE);
				viewTime.setVisibility(View.VISIBLE);
				tvTime.setText(BookApplication.getInstance().reaminTime + "s");
			}
		}
	}
}
