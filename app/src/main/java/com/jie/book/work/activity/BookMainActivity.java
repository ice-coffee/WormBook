package com.jie.book.work.activity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import cn.sharesdk.framework.ShareSDK;

import com.jie.book.work.R;
import com.jie.book.work.application.BookActivityManager;
import com.jie.book.work.application.BookApplication;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.fragment.BookMainMenuFragment;
import com.jie.book.work.fragment.BookRankFragment;
import com.jie.book.work.fragment.BookShelfFragment;
import com.jie.book.work.fragment.BookSubjectFragment;
import com.jie.book.work.fragment.BookTypeFragment;
import com.jie.book.work.local.LocalFileListActivity;
import com.jie.book.work.service.RemindService;
import com.jie.book.work.utils.Config;
import com.jie.book.work.utils.LoginUtil;
import com.jie.book.work.utils.LoginUtil.onSignUpListener;
import com.jie.book.work.utils.LoginUtil.onSyncListener;
import com.jie.book.work.utils.MiscUtils;
import com.jie.book.work.utils.SharedPreferenceUtil;
import com.jie.book.work.utils.StatisticUtil;
import com.jie.book.work.utils.UIHelper;
import com.jie.book.work.utils.UIHelper.OnDialogClickListener;
import com.jie.book.work.view.LimitViewPager;
import com.jie.book.work.view.LimitViewPager.OnPageChangeListener;
import com.jie.book.work.view.slidingMenu.SlidingFragmentActivity;
import com.jie.book.work.view.slidingMenu.SlidingMenu;
import com.jie.book.work.view.slidingMenu.SlidingMenu.CanvasTransformer;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

public class BookMainActivity extends SlidingFragmentActivity implements OnClickListener, OnPageChangeListener {
	private static BookMainActivity instance;
	private static int luanchType = 0;// 0返回 ，1启动
	private LimitViewPager mViewPager;
	private List<Fragment> bookFragment;
	private String[] fragmentTitles;
	private MainFragmentAdapter fragmentAdapter;
	private SlidingMenu slindingMenu;
	private ImageButton ibDayNilght;
	private Button btnShelf, btnRank, btnSubject, btnType;
	private BookShelfFragment bookShelfFragment;
	private BookMainMenuFragment bookMenuFragment;
	private BookRankFragment bookRankFragment;
	private BookTypeFragment bookTypeFragment;
	private BookSubjectFragment bookSubjectFragment;
	private View viewShade;
	private LoginUtil loginUtil;
	private long mExitTime;

	public static BookMainActivity getInstance() {
		return instance;
	}

	public SlidingMenu getSlindingMenu() {
		return slindingMenu;
	}

	public static void luanch(Activity content) {
		luanchType = 1;
		if (instance == null) {
			Intent intent = new Intent();
			intent.setClass(content, BookMainActivity.class);
			BookActivityManager.getInstance().goTo(content, intent, R.anim.fade_in, R.anim.fade_out);
		} else {
			LinkedList<Activity> reomveActs = new LinkedList<Activity>();
			reomveActs.addAll(BookActivityManager.getInstance().getActs());
			if (reomveActs.contains(instance))
				reomveActs.remove(instance);
			while (reomveActs.size() != 0) {
				Activity act = reomveActs.poll();
				act.finish();
			}
		}
	}

