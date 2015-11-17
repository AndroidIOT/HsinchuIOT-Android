package org.slstudio.hsinchuiot.service.push;

import org.slstudio.hsinchuiot.Constants;
import org.slstudio.hsinchuiot.R;
import org.slstudio.hsinchuiot.SplashActivity;
import org.slstudio.hsinchuiot.model.Session;
import org.slstudio.hsinchuiot.model.User;
import org.slstudio.hsinchuiot.service.IOTException;
import org.slstudio.hsinchuiot.service.ServiceContainer;
import org.slstudio.hsinchuiot.service.http.HttpConfig;
import org.slstudio.hsinchuiot.service.http.HttpRequest;
import org.slstudio.hsinchuiot.service.http.NoneAuthedHttpRequest;
import org.slstudio.hsinchuiot.service.http.RequestControl;
import org.slstudio.hsinchuiot.service.http.RequestListener;
import org.slstudio.hsinchuiot.util.IOTLog;

import android.content.Context;

public class GCMServerRegister {
	
	public static final String PLATFORM_ANDROID = "Android";

	private Object lock = new Object();
	private boolean succeed = false;
	private IOTException exception = null;
	
	public void GSMServerRegister(){
	}
	
	public void register(String sessionID, String username, String regID, String deviceKey) throws IOTException{
		
		HttpRequest request = new NoneAuthedHttpRequest(new HttpConfig.GetHttpConfig(),
				Constants.ServerAPIURI.PUSH_DEVICE_BINDING);
		request.addParameter("__session_id", sessionID);
		request.addParameter("dataType", "xml");
		request.addParameter("username", username);
		request.addParameter("token", regID);
		request.addParameter("platform", PLATFORM_ANDROID);
		request.addParameter("dev_key", deviceKey);
		
		
		ServiceContainer.getInstance().getHttpHandler().doRequest(request, new RequestListener<String>() {

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

			@Override
			public void onRequestError(Exception e) {
				succeed = false;
				if (e instanceof IOTException) {
					exception = (IOTException) e;
				} else {
					exception = new IOTException(-1, e.getMessage());
				}
			}

			@Override
			public void onRequestResult(String result) {
				// TODO Auto-generated method stub
				succeed = true;
			}

			@Override
			public void onRequestComplete() {
				// TODO Auto-generated method stub
				synchronized (lock) {
					lock.notify();
				}
			}

		});
		
		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		if (exception != null)
			throw exception;
		
		if(!succeed){
			throw new IOTException(-1, "Register to server failed");
		}
	}
	
}
