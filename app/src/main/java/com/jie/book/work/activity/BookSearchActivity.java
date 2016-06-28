package com.jie.book.work.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bond.bookcatch.BookChannel;
import com.bond.bookcatch.mixed.MixedRankType;
import com.bond.bookcatch.vo.SearchResult;
import com.jie.book.work.R;
import com.jie.book.work.adapter.SearchResultAdapter;
import com.jie.book.work.application.BookActivityManager;
import com.jie.book.work.application.BookApplication;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.read.BookCatchManager.SearchResultInterface;
import com.jie.book.work.utils.Config;
import com.jie.book.work.utils.SharedPreferenceUtil;
import com.jie.book.work.utils.StringUtil;
import com.jie.book.work.utils.UIHelper;
import com.jie.book.work.view.pullrefresh.PullToRefreshButtomListView;
import com.jie.book.work.view.pullrefresh.PullToRefreshButtomListView.onRefreshLoadListener;

public class BookSearchActivity extends BaseActivity implements OnClickListener, TextWatcher {
	private static final String LUANCH_KEYWORD = "luanch_keyword";
	private PullToRefreshButtomListView searchListView;
	private Button btnSearch;
	private ImageView ivSource;
	private ImageButton ibtnDel;
	private EditText edSearch;
	private View comBookView, viewSource, sourceDialog, sourceShade, sourceGuide;
	private View viewSourceNormal, ViewSourceBaidu, ViewSourceEasou, ViewSourceSogou;
	private SearchResultAdapter adapter;
	private List<SearchResult> bookSearchResult = new ArrayList<SearchResult>();
	private List<Button> bookComList;
	private String[] searchBooks;
	private String[] filterKeyWords = null;
	private String lastSearchKey = StringUtil.EMPTY;
	private int pageNo = 1;
	private BookChannel bookChannel = BookChannel.MIXED;

	public static void luanch(Activity content) {
		Intent intent = new Intent();
		intent.setClass(content, BookSearchActivity.class);
		BookActivityManager.getInstance().goFoResult(content, intent, 0);
	}

