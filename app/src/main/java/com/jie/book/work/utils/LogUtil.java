/**
 * LogUtil.java
 * com.huyeal.util
 *
 * Function： TODO 
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2012-2-10 		turlet@163.com
 *
 * Copyright (c) 2012,  All Rights Reserved.
 */

package com.jie.book.work.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.util.Log;

/**
 * 全局日志管理类<BR>
 * [功能详细描述]
 */
public class LogUtil {

	public static final String TAG = LogUtil.class.getSimpleName();

	public static void d(String tag, String msg) {
		if (Config.DEBUG) {
			Log.d(tag, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (Config.DEBUG) {
			Log.i(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (Config.DEBUG) {
			Log.w(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (Config.DEBUG) {
			Log.e(tag, msg);
		}
	}

	public static void v(String tag, String msg) {
		if (Config.DEBUG) {
			Log.v(tag, msg);
		}
	}

	public static void log(Throwable t) {
		if (Config.DEBUG && t != null) {
			t.printStackTrace();
		}
	}

	@SuppressLint("SdCardPath")
	public static void writeFile(String fileName, String msg) {
		if (Config.DEBUG && !StringUtil.isEmpty(msg)) {
			try {
				File rootFile = new File("/sdcard/booknoverls/");
				if (!rootFile.exists()) {
					rootFile.mkdirs();
				}
				FileWriter filerWriter = new FileWriter("/sdcard/booknoverls/" + fileName, true);// 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
				BufferedWriter bufWriter = new BufferedWriter(filerWriter);
				bufWriter.write(msg);
				bufWriter.newLine();
				bufWriter.close();
				filerWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
