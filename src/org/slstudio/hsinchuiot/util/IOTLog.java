package org.slstudio.hsinchuiot.util;


import org.apache.log4j.Logger;
import org.slstudio.hsinchuiot.AppConfig;

import android.util.Log;

public final class IOTLog {
	private static final String PREFIX = "IOT-";
	
	public static void v(String tag, String msg) {
		if (!AppConfig.DEBUG) {
			return;
		}

		Log.println(Log.VERBOSE, PREFIX + tag, msg);

	}

	public static void d(String tag, String msg) {
		if (!AppConfig.DEBUG) {
			return;
		}

		Log.println(Log.DEBUG, PREFIX + tag, msg);

	}

	public static void i(String tag, String msg) {
		if (!AppConfig.DEBUG) {
			return;
		}

		Log.println(Log.INFO, PREFIX + tag, "" + msg);
	}

	public static void w(String tag, String msg) {
		if (!AppConfig.DEBUG) {
			return;
		}

		Log.println(Log.WARN, PREFIX + tag, msg);
	}

	public static void e(String tag, String msg) {
		if (!AppConfig.DEBUG) {
			return;
		}

		Log.println(Log.ERROR, PREFIX + tag, msg);
	}

	public static void e(String tag, String msg, Throwable tr) {
		if (!AppConfig.DEBUG) {
			return;
		}

		Log.e(PREFIX + tag, msg, tr);

	}

	public static void f(String tag, String msg) {
		try {
			Logger	fileLogger = Logger.getLogger(tag);
			
			fileLogger.info(msg);		
		} catch (Exception e) {

		}
	}

	private IOTLog() {

	}
}
