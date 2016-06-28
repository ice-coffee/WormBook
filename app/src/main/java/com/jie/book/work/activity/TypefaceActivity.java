package com.jie.book.work.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jie.book.work.R;
import com.jie.book.work.application.BookActivityManager;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.download.DownLoadCallback;
import com.jie.book.work.download.DownloadManager;
import com.jie.book.work.entity.TypefaceInfo;
import com.jie.book.work.utils.Config;
import com.jie.book.work.utils.HttpDownloader;
import com.jie.book.work.utils.HttpDownloader.httpDownloadCallBack;
import com.jie.book.work.utils.ImageLoadUtil;
import com.jie.book.work.utils.ImageLoadUtil.ImageType;
import com.jie.book.work.utils.MiscUtils;
import com.jie.book.work.utils.StringUtil;
import com.jie.book.work.utils.UIHelper;
import com.jie.book.work.utils.UIHelper.OnDialogClickListener;

@SuppressLint("HandlerLeak")
public class TypefaceActivity extends BaseActivity implements OnClickListener {
	public static final String TYPEPFACE_JT = "jt";
	public static final String TYPEPFACE_FT = "ft";
	private List<TypefaceInfo> typefaces = new ArrayList<TypefaceInfo>();
	private List<TypefaceInfo> donwloadTypeface = new ArrayList<TypefaceInfo>();
	private List<TypefaceInfo> unDonwloadTypeface = new ArrayList<TypefaceInfo>();
	private DownloadManager downloadManager;
	private TypefaceAdapter adapter;
	private ListView listview;
	private Dialog dialog;

