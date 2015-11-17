package org.slstudio.hsinchuiot.service.push;

import org.slstudio.hsinchuiot.AppConfig;
import org.slstudio.hsinchuiot.Constants;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;


public class ServerPushHelper {

	// 获�?�ApiKey
	public static String getMetaValue(Context context, String metaKey) {
		Bundle metaData = null;
		String apiKey = null;
		if (context == null || metaKey == null) {
			return null;
		}
		try {
			ApplicationInfo ai = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			if (null != ai) {
				metaData = ai.metaData;
			}
			if (null != metaData) {
				apiKey = metaData.getString(metaKey);
			}
		} catch (NameNotFoundException e) {

		}
		return apiKey;
	}

	public static boolean hasBind(Context context) {
		return context.getSharedPreferences(AppConfig.PREF_NAME,
				Context.MODE_PRIVATE).getBoolean(
				Constants.PreferenceKey.SERVER_PUSH_BIND_FLAG, false);
	}

	public static void setBind(Context context, boolean flag, String userId,String channelId) {
		context.getSharedPreferences(AppConfig.PREF_NAME, Context.MODE_PRIVATE)
				.edit()
				.putBoolean(Constants.PreferenceKey.SERVER_PUSH_BIND_FLAG, flag)
				.commit();
		if (userId != null) {
			context.getSharedPreferences(AppConfig.PREF_NAME,
					Context.MODE_PRIVATE)
					.edit()
					.putString(Constants.PreferenceKey.SERVER_PUSH_BIND_USRID,
							userId).commit();
		}
		if (channelId != null) {
			context.getSharedPreferences(AppConfig.PREF_NAME,
					Context.MODE_PRIVATE)
					.edit()
					.putString(Constants.PreferenceKey.SERVER_PUSH_BIND_DECIVEID,
							channelId).commit();
		}

	}

}
