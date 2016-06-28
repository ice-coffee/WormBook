package com.jie.book.work.adapter;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bond.bookcatch.vo.BookDesc;
import com.jie.book.work.R;
import com.jie.book.work.activity.BaseActivity;
import com.jie.book.work.activity.BookDetailActivity;
import com.jie.book.work.utils.ImageLoadUtil;
import com.jie.book.work.utils.ImageLoadUtil.ImageType;
import com.jie.book.work.utils.UIHelper;

public class BookDescAdapter extends BaseAdapter {
	private BaseActivity activity;
	private List<BookDesc> bookSearchResult = new ArrayList<BookDesc>();

	public BookDescAdapter(BaseActivity activity, List<BookDesc> bookSearchResult) {
		this.activity = activity;
		this.bookSearchResult = bookSearchResult;
	}

	@Override
	public int getCount() {
		return bookSearchResult.size();
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHoldr holder = null;
		if (convertView == null) {
			holder = new ViewHoldr();
			convertView = LayoutInflater.from(activity).inflate(R.layout.item_book_search, null);
			holder.ivBookIcon = (ImageView) convertView.findViewById(R.id.item_book_icon);
			holder.tvBookName = (TextView) convertView.findViewById(R.id.item_book_name);
			holder.tvBookInfo = (TextView) convertView.findViewById(R.id.item_book_info);
			holder.tvBookAuthor = (TextView) convertView.findViewById(R.id.item_book_author);
			convertView.setTag(holder);
		} else {
			holder = (ViewHoldr) convertView.getTag();
		}

		final BookDesc result = bookSearchResult.get(position);
		ImageLoadUtil.loadImage(holder.ivBookIcon, result.getImageUrl(), ImageType.BOOK);
		UIHelper.setText(holder.tvBookName, result.getBookName());
		UIHelper.setText(holder.tvBookInfo, result.getDesc(), "暂无详情");
		holder.tvBookAuthor.setText("作者： " + UIHelper.getUnEmptyString(result.getAuthor()));
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				BookDetailActivity.luanch(activity, result);
			}
		});
		return convertView;
	}

	private class ViewHoldr {
		ImageView ivBookIcon;
		TextView tvBookName;
		TextView tvBookInfo;
		TextView tvBookAuthor;
	}

}
