package org.slstudio.hsinchuiot;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.slstudio.hsinchuiot.fragment.UserSiteHomePageFragment;
import org.slstudio.hsinchuiot.model.Device;
import org.slstudio.hsinchuiot.model.IOTMonitorData;
import org.slstudio.hsinchuiot.model.IOTMonitorThreshold;
import org.slstudio.hsinchuiot.model.IOTSampleData;
import org.slstudio.hsinchuiot.model.Site;
import org.slstudio.hsinchuiot.service.ServiceContainer;
import org.slstudio.hsinchuiot.service.SessionService;
import org.slstudio.hsinchuiot.service.http.ForgroundRequestListener;
import org.slstudio.hsinchuiot.service.http.HttpConfig;
import org.slstudio.hsinchuiot.service.http.HttpRequest;
import org.slstudio.hsinchuiot.service.http.NoneAuthedHttpRequest;
import org.slstudio.hsinchuiot.service.http.RequestControl;
import org.slstudio.hsinchuiot.service.http.RequestListener;
import org.slstudio.hsinchuiot.ui.TVOffAnimation;
import org.slstudio.hsinchuiot.util.IOTLog;
import org.slstudio.hsinchuiot.util.ReportUtil;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class UserMainActivity extends BaseActivity{

	private ViewPager viewPager;
	private FragmentPagerAdapter pagerAdapter;
	private List<UserSiteHomePageFragment> fragments = new ArrayList<UserSiteHomePageFragment>();
	private int currentIndex = -1;
	private List<Site> siteList = new ArrayList<Site>();

	private boolean isPaused = false;
	private boolean isRTRequestHandling = false;
	private boolean isChartRequestHandling = false;
	
	private Vector<RequestControl> monitorRCList = new Vector<RequestControl>();
	private Vector<RequestControl> chartRCList = new Vector<RequestControl>();
	
	private int chartType = Constants.ChartSettings.CHART_TYPE_REALTIME;
	private int chartTimeDuration = 5;
	private int chartGranularity = Constants.ChartSettings.GRANULARITY_HOUR;
	private Date chartStartTime;
	private Date chartEndTime;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.MessageKey.MESSAGE_GET_REALTIME_DATA:
				IOTLog.d(
						"Handler",
						"debuginfo(REALTIME_DATA) - handleMessage: receive msg MESSAGE_GET_REALTIME_DATA");
				UserSiteHomePageFragment currentFragment = fragments
						.get(currentIndex);
				if (currentFragment != null && !isPaused
						&& !isRTRequestHandling) {
					sendQueryRealtimeDataRequest(currentFragment.getSite()
							.getDevice().getDeviceID());
				}
				break;
			case Constants.MessageKey.MESSAGE_GET_CHART_DATA:
				IOTLog.d("Handler",
						"debuginfo(CHART_DATA) - handleMessage: receive msg MESSAGE_GET_CHART_DATA");
				UserSiteHomePageFragment currentFragment2 = fragments
						.get(currentIndex);
				if (currentFragment2 != null && !isPaused) {
					if (sendQueryChartDataRequest(currentFragment2.getSite()
							.getDevice().getDeviceID())) {
						currentFragment2.updateChartDataInProcessing();
					}
				}
				break;
			case Constants.MessageKey.MESSAGE_UPDATE_CHART_DATA:
				IOTLog.d("Handler",
						"debuginfo(CHART_DATA) - handleMessage: receive msg MESSAGE_UPDATE_CHART_DATA");
				UserSiteHomePageFragment currentFragment3 = fragments
						.get(currentIndex);
				if (currentFragment3 != null && !isPaused) {
					if (currentFragment3
							.updateChartData((List<IOTSampleData>) msg.obj)) {
						currentFragment3.updateChartDataFinished();
					}
				}
				break;
			case Constants.MessageKey.MESSAGE_UPDATE_MONITOR_DATA:
				IOTLog.d(
						"Handler",
						"debuginfo(CHART_DATA) - handleMessage: receive msg MESSAGE_UPDATE_MONITOR_DATA");
				UserSiteHomePageFragment currentFragment4 = fragments
						.get(currentIndex);
				if (currentFragment4 != null && !isPaused) {
					if (currentFragment4
							.updateMonitorData((IOTMonitorData) msg.obj)) {
					}
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

	public int getChartType() {
		return chartType;
	}

	public int getChartTimeDuration() {
		return chartTimeDuration;
	}

	public int getChartGranularity() {
		return chartGranularity;
	}

	public Date getChartStartTime() {
		return chartStartTime;
	}

	public Date getChartEndTime() {
		return chartEndTime;
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_user_main);

		initViews();
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				getDeviceList();
			}

		});
		ServiceContainer.getInstance().getUpgradeController().checkVersion(null, true);
		//ServiceContainer.getInstance().getUpgradeController().handleUpgrade();
	}

	@Override
	protected void onResume() {

		IOTLog.d("UserMainActivity", "debuginfo - onResume: re-send messages");
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
		
		for(RequestControl rc: monitorRCList){
			rc.cancel();
		}
		IOTLog.d("UserMainActivity",
				"debuginfo(CHART_DATA) - onPause: remove msgs from queue");
		handler.removeMessages(Constants.MessageKey.MESSAGE_GET_CHART_DATA);
		
		IOTLog.d("UserMainActivity",
				"debuginfo(CHART_DATA) - onPause: cancel http request");
		
		for(RequestControl rc: chartRCList){
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
		
		for(RequestControl rc: monitorRCList){
			rc.cancel();
		}
		
		IOTLog.d("UserMainActivity",
				"debuginfo(CHART_DATA) - onDestroy: remove msgs from queue");
		handler.removeMessages(Constants.MessageKey.MESSAGE_GET_CHART_DATA);
		
		IOTLog.d("UserMainActivity",
				"debuginfo(CHART_DATA) - onDestroy: cancel http request");
		
		for(RequestControl rc: chartRCList){
			rc.cancel();
		}
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.ResultCode.CHART_SETTINGS) {
			if (resultCode == RESULT_OK) {
				chartType = data.getIntExtra(
						Constants.ActivityPassValue.CHART_TYPE,
						Constants.ChartSettings.CHART_TYPE_REALTIME);
				if (chartType == Constants.ChartSettings.CHART_TYPE_REALTIME) {
					chartTimeDuration = data.getIntExtra(
							Constants.ActivityPassValue.CHART_RT_DURATION, 5);
				} else if (chartType == Constants.ChartSettings.CHART_TYPE_AGGRAGATION) {
					chartGranularity = data.getIntExtra(
							Constants.ActivityPassValue.CHART_AGGR_GRANULARITY,
							Constants.ChartSettings.GRANULARITY_HOUR);
					long startTimeLong = data
							.getLongExtra(
									Constants.ActivityPassValue.CHART_AGGR_STARTTIME,
									0);
					if (startTimeLong != 0) {
						chartStartTime = new Date();
						chartStartTime.setTime(startTimeLong);
					}
					long endTimeLong = data.getLongExtra(
							Constants.ActivityPassValue.CHART_AGGR_ENDTIME, 0);
					if (endTimeLong != 0) {
						chartEndTime = new Date();
						chartEndTime.setTime(endTimeLong);
					}
				}

				// re-generate chart
				generateChart();

			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			break;
		case R.id.menu_user_main_settings:
			showSettingsActivity();
			break;
		case R.id.menu_user_main_logoff:
			logoff();
			break;
		}
		return true;
	}

	public void showSettingsActivity(){
		Intent intent = new Intent(
				Constants.Action.HSINCHUIOT_USER_SETTINGS);
		startActivity(intent);
	}
	
	public void logoff(){

		ServiceContainer.getInstance().getSessionService()
				.setLoginUser(null);
		ServiceContainer.getInstance().getSessionService()
				.setSessionID(null);
		ServiceContainer
				.getInstance()
				.getSessionService()
				.setSessionValue(Constants.SessionKey.THRESHOLD_BREACH,
						null);
		ServiceContainer
				.getInstance()
				.getSessionService()
				.setSessionValue(Constants.SessionKey.THRESHOLD_WARNING,
						null);

		Intent loginIntent = new Intent(Constants.Action.HSINCHUIOT_LOGIN);
		startActivity(loginIntent);
		finish();

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_user_main, menu);
		setIconEnable(menu, true);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(this)
					.setTitle("系統提示")
					.setMessage("確定要退出嗎?")
					.setPositiveButton(getResources().getString(R.string.yes),
							new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									View v = UserMainActivity.this.findViewById(R.id.view_main_bg);
									v.setBackgroundColor(Color.BLACK);
									new Handler().postDelayed(new Runnable() {

										@Override
										public void run() {
											finish();
										}
									}, 1000);
									viewPager.startAnimation(new TVOffAnimation());
									
								}

							})
					.setNegativeButton(getResources().getString(R.string.no),
							new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}

							}).create().show();

		}else if(keyCode ==KeyEvent.KEYCODE_MENU){
			UserSiteHomePageFragment currentFragment = fragments
					.get(currentIndex);
			if (currentFragment != null){
				currentFragment.onMenuRopeClick();
				return true;
			}
			return true;
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
		IOTLog.d("UserMainActivity",
				"debuginfo(REALTIME_DATA) - resendMessage, remove msgs in queue");
		handler.removeMessages(Constants.MessageKey.MESSAGE_GET_REALTIME_DATA);
		
		IOTLog.d("UserMainActivity",
				"debuginfo(REALTIME_DATA) - resendMessage: cancel http request");
		
		for(RequestControl rc: monitorRCList){
			rc.cancel();
		}
		IOTLog.d("UserMainActivity",
				"debuginfo(CHART_DATA) - resendMessage, remove msgs in queue");
		handler.removeMessages(Constants.MessageKey.MESSAGE_GET_CHART_DATA);

		IOTLog.d("UserMainActivity",
				"debuginfo(CHART_DATA) - resendMessage: cancel http request");
		
		for(RequestControl rc: chartRCList){
			rc.cancel();
		}
		if (currentIndex != -1) {

			IOTLog.d(
					"UserMainActivity",
					"debuginfo(REALTIME_DATA) - resendMessage, send message MESSAGE_GET_REALTIME_DATA");
			Message msg = new Message();
			msg.what = Constants.MessageKey.MESSAGE_GET_REALTIME_DATA;
			handler.sendMessageDelayed(msg, 2000);

			IOTLog.d("UserMainActivity",
					"debuginfo(CHART_DATA) - resendMessage, send message MESSAGE_GET_CHART_DATA");
			Message msg2 = new Message();
			msg2.what = Constants.MessageKey.MESSAGE_GET_CHART_DATA;
			handler.sendMessageDelayed(msg2, 2000);
		}
	}
	

	private void generateChart() {
		if (currentIndex >= 0 && currentIndex < fragments.size() - 1) {
			UserSiteHomePageFragment fragment = fragments.get(currentIndex);
			fragment.generateChart();
		}
	}
	
	private void setIconEnable(Menu menu, boolean enable) {
		try {
			Class<?> clazz = Class
					.forName("com.android.internal.view.menu.MenuBuilder");
			Method m = clazz.getDeclaredMethod("setOptionalIconsVisible",
					boolean.class);
			m.setAccessible(true);

			// MenuBuilder实现Menu接口，创建菜单时，传进来的menu其实就是MenuBuilder对象(java的多态特征)
			m.invoke(menu, enable);

		} catch (Exception e) {
			e.printStackTrace();
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
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
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
			UserSiteHomePageFragment fragment = new UserSiteHomePageFragment();
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

	}

	private void sendQueryRealtimeDataRequest(String deviceID) {
		IOTLog.d(
				"UserMainActivity",
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
	}

	private boolean sendQueryChartDataRequest(String deviceID) {
		if (chartType == Constants.ChartSettings.CHART_TYPE_REALTIME) {
			int pageSize = 12 * chartTimeDuration; // 15s for each type sample,
													// so 12 samples for 3
													// types in 1 min
			if (!isChartRequestHandling) {
				sendQueryRealtimeChartDataRequest(deviceID, pageSize);
			} else {
				return false;
			}
		} else if (chartType == Constants.ChartSettings.CHART_TYPE_AGGRAGATION) {
			if (chartGranularity == Constants.ChartSettings.GRANULARITY_HOUR) {
				sendQuery1HourAggrChartDataRequest(deviceID, chartStartTime,
						chartEndTime);
			} else if (chartGranularity == Constants.ChartSettings.GRANULARITY_HOURS) {
				sendQuery8HoursAggrChartDataRequest(deviceID, chartStartTime,
						chartEndTime);
			} else if (chartGranularity == Constants.ChartSettings.GRANULARITY_DAY) {
				sendQueryDayAggrChartDataRequest(deviceID, chartStartTime,
						chartEndTime);
			} else if (chartGranularity == Constants.ChartSettings.GRANULARITY_WEEK) {
				sendQueryWeekAggrChartDataRequest(deviceID, chartStartTime,
						chartEndTime);
			} else if (chartGranularity == Constants.ChartSettings.GRANULARITY_MONTH) {
				sendQueryMonthAggrChartDataRequest(deviceID, chartStartTime,
						chartEndTime);
			} else {
				return false;
			}
		}
		return true;
	}

	private void sendQueryRealtimeChartDataRequest(String deviceID, int pageSize) {
		IOTLog.d(
				"UserMainActivity",
				"debuginfo(CHART_DATA) - sendQueryRealtimeChartDataRequest: send request for site:"
						+ deviceID);
		isChartRequestHandling = true;
		IOTLog.d(
				"UserMainActivity",
				"debuginfo(CHART_DATA) - sendQueryRealtimeChartDataRequest: set isChartRequestHandling is true");

		HttpRequest request = new NoneAuthedHttpRequest(
				new HttpConfig.GetHttpConfig(),
				Constants.ServerAPIURI.GET_SAMPLE_DATA);
		String sessionID = ServiceContainer.getInstance().getSessionService()
				.getSessionID();
		request.addParameter("dataType", "xml");
		request.addParameter("__session_id", sessionID);
		request.addParameter("__page_no", "1");
		request.addParameter("__column", "name,value,t");
		request.addParameter("__page_size", Integer.toString(pageSize));
		request.addParameter("__group_by", "did,name");
		request.addParameter("__sort", "-id");
		request.addParameter("did[0]", deviceID);
		GetRealtimeChartDataListener l = new GetRealtimeChartDataListener(
				deviceID);

		ServiceContainer.getInstance().getHttpHandler().doRequest(request, l);
	}

	private void sendQuery1HourAggrChartDataRequest(String deviceID,
			Date startTime, Date endTime) {
		IOTLog.d(
				"UserMainActivity",
				"debuginfo(CHART_DATA) - sendQuery1HourAggrChartDataRequest: send request for site:"
						+ deviceID);
		// isChartRequestHandling = true;
		// IOTLog.d("UserMainActivity",
		// "debuginfo(CHART_DATA) - sendQuery1HourAggrChartDataRequest: set
		// isChartRequestHandling is true");

		String from = ReportUtil.getServerTimeHourString(startTime);
		String to = ReportUtil.getServerTimeHourString(endTime);

		HttpRequest request = new NoneAuthedHttpRequest(
				new HttpConfig.GetHttpConfig(),
				Constants.ServerAPIURI.GET_HOUR_AGG_DATA);
		String sessionID = ServiceContainer.getInstance().getSessionService()
				.getSessionID();
		request.addParameter("dataType", "xml");
		request.addParameter("__session_id", sessionID);
		request.addParameter("__page_no", "1");
		request.addParameter("__page_size", "10000");
		request.addParameter("__column",
				"did%2Csensor%2Cname%2Cvalue%2Chour_in_epoch");
		request.addParameter("__group_by",
				"did%2Csensor%2Cname%2Chour_in_epoch");
		request.addParameter("__sort", "-id");
		request.addParameter("hour_in_epoch__from", from);
		request.addParameter("hour_in_epoch__to", to);
		request.addParameter("did%5B0%5D", deviceID);
		GetAggrChartDataListener l = new GetAggrChartDataListener(deviceID,
				from, to);

		ServiceContainer.getInstance().getHttpHandler().doRequest(request, l);
	}

	private void sendQuery8HoursAggrChartDataRequest(String deviceID,
			Date startTime, Date endTime) {
		IOTLog.d(
				"UserMainActivity",
				"debuginfo(CHART_DATA) - sendQuery8HoursAggrChartDataRequest: send request for site:"
						+ deviceID);
		// isChartRequestHandling = true;
		// IOTLog.d("UserMainActivity",
		// "debuginfo(CHART_DATA) - sendQuery8HoursAggrChartDataRequest: set
		// isChartRequestHandling is true");

		String from = ReportUtil.getServerTimeHourString(startTime);
		String to = ReportUtil.getServerTimeHourString(endTime);

		HttpRequest request = new NoneAuthedHttpRequest(
				new HttpConfig.GetHttpConfig(),
				Constants.ServerAPIURI.GET_HOURS_AGG_DATA);
		String sessionID = ServiceContainer.getInstance().getSessionService()
				.getSessionID();
		request.addParameter("dataType", "xml");
		request.addParameter("__session_id", sessionID);
		request.addParameter("__page_no", "1");
		request.addParameter("__page_size", "10000");
		request.addParameter("__column",
				"did%2Csensor%2Cname%2Cvalue%2Chours_in_epoch");
		request.addParameter("__group_by",
				"did%2Csensor%2Cname%2Chours_in_epoch");
		request.addParameter("__sort", "-id");
		request.addParameter("hours_in_epoch__from", from);
		request.addParameter("hours_in_epoch__to", to);
		request.addParameter("did%5B0%5D", deviceID);
		GetAggrChartDataListener l = new GetAggrChartDataListener(deviceID,
				from, to);

		ServiceContainer.getInstance().getHttpHandler().doRequest(request, l);
	}

	private void sendQueryDayAggrChartDataRequest(String deviceID,
			Date startTime, Date endTime) {
		IOTLog.d(
				"UserMainActivity",
				"debuginfo(CHART_DATA) - sendQueryDayAggrChartDataRequest: send request for site:"
						+ deviceID);
		// isChartRequestHandling = true;
		// IOTLog.d("UserMainActivity",
		// "debuginfo(CHART_DATA) - sendQueryDayAggrChartDataRequest: set
		// isChartRequestHandling is true");

		String from = ReportUtil.getServerTimeDayString(startTime);
		String to = ReportUtil.getServerTimeDayString(endTime);

		HttpRequest request = new NoneAuthedHttpRequest(
				new HttpConfig.GetHttpConfig(),
				Constants.ServerAPIURI.GET_DAY_AGG_DATA);
		String sessionID = ServiceContainer.getInstance().getSessionService()
				.getSessionID();
		request.addParameter("dataType", "xml");
		request.addParameter("__session_id", sessionID);
		request.addParameter("__page_no", "1");
		request.addParameter("__page_size", "10000");
		request.addParameter("__column",
				"did%2Csensor%2Cname%2Cvalue%2Cday_in_epoch");
		request.addParameter("__group_by", "did%2Csensor%2Cname%2Cday_in_epoch");
		request.addParameter("__sort", "-id");
		request.addParameter("day_in_epoch__from", from);
		request.addParameter("day_in_epoch__to", to);
		request.addParameter("did%5B0%5D", deviceID);
		GetAggrChartDataListener l = new GetAggrChartDataListener(deviceID,
				from, to);

		ServiceContainer.getInstance().getHttpHandler().doRequest(request, l);
	}

	private void sendQueryWeekAggrChartDataRequest(String deviceID,
			Date startTime, Date endTime) {
		IOTLog.d(
				"UserMainActivity",
				"debuginfo(CHART_DATA) - sendQueryWeekAggrChartDataRequest: send request for site:"
						+ deviceID);
		// isChartRequestHandling = true;
		// IOTLog.d("UserMainActivity",
		// "debuginfo(CHART_DATA) - sendQueryWeekAggrChartDataRequest: set
		// isChartRequestHandling is true");

		String from = ReportUtil.getServerTimeDayString(startTime);
		String to = ReportUtil.getServerTimeDayString(endTime);

		HttpRequest request = new NoneAuthedHttpRequest(
				new HttpConfig.GetHttpConfig(),
				Constants.ServerAPIURI.GET_WEEK_AGG_DATA);
		String sessionID = ServiceContainer.getInstance().getSessionService()
				.getSessionID();
		request.addParameter("dataType", "xml");
		request.addParameter("__session_id", sessionID);
		request.addParameter("__page_no", "1");
		request.addParameter("__page_size", "10000");
		request.addParameter("__column",
				"did%2Csensor%2Cname%2Cvalue%2Cweek_in_epoch");
		request.addParameter("__group_by",
				"did%2Csensor%2Cname%2Cweek_in_epoch");
		request.addParameter("__sort", "-id");
		request.addParameter("week_in_epoch__from", from);
		request.addParameter("week_in_epoch__to", to);
		request.addParameter("did%5B0%5D", deviceID);
		GetAggrChartDataListener l = new GetAggrChartDataListener(deviceID,
				from, to);

		ServiceContainer.getInstance().getHttpHandler().doRequest(request, l);
	}

	private void sendQueryMonthAggrChartDataRequest(String deviceID,
			Date startTime, Date endTime) {
		IOTLog.d(
				"UserMainActivity",
				"debuginfo(CHART_DATA) - sendQueryMonthAggrChartDataRequest: send request for site:"
						+ deviceID);
		// isChartRequestHandling = true;
		// IOTLog.d("UserMainActivity",
		// "debuginfo(CHART_DATA) - sendQueryMonthAggrChartDataRequest: set
		// isChartRequestHandling is true");

		String from = ReportUtil.getServerTimeDayString(startTime);
		String to = ReportUtil.getServerTimeDayString(endTime);

		HttpRequest request = new NoneAuthedHttpRequest(
				new HttpConfig.GetHttpConfig(),
				Constants.ServerAPIURI.GET_MONTH_AGG_DATA);
		String sessionID = ServiceContainer.getInstance().getSessionService()
				.getSessionID();
		request.addParameter("dataType", "xml");
		request.addParameter("__session_id", sessionID);
		request.addParameter("__page_no", "1");
		request.addParameter("__page_size", "10000");
		request.addParameter("__column",
				"did%2Csensor%2Cname%2Cvalue%2Cmonth_in_epoch");
		request.addParameter("__group_by",
				"did%2Csensor%2Cname%2Cmonth_in_epoch");
		request.addParameter("__sort", "-id");
		request.addParameter("month_in_epoch__from", from);
		request.addParameter("month_in_epoch__to", to);
		request.addParameter("did%5B0%5D", deviceID);
		GetAggrChartDataListener l = new GetAggrChartDataListener(deviceID,
				from, to);

		ServiceContainer.getInstance().getHttpHandler().doRequest(request, l);
	}

	private List<IOTSampleData> convertToChartData(
			Map<String, IOTMonitorData> result) {
		List<IOTSampleData> chartData = new ArrayList<IOTSampleData>();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Set<String> keys = result.keySet();
		for (String key : keys) {
			IOTMonitorData data = result.get(key);
			float co2 = data.getCo2();
			float temperature = data.getTemperature();
			float humidity = data.getHumidity();
			Date time = null;
			try {
				time = ReportUtil.getLocalTime(sdf.parse(key));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (time != null) {
				IOTSampleData sample1 = new IOTSampleData(
						IOTSampleData.IOTSampleDataType.CO2, time, co2);
				IOTSampleData sample2 = new IOTSampleData(
						IOTSampleData.IOTSampleDataType.TEMPERATURE, time,
						temperature);
				IOTSampleData sample3 = new IOTSampleData(
						IOTSampleData.IOTSampleDataType.HUMIDITY, time,
						humidity);

				chartData.add(sample1);
				chartData.add(sample2);
				chartData.add(sample3);
			}
		}

		return chartData;
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

			final UserSiteHomePageFragment fragment = fragments
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
			UserMainActivity.this.monitorRCList.add(control);
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
			UserMainActivity.this.monitorRCList.remove(control);
			
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

	private class GetRealtimeChartDataListener implements
			RequestListener<List<IOTSampleData>> {
		private RequestControl control;
		private String deviceID;

		public GetRealtimeChartDataListener(String deviceID) {
			this.deviceID = deviceID;
		}

		@Override
		public void onRequestCancelled() {
			if (control != null)
				control.cancel();

		}

		@Override
		public void onRequestResult(final List<IOTSampleData> result) {
			IOTLog.d("GetRealtimeChartDataListener",
					"debuginfo(CHART_DATA) - onRequestResult: receive response for request:"
							+ deviceID);
			// IOTLog.d("UserMainActivity", "receive chart result size:" +
			// result.size());

			final UserSiteHomePageFragment fragment = fragments
					.get(currentIndex);

			if (fragment != null
					&& fragment.getSite().getDevice().getDeviceID()
							.equals(deviceID)) {
				Message msg = new Message();
				msg.what = Constants.MessageKey.MESSAGE_UPDATE_CHART_DATA;
				msg.obj = result;
				handler.sendMessage(msg);
			}

		}

		@Override
		public void onRequestGetControl(RequestControl control) {
			this.control = control;
			UserMainActivity.this.chartRCList.add(control);
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
			isChartRequestHandling = false;
			UserMainActivity.this.chartRCList.remove(control);
			
			IOTLog.d(
					"GetRealtimeChartDataListener",
					"debuginfo(CHART_DATA) - onRequestComplete: set isChartRequestHandling is false");

			Message msg = new Message();
			msg.what = Constants.MessageKey.MESSAGE_GET_CHART_DATA;

			int refreshTime = (Integer) ServiceContainer
					.getInstance()
					.getSessionService()
					.getSessionValue(
							Constants.SessionKey.REALTIME_DATA_MONITOR_REFRESH_TIME,
							10);
			IOTLog.d(
					"GetRealtimeChartDataListener",
					"debuginfo(CHART_DATA) - onRequestComplete: send message MESSAGE_GET_CHART_DATA for "
							+ deviceID
							+ " with deplay time:"
							+ refreshTime
							+ "s");
			handler.sendMessageDelayed(msg, refreshTime * 1000);

		}
	}

	private class GetAggrChartDataListener implements
			RequestListener<Map<String, IOTMonitorData>> {
		private RequestControl control;
		private String deviceID;
		private String from;
		private String to;

		public GetAggrChartDataListener(String deviceID, String from, String to) {
			this.deviceID = deviceID;
			this.from = from;
			this.to = to;
		}

		@Override
		public void onRequestCancelled() {
			if (control != null)
				control.cancel();

		}

		@Override
		public void onRequestResult(final Map<String, IOTMonitorData> result) {
			IOTLog.d("GetAggrChartDataListener",
					"debuginfo(CHART_DATA) - onRequestResult: receive response for request:"
							+ deviceID);
			final List<IOTSampleData> chartData = convertToChartData(result);

			final UserSiteHomePageFragment fragment = fragments
					.get(currentIndex);

			if (fragment != null
					&& fragment.getSite().getDevice().getDeviceID()
							.equals(deviceID)) {
				Message msg = new Message();
				msg.what = Constants.MessageKey.MESSAGE_UPDATE_CHART_DATA;
				msg.obj = chartData;
				handler.sendMessage(msg);
			}

		}

		@Override
		public void onRequestGetControl(RequestControl control) {
			this.control = control;
			UserMainActivity.this.chartRCList.add(control);
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
			UserMainActivity.this.chartRCList.remove(control);
			// isChartRequestHandling = false;
			// IOTLog.d("GetAggrChartDataListener",
			// "debuginfo(CHART_DATA) - onRequestComplete: set
			// isChartRequestHandling is false");

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
