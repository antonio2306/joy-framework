package cn.joy.framework.kits;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/**
 * 日期操作工具类
 */
public class DateKit {
	private static String[] pattern = new String[]{"yyyy-MM","yyyyMM","yyyy/MM",   
            "yyyyMMdd","yyyy-MM-dd","yyyy/MM/dd",   
            "yyyyMMddHHmmss", "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss"}; 
	
	/**
	 * 从时间字符串构造时间对象
	 * 支持时间格式："yyyy-MM","yyyyMM","yyyy/MM",   
            "yyyyMMdd","yyyy-MM-dd","yyyy/MM/dd",   
            "yyyyMMddHHmmss", "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss"
	 */
	public static Date parseDate(String dateStr){
		try {
			return DateUtils.parseDateStrictly(dateStr, pattern);
		} catch (ParseException e) {
			return null;
		}
	}
	
	/**
	 * 按指定格式从时间字符串构造时间对象
	 * @param dateStr	要解析的时间字符串
	 * @param datePattern	指定的时间格式
	 * @return
	 */
	public static Date parseDate(String dateStr, String datePattern){
		try {
			return DateUtils.parseDateStrictly(dateStr, new String[]{datePattern});
		} catch (ParseException e) {
			return null;
		}
	}
	
	/**
	 * 填充时间字符串为完整格式
	 * 如：yyyy-MM-dd，补全成yyyy-MM-dd 00:00:00
	 * 	yyyy-MM-dd HH，补全成yyyy-MM-dd HH:00:00
	 * 	yyyy-MM-dd HH:mm，补全成yyyy-MM-dd HH:mm:00
	 */
	public static String fillDateStr(String dateStr){
		int count = StringKit.getOccurCount(dateStr, ":");
		if(count==1)
			dateStr += ":00";
		else if(count==0){
			if(dateStr.indexOf(" ")==-1)
				dateStr += " 00:00:00";
			else
				dateStr += ":00:00";
		}
		return dateStr;
	}
	
	/**
	 * 按指定格式格式化时间对象
	 * @param date 时间对象
	 * @param datePattern 要格式化的时间格式
	 */
	public static String formatDate(Date date, String datePattern) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(StringKit.getString(datePattern, "yyyy-MM-dd"));
		return dateFormat.format(date);
	}

	/**
	 * 根据指定时间和当前时间的时差，判断指定时间的日期是否要显示前天、昨天、今天等
	 * @param time 指定时间对应的毫秒数
	 * @return -2:前天 -1：昨天 0：今天 1：明天 2：后天， out：显示日期
	 * @throws Exception
	 */
	public static String transferTime(Long time) throws Exception{
		Calendar today = Calendar.getInstance();
		Calendar old = Calendar.getInstance();
		old.setTimeInMillis(time);

		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		old.set(Calendar.HOUR_OF_DAY, 0);
		old.set(Calendar.MINUTE, 0);
		old.set(Calendar.SECOND, 0);
		
		long intervalMilli = old.getTimeInMillis() - today.getTimeInMillis();
		int xcts = (int) (intervalMilli / (24 * 60 * 60 * 1000));
		// -2:前天 -1：昨天 0：今天 1：明天 2：后天， out：显示日期
		if (xcts >= -2 && xcts <= 2) {
			return String.valueOf(xcts);
		} else {
			return "out";
		}
	}
	
	/**
	 * 返回两个时间相差的准确天数 d2-d1
	 */
	public static int daysBetween(Date d1, Date d2) {
		return Days.daysBetween(new DateTime(d1), new DateTime(d2)).getDays();
	}
	
	/**
	 * 返回两个时间相差的天数 d2-d1，只比较日期，忽略时分秒
	 */
	public static int daysBetweenIgnoreTime(Date d1, Date d2) {
		return Days.daysBetween(new DateTime(startTimeOfDay(d1)), new DateTime(startTimeOfDay(d2))).getDays();
	}
	
	/**
	 * 从指定时间到当前时间经过的时间，格式是xx天xx小时xx分钟xx秒
	 */
	public static String getPassTimeStr(Date thatTime) {
		if (thatTime == null) {
			throw new NullPointerException("thatTime can't be null!");
		}

		String passTimeStr = "0秒";
		Duration duration = new Duration(new DateTime(thatTime), new DateTime());
		Period period = duration.toPeriodFrom(null, PeriodType.dayTime());
		if (duration.isLongerThan(Duration.ZERO)) {
			PeriodFormatter dayAndHoursAndMinitue = new PeriodFormatterBuilder().printZeroRarelyLast().appendDays().appendSuffix("天").appendHours()
					.appendSuffix("小时").appendMinutes().appendSuffix("分钟").appendSeconds().appendSuffix("秒").toFormatter();
			passTimeStr = dayAndHoursAndMinitue.print(period);
		}

		return passTimeStr;
	}
	
	/**
	 * 获取指定日期的起始时间，即当天的0点0分0秒0毫秒
	 */
	public static Date startTimeOfDay(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date==null?new Date():date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
	/**
	 * 获取指定日期偏移月数的当月首日，如上月1日，下月1日
	 * @param date 指定日期
	 * @param offsetMonth 偏移月数，可以为负数
	 * @return
	 */
	public static Date firstDayOfMonth(Date date, int offsetMonth){
		return new LocalDate(date).plusMonths(offsetMonth).dayOfMonth().withMinimumValue().toDate();
	}
	
	/**
	 * 获取指定日期偏移月数的当月末日，如上月31日，下月30日
	 * @param date 指定日期
	 * @param offsetMonth 偏移月数，可以为负数
	 * @return
	 */
	public static Date lastDayOfMonth(Date date, int offsetMonth){
		return new LocalDate(date).plusMonths(offsetMonth).dayOfMonth().withMaximumValue().toDate();
	}
	/*
	public static void main(String[] args) throws Exception{
		Date d1 = parseDate("2016-04-10 11:22:33");
		Date d2 = parseDate("2016-04-13 10:22:33");
		System.out.println(daysBetween(d1, d2));
		System.out.println(daysBetweenIgnoreTime(d1, d2));
	}*/
}