	public static void luanch2(Activity content) {
		luanchType = 1;
		if (instance == null) {
			Intent intent = new Intent();
			intent.setClass(content, BookMainActivity.class);
			BookActivityManager.getInstance().goTo(content, intent);
		} else {
			LinkedList<Activity> reomveActs = new LinkedList<Activity>();
			reomveActs.addAll(BookActivityManager.getInstance().getActs());
			if (reomveActs.contains(instance))
				reomveActs.remove(instance);
			while (reomveActs.size() != 0) {
				Activity act = reomveActs.poll();
				act.finish();
			}
		}
	}

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		getWindow().setBackgroundDrawableResource(
				Cookies.getReadSetting().isNightMode() ? R.drawable.black : R.drawable.bg_sliding_menu);
		setContentView(R.layout.act_book_shelf);
		UIHelper.setTranStateBar(activity);
		loginUtil = new LoginUtil(activity);
		instance = this;
		initUI();
		initListener();
		initData();
		RemindService.startService(this);
		ShareSDK.initSDK(this);
		FeedbackAgent agent = new FeedbackAgent(this);
		agent.sync();
	}

	private void initSlidingMenu() {
		slindingMenu = getSlidingMenu();
		slindingMenu.setBehindWidthPr(0.75F);
		slindingMenu.setFadeDegree(0.75f);
		slindingMenu.setFadeEnabled(false);
		slindingMenu.setMode(SlidingMenu.LEFT);
		slindingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		slindingMenu.setBehindCanvasTransformer(new CanvasTransformer() {

			/**
			 * 侧滑栏滑动效果
			 */
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scale = (float) (percentOpen * 0.25 + 0.75);
				canvas.scale(scale, scale, canvas.getWidth() / 2, canvas.getHeight() / 2);
			}
		});
		slindingMenu.setAboveCanvasTransformer(new CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scale = (float) (1 - percentOpen * 0.25);
				canvas.scale(scale, scale, 0, canvas.getHeight() / 2);
			}
		});
	}

	protected void initUI() {
		initSlidingMenu();
		setBehindContentView(R.layout.act_menu_frame);
		bookMenuFragment = new BookMainMenuFragment();
		getSupportFragmentManager().beginTransaction().replace(R.id.ac_menu_frame, bookMenuFragment).commit();
		fragmentTitles = new String[] { "书架", "排行", "分类", "专题" };
		btnShelf = (Button) findViewById(R.id.book_shelf_btn);
		btnRank = (Button) findViewById(R.id.book_rank_btn);
		btnType = (Button) findViewById(R.id.book_type_btn);
		btnSubject = (Button) findViewById(R.id.book_subject_btn);
		ibDayNilght = (ImageButton) findViewById(R.id.book_shlef_day_night);
		mViewPager = (LimitViewPager) findViewById(R.id.ac_base_pager);
		viewShade = findViewById(R.id.book_shlef_shae);
		ibDayNilght.setImageResource(Cookies.getReadSetting().isNightMode() ? R.drawable.btn_bar_day
				: R.drawable.btn_bar_night);
		viewShade.setVisibility(Cookies.getReadSetting().isNightMode() ? View.VISIBLE : View.GONE);
		mViewPager.setOffscreenPageLimit(4);
		bookFragment = new ArrayList<Fragment>();
		bookShelfFragment = new BookShelfFragment();
		bookRankFragment = new BookRankFragment();
		bookTypeFragment = new BookTypeFragment();
		bookSubjectFragment = new BookSubjectFragment();
		bookFragment.add(bookShelfFragment);
		bookFragment.add(bookRankFragment);
		bookFragment.add(bookTypeFragment);
		bookFragment.add(bookSubjectFragment);
		fragmentAdapter = new MainFragmentAdapter(getSupportFragmentManager(), bookFragment);
		mViewPager.setAdapter(fragmentAdapter);
		mViewPager.setCurrentItem(0);
	}

	@Override
	protected void initData() {
		checkIfUpdate();
		if (Cookies.getUserSetting().getOpenTime() > Cookies.getUserSetting().getMaxOpenAdTime()
				&& chatchManager.isLogin() && !chatchManager.isActivedAdv()) {
			chatchManager.activeAdv(null);
		}
	}

	@Override
	protected void initListener() {
		findViewById(R.id.book_shlef_menu).setOnClickListener(this);
		findViewById(R.id.book_shelf_more2).setOnClickListener(this);
		findViewById(R.id.book_shlef_search).setOnClickListener(this);
		ibDayNilght.setOnClickListener(this);
		mViewPager.setOnPageChangeListener(this);
		btnShelf.setOnClickListener(this);
		btnRank.setOnClickListener(this);
		btnType.setOnClickListener(this);
		btnSubject.setOnClickListener(this);
	}

	private void SwitchModel() {
		getWindow().setBackgroundDrawableResource(
				Cookies.getReadSetting().isNightMode() ? R.drawable.black : R.drawable.bg_sliding_menu);
		viewShade.setVisibility(Cookies.getReadSetting().isNightMode() ? View.VISIBLE : View.GONE);
		ibDayNilght.setImageResource(Cookies.getReadSetting().isNightMode() ? R.drawable.btn_bar_day
				: R.drawable.btn_bar_night);
		bookShelfFragment.SwitchModel();
		bookRankFragment.SwitchModel();
		bookTypeFragment.SwitchModel();
		bookSubjectFragment.SwitchModel();
		bookMenuFragment.SwitchModel();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.book_shelf_btn:
			mViewPager.setCurrentItem(0);
			break;
		case R.id.book_rank_btn:
			mViewPager.setCurrentItem(1);
			break;
		case R.id.book_type_btn:
			mViewPager.setCurrentItem(2);
			break;
		case R.id.book_subject_btn:
			mViewPager.setCurrentItem(3);
			break;
		case R.id.book_shlef_menu:
			slindingMenu.toggle();
			break;
		case R.id.book_shelf_more2:
			showMenuMore();
			break;
		case R.id.book_shlef_search:
			StatisticUtil.sendEvent(activity, StatisticUtil.SEARCH);
			BookSearchActivity.luanch(activity);
			break;
		case R.id.book_shlef_day_night:
			Cookies.getReadSetting().setNightMode(!Cookies.getReadSetting().isNightMode());
			SwitchModel();
			break;
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		switch (position) {
		case 0:
			btnShelf.setBackgroundResource(R.drawable.bg_tab_select);
			btnRank.setBackgroundResource(R.drawable.transparent);
			btnType.setBackgroundResource(R.drawable.transparent);
			btnSubject.setBackgroundResource(R.drawable.transparent);
			btnShelf.setTextColor(getResources().getColor(R.color.book_default_red));
			btnRank.setTextColor(getResources().getColor(R.color.white));
			btnType.setTextColor(getResources().getColor(R.color.white));
			btnSubject.setTextColor(getResources().getColor(R.color.white));
			break;
		case 1:
			btnShelf.setBackgroundResource(R.drawable.transparent);
			btnRank.setBackgroundResource(R.drawable.bg_tab_select);
			btnType.setBackgroundResource(R.drawable.transparent);
			btnSubject.setBackgroundResource(R.drawable.transparent);
			btnShelf.setTextColor(getResources().getColor(R.color.white));
			btnRank.setTextColor(getResources().getColor(R.color.book_default_red));
			btnType.setTextColor(getResources().getColor(R.color.white));
			btnSubject.setTextColor(getResources().getColor(R.color.white));
			break;
		case 2:
			btnShelf.setBackgroundResource(R.drawable.transparent);
			btnRank.setBackgroundResource(R.drawable.transparent);
			btnType.setBackgroundResource(R.drawable.bg_tab_select);
			btnSubject.setBackgroundResource(R.drawable.transparent);
			btnShelf.setTextColor(getResources().getColor(R.color.white));
			btnRank.setTextColor(getResources().getColor(R.color.white));
			btnType.setTextColor(getResources().getColor(R.color.book_default_red));
			btnSubject.setTextColor(getResources().getColor(R.color.white));
			break;
		case 3:
			btnShelf.setBackgroundResource(R.drawable.transparent);
			btnRank.setBackgroundResource(R.drawable.transparent);
			btnType.setBackgroundResource(R.drawable.transparent);
			btnSubject.setBackgroundResource(R.drawable.bg_tab_select);
			btnShelf.setTextColor(getResources().getColor(R.color.white));
			btnRank.setTextColor(getResources().getColor(R.color.white));
			btnType.setTextColor(getResources().getColor(R.color.white));
			btnSubject.setTextColor(getResources().getColor(R.color.book_default_red));
			break;
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (slindingMenu.isMenuShowing()) {
				slindingMenu.toggle();
			} else {
				if (BookApplication.getInstance().downLoadNotifiMap.size() > 0) {
					UIHelper.showTowButtonDialog(activity, "如果退出应用将会终止书籍缓存，是否退出？", "取消", "确定", true, null,
							new OnDialogClickListener() {

								@Override
								public void onClick() {
									BookActivityManager.getInstance().ExitApp(activity);
								}
							});
				} else {
                    /* $$$目前先不进行FM的推广提示 */
                    quit();
//					recListenBookAndQuit();
				}
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void quit() {
		if ((System.currentTimeMillis() - mExitTime) > 2000) {
			showToast("再按一次退出程序");
			mExitTime = System.currentTimeMillis();
		} else {
			BookActivityManager.getInstance().ExitApp(activity);
		}
	}

	public class MainFragmentAdapter extends FragmentPagerAdapter {

		private List<Fragment> mFragments;

		public MainFragmentAdapter(FragmentManager fm, List<Fragment> mFragments) {
			super(fm);
			this.mFragments = mFragments;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return fragmentTitles[position];
		}

		@Override
		public int getCount() {
			return mFragments.size();
		}

		@Override
		public Fragment getItem(int position) {
			return mFragments.get(position);
		}

	}

	// 检查更新
	private void checkIfUpdate() {
		int updateVersion = SharedPreferenceUtil.getInstance(instance).getInt(Config.UPDATE_VERSION,
				Config.UPDATE_VERSION_DEFAULT);
		if (MiscUtils.getVersionCode() <= updateVersion) {
			UIHelper.showTowButtonDialog(activity, "当前版本已停止使用\n请升级到最新版", "取消", "确定", false,
					new OnDialogClickListener() {

						@Override
						public void onClick() {
							BookActivityManager.getInstance().ExitApp(activity);
						}
					}, new OnDialogClickListener() {

						@Override
						public void onClick() {
							checkUpdate(false);
						}
					});
		} else {
			checkUpdate(true);
		}
	}

	private void checkUpdate(final boolean canCancel) {
		UmengUpdateListener updateListener = new UmengUpdateListener() {

			@Override
			public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
				switch (updateStatus) {
				case 0:
					BookApplication.check_new = true;
					UIHelper.showUpdateDialog(activity, updateInfo, canCancel);
					break;
				case 1:
					break;
				case 2: // none wifi
					break;
				case 3:
					break;
				}

			}
		};
		UmengUpdateAgent.setUpdateListener(updateListener);
		UmengUpdateAgent.update(activity);
	}

	@Override
	protected void onResume() {
		super.onResume();
		BookApplication.getInstance().resumeUmengParams();
		if (luanchType == 1) {
			luanchType = 0;
			mViewPager.setCurrentItem(0);
		}
		if (bookShelfFragment != null)
			bookShelfFragment.setTopSelection();
		if (BookApplication.getInstance().swithNightModel == true) {
			BookApplication.getInstance().swithNightModel = false;
			SwitchModel();
		}
	}

	// 显示展示对话框
	@SuppressLint("InflateParams")
	private void showMenuMore() {
		final Dialog dialog = new Dialog(this, R.style.CustomDialog);
		View contentView = LayoutInflater.from(this).inflate(
				Cookies.getReadSetting().isNightMode() ? R.layout.dialog_shelf_more_ef : R.layout.dialog_shelf_more,
				null);
		contentView.findViewById(R.id.btn_shelf_more_lorcal).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.cancel();
				LocalFileListActivity.luanch(activity);
			}
		});
		contentView.findViewById(R.id.btn_shelf_more_syn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.cancel();
				if (loginUtil.isLogin()) {
					loginUtil.sync(new onSyncListener() {

						@Override
						public void onSync() {
							BookMainActivity.getInstance().getDataAfterSync();
						}
					});
				} else {
					loginUtil.showlogin(new onSignUpListener() {

						@Override
						public void onSignUp() {
							if (bookMenuFragment != null)
								bookMenuFragment.setUserInfo();
							BookMainActivity.getInstance().getDataAfterSync();
						}
					});
				}
			}
		});
		dialog.setContentView(contentView);
		dialog.show();
	}

	public void setLogout() {
		if (bookMenuFragment != null)
			bookMenuFragment.setLogout();
	}

	// 推荐书城听书FM
	private void recListenBookAndQuit() {
		if (!MiscUtils.checkApkExist(activity, "com.jie.listen.book")) {
			if (System.currentTimeMillis() - BookApplication.getInstance().lauchTime <= 5 * 60 * 1000) {
				if (bookShelfFragment.getStoreBookList().size() == 0) {
					UIHelper.showTowButtonDialog(activity, "书城听书FM让您解放双眼\n畅听高品质有声小说", "退出", "详情", true,
							new OnDialogClickListener() {

								@Override
								public void onClick() {
									BookActivityManager.getInstance().ExitApp(activity);
								}
							}, new OnDialogClickListener() {

								@Override
								public void onClick() {
									RecAppActivity.launcher(activity, Config.LISETN_SITE_URL, "书城听书FM");
								}
							});
					return;
				}
			}
		}
		quit();
	}

	public void getDataAfterSync() {
		bookShelfFragment.getData(true);
	}
}