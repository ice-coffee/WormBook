package com.jie.book.work.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;

/**
 * 手机基本信息
 * 
 * @author linyg
 * 
 */
public class PhoneUtil {
	private TelephonyManager telephonyManager;
	private Context context;
	static PhoneUtil phoneinfo;
	private Map<String, Object> infoMap;

	private PhoneUtil(Context context) {
		telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		this.context = context;
		infoMap = new HashMap<String, Object>();
	}

	public int getAndroidOSVersionCode() {
		return Build.VERSION.SDK_INT;
	}

	public String getScreenDpi() {
		return context.getResources().getDisplayMetrics().widthPixels + "*"
				+ context.getResources().getDisplayMetrics().heightPixels;
	}

	public int getSIMState() {
		return telephonyManager.getSimState();
	}

	public static PhoneUtil getInstance(Context context) {
		if (phoneinfo == null) {
			phoneinfo = new PhoneUtil(context);
		}
		return phoneinfo;
	}

	/**
	 * 获取手机mac地址
	 * 
	 * @return
	 * @time 2011-8-12 下午03:45:01
	 * @author:linyg
	 */
	public String macAddress() {
		String macAddress = "";
		if (infoMap.containsKey("mac_address")) {
			macAddress = (String) infoMap.get("mac_address");
		} else {
			try {
				WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				WifiInfo info = wifi.getConnectionInfo();
				macAddress = info.getMacAddress();
			} catch (Exception e) {

			}
			infoMap.put("mac_address", macAddress);
		}
		return macAddress;
	}

	/**
	 * 手机型号
	 * 
	 * @return
	 * @time 2011-6-1 下午03:20:14
	 * @author:linyg
	 */
	public String getModel() {
		String model = "";
		if (infoMap.containsKey("get_model")) {
			model = (String) infoMap.get("get_model");
		} else {
			model = Build.MODEL;
			infoMap.put("get_model", model);
		}
		return model;
	}

	/**
	 * 固件号
	 * 
	 * @return
	 * @time 2011-6-1 下午03:20:23
	 * @author:linyg
	 */
	public String getFramework() {
		String firewall = "";
		if (infoMap.containsKey("get_fire_wall")) {
			firewall = (String) infoMap.get("get_fire_wall");
		} else {
			firewall = Build.VERSION.SDK;
			infoMap.put("get_fire_wall", firewall);
		}
		return firewall;
	}

	/**
	 * 手机号
	 * 
	 * @return
	 * @time 2011-6-1 下午03:20:35
	 * @author:linyg
	 */
	public String getPhoneNum() {
		return telephonyManager.getLine1Number();
	}

	/**
	 * 获取当前系统语言
	 * 
	 * @param context
	 * @return
	 * @time 2011-8-8 上午11:57:15
	 * @author:linyg
	 */
	public String getLanguage() {
		return Locale.getDefault().getLanguage();
	}

	public String getSettingLang() {
		return context.getResources().getConfiguration().locale.getCountry();
	}

	/**
	 * 获取当前国家和地区
	 * 
	 * @return
	 * @time 2011-8-8 上午11:57:52
	 * @author:linyg
	 */
	public String getCountry() {
		return Locale.getDefault().getCountry();
	}

	/**
	 * 获取当前网络类型
	 * 
	 * @param context
	 * @return
	 */
	public String getNetType() {
		String netType = "";
		try {
			ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			String type = conn.getActiveNetworkInfo().getTypeName();
			// 若当前是wifi网络，则直接返回wifi; 否则返回详细的网络连接类型
			return type;
			// if (type != null && type.equalsIgnoreCase("wifi")) {
			// netType = type;
			// } else {
			// context.getSystemService(Context.CONNECTIVITY_SERVICE);
			// NetworkInfo mobNetInfo = conn
			// .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			// netType = mobNetInfo.
			// }
		} catch (Exception e) {
		}
		return netType;
	}

	/**
	 * 生产厂商
	 * 
	 * @param @return
	 * @return String
	 */
	public String phoneManufacturer() {
		return Build.MANUFACTURER;
	}

	/**
	 * 运营商名称
	 * 
	 * @param @return
	 * @return String
	 */
	public String phoneCarrierName() {
		return telephonyManager.getNetworkOperatorName();
	}

	/**
	 * 运营商国家码
	 * 
	 * @param @return
	 * @return String
	 */
	public String phoneCarrierCountryIso() {
		return telephonyManager.getNetworkCountryIso();
	}

