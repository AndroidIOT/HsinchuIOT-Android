package org.slstudio.hsinchuiot.user;

import java.util.Hashtable;
import java.util.Map;

import org.slstudio.hsinchuiot.ActivityManager;
import org.slstudio.hsinchuiot.BaseActivity;
import org.slstudio.hsinchuiot.Constants;
import org.slstudio.hsinchuiot.R;
import org.slstudio.hsinchuiot.service.IOTException;
import org.slstudio.hsinchuiot.service.ServiceContainer;
import org.slstudio.hsinchuiot.service.http.HttpConfig;
import org.slstudio.hsinchuiot.service.http.HttpRequest;
import org.slstudio.hsinchuiot.service.http.NoneAuthedHttpRequest;
import org.slstudio.hsinchuiot.service.http.RequestControl;
import org.slstudio.hsinchuiot.service.http.RequestListener;
import org.slstudio.hsinchuiot.util.EncryptUtil;
import org.slstudio.hsinchuiot.widget.AuthCodeButton;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ChangePasswordActivity extends BaseActivity {

	private static final int DIALOG_PROGRESS = 1000001;
	private static final int DIALOG_ERROR = 1000002;

	private EditText phoneNumber;
	private EditText authCode;
	private EditText password;
	private Button signupBtn;
	private AuthCodeButton retrieveAuthCodeBtn;
	private ChangePasswordListener listener;
	private Exception exception;
	private Handler handler;
	private ProgressDialog pd;
	private AlertDialog errordia;

	private RequestControl control;
	private Hashtable<String, String> lp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_change_password);
		setupActionBar();
		phoneNumber = (EditText) findViewById(R.id.change_password_phone_number);
		authCode = (EditText) findViewById(R.id.change_password_auth_code);
		password = (EditText) findViewById(R.id.change_password);
		signupBtn = (Button) findViewById(R.id.change_password_button);
		signupBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doChangePassword();

			}
		});
		retrieveAuthCodeBtn = (AuthCodeButton) findViewById(R.id.change_password_retrieve_auth_code_button);
		retrieveAuthCodeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String phone = phoneNumber.getText().toString().trim();
				if (phone.equals("")) {
					exception = new IOTException(
							-1,
							getString(R.string.error_message_require_loginname_text));
					showDialog(DIALOG_ERROR);
					return;
				}
				retrieveAuthCodeBtn.retrieveAuthCode(phone);
			}
		});
		listener = new ChangePasswordListener();
		handler = new Handler();

	}

	protected void doChangePassword() {
		String phone = phoneNumber.getText().toString().trim();
		String code = authCode.getText().toString().trim();
		String pwd = password.getText().toString();

		if (phone.equals("")) {
			exception = new IOTException(-1,
					getString(R.string.error_message_require_loginname_text));
			showDialog(DIALOG_ERROR);
			return;
		}

		if (password.getText().toString().equals("")) {
			exception = new IOTException(-1,
					getString(R.string.error_message_require_pwd_text));
			showDialog(DIALOG_ERROR);
			return;
		}

		HttpRequest request = new NoneAuthedHttpRequest(
				new HttpConfig.PostHttpConfig(),
				Constants.ServerAPIURI.USER_UPDATEPWD);
		request.addParameter("phone", phone);
		request.addParameter("check_code", code);
		request.addParameter("after_password", EncryptUtil.getStringMD5(pwd)
				.toUpperCase());
		ServiceContainer.getInstance().getHttpHandler()
				.doRequest(request, listener);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == DIALOG_PROGRESS) {
			pd = new ProgressDialog(this);

			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.setMessage(getString(R.string.common_please_wait));
			pd.setCanceledOnTouchOutside(false);
			pd.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					if (control != null) {
						control.cancel();
					}

				}
			});
			return pd;
		} else if (id == DIALOG_ERROR) {
			if (exception == null) {
				exception = new IOTException(-1,
						getString(R.string.common_default_error_string));
			}
			errordia = new AlertDialog.Builder(this)
					.setTitle(R.string.common_default_error_name)
					.setMessage(exception.getMessage())
					.setPositiveButton(android.R.string.ok, null).create();
			errordia.setIcon(android.R.drawable.ic_dialog_alert);
			return errordia;
		}

		return super.onCreateDialog(id);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if (id == DIALOG_PROGRESS) {
			removeDialog(DIALOG_ERROR);
		} else if (id == DIALOG_ERROR) {
			String msg = (exception == null) ? getString(R.string.common_default_error_string)
					: exception.getMessage();
			removeDialog(DIALOG_PROGRESS);
			errordia.setMessage(msg);
		}
		super.onPrepareDialog(id, dialog);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 1) {
			finish();
		}
	}

	private class ChangePasswordListener implements RequestListener<Map> {

		@Override
		public void onRequestStart() {
			handler.post(new Runnable() {

				@Override
				public void run() {
					showDialog(DIALOG_PROGRESS);
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

					if (e instanceof IOTException) {
						exception = (IOTException) e;
					} else {
						exception = new IOTException(0, e.getMessage());

					}
					showDialog(DIALOG_ERROR);
				}
			});
		}

		@Override
		public void onRequestResult(Map result) {
			handler.post(new Runnable() {

				@Override
				public void run() {
					finish();
				}
			});

		}

		@Override
		public void onRequestComplete() {
			handler.post(new Runnable() {

				@Override
				public void run() {
					removeDialog(DIALOG_PROGRESS);
				}
			});
		}

		@Override
		public void onRequestGetControl(RequestControl control) {
			ChangePasswordActivity.this.control = control;
		}
	}

	protected void setupActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setTitle(R.string.common_title_change_password);
		super.setupActionBar();
	}
}
