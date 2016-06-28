package com.jie.book.work.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jie.book.work.R;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.utils.StringUtil;
import com.jie.book.work.utils.UIHelper;

//百度贴吧
public class WebActivity extends BaseActivity implements OnClickListener {
	private static final String LUANCH_URL = "luanch_url";
	private WebView strategyWebview;
	private Button btnBack;
	private Button btnGo;
	private Button btnRefresh;
	private ProgressBar strategypb;
	private TextView tvTitle;
	private String bookName;

	public static void launcher2(Context context, String bookName) {
		if (!StringUtil.isEmpty(bookName)) {
			Intent intent = new Intent();
			intent.setClass(context, WebActivity.class);
			intent.putExtra(LUANCH_URL, bookName);
			context.startActivity(intent);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(Cookies.getReadSetting().isNightMode() ? R.layout.act_baidu_bar_ef : R.layout.act_baidu_bar);
		UIHelper.setTranStateBar(activity);
		initUI();
		initListener();
		initData();
	}

	@SuppressLint("SetJavaScriptEnabled")
	protected void initUI() {
		strategyWebview = (WebView) findViewById(R.id.ac_search_strategy_webview);
		strategyWebview.getSettings().setSupportZoom(true);
		strategyWebview.getSettings().setJavaScriptEnabled(true);
		strategyWebview.setWebViewClient(new strategyWebViewClient());
		strategyWebview.setWebChromeClient(new strategyWebChromeClient());
		btnBack = (Button) findViewById(R.id.ac_search_strategy_back_btn);
		btnGo = (Button) findViewById(R.id.ac_search_strategy_go_btn);
		btnRefresh = (Button) findViewById(R.id.ac_search_strategy_refresh_btn);
		strategypb = (ProgressBar) findViewById(R.id.ac_search_strategy_progress);
		tvTitle = (TextView) findViewById(R.id.book_search_back);
	}

	@Override
	protected void initListener() {
		btnBack.setOnClickListener(this);
		btnGo.setOnClickListener(this);
		btnRefresh.setOnClickListener(this);
		findViewById(R.id.book_search_back).setOnClickListener(this);
		findViewById(R.id.book_shlef_main).setOnClickListener(this);
	}

	protected void initData() {
		bookName = getIntent().getStringExtra(LUANCH_URL);
		if (!StringUtil.isEmpty(bookName)) {
			String url = "http://tieba.baidu.com/f?ie=utf-8&kw=" + bookName + "&fr=search";
            Toast.makeText(this, bookName, Toast.LENGTH_LONG).show();
			tvTitle.setText(bookName + "吧");
            strategyWebview.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if( url.startsWith("http:") || url.startsWith("https:") ) {
                        return false;
                    }

                    // Otherwise allow the OS to handle things like tel, mailto, etc.
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                    startActivity( intent );
                    return true;
                }
            });

			strategyWebview.loadUrl(url);

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ac_search_strategy_back_btn:
			strategyWebview.goBack();
			break;
		case R.id.ac_search_strategy_go_btn:
			strategyWebview.goForward();
			break;
		case R.id.ac_search_strategy_refresh_btn:
			strategyWebview.reload();
			break;
		case R.id.book_search_back:
			finishInAnim();
			break;
		case R.id.book_shlef_main:
			BookMainActivity.luanch(activity);
			break;
		}
	}

	private class strategyWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			btnBack.setEnabled(view.canGoBack());
			btnGo.setEnabled(view.canGoForward());
		}

	}

	private class strategyWebChromeClient extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			super.onProgressChanged(view, newProgress);
			strategypb.setProgress(newProgress);
		}

		// @Override
		// public void onReceivedTitle(WebView view, String title) {
		// super.onReceivedTitle(view, title);
		// if (title.length() > 8) {
		// title = title.substring(0, 5) + "...";
		// }
		// tvTitle.setText(title);
		// }
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (strategyWebview.canGoBack()) {
				strategyWebview.goBack();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

}
