package org.slstudio.hsinchuiot.upgrade;

import org.slstudio.hsinchuiot.Constants;
import org.slstudio.hsinchuiot.service.IOTException;
import org.slstudio.hsinchuiot.service.ServiceContainer;
import org.slstudio.hsinchuiot.util.IOTLog;

import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Window;

public class StartInstallActivity extends Activity {
	private final int DIALOG_SURGGEST = 100;
	private final int DIALOG_FORCE = 101;
	private final int DIALOG_WAIT = 102;
	private final int DIALOG_ERROR = 103;

	private IOTException exception;

	private ProgressDialog pd;

	private AlertDialog alert;
	private int code;

	private boolean isDownloading = false;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			IOTLog.i("[StartInstallActivity]", "[handleMessage]msg.arg1:"
					+ msg.arg1);
			super.handleMessage(msg);
			if (isFinishing())
				return;

			if (msg.what == UpgradeController.MSG_SHOW_WAIT) {
				showDialog(DIALOG_WAIT);
				return;
			}
			if (msg.what == UpgradeController.MSG_SHOW_ERROR) {
				removeDialog(DIALOG_WAIT);
				showDialog(DIALOG_ERROR);
				return;
			}
			if (msg.what == UpgradeController.MSG_FORCE_DOWNLOAD_COMPLETE) {
				removeDialog(DIALOG_WAIT);
				ServiceContainer.getInstance()
						.getUpgradeController()
						.install();
				finish();
				return;
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		code = getIntent().getIntExtra(Constants.BundleKey.UPGRAGE_TYPE,
				CheckUpgradeResult.SUGGEST_UPGRADE);

	}

	@Override
	protected void onResume() {
		super.onResume();
		switch (code) {
		case CheckUpgradeResult.SUGGEST_UPGRADE: {
			showDialog(DIALOG_SURGGEST);

			break;
		}
		case CheckUpgradeResult.FORCE_UPGRADE: {
			showDialog(DIALOG_FORCE);

			break;
		}

		default:
			finish();
			break;

		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return true;
	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_SURGGEST:
			return new AlertDialog.Builder(this)
					.setIcon(R.drawable.ic_menu_info_details)
					.setTitle(
							org.slstudio.hsinchuiot.R.string.common_upgrade_dialog_headline)
					.setMessage(
							org.slstudio.hsinchuiot.R.string.common_upgrade_dialog_message)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									ServiceContainer
											.getInstance()
											.getUpgradeController()
											.sendNotification();
									finish();
								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									finish();

								}

							}).setCancelable(false).create();
		case DIALOG_FORCE:
			return new AlertDialog.Builder(this)
					.setIcon(R.drawable.ic_menu_info_details)
					.setTitle(
							org.slstudio.hsinchuiot.R.string.common_upgrade_dialog_headline)
					.setMessage(
							org.slstudio.hsinchuiot.R.string.common_upgrade_dialog_message)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									ServiceContainer
											.getInstance()
											.getUpgradeController()
											.sendNotification();
									moveTaskToBack(true);

								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									moveTaskToBack(true);
									finish();
									android.os.Process
											.killProcess(android.os.Process
													.myPid());
									System.exit(0);

								}

							}).setCancelable(false).create();

		case DIALOG_ERROR: {
			if (exception == null) {
				exception = new IOTException(0, "");
			}
			alert = new AlertDialog.Builder(this)
					.setIcon(R.drawable.ic_dialog_alert)
					.setMessage(exception.getMessage())
					.setPositiveButton(R.string.ok, null).create();
			return alert;
		}
		case DIALOG_WAIT:

			pd = new ProgressDialog(this);

			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.setMessage(getString(org.slstudio.hsinchuiot.R.string.common_please_wait));
			pd.setCanceledOnTouchOutside(false);

			return pd;

		default:
			break;
		}
		return super.onCreateDialog(id);
	}

}
