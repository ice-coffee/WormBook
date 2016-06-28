package com.jie.book.work.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.bond.bookcatch.BookType;
import com.bond.bookcatch.vo.SearchResult;
import com.jie.book.work.R;
import com.jie.book.work.adapter.SearchResultAdapter;
import com.jie.book.work.application.BookActivityManager;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.read.BookCatchManager.SearchResultInterface;
import com.jie.book.work.utils.UIHelper;
import com.jie.book.work.view.EmptyPage;
import com.jie.book.work.view.EmptyPage.onReLoadListener;
import com.jie.book.work.view.pullrefresh.PullToRefreshButtomListView;
import com.jie.book.work.view.pullrefresh.PullToRefreshButtomListView.onRefreshLoadListener;

public class BookTypeResultActivity extends BaseActivity implements OnClickListener {
	private static final String LUANCH_POSITION = "luanchPosition";
	private static final String LUANCH_TITLE = "luanchTitle";
	private List<SearchResult> bookSearchResult = new ArrayList<SearchResult>();
	private PullToRefreshButtomListView searchListView;
	private SearchResultAdapter adapter;
	private EmptyPage emptyPage;
	private TextView tvBack;
	private int pageNo = 1;
	private int position = 0;
	private String title;

	public static void luanch(Activity content, int position, String title) {
		Intent intent = new Intent();
		intent.putExtra(LUANCH_POSITION, position);
		intent.putExtra(LUANCH_TITLE, title);
		intent.setClass(content, BookTypeResultActivity.class);
		BookActivityManager.getInstance().goTo(content, intent);
	}

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(Cookies.getReadSetting().isNightMode() ? R.layout.act_book_result_ef : R.layout.act_book_result);
		UIHelper.setTranStateBar(activity);
		initUI();
		initListener();
		initData();
	}

	@Override
	protected void initUI() {
		searchListView = (PullToRefreshButtomListView) findViewById(R.id.pull_refresh_list);
		tvBack = (TextView) findViewById(R.id.result_head_back);
		title = getIntent().getStringExtra(LUANCH_TITLE);
		position = getIntent().getIntExtra(LUANCH_POSITION, 0);
		tvBack.setText(title);
		emptyPage = new EmptyPage(activity);
		emptyPage.setOnReLoadListener(new onReLoadListener() {

			@Override
			public void onload() {
				pageNo = 1;
				getRankBook();
			}
		});

	}

	@Override
	protected void initData() {
		adapter = new SearchResultAdapter(activity, bookSearchResult);
		searchListView.setAdapter(adapter);
		searchListView.setRefreshing();
	}

	@Override
	protected void initListener() {
		tvBack.setOnClickListener(this);
		searchListView.setonRefreshLoadListener(new onRefreshLoadListener() {

			@Override
			public void onRefreshing() {
				pageNo = 1;
				getRankBook();
			}

			@Override
			public void onLoadMore() {
				getRankBook();
			}
		});
	}

	private void getRankBook() {
		BookType bookType = BookType.CX;
		switch (position) {
		case 11:
			bookType = BookType.XH;
			break;
		case 12:
			bookType = BookType.QH;
			break;
		case 13:
			bookType = BookType.WX;
			break;
		case 14:
			bookType = BookType.XX;
			break;
		case 15:
			bookType = BookType.WY;
			break;
		case 16:
			bookType = BookType.KH;
			break;
		case 17:
			bookType = BookType.DS;
			break;
		case 18:
			bookType = BookType.JS;
			break;
		case 19:
			bookType = BookType.JJ;
			break;
		case 110:
			bookType = BookType.MS;
			break;
		case 111:
			bookType = BookType.CS;
			break;
		case 112:
			bookType = BookType.TR;
			break;
		case 113:
			bookType = BookType.LS;
			break;
		case 114:
			bookType = BookType.TY;
			break;
		case 115:
			bookType = BookType.WX;
			break;
		case 21:
			bookType = BookType.YQ;
			break;
		case 22:
			bookType = BookType.CY;
			break;
		case 23:
			bookType = BookType.DSYQ;
			break;
		case 24:
			bookType = BookType.YX;
			break;
		case 25:
			bookType = BookType.QC;
			break;
		case 26:
			bookType = BookType.ZC;
			break;
		case 27:
			bookType = BookType.DM;
			break;
		case 28:
			bookType = BookType.GT;
			break;
		case 29:
			bookType = BookType.HX;
			break;
		case 210:
			bookType = BookType.ZT;
			break;
		case 211:
			bookType = BookType.LY;
			break;
		case 212:
			bookType = BookType.XY;
			break;
		case 213:
			bookType = BookType.TLI;
			break;
		case 214:
			bookType = BookType.AQ;
			break;
		case 215:
			bookType = BookType.JD;
			break;
		}
		chatchManager.getBookType(bookType, pageNo, new SearchResultInterface() {

			@Override
			public void getSearchResultList(List<SearchResult> searchResult) {
				if (searchListView.isRefersh()) {
					if (searchResult != null && searchResult.size() > 0) {
						pageNo++;
						bookSearchResult.clear();
						bookSearchResult.addAll(searchResult);
						adapter.notifyDataSetChanged();
						searchListView.getRefreshableView().setSelection(0);
					}
				} else {
					if (searchResult != null && searchResult.size() > 0) {
						pageNo++;
						bookSearchResult.addAll(searchResult);
						adapter.notifyDataSetChanged();
					} else {
						searchListView.setCanLoadMore(false);
						showToast("亲，没有更多了哦！");
					}
				}
				searchListView.onRefreshCompleteAll();
				emptyPage.onReloadComplete();
				searchListView.setEmptyView(emptyPage);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.result_head_back:
			finishInAnim();
			break;
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent intent) {
		super.onActivityResult(arg0, arg1, intent);
		if (arg0 == BookDetailActivity.REQUEST_CODE && arg1 == RESULT_OK) {
			String bookDescId = intent.getStringExtra(SearchResultAdapter.BOOK_DESC_ID);
			if (bookSearchResult != null && bookSearchResult.size() > 0) {
				for (SearchResult result : bookSearchResult) {
					if (result.getGid().equals(bookDescId))
						activity.chatchManager.checkIfExists(result, activity);
				}
				adapter.notifyDataSetChanged();
			}
		}
	}
}