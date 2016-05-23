package org.slstudio.hsinchuiot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.slstudio.hsinchuiot.model.Alarm;
import org.slstudio.hsinchuiot.model.Device;
import org.slstudio.hsinchuiot.model.Site;
import org.slstudio.hsinchuiot.model.User;
import org.slstudio.hsinchuiot.service.IOTException;
import org.slstudio.hsinchuiot.service.LoginService;
import org.slstudio.hsinchuiot.service.ServiceContainer;
import org.slstudio.hsinchuiot.ui.adapter.V2AlarmListViewAdapter;
import org.slstudio.hsinchuiot.util.IOTLog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

public class V2AlarmActivity extends BaseActivity {

	private Site currentSite = null;

	protected ListView listView;
	protected V2AlarmListViewAdapter lvAdapter;

	private ImageButton btnBack;
	private ImageButton btnDeleteAll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		currentSite = (Site) getIntent().getSerializableExtra(
				Constants.ActivityPassValue.SELECTED_SITE);

		setContentView(R.layout.v2_activity_alarm);
		initViews();
		
		new Handler().post(new Runnable() {

			@Override
			public void run() {
				// start new thread for handle the login
				new Thread(new Runnable() {

					@Override
					public void run() {
						checkLogin();
						// showDebugActivity(Constants.Action.HSINCHUIOT_USER_CHART_SETTINGS);
					}

				}).start();

			}

		});
	}
	
	
	public void refreshList() {
		refreshAlarmList();
	}
	
	private void initViews() {
		btnBack = (ImageButton) findViewById(R.id.btn_back);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}

		});

		btnDeleteAll = (ImageButton) findViewById(R.id.btn_deleteall);
		btnDeleteAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(V2AlarmActivity.this)
						.setTitle(
								getResources().getString(
										R.string.dlg_title_systemprompt))
						.setMessage(
								getResources().getString(
										R.string.dlg_caption_deleteallalarms))
						.setPositiveButton(
								getResources().getString(R.string.yes),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										deleteAllAlarms();
										refreshAlarmList();
									}

								})
						.setNegativeButton(
								getResources().getString(R.string.no),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
									}

								}).create().show();
			}
		});

		listView = (ListView) findViewById(R.id.alarm_list_view);
		listView.setVisibility(View.VISIBLE);

		lvAdapter = new V2AlarmListViewAdapter(this, getSiteAlarms(), currentSite);

		listView.setAdapter(lvAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				
				Alarm alarm = (Alarm)lvAdapter.getItem(position);
				
				Device fakeDevice = new Device();
				fakeDevice.setDeviceID(alarm.getDeviceID());
				Site fakeSite = new Site();
				fakeSite.setDevice(fakeDevice);
				fakeSite.setSiteName(alarm.getAlarmSite());
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date alarmTime = new Date();
				try{
					alarmTime = sdf.parse(alarm.getAlarmTime());
				}catch(Exception exp){
					exp.printStackTrace();
					
				}
				Calendar from = Calendar.getInstance();
				from.setTime(alarmTime);
				from.add(Calendar.MINUTE, -5);
				
				Calendar to = Calendar.getInstance();
				to.setTime(alarmTime);
				to.add(Calendar.MINUTE, 5);
				
				IOTLog.f("V2AlarmActivity", "Start site detail activity for alarm:" + alarm.toString());
				
				Intent intent = new Intent(Constants.Action.HSINCHUIOT_SITEDETAIL);
				
				intent.putExtra(Constants.ActivityPassValue.SELECTED_SITE,
						fakeSite);
				
				intent.putExtra(Constants.ActivityPassValue.CHART_TYPE,
						Constants.ChartSettings.CHART_TYPE_AGGRAGATION);
				/*
				intent.putExtra(Constants.ActivityPassValue.CHART_RT_DURATION,
						10);
				*/
				intent.putExtra(Constants.ActivityPassValue.CHART_AGGR_GRANULARITY,
						Constants.ChartSettings.GRANULARITY_SECONDS);
				
				
				intent.putExtra(Constants.ActivityPassValue.CHART_AGGR_STARTTIME,
						from.getTime().getTime());
				
				intent.putExtra(Constants.ActivityPassValue.CHART_AGGR_ENDTIME,
						to.getTime().getTime());
				
				startActivity(intent);
			}
		});
	}

	private List<Alarm> getSiteAlarms() {
		List<Alarm> result = new ArrayList<Alarm>();
		try {
			String alarmListString = ServiceContainer.getInstance()
					.getPerferenceService()
					.getValue(Constants.PreferenceKey.ALARM_LIST);
			StringTokenizer st = new StringTokenizer(alarmListString, "|");
			while (st.hasMoreElements()) {
				String alarmString = st.nextToken();

				StringTokenizer st2 = new StringTokenizer(alarmString, ";");
				String time = st2.nextToken();
				String siteName = st2.nextToken();
				String alarmValueType = st2.nextToken();
				String alarmValue = st2.nextToken();
				String alarmType = st2.nextToken();
				String deviceID = st2.nextToken();

				Alarm alarm = new Alarm(time, deviceID, siteName,
						alarmValueType, alarmValue, alarmType);
				result.add(alarm);
				
			}
		} catch (Exception exp) {
			IOTLog.e("V2AlarmActivity", "Retrieve alarm list failed", exp);
		}

		return result;
	}
	
	private void deleteAllAlarms(){

		String leftAlarmListString = "";
		
		String alarmListString = ServiceContainer.getInstance().getPerferenceService().getValue(Constants.PreferenceKey.ALARM_LIST);

		StringTokenizer st = new StringTokenizer(alarmListString, "|");
		while (st.hasMoreElements()) {
			String alarmString = st.nextToken();

			StringTokenizer st2 = new StringTokenizer(alarmString, ";");
			String time = st2.nextToken();
			String siteName = st2.nextToken();
			String alarmValueType = st2.nextToken();
			String alarmValue = st2.nextToken();
			String alarmType = st2.nextToken();
			String deviceID = st2.nextToken();
			
			if(currentSite!= null && (!currentSite.getSiteName().equals(siteName))){
				if(!leftAlarmListString.equals("")){
					leftAlarmListString += "|";
				}
				leftAlarmListString += alarmString;
			}
		}
		
		ServiceContainer.getInstance().getPerferenceService().setValue(Constants.PreferenceKey.ALARM_LIST, leftAlarmListString);

	}
	
	private void refreshAlarmList(){
		lvAdapter = new V2AlarmListViewAdapter(this, getSiteAlarms(), currentSite);
		listView.setAdapter(lvAdapter);
		lvAdapter.notifyDataSetChanged();
	}

	
	private void checkLogin() {
		
		if (ServiceContainer.getInstance()
				.getSessionService().getLoginUser() != null) {
			String sessionID = ServiceContainer.getInstance().getSessionService().getSessionID();
			if (sessionID != null && (!sessionID.equals(""))){
				//user already login, then skip login process
				return;
			}
		} 
		
		//user not login, do check remember user and relogin if needed
		String loginName = ServiceContainer
				.getInstance()
				.getPerferenceService()
				.getValue(
						Constants.PreferenceKey.LOGINNAME);
		String password = ServiceContainer
				.getInstance()
				.getPerferenceService()
				.getValue(
						Constants.PreferenceKey.PASSWORD);

		if (loginName.equals("") || password.equals("")) {
			showLoginActivity();
		} else {
			try {
				if (LoginService.getInstance().login(loginName, password)) {
					ServiceContainer
							.getInstance()
							.getSessionService()
							.setSessionValue(
									Constants.SessionKey.THRESHOLD_WARNING,
									LoginService.getWarningThreshold(this));
					ServiceContainer
							.getInstance()
							.getSessionService()
							.setSessionValue(
									Constants.SessionKey.THRESHOLD_BREACH,
									LoginService.getBreachThreshold(this));
					User loginUser = ServiceContainer.getInstance()
							.getSessionService().getLoginUser();

					if (loginUser == null) {
						showLoginActivity();
					} else {
						if (loginUser.isAdminUser()) {
							//handle gcm register
							try{
								ServiceContainer.getInstance().getPushService().registerGSM();
							}catch(IOTException exp){
								
							}
							
							
						} else if (loginUser.isNormalUser()) {
							
							//handle gcm register
							try{
								ServiceContainer.getInstance().getPushService().registerGSM();
							}catch(IOTException exp){
								
							}
							int refreshTime = 30;
							String refreshTimeStr = ServiceContainer
									.getInstance()
									.getPerferenceService()
									.getValue(
											Constants.PreferenceKey.REALTIME_DATA_MONITOR_REFRESH_TIME);
							if (!"".equals(refreshTimeStr)) {
								refreshTime = Integer.parseInt(refreshTimeStr);
							}

							ServiceContainer
									.getInstance()
									.getSessionService()
									.setSessionValue(
											Constants.SessionKey.REALTIME_DATA_MONITOR_REFRESH_TIME,
											refreshTime);
						} else {
							showLoginActivity();
						}
					}
				} else {
					showLoginActivity();
				}
			} catch (IOTException e) {
				showLoginActivity();
			}

		}
	}
	
	private void showLoginActivity() {
		Intent i = new Intent(Constants.Action.HSINCHUIOT_LOGIN);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
		finish();

	}
	
}
