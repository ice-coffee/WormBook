package com.jie.book.work.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bond.bookcatch.vo.BookDesc;
import com.jie.book.work.R;
import com.jie.book.work.application.BookActivityManager;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.read.BookCatchManager.BookDescListInterface;
import com.jie.book.work.utils.ImageLoadUtil;
import com.jie.book.work.utils.UIHelper;
import com.jie.book.work.utils.ImageLoadUtil.ImageType;

public class KeepAddActivity extends BaseActivity implements OnClickListener {
	private List<BookDesc> bookList = new ArrayList<BookDesc>();
	private List<BookDesc> chooseList = new ArrayList<BookDesc>();
	private ListView listView;
	private BookKeepAdapter adapter;

	public static void luanch(Activity content) {
		Intent intent = new Intent();
		intent.setClass(content, KeepAddActivity.class);
		BookActivityManager.getInstance().goFoResult(content, intent, 0);
	}

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(Cookies.getReadSetting().isNightMode() ? R.layout.act_add_keep_ef : R.layout.act_add_keep);
		UIHelper.setTranStateBar(activity);
		initUI();
		initListener();
		initData();
	}

	@Override
	protected void initUI() {
		listView = (ListView) findViewById(R.id.pull_refresh_list);
		adapter = new BookKeepAdapter();
		listView.setAdapter(adapter);
	}

	@Override
	protected void initListener() {
		findViewById(R.id.add_keep_back).setOnClickListener(this);
		findViewById(R.id.add_keep_sure).setOnClickListener(this);
	}

	@Override
	protected void initData() {
		getData();
	}

	private void getData() {

		chatchManager.getBookShelfList(new BookDescListInterface() {

			@Override
			public void getBookDescList(List<BookDesc> list) {
				if (list != null && list.size() > 0) {
					for (BookDesc desc : list) {
						if (!desc.isLocalBook())
							bookList.add(desc);
					}
					adapter.notifyDataSetChanged();
				}
			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_keep_back:
			finishInAnim();
			break;
		case R.id.add_keep_sure:
			chatchManager.moveBookToKeepTime(chooseList, Cookies.getUserSetting().getBookKeepTime());
			finishInAnim();
			break;
		}
	}

	private class BookKeepAdapter extends BaseAdapter {

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
						Cookies.getReadSetting().isNightMode() ? R.layout.item_keep_add_ef : R.layout.item_keep_add,
						null);
				holder.ivBookIcon = (ImageView) convertView.findViewById(R.id.item_book_icon);
				holder.tvName = (TextView) convertView.findViewById(R.id.item_book_name);
				holder.btnAdd = (ImageButton) convertView.findViewById(R.id.item_book_remove);
				convertView.setTag(holder);
			} else {
				holder = (ViewHoldr) convertView.getTag();
			}
			BookDesc desc = bookList.get(position);
			ImageLoadUtil.loadImage(holder.ivBookIcon, desc.getImageUrl(), ImageType.BOOK);
			holder.btnAdd.setImageResource(chooseList.contains(desc) ? R.drawable.btn_typeface_choose
					: R.drawable.btn_typeface_unchoose);
			holder.tvName.setText(desc.getBookName());
			AddClick click = new AddClick(holder.btnAdd, desc);
			holder.btnAdd.setOnClickListener(click);
			return convertView;
		}
	}

	private class ViewHoldr {
		ImageView ivBookIcon;
		TextView tvName;
		ImageButton btnAdd;
	}

	private class AddClick implements OnClickListener {
		ImageButton btnAdd;
		BookDesc desc;

		public AddClick(ImageButton btnAdd, BookDesc desc) {
			this.btnAdd = btnAdd;
			this.desc = desc;
		}

		@Override
		public void onClick(View arg0) {
			btnAdd.setImageResource(chooseList.contains(desc) ? R.drawable.btn_typeface_unchoose
					: R.drawable.btn_typeface_choose);
			delBtnAdd(desc);
		}

	}

	private void delBtnAdd(BookDesc desc) {
		List<BookDesc> newChooseList = new ArrayList<BookDesc>(chooseList);
		if (chooseList.contains(desc)) {
			newChooseList.remove(desc);
		} else {
			newChooseList.add(desc);
		}
		chooseList = newChooseList;
	}

}