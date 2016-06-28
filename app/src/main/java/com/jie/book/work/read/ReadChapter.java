package com.jie.book.work.read;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bond.bookcatch.BookCatcher;
import com.bond.bookcatch.vo.BookCatalog;
import com.bond.bookcatch.vo.BookMark;
import com.jie.book.work.R;
import com.jie.book.work.activity.ReadActivity;
import com.jie.book.work.read.BookCatchManager.DefaultInterface;
import com.jie.book.work.read.BookCatchManager.RangeInterface;
import com.jie.book.work.utils.StringUtil;
import com.jie.book.work.utils.TimeUtil;
import com.jie.book.work.utils.UIHelper;
import com.jie.book.work.utils.UIHelper.OnDialogClickListener;

public class ReadChapter implements OnClickListener {
	private List<BookCatalog> catalogList = new ArrayList<BookCatalog>();
	private List<BookMark> bookMarks = new ArrayList<BookMark>();
	private List<Object> datas = new ArrayList<Object>();
	private ListView listView;
	private ReadActivity readActivity;
	private BookChapterListAdapter adapter;
	private TextView tvBookName, tvBookAuthor;
	private TextView tvBookChapter, tvBookMark;
	private Button btnButtomTop, btnButtomButtom;
	private View chapterView;
	private ImageView ivButtomDivider1, ivButtomDivider2;
	private ImageView ivMarkDivider1, ivMarkDivider2;
	private ImageView ivMarkIndex1, ivMarkIndex2;
	private ReadTheme theme;
	private BookCatalog lastBookCatalog;
	private Dialog dialog;
	private int[] offlineRange = new int[] { 0, 0 };

	public View getChapterView() {
		return chapterView;
	}

	public void setChapterView(View chapterView) {
		this.chapterView = chapterView;
	}

	public List<BookCatalog> getCatalogList() {
		return catalogList;
	}

	public void setCatalogList(LinkedList<BookCatalog> catalogList) {
		this.catalogList = catalogList;
	}

	@SuppressWarnings("deprecation")
	public void setTheme(ReadTheme theme) {
		this.theme = theme;
		tvBookName.setTextColor(theme.getTextColorRes());
		tvBookAuthor.setTextColor(theme.getTextColorRes());
		btnButtomTop.setTextColor(theme.getTextColorRes());
		tvBookChapter.setTextColor(theme.getTextColorRes());
		tvBookMark.setTextColor(theme.getTextColorRes());
		btnButtomButtom.setTextColor(theme.getTextColorRes());
		ivButtomDivider1.setBackgroundResource(theme.getBgDivider());
		ivButtomDivider2.setBackgroundResource(theme.getBgDivider());
		ivMarkDivider1.setBackgroundResource(theme.getBgDivider());
		ivMarkDivider2.setBackgroundResource(theme.getBgDivider());
		ivMarkIndex1.setBackgroundResource(theme.getMarkIndicator());
		ivMarkIndex2.setBackgroundResource(theme.getMarkIndicator());
		if (theme.getId() == 2)
			chapterView.setBackgroundResource(theme.getBgColorRes());
		else
			chapterView.setBackgroundDrawable(UIHelper.color2Drawble(theme.getBgColorRes()));
		listView.setDivider(readActivity.getResources().getDrawable(theme.getBgDivider()));
		listView.setDividerHeight(1);
		adapter.notifyDataSetChanged();
	}

	public ReadChapter(ReadActivity readActivity, View chapterView) {
		this.readActivity = readActivity;
		this.chapterView = chapterView;
		listView = (ListView) chapterView.findViewById(R.id.pull_refresh_list);
		tvBookName = (TextView) chapterView.findViewById(R.id.chapter_list_book_name);
		tvBookAuthor = (TextView) chapterView.findViewById(R.id.chapter_list_book_author);
		tvBookChapter = (TextView) chapterView.findViewById(R.id.chapter_list_book_chapter);
		tvBookMark = (TextView) chapterView.findViewById(R.id.chapter_list_book_mark);
		btnButtomTop = (Button) chapterView.findViewById(R.id.chapter_list_top_btn);
		btnButtomButtom = (Button) chapterView.findViewById(R.id.chapter_list_buttom_btn);
		ivButtomDivider1 = (ImageView) chapterView.findViewById(R.id.chapter_list_buttom_divider1);
		ivButtomDivider2 = (ImageView) chapterView.findViewById(R.id.chapter_list_buttom_divider2);
		ivMarkDivider1 = (ImageView) chapterView.findViewById(R.id.chapter_list_book_head_divider1);
		ivMarkDivider2 = (ImageView) chapterView.findViewById(R.id.chapter_list_book_head_divider2);
		ivMarkIndex1 = (ImageView) chapterView.findViewById(R.id.chapter_list_book_left);
		ivMarkIndex2 = (ImageView) chapterView.findViewById(R.id.chapter_list_book_right);
		adapter = new BookChapterListAdapter();
		listView.setAdapter(adapter);
		chapterView.findViewById(R.id.chapter_list_top_btn).setOnClickListener(this);
		chapterView.findViewById(R.id.chapter_list_buttom_btn).setOnClickListener(this);
		chapterView.findViewById(R.id.chapter_list_drag).setOnClickListener(this);
		chapterView.findViewById(R.id.chapter_list_book_chapter).setOnClickListener(this);
		chapterView.findViewById(R.id.chapter_list_book_mark).setOnClickListener(this);
	}

