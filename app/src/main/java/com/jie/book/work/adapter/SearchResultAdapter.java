package com.jie.book.work.adapter;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bond.bookcatch.vo.SearchResult;
import com.jie.book.work.R;
import com.jie.book.work.activity.BaseActivity;
import com.jie.book.work.activity.BookDetailActivity;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.read.BookCatchManager.DefaultInterface;
import com.jie.book.work.utils.ImageLoadUtil;
import com.jie.book.work.utils.ImageLoadUtil.ImageType;
import com.jie.book.work.utils.UIHelper;

public class SearchResultAdapter extends BaseAdapter {
	public static final String BOOK_DESC_ID = "book_desc_id";
	private BaseActivity activity;
	private List<SearchResult> bookSearchResult = new ArrayList<SearchResult>();

	public SearchResultAdapter(BaseActivity activity, List<SearchResult> bookSearchResult) {
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
			convertView = LayoutInflater.from(activity).inflate(
					Cookies.getReadSetting().isNightMode() ? R.layout.item_book_search_ef : R.layout.item_book_search,
					null);
			holder.ivBookIcon = (ImageView) convertView.findViewById(R.id.item_book_icon);
			holder.tvBookName = (TextView) convertView.findViewById(R.id.item_book_name);
			holder.tvBookInfo = (TextView) convertView.findViewById(R.id.item_book_info);
			holder.tvBookAuthor = (TextView) convertView.findViewById(R.id.item_book_author);
			holder.ibBookAdd = (ImageButton) convertView.findViewById(R.id.item_book_add);
			holder.ibBookRemove = (ImageButton) convertView.findViewById(R.id.item_book_remove);
			convertView.setTag(holder);
		} else {
			holder = (ViewHoldr) convertView.getTag();
		}
		final SearchResult result = bookSearchResult.get(position);
		if (result.getIsAlreadyStored() == null)
			activity.chatchManager.checkIfExists(result, activity);
		holder.ibBookAdd.setVisibility(result.getIsAlreadyStored() ? View.GONE : View.VISIBLE);
		holder.ibBookRemove.setVisibility(result.getIsAlreadyStored() ? View.VISIBLE : View.GONE);
		ImageLoadUtil.loadImage(holder.ivBookIcon, result.getImageUrl(), ImageType.BOOK);
		UIHelper.setText(holder.tvBookName, result.getBookName());
		UIHelper.setText(holder.tvBookInfo, result.getDesc(), "暂无详情");
		holder.tvBookAuthor.setText("作者： " + UIHelper.getUnEmptyString(result.getAuthor()));
		FunctionClick click = new FunctionClick(holder, result);
		holder.ibBookAdd.setOnClickListener(click);
		holder.ibBookRemove.setOnClickListener(click);
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				BookDetailActivity.luanch(activity, result);
			}
		});
		return convertView;
	}

	private class FunctionClick implements OnClickListener {
		ViewHoldr holder;
		SearchResult result;

		public FunctionClick(ViewHoldr holder, SearchResult result) {
			super();
			this.holder = holder;
			this.result = result;
		}

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.item_book_add:
				activity.chatchManager.addBookDesc(result, new DefaultInterface() {

					@Override
					public void getDefault(boolean haBack) {
						if (haBack) {
							result.setIsAlreadyStored(true);
							holder.ibBookAdd.setVisibility(View.GONE);
							holder.ibBookRemove.setVisibility(View.VISIBLE);
							activity.showToast("添加成功");
						} else {
							activity.showToast("添加失败");
						}
					}
				});
				break;
			case R.id.item_book_remove:
				activity.chatchManager.deleteBookDesc(result);
				result.setIsAlreadyStored(true);
				holder.ibBookAdd.setVisibility(View.VISIBLE);
				holder.ibBookRemove.setVisibility(View.GONE);
				activity.showToast("移出成功");
				break;
			}
		}

	}

	private class ViewHoldr {
		ImageView ivBookIcon;
		TextView tvBookName;
		TextView tvBookInfo;
		TextView tvBookAuthor;
		ImageButton ibBookAdd;
		ImageButton ibBookRemove;
	}

}
