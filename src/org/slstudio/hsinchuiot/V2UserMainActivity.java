package org.slstudio.hsinchuiot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.slstudio.hsinchuiot.fragment.V2UserSiteHomePageFragment;
import org.slstudio.hsinchuiot.model.Device;
import org.slstudio.hsinchuiot.model.IOTMonitorData;
import org.slstudio.hsinchuiot.model.IOTSampleData;
import org.slstudio.hsinchuiot.model.Site;
import org.slstudio.hsinchuiot.service.ServiceContainer;
import org.slstudio.hsinchuiot.service.http.ForgroundRequestListener;
import org.slstudio.hsinchuiot.service.http.HttpConfig;
import org.slstudio.hsinchuiot.service.http.HttpRequest;
import org.slstudio.hsinchuiot.service.http.NoneAuthedHttpRequest;
import org.slstudio.hsinchuiot.service.http.RequestControl;
import org.slstudio.hsinchuiot.service.http.RequestListener;
import org.slstudio.hsinchuiot.ui.TVOffAnimation;
import org.slstudio.hsinchuiot.util.IOTLog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;

public class V2UserMainActivity extends BaseActivity {

	private ViewPager viewPager;
	private FragmentPagerAdapter pagerAdapter;
	private List<V2UserSiteHomePageFragment> fragments = new ArrayList<V2UserSiteHomePageFragment>();
	private int currentIndex = -1;
	private List<Site> siteList = new ArrayList<Site>();

	private boolean isPaused = false;
	private boolean isRTRequestHandling = false;

	private Vector<RequestControl> monitorRCList = new Vector<RequestControl>();

	private ProgressDialog progressDialog;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.MessageKey.MESSAGE_GET_REALTIME_DATA:
				IOTLog.d(
						"Handler",
						"debuginfo(REALTIME_DATA) - handleMessage: receive msg MESSAGE_GET_REALTIME_DATA");
				V2UserSiteHomePageFragment currentFragment = fragments
						.get(currentIndex);
				if (currentFragment != null && !isPaused
						&& !isRTRequestHandling) {
					if (sendQueryRealtimeDataRequest(currentFragment.getSite()
							.getDevice().getDeviceID())) {
						updateDataInProcessing();
					}
				}
				break;
			case Constants.MessageKey.MESSAGE_UPDATE_MONITOR_DATA:
				IOTLog.d(
						"Handler",
						"debuginfo(CHART_DATA) - handleMessage: receive msg MESSAGE_UPDATE_MONITOR_DATA");
				V2UserSiteHomePageFragment currentFragment2 = fragments
						.get(currentIndex);
				if (currentFragment2 != null && !isPaused) {
					if (currentFragment2
							.updateMonitorData((IOTMonitorData) msg.obj)) {
						updateDataFinished();
					}
				}
				break;

			case Constants.MessageKey.V2_MESSAGE_UPDATE_TIME:
				V2UserSiteHomePageFragment currentFragment3 = fragments
						.get(currentIndex);
				if (currentFragment3 != null && !isPaused) {
					currentFragment3.updateTimeInfo();
				}

				handler.sendEmptyMessageDelayed(
						Constants.MessageKey.V2_MESSAGE_UPDATE_TIME, 1000);
				break;

			case Constants.MessageKey.V2_MESSAGE_UPDATE_ALARM:
				V2UserSiteHomePageFragment currentFragment4 = fragments
						.get(currentIndex);
				if (currentFragment4 != null && !isPaused) {
					currentFragment4.updateAlarm();
				}

