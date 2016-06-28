package com.jie.book.work.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bond.bookcatch.vo.BookDesc;
import com.bond.bookcatch.vo.SearchResult;
import com.jie.book.work.R;
import com.jie.book.work.adapter.SearchResultAdapter;
import com.jie.book.work.application.BookActivityManager;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.read.BookCatchManager.BookDescInterface;
import com.jie.book.work.read.BookCatchManager.DefaultInterface;
import com.jie.book.work.utils.DownloadUtil;
import com.jie.book.work.utils.ImageLoadUtil;
import com.jie.book.work.utils.ImageLoadUtil.ImageType;
import com.jie.book.work.utils.MiscUtils;
import com.jie.book.work.utils.StatisticUtil;
import com.jie.book.work.utils.TimeUtil;
import com.jie.book.work.utils.UIHelper;

@SuppressLint("HandlerLeak")
public class BookDetailActivity extends BaseActivity implements OnClickListener {
	public static final int REQUEST_CODE = 1001;
	private static final String LUANCH_SEARCH_RESULT = "luanch_search_result";
	private static final String LUANCH_BOOK_DESC = "luanch_book_desc";
	private TextView tvBookTime, tvBookAuthor, tvBookStarus, tvBookDesc, tvBack, tvLastChapter, tvAddBook;
	private ImageButton btnAddShelf;
	private SearchResult searchResult;
	private BookDesc bookDesc;
	private ImageView bookIocn;
	private boolean isFrist = true;

	public static void luanch(Activity content, SearchResult searchResult) {
		Intent intent = new Intent();
		intent.putExtra(LUANCH_SEARCH_RESULT, searchResult);
		intent.setClass(content, BookDetailActivity.class);
		BookActivityManager.getInstance().goFoResult(content, intent, REQUEST_CODE);
	}

