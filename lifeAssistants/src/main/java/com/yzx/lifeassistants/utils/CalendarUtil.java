package com.yzx.lifeassistants.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;

/**
 * 
 * @Description: 日期工具类
 * @author: yzx
 * @date: 2015-11-30
 */
@SuppressLint("SimpleDateFormat")
public class CalendarUtil {

	public static final int FIRST_DAY = 0;
	public static final int LAST_DAY = 1;
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * 
	 * @Description: 获取当前时间
	 */
	public static String getCurrentTime(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(System.currentTimeMillis());
	}

	/**
	 * 
	 * @Description: 获取本月第一天
	 */
	public static String getThisMonthFirstDay(String format) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		Date strDateTo = calendar.getTime();
		return sdf.format(strDateTo);
	}

	public static String longToString(String format, long time) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(time);
	}

	public static String dateToString(String format, Date time) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(time);
	}

	public static Date stringToDate(String format, String time) {
		Date res = null;
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			res = sdf.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static long stringToLong(String format, String time) {
		long res = 0;
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			res = sdf.parse(time).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * @Description: 得到一个月中的第一天或最后一天的日期
	 * @monthGap: 与当前月份的差（如：0为当月，1为下个月，-1为上个月）
	 */
	public static String getDayOfMonth(int monthGap, int dayFlag) {
		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH);
		calendar.set(Calendar.MONTH, month + monthGap);
		if (dayFlag == FIRST_DAY) {
			calendar.set(Calendar.DAY_OF_MONTH,
					calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		} else if (dayFlag == LAST_DAY) {
			calendar.set(Calendar.DAY_OF_MONTH,
					calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		}
		Date strDateTo = calendar.getTime();
		return sdf.format(strDateTo);
	}

	/**
	 * @Description: 得到从今天起往前或往后几天的日期
	 * @dayGap: 与今天的天数差
	 */
	public static String getDayOfToday(int dayGap) {
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		calendar.set(Calendar.DAY_OF_MONTH, day + dayGap);
		Date strDateTo = calendar.getTime();
		return sdf.format(strDateTo);
	}

	/**
	 * @Description: 计算某个日期所在周的星期几的日期
	 * 
	 * @date 所要计算的日期
	 * @firstDayOfWeek 
	 *                 要设置星期几为周的第一天(如firstDayOfWeek=1则星期日为第一天,firstDayOfWeek=2则星期一为第一天
	 *                 ,以此类推)
	 * @dayOfWeek 所要计算的某个日期所在周的星期几数(随firstDayOfWeek变化而变化)
	 * @format 输入的日期格式
	 */
	public static String getDayOfWeek(Date date, int firstDayOfWeek,
			int dayOfWeek, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format); // 设置时间格式
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		// 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
		int dayWeek = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
		if (firstDayOfWeek > 1 && 1 == dayWeek) {
			cal.add(Calendar.DAY_OF_MONTH, -1);
		}
		if (firstDayOfWeek > 0 && firstDayOfWeek < 8) {
			cal.setFirstDayOfWeek(firstDayOfWeek);// 设置一个星期的第一天为星期几
		} else {
			return "firstDayOfWeek为1至7的整数";
		}
		int day = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
		cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);// 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
		cal.add(Calendar.DATE, dayOfWeek - 1);
		String time = sdf.format(cal.getTime());
		return time;
	}

}