	public void getChapterList() {
		ivMarkIndex1.setVisibility(View.VISIBLE);
		ivMarkIndex2.setVisibility(View.GONE);
		readActivity.chatchManager.refreshBookDesc(readActivity.getBookDesc());
		setBookInfo();
		getOnlineChapter();
	}

	public void setBookInfo() {
		if (!StringUtil.isEmpty(readActivity.getBookDesc().getBookName()))
			tvBookName.setText(readActivity.getBookDesc().getBookName());
		if (!StringUtil.isEmpty(readActivity.getBookDesc().getAuthor()))
			tvBookAuthor.setText(readActivity.getBookDesc().getAuthor());
	}

	public void getOnlineChapter() {
		if (readActivity.getBookDesc().getCatalogs() == null) {
			dialog = UIHelper.showProgressDialog(dialog, readActivity);
			readActivity.chatchManager.loadBookCatalog(readActivity.getBookDesc(), new DefaultInterface() {

				@Override
				public void getDefault(boolean haBack) {
					if (readActivity.getBookDesc() != null && readActivity.getBookDesc().getCatalogs() != null) {
						lastBookCatalog = readActivity.getBookDesc().getLastReadCatalog();
						catalogList.clear();
						catalogList.addAll(readActivity.getBookDesc().getCatalogs());
						datas.clear();
						datas.addAll(catalogList);
						adapter.notifyDataSetChanged();
						if (lastBookCatalog != null)
							listView.setSelection(lastBookCatalog.getIndex() == 0 ? 0 : lastBookCatalog.getIndex());
						readActivity.chatchManager.getOfflineCatalogRange(readActivity.getBookDesc(),
								new RangeInterface() {

									@Override
									public void getRange(int[] range) {
										offlineRange = range;
										adapter.notifyDataSetChanged();
										UIHelper.cancleProgressDialog(dialog);
									}
								});
					} else {
						UIHelper.cancleProgressDialog(dialog);
					}
				}
			});
		} else {
			offlineRange = BookCatcher.getOfflineCatalogRange(readActivity.getBookDesc(), readActivity);
			catalogList.clear();
			catalogList.addAll(readActivity.getBookDesc().getCatalogs());
			datas.clear();
			datas.addAll(catalogList);
			adapter.notifyDataSetChanged();
			lastBookCatalog = readActivity.getBookDesc().getLastReadCatalog();
			if (lastBookCatalog != null)
				listView.setSelection(lastBookCatalog.getIndex() == 0 ? 0 : lastBookCatalog.getIndex());
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.chapter_list_top_btn:
			if (catalogList.size() > 0)
				listView.setSelection(0);
			break;
		case R.id.chapter_list_buttom_btn:
			if (catalogList.size() > 0)
				listView.setSelection(listView.getCount() - 1);
			break;
		case R.id.chapter_list_drag:
			readActivity.toggoleChapterList();
			break;
		case R.id.chapter_list_book_chapter:
			ivMarkIndex1.setVisibility(View.VISIBLE);
			ivMarkIndex2.setVisibility(View.GONE);
			datas.clear();
			datas.addAll(catalogList);
			adapter.notifyDataSetChanged();
			lastBookCatalog = readActivity.getBookDesc().getLastReadCatalog();
			if (lastBookCatalog != null)
				listView.setSelection(lastBookCatalog.getIndex() == 0 ? 0 : lastBookCatalog.getIndex());
			break;
		case R.id.chapter_list_book_mark:
			ivMarkIndex1.setVisibility(View.GONE);
			ivMarkIndex2.setVisibility(View.VISIBLE);
			bookMarks = readActivity.chatchManager.getBookMarks(readActivity.getBookDesc());
			datas.clear();
			datas.addAll(bookMarks);
			adapter.notifyDataSetChanged();
			break;
		}

	}

