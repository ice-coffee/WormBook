package com.jie.book.work.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jie.book.work.R;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.entity.App;
import com.jie.book.work.service.DownloadService;
import com.jie.book.work.utils.HttpDownloader;
import com.jie.book.work.utils.HttpDownloader.httpDownloadCallBack;
import com.jie.book.work.utils.ImageLoadUtil;
import com.jie.book.work.utils.ImageLoadUtil.ImageType;
import com.jie.book.work.utils.MiscUtils;
import com.jie.book.work.utils.StatisticUtil;
import com.jie.book.work.utils.StringUtil;
import com.jie.book.work.utils.UIHelper;
import com.jie.book.work.utils.UIHelper.OnDialogClickListener;
import com.jie.book.work.view.HorizontalListView;

public class RecAppActivity extends BaseActivity implements OnClickListener {
	private static final String LUANCH_APP_URL = "luanch_app_url";
	private static final String LUANCH_APP_NAME = "luanch_app_name";
	private List<String> images = new ArrayList<String>();
	private TextView tvTitle, tvAppName, tvAppSize, tvAppDisName, tvAppDis;
	private ImageView ivIvcon;
	private Button btnDownload;
	private HorizontalListView gallery;
	private recAppAdapter adapter;
	private App app;

	public static void launcher(Context context, String appUrl, String appName) {
		Intent intent = new Intent();
		intent.setClass(context, RecAppActivity.class);
		intent.putExtra(LUANCH_APP_URL, appUrl);
		intent.putExtra(LUANCH_APP_NAME, appName);
		context.startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(Cookies.getReadSetting().isNightMode() ? R.layout.act_rec_app_ef : R.layout.act_rec_app);
		UIHelper.setTranStateBar(activity);
		initUI();
		initListener();
		initData();
	}

	protected void initUI() {
		ivIvcon = (ImageView) findViewById(R.id.rec_app_icon);
		tvTitle = (TextView) findViewById(R.id.book_search_back);
		tvAppName = (TextView) findViewById(R.id.rec_app_name);
		tvAppDis = (TextView) findViewById(R.id.rec_app_dis);
		tvAppSize = (TextView) findViewById(R.id.rec_app_size);
		tvAppDisName = (TextView) findViewById(R.id.rec_app_dis_name);
		btnDownload = (Button) findViewById(R.id.rec_app_download);
		gallery = (HorizontalListView) findViewById(R.id.rec_app_image);
		adapter = new recAppAdapter();
		gallery.setAdapter(adapter);
	}

	@Override
	protected void initListener() {
		findViewById(R.id.book_search_back).setOnClickListener(this);
		btnDownload.setOnClickListener(this);
	}

	protected void initData() {
		dialog = UIHelper.showProgressDialog(dialog, activity);
		String appUrl = getIntent().getStringExtra(LUANCH_APP_URL);
		String name = getIntent().getStringExtra(LUANCH_APP_NAME);
		tvTitle.setText(name);
		if (!StringUtil.isEmpty(appUrl)) {
			try {
				HttpDownloader.download(activity, appUrl, new httpDownloadCallBack() {
					@Override
					public void onResult(String result) {
						if (!StringUtil.isEmpty(result)) {
							app = new Gson().fromJson(result, App.class);
							if (app != null) {
								ImageLoadUtil.loadImage(ivIvcon, app.getLogo(), ImageType.APP_ICON);
								tvAppDisName.setText(app.getAppType() == 1 ? "游戏简介" : "应用简介");
								String sizeName = app.getAppType() == 1 ? "游戏大小：" : "应用大小：";
								tvAppSize.setText(sizeName + app.getSize() + "M");
								tvAppDis.setText(app.getDepict());
								tvTitle.setText(app.getName());
								tvAppName.setText(app.getName());
								images = app.getImageurls();
								adapter.notifyDataSetChanged();
							}
						}
						UIHelper.cancleProgressDialog(dialog);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			UIHelper.cancleProgressDialog(dialog);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.book_search_back:
			finishInAnim();
			break;
		case R.id.rec_app_download:
			if (app != null) {
				if (!MiscUtils.isWifiConnected(activity)) {
					UIHelper.showTowButtonDialog(activity, "当前不是wifi网络，是否要继续下载", "取消", "确认", true, null,
							new OnDialogClickListener() {

								@Override
								public void onClick() {
									DownloadService.luanch(activity, app.getName(), app.getUrl());
									StatisticUtil.sendEvent(activity, StatisticUtil.DOWNLOAD_REC_APP);
								}
							});
					return;
				} else {
					DownloadService.luanch(activity, app.getName(), app.getUrl());
					StatisticUtil.sendEvent(activity, StatisticUtil.DOWNLOAD_REC_APP);
				}
			}
			break;
		}
	}

	private class recAppAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return images.size();
		}

		@Override
		public Object getItem(int position) {
			return images.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(activity).inflate(R.layout.item_app_image, null);
				holder.ivImage = (ImageView) convertView.findViewById(R.id.item_image);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			ImageLoadUtil.loadImage(holder.ivImage, images.get(position), ImageType.APP_IMAGE);
			return convertView;
		}

	}

	private class ViewHolder {
		private ImageView ivImage;
	}

}
