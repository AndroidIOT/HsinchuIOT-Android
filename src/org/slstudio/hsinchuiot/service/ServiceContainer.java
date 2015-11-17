package org.slstudio.hsinchuiot.service;

import org.slstudio.hsinchuiot.service.db.DatabaseHelper;
import org.slstudio.hsinchuiot.service.http.HttpRequestHandler;
import org.slstudio.hsinchuiot.service.push.GCMPushService;
import org.slstudio.hsinchuiot.upgrade.UpgradeController;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import android.content.Context;

public class ServiceContainer {
	private static ServiceContainer INSTANCE;
	private HttpRequestHandler httpHandler;
	private PerferenceService appStateService;
	private SessionService sessionService;
	private DeviceUUIDService uuidService;
	private GCMPushService pushService;
	
	private DatabaseHelper databaseHelper;
	private UpgradeController upgradeController;
	private String version;

	private Context context;

	private ServiceContainer() {
	}

	public void init(Context context) {
		this.context = context;
	}

	public Context getContext() {
		return context;
	}

	public static ServiceContainer getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ServiceContainer();
		}

		return INSTANCE;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public PerferenceService getPerferenceService() {
		if (appStateService == null) {
			appStateService = new PerferenceService(context);
		}

		return appStateService;
	}

	public SessionService getSessionService(){
		if(sessionService == null){
			sessionService = new SessionService();
		}
		return sessionService;
	}
	
	public DeviceUUIDService getDeviceUUIDService(){
		if(uuidService == null){
			uuidService = new DeviceUUIDService(context);
		}
		return uuidService;
	}
	
	public GCMPushService getPushService(){
		if(pushService == null){
			pushService = new GCMPushService(context);
		}
		return pushService;
	}
	
	public HttpRequestHandler getHttpHandler() {
		if (httpHandler == null) {
			httpHandler = new HttpRequestHandler();
		}

		return httpHandler;
	}

	public UpgradeController getUpgradeController() {
		if (upgradeController == null) {
			upgradeController = new UpgradeController(context);
		}
		return upgradeController;
	}

	@SuppressWarnings("unchecked")
	public DatabaseHelper getHelper(Context context, Class helper) {
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(context, helper);
		}
		return databaseHelper;

	}

	private LocationService locationService;

	public LocationService getLocationService(Context context) {
		if (locationService == null) {
			locationService = new LocationService(context);
		}
		return locationService;
	}

	public void recycle() {
		locationService = null;
		httpHandler = null;
		OpenHelperManager.releaseHelper();
		databaseHelper = null;
		INSTANCE = null;
	}
}