	private class BookChapterListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return datas.size();
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
		public int getItemViewType(int position) {
			Object obj = datas.get(position);
			if (obj instanceof BookCatalog) {
				return 0;
			} else {
				return 1;
			}
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHoldrChapter chapterHolder = null;
			ViewHoldrMark markHolder = null;
			if (convertView == null) {
				if (getItemViewType(position) == 0) {
					chapterHolder = new ViewHoldrChapter();
					convertView = LayoutInflater.from(readActivity).inflate(R.layout.item_view_chapter_list, null);
					chapterHolder.ivBookChapterRound = (ImageView) convertView
							.findViewById(R.id.item_chapter_list_ruond);
					chapterHolder.tvBookChapterName = (TextView) convertView.findViewById(R.id.item_chapter_list_name);
					chapterHolder.tvBookChapterNum = (TextView) convertView.findViewById(R.id.item_chapter_list_num);
					convertView.setTag(chapterHolder);
				} else {
					markHolder = new ViewHoldrMark();
					convertView = LayoutInflater.from(readActivity).inflate(R.layout.item_view_mark_list, null);
					markHolder.tvBookMarkChapter = (TextView) convertView
							.findViewById(R.id.item_chapter_list_mark_chapter);
					markHolder.tvBookMarkTime = (TextView) convertView.findViewById(R.id.item_chapter_list_mark_time);
					markHolder.tvBookMarkContent = (TextView) convertView
							.findViewById(R.id.item_chapter_list_mark_content);
					convertView.setTag(markHolder);
				}
			} else {
				if (getItemViewType(position) == 0) {
					chapterHolder = (ViewHoldrChapter) convertView.getTag();
				} else {
					markHolder = (ViewHoldrMark) convertView.getTag();
				}
			}

			if (getItemViewType(position) == 0) {
				final BookCatalog catalog = (BookCatalog) datas.get(position);
				chapterHolder.tvBookChapterName.setText(catalog.getTitle());
				chapterHolder.tvBookChapterNum.setText(catalog.getIndex() + 1 + ".");
				if (offlineRange[1] != 0) {// 有缓存记录
					if (catalog.getIndex() >= offlineRange[0] && catalog.getIndex() < offlineRange[1]) {
						chapterHolder.ivBookChapterRound
								.setBackgroundResource(R.drawable.icon_chapter_list_round_cache);
						chapterHolder.tvBookChapterName.setTextColor(theme.getTextColorRes());
						chapterHolder.tvBookChapterNum.setTextColor(theme.getTextColorRes());
					} else {
						chapterHolder.ivBookChapterRound.setBackgroundResource(theme.getChapterRoundDrawble());
						chapterHolder.tvBookChapterName.setTextColor(theme.getTextColorRes());
						chapterHolder.tvBookChapterNum.setTextColor(theme.getTextColorRes());
					}
				} else {// 没有缓存记录
					chapterHolder.ivBookChapterRound.setBackgroundResource(theme.getChapterRoundDrawble());
					chapterHolder.tvBookChapterName.setTextColor(theme.getTextColorRes());
					chapterHolder.tvBookChapterNum.setTextColor(theme.getTextColorRes());
				}

				if (lastBookCatalog != null && lastBookCatalog.getIndex() == catalog.getIndex()) {
					chapterHolder.ivBookChapterRound.setBackgroundResource(R.drawable.icon_book_chapter_now);
					chapterHolder.tvBookChapterName.setTextColor(readActivity.getResources().getColor(
							R.color.book_default_red));
					chapterHolder.tvBookChapterNum.setTextColor(readActivity.getResources().getColor(
							R.color.book_default_red));
				}

				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						readActivity.reloadData(catalog, true);
					}
				});
			} else {
				final BookMark mark = (BookMark) datas.get(position);
				markHolder.tvBookMarkChapter.setTextColor(theme.getTextColorRes());
				markHolder.tvBookMarkTime.setTextColor(theme.getTextColorRes());
				markHolder.tvBookMarkContent.setTextColor(theme.getTextColorRes());
				markHolder.tvBookMarkChapter.setText("第" + String.valueOf(mark.getIndex() + 1) + "章");
				markHolder.tvBookMarkContent.setText(mark.getContent());
				markHolder.tvBookMarkTime.setText(TimeUtil.fmttoCN(mark.getTime()));
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						readActivity.reloadData(mark.getIndex(), (int) mark.getPosition());
					}
				});
				convertView.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View arg0) {
						UIHelper.showTowButtonDialog(readActivity, "是否删除书签", "取消", "确定", true, null,
								new OnDialogClickListener() {

									@Override
									public void onClick() {
										readActivity.chatchManager.deletBookMark(mark);
										bookMarks.remove(mark);
										datas.clear();
										datas.addAll(bookMarks);
										adapter.notifyDataSetChanged();
									}
								});
						return false;
					}
				});
			}

			return convertView;
		}
	}

	private class ViewHoldrChapter {
		ImageView ivBookChapterRound;
		TextView tvBookChapterName;
		TextView tvBookChapterNum;
	}

	private class ViewHoldrMark {
		TextView tvBookMarkChapter;
		TextView tvBookMarkTime;
		TextView tvBookMarkContent;
	}

}