	public static void luanch(Activity content, BookDesc bookDesc) {
		Intent intent = new Intent();
		intent.putExtra(LUANCH_BOOK_DESC, bookDesc);
		intent.setClass(content, BookDetailActivity.class);
		BookActivityManager.getInstance().goTo(content, intent);
	}

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		MiscUtils.goneSoftKey(activity);
		setContentView(Cookies.getReadSetting().isNightMode() ? R.layout.act_book_detail_ef : R.layout.act_book_detail);
		UIHelper.setTranStateBar(activity);
		initUI();
		initListener();
		initData();
	}

	@Override
	protected void onResume() {
		super.onResume();
		getData();
	}

	@Override
	protected void initUI() {
		tvBack = (TextView) findViewById(R.id.book_detail_back);
		tvBookTime = (TextView) findViewById(R.id.book_detail_last_time_name);
		tvBookAuthor = (TextView) findViewById(R.id.book_detail_author_name);
		tvBookStarus = (TextView) findViewById(R.id.book_detail_satus_name);
		tvBookDesc = (TextView) findViewById(R.id.book_detail_desc_tv);
		tvAddBook = (TextView) findViewById(R.id.book_detail_add_shelf_text);
		btnAddShelf = (ImageButton) findViewById(R.id.book_detail_add_shelf_btn);
		bookIocn = (ImageView) findViewById(R.id.item_book_icon);
		tvLastChapter = (TextView) findViewById(R.id.book_detail_last_chapter_name);
	}

	@Override
	protected void initData() {
		if (getIntent().hasExtra(LUANCH_SEARCH_RESULT))
			searchResult = (SearchResult) getIntent().getSerializableExtra(LUANCH_SEARCH_RESULT);
		if (getIntent().hasExtra(LUANCH_BOOK_DESC))
			bookDesc = (BookDesc) getIntent().getSerializableExtra(LUANCH_BOOK_DESC);
	}

	public void getData() {
		if (isFrist) {
			if (searchResult != null) {
				dialog = UIHelper.showProgressDialog(dialog, activity);
				chatchManager.getBookDescBySearchResult(searchResult, new BookDescInterface() {

					@Override
					public void getBookDesc(BookDesc desc) {
						bookDesc = desc;
						setBookDesc();
						UIHelper.cancleProgressDialog(dialog);
						isFrist = false;
					}
				});
			} else {
				if (bookDesc != null) {
					bookDesc = chatchManager.reLoadBookDesc(bookDesc);
					setBookDesc();
					isFrist = false;
				}
			}
		} else {
			if (bookDesc != null) {
				int orldStore = bookDesc.getIsStore();
				bookDesc = chatchManager.reLoadBookDesc(bookDesc);
				setBookDesc();
				if (orldStore != bookDesc.getIsStore()) {
					Intent intent = new Intent();
					intent.putExtra(SearchResultAdapter.BOOK_DESC_ID, bookDesc.getGid());
					setResult(RESULT_OK, intent);
				}
			}
		}
	}

	public void setBookDesc() {
		if (bookDesc != null) {
			UIHelper.setText(tvBack, bookDesc.getBookName());
			UIHelper.setText(tvBookAuthor, bookDesc.getAuthor() + "作品", "未知作者作品");
			UIHelper.setText(tvBookTime, TimeUtil.fmttoCN(bookDesc.getLastUpdateTime()));
			UIHelper.setText(tvBookStarus, bookDesc.getStatus());
			UIHelper.setText(tvBookDesc, bookDesc.getDesc(), "暂无详情");
			UIHelper.setText(tvLastChapter, bookDesc.getLastChapterTitle());
			btnAddShelf.setBackgroundResource(bookDesc.getIsStore() == 0 ? R.drawable.btn_detail_add
					: R.drawable.btn_detail_remove);
			tvAddBook.setText(bookDesc.getIsStore() == 0 ? "加入" : "移出");
			ImageLoadUtil.loadImage(bookIocn, bookDesc.getImageUrl(), ImageType.BOOK);
		} else {
			showToast("获取详情失败");
		}
	}

	@Override
	protected void initListener() {
		tvBack.setOnClickListener(this);
		findViewById(R.id.book_shlef_bar).setOnClickListener(this);
		findViewById(R.id.book_detail_read_now_btn).setOnClickListener(this);
		findViewById(R.id.book_detail_chapter_layout).setOnClickListener(this);
		findViewById(R.id.book_detail_download_layout).setOnClickListener(this);
		findViewById(R.id.book_detail_add_shelf_btn).setOnClickListener(this);
		findViewById(R.id.book_detail_lisetn_btn).setOnClickListener(this);
		findViewById(R.id.book_detail_author_name).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.book_detail_add_shelf_btn:
			if (bookDesc != null)
				if (bookDesc.getIsStore() == 0) {
					chatchManager.addBookDesc(bookDesc, new DefaultInterface() {

						@Override
						public void getDefault(boolean haBack) {
							if (haBack) {
								btnAddShelf.setBackgroundResource(R.drawable.btn_detail_remove);
								tvAddBook.setText("移出");
								bookDesc.setIsStore(1);
								showToast("添加成功");
								Intent intent = new Intent();
								intent.putExtra(SearchResultAdapter.BOOK_DESC_ID, bookDesc.getGid());
								setResult(RESULT_OK, intent);
							} else {
								showToast("添加失败");
							}
						}
					});
				} else {
					chatchManager.deleteBookDesc(bookDesc, new DefaultInterface() {

						@Override
						public void getDefault(boolean haBack) {
							if (haBack) {
								btnAddShelf.setBackgroundResource(R.drawable.btn_detail_add);
								tvAddBook.setText("加入");
								bookDesc.setIsStore(0);
								showToast("移出成功");
								Intent intent = new Intent();
								intent.putExtra(SearchResultAdapter.BOOK_DESC_ID, bookDesc.getGid());
								setResult(RESULT_OK, intent);
							} else {
								showToast("移出失败");
							}
						}
					});
				}
			break;
		case R.id.book_detail_read_now_btn:
			if (bookDesc != null)
				ReadActivity.luanch(activity, bookDesc, null);
			break;
		case R.id.book_detail_chapter_layout:
			if (bookDesc != null)
				ChapterListActivity.luanch(activity, bookDesc);
			break;
		case R.id.book_detail_download_layout:
			if (bookDesc != null) {
				DownloadUtil downloadUtil = new DownloadUtil(activity, activity.findViewById(R.id.book_detail_root),
						bookDesc, 0);
				downloadUtil.downloadAllFromShelf();
			}
			break;
		case R.id.book_detail_back:
			finishInAnim();
			break;
		case R.id.book_shlef_bar:
			StatisticUtil.sendEvent(activity, StatisticUtil.BAIDU_BAR);
			if (bookDesc != null)
				WebActivity.launcher2(activity, bookDesc.getBookName());
			break;
		case R.id.book_detail_lisetn_btn:
			StatisticUtil.sendEvent(activity, StatisticUtil.BOOK_LISETN);
			UIHelper.luanchListen(bookDesc, activity);
			break;
		case R.id.book_detail_author_name:
			if (bookDesc != null)
				BookSearchActivity.luanch(activity, bookDesc.getAuthor(), true);
			break;
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent intent) {
		super.onActivityResult(arg0, arg1, intent);
		if (arg0 == BookDetailActivity.REQUEST_CODE && arg1 == RESULT_OK) {

		}
	}
}