package com.jie.book.work.local;

import java.io.File;

import android.os.Environment;

public abstract class Paths {

	public static String cardDirectory() {
		return Environment.getExternalStorageDirectory().getPath() + "/ShuChengXiaoShuo/";
	}

	public static File getCacheDirectory() {
		File dir = new File(cardDirectory() + ".cache");
		if (!dir.exists())
			dir.mkdirs();
		return dir;
	}

	public static String getCacheDirectoryPath() {
		return getCacheDirectory().getPath() + File.separatorChar;
	}

	public static File getCacheDirectorySubFolder(int bookId) {
		File dir = new File(getCacheDirectoryPath() + bookId);
		if (!dir.exists())
			dir.mkdirs();
		return dir;
	}

}
