package org.slstudio.hsinchuiot;

import org.slstudio.hsinchuiot.model.Alarm;
import org.slstudio.hsinchuiot.service.IOTException;
import org.slstudio.hsinchuiot.service.ServiceContainer;
import org.slstudio.hsinchuiot.util.AlarmHelper;
import org.slstudio.hsinchuiot.util.IOTLog;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {
	
	
	public GCMIntentService() {
		super(AppConfig.GCM_SENDER_ID);
	}

	@Override
	protected void onError(Context context, String error) {
		IOTLog.e("GCMIntentService", "GCM onError:" + error);
		Toast.makeText(context,
				context.getString(R.string.error_message_gcm_register_failed),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		String message = intent.getDataString();
		IOTLog.d("GCMIntentService", "GCM onMessage:" + message);
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
		
		Alarm alarm = new Alarm(message);
		AlarmHelper.sendAlarmNotification(context, alarm);

		

		String alarmList = ServiceContainer.getInstance()
				.getPerferenceService()
				.getValue(Constants.PreferenceKey.ALARM_LIST);
		if (alarmList == null) {
			ServiceContainer
					.getInstance()
					.getPerferenceService()
					.setValue(Constants.PreferenceKey.ALARM_LIST,
							message);
		} else {
			ServiceContainer
					.getInstance()
					.getPerferenceService()
					.setValue(Constants.PreferenceKey.ALARM_LIST,
							message + "|" + alarmList);
			if (context instanceof V2AlarmActivity) {
				((V2AlarmActivity) context).refreshList();
			} else if (context instanceof V2SuperUserMainActivity) {
				((V2SuperUserMainActivity) context).updateAlarmStatus();
			} else if (context instanceof V2UserMainActivity) {
				((V2UserMainActivity) context).updateAlarmStatus();
			}
		}

	}

	@Override
	protected void onRegistered(Context context, String regID) {
		IOTLog.d("GCMIntentService", "GCM onRegistered:" + regID);

		try {
			ServiceContainer.getInstance().getPushService()
					.registerToServer(regID);
		} catch (IOTException e) {
			IOTLog.e("GCMIntentService", "GCM register to server failed", e);
			Toast.makeText(
					context,
					context.getString(R.string.error_message_gcm_register_failed),
					Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	protected void onUnregistered(Context context, String regID) {
		IOTLog.d("GCMIntentService", "GCM onUnregistered:" + regID);
	}

	

}
