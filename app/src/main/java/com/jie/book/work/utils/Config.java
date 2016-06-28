package com.jie.book.work.utils;

/**
 * 常用常量，设置<BR>
 */
public class Config {

	/*************************************************************** 基本配置 *********************************************************/
	// 是否开发模式
	public static boolean DEBUG = false;
	// 服务是否开发模式
	public static boolean SERVICE_DEBUG = false;
	// 是否开启关键字过滤
	public static boolean OPEN_FILTER = true;

	/*************************************************************** 默认参数 *********************************************************/
	public static final String FILTER_NAME_DEFAULT = "白洁,淫荡,风流,寡妇,山村,少妇,坏蛋,多情,床,蹂躏,内裤,沉沦,堕落,玩物,宠物,荡妇,乡村,情色,色情,美色,色狼,情妇,艳妇,美妇,奸淫,轮奸,诱奸,性爱,性,做爱,嘿咻,妓,奸,xxoo";
	public static final String SEARCH_BOOK_DEFAULT = "花千骨;何以笙箫默;绝世唐门;傲世九重天;黑道特种兵;盗墓笔记;魔天记;我当道士那些年";
	// 追书神器用户代理
	public static final String ZH_USER_AGENT_DEFAULT = "ZhuiShuShenQi/2.20.2 (Android %s; %s %s / %s %s; %s)[preload=false;locale=zh_CN;clientidbase=%s]";
	// 搜索模式M追书 B百度 E宜搜
	public static final String SEARCH_BOOK_CHANNEL_DEFAULT = "MBES";
	// 默认搜索渠道
	public static String SEARCH_SOURCE_DEFAULT = "M";
	// 分类M追书 B百度 E宜搜
	public static final String TYPE_BOOK_CHANNEL_DEFAULT = "C";
	// 开启广告需要的次数
	public static final int OPEN_AD_TIME_DEFAULT = 10;
	// 用户通知
	public static final String USER_NOTIFICATION_DEFAULT = "0";
	// 是否打开书架推荐
	public static final int OPEN_SHELF_REC_DEFAULT = 0;
	// 是否打开精品应用
	public static final int OPEN_APP_LIST_DEFAULT = 1;
	// 0无广告 1百度 2宜搜3广点通4百通
	public static int BANNER_AD_DEFAULT = 2;
	// 必须升级版号本0为没有
	public static int UPDATE_VERSION_DEFAULT = 0;
	// 搜狗版本号
	public static final String SOGOU_VERSION_DEFAULT = "3.8.2";
	// 开屏间隔次数默认为0,-1为关闭
	public static int SPLASH_SPACE_DEFAULT = 0;

	/*************************************************************** 配置URL地址 *********************************************************/
	// 神秘功能推广地址
	public static String SECRET_APP_URL = "http://www.shuchengxs.com/configs/shucheng_secret.txt";
	// 书架推广地址
	public static String SHELF_APP_URL = "http://www.shuchengxs.com/configs/shelf_app_url.txt";
	// 下载字体地址
	public static String DOWNLOAD_TYPEFACE_URL = "http://www.shuchengxs.com/configs/shucheng_typeface.txt";
	// 书城听书FM地址
	public static String LISETN_SITE_URL = "http://www.shuchengxs.com/configs/app_ting_site.txt";

	/*************************************************************** 友盟在线参数 ******************************************************/
	// 0无广告 1百度 2宜搜3广点通
	public static final String BANNER_AD = "bannerAd27";
	public static final String SEARCH_BOOK_CHANNEL = "searchBookChannel";
	public static final String TYPE_BOOK_CHANNEL = "typeBookChannel2";
	public static final String SEARCH_BOOK = "newSearchBook";
	public static final String OPEN_AD_TIME = "openAdTime";
	public static final String ZH_USER_AGENT = "zhUserAgent";
	public static final String USER_NOTIFICATION = "userNotification";
	public static final String BAIDU_ID = "baidu_id";
	public static final String OPEN_SHELF_REC = "open_shelf_rec2";
	public static final String OPEN_APP_LIST = "open_app_list";
	public static final String UPDATE_VERSION = "update_version";
	public static final String SOGOU_VERSION = "sogou_version";
	public static final String SPLASH_SPACE = "splash_space";

	/*************************************************************** 配置渠道ID *********************************************************/
	public static final String EASOU_ID_DEFAULT = "8791_826";
	public static final String BAIDU_ID_DEFAULT = "1003c9f8";
	public static final String GDT_APPID = "1101815546";
	public static final String GDT_BannerPosId = "9079537082772949621";
	public static final String GDT_SplashPosId = "9010106409067920";
}
