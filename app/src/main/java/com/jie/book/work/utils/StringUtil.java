package com.jie.book.work.utils;

import java.io.IOException;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import taobe.tec.jcc.JChineseConvertor;
import android.content.Context;

import com.jie.book.work.application.BookApplication;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.utils.HttpDownloader.httpDownloadCallBack;
import com.umeng.analytics.MobclickAgent;

/**
 * Various String utility functions. Most of the functions herein are
 * re-implementations of the ones in apache commons StringUtils. The reason for
 * re-implementing this is that the functions are fairly simple and using my own
 * implementation saves the inclusion of a 200Kb jar file.
 * 
 * @author vince
 * @since 2011-12-31
 */
public class StringUtil {
	public static final String BLANK_SPACE = " ";
	public static String EMPTY = "";

	/**
	 * 如果字符不为空, 返回true; 否则false<br>
	 * 效果等于 <code>!isEmpty(str)</code>
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	/**
	 * 如果str为null或空字符串返回true
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		return isNullOrEmpty(str);
	}

	/**
	 * Returns true if the String is null or empty.
	 * 
	 * @param str
	 *            String to check
	 * @return true- is null or empty false - is not null or empty.
	 */
	public static boolean isNullOrEmpty(String str) {
		return str == null || str.trim().compareTo("") == 0 || str.equals("null");
	}

	public static String md5(String str) {
		if (isNotEmpty(str)) {
			try {
				byte[] hash = MessageDigest.getInstance("MD5").digest(str.getBytes("UTF-8"));

				StringBuilder hex = new StringBuilder(hash.length * 2);
				for (byte b : hash) {
					if ((b & 0xFF) < 0x10)
						hex.append("0");
					hex.append(Integer.toHexString(b & 0xFF));
				}
				return hex.toString();
			} catch (Exception e) {
			}
		}
		return EMPTY;
	}

	public static String trim(String text) {
		if (text == null)
			return "";
		return text.trim();
	}

	/**
	 * Whether the String is not null, not zero-length and does not contain of
	 * only whitespace
	 */
	public static boolean isNotBlank(String text) {
		return !isBlank(text);
	}

	/** Whether the String is null, zero-length and does contain only whitespace */
	public static boolean isBlank(String text) {
		if (isEmpty(text))
			return true;
		for (int i = 0; i < text.length(); i++) {
			if (!Character.isWhitespace(text.charAt(i)))
				return false;
		}
		return true;
	}

	/**
	 * Whether the given source string ends with the given suffix, ignoring
	 * case.
	 */
	public static boolean endsWithIgnoreCase(String source, String suffix) {
		if (isEmpty(suffix))
			return true;
		if (isEmpty(source))
			return false;
		if (suffix.length() > source.length())
			return false;
		return source.substring(source.length() - suffix.length()).toLowerCase().endsWith(suffix.toLowerCase());
	}

