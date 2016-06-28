package com.jie.book.work.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;

public class HttpDownloader {

	public interface httpDownloadCallBack {
		public void onResult(String result);
	}

	/**
	 * 根据URL下载文本文件
	 */
	public static void download(final Activity activity, final String urlStr, final httpDownloadCallBack callBack)
			throws Exception {
		TaskExecutor.getInstance().executeTask(new Runnable() {

			@Override
			public void run() {
				final StringBuffer sb = new StringBuffer();
				String line = null;
				BufferedReader buffer = null;
				try {
					URL url = new URL(urlStr);
					HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
					buffer = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
					while ((line = buffer.readLine()) != null) {
						sb.append(line);
					}
					if (activity != null) {
						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								callBack.onResult(sb.toString());
							}
						});
					} else {
						callBack.onResult(sb.toString());
					}
				} catch (Exception e) {
					if (activity != null) {
						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								callBack.onResult(null);
							}
						});
					} else {
						callBack.onResult(null);
					}
					e.printStackTrace();
				} finally {
					try {
						buffer.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	/**
	 * 根据URL下载文本文件
	 */
	public static void download(final String urlStr, final httpDownloadCallBack callBack) throws Exception{
		download(null, urlStr, callBack);
	}
}
