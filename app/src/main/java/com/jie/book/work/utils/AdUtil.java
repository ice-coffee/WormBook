package com.jie.book.work.utils;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.baidu.ops.appunion.sdk.banner.BaiduBanner;
import com.easou.ecom.mads.AdSwitchLayout;
import com.easou.ecom.mads.AdSwitchListener;
import com.jie.book.work.R;
import com.jie.book.work.activity.BaseActivity;
import com.jie.book.work.application.BookApplication.Cookies;
import com.qq.e.ads.AdListener;
import com.qq.e.ads.AdRequest;
import com.qq.e.ads.AdSize;

@SuppressLint("HandlerLeak")
public class AdUtil {

	private static final int MSG_TIME_RUN = 1001;
	private static final int TIMER_DELAY_TIME = 5 * 1000;
	private static final int TIMER_SPACE_TIME = 10 * 1000;
	private BaseActivity activity;
	private RelativeLayout adParentLayout;
	private LinearLayout adLayout;
	private ImageView adShade;
	private SharedPreferenceUtil sp;
	private int bannerChannel = Config.BANNER_AD_DEFAULT;
	private Timer timer;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_TIME_RUN:
				if (!activity.isShowAd() && adLayout.getChildCount() == 0) {
					if (MiscUtils.isNetwork(activity)
							&& Cookies.getUserSetting().getOpenTime() > Cookies.getUserSetting().getMaxOpenAdTime()
							&& bannerChannel > 0) {
						timer.cancel();
						if (!activity.isFinishing()) {
							activity.setAdMode();
							initBananer();
						}
					}
				} else {
					timer.cancel();
				}
				break;
			}
		}
	};

	public AdUtil(BaseActivity activity) {
		this.activity = activity;
		sp = SharedPreferenceUtil.getInstance(activity);
		bannerChannel = sp.getInt(Config.BANNER_AD, Config.BANNER_AD_DEFAULT);
		adParentLayout = (RelativeLayout) activity.findViewById(R.id.adParentLayout);
		adShade = (ImageView) activity.findViewById(R.id.adLayout_shade);
		adLayout = (LinearLayout) activity.findViewById(R.id.adLayout);
		adShade.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				adLayout.dispatchTouchEvent(event);
				return false;
			}
		});
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				Message msg3 = new Message();
				msg3.what = MSG_TIME_RUN;
				mHandler.sendMessage(msg3);
			}
		}, TIMER_DELAY_TIME, TIMER_SPACE_TIME);
	}

	// 获取横幅
	public void initBananer() {
		switch (bannerChannel) {
		case 0:
			adShade.setVisibility(View.GONE);
			adLayout.setVisibility(View.GONE);
			adParentLayout.setVisibility(View.GONE);
			break;
		case 1:
			initShowAd();
			AdView.setAppSec(activity, sp.getString(Config.BAIDU_ID, Config.BAIDU_ID_DEFAULT));
			AdView.setAppSid(activity, sp.getString(Config.BAIDU_ID, Config.BAIDU_ID_DEFAULT));
			AdView adView = new AdView(activity);
			adLayout.addView(adView);
			adView.setListener(new AdViewListener() {

				@Override
				public void onVideoStart() {

				}

				@Override
				public void onVideoFinish() {

				}

				@Override
				public void onVideoError() {

				}

				@Override
				public void onVideoClickReplay() {

				}

				@Override
				public void onVideoClickClose() {

				}

				@Override
				public void onVideoClickAd() {

				}

				@Override
				public void onAdSwitch() {

				}

				@Override
				public void onAdShow(JSONObject arg0) {
				}

				@Override
				public void onAdReady(AdView arg0) {
					onAdsShow();
				}

				@Override
				public void onAdFailed(String arg0) {

				}

				@Override
				public void onAdClick(JSONObject arg0) {
					onAdsClick();
				}
			});
			break;
		case 2:
			initShowAd();
			AdSwitchLayout adSwitchLayout = new AdSwitchLayout(activity, AdSwitchLayout.AdType.BANNER,
					StringUtil.getChannel(activity));
			adLayout.addView(adSwitchLayout);
			adSwitchLayout.setAdSwitchListener(new AdSwitchListener() {

				@Override
				public void onShowAd() {
				}

				@Override
				public void onReceiveAd() {
					onAdsShow();
				}

				@Override
				public void onFailedToReceiveAd() {

				}

				@Override
				public void onClick() {
					onAdsClick();
				}
			});
			break;
		case 3:
			initShowAd();
			com.qq.e.ads.AdView adview = new com.qq.e.ads.AdView(activity, AdSize.BANNER, Config.GDT_APPID,
					Config.GDT_BannerPosId);
			adLayout.addView(adview);
			adview.fetchAd(new AdRequest());
			adview.setAdListener(new AdListener() {

				@Override
				public void onAdReceiv() {
					onAdsShow();
				}

				@Override
				public void onBannerClosed() {
				}

				@Override
				public void onNoAd() {
				}

				@Override
				public void onAdClicked() {
					onAdsClick();
				}

				@Override
				public void onAdExposure() {

				}

			});
			break;
		case 4:
			initShowAd();
			adLayout.setVisibility(View.VISIBLE);
			BaiduBanner banner = new BaiduBanner(activity);
			adLayout.addView(banner);
			onAdsShow();
			break;
		}
	}

	// 初始化准备显示广告
	private void initShowAd() {
		adParentLayout.setVisibility(View.VISIBLE);
		adLayout.setVisibility(View.VISIBLE);
		adLayout.removeAllViews();
	}

	private void onAdsClick() {
		StatisticUtil.sendEvent(activity, StatisticUtil.AD_CLICK);
	}

	private void onAdsShow() {
		activity.setShowAd(true);
		StatisticUtil.sendEvent(activity, StatisticUtil.AD_SHOW);
		adShade.setVisibility(Cookies.getReadSetting().isNightMode() ? View.VISIBLE : View.GONE);
		new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				resetShadeHeight(adShade);
			}

		}.sendEmptyMessageDelayed(0, 100);
	}

	public void resetShadeHeight(View view) {
		RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) view.getLayoutParams();
		int adHeight = UIHelper.getMeasureHeight(adLayout);
		if (adHeight > 0 && adHeight < UIHelper.dipToPx(60))
			param.height = adHeight;
		else
			param.height = UIHelper.dipToPx(50);
		view.requestLayout();
	}

}
