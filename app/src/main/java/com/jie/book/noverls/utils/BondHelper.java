package com.jie.book.noverls.utils;

import android.content.Context;
import com.jie.book.work.application.BookApplication;
import com.jie.book.work.utils.Config;
import com.jie.book.work.utils.MiscUtils;
import com.jie.book.work.utils.SharedPreferenceUtil;


public class BondHelper {
	public static final int BOOK_CHANNEL_DEFAULT = 1;

	public static Context getContext() {
		return BookApplication.getInstance();
	}

	// 0宜搜 1百度
	public static int getBookChannel() {
		return BOOK_CHANNEL_DEFAULT;
	}

	// 用户代理
	public static String getUserAgentFormat() {
		return SharedPreferenceUtil.getInstance(BookApplication.getInstance()).getString(Config.ZH_USER_AGENT,
				Config.ZH_USER_AGENT_DEFAULT);
	}

	// 搜索来源配置
	public static String getChannelConfig() {
		return SharedPreferenceUtil.getInstance(BookApplication.getInstance()).getString(Config.SEARCH_BOOK_CHANNEL,
				Config.SEARCH_BOOK_CHANNEL_DEFAULT);
	}

	// 分类来源配置
	public static String getChannelConfigByType() {
		return SharedPreferenceUtil.getInstance(BookApplication.getInstance()).getString(Config.TYPE_BOOK_CHANNEL,
				Config.TYPE_BOOK_CHANNEL_DEFAULT);
	}

	// APP版本号
	public static String getClientVersion() {
		return String.valueOf(MiscUtils.getVersionCode());
	}

	// 搜狗版本号
	public static String getSogouMseVersion() {
		return SharedPreferenceUtil.getInstance(BookApplication.getInstance()).getString(Config.SOGOU_VERSION,
				Config.SOGOU_VERSION_DEFAULT);
	}

}
