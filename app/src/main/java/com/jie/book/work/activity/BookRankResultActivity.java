package com.jie.book.work.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.bond.bookcatch.easou.EasouRankType;
import com.bond.bookcatch.mixed.MixedRankType;
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

public class BookRankResultActivity extends BaseActivity implements OnClickListener, onReLoadListener {
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
	private String firstGid;// 第一本书的GID

	public static void luanch(Activity content, int position, String title) {
		Intent intent = new Intent();
		intent.putExtra(LUANCH_POSITION, position);
		intent.putExtra(LUANCH_TITLE, title);
		intent.setClass(content, BookRankResultActivity.class);
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
		emptyPage.setOnReLoadListener(this);
	}

	@Override
	protected void initData() {
		adapter = new SearchResultAdapter(activity, bookSearchResult);
		searchListView.setAdapter(adapter);
		searchListView.setRefreshing();
	}

	@Override
	public void onload() {
		pageNo = 1;
		getRankBook();
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
		if (position == 1 || position == 2 || position == 5 || position == 6 || position == 21 || position == 22
				|| position == 23 || position == 24 || position == 25 || position == 11 || position == 13
				|| position == 15 || position == 16) {
			MixedRankType mixeType = MixedRankType.M_ZSZRB;
			switch (position) {
			case 1:
				mixeType = MixedRankType.M_ZSZRB;
				break;
			case 2:
				mixeType = MixedRankType.M_DZLCL;
				break;
			case 5:
				mixeType = MixedRankType.F_ZSZRB;
				break;
			case 6:
				mixeType = MixedRankType.F_DZLCL;
				break;
			case 11:
				mixeType = MixedRankType.M_QDYPB;
				break;
			case 13:
				mixeType = MixedRankType.M_ZHYPB;
				break;
			case 15:
				mixeType = MixedRankType.M_SQKXHB;
				break;
			case 16:
				mixeType = MixedRankType.M_ZSWJB;
				break;
			case 21:
				mixeType = MixedRankType.F_JJYQB;
				break;
			case 22:
				mixeType = MixedRankType.F_QDFHPH;
				break;
			case 23:
				mixeType = MixedRankType.F_SQKDYB;
				break;
			case 24:
				mixeType = MixedRankType.F_YDYPB;
				break;
			case 25:
				mixeType = MixedRankType.F_ZSWJB;
				break;
			}
			chatchManager.getMixeBookRank(mixeType, new SearchResultInterface() {

				@Override
				public void getSearchResultList(List<SearchResult> searchResult) {
					if (searchListView.isRefersh()) {
						if (searchResult != null && searchResult.size() > 0) {
							bookSearchResult.clear();
							bookSearchResult.addAll(searchResult);
							adapter.notifyDataSetChanged();
							searchListView.getRefreshableView().setSelection(0);
						}
					} else {
						if (searchResult != null && searchResult.size() > 0) {
							bookSearchResult.addAll(searchResult);
							adapter.notifyDataSetChanged();
						}
					}
					searchListView.setCanLoadMore(false);
					searchListView.onRefreshCompleteAll();
					emptyPage.onReloadComplete();
					searchListView.setEmptyView(emptyPage);
				}
			});
		} else if (position == 0 || position == 4 || position == 12 || position == 14 || position == 17) {
			EasouRankType easouType = EasouRankType.ZPH_QD;
			switch (position) {
			case 0:
				easouType = EasouRankType.ZPH_QD;
				break;
			case 4:
				easouType = EasouRankType.ZSB;
				break;
			case 12:
				easouType = EasouRankType.ZPH_BD;
				break;
			case 14:
				easouType = EasouRankType.ZPH_ZL;
				break;
			case 17:
				easouType = EasouRankType.ZTB;
				break;
			}
			chatchManager.getEasouBookRank(easouType, pageNo, new SearchResultInterface() {

				@Override
				public void getSearchResultList(List<SearchResult> searchResult) {
					if (searchListView.isRefersh()) {
						if (searchResult != null && searchResult.size() > 0) {
							pageNo++;
							bookSearchResult.clear();
							bookSearchResult.addAll(searchResult);
							adapter.notifyDataSetChanged();
							searchListView.getRefreshableView().setSelection(0);
							firstGid = searchResult.get(0).getGid();
						}
					} else {
						if (searchResult != null && searchResult.size() > 0) {
							String nextFirstGid = searchResult.get(0).getGid();
							if (!firstGid.equals(nextFirstGid)) {
								pageNo++;
								bookSearchResult.addAll(searchResult);
								adapter.notifyDataSetChanged();
								firstGid = nextFirstGid;
							} else {
								searchListView.setCanLoadMore(false);
								showToast("亲，没有更多了哦！");
							}
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