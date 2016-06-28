package com.jie.book.work.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceUtil {
	private static final String KEY = "jie_noverls_book_sharedpreferences";
	private static SharedPreferenceUtil sharedPreferenceUtil;
	private static SharedPreferences sharedPreferences;
	public static final String FRIST_LUANCH = "first_lunc";

	private SharedPreferenceUtil(Context paramContext) {
		sharedPreferences = paramContext.getSharedPreferences(KEY, 0);
	}

	public static SharedPreferenceUtil getInstance(Context paramContext) {
		if (sharedPreferenceUtil == null)
			sharedPreferenceUtil = new SharedPreferenceUtil(paramContext);
		return sharedPreferenceUtil;
	}

	public boolean getBoolean(String paramString, boolean paramBoolean) {
		return sharedPreferences.getBoolean(paramString, paramBoolean);
	}

	public float getFloat(String paramString, float paramFloat) {
		return sharedPreferences.getFloat(paramString, paramFloat);
	}

	public int getInt(String paramString, int paramInt) {
		return sharedPreferences.getInt(paramString, paramInt);
	}

	public long getLong(String paramString, long paramLong) {
		return sharedPreferences.getLong(paramString, paramLong);
	}

	public String getString(String paramString1, String paramString2) {
		if (sharedPreferences == null)
			return "";
		return sharedPreferences.getString(paramString1, paramString2);
	}

	public boolean has(String paramString) {
		return sharedPreferences.contains(paramString);
	}

	public void putBoolean(String paramString, boolean paramBoolean) {
		SharedPreferences.Editor localEditor = sharedPreferences.edit();
		localEditor.putBoolean(paramString, paramBoolean);
		localEditor.commit();
	}

	public void putFloat(String paramString, float paramFloat) {
		SharedPreferences.Editor localEditor = sharedPreferences.edit();
		localEditor.putFloat(paramString, paramFloat);
		localEditor.commit();
	}

	public void putInt(String paramString, int paramInt) {
		SharedPreferences.Editor localEditor = sharedPreferences.edit();
		localEditor.putInt(paramString, paramInt);
		localEditor.commit();
	}

	public void putLong(String paramString, long paramLong) {
		SharedPreferences.Editor localEditor = sharedPreferences.edit();
		localEditor.putLong(paramString, paramLong);
		localEditor.commit();
	}

	public void putString(String paramString1, String paramString2) {
		SharedPreferences.Editor localEditor = sharedPreferences.edit();
		localEditor.putString(paramString1, paramString2);
		localEditor.commit();
	}
}