package com.jie.book.work.local;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jie.book.work.R;
import com.jie.book.work.activity.BaseActivity;
import com.jie.book.work.application.BookActivityManager;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.entity.FileInfo;
import com.jie.book.work.utils.StringUtil;
import com.jie.book.work.utils.TaskExecutor;
import com.jie.book.work.utils.UIHelper;

public class LocalFileListActivity extends BaseActivity implements OnClickListener {
	private static final int SPITE_THREAD_COUNT = 5;
	private static final int MSG_SEARCH_TXT_ERRO = 0;
	private static final int MSG_SEARCH_TXT_SCUCESS = 1;
	private static final int MSG_SEARCH_TXT_COMPLETE = 2;
	private List<FileInfo> bookList = new ArrayList<FileInfo>();
	private List<FileInfo> chooseList = new ArrayList<FileInfo>();
	private ListView listView;
	private FileAdapter adapter;
	private TextView tvProgress;
	private ProgressBar pbProgress;
	private int compCount = 0;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (!isFinishing()) {
				switch (msg.what) {
				case MSG_SEARCH_TXT_ERRO:
					showToast("请检查SD卡是否存在！");
					tvProgress.setText("扫描本地书籍失败！");
					pbProgress.setVisibility(View.GONE);
					break;
				case MSG_SEARCH_TXT_SCUCESS:
					FileInfo info = (FileInfo) msg.obj;
					bookList.add(info);
					adapter.notifyDataSetChanged();
					tvProgress.setText("扫描到" + bookList.size() + "本TXT书籍");
					break;
				case MSG_SEARCH_TXT_COMPLETE:
					tvProgress.setText("扫描到" + bookList.size() + "本TXT书籍");
					pbProgress.setVisibility(View.GONE);
					break;
				}
			}
		}

	};

	public static void luanch(Activity content) {
		Intent intent = new Intent();
		intent.setClass(content, LocalFileListActivity.class);
		BookActivityManager.getInstance().goFoResult(content, intent, 0);
	}

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(Cookies.getReadSetting().isNightMode() ? R.layout.act_file_list_ef : R.layout.act_file_list);
		UIHelper.setTranStateBar(activity);
		initUI();
		initListener();
		initData();
	}

	@Override
	public void finish() {
		super.finish();
	}

	@Override
	protected void initUI() {
		listView = (ListView) findViewById(R.id.pull_refresh_list);
		tvProgress = (TextView) findViewById(R.id.file_list_search_text);
		pbProgress = (ProgressBar) findViewById(R.id.file_list_search_pb);
		adapter = new FileAdapter();
		listView.setAdapter(adapter);
	}

	@Override
	protected void initListener() {
		findViewById(R.id.file_list_back).setOnClickListener(this);
		findViewById(R.id.file_list_sure).setOnClickListener(this);
		findViewById(R.id.file_list_all).setOnClickListener(this);
	}

	@Override
	protected void initData() {
		getData();
	}

	private void getData() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File path = Environment.getExternalStorageDirectory();// 获得SD卡路径
			File[] files = path.listFiles();
			if (files != null && files.length > 0) {
				List<File> fileList = Arrays.asList(files);
				getSeachFile(fileList);
			} else {
				mHandler.sendEmptyMessage(MSG_SEARCH_TXT_ERRO);
			}
		} else {
			mHandler.sendEmptyMessage(MSG_SEARCH_TXT_ERRO);
		}

	}

	private void getSeachFile(final List<File> files) {
		if (files != null && files.size() > 0) {// 先判断目录是否为空，否则会报空指针
			final int fileSize = files.size();
			if (fileSize < 5) {
				TaskExecutor.getInstance().executeTask(new Runnable() {

					@Override
					public void run() {
						getFileInfo(files);
						mHandler.sendEmptyMessage(MSG_SEARCH_TXT_COMPLETE);
					}
				});
			} else {
				final int teamCount = fileSize / SPITE_THREAD_COUNT;
				for (int i = 0; i < SPITE_THREAD_COUNT; i++) {
					final int beginIndex = teamCount * i;
					final int endIndex = i == SPITE_THREAD_COUNT - 1 ? fileSize - 1 : (i + 1) * teamCount - 1;
					TaskExecutor.getInstance().executeTask(new Runnable() {

						@Override
						public void run() {
							List<File> teamFiles = files.subList(beginIndex, endIndex);
							if (teamFiles != null && teamFiles.size() > 0) {
								getFileInfo(teamFiles);
							}
							compCount++;
							if (compCount == SPITE_THREAD_COUNT)
								mHandler.sendEmptyMessage(MSG_SEARCH_TXT_COMPLETE);
						}
					});
				}
			}
		} else {
			mHandler.sendEmptyMessage(MSG_SEARCH_TXT_ERRO);
		}
	}

	public void getFileInfo(List<File> files) {
		if (isFinishing())
			return;
		for (File file : files) {
			if (file.isDirectory()) {
				File[] sfiles = file.listFiles();
				if (sfiles != null && sfiles.length > 0) {
					getFileInfo(Arrays.asList(file.listFiles()));
				}
			} else {
				String fileName = file.getName();
				if (StringUtil.isContainsChinese(fileName)) {
					if (fileName.endsWith(".txt")) {
						if (!LocalBookManager.checkBookExist(file.getAbsolutePath())) {
							FileInfo info = new FileInfo();
							info.setFileName(fileName.substring(0, fileName.lastIndexOf(".")));
							info.setFileUrl(file.getAbsolutePath());
							if (file.length() < 1024) {
								info.setFileSize(file.length() + "B");
							} else {
								if (file.length() < 1024 * 1024) {
									float size = file.length() / 1024;
									info.setFileSize(size + "KB");
								} else {
									float size = file.length() / (1024 * 1024);
									info.setFileSize(size + "MB");
								}
							}
							Message msg = Message.obtain();
							msg.what = MSG_SEARCH_TXT_SCUCESS;
							msg.obj = info;
							mHandler.sendMessage(msg);
						}
					}
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.file_list_back:
			finishInAnim();
			break;
		case R.id.file_list_sure:
			if (chooseList != null && chooseList.size() > 0) {
				for (FileInfo fileInfo : chooseList) {
					LocalBookManager.saveBook(fileInfo.getFileName(), fileInfo.getFileUrl());
				}
				showToast("导入成功");
				finishInAnim();
			} else {
				showToast("请选择要导入的书籍");
			}
			break;
		case R.id.file_list_all:
			if (bookList.size() > 0) {
				chooseList.clear();
				chooseList.addAll(bookList);
				adapter.notifyDataSetChanged();
			}
			break;
		}
	}

	private class FileAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return bookList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHoldr holder = null;
			if (convertView == null) {
				holder = new ViewHoldr();
				convertView = LayoutInflater.from(activity).inflate(
						Cookies.getReadSetting().isNightMode() ? R.layout.item_file_list_ef : R.layout.item_file_list,
						null);
				holder.tvName = (TextView) convertView.findViewById(R.id.item_book_name);
				holder.tvSize = (TextView) convertView.findViewById(R.id.item_book_size);
				holder.tvPath = (TextView) convertView.findViewById(R.id.item_book_path);
				holder.btnAdd = (ImageButton) convertView.findViewById(R.id.item_book_remove);
				convertView.setTag(holder);
			} else {
				holder = (ViewHoldr) convertView.getTag();
			}
			final FileInfo fileInfo = bookList.get(position);
			holder.btnAdd.setImageResource(chooseList.contains(fileInfo) ? R.drawable.btn_typeface_choose
					: R.drawable.btn_typeface_unchoose);
			holder.tvName.setText(fileInfo.getFileName());
			holder.tvSize.setText(fileInfo.getFileSize());
			holder.tvPath.setText(fileInfo.getFileUrl());
			AddClick click = new AddClick(holder.btnAdd, fileInfo);
			holder.btnAdd.setOnClickListener(click);
			return convertView;
		}
	}

	private class ViewHoldr {
		TextView tvName;
		TextView tvSize;
		TextView tvPath;
		ImageButton btnAdd;
	}

	private class AddClick implements OnClickListener {
		ImageButton btnAdd;
		FileInfo fileInfo;

		public AddClick(ImageButton btnAdd, FileInfo fileInfo) {
			this.btnAdd = btnAdd;
			this.fileInfo = fileInfo;
		}

		@Override
		public void onClick(View arg0) {
			btnAdd.setImageResource(chooseList.contains(fileInfo) ? R.drawable.btn_typeface_unchoose
					: R.drawable.btn_typeface_choose);
			delBtnAdd(fileInfo);
		}

	}

	private void delBtnAdd(FileInfo fileInfo) {
		List<FileInfo> newChooseList = new ArrayList<FileInfo>(chooseList);
		if (chooseList.contains(fileInfo)) {
			newChooseList.remove(fileInfo);
		} else {
			newChooseList.add(fileInfo);
		}
		chooseList = newChooseList;
	}

}