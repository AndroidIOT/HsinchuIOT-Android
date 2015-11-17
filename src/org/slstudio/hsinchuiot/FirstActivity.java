package org.slstudio.hsinchuiot;

import org.slstudio.hsinchuiot.service.ServiceContainer;

import android.content.Intent;
import android.os.Bundle;


public class FirstActivity extends BaseActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		final String sessionId = ServiceContainer.getInstance()
				.getPerferenceService().getSessionId();
		ServiceContainer.getInstance().getUpgradeController()
		.checkVersion(null, true);
		if ("".equals(sessionId)) {
			gotoLoginView();
		} else {
			gotoMainView();
		}

	}



	protected void gotoLoginView() {
		Intent i = new Intent(Constants.Action.HSINCHUIOT_LOGIN);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
		finish();

	}

	protected void gotoMainView() {
		Intent i = new Intent(Constants.Action.HSINCHUIOT_MAIN);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
		finish();
	}

}
