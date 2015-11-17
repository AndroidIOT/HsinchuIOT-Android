package org.slstudio.hsinchuiot.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class StringUtil {

	private static final String HH_MM = "HH:mm";
	private static final String DD_HH_MM = "MM-dd HH:mm";

	private static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
	private static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

	public static final long SIZE_BT = 1024L;

	public static final long SIZE_KB = SIZE_BT * 1024L;

	public static final long SIZE_MB = SIZE_KB * 1024L;

	public static final long SIZE_GB = SIZE_MB * 1024L;

	public static final long SIZE_TB = SIZE_GB * 1024L;

	public static final long UNLIMITED_SIZE = SIZE_GB * 90L;

	public static final int SACLE = 2;

	public static String toStringSize(Long longSize) {
		if (longSize >= SIZE_BT && longSize < SIZE_KB) {
			return longSize / SIZE_BT + "KB";
		} else if (longSize >= SIZE_KB && longSize < SIZE_MB) {
			return longSize / SIZE_KB + "MB";
		} else if (longSize >= SIZE_MB && longSize < SIZE_GB) {
			BigDecimal longs = new BigDecimal(Double.valueOf(longSize + "")
					.toString());
			BigDecimal sizeMB = new BigDecimal(Double.valueOf(SIZE_MB + "")
					.toString());
			String result = longs.divide(sizeMB, SACLE,
					BigDecimal.ROUND_HALF_UP).toString();
			return result + "GB";
		} else if (longSize >= SIZE_GB) {
			BigDecimal longs = new BigDecimal(Double.valueOf(longSize + "")
					.toString());
			BigDecimal sizeMB = new BigDecimal(Double.valueOf(SIZE_GB + "")
					.toString());
			String result = longs.divide(sizeMB, SACLE,
					BigDecimal.ROUND_HALF_UP).toString();
			return result + "TB";
		} else if (longSize < 0) {
			return "0B";
		} else {
			return longSize + "B";
		}
	}

	public static String getPercent(double x, double total) {
		if (total == 0 || x == 0) {
			return "0%";
		}
		double tempresult = x / total;
		DecimalFormat df1 = new DecimalFormat("0.0%"); // ##.00%
														// 百分比格式，后面不足2位的用0补齐
		String result = df1.format(tempresult);
		return result;
	}

	public static String localDate2utc(long inputTime) {
		SimpleDateFormat inputFormat = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
		inputFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
		return inputFormat.format(new Date(inputTime));
	}

	public static String date2Str(Date inputTime) {
		SimpleDateFormat inputFormat = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
		inputFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
		return inputFormat.format(inputTime);

	}
}
