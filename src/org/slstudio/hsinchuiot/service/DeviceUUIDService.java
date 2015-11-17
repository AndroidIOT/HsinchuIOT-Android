package org.slstudio.hsinchuiot.service;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.slstudio.hsinchuiot.Constants;

import android.content.Context;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

public class DeviceUUIDService {
	protected UUID uuid;
	
	public DeviceUUIDService(Context context) {
		if(uuid == null){
			synchronized(DeviceUUIDService.class){
				if(uuid == null){
					final String id = ServiceContainer.getInstance().getPerferenceService().getValue(Constants.PreferenceKey.DEVICE_UUID);
					if(id != null && !id.equals("")){
						uuid = UUID.fromString(id);
					}else{
						final String androidID = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
						try{
						if (!"9774d56d682e549c".equals(androidID)) {
                            uuid = UUID.nameUUIDFromBytes(androidID.getBytes("utf8"));
                        } else {
                            final String deviceID = ((TelephonyManager) context.getSystemService( Context.TELEPHONY_SERVICE )).getDeviceId();
                            uuid = deviceID!=null ? UUID.nameUUIDFromBytes(deviceID.getBytes("utf8")) : UUID.randomUUID();
                        }
						}catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                        // Write the value out to the prefs file
                        ServiceContainer.getInstance().getPerferenceService().setValue(Constants.PreferenceKey.DEVICE_UUID, uuid.toString());
                    }
				}
			}
		}
		
	}
	
	public UUID getDeviceUUID(){
		return uuid;
	}
	
	
	
}
