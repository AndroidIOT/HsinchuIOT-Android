package org.slstudio.hsinchuiot.upgrade;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.slstudio.hsinchuiot.Constants;
import org.slstudio.hsinchuiot.R;
import org.slstudio.hsinchuiot.service.IOTException;
import org.slstudio.hsinchuiot.service.ServiceContainer;
import org.slstudio.hsinchuiot.service.http.DownloadAsyncTask;
import org.slstudio.hsinchuiot.service.http.DownloadListener;
import org.slstudio.hsinchuiot.service.http.HttpConfig;
import org.slstudio.hsinchuiot.service.http.HttpRequest;
import org.slstudio.hsinchuiot.service.http.RequestControl;
import org.slstudio.hsinchuiot.service.http.RequestListener;
import org.slstudio.hsinchuiot.service.http.RequestListener.DefaultRequestListener;
import org.slstudio.hsinchuiot.service.http.NoneAuthedHttpRequest;
import org.slstudio.hsinchuiot.util.IOTLog;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.widget.RemoteViews;
import android.widget.Toast;

public class UpgradeController implements RequestListener<CheckUpgradeResult> {

	public static final String TAG = "UpgradeController";

	public static final int MSG_SHOW_WAIT = 1000;

	public static final int MSG_SHOW_ERROR = 1001;

	public static final int MSG_FORCE_DOWNLOAD_COMPLETE = 1002;

	private UpgradeProcessor processor;
	private Context context;
	private UpgradeHandler upgradeHandler;

	private CheckUpgradeResult upgradeInfo;

	private boolean firstTimeChecking = true;

	private Exception downloadException = null;
	
	private class UpgradeProcessor extends HandlerThread {
		UpgradeProcessor() {
			super("UPGRADE_PROCESSOR");
		}
	}

	private class UpgradeHandler extends Handler {

		public UpgradeHandler(Looper looper) {
			super(looper);
		}

	}

	public UpgradeController(Context context) {
		this.context = context;
		processor = new UpgradeProcessor();
		processor.start();
		upgradeHandler = new UpgradeHandler(processor.getLooper());

	}

	private Runnable mrun;
	private NotificationManager mNotificationManager;
	private Notification notification;
	private RemoteViews remoteviews;
	private int count;

	public void resetFirstTimeCheckFlag(){
		firstTimeChecking = true;
	}
	
	public void checkVersion(RequestListener<CheckUpgradeResult> listener,
			boolean autoCheck) {

		IOTLog.d("UpgradeHandler",
				"debuginfo(UPGRADE) - checking need update from server");

		if (autoCheck && !firstTimeChecking) {
			IOTLog.d("UpgradeHandler",
					"debuginfo(UPGRADE) - not first time auto checking, so just return");
			return;
		}
		firstTimeChecking = false;

		PackageManager nPackageManager = context.getPackageManager();
		try {
			final PackageInfo nPackageInfo = nPackageManager
					.getPackageInfo(context.getPackageName(),
							PackageManager.GET_CONFIGURATIONS);
			IOTLog.d("UpgradeHandler",
					"debuginfo(UPGRADE) - current version is:"
							+ nPackageInfo.versionName);

			HttpRequest request = new NoneAuthedHttpRequest(
					new HttpConfig.GetHttpConfig(),
					Constants.ServerAPIURI.COMMON_CHKVERSION);
			request.addParameter("current_version", nPackageInfo.versionName);

			if (listener == null) {
				listener = this;
			}
			ServiceContainer.getInstance().getHttpHandler()
					.doRequest(request, listener);
			IOTLog.d("UpgradeHandler",
					"debuginfo(UPGRADE) - send checking version request:"
							+ request.getRequestURI());

		} catch (NameNotFoundException e1) {
			IOTLog.e(TAG, e1.getMessage());
			e1.printStackTrace();
		}

	}
	

	/*
	 * public void handleUpgrade(){ if(needHandleUpgrade){ final int code =
	 * upgradeInfo.getUpgradeSuggest(); if (code !=
	 * CheckUpgradeResult.NEEDLESS_UPGRADE) { final Intent intent = new
	 * Intent(context, StartInstallActivity.class);
	 * intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	 * intent.putExtra(Constants.BundleKey.UPGRAGE_TYPE, code);
	 * context.startActivity(intent); } needHandleUpgrade = false; } }
	 */

