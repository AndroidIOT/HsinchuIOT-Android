package org.slstudio.hsinchuiot;


import org.slstudio.hsinchuiot.service.IOTException;
import org.slstudio.hsinchuiot.service.push.ServerPushHelper;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class BaseActivity extends FragmentActivity {
	
	public static final int DIALOG_ERROR = 1000002;
	
	private AlertDialog errordia;
	private Exception exception;
	
	private long lastClickTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityManager.addActivities(this);
		if (!ServerPushHelper.hasBind(this)) {
			PushManager.startWork(this, PushConstants.LOGIN_TYPE_API_KEY, ServerPushHelper.getMetaValue(this, "api_key"));
		}
	}
	
	
	

	@Override
	protected void onResume() {
		super.onResume();
	}




	@Override
	protected void onPause() {
		super.onPause();
	}

	
	@Override
	protected void onDestroy() {
		ActivityManager.removeActivities(this);
		super.onDestroy();
	}




	public void setException(Exception exp){
		this.exception = exp;
	}


	private void centerActionBarTitle() {

		int titleId = 0;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			titleId = getResources().getIdentifier("action_bar_title", "id", "android");
		} else {
			return;
		}

		if (titleId > 0) {
			TextView titleTextView = (TextView) findViewById(titleId);
			if(titleTextView == null){
				return;
			}
			DisplayMetrics metrics = getResources().getDisplayMetrics();

			LinearLayout.LayoutParams txvPars = (LayoutParams) titleTextView.getLayoutParams();
			txvPars.gravity = Gravity.CENTER_HORIZONTAL;
			txvPars.width = metrics.widthPixels;
			titleTextView.setLayoutParams(txvPars);
			titleTextView.setGravity(Gravity.CENTER);
		}
	}
	
	protected void setActionBarTextSize(float textSize){
		int titleId = 0;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			titleId = getResources().getIdentifier("action_bar_title", "id", "android");
		} else {
			return;
		}

		if (titleId > 0) {
			TextView titleTextView = (TextView) findViewById(titleId);
			if(titleTextView == null){
				return;
			}
			titleTextView.setTextSize(textSize);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch(item.getItemId())
		{
		case android.R.id.home:
			finish();
			
		break;
		}
		return true; 
	}
	
	protected void setupActionBar() {
		getActionBar().setDisplayShowHomeEnabled(false);
		centerActionBarTitle();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == DIALOG_ERROR) {
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
		if (id == DIALOG_ERROR) {
			String msg = (exception == null) ? getString(R.string.common_default_error_string)
					: exception.getMessage();
			errordia.setMessage(msg);
		}
		super.onPrepareDialog(id, dialog);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isFastDoubleClick()) {
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }
	
	private boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (timeD >= 0 && timeD <= 1000) {
            return true;
        } else {
            lastClickTime = time;
            return false;
        }
    }
	
}
