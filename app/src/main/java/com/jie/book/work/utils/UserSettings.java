package com.jie.book.work.utils;

import android.content.Context;

public class UserSettings {
	// 通知提醒
	public static final String NOTIFI_OPEN = "notifiOpen";
	public static final String NOTIFI_VOICE_OPEN = "notifiVoiceOpen";
	public static final String NOTIFI_SHAKE_OPEN = "notifiShakeOpen";
	public static final String FIRST_LUANCH = "FirstLuanch";
	public static final String OPEN_TIME = "openTime";
	public static final String OPEN_SPACE_TIME = "openSpaceTime";
	// 阅读设置
	public static final String READ_VOICE = "readVoice";
	public static final String READ_SCREEN_LIGHT = "readScreenLight";
	public static final String READ_SCREEN_FULL = "readScreenFull";
	public static final String READ_ANIM = "readAnim";
	// 专题缓存
	public static final String SUBJECT_COOKIE = "subject_cookie";
	// 搜索渠道
	public static final String SEARCH_SOURCE = "search_source";
	// 来源切换引导
	public static final String SHOW_SOURCE_GUIDE = "show_source_guide";
	// 当前使用的字体
	public static final String USER_TYPEFACE = "user_typeface";
	// 缓存下载字体
	public static final String CACHE_DOWNLOAD_TYPEFACE = "cache_download_typeface";
	// 是否隐藏空的养肥区
	public static final String GONE_EMPTY_KEEP = "gone_empty_keep";
	// 养肥时间
	public static final String BOOK_KEEP_TIME = "book_keep_time";
	// 已通知的消息
	public static final String NOTIFI_STRING = "notifi_string";
	// 养肥区第一次打开
	public static final String FIRST_KEEP = "FirstKeep";
	// 云书架书籍本数
	public static final String CLOUD_BOOK_COUNT = "cloud_book_count";
	// 上次同步时间
	public static final String LAST_SYNC_TIME = "last_sync_time";
	// 神秘功能任务完成时间
	public static final String SECRET_COMPLETE_TIME = "secret_complete_time";
	// 登录的来源
	public static final String LOGIN_TYPE = "login_type";// 0腾讯1微博
	// 登录的来源

	private SharedPreferenceUtil spUtil;

	public UserSettings(Context paramContext) {
		spUtil = SharedPreferenceUtil.getInstance(paramContext);
	}

	public boolean isNotifiOpen() {
		return spUtil.getBoolean(NOTIFI_OPEN, true);
	}

	public void setNotifiOpen(boolean notifiOpen) {
		spUtil.putBoolean(NOTIFI_OPEN, notifiOpen);
	}

	public boolean isNotifiVoiceOpen() {
		return spUtil.getBoolean(NOTIFI_VOICE_OPEN, true);
	}

	public void setNotifiVoiceOpen(boolean notifiVoiceOpen) {
		spUtil.putBoolean(NOTIFI_VOICE_OPEN, notifiVoiceOpen);
	}

	public boolean isNotifiShakeOpen() {
		return spUtil.getBoolean(NOTIFI_SHAKE_OPEN, true);
	}

	public void setNotifiShakeOpen(boolean notifiShakeOpen) {
		spUtil.putBoolean(NOTIFI_SHAKE_OPEN, notifiShakeOpen);
	}

	public boolean isFristLuanch() {
		return spUtil.getBoolean(FIRST_LUANCH, true);
	}

	public void setFristLuanch(boolean isFrist) {
		spUtil.putBoolean(FIRST_LUANCH, isFrist);
	}

	public int getOpenTime() {
		return spUtil.getInt(OPEN_TIME, 0);
	}

	public void setOpenTime(int openTime) {
		spUtil.putInt(OPEN_TIME, openTime);
	}

	public int getOpenSpaceTime() {
		return spUtil.getInt(OPEN_SPACE_TIME, 0);
	}

	public void setOpenSpaceTime(int time) {
		spUtil.putInt(OPEN_SPACE_TIME, time);
	}

	public int getMaxOpenAdTime() {
		if (Config.DEBUG)
			return 0;
		else
			return spUtil.getInt(Config.OPEN_AD_TIME, Config.OPEN_AD_TIME_DEFAULT);
	}

	public boolean isReadVoice() {
		return spUtil.getBoolean(READ_VOICE, true);
	}

	public void setReadVoice(boolean isReadVoice) {
		spUtil.putBoolean(READ_VOICE, isReadVoice);
	}

	public boolean isReadScreenLight() {
		return spUtil.getBoolean(READ_SCREEN_LIGHT, true);
	}

