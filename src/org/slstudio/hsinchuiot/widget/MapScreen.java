package org.slstudio.hsinchuiot.widget;

import org.slstudio.hsinchuiot.R;
import org.slstudio.hsinchuiot.service.LocationService;
import org.slstudio.hsinchuiot.service.ServiceContainer;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;

public class MapScreen extends FrameLayout {
	private LocationService locationService;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private SupportMapFragment mapFragment;
	private LocationMode mCurrentMode;
	private BitmapDescriptor mCurrentMarker;
	public BDLocationListener myListener = new MyLocationListener();
	boolean isFirstLoc = true;// 是否首次定位
	private ImageView locateMe;
	private Context mContext;
	private MyLocationData locData;

	public MapScreen(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public MapScreen(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public MapScreen(Context context) {
		super(context);
		initView(context);
	}

	private void initView(Context context) {
		this.mContext = context;
		View content = null;
		try {
			content = LayoutInflater.from(context).inflate(
					R.layout.common_widget_map_screen, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		addView(content);

		locateMe = (ImageView) content.findViewById(R.id.locate_me_button);
		locateMe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mBaiduMap == null) {
					return;
				}
				mCurrentMode = LocationMode.FOLLOWING;
				mBaiduMap
						.setMyLocationConfigeration(new MyLocationConfiguration(
								mCurrentMode, true, mCurrentMarker));

			}
		});
	}

	public void create() {
		MapStatus ms = new MapStatus.Builder().overlook(-20).zoom(15).build();
		BaiduMapOptions bo = new BaiduMapOptions().mapStatus(ms)
				.compassEnabled(true).zoomControlsEnabled(true);

		mMapView = (MapView) findViewById(R.id.bmapView);

		// 定位初始化
		locationService = ServiceContainer.getInstance().getLocationService(
				mContext.getApplicationContext());
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMyLocationEnabled(true);
		locationService.addListener(myListener);
		locationService.start();

		/*
		 * new Handler().postDelayed(new Runnable() {
		 * 
		 * @Override public void run() {
		 */
		// mMapView = mapFragment.getMapView();

		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				/*
				 * Driver driver = driverMap.get(marker); Intent intent = new
				 * Intent(mContext, DriverInfoActivity.class);
				 * intent.putExtra(DriverInfoActivity.ACTIVITY_INTENT_DRIVER ,
				 * driver); mContext.startActivity(intent);
				 */
				return false;
			}
		});
		// 开启定位图层

		/*
		 * } }, 500);
		 */

	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			Log.e("BaiduMap", "getLatitude:" + location.getLatitude()
					+ "getLongitude" + location.getLongitude());
			if (isFirstLoc) {
				Log.e("BaiduMap", "isFirstLoc");
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
				if (locateMeListener != null) {
					locateMeListener.onLocateMe(location);
				}
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	private LocateMeListener locateMeListener;

	public void setLocateMeListener(LocateMeListener locateMeListener) {
		this.locateMeListener = locateMeListener;
	}

	public interface LocateMeListener {
		public void onLocateMe(BDLocation location);
	}

	public void onPause() {
		if (mMapView != null) {
			mMapView.onPause();
		}
	}

	public void onResume() {
		if (mMapView != null) {
			mMapView.onResume();
		}
	}

	public void onDestroy() {
		if (mMapView == null) {
			return;
		}
		// 退出时销毁定位
		try {
			locationService.removeListener(myListener);
			locationService.stop();
			// 关闭定位图层
			mBaiduMap.setMyLocationEnabled(false);
			mMapView.onDestroy();
			mMapView = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
