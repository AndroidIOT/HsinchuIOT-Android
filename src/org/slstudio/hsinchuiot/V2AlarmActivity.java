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
import org.slstudio.hsinchuiot.service.ServiceContainer;
import org.slstudio.hsinchuiot.ui.adapter.V2AlarmListViewAdapter;
import org.slstudio.hsinchuiot.util.IOTLog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
				
				
				
				Intent intent = new Intent(Constants.Action.HSINCHUIOT_SITEDETAIL);
				
				intent.putExtra(Constants.ActivityPassValue.SELECTED_SITE,
						fakeSite);
				
				intent.putExtra(Constants.ActivityPassValue.CHART_TYPE,
						Constants.ChartSettings.CHART_TYPE_AGGRAGATION);
				
				intent.putExtra(Constants.ActivityPassValue.CHART_RT_DURATION,
						10);
				
				intent.putExtra(Constants.ActivityPassValue.CHART_AGGR_GRANULARITY,
						Constants.ChartSettings.GRANULARITY_MINUTE);
				
				
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
				String deviceID = st2.nextToken();
				String siteName = st2.nextToken();
				String alarmValueType = st2.nextToken();
				String alarmValue = st2.nextToken();
				String alarmType = st2.nextToken();

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
			String deviceID = st2.nextToken();
			String siteName = st2.nextToken();
			String alarmValueType = st2.nextToken();
			String alarmValue = st2.nextToken();
			String alarmType = st2.nextToken();

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

	
}