	public void setReadScreenLight(boolean isReadScreenLight) {
		spUtil.putBoolean(READ_SCREEN_LIGHT, isReadScreenLight);
	}

	public boolean isReadScreenFull() {
		return spUtil.getBoolean(READ_SCREEN_FULL, true);
	}

	public void setReadScreenFull(boolean readScreenFull) {
		spUtil.putBoolean(READ_SCREEN_FULL, readScreenFull);
	}

	public boolean isReadAnim() {
		return spUtil.getBoolean(READ_ANIM, true);
	}

	public void setReadAnim(boolean readAnim) {
		spUtil.putBoolean(READ_ANIM, readAnim);
	}

	public String getSubjectCookie() {
		return spUtil.getString(SUBJECT_COOKIE, StringUtil.EMPTY);
	}

	public void setSubjectCookie(String subjectCookie) {
		spUtil.putString(SUBJECT_COOKIE, subjectCookie);
	}

	public String getSearchSource() {
		return spUtil.getString(SEARCH_SOURCE, Config.SEARCH_SOURCE_DEFAULT);

	}

	public void setSearchSource(String searchSource) {
		spUtil.putString(SEARCH_SOURCE, searchSource);
	}

	public boolean isShowSourceGuid() {
		return spUtil.getBoolean(SHOW_SOURCE_GUIDE, true);
	}

	public void setShowSourceGuid(boolean showSourceGuide) {
		spUtil.putBoolean(SHOW_SOURCE_GUIDE, showSourceGuide);
	}

	public String getUserTypeface() {
		return spUtil.getString(USER_TYPEFACE, StringUtil.EMPTY);
	}

	public void setUserTypeface(String userTypeface) {
		spUtil.putString(USER_TYPEFACE, userTypeface);
	}

	public String getCacheDownloadTypeface() {
		return spUtil.getString(CACHE_DOWNLOAD_TYPEFACE, StringUtil.EMPTY);
	}

	public void setCacheDownloadTypeface(String cacheDownloadTypeface) {
		spUtil.putString(CACHE_DOWNLOAD_TYPEFACE, cacheDownloadTypeface);
	}

	public boolean isGoneKeep() {
		return spUtil.getBoolean(GONE_EMPTY_KEEP, false);
	}

	public void setGoneKeep(boolean goneKeep) {
		spUtil.putBoolean(GONE_EMPTY_KEEP, goneKeep);
	}

	public int getBookKeepTime() {
		return spUtil.getInt(BOOK_KEEP_TIME, 5);
	}

	public void setBookKeepTime(int bookKeepTime) {
		spUtil.putInt(BOOK_KEEP_TIME, bookKeepTime);
	}

	public String getNotifiString() {
		return spUtil.getString(NOTIFI_STRING, StringUtil.EMPTY);
	}

	public void setNotifiString(String notifiStr) {
		String ordNotifiStr = getNotifiString();
		if (StringUtil.isEmpty(ordNotifiStr)) {
			spUtil.putString(NOTIFI_STRING, notifiStr);
		} else {
			String newNotifiStr = ordNotifiStr + ";" + notifiStr;
			spUtil.putString(NOTIFI_STRING, newNotifiStr);
		}
	}

	public boolean hasNotifiString(String notifiStr) {
		String ordNotifiStr = getNotifiString();
		if (!StringUtil.isEmpty(ordNotifiStr)) {
			String[] notifiStrs = ordNotifiStr.split(";");
			for (String name : notifiStrs) {
				if (name.equals(notifiStr))
					return true;
			}
		}
		return false;
	}

	public boolean isFristKeep() {
		return spUtil.getBoolean(FIRST_KEEP, true);
	}

	public void setFristKeep(boolean fristKeep) {
		spUtil.putBoolean(FIRST_KEEP, fristKeep);
	}

	public int getCloudBookCount() {
		return spUtil.getInt(CLOUD_BOOK_COUNT, 0);
	}

	public void setCloudBookCount(int count) {
		spUtil.putInt(CLOUD_BOOK_COUNT, count);
	}

	public void setLastSyncTime() {
		spUtil.putLong(LAST_SYNC_TIME, System.currentTimeMillis());
	}

	public long getLastSyncTime() {
		return spUtil.getLong(LAST_SYNC_TIME, 0);
	}

	public int getSecretCompeletTime() {
		return spUtil.getInt(SECRET_COMPLETE_TIME, 180);
	}

	public void setSecretCompeletTime(int time) {
		spUtil.putInt(SECRET_COMPLETE_TIME, time);
	}

	public int getLoginType() {
		return spUtil.getInt(LOGIN_TYPE, 0);
	}

	public void setLoginType(int type) {
		spUtil.putInt(LOGIN_TYPE, type);
	}

}