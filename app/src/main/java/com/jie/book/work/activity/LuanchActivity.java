package com.jie.book.work.activity;

import java.util.List;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

import com.bond.bookcatch.vo.BookDesc;
import com.jie.book.work.R;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.read.BookCatchManager.BookDescListInterface;
import com.jie.book.work.utils.Config;
import com.jie.book.work.utils.MiscUtils;
import com.jie.book.work.utils.SharedPreferenceUtil;
import com.jie.book.work.utils.StringUtil;
import com.jie.book.work.utils.UIHelper;
import com.qq.e.splash.SplashAd;
import com.qq.e.splash.SplashAdListener;

public class LuanchActivity extends BaseActivity implements OnClickListener {

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(Cookies.getReadSetting().isNightMode() ? R.layout.act_luanch_ef : R.layout.act_luanch);
		Cookies.getUserSetting().setOpenTime(Cookies.getUserSetting().getOpenTime() + 1);
		Cookies.getUserSetting().setOpenSpaceTime(Cookies.getUserSetting().getOpenSpaceTime() + 1);
		MiscUtils.addSelfShortcut(activity);
		UIHelper.initBrightness(activity);
		String bookName = getIntent().getStringExtra("bookName");
		if (!StringUtil.isEmpty(bookName)) {
			openFromLisenBook(bookName);
		} else {
			initSplashAd();
		}
	}

	private void initSplashAd() {
		if (MiscUtils.isNetwork(activity)) {
			if (SharedPreferenceUtil.getInstance(activity).getInt(Config.SPLASH_SPACE, Config.SPLASH_SPACE_DEFAULT) > -1
					&& Cookies.getUserSetting().getOpenSpaceTime() > SharedPreferenceUtil.getInstance(activity).getInt(
							Config.SPLASH_SPACE, Config.SPLASH_SPACE_DEFAULT)
					&& Cookies.getUserSetting().getOpenTime() > Cookies.getUserSetting().getMaxOpenAdTime()) {
				FrameLayout adLayout = (FrameLayout) this.findViewById(R.id.adLayout);
				new SplashAd(this, adLayout, Config.GDT_APPID, Config.GDT_SplashPosId, new SplashAdListener() {

					@Override
					public void onAdPresent() {
						Cookies.getUserSetting().setOpenSpaceTime(0);
					}

					@Override
					public void onAdFailed(int arg0) {
						BookMainActivity.luanch(activity);
						finish();
					}

					@Override
					public void onAdDismissed() {
						BookMainActivity.luanch(activity);
						finish();
					}
				});
			} else {
				BookMainActivity.luanch(activity);
				finish();
			}
		} else {
			BookMainActivity.luanch(activity);
			finish();
		}
	}

	// 从听书打开
	private void openFromLisenBook(final String bookName) {
		chatchManager.getBookShelfList(new BookDescListInterface() {

			@Override
			public void getBookDescList(List<BookDesc> list) {
				if (list != null && list.size() > 0) {
					BookDesc desc = getBookDesc(list, bookName);
					if (desc != null) {
						ReadActivity.luanch(activity, desc, null);
					} else {
						BookSearchActivity.luanch(activity, bookName, false);
					}
				} else {
					BookSearchActivity.luanch(activity, bookName, false);
				}
				finish();
			}
		});
	}

	private BookDesc getBookDesc(List<BookDesc> list, String bookName) {
		for (BookDesc desc : list) {
			if (desc.getBookName().equals(bookName))
				return desc;
		}
		return null;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	protected void initUI() {

	}

	@Override
	protected void initData() {
	}

	@Override
	protected void initListener() {

	}

}