	public static void luanch(Activity content) {
		Intent intent = new Intent();
		intent.setClass(content, TypefaceActivity.class);
		BookActivityManager.getInstance().goTo(content, intent);
	}

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(Cookies.getReadSetting().isNightMode() ? R.layout.act_typeface_ef : R.layout.act_typeface);
		UIHelper.setTranStateBar(activity);
		downloadManager = DownloadManager.getDownloadManager(activity);
		initUI();
		initListener();
		initData();
	}

	@Override
	protected void initListener() {
		findViewById(R.id.actionbar_layout).setOnClickListener(this);
		downloadManager.setDownLoadCallback(new DownLoadCallback() {

			@Override
			public void onLoading(String url, long totalSize, long currentSize, long speed) {
				super.onLoading(url, totalSize, currentSize, speed);
				TypefaceInfo typeface = getTypefaceInfoByUrl(url);
				int precent = (int) (((float) currentSize / totalSize) * 100);
				if (precent % 2 == 0) {
					typeface.setPrecent(precent);
					adapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onSuccess(String url) {
				super.onSuccess(url);
				TypefaceInfo typeface = getTypefaceInfoByUrl(url);
				typeface.setPrecent(-2);
				adapter.notifyDataSetChanged();
				activity.showToast(typeface.getName() + "下载完毕");
			}

			@Override
			public void onFailure(String url, String strMsg) {
				super.onFailure(url, strMsg);
				TypefaceInfo typeface = getTypefaceInfoByUrl(url);
				typeface.setPrecent(-3);
				adapter.notifyDataSetChanged();
				activity.showToast(typeface.getName() + "下载失败，请重新下载");
			}

		});
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.actionbar_layout:
			if (downloadManager.getDownloadinghandlerCount() > 0) {
				UIHelper.showTowButtonDialog(activity, "字体正在下载中，是否退出？", "取消", "确定", true, null,
						new OnDialogClickListener() {

							@Override
							public void onClick() {
								downloadManager.pauseAllHandler();
								finishInAnim();
							}
						});
			} else {
				finishInAnim();
			}
			break;

		}
	}

	@Override
	protected void initUI() {
		listview = (ListView) findViewById(R.id.pull_refresh_list);
		adapter = new TypefaceAdapter();
		listview.setAdapter(adapter);
		dialog = UIHelper.showProgressDialog(dialog, activity);
	}

	@Override
	protected void initData() {
		TypefaceInfo typeface1 = new TypefaceInfo(TYPEPFACE_JT, "默认");
		TypefaceInfo typeface2 = new TypefaceInfo(TYPEPFACE_FT, "繁体");
		typefaces.add(typeface1);
		typefaces.add(typeface2);
		if (MiscUtils.isNetwork(activity)) {
			try {
				HttpDownloader.download(activity, Config.DOWNLOAD_TYPEFACE_URL, new httpDownloadCallBack() {
					@Override
					public void onResult(String result) {
						parseData(result);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			String cacheDownloadTypeface = Cookies.getUserSetting().getCacheDownloadTypeface();
			parseData(cacheDownloadTypeface);
		}
	}

	// 解析数据
	private void parseData(String result) {
		if (!StringUtil.isEmpty(result)) {
			try {
				Cookies.getUserSetting().setCacheDownloadTypeface(result);
				JSONArray jsonArray = new JSONArray(result);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject js = jsonArray.optJSONObject(i);
					TypefaceInfo typeface = new TypefaceInfo();
					typeface.setFileUrl(js.optString("fileUrl"));
					typeface.setImageUrl(js.optString("imageUrl"));
					typeface.setFileName(js.optString("fileName"));
					typeface.setName(js.optString("name"));
					typeface.setSize(js.optDouble("size"));
					if (checkIfExist(typeface)) {
						typeface.setPrecent(-2);
						donwloadTypeface.add(typeface);
					} else {
						unDonwloadTypeface.add(typeface);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		typefaces.addAll(donwloadTypeface);
		typefaces.addAll(unDonwloadTypeface);
		adapter.notifyDataSetChanged();
		UIHelper.cancleProgressDialog(dialog);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (downloadManager.getDownloadinghandlerCount() > 0) {
				UIHelper.showTowButtonDialog(activity, "字体正在下载中，是否退出？", "取消", "确定", true, null,
						new OnDialogClickListener() {

							@Override
							public void onClick() {
								downloadManager.pauseAllHandler();
								finishInAnim();
							}
						});
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	// 下载字体
	private void downloadTypeface(final TypefaceInfo typeface) {
		if (MiscUtils.isNetwork(activity)) {
			if (MiscUtils.isWifiConnected(activity)) {
				typeface.setPrecent(0);
				downloadManager.addHandler(typeface.getFileUrl(), typeface.getFileName() + ".ttf");
				adapter.notifyDataSetChanged();
				activity.showToast(typeface.getName() + "开始下载");
			} else {
				UIHelper.showTowButtonDialog(activity, "当前网络不是wifi，是否继续下载？", "取消", "确定", true, null,
						new OnDialogClickListener() {

							@Override
							public void onClick() {
								typeface.setPrecent(0);
								downloadManager.addHandler(typeface.getFileUrl(), typeface.getFileName() + ".ttf");
								adapter.notifyDataSetChanged();
								activity.showToast(typeface.getName() + "开始下载");
							}
						});
			}
		} else {
			activity.showToast("网络异常，请检查网络");
		}
	}

	// 通过url查找字体
	private TypefaceInfo getTypefaceInfoByUrl(String url) {
		for (TypefaceInfo typeface : typefaces) {
			if (!StringUtil.isEmpty(typeface.getFileUrl()) && typeface.getFileUrl().equals(url))
				return typeface;
		}
		return null;
	}

	// 检查是否下载完
	private boolean checkIfExist(TypefaceInfo typeface) {
		File file = downloadManager.getFile(typeface.getFileName() + ".ttf");
		return file.exists();
	}

	private class TypefaceAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return typefaces.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View contentView, ViewGroup arg2) {
			ViewHolder holder;
			if (contentView == null) {
				holder = new ViewHolder();
				contentView = LayoutInflater.from(activity).inflate(R.layout.item_typeface_list, null);
				holder.viewTypeface = contentView.findViewById(R.id.typeface_layout);
				holder.viewDownload = contentView.findViewById(R.id.typeface_download);
				holder.ivDownloadUrl = (ImageView) contentView.findViewById(R.id.typeface_download_image);
				holder.tvNormalName = (TextView) contentView.findViewById(R.id.typeface_normal_name);
				holder.tvDownloadSize = (TextView) contentView.findViewById(R.id.typeface_download_size);
				holder.btnChoose = (ImageButton) contentView.findViewById(R.id.typeface_choose);
				holder.btnBlack = (Button) contentView.findViewById(R.id.typeface_black_btn);
				holder.btnRed = (Button) contentView.findViewById(R.id.typeface_red_btn);
				contentView.setTag(holder);
			} else {
				holder = (ViewHolder) contentView.getTag();
			}
			final TypefaceInfo info = typefaces.get(position);
			holder.btnChoose
					.setVisibility(Cookies.getUserSetting().getUserTypeface().equals(info.getFileName()) ? View.VISIBLE
							: View.GONE);
			ImageLoadUtil.loadImage(holder.ivDownloadUrl, info.getImageUrl(), ImageType.NULL);
			holder.tvDownloadSize.setText(info.getSize() + "M");
			if (info.getFileName().equals(TYPEPFACE_FT) || info.getFileName().equals(TYPEPFACE_JT)) {// 繁体和简体
				holder.viewDownload.setVisibility(View.GONE);
				holder.btnRed.setVisibility(View.GONE);
				holder.btnBlack.setVisibility(View.GONE);
				holder.tvNormalName.setVisibility(View.VISIBLE);
				holder.tvNormalName.setText(info.getName());
			} else {// 下载字体
				holder.viewDownload.setVisibility(View.VISIBLE);
				holder.tvNormalName.setVisibility(View.GONE);
				if (info.getPrecent() == 0) {// 等待下载
					holder.btnRed.setVisibility(View.GONE);
					holder.btnBlack.setVisibility(View.VISIBLE);
					holder.btnBlack.setText("等待");
				} else if (info.getPrecent() == -1) {// 还未开始
					holder.btnRed.setVisibility(View.VISIBLE);
					holder.btnBlack.setVisibility(View.GONE);
				} else if (info.getPrecent() == -2) {// 完成
					holder.btnRed.setVisibility(View.GONE);
					holder.btnBlack.setVisibility(View.GONE);
				} else if (info.getPrecent() == -3) {// 失败
					holder.btnRed.setVisibility(View.GONE);
					holder.btnBlack.setVisibility(View.VISIBLE);
					holder.btnBlack.setText("重试");
				} else {
					holder.btnRed.setVisibility(View.GONE);
					holder.btnBlack.setVisibility(View.VISIBLE);
					holder.btnBlack.setText(info.getPrecent() + "%");
				}
			}
			holder.btnRed.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					downloadTypeface(info);
				}
			});
			holder.btnBlack.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (info.getPrecent() == -3) {// 失败
						downloadTypeface(info);
					}
				}
			});
			holder.viewTypeface.setEnabled(info.getPrecent() == -2 ? true : false);
			holder.viewTypeface.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (info.getPrecent() == -2) {
						String selectTypeface = Cookies.getUserSetting().getUserTypeface();
						if (selectTypeface != info.getFileName()) {
							Cookies.getUserSetting().setUserTypeface(info.getFileName());
							adapter.notifyDataSetChanged();
						}
					}
				}
			});
			return contentView;
		}
	}

	private class ViewHolder {
		private View viewTypeface;
		private View viewDownload;
		private TextView tvNormalName;
		private TextView tvDownloadSize;
		private ImageView ivDownloadUrl;
		private ImageButton btnChoose;
		private Button btnBlack;
		private Button btnRed;
	}

}