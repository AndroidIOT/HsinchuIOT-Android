package org.slstudio.hsinchuiot.util;

import org.slstudio.hsinchuiot.Constants;
import org.slstudio.hsinchuiot.R;
import org.slstudio.hsinchuiot.V2AlarmActivity;
import org.slstudio.hsinchuiot.model.Alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.widget.RemoteViews;

public class AlarmHelper {
	
	private static int notificationID = 8899;

	public static void sendAlarmNotification(Context context, Alarm alarm) {
		PackageManager nPackageManager = context.getPackageManager();
		try {
			final PackageInfo nPackageInfo = nPackageManager
					.getPackageInfo(context.getPackageName(),
							PackageManager.GET_CONFIGURATIONS);

			NotificationManager mNotificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);

			Notification notification = new Notification(
					nPackageInfo.applicationInfo.icon,
					nPackageInfo.applicationInfo.name,
					System.currentTimeMillis());
			notification.flags |= Notification.FLAG_AUTO_CANCEL;

			RemoteViews remoteviews = new RemoteViews(context.getPackageName(),
					R.layout.v2_alarm_notification);
			notification.contentView = remoteviews;

			if (alarm.isBreached()) {
				remoteviews.setImageViewResource(R.id.notification_alarmtype,
						R.drawable.alarm_type_icon_breached);
			} else if (alarm.isWarning()) {
				remoteviews.setImageViewResource(R.id.notification_alarmtype,
						R.drawable.alarm_type_icon_warning);
			}

			remoteviews.setTextViewText(
					R.id.notification_alarmtitle,
					context.getResources().getString(R.string.app_name) + " - "
							+ alarm.getAlarmValueType() + alarm.getAlarmType()
							+ "(" + alarm.getAlarmValue() + ")");
			remoteviews.setTextViewText(R.id.notification_alarminfo,
					alarm.getAlarmTime() + " " + alarm.getAlarmSite());

			Intent notificationIntent = new Intent(context, V2AlarmActivity.class);
			notification.contentIntent = PendingIntent.getActivity(context, Constants.ResultCode.ALARM_LIST, notificationIntent, 0);
			 
			mNotificationManager.notify((notificationID++), notification);

		} catch (NameNotFoundException e) {
			IOTLog.d(
					"AlarmHelper",
					"debuginfo(ALARM) - send alarm notification failure:"
							+ e.getMessage());
			e.printStackTrace();
			final Exception exp = e;

		}
	}
}
