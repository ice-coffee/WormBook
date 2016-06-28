package com.jie.book.work.utils;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;

import com.bond.bookcatch.BookSync.AuthChannel;
import com.jie.book.work.activity.BaseActivity;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.entity.UserInfo;
import com.jie.book.work.read.BookCatchManager.DefaultInterface;
import com.jie.book.work.utils.UIHelper.OnDialogClickListener;

@SuppressLint("HandlerLeak")
public class LoginUtil implements PlatformActionListener {
	private BaseActivity activity;
	private onSignUpListener onSignUp;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Platform plat = (Platform) msg.obj;
			singup(plat);
		}

	};

	public LoginUtil(BaseActivity activity) {
		this.activity = activity;
	}

	@Override
	public void onCancel(Platform arg0, int arg1) {

	}

	@Override
	public void onComplete(Platform plat, int arg1, HashMap<String, Object> arg2) {
		Message msg = Message.obtain();
		msg.obj = plat;
		mHandler.sendMessage(msg);
	}

	@Override
	public void onError(Platform arg0, int arg1, Throwable arg2) {

	}

	// 授权登录
	public void authorize(String platName) {
		Platform plat = ShareSDK.getPlatform(platName);
		plat.setPlatformActionListener(this);
		plat.SSOSetting(false);
		plat.showUser(null);
	}

	// 注册
	public void singup(final Platform plat) {
		activity.dialog = UIHelper.showProgressDialog(activity.dialog, activity, "登录并同步书架");
		AuthChannel channel = AuthChannel.QQ;
		if (plat.getName() == SinaWeibo.NAME)
			channel = AuthChannel.WEIBO;
		activity.chatchManager.register(channel, plat.getDb().getUserId(), new DefaultInterface() {

			@Override
			public void getDefault(boolean haBack) {
				if (haBack) {
					Cookies.getUserSetting().setLoginType(plat.getName() == QZone.NAME ? 0 : 1);
					if (onSignUp != null)
						onSignUp.onSignUp();
					if (activity.chatchManager.isActivedAdv())
						Cookies.getUserSetting().setOpenTime(Cookies.getUserSetting().getMaxOpenAdTime() + 10);
					activity.showToast("登录同步成功");
				} else {
					logout();
					activity.showToast("登录同步失败");
				}
				UIHelper.cancleProgressDialog(activity.dialog);
			}
		});
	}

	// 同步书架
	public void sync(final onSyncListener listener) {
		activity.dialog = UIHelper.showProgressDialog(activity.dialog, activity, "书架同步中");
		activity.chatchManager.sync(new DefaultInterface() {

			@Override
			public void getDefault(boolean haBack) {
				if (haBack) {
					listener.onSync();
					activity.showToast("书架同步成功");
				} else {
					activity.showToast("书架同步失败");
				}
				UIHelper.cancleProgressDialog(activity.dialog);
			}
		});
	}

	// 注销
	public void logout() {
		if (isLogin()) {
			activity.chatchManager.logout();
			Platform qzone = ShareSDK.getPlatform(QZone.NAME);
			Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
			if (qzone != null && qzone.isValid())
				qzone.removeAccount();
			if (sina != null && sina.isValid())
				sina.removeAccount();
		}
	}

	// 获取用户资料
	public UserInfo getUserInfo() {
		UserInfo userInfo = null;
		if (isLogin()) {
			if (Cookies.getUserSetting().getLoginType() == 0) {
				Platform qzone = ShareSDK.getPlatform(QZone.NAME);
				if (qzone != null && qzone.isValid()) {
					userInfo = new UserInfo();
					userInfo.setUserIcon(qzone.getDb().getUserIcon());
					userInfo.setUserName(qzone.getDb().getUserName());
					userInfo.setUserNote(qzone.getDb().getUserId());
				}
			} else {
				Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
				if (sina != null && sina.isValid()) {
					userInfo = new UserInfo();
					userInfo.setUserIcon(sina.getDb().getUserIcon());
					userInfo.setUserName(sina.getDb().getUserName());
					userInfo.setUserNote(sina.getDb().getUserId());
				}
			}
		}
		return userInfo;
	}

	// 检查是否登录
	public boolean isLogin() {
		return activity.chatchManager.isLogin();
	}

	// 展示登录对话框
	public void showlogin(onSignUpListener listener) {
		onSignUp = listener;
		UIHelper.showChooseDialog(activity, "QQ登录", "新浪微博登录", null, null, null, new OnDialogClickListener() {

			@Override
			public void onClick() {
				authorize(QZone.NAME);
			}
		}, new OnDialogClickListener() {

			@Override
			public void onClick() {
				authorize(SinaWeibo.NAME);
			}
		}, null, null, null);
	}

	public interface onSignUpListener {
		public void onSignUp();
	}

	public interface onSyncListener {
		public void onSync();
	}
}
