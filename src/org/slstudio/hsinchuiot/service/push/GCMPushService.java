package org.slstudio.hsinchuiot.service.push;

import java.util.UUID;

import org.slstudio.hsinchuiot.AppConfig;
import org.slstudio.hsinchuiot.R;
import org.slstudio.hsinchuiot.model.Alarm;
import org.slstudio.hsinchuiot.model.User;
import org.slstudio.hsinchuiot.service.IOTException;
import org.slstudio.hsinchuiot.service.ServiceContainer;
import org.slstudio.hsinchuiot.service.push.GCMServerRegister;
import org.slstudio.hsinchuiot.util.AlarmHelper;
import org.slstudio.hsinchuiot.util.IOTLog;

import com.google.android.gcm.GCMRegistrar;

import android.content.Context;

public class GCMPushService {
	private Context context ;
	
	private GCMServerRegister serverRegister = null;
	
	public GCMPushService(Context context){
		this.context = context;
		this.serverRegister = new GCMServerRegister();
	}
	
	
	public void registerGSM() throws IOTException{
		try {
			GCMRegistrar.checkDevice(context);
			GCMRegistrar.checkManifest(context);
			
			final String regID = GCMRegistrar.getRegistrationId(context);
			
			if (regID.equals("")) {
				GCMRegistrar.register(context, AppConfig.GCM_SENDER_ID);
				IOTLog.d("GSMPushService", "GCM registered with senderID");
			} else {
				IOTLog.d("GSMPushService", "GCM already registered with ID:" + regID);
				if(!GCMRegistrar.isRegisteredOnServer(context)){
					registerToServer(regID);
				}
			}
			
		} catch (Exception exp) {
			IOTLog.e("GSMPushService", "GSM register failed:" + exp.getMessage(), exp);
			throw new IOTException(-2, context.getString(R.string.error_message_gcm_register_failed));
		}
	}
	
	public void registerToServer(String regID) throws IOTException{
		String sessionID = ServiceContainer.getInstance().getSessionService().getSessionID();
		if (sessionID == null || sessionID.equals("")){
			throw new IOTException(-1, "Session ID is null");
		}
		User loginUser = ServiceContainer.getInstance().getSessionService().getLoginUser();
		
		if (loginUser == null || loginUser.getLoginName()==null|| loginUser.getLoginName().equals("")){
			throw new IOTException(-1, "Username is null");
		}
		
		UUID deviceUUID = ServiceContainer.getInstance().getDeviceUUIDService().getDeviceUUID();
		if(deviceUUID == null){
			throw new IOTException(-1, "Device key is null");
		}
		
		if(regID == null || regID.equals("")){
			throw new IOTException(-1, "Token is null");
		}
		
		serverRegister.register(sessionID, loginUser.getLoginName(), regID, deviceUUID.toString());
		
	}

}
