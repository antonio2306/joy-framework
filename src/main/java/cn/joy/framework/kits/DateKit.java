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

public class DateKit {
	private static String[] pattern = new String[]{"yyyy-MM","yyyyMM","yyyy/MM",   
            "yyyyMMdd","yyyy-MM-dd","yyyy/MM/dd",   
            "yyyyMMddHHmmss", "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss"}; 
	
	public static Date parseDate(String dateStr){
		try {
			return DateUtils.parseDateStrictly(dateStr, pattern);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static Date parseDate(String dateStr, String datePattern){
		try {
			return DateUtils.parseDateStrictly(dateStr, new String[]{datePattern});
		} catch (ParseException e) {
			return null;
		}
	}
	
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
	
	public static String formatDate(Date date, String datePattern) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(StringKit.getString(datePattern, "yyyy-MM-dd"));
		return dateFormat.format(date);
	}

	public static String transferTime(Long originalTime) throws Exception{
		Calendar today = Calendar.getInstance();
		Calendar old = Calendar.getInstance();
		old.setTimeInMillis(originalTime);

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
	 * 两个日期相差天数 d2-d1
	 */
	public static int daysBetween(Date d1, Date d2) {
		return Days.daysBetween(new DateTime(d1), new DateTime(d2)).getDays();
	}
	
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
	
	public static Date startTimeOfDay(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date==null?new Date():date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
	public static Date firstDayOfMonth(Date date, int offsetMonth){
		return new LocalDate(date).plusMonths(offsetMonth).dayOfMonth().withMinimumValue().toDate();
	}
	
	public static Date lastDayOfMonth(Date date, int offsetMonth){
		return new LocalDate(date).plusMonths(offsetMonth).dayOfMonth().withMaximumValue().toDate();
	}
	
	public static void main(String[] args) throws Exception{
		Date d1 = parseDate("2016-04-10 11:22:33");
		Date d2 = parseDate("2016-04-13 10:22:33");
		System.out.println(daysBetween(d1, d2));
		System.out.println(daysBetweenIgnoreTime(d1, d2));
	}
}
