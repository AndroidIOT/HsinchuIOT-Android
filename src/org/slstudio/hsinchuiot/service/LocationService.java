package org.slstudio.hsinchuiot.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.slstudio.hsinchuiot.Constants;
import org.slstudio.hsinchuiot.service.http.AuthedHttpRequest;
import org.slstudio.hsinchuiot.service.http.HttpConfig;
import org.slstudio.hsinchuiot.service.http.HttpRequest;
import org.slstudio.hsinchuiot.service.http.RequestListener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import android.content.Context;

public class LocationService {
	private Context mContext;
	private LocationClient mLocClient;
	private List<BDLocationListener> listeners = new ArrayList<BDLocationListener>();
	private Timer timmer = null;
	private BDLocation lastKnown = null;
	private boolean isUpload = false;

	public void addListener(BDLocationListener listener) {
		listeners.add(listener);
	}

	public void removeListener(BDLocationListener listener) {
		listeners.remove(listener);
	}

	public LocationService(Context context) {
		this.mContext = context;
	}

	public void start() {
		mLocClient = new LocationClient(mContext);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();

		if (timmer == null) {
			timmer = new Timer();
		}
		timmer.schedule(new TimerTask() {

			@Override
			public void run() {

				if (lastKnown != null) {
					if (isUpload) {
						reportGps(lastKnown);
					}
				}

			}
		}, 1000, 60 * 1000);

	}

	public void stop() {
		if (mLocClient != null) {
			mLocClient.stop();
		}
		lastKnown = null;
	}

	public boolean isStart() {
		return mLocClient != null && (mLocClient.isStarted() == true);
	}

	public BDLocationListener myListener = new BDLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation loc) {
			for (BDLocationListener bdLocationListener : listeners) {
				bdLocationListener.onReceiveLocation(loc);
			}
			if (lastKnown == null) {
				lastKnown = loc;
				isUpload = true;
				reportGps(lastKnown);
				return;
			}
			if (lastKnown.getLatitude() != loc.getLatitude()
					|| lastKnown.getLongitude() != loc.getLongitude()) {
				lastKnown = loc;
				isUpload = true;
			}
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {
			for (BDLocationListener bdLocationListener : listeners) {
				bdLocationListener.onReceivePoi(arg0);
			}

		}
	};

	private void reportGps(BDLocation location) {

		HttpRequest request = new AuthedHttpRequest(
				new HttpConfig.PostHttpConfig(),
				Constants.ServerAPIURI.COMMON_UPLOADLOCATION);

		if (mContext.getPackageName().equals("org.slstudio.hsinchuiot")) {
			request.addParameter("user_type", "0");
		} else {
			request.addParameter("user_type", "1");
		}
		request.addParameter("lon", location.getLongitude() + "");
		request.addParameter("lat", location.getLatitude() + "");
		ServiceContainer
				.getInstance()
				.getHttpHandler()
				.doRequest(request,
						new RequestListener.DefaultRequestListener<Object>() {
							@Override
							public void onRequestResult(Object result) {
								isUpload = false;
							}
						});
	}

	public BDLocation getLastKnown() {
		return lastKnown;
	}
}