	public void sendNotification() {
		IOTLog.d("UpgradeHandler", "debuginfo(UPGRADE) - send notification");

		PackageManager nPackageManager = context.getPackageManager();
		try {
			final PackageInfo nPackageInfo = nPackageManager
					.getPackageInfo(context.getPackageName(),
							PackageManager.GET_CONFIGURATIONS);

			mNotificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			notification = new Notification(nPackageInfo.applicationInfo.icon,
					nPackageInfo.applicationInfo.name,
					System.currentTimeMillis());
			notification.flags |= Notification.FLAG_ONGOING_EVENT;
			remoteviews = new RemoteViews(context.getPackageName(),
					R.layout.common_upgrade_download_bar);
			notification.contentView = remoteviews;

			count = 0;
			mrun = new Runnable() {

				@Override
				public void run() {
					IOTLog.d("UpgradeHandler", "debuginfo(UPGRADE) - downloading progress update:" + count +"%");
					
					if (count < 100) {
						if(downloadException != null){
							Toast.makeText(context,
									R.string.common_upgrade_download_fail,
									Toast.LENGTH_SHORT).show();
							mNotificationManager.cancel(8888);
							return;
						}
						remoteviews.setProgressBar(
								R.id.common_upgrade_download_process_bar, 100,
								count, false);
						remoteviews.setTextViewText(
								R.id.common_download_percent_text, count + "%");

						mNotificationManager.notify(8888, notification);
						upgradeHandler.postDelayed(mrun, 1000);
					} else {
						/*
						remoteviews.setProgressBar(
								R.id.common_upgrade_download_process_bar, 100,
								100, false);
						remoteviews.setTextViewText(
								R.id.common_download_percent_text, 100 + "%");
						PendingIntent pendingintent = PendingIntent
								.getActivity(context, 0, getInstallIntent(),
										PendingIntent.FLAG_UPDATE_CURRENT);

						notification.contentIntent = pendingintent;
						notification.flags &= (~Notification.FLAG_ONGOING_EVENT);
						notification.flags |= Notification.FLAG_AUTO_CANCEL;
						
						Toast.makeText(context,
								R.string.common_upgrade_download_over,
								Toast.LENGTH_SHORT).show();
						*/
						mNotificationManager.cancel(8888);
						
						Intent notifyIntent = getInstallIntent();
						notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(notifyIntent);
					}
				}
			};
			
			
			
			upgradeHandler.postDelayed(mrun, 1000);
			File file = new File(Constants.UPGRADE_FILE_PATH,
					upgradeInfo.getPackageFilename());
			if (file.exists()) {
				IOTLog.d("UpgradeHandler", "debuginfo(UPGRADE) - upgrade file("
						+ upgradeInfo.getPackageFilename() + ") exist at:"
						+ Constants.UPGRADE_FILE_PATH + ", remove it.");

				file.delete();
			}
			
			DownloadAsyncTask updateAsyncTask = new DownloadAsyncTask(file,
					new DownloadListener() {

						@Override
						public void onStart() {
							downloadException = null;
						}

						@Override
						public void onProcessUpdate(int value) {
							count = value;

						}

						@Override
						public void onComplete(File file) {
							
						}

						@Override
						public void onException(Exception exception) {
							downloadException = exception;
						}
					});
			try {
				IOTLog.d("UpgradeHandler", "debuginfo(UPGRADE) - start async download task from:" + upgradeInfo.getPackageURL());
				
				updateAsyncTask.execute(new URL(upgradeInfo.getPackageURL()));
				
			} catch (Exception e) {
				IOTLog.d("UpgradeHandler", "debuginfo(UPGRADE) - download upgrade failure:" + e.getMessage());
				e.printStackTrace();
				final Exception exp = e;
				upgradeHandler.post(new Runnable(){

					@Override
					public void run() {
						Toast.makeText(context,
								R.string.common_upgrade_download_fail + ":" + exp .getMessage(),
								Toast.LENGTH_SHORT).show();
					}
					
				});
			}

		} catch (NameNotFoundException e) {
			IOTLog.d("UpgradeHandler", "debuginfo(UPGRADE) - start download upgrade failure:" + e.getMessage());
			e.printStackTrace();
			final Exception exp = e;
			upgradeHandler.post(new Runnable(){

				@Override
				public void run() {
					Toast.makeText(context,
							R.string.common_upgrade_download_fail + ":" + exp .getMessage(),
							Toast.LENGTH_SHORT).show();
				}
				
			});
		}
	}

	public void install() {
		Intent notifyIntent = getInstallIntent();
		context.startActivity(notifyIntent);
	}
 
	private Intent getInstallIntent() {
		Intent notifyIntent = new Intent(Intent.ACTION_VIEW);
		notifyIntent.setDataAndType(
				Uri.fromFile(new File(Constants.UPGRADE_FILE_PATH, upgradeInfo
						.getPackageFilename())),
				"application/vnd.android.package-archive");
		return notifyIntent;
	}

	@Override
	public void onRequestResult(CheckUpgradeResult result) {
		upgradeInfo = result;
		IOTLog.d(
				"UpgradeHandler",
				"debuginfo(UPGRADE) - get checking version result("
						+ result.toString() + ")");

		// needHandleUpgrade = true;
		final int code = upgradeInfo.getUpgradeSuggest();
		if (code != CheckUpgradeResult.NEEDLESS_UPGRADE) {
			IOTLog.d("UpgradeHandler",
					"debuginfo(UPGRADE) - start install activity");

			final Intent intent = new Intent(context,
					StartInstallActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra(Constants.BundleKey.UPGRAGE_TYPE, code);
			context.startActivity(intent);
		}
	}

	@Override
	public void onRequestError(Exception e) {
		IOTLog.d(
				"UpgradeHandler",
				"debuginfo(UPGRADE) - checking version failed:"
						+ e.getMessage());	
	}

	@Override
	public void onRequestComplete() {
		// synchronized (lock) {
		// lock.notify();
		// }
	}

	@Override
	public void onRequestStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRequestGetControl(RequestControl control) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRequestCancelled() {
		// TODO Auto-generated method stub

	}

}
