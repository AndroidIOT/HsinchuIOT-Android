package org.slstudio.hsinchuiot.service;

import org.slstudio.hsinchuiot.AppConfig;
import org.slstudio.hsinchuiot.Constants;

import android.content.Context;


public class PerferenceService {
	private Context context;
	
	PerferenceService(Context context) {
		this.context = context;
	}
    
    public String getSessionId() {
        return context.getSharedPreferences(AppConfig.PREF_NAME, Context.MODE_PRIVATE).getString(Constants.PreferenceKey.SESSION_ID, "");

    }
    
    public void clear() {
        context.getSharedPreferences(AppConfig.PREF_NAME, Context.MODE_PRIVATE).edit().clear().commit();
    }

    
    public void setSessionId(String sessionId) {
        context.getSharedPreferences(AppConfig.PREF_NAME, Context.MODE_PRIVATE).edit().putString(Constants.PreferenceKey.SESSION_ID, sessionId)
                .commit();
    }
    
    
    public void setValue(String key,String value) {
        context.getSharedPreferences(AppConfig.PREF_NAME, Context.MODE_PRIVATE).edit().putString(key, value)
                .commit();
    }
    
    public String getValue(String key) {
    	 return context.getSharedPreferences(AppConfig.PREF_NAME, Context.MODE_PRIVATE).getString(key, "");
    }
    /*
    public void setUserId(Context context, String sessionId) {
        context.getSharedPreferences(AppConfig.PREF_NAME, Context.MODE_PRIVATE).edit().putString(Constants.PreferenceKey.USER_ID, sessionId)
                .commit();
    }

	public String getUserId(Context context) {
		 return context.getSharedPreferences(AppConfig.PREF_NAME, Context.MODE_PRIVATE).getString(Constants.PreferenceKey.USER_ID, "");

	}
	*/
}
