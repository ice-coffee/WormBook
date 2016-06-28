package com.jie.book.work.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bond.bookcatch.easou.vo.EasouSubject;
import com.bond.bookcatch.vo.SearchResult;
import com.jie.book.work.R;
import com.jie.book.work.adapter.SearchResultAdapter;
import com.jie.book.work.application.BookActivityManager;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.read.BookCatchManager.SearchResultInterface;
import com.jie.book.work.utils.ImageLoadUtil;
import com.jie.book.work.utils.UIHelper;
import com.jie.book.work.utils.ImageLoadUtil.ImageType;
import com.jie.book.work.view.EmptyPage;
import com.jie.book.work.view.EmptyPage.onReLoadListener;
import com.jie.book.work.view.pullrefresh.PullToRefreshBase;
import com.jie.book.work.view.pullrefresh.PullToRefreshBase.OnRefreshListener;
import com.jie.book.work.view.pullrefresh.PullToRefreshButtomListView;

public class BookSubjectResultActivity extends BaseActivity implements OnClickListener, onReLoadListener {
	private static final String LUANCH_SUBJECT = "luanchSubject";
	private static final String LUANCH_TITLE = "luanchTitle";
	private List<SearchResult> bookSearchResult = new ArrayList<SearchResult>();
	private PullToRefreshButtomListView searchListView;
	private SearchResultAdapter adapter;
	private EasouSubject subject;
	private EmptyPage emptyPage;
	private TextView tvBack;
	private String title;
	private View headView;

	public static void luanch(Activity content, EasouSubject subject, String title) {
		Intent intent = new Intent();
		intent.putExtra(LUANCH_SUBJECT, subject);
		intent.putExtra(LUANCH_TITLE, title);
		intent.setClass(content, BookSubjectResultActivity.class);
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
		headView = LayoutInflater.from(activity).inflate(
				Cookies.getReadSetting().isNightMode() ? R.layout.view_subject_detail_head_ef
						: R.layout.view_subject_detail_head, null);
		searchListView = (PullToRefreshButtomListView) findViewById(R.id.pull_refresh_list);
		searchListView.getRefreshableView().addHeaderView(headView);
		tvBack = (TextView) findViewById(R.id.result_head_back);
		title = getIntent().getStringExtra(LUANCH_TITLE);
		subject = (EasouSubject) getIntent().getSerializableExtra(LUANCH_SUBJECT);
		searchListView.setCanLoadMore(false);
		tvBack.setText(title);
		emptyPage = new EmptyPage(activity);
		emptyPage.setOnReLoadListener(this);
	}

	private void setHeadSubject() {
		if (subject != null) {
			ImageView image = (ImageView) headView.findViewById(R.id.item_subject_image);
			TextView tvBookName = (TextView) headView.findViewById(R.id.item_subject_name);
			TextView tvBookTime = (TextView) headView.findViewById(R.id.item_subject_time);
			TextView tvBookInfo = (TextView) headView.findViewById(R.id.item_subject_info);
			ImageLoadUtil.loadImage(image, subject.getImageUrl(), ImageType.SUBJECT);
			tvBookName.setText(subject.getSubName());
			tvBookTime.setText(subject.getDate());
			tvBookInfo.setText(subject.getDesc());
		}
	}

	@Override
	protected void initData() {
		setHeadSubject();
		adapter = new SearchResultAdapter(activity, bookSearchResult);
		searchListView.setAdapter(adapter);
		searchListView.setRefreshing();
	}

	@Override
	protected void initListener() {
		tvBack.setOnClickListener(this);
		searchListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				getData();
			}
		});
	}

	private void getData() {
		chatchManager.getSubjectResult(subject, new SearchResultInterface() {

			@Override
			public void getSearchResultList(List<SearchResult> searchResult) {
				if (searchResult != null) {
					bookSearchResult.clear();
					bookSearchResult.addAll(searchResult);
					adapter.notifyDataSetChanged();
				}
				emptyPage.onReloadComplete();
				searchListView.onRefreshComplete();
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
	public void onload() {
		getData();
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