	public static void luanch(Activity content, String key, boolean defaultAnim) {
		Intent intent = new Intent();
		intent.putExtra(LUANCH_KEYWORD, key);
		intent.setClass(content, BookSearchActivity.class);
		if (defaultAnim)
			BookActivityManager.getInstance().goFoResult(content, intent, 0);
		else
			BookActivityManager.getInstance().goTo(content, intent, R.anim.fade_in, R.anim.fade_out);
	}

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(Cookies.getReadSetting().isNightMode() ? R.layout.act_book_search_ef : R.layout.act_book_search);
		UIHelper.setTranStateBar(activity);
		initUI();
		initListener();
		initFilterKeyWords();
		initData();
	}

	@Override
	protected void initUI() {
		sourceGuide = findViewById(R.id.change_source_guide);
		sourceShade = findViewById(R.id.book_search_source_shade);
		sourceDialog = findViewById(R.id.book_search_dialog);
		comBookView = findViewById(R.id.book_search_com_layout);
		ivSource = (ImageView) findViewById(R.id.book_search_source_logo);
		edSearch = (EditText) findViewById(R.id.book_search_edit);
		ibtnDel = (ImageButton) findViewById(R.id.book_search_delete);
		searchListView = (PullToRefreshButtomListView) findViewById(R.id.book_search_result_list);
		btnSearch = (Button) findViewById(R.id.book_search_go_btn);
		viewSource = findViewById(R.id.book_search_source_layout);
		viewSourceNormal = findViewById(R.id.layout_source_normal);
		ViewSourceBaidu = findViewById(R.id.layout_source_baidu);
		ViewSourceEasou = findViewById(R.id.layout_source_easou);
		ViewSourceSogou = findViewById(R.id.layout_source_sogou);
		Button bookCom1 = (Button) findViewById(R.id.book_search_com1);
		Button bookCom2 = (Button) findViewById(R.id.book_search_com2);
		Button bookCom3 = (Button) findViewById(R.id.book_search_com3);
		Button bookCom4 = (Button) findViewById(R.id.book_search_com4);
		Button bookCom5 = (Button) findViewById(R.id.book_search_com5);
		Button bookCom6 = (Button) findViewById(R.id.book_search_com6);
		Button bookCom7 = (Button) findViewById(R.id.book_search_com7);
		Button bookCom8 = (Button) findViewById(R.id.book_search_com8);
		ibtnDel.setVisibility(View.GONE);
		bookComList = new ArrayList<Button>();
		bookComList.add(bookCom1);
		bookComList.add(bookCom2);
		bookComList.add(bookCom3);
		bookComList.add(bookCom4);
		bookComList.add(bookCom5);
		bookComList.add(bookCom6);
		bookComList.add(bookCom7);
		bookComList.add(bookCom8);
		adapter = new SearchResultAdapter(activity, bookSearchResult);
		searchListView.setAdapter(adapter);
		if (Cookies.getUserSetting().isShowSourceGuid()) {
			sourceGuide.setVisibility(View.VISIBLE);
			Cookies.getUserSetting().setShowSourceGuid(false);
		}
	}

	@Override
	protected void initData() {
		String searchStr = SharedPreferenceUtil.getInstance(activity).getString(Config.SEARCH_BOOK,
				Config.SEARCH_BOOK_DEFAULT);
		if (!StringUtil.isEmpty(searchStr)) {
			searchBooks = searchStr.split(";");
			if (searchBooks != null && searchBooks.length > 0)
				for (int i = 0; i < searchBooks.length; i++) {
					Button btn = bookComList.get(i);
					btn.setText(searchBooks[i]);
				}
		}
		String keyWord = getIntent().getStringExtra(LUANCH_KEYWORD);
		if (!StringUtil.isEmpty(keyWord)) {
			findViewById(R.id.book_shlef).setVisibility(View.VISIBLE);
			edSearch.setText(keyWord);
			pageNo = 1;
			searchBook(true);
		} else {
			findViewById(R.id.book_shlef).setVisibility(View.GONE);
		}
		UIHelper.hideInputMethodWinods(activity);
	}

	@SuppressLint("HandlerLeak")
	@Override
	protected void initListener() {
		btnSearch.setOnClickListener(this);
		ibtnDel.setOnClickListener(this);
		edSearch.addTextChangedListener(this);
		viewSourceNormal.setOnClickListener(this);
		ViewSourceBaidu.setOnClickListener(this);
		ViewSourceEasou.setOnClickListener(this);
		ViewSourceSogou.setOnClickListener(this);
		sourceShade.setOnClickListener(this);
		sourceGuide.setOnClickListener(this);
		initSourceSearch();
		findViewById(R.id.book_search_back).setOnClickListener(this);
		findViewById(R.id.book_search_com1).setOnClickListener(this);
		findViewById(R.id.book_search_com2).setOnClickListener(this);
		findViewById(R.id.book_search_com3).setOnClickListener(this);
		findViewById(R.id.book_search_com4).setOnClickListener(this);
		findViewById(R.id.book_search_com5).setOnClickListener(this);
		findViewById(R.id.book_search_com6).setOnClickListener(this);
		findViewById(R.id.book_search_com7).setOnClickListener(this);
		findViewById(R.id.book_search_com8).setOnClickListener(this);
		findViewById(R.id.book_search_source).setOnClickListener(this);
		findViewById(R.id.book_shlef).setOnClickListener(this);
		searchListView.setonRefreshLoadListener(new onRefreshLoadListener() {

			@Override
			public void onRefreshing() {
				pageNo = 1;
				searchBook(false);
			}

			@Override
			public void onLoadMore() {
				searchBook(false);
			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.book_search_back:
			finishInAnim();
			break;
		case R.id.book_search_delete:
			edSearch.setText(StringUtil.EMPTY);
			lastSearchKey = StringUtil.EMPTY;
			break;
		case R.id.book_search_source_shade:
			toggoleSource();
			break;
		case R.id.book_search_com1:
		case R.id.book_search_com2:
		case R.id.book_search_com3:
		case R.id.book_search_com4:
		case R.id.book_search_com5:
		case R.id.book_search_com6:
		case R.id.book_search_com7:
		case R.id.book_search_com8:
			Button btnCom = (Button) v;
			String comName = btnCom.getText().toString();
			edSearch.setText(comName);
			if (!lastSearchKey.equals(comName)) {
				pageNo = 1;
				searchListView.setRefersh(true);
				searchBook(true);
			}
			break;
		case R.id.book_search_go_btn:
			final String edString = edSearch.getText().toString();
			if (StringUtil.isEmpty(edString)) {
				UIHelper.showShakeAnim(activity, edSearch, "请输入书名或作者名");
			} else {
				if (!lastSearchKey.equals(edString)) {
					pageNo = 1;
					searchListView.setRefersh(true);
					searchBook(true);
				}
			}
			break;
		case R.id.book_search_source:
			toggoleSource();
			break;
		case R.id.layout_source_normal:
			toggoleSource();
			changeSourceSearch(BookChannel.MIXED);
			setSourceImage();
			break;
		case R.id.layout_source_baidu:
			toggoleSource();
			changeSourceSearch(BookChannel.BAIDU);
			setSourceImage();
			break;
		case R.id.layout_source_easou:
			toggoleSource();
			changeSourceSearch(BookChannel.EASOU);
			setSourceImage();
			break;
		case R.id.layout_source_sogou:
			toggoleSource();
			changeSourceSearch(BookChannel.SOGOU);
			setSourceImage();
			break;
		case R.id.change_source_guide:
			sourceGuide.setVisibility(View.GONE);
			break;
		case R.id.book_shlef:
			BookMainActivity.luanch2(activity);
			break;
		}

	}

	@Override
	public void afterTextChanged(Editable s) {
		int length = s.length();
		if (length == 0) {
			comBookView.setVisibility(View.VISIBLE);
			searchListView.setVisibility(View.GONE);
			ibtnDel.setVisibility(View.GONE);
		} else {
			ibtnDel.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (viewSource.getVisibility() == View.VISIBLE) {
				toggoleSource();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	// 初始化来源切换
	private void initSourceSearch() {
		String channel = SharedPreferenceUtil.getInstance(BookApplication.getInstance()).getString(
				Config.SEARCH_BOOK_CHANNEL, Config.SEARCH_BOOK_CHANNEL_DEFAULT);
		String cookSource = Cookies.getUserSetting().getSearchSource();
		viewSourceNormal.setVisibility(channel.contains("M") ? View.VISIBLE : View.GONE);
		ViewSourceBaidu.setVisibility(channel.contains("B") ? View.VISIBLE : View.GONE);
		ViewSourceEasou.setVisibility(channel.contains("E") ? View.VISIBLE : View.GONE);
		ViewSourceSogou.setVisibility(channel.contains("S") ? View.VISIBLE : View.GONE);
		if (!channel.contains(cookSource)) {
			Cookies.getUserSetting().setSearchSource(channel.substring(0, 1));
		}
		String newCookSource = Cookies.getUserSetting().getSearchSource();
		if (newCookSource.equals("M")) {
			bookChannel = BookChannel.MIXED;
		} else if (newCookSource.equals("B")) {
			bookChannel = BookChannel.BAIDU;
		} else if (newCookSource.equals("E")) {
			bookChannel = BookChannel.EASOU;
		} else if (newCookSource.equals("S")) {
			bookChannel = BookChannel.SOGOU;
		}
		setSourceImage();
	}

	// 显示或者隐藏
	private void toggoleSource() {
		if (viewSource.getVisibility() == View.VISIBLE) {
			Animation animation = AnimationUtils.loadAnimation(activity, R.anim.dialog_source_out);
			sourceDialog.startAnimation(animation);
			animation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation arg0) {
				}

				@Override
				public void onAnimationRepeat(Animation arg0) {
				}

				@Override
				public void onAnimationEnd(Animation arg0) {
					viewSource.setVisibility(View.GONE);
				}
			});
		} else {
			viewSource.setVisibility(View.VISIBLE);
			Animation animation = AnimationUtils.loadAnimation(activity, R.anim.dialog_source_in);
			sourceDialog.startAnimation(animation);
		}
	}

	private void setSourceImage() {
		if (bookChannel == BookChannel.MIXED) {
			ivSource.setImageResource(R.drawable.icon_source_default);
		} else if (bookChannel == BookChannel.BAIDU) {
			ivSource.setImageResource(R.drawable.icon_source_baidu);
		} else if (bookChannel == BookChannel.EASOU) {
			ivSource.setImageResource(R.drawable.icon_source_easou);
		} else if (bookChannel == BookChannel.SOGOU) {
			ivSource.setImageResource(R.drawable.icon_source_sogou);
		}
	}

	// 更换来源后
	private void changeSourceSearch(BookChannel channel) {
		if (bookChannel != channel) {
			bookChannel = channel;
			if (bookChannel == BookChannel.MIXED) {
				Cookies.getUserSetting().setSearchSource("M");
			} else if (bookChannel == BookChannel.BAIDU) {
				Cookies.getUserSetting().setSearchSource("B");
			} else if (bookChannel == BookChannel.EASOU) {
				Cookies.getUserSetting().setSearchSource("E");
			} else if (bookChannel == BookChannel.SOGOU) {
				Cookies.getUserSetting().setSearchSource("S");
			}
			if (!StringUtil.isEmpty(edSearch.getText().toString())) {
				pageNo = 1;
				searchListView.setRefersh(true);
				searchBook(true);
			}
		}
	}

	private void searchBook(boolean showDialog) {
		final String bookName = edSearch.getText().toString();
		if (StringUtil.isEmpty(bookName))
			return;
		if (showDialog)
			dialog = UIHelper.showProgressDialog(dialog, activity);
		if (Config.OPEN_FILTER && filterBook(bookName)) {
			getRecommBook();
			return;
		}
		searchListView.setCanLoadMore(true);
		chatchManager.getSearchResultList(bookChannel, bookName, pageNo, new SearchResultInterface() {

			@Override
			public void getSearchResultList(List<SearchResult> searchResult) {
				lastSearchKey = bookName;
				if (searchListView.isRefersh()) {
					if (searchResult != null && searchResult.size() > 0) {
						pageNo++;
						bookSearchResult.clear();
						bookSearchResult.addAll(searchResult);
						adapter.notifyDataSetChanged();
						comBookView.setVisibility(View.GONE);
						searchListView.getRefreshableView().setSelection(0);
						searchListView.setVisibility(View.VISIBLE);
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
				UIHelper.cancleProgressDialog(dialog);
			}
		});
	}

	// 获取关键字
	private void initFilterKeyWords() {
		if (Config.OPEN_FILTER)
			filterKeyWords = Config.FILTER_NAME_DEFAULT.split(",");
	}

	// 关键字过滤
	private boolean filterBook(String keyWord) {
		if (filterKeyWords == null) {
			return false;
		} else {
			for (String key : filterKeyWords) {
				if (keyWord.contains(key))
					return true;
			}
		}
		return false;
	}

	// 获取推荐书籍
	private void getRecommBook() {
		searchListView.setCanLoadMore(false);
		chatchManager.getMixeBookRank(MixedRankType.M_BZQLB, new SearchResultInterface() {

			@Override
			public void getSearchResultList(List<SearchResult> searchResult) {
				if (searchResult != null && searchResult.size() > 0) {
					pageNo++;
					bookSearchResult.clear();
					bookSearchResult.addAll(searchResult);
					adapter.notifyDataSetChanged();
					comBookView.setVisibility(View.GONE);
					searchListView.getRefreshableView().setSelection(0);
					searchListView.setVisibility(View.VISIBLE);
				}
				UIHelper.cancleProgressDialog(dialog);
				searchListView.onRefreshCompleteAll();
			}
		});
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