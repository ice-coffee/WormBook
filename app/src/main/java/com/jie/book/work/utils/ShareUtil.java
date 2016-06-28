package com.jie.book.work.utils;

import java.util.HashMap;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.PopupWindow;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.jie.book.work.R;
import com.jie.book.work.activity.BaseActivity;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.view.PopupHelper;
import com.jie.book.work.view.PopupHelper.PopGravity;
import com.jie.book.work.view.PopupHelper.PopStyle;

public class ShareUtil {
	public static final String TITLE = "棒棒书城-免费追书工具";
	public static final String COM_SITE = "http://www.shuchengxs.com/";
	public static final String QQ_SITE = "http://a.app.qq.com/o/simple.jsp?pkgname=com.jie.book.work";
	public static final String CONTENT = "专业的网络小说阅读神器。最热最全小说一网打尽。";
	public static final String PATH = "http://684.s21i-2.faidns.com/2722684/4/ABUIABAEGAAgnqjongUouv-X1QYwrAE4rAE.png";

	public static void showSharePop(BaseActivity context, View view) {
		final PopupWindow sharePop = PopupHelper.newBasicPopupWindow(context, PopStyle.MATCH_PARENT);
		View popupView = LayoutInflater.from(context).inflate(
				Cookies.getReadSetting().isNightMode() ? R.layout.act_share_ef : R.layout.act_share, null);
		sharePop.setContentView(popupView);
		PopupHelper.showLocationPop(sharePop, view, PopGravity.TOP);
		ShareClick shareClick = new ShareClick(context, sharePop);
		popupView.findViewById(R.id.lay_weixin).setOnClickListener(shareClick);
		popupView.findViewById(R.id.lay_qq).setOnClickListener(shareClick);
		popupView.findViewById(R.id.lay_weixinfri).setOnClickListener(shareClick);
		popupView.findViewById(R.id.lay_sina).setOnClickListener(shareClick);
		popupView.findViewById(R.id.lay_zone).setOnClickListener(shareClick);
		popupView.findViewById(R.id.share_dis).setOnClickListener(shareClick);
		View touchView = popupView.findViewById(R.id.view_touch);
		touchView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				sharePop.dismiss();
				return true;
			}
		});
	}

	public static class ShareClick implements OnClickListener {
		private BaseActivity context;
		private PopupWindow sharePop;

		public ShareClick(BaseActivity context, PopupWindow sharePo) {
			this.context = context;
			this.sharePop = sharePo;
		}

		@Override
		public void onClick(View v) {
			sharePop.dismiss();
			switch (v.getId()) {
			case R.id.lay_weixin:
				share(context, Wechat.NAME);
				break;
			case R.id.lay_qq:
				share(context, QQ.NAME);
				break;
			case R.id.lay_weixinfri:
				share(context, WechatMoments.NAME);
				break;
			case R.id.lay_sina:
				share(context, SinaWeibo.NAME);
				break;
			case R.id.lay_zone:
				share(context, QZone.NAME);
				break;
			case R.id.share_dis:
				break;
			}
		}
	}

	/**
	 * ShareSDK集成方法有两种</br>
	 * 1、第一种是引用方式，例如引用onekeyshare项目，onekeyshare项目再引用mainlibs库</br>
	 * 2、第二种是把onekeyshare和mainlibs集成到项目中，本例子就是用第二种方式</br> 请看“ShareSDK
	 * 使用说明文档”，SDK下载目录中 </br> 或者看网络集成文档
	 * http://wiki.sharesdk.cn/Android_%E5%BF%AB
	 * %E9%80%9F%E9%9B%86%E6%88%90%E6%8C%87%E5%8D%97
	 * 3、混淆时，把sample或者本例子的混淆代码copy过去，在proguard-project.txt文件中
	 * 
	 * 
	 * 平台配置信息有三种方式： 1、在我们后台配置各个微博平台的key
	 * 2、在代码中配置各个微博平台的key，http://sharesdk.cn/androidDoc
	 * /cn/sharesdk/framework/ShareSDK.html
	 * 3、在配置文件中配置，本例子里面的assets/ShareSDK.conf,
	 */
	public static void share(final BaseActivity context, String platform) {
		final OnekeyShare oks = new OnekeyShare();
		String shareContent = CONTENT;
		// 分享时Notification的图标和文字
		// oks.setNotification(R.drawable.logo,
		// context.getString(R.string.app_name));
		// title标题，在印象笔记、邮箱、信息、微信（包括好友、朋友圈和收藏）、
		// 易信（包括好友、朋友圈）、人人网和QQ空间使用，否则可以不提供

		if (platform.equals(WechatMoments.NAME)) {
			context.showToastLong("正在调用朋友圈分享");
			oks.setTitleUrl(QQ_SITE);
			oks.setUrl(QQ_SITE);
			oks.setTitle(CONTENT);
		} else if (platform.equals(Wechat.NAME)) {
			context.showToastLong("正在调用微信分享");
			oks.setTitleUrl(QQ_SITE);
			oks.setUrl(QQ_SITE);
			oks.setTitle(TITLE);
		} else if (platform.equals(QQ.NAME)) {
			context.showToastLong("正在调用QQ分享");
			oks.setTitleUrl(COM_SITE);
			oks.setUrl(COM_SITE);
			oks.setTitle(TITLE);
		} else if (platform.equals(SinaWeibo.NAME)) {
			context.showToastLong("正在调用新浪分享");
			shareContent = CONTENT + COM_SITE;
			oks.setTitleUrl(COM_SITE);
			oks.setUrl(COM_SITE);
			oks.setTitle(TITLE);
		} else if (platform.equals(QZone.NAME)) {
			context.showToastLong("正在调用QQ空间分享");
			oks.setSite(context.getString(R.string.app_name));
			oks.setTitleUrl(COM_SITE);
			oks.setUrl(COM_SITE);
			oks.setSiteUrl(COM_SITE);
			oks.setTitle(TITLE);
		}
		oks.setText(shareContent);
		oks.setImageUrl(PATH);
		oks.setSilent(false);
		// titleUrl是标题的网络链接，仅在人人网和QQ空间,QQ使用
		// text是分享文本，所有平台都需要这个字段
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		// oks.setImagePath(PATH);
		// imageUrl是图片的网络路径，新浪微博、人人网、QQ空间和Linked-In支持此字段
		// url在微信（包括好友、朋友圈收藏）和易信（包括好友和朋友圈）中使用，否则可以不提供
		// filePath是待分享应用程序的本地路劲，仅在微信（易信）好友和Dropbox中使用，否则可以不提供
		// oks.setFilePath(path);
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用，否则可以不提供
		// oks.setComment(content);
		// site是分享此内容的网站名称，仅在QQ空间使用，否则可以不提供
		// oks.setSite(context.getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用，否则可以不提供
		// oks.setSiteUrl(site);
		// 是否直接分享（true则直接分享）
		if (platform != null) {
			oks.setPlatform(platform);
		}
		oks.setCallback(new PlatformActionListener() {

			@Override
			public void onError(final Platform platform, int arg1, final Throwable arg2) {
				context.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (platform.getName().equals(SinaWeibo.NAME)) {
							context.showToast("新浪微博分享失败");
						} else if (platform.getName().equals(WechatMoments.NAME)) {
							context.showToast("微信朋友圈分享失败"+arg2.getMessage().toString());
						} else if (platform.getName().equals(QZone.NAME)) {
							context.showToast("QQ空间分享失败");
						} else if (platform.getName().equals(Wechat.NAME)) {
							context.showToast("微信分享失败");
						} else if (platform.getName().equals(QQ.NAME)) {
							context.showToast("QQ分享失败");
						}
					}
				});
			}

			@Override
			public void onComplete(final Platform platform, int arg1, HashMap<String, Object> arg2) {

				context.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (platform.getName().equals(SinaWeibo.NAME)) {
							context.showToast("新浪微博分享成功");
						} else if (platform.getName().equals(WechatMoments.NAME)) {
							context.showToast("微信朋友圈分享成功");
						} else if (platform.getName().equals(QZone.NAME)) {
							context.showToast("QQ空间分享成功");
						} else if (platform.getName().equals(Wechat.NAME)) {
							context.showToast("微信分享成功");
						} else if (platform.getName().equals(QQ.NAME)) {
							context.showToast("QQ分享成功");
						}
					}
				});
			}

			@Override
			public void onCancel(final Platform platform, int arg1) {

				context.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (platform.getName().equals(SinaWeibo.NAME)) {
							context.showToast("新浪微博分享取消");
						} else if (platform.getName().equals(WechatMoments.NAME)) {
							context.showToast("微信朋友圈分享取消");
						} else if (platform.getName().equals(QZone.NAME)) {
							context.showToast("QQ空间分享取消");
						} else if (platform.getName().equals(Wechat.NAME)) {
							context.showToast("微信分享取消");
						} else if (platform.getName().equals(QQ.NAME)) {
							context.showToast("QQ分享取消");
						}
					}
				});
			}
		});
		oks.show(context);
	}

}
