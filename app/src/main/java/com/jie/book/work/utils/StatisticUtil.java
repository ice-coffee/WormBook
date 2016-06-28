package com.jie.book.work.utils;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

public class StatisticUtil {

	public static final String SET_NOTIFI = "set_notifi";
	public static final String SET_DISCLAIMER = "set_disclaimer";
	public static final String SET_UMBACK = "set_umback";
	public static final String SET_CHECK_NEW = "set_check_new";
	public static final String SET_GOOD = "set_good";
	public static final String SET_SHARE = "set_share";
	public static final String SET_COMM = "set_comm";
	public static final String SET_READ = "set_read";
	public static final String SEARCH = "search";
	public static final String RANK_HOT = "rank_hot";
	public static final String RANK_SELL = "rank_sell";
	public static final String RANK_UPDATE = "rank_update";
	public static final String RANK_RECOMMED = "rank_recommed";
	public static final String RANK_TOTAL = "rank_total";
	public static final String RANK_MONTH = "rank_month";
	public static final String RANK_NEW = "rank_new";
	public static final String RANK_OVER = "rank_over";
	public static final String DOWNLOAD_100 = "download_100";
	public static final String DOWNLOAD_LAST_TOTAL = "download_last_total";
	public static final String DOWNLOAD_TOTAL = "download_total";
	public static final String BAIDU_BAR = "baidu_bar";
	public static final String TEXT_SIZE = "text_size";
	public static final String TEXT_TYPEFACE = "text_typeface";
	public static final String TEXT_SPACE_ADD = "text_space_add";
	public static final String TEXT_SPACE_DELE = "text_space_dele";
	public static final String READ_LOCK = "read_lock";
	public static final String READ_SCREEN = "read_screen";
	public static final String BG_WHITE = "bg_white";
	public static final String BG_SHEP = "bg_shep";
	public static final String BG_BLUE = "bg_blue";
	public static final String BG_GREEN = "bg_green";
	public static final String BG_PINK = "bg_pink";
	public static final String LIGHT_MODE = "light_mode";
	public static final String BOOK_SOURCE = "book_source";
	public static final String TYPE_QH = "type_qh";
	public static final String TYPE_XH = "type_xh";
	public static final String TYPE_WX = "type_wx";
	public static final String TYPE_XX = "type_xx";
	public static final String TYPE_WY = "type_wy";
	public static final String TYPE_KH = "type_kh";
	public static final String TYPE_JS = "type_js";
	public static final String TYPE_LS = "type_ls";
	public static final String TYPE_JD = "type_jd";
	public static final String TYPE_YQ = "type_yq";
	public static final String TYPE_CY = "type_cy";
	public static final String TYPE_DS = "type_ds";
	public static final String TYPE_XY = "type_xy";
	public static final String TYPE_QC = "type_qc";
	public static final String TYPE_TR = "type_tr";
	public static final String TYPE_ZT = "type_zt";
	public static final String TYPE_LY = "type_ly";
	public static final String TYPE_XUY = "type_xuy";
	public static final String TYPE_CX = "type_cx";
	public static final String TYPE_MZ = "type_mz";
	public static final String TYPE_QT = "type_qt";
	public static final String MODIFI_TOP = "modifi_top";
	public static final String MODIFI_DELETE = "modifi_delete";
	public static final String MODIFI_CLEAN = "modifi_clean";
	public static final String DOWNLOAD_REC_APP = "download_rec_app";
	public static final String DELET_REC_APP = "delet_rec_app";
	public static final String AD_SHOW = "ad_show";
	public static final String AD_CLICK = "ad_click";
	public static final String BOOK_LISETN = "book_listen";

	public static void sendEvent(Context context, String lableID) {
		MobclickAgent.onEvent(context, lableID);
	}

	public static void sendEventLable(Context context, String lableID, String lable) {
		MobclickAgent.onEvent(context, lableID, lable);
	}

}
