package com.jie.book.work.utils;

import android.annotation.SuppressLint;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {

	/**
	 * 格式化时间 如：12小时前
	 * 
	 * @param timestr
	 *            秒
	 */
	public static String fmttoCN(long timestr) {
		String timeText = null;
		long time = Long.valueOf(timestr);
		if (timestr <= 0) {
			timeText = "刚刚";
		} else {
			Date dt = new Date();
			long nowSec = dt.getTime();
			long timediff = (nowSec - time) / 1000;
			if (timediff < 60) {
				// 小与1分钟显示 ‘刚刚’
				timeText = "刚刚";
			} else if (timediff >= 60 && timediff < 60 * 60) {
				// 小于1小时 显示‘分钟’
				timeText = String.valueOf((int) timediff / 60) + "分钟前";
			} else if (timediff >= 60 * 60 && timediff < 24 * 60 * 60) {
				// 小于24小时，则显示‘时’
				timeText = String.valueOf((int) timediff / (60 * 60)) + "小时前";
			} else if (timediff >= 24 * 60 * 60 && timediff < 30 * 24 * 60 * 60) {
				// 小于1个月，则显示‘天’
				timeText = String.valueOf((int) timediff / (24 * 60 * 60)) + "天前";
			} else if (timediff >= 30 * 24 * 60 * 60 && timediff < 12 * 30 * 24 * 60 * 60) {
				// 小于1年，则显示‘月’
				timeText = String.valueOf((int) timediff / (30 * 24 * 60 * 60)) + "个月前";
			} else if (timediff >= 12 * 30 * 24 * 60 * 60) {
				// 大于1年显示‘年’
				timeText = String.valueOf((int) timediff / (12 * 30 * 24 * 60 * 60)) + "年前";
			}
		}
		return timeText;
	}

	/**
	 * @param time
	 *            时间
	 * @param level
	 *            参考Calendar
	 * @return "yyyy-MM-dd kk:mm:ss" 格式的时间
	 */
	@SuppressLint("SimpleDateFormat")
	public static String longToTime(long time) {
		String format = "yyyy-MM-dd kk:mm";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(time);
	}

	/** 时间转换为long */
	public static long timeToSecond(String time, int level) {
		String format = "yyyy-MM-dd HH:mm:ss";
		switch (level) {
		case Calendar.MINUTE: {
			format = "yyyy-MM-dd HH:mm";
		}
			break;
		case Calendar.HOUR: {
			format = "yyyy-MM-dd HH";
		}
			break;
		case Calendar.DATE: {
			format = "yyyy-MM-dd";
		}
			break;
		case Calendar.MONTH: {
			format = "yyyy-MM";
		}
			break;
		case Calendar.YEAR: {
			format = "yyyy";
		}
			break;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = null;
		long second = 0;
		try {
			date = sdf.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (date != null) {
			second = date.getTime();
		}
		return second;
	}

	/**
	 * 计算个性化距离显示
	 * 
	 * @param distance
	 *            距离
	 * @return String 显示内容
	 */
	public static String covertSelfDistance(int distance) {
		String str = "";
		double d_distance = distance / 1000d;
		if (d_distance > 1000) {
			int d = (int) d_distance;
			str = d + "km";
		} else {
			DecimalFormat df = new DecimalFormat("0.00");
			String result = df.format(d_distance);
			str = result + "km";
		}
		return str;
	}

	/**
	 * 将秒转换为时间格式（00：00）
	 * 
	 * @param s
	 *            秒数
	 * @return
	 */
	public static String secToTime(int s) {
		String time = null;

		int m = s / 60; // 分
		s = s - (m * 60); // 秒

		time = String.format("%02d:%02d", m, s);

		return time;
	}

	// date
	public static String getMonth(long time) {
		Date date = new Date(time);
		SimpleDateFormat formatter = new SimpleDateFormat("M");
		formatter.format(date);
		return formatter.format(date);
	}

	// date
	public static String getDay(long time) {
		Date date = new Date(time);
		SimpleDateFormat formatter = new SimpleDateFormat("d");
		formatter.format(date);
		return formatter.format(date);
	}

	// date
	public static String getWeek(long time) {
		Date date = new Date(time);
		SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
		formatter.format(date);
		return formatter.format(date);
	}

	public static String[] getDatas() {
		String[] datas = new String[60];
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("MM月dd日 EEEE");
		datas[0] = format.format(calendar.getTime());
		for (int i = 1; i < 60; i++) {
			calendar.add(Calendar.DATE, 1);
			datas[i] = format.format(calendar.getTime());
		}

		return datas;
	}

	public static String getDayBySecond(long second) {
		int dayLong = 24 * 60 * 60 * 1000;
		int day = (int) (second / dayLong);
		long leftSecond = second - dayLong * day;
		int houres = (int) (leftSecond / 1000 / 60 / 60);
		return day + "天" + houres + "小时";
	}
}
