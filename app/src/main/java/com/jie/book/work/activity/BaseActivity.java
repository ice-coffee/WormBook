package com.jie.book.work.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.jie.book.work.R;
import com.jie.book.work.application.BookActivityManager;
import com.jie.book.work.read.BookCatchManager;
import com.jie.book.work.utils.LogUtil;
import com.jie.book.work.utils.UIHelper;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseActivity extends FragmentActivity implements Thread.UncaughtExceptionHandler {
	protected BaseActivity activity;
	public Dialog dialog;
	public BookCatchManager chatchManager;
	protected boolean isShowAd = false;
	public boolean isReusem;

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		BookActivityManager.getInstance().addActivity(this);
		UIHelper.setNoActionbar(this);
		activity = this;
		chatchManager = new BookCatchManager(activity);
	}

	protected abstract void initUI();

	protected abstract void initData();

	protected abstract void initListener();

	protected void onResume() {
		super.onResume();
		isReusem = true;
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		isReusem = false;
		MobclickAgent.onPause(this);
	}

	public void finish() {
		super.finish();
		System.gc();
		BookActivityManager.getInstance().removeActivity(this);
	}

	public void finishInAnim() {
		finish();
		overridePendingTransition(R.anim.open_main, R.anim.close_next);
	}

	public void quit() {
		BookActivityManager.getInstance().ExitApp(this);
	}

	public void showToast(String paramString) {
		UIHelper.showToast(this, paramString);
	}

	public void showToastLong(String paramString) {
		UIHelper.showToast(this, paramString, Toast.LENGTH_LONG);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.open_main, R.anim.close_next);
	}

	public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
		BookActivityManager.getInstance().removeActivity(this);
		LogUtil.writeFile("error.txt", paramThrowable.toString());
	}

	public boolean isShowAd() {
		return isShowAd;
	}

	public void setShowAd(boolean isShowAd) {
		this.isShowAd = isShowAd;
	}

	public void setAdMode() {
	};

	public void setNomalMode() {
	};

	public void cancleProgressDialog() {
		UIHelper.cancleProgressDialog(dialog);
	}

}