	/**
	 * 手机国家码
	 * 
	 * @param @return
	 * @return String
	 */
	public String phoneOperator() {
		return telephonyManager.getNetworkOperator();
	}

	/**
	 * 获取电信运营商
	 * 
	 * @return String
	 */
	public String getIMSI() {
		String imsi = "";
		if (infoMap.containsKey("imsi")) {
			imsi = (String) infoMap.get("imsi");
		} else {
			String subscriberId = telephonyManager.getSubscriberId();
			if (subscriberId != null) {
				if (imsi.startsWith("46000") || subscriberId.startsWith("46002")) {// 中国移动
					imsi = "中国移动";
				} else if (subscriberId.startsWith("46001")) {// 中国联通
					imsi = "中国联通";
				} else if (imsi.startsWith("46003")) {// 中国电信
					imsi = "中国电信";
				} else {
					imsi = "Unknow";
				}
			}
			infoMap.put("imsi", imsi);
		}
		return imsi;
	}

	/**
	 * 是否为中国大陸
	 * 
	 * @return boolean
	 */
	public boolean isChinaCarrier() {
		boolean isChina = false;
		if (infoMap.containsKey("is_china_carrier")) {
			isChina = (Boolean) infoMap.get("is_china_carrier");
		} else {
			String imsi = telephonyManager.getSubscriberId();
			if (imsi != null) { // 含手机卡
				if (imsi.startsWith("460"))
					isChina = true;
			} else { // 不含手机卡，根据地区
				if (getCountry().toLowerCase().equals("cn")) {
					isChina = true;
				}
			}
			infoMap.put("is_china_carrier", isChina);
		}
		return isChina;
	}

	/**
	 * imei号
	 * 
	 * @return
	 */
	public String getIMEI() {
		String deviceID = telephonyManager.getDeviceId();
		return deviceID;
	}

	/**
	 * 获取包签名值
	 * 
	 * @param context
	 * @return
	 */
	public PackageInfo getPackageSign(Context context) {
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> apps = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
		Iterator<PackageInfo> iter = apps.iterator();
		while (iter.hasNext()) {
			PackageInfo info = iter.next();
			String packageName = info.packageName;
			if (packageName.equals("net.iaround")) {
				return info;
				// return info.signatures[0].toCharsString();
			}
		}
		return null;// "30820281308201eaa00302010202044e549d3a300d06092a864886f70d0101050500308183310b300906035504061302434e31123010060355040813094775616e67446f6e6731123010060355040713094775616e675a686f7531183016060355040a130f7777772e6961726f756e642e6e657431183016060355040b130f7777772e6961726f756e642e6e6574311830160603550403130f7777772e6961726f756e642e6e65743020170d3131303832343036343230325a180f32303636303532373036343230325a308183310b300906035504061302434e31123010060355040813094775616e67446f6e6731123010060355040713094775616e675a686f7531183016060355040a130f7777772e6961726f756e642e6e657431183016060355040b130f7777772e6961726f756e642e6e6574311830160603550403130f7777772e6961726f756e642e6e657430819f300d06092a864886f70d010101050003818d0030818902818100a9e4d0d806b8788bf5b0a6b175ca06476aaacbff17aca32fd3b6a7d94e4d3e5dcc80871b1614f11ad157dcef240ea56e5eb7be219ec57911ad117c0e8f4f655fa4daff023e21c239fd907f622b1940dd808407eee7922dbc67c6a5ee0c42e77a21a92c3cbeaecb7d8f5e1ceeb1dbbca337b79859c66df3ad9fe9f03d452d40fd0203010001300d06092a864886f70d0101050500038181009be8d8e0a4ca86b2dd1b3acee832ec14a5be13186da583e9c83030138115bfc84e8ccbc67ce9f90a65bb05e4670cf6777f3a9710b1e255dbacddfeb1a34f9f989b8fa6a3b228c66a082250edad8aae341be3a4c75cf2e31b67e0a6e2831146836e30692f38c2c8bdff0687b03a2a98b741742eb83e7f4c0ae1eb90406e694197";
	}

	/**
	 * 判断程序是否在前台运行
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isTopActivity(Context context) {
		ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasksInfo = mActivityManager.getRunningTasks(1);
		if (tasksInfo.size() > 0) {
			// 应用程序位于堆栈的顶层
			if (context.getPackageName().equals(tasksInfo.get(0).topActivity.getPackageName())) {
				return true;
			}
		}
		return false;
	}
}