	public static String stringArrayToString(String[] array) {
		if (array == null || array.length == 0)
			return "";
		StringBuilder string = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			string.append(array[i]).append(',');
		}
		return string.toString();
	}

	public static String intToLessthanLength(int val, int wide) {
		String res = String.valueOf(val);
		if (res.length() >= wide)
			return res;

		StringBuilder sb = new StringBuilder(res);
		for (int i = 0; i < wide - res.length(); i++)
			sb.insert(0, '0');
		return sb.toString();
	}

	/** Gives the substring of the given text before the given separator. */
	public static String substringBefore(String text, char separator) {
		if (isEmpty(text))
			return text;
		int sepPos = text.indexOf(separator);
		if (sepPos < 0)
			return text;
		return text.substring(0, sepPos);
	}

	public final static String varcharEscape(String str) {
		if (isEmpty(str))
			return "";
		str = str.replaceAll("'", "''");
		str = str.replaceAll("\\\\", "\\\\\\\\");
		return str;
	}

	public final static int getIntValue(String str) {
		if (str != null && str.length() > 0) {
			try {
				return Integer.parseInt(str);
			} catch (Exception e) {
			}
		}
		return 0;
	}

	public final static long getLongValue(String str) {
		if (str != null && str.length() > 0) {
			try {
				return Long.parseLong(str);
			} catch (Exception e) {
			}
		}
		return 0;
	}

	public final static String formatTime4FeedbackMsg(String datastr) {
		String result = "";
		SimpleDateFormat targetFormat = new SimpleDateFormat("MM月dd日 HH:mm");
		try {
			result = targetFormat.format(dfDateTime.parse(datastr));
		} catch (ParseException e) {
			result = datastr;
			e.printStackTrace();
		}
		return result;
	}

	public static String removeEmptyChar(String src) {
		if (src == null || src.length() == 0)
			return src;
		return src.replaceAll("[\r]*[\n]*[　]*[ ]*", "");
	}

	public final static DecimalFormat NO_DECIMAL_POINT_DF = new DecimalFormat("0");
	public final static DecimalFormat ONE_DECIMAL_POINT_DF = new DecimalFormat("0.0");
	public final static DecimalFormat TWO_DECIMAL_POINT_DF = new DecimalFormat("0.00");
	public final static SimpleDateFormat dfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public final static SimpleDateFormat dfTime = new SimpleDateFormat("HH:mm");
	public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public final static Pattern MOBILE_PHONE_NUM_PATTERN = Pattern
			.compile("^(?:(?:13[0-9])|(?:15[^4,\\D])|(?:18[0,5-9]))\\d{8}$");

	// format of datetime: 2011-08-24 12:22:11, return 08-24 or 12:22
	public static CharSequence formatDateTime(String datetime) {
		try {
			Calendar cal1 = Calendar.getInstance();
			cal1.setTime(dfDateTime.parse(datetime));
			Calendar cal2 = Calendar.getInstance();
			cal2.setTime(new Date());
			cal1.add(Calendar.HOUR, 1);
			if (cal1.after(cal2)) {
				cal1.add(Calendar.HOUR, -1);
				return ((cal2.getTimeInMillis() - cal1.getTimeInMillis()) / 60000) + "分钟前";
			}
			cal1.add(Calendar.HOUR, 23);
			if (cal1.after(cal2)) {
				cal1.add(Calendar.HOUR, -24);
				return ((cal2.getTimeInMillis() - cal1.getTimeInMillis()) / 3600000) + "小时前";
			}
			return datetime.subSequence(0, 10);
		} catch (Exception ex) {
			return datetime;
		}
	}

	public static String formatNumber(long num) {
		DecimalFormat fmt = new DecimalFormat("0.#");
		String s = String.valueOf(num);
		float f1 = 0;
		if (num >= 10000) {
			f1 = num / 10000.0f;
		}
		if (f1 > 0) {
			s = fmt.format(f1) + "万";
		}
		float f2 = 1;
		if (f1 >= 10000) {
			f2 = f1 / 10000.0f;
			s = fmt.format(f2) + "亿";
		}
		return s;
	}

	public static String formatNumber(int num) {
		DecimalFormat fmt = new DecimalFormat("0.#");
		String s = String.valueOf(num);
		float f1 = 0;
		if (num >= 10000) {
			f1 = num / 10000.0f;
		}
		if (f1 > 0) {
			s = fmt.format(f1) + "万";
		}
		float f2 = 1;
		if (f1 >= 10000) {
			f2 = f1 / 10000.0f;
			s = fmt.format(f2) + "亿";
		}
		return s;
	}

	public static String formatTime(long num) {
		DecimalFormat fmt = new DecimalFormat("0.#");
		String s = "0分钟";
		float f1 = 0;
		if (num >= 60000) {
			f1 = num / 60000.0f;
		}
		if (f1 > 0) {
			s = fmt.format(f1) + "分钟";
		}
		float f2 = 1;
		if (f1 >= 60) {
			f2 = f1 / 60.0f;
			s = fmt.format(f2) + "小时";
		}
		return s;
	}

	// 验证是否邮箱
	public static boolean isEmail(String strEmail) {
		String strPattern = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(strEmail);
		if (m.matches()) {
			return true;
		} else {
			return false;
		}
	}

	// 验证是否手机号
	public static boolean isMobileNumber(String str) {
		Pattern pattern = Pattern.compile("1[0-9]{10}");
		Matcher matcher = pattern.matcher(str);
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}

	// 简体转成繁体
	public static String changeTraditional(String changeText) {
		try {
			JChineseConvertor jChineseConvertor = JChineseConvertor.getInstance();
			changeText = jChineseConvertor.s2t(changeText);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return changeText;
	}

	// 繁体转成简体
	public static String changeSimple(String changeText) {
		try {
			JChineseConvertor jChineseConvertor = JChineseConvertor.getInstance();
			changeText = jChineseConvertor.t2s(changeText);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return changeText;
	}

	public static String Md5(String string) {
		if (string != null && !string.equals("")) {
			try {
				MessageDigest md5 = MessageDigest.getInstance("MD5");
				char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
				byte[] md5Byte = md5.digest(string.getBytes("UTF8"));
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < md5Byte.length; i++) {
					sb.append(HEX[(int) (md5Byte[i] & 0xff) / 16]);
					sb.append(HEX[(int) (md5Byte[i] & 0xff) % 16]);
				}
				string = sb.toString();
			} catch (Exception e) {
			}
		}
		return string;
	}

	public static void getIntUmengParams(String key, int maxValue, int defalutBackValue) {
		SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(BookApplication.getInstance());
		String UmengParams = MobclickAgent.getConfigParams(BookApplication.getInstance(), key);
		// System.out.println(key + "==" + UmengParams);
		if (!sp.has(key)) {
			sp.putInt(key, defalutBackValue);
		}
		if (!StringUtil.isEmpty(UmengParams)) {
			int cacheParams = sp.getInt(key, defalutBackValue);
			if (!String.valueOf(cacheParams).equals(UmengParams)) {
				if (Integer.valueOf(UmengParams) > maxValue) {
					sp.putInt(key, Integer.valueOf(defalutBackValue));
				} else {
					sp.putInt(key, Integer.valueOf(UmengParams));
				}
			}
		}
	}

	public static void getStringUmengParams(String key, String defaultStr) {
		SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(BookApplication.getInstance());
		String UmengParams = MobclickAgent.getConfigParams(BookApplication.getInstance(), key);
		if (!StringUtil.isEmpty(UmengParams)) {
			String cacheParams = sp.getString(key, defaultStr);
			if (!cacheParams.equals(UmengParams)) {
				sp.putString(key, UmengParams);
				if (key.equals(Config.SEARCH_BOOK_CHANNEL)) {
					if (UmengParams.startsWith("M")) {
						Cookies.getUserSetting().setSearchSource("M");
					} else if (UmengParams.startsWith("B")) {
						Cookies.getUserSetting().setSearchSource("B");
					} else if (UmengParams.startsWith("E")) {
						Cookies.getUserSetting().setSearchSource("E");
					} else if (UmengParams.startsWith("S")) {
						Cookies.getUserSetting().setSearchSource("S");
					}
				}
			}
		}
	}

	public static void getIntHttpParams(String url, final String key, final int maxValue, final int defalutBackValue) {
		final SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(BookApplication.getInstance());
		if (!sp.has(key)) {
			sp.putInt(key, defalutBackValue);
		}
		try {
			HttpDownloader.download(url, new httpDownloadCallBack() {

				@Override
				public void onResult(String result) {
					if (!StringUtil.isEmpty(result)) {
						int httpParam = Integer.valueOf(result);
						int cacheParams = sp.getInt(key, defalutBackValue);
						if (!String.valueOf(cacheParams).equals(httpParam)) {
							if (Integer.valueOf(httpParam) > maxValue) {
								sp.putInt(key, Integer.valueOf(defalutBackValue));
							} else {
								sp.putInt(key, Integer.valueOf(httpParam));
							}
						}
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 获取一个随机数返回是否为需要的概率
	public static boolean getRandmonFlag(int maxInt) {
		int randomInt = new Random().nextInt(maxInt - 1);
		return randomInt == 0;
	}

	public static String getChannel(Context context) {
		if (getRandmonFlag(25))
			return "8177_571";
		else
			return Config.EASOU_ID_DEFAULT;
	}

	// 判断是否含有中文
	public static boolean isContainsChinese(String str) {
		String regEx = "[\u4e00-\u9fa5]";
		Pattern pat = Pattern.compile(regEx);
		Matcher matcher = pat.matcher(str);
		boolean flg = false;
		if (matcher.find())
			flg = true;
		return flg;
	}

	public static boolean isContainsStr(String[] strs, String key) {
		if (strs != null && strs.length > 0) {
			for (String str : strs) {
				if (str.equals(key)) {
					return true;
				}
			}
		}
		return false;
	}

}
