package org.slstudio.hsinchuiot.service.http;

import org.slstudio.hsinchuiot.Constants;
import org.slstudio.hsinchuiot.R;
import org.slstudio.hsinchuiot.service.ErrorCode;
import org.slstudio.hsinchuiot.service.IOTException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Handler;

public abstract class ForgroundRequestListener<T> implements RequestListener<T> {
	private Exception exception;
	private AlertDialog errordia;
	private Handler handler;
	private Context mContext;
	private ProgressDialog progressDialog;
	private boolean isShowProgressDialog;
	private String content;
	private RequestControl control;

	public ForgroundRequestListener(Context context,
			boolean isShowProgressDialog, String content) {
		this.mContext = context;
		this.isShowProgressDialog = isShowProgressDialog;
		this.content = content;
		handler = new Handler(context.getMainLooper());
	}

	@Override
	public void onRequestStart() {
		handler.post(new Runnable() {

			@Override
			public void run() {
				if (isShowProgressDialog) {
					progressDialog = ProgressDialog.show(mContext, "", content,
							true);
					progressDialog
							.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					progressDialog.setCanceledOnTouchOutside(false);
					progressDialog.setCancelable(true);
					progressDialog.setOnCancelListener(new OnCancelListener() {

						@Override
						public void onCancel(DialogInterface dialog) {
							if (control != null) {
								control.cancel();
							}
						}
					});
				}
			}
		});

	}

	@Override
	public void onRequestCancelled() {

	}

	@Override
	public void onRequestError(final Exception e) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				e.printStackTrace();
				if (e instanceof IOTException) {
					exception = (IOTException) e;

					if (((IOTException) e).getErrorCode() == ErrorCode.TOKEN_INVAILD
							|| (exception.getMessage() != null && exception
									.getMessage().contains("token"))) {
						Intent i = new Intent(Constants.Action.HSINCHUIOT_LOGIN);
						i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						mContext.startActivity(i);
						((Activity) mContext).finish();
						return;
					}
				} else {
					exception = new IOTException(ErrorCode.UNKNOWN_ERROR, e
							.getMessage());
				}
				if (isShowProgressDialog && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				showErroeDialog(exception);
			}
		});

	}

	@Override
	public abstract void onRequestResult(T result);

	@Override
	public void onRequestComplete() {
		handler.post(new Runnable() {

			@Override
			public void run() {
				if (isShowProgressDialog && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
			}
		});
	}

	@Override
	public void onRequestGetControl(RequestControl control) {
		this.control = control;
	}

	private void showErroeDialog(Exception exception) {
		if (exception == null) {
			exception = new IOTException(-1,
					mContext.getString(R.string.common_default_error_string));
		}
		errordia = new AlertDialog.Builder(mContext)
				.setTitle(R.string.common_default_error_name)
				.setMessage(exception.getMessage())
				.setPositiveButton(android.R.string.ok, null).create();
		errordia.setIcon(android.R.drawable.ic_dialog_alert);
		errordia.show();
	}
}
