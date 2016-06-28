package com.jie.book.work.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bond.bookcatch.BookSync;
import com.jie.book.work.R;
import com.jie.book.work.application.BookActivityManager;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.entity.UserInfo;
import com.jie.book.work.read.BookCatchManager.DefaultInterface;
import com.jie.book.work.utils.ImageLoadUtil;
import com.jie.book.work.utils.LoginUtil;
import com.jie.book.work.utils.LoginUtil.onSyncListener;
import com.jie.book.work.utils.TimeUtil;
import com.jie.book.work.utils.UIHelper;
import com.jie.book.work.utils.UIHelper.OnDialogClickListener;

/**
 * 养肥区引导界面
 * 
 * @author cwj
 * 
 */
public class MyInfoActivity extends BaseActivity implements OnClickListener {
	private ImageView ivIcon;
	private TextView tvName;
	private TextView tvTime;
	private TextView tvCount;
	private LoginUtil loginUtil;

	public static void luanch(Activity content) {
		Intent intent = new Intent();
		intent.setClass(content, MyInfoActivity.class);
		BookActivityManager.getInstance().goTo(content, intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(Cookies.getReadSetting().isNightMode() ? R.layout.act_my_info_ef : R.layout.act_my_info);
		UIHelper.setTranStateBar(activity);
		loginUtil = new LoginUtil(activity);
		initUI();
		initData();
	}

	@Override
	protected void initUI() {
		findViewById(R.id.my_info_back).setOnClickListener(this);
		findViewById(R.id.my_info_time).setOnClickListener(this);
		findViewById(R.id.my_info_quit).setOnClickListener(this);
		findViewById(R.id.my_info_book).setOnClickListener(this);
		ivIcon = (ImageView) activity.findViewById(R.id.my_info_icon);
		tvName = (TextView) activity.findViewById(R.id.my_info_name);
		tvTime = (TextView) activity.findViewById(R.id.my_info_time_value);
		tvCount = (TextView) activity.findViewById(R.id.my_info_count);
	}

	@Override
	protected void initData() {
		setUserInfo();
	}

	@Override
	protected void initListener() {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.my_info_back:
			finishInAnim();
			break;
		case R.id.my_info_time:
		case R.id.my_info_book:
			UIHelper.showTowButtonDialog(activity, "是否同步书架？", "取消", "确定", true, null, new OnDialogClickListener() {

				@Override
				public void onClick() {
					loginUtil.sync(new onSyncListener() {

						@Override
						public void onSync() {
							setUserInfo();
							BookMainActivity.getInstance().getDataAfterSync();
						}
					});
				}
			});
			break;
		case R.id.my_info_quit:
			UIHelper.showTowButtonDialog(activity, "退出后会删除本地书架中的书(不会清除缓存),确定退出登录？", "取消", "确定", true, null,
					new OnDialogClickListener() {

						@Override
						public void onClick() {
							logoutWithSyn();
						}
					});
			break;
		}
	}

	public void setUserInfo() {
		UserInfo userInfo = loginUtil.getUserInfo();
		if (userInfo != null) {
			tvName.setText(userInfo.getUserName());
			tvCount.setText(Cookies.getUserSetting().getCloudBookCount() + "本");
			if (Cookies.getUserSetting().getLastSyncTime() > 0)
				tvTime.setText(TimeUtil.longToTime(Cookies.getUserSetting().getLastSyncTime()));
			ImageLoadUtil.loadRoundImage(ivIcon, R.drawable.icon_menu_head, userInfo.getUserIcon());
		}
	}

	// 先同步后注销
	public void logoutWithSyn() {
		if (BookSync.isLogin()) {
			dialog = UIHelper.showProgressDialog(dialog, activity, "同步并注销中");
			activity.chatchManager.logoutWithSync(new DefaultInterface() {

				@Override
				public void getDefault(boolean haBack) {
					UIHelper.cancleProgressDialog(dialog);
					if (!haBack) {
						UIHelper.showTowButtonDialog(activity, "书架同步失败，是否继续退出？", "取消", "确定", true, null,
								new OnDialogClickListener() {

									@Override
									public void onClick() {
										loginUtil.logout();
										activity.showToast("退出成功");
										BookMainActivity.getInstance().setLogout();
										finishInAnim();
									}
								});
					} else {
						activity.showToast("退出成功");
						BookMainActivity.getInstance().setLogout();
						finishInAnim();
					}
				}
			});
		}
	}
}