				break;
			}

			super.handleMessage(msg);
		}
	};

	public List<Site> getSiteList() {
		return siteList;
	}

	public ViewPager getViewPager() {
		return viewPager;
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.v2_activity_user_main);

		initViews();
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				getDeviceList();
			}

		});

		ServiceContainer.getInstance().getUpgradeController()
				.checkVersion(null, true);
		// ServiceContainer.getInstance().getUpgradeController().handleUpgrade();
	}

	@Override
	protected void onResume() {

		IOTLog.d("V2UserMainActivity", "debuginfo - onResume: re-send messages");
		isPaused = false;

		resendMessage();

		super.onResume();
	}

	@Override
	protected void onPause() {
		isPaused = true;
		IOTLog.d("UserMainActivity",
				"debuginfo(REALTIME_DATA) - onPause: remove msgs from queue");
		handler.removeMessages(Constants.MessageKey.MESSAGE_GET_REALTIME_DATA);

		IOTLog.d("UserMainActivity",
				"debuginfo(REALTIME_DATA) - onPause: cancel http request");

		for (RequestControl rc : monitorRCList) {
			rc.cancel();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		IOTLog.d("UserMainActivity",
				"debuginfo(REALTIME_DATA) - onDestroy: remove msgs from queue");
		handler.removeMessages(Constants.MessageKey.MESSAGE_GET_REALTIME_DATA);

		IOTLog.d("UserMainActivity",
				"debuginfo(REALTIME_DATA) - onDestroy: cancel http request");

		for (RequestControl rc : monitorRCList) {
			rc.cancel();
		}

		super.onDestroy();
	}

	public void logoff() {

		ServiceContainer.getInstance().getSessionService().setLoginUser(null);
		ServiceContainer.getInstance().getSessionService().setSessionID(null);
		ServiceContainer.getInstance().getSessionService()
				.setSessionValue(Constants.SessionKey.THRESHOLD_BREACH, null);
		ServiceContainer.getInstance().getSessionService()
				.setSessionValue(Constants.SessionKey.THRESHOLD_WARNING, null);

		Intent loginIntent = new Intent(Constants.Action.HSINCHUIOT_LOGIN);
		startActivity(loginIntent);
		finish();

	}

	public void updateDataFinished() {
		IOTLog.d("V2UserMainActivity",
				"debuginfo - updateDataFinished: update data proceed");
		progressDialog.dismiss();
	}

	public void updateDataInProcessing() {
		IOTLog.d("V2UserMainActivity",
				"debuginfo - updateDataInProcessing: update data in processing");
		showProgressDialog();
	}

	public void updateAlarmStatus() {
		handler.sendEmptyMessage(Constants.MessageKey.V2_MESSAGE_UPDATE_ALARM);
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(this)
					.setTitle(getResources().getString(R.string.dlg_title_systemprompt))
					.setMessage(getResources().getString(R.string.dlg_caption_exit))
					.setPositiveButton(getResources().getString(R.string.yes),
							new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									View v = V2UserMainActivity.this
											.findViewById(R.id.view_main_bg);
									v.setBackgroundColor(Color.BLACK);
									new Handler().postDelayed(new Runnable() {

										@Override
										public void run() {
											finish();
										}
									}, 1000);
									viewPager
											.startAnimation(new TVOffAnimation());

								}

							})
					.setNegativeButton(getResources().getString(R.string.no),
							new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}

							}).create().show();

		}

		return false;

	}

	public void moveToPreSite() {
		int currentItem = viewPager.getCurrentItem();
		if (currentItem > 0) {
			viewPager.setCurrentItem(currentItem - 1);
		}
	}

	public void moveToNextSite() {
		int currentItem = viewPager.getCurrentItem();
		if (currentItem < fragments.size() - 1) {
			viewPager.setCurrentItem(currentItem + 1);
		}
	}

	public void resendMessage() {

		handler.removeMessages(Constants.MessageKey.V2_MESSAGE_UPDATE_TIME);

		IOTLog.d("UserMainActivity",
				"debuginfo(REALTIME_DATA) - resendMessage, remove msgs in queue");
		handler.removeMessages(Constants.MessageKey.MESSAGE_GET_REALTIME_DATA);

		IOTLog.d("UserMainActivity",
				"debuginfo(REALTIME_DATA) - resendMessage: cancel http request");

		for (RequestControl rc : monitorRCList) {
			rc.cancel();
		}

		if (currentIndex != -1) {

			handler.sendEmptyMessage(Constants.MessageKey.V2_MESSAGE_UPDATE_TIME);

			IOTLog.d(
					"UserMainActivity",
					"debuginfo(REALTIME_DATA) - resendMessage, send message MESSAGE_GET_REALTIME_DATA");
			handler.sendEmptyMessage(Constants.MessageKey.MESSAGE_GET_REALTIME_DATA);

		}
	}

	private void initViews() {

		viewPager = (ViewPager) findViewById(R.id.vp_user_site_home);
		pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return fragments.size();
			}

			@Override
			public Fragment getItem(int arg0) {
				return fragments.get(arg0);
			}
		};

		viewPager.setAdapter(pagerAdapter);

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				currentIndex = position;
				resendMessage();
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

	}

	private void showProgressDialog() {
		progressDialog = ProgressDialog.show(this, "",
				getString(R.string.chart_waiting), true);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(true);
		progressDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
			}
		});
	}

	private void getDeviceList() {
		String sessionID = ServiceContainer.getInstance().getSessionService()
				.getSessionID();

		HttpRequest request = new NoneAuthedHttpRequest(
				new HttpConfig.GetHttpConfig(),
				Constants.ServerAPIURI.DEVICE_LIST);

		request.addParameter("dataType", "xml");
		request.addParameter("__session_id", sessionID);
		request.addParameter("__page_no", "1");
		request.addParameter("__page_size", "1000");
		request.addParameter("__sort", "-id");

		GetDeviceListListener listener = new GetDeviceListListener(this, true,
				getString(R.string.common_please_wait));

		ServiceContainer.getInstance().getHttpHandler()
				.doRequest(request, listener);

	}

	private void createHomeFragments() {
		fragments.clear();

		for (int i = 0; i < siteList.size(); i++) {
			Site site = siteList.get(i);
			V2UserSiteHomePageFragment fragment = new V2UserSiteHomePageFragment();
			fragment.setSite(site);
			fragment.setIndex(i);
			fragments.add(fragment);
		}

		pagerAdapter.notifyDataSetChanged();

		if (fragments.size() > 0) {
			viewPager.setCurrentItem(0);
			currentIndex = 0;
			resendMessage();
		}

		// handler.sendEmptyMessage(Constants.MessageKey.V2_MESSAGE_UPDATE_TIME);

	}

	private boolean sendQueryRealtimeDataRequest(String deviceID) {
		IOTLog.d(
				"V2UserMainActivity",
				"debuginfo(REALTIME_DATA) - sendQueryRealtimeDataRequest: send request for site:"
						+ deviceID);
		isRTRequestHandling = true;
		IOTLog.d(
				"UserMainActivity",
				"debuginfo(REALTIME_DATA) - sendQueryRealtimeDataRequest: set isRTRequestHandling is true");

		HttpRequest request = new NoneAuthedHttpRequest(
				new HttpConfig.GetHttpConfig(),
				Constants.ServerAPIURI.GET_SAMPLE_DATA);
		String sessionID = ServiceContainer.getInstance().getSessionService()
				.getSessionID();
		request.addParameter("dataType", "xml");
		request.addParameter("__session_id", sessionID);
		request.addParameter("__page_no", "1");
		request.addParameter("__column", "did,sensor,name,value,t");
		request.addParameter("__having_max", "id");
		request.addParameter("__group_by", "did,name");
		request.addParameter("__sort", "-id");
		request.addParameter("did[0]", deviceID);
		GetRealtimeDataListener l = new GetRealtimeDataListener(deviceID);

		ServiceContainer.getInstance().getHttpHandler().doRequest(request, l);

		return true;
	}

	private class GetRealtimeDataListener implements
			RequestListener<List<IOTSampleData>> {
		private RequestControl control;
		private String deviceID;

		public GetRealtimeDataListener(String deviceID) {
			this.deviceID = deviceID;
		}

		@Override
		public void onRequestCancelled() {
			if (control != null)
				control.cancel();

		}

		@Override
		public void onRequestResult(final List<IOTSampleData> result) {
			IOTLog.d("GetRealtimeDataListener",
					"debuginfo(REATTIME_DATA) - onRequestResult: receive response for request:"
							+ deviceID);
			// IOTLog.d("UserMainActivity", "receive result size:" +
			// result.size());

			final IOTMonitorData data = new IOTMonitorData();
			for (IOTSampleData sample : result) {
				if (sample.getType() == IOTSampleData.IOTSampleDataType.CO2) {
					data.setCo2(sample.getValue());
				} else if (sample.getType() == IOTSampleData.IOTSampleDataType.TEMPERATURE) {
					data.setTemperature(sample.getValue());
				} else if (sample.getType() == IOTSampleData.IOTSampleDataType.HUMIDITY) {
					data.setHumidity(sample.getValue());
				}
			}

			final V2UserSiteHomePageFragment fragment = fragments
					.get(currentIndex);

			if (fragment != null
					&& fragment.getSite().getDevice().getDeviceID()
							.equals(deviceID)) {

				Message msg = new Message();
				msg.what = Constants.MessageKey.MESSAGE_UPDATE_MONITOR_DATA;
				msg.obj = data;

				handler.sendMessage(msg);
			}

		}

		@Override
		public void onRequestGetControl(RequestControl control) {
			this.control = control;
			V2UserMainActivity.this.monitorRCList.add(control);
		}

		@Override
		public void onRequestStart() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRequestError(Exception e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRequestComplete() {
			V2UserMainActivity.this.monitorRCList.remove(control);

			Message msg = new Message();
			msg.what = Constants.MessageKey.MESSAGE_GET_REALTIME_DATA;

			int refreshTime = (Integer) ServiceContainer
					.getInstance()
					.getSessionService()
					.getSessionValue(
							Constants.SessionKey.REALTIME_DATA_MONITOR_REFRESH_TIME,
							10);

			IOTLog.d(
					"GetRealtimeDataListener",
					"debuginfo(REALTIME_DATA) - onRequestComplete: send message MESSAGE_GET_REALTIME_DATA for "
							+ deviceID
							+ " with deplay time:"
							+ refreshTime
							+ "s");
			handler.sendMessageDelayed(msg, refreshTime * 1000);
			isRTRequestHandling = false;
			IOTLog.d(
					"GetRealtimeDataListener",
					"debuginfo(REALTIME_DATA) - onRequestComplete: set isRTRequestHandling is false");

		}
	}

	private class GetDeviceListListener extends
			ForgroundRequestListener<List<Device>> {

		public GetDeviceListListener(Context context,
				boolean isShowProgressDialog, String content) {
			super(context, isShowProgressDialog, content);
		}

		@Override
		public void onRequestComplete() {
			super.onRequestComplete();
		}

		@Override
		public void onRequestResult(final List<Device> result) {
			siteList.clear();
			for (Device d : result) {
				Site site = new Site();
				site.setSiteID(d.getDeviceID());
				site.setDevice(d);
				site.setSiteName(d.getSiteName());
				site.setSiteImageFilename("site_" + d.getDeviceSN() + ".png");
				site.setMonitorData(new IOTMonitorData(0, 0, 0));
				siteList.add(site);
			}

			handler.post(new Runnable() {

				@Override
				public void run() {
					createHomeFragments();
				}

			});

		}
	}

}
