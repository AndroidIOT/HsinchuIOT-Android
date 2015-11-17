package org.slstudio.hsinchuiot.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slstudio.hsinchuiot.AppConfig;

public class ReportUtil {
	private static SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat sdfDateTime = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private static SimpleDateFormat sdfDate2 = new SimpleDateFormat(
			"yyyy/MM/dd");
	private static SimpleDateFormat sdfHour = new SimpleDateFormat("HH:00");
	private static SimpleDateFormat sdfHour2 = new SimpleDateFormat(
			"yyyy-MM-dd HH");

	public static Calendar[] get8HoursTimePeriod() {
		Calendar now = Calendar.getInstance();
		int hour = now.get(Calendar.HOUR_OF_DAY);

		String dateStr = sdfDate.format(now.getTime());

		Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DATE, -1);
		String dateStr2 = sdfDate.format(yesterday.getTime());

		Calendar c0 = Calendar.getInstance();
		try {
			c0.setTime(sdfDateTime.parse(dateStr + " 00:00:00"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Calendar c8 = Calendar.getInstance();
		try {
			c8.setTime(sdfDateTime.parse(dateStr + " 08:00:00"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Calendar c16 = Calendar.getInstance();
		try {
			c16.setTime(sdfDateTime.parse(dateStr + " 16:00:00"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Calendar c16_yesterday = Calendar.getInstance();
		try {
			c16_yesterday.setTime(sdfDateTime.parse(dateStr2 + " 16:00:00"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (now.before(c8)) {
			return new Calendar[] { c16_yesterday, c0 };
		} else if (now.after(c16)) {
			return new Calendar[] { c8, c16 };
		} else {
			return new Calendar[] { c0, c8 };
		}
	}

	public static Calendar[] get1HourTimePeriod() {
		Calendar currentHour = Calendar.getInstance();
		currentHour.set(Calendar.MINUTE, 0);
		currentHour.set(Calendar.SECOND, 0);

		Calendar lastHour = Calendar.getInstance();
		lastHour.add(Calendar.HOUR, -1);
		lastHour.set(Calendar.MINUTE, 0);
		lastHour.set(Calendar.SECOND, 0);

		return new Calendar[] { lastHour, currentHour };
	}

	public static String get8HoursTimePeriodString(final Calendar from,
			final Calendar to) {

		int fromHour = from.get(Calendar.HOUR_OF_DAY);
		int toHour = to.get(Calendar.HOUR_OF_DAY);

		String fromDate = sdfDate2.format(from.getTime());
		String toDate = sdfDate2.format(to.getTime());

		if (fromHour == 0) {
			return fromDate + " 00:00-08:00";
		} else if (fromHour == 8) {
			return fromDate + " 08:00-16:00";
		} else {
			return toDate + " 16:00-00:00";
		}

	}

	public static String get1HourTimePeriodString(final Calendar from,
			final Calendar to) {

		int fromHour = from.get(Calendar.HOUR_OF_DAY);
		int toHour = to.get(Calendar.HOUR_OF_DAY);

		String fromDate = sdfDate2.format(from.getTime());
		String toDate = sdfDate2.format(to.getTime());

		if (toHour == 0) {
			return fromDate + " 00:00-01:00";
		} else {
			return toDate + " " + sdfHour.format(from.getTime()) + "-"
					+ sdfHour.format(to.getTime());
		}
	}

	public static String getServerTimeHourString(final Calendar cal) {
		Date d = getServerTime(cal.getTime());
		return sdfHour2.format(d) + ":00:00";
	}

	public static String getServerTimeHourString(final Date date) {
		Date d = getServerTime(date);
		return sdfHour2.format(d) + ":00:00";
	}

	public static String getServerTimeDayString(final Calendar cal) {
		Date d = getServerTime(cal.getTime());
		return sdfDate2.format(d) + " 00:00:00";
	}

	public static String getServerTimeDayString(final Date date) {
		Date d = getServerTime(date);
		return sdfDate2.format(d) + " 00:00:00";
	}

	public static Date getServerTime(Date localTime) {
		Calendar c = Calendar.getInstance();
		c.setTime(localTime);
		c.add(Calendar.HOUR, -8);
		return c.getTime();

	}

	public static Date getLocalTime(Date utcTime) {
		Calendar c = Calendar.getInstance();
		c.setTime(utcTime);
		c.add(Calendar.HOUR, 8);
		return c.getTime();

	}

	public static boolean isCO2Alarm(float co2) {
		if (co2 >= 1000) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isCO2Warning(float co2) {
		if (co2 >= 800 && co2 < 1000) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isTemperatureAlarm(float temperature) {
		if (temperature <= 15 || temperature >= 28) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isTemperatureWarning(float temperature) {
		if (temperature < 16 || temperature > 27) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isHumidityAlarm(float humidity) {
		if (humidity <= 40 || humidity >= 65) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isHumidityWarning(float humidity) {
		if (humidity < 45 || humidity > 60) {
			return true;
		} else {
			return false;
		}
	}

}
