package cn.joy.framework.kits;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateKit {
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
	
	public static String getDateTime(String aMask, Date aDate) {
		if (aDate == null) {
			return "";
		} else {
			SimpleDateFormat dateFormat = new SimpleDateFormat(aMask);
			return dateFormat.format(aDate);
		}
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
	
	public static void main(String[] args) throws Exception{
		Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2013-12-31 13:00:00");
		System.out.println(transferTime(d.getTime()));
	}
}
