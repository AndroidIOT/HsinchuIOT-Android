package org.slstudio.hsinchuiot.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.slstudio.hsinchuiot.Constants;
import org.slstudio.hsinchuiot.R;
import org.slstudio.hsinchuiot.V2SiteDetailActivity;
import org.slstudio.hsinchuiot.model.Alarm;
import org.slstudio.hsinchuiot.model.IOTMonitorData;
import org.slstudio.hsinchuiot.model.Site;
import org.slstudio.hsinchuiot.service.ServiceContainer;
import org.slstudio.hsinchuiot.util.IOTLog;
import org.slstudio.hsinchuiot.util.ReportUtil;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class V2UserSiteHomePageFragment extends Fragment {
	
	private Site site;
	private int index;
	
	private TextView tvTitle;
	private TextView tvInfo; 
	private ImageButton logoffBtn;
	private ImageButton alarmBtn;
	private ImageButton detailBtn;
	
	private LinearLayout llCo2Panel;
	private LinearLayout llCo2IconPanel;
	private ImageView ivCo2Icon;
	private TextView tvCo2Caption;
	private TextView tvCo2Value;
	private TextView tvCo2Unit;
	
	private LinearLayout llTemperaturePanel;
	private LinearLayout llTemperatureIconPanel;
	private ImageView ivTemperatureIcon;
	private TextView tvTemperatureCaption;
	private TextView tvTemperatureValue;
	private TextView tvTemperatureUnit;
	
	private LinearLayout llHumidityPanel;
	private LinearLayout llHumidityIconPanel;
	private ImageView ivHumidityIcon;
	private TextView tvHumidityCaption;
	private TextView tvHumidityValue;
	private TextView tvHumidityUnit;
	
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.v2_fragment_user_site_homepage, container, false);
		initViews(view);

		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onResume() {
		super.onResume();

	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.ResultCode.ALARM_LIST) {
			updateAlarm();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	private void initViews(View parentView) {
		tvTitle = (TextView)parentView.findViewById(R.id.user_main_title);
		if(site != null){
			tvTitle.setText(site.getSiteName());
		}
		logoffBtn = (ImageButton)parentView.findViewById(R.id.btn_logoff);
		logoffBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				logoff();
			}
			
		});

		alarmBtn = (ImageButton)parentView.findViewById(R.id.btn_alarm);
		alarmBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				showAlarmList();	
			}
			
		});
		updateAlarm();
		
		
		detailBtn = (ImageButton)parentView.findViewById(R.id.btn_detail);
		detailBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				showSiteDetail();
			}
			
		});
		
		tvInfo = (TextView)parentView.findViewById(R.id.user_main_info);
		
		llCo2Panel = (LinearLayout)parentView.findViewById(R.id.co2_panel);
		llCo2IconPanel = (LinearLayout)parentView.findViewById(R.id.co2_icon_panel);
		ivCo2Icon = (ImageView)parentView.findViewById(R.id.co2_icon);
		tvCo2Caption = (TextView)parentView.findViewById(R.id.co2_caption);
		tvCo2Value = (TextView)parentView.findViewById(R.id.co2_value);
		tvCo2Unit= (TextView)parentView.findViewById(R.id.co2_unit);
		
		tvCo2Value.setTypeface(null, Typeface.BOLD);
		
		
		llTemperaturePanel = (LinearLayout)parentView.findViewById(R.id.temperature_panel);
		llTemperatureIconPanel = (LinearLayout)parentView.findViewById(R.id.temperature_icon_panel);
		ivTemperatureIcon = (ImageView)parentView.findViewById(R.id.temperature_icon);
		tvTemperatureCaption = (TextView)parentView.findViewById(R.id.temperature_caption);
		tvTemperatureValue = (TextView)parentView.findViewById(R.id.temperature_value);
		tvTemperatureUnit= (TextView)parentView.findViewById(R.id.temperature_unit);
		
		tvTemperatureValue.setTypeface(null, Typeface.BOLD);
		
		llHumidityPanel = (LinearLayout)parentView.findViewById(R.id.humidity_panel);
		llHumidityIconPanel = (LinearLayout)parentView.findViewById(R.id.humidity_icon_panel);
		ivHumidityIcon = (ImageView)parentView.findViewById(R.id.humidity_icon);
		tvHumidityCaption = (TextView)parentView.findViewById(R.id.humidity_caption);
		tvHumidityValue = (TextView)parentView.findViewById(R.id.humidity_value);
		tvHumidityUnit= (TextView)parentView.findViewById(R.id.humidity_unit);
		
		tvHumidityValue.setTypeface(null, Typeface.BOLD);
	}

	public boolean updateMonitorData(IOTMonitorData data) {
		Context context = getActivity();
		
		float co2 = data.getCo2();
		
		tvCo2Value.setText(Integer.toString((int)co2));
		
		if(ReportUtil.isCO2Alarm(co2)){
			llCo2Panel.setBackgroundResource(R.drawable.panel_info_worst);
			llCo2IconPanel.setBackgroundResource(R.drawable.status_icon_panel_worst);
			ivCo2Icon.setBackgroundResource(R.drawable.status_icon_alarm);
			tvCo2Caption.setTextAppearance(context, R.style.status_normal_caption_text);
			tvCo2Value.setTextAppearance(context, R.style.status_worst_text);
			tvCo2Unit.setTextAppearance(context, R.style.status_worst_unit_text);
		}else if(ReportUtil.isCO2Warning(co2)){
			llCo2Panel.setBackgroundResource(R.drawable.panel_info_worse);
			llCo2IconPanel.setBackgroundResource(R.drawable.status_icon_panel_worse);
			ivCo2Icon.setBackgroundResource(R.drawable.status_icon_warning);
			tvCo2Caption.setTextAppearance(context, R.style.status_normal_caption_text);
			tvCo2Value.setTextAppearance(context, R.style.status_worse_text);
			tvCo2Unit.setTextAppearance(context, R.style.status_normal_unit_text);
		}else{
			llCo2Panel.setBackgroundResource(R.drawable.panel_info);
			llCo2IconPanel.setBackgroundResource(R.drawable.status_icon_panel);
			ivCo2Icon.setBackgroundResource(R.drawable.status_icon_normal);
			tvCo2Caption.setTextAppearance(context, R.style.status_normal_caption_text);
			tvCo2Value.setTextAppearance(context, R.style.status_normal_text);
			tvCo2Unit.setTextAppearance(context, R.style.status_normal_unit_text);
		}
		tvCo2Value.setTypeface(null, Typeface.BOLD);
		
		float temperature = data.getTemperature();
		tvTemperatureValue.setText(Float.toString(temperature));
		
		if(ReportUtil.isTemperatureAlarm(temperature)){
			ivTemperatureIcon.setBackgroundResource(R.drawable.status_icon_alarm);			
		}else if(ReportUtil.isTemperatureWarning(temperature)){
			ivTemperatureIcon.setBackgroundResource(R.drawable.status_icon_warning);
		}else{
			ivTemperatureIcon.setBackgroundResource(R.drawable.status_icon_normal);
		}
		
		
		if(temperature < 15){
			llTemperaturePanel.setBackgroundResource(R.drawable.panel_info_cold);
			llTemperatureIconPanel.setBackgroundResource(R.drawable.status_icon_panel_cold);
			tvTemperatureCaption.setTextAppearance(context, R.style.status_normal_caption_text);
			tvTemperatureValue.setTextAppearance(context, R.style.status_cold_text);
			tvTemperatureUnit.setTextAppearance(context, R.style.status_normal_unit_text);
		}else if(temperature > 28){
			llTemperaturePanel.setBackgroundResource(R.drawable.panel_info_hot);
			llTemperatureIconPanel.setBackgroundResource(R.drawable.status_icon_panel_hot);
			tvTemperatureCaption.setTextAppearance(context, R.style.status_normal_caption_text);
			tvTemperatureValue.setTextAppearance(context, R.style.status_hot_text);
			tvTemperatureUnit.setTextAppearance(context, R.style.status_normal_unit_text);
		}else{
			llTemperaturePanel.setBackgroundResource(R.drawable.panel_info);
			llTemperatureIconPanel.setBackgroundResource(R.drawable.status_icon_panel);
			tvTemperatureCaption.setTextAppearance(context, R.style.status_normal_caption_text);
			tvTemperatureValue.setTextAppearance(context, R.style.status_cold_text);
			tvTemperatureUnit.setTextAppearance(context, R.style.status_normal_unit_text);
		}
		tvTemperatureValue.setTypeface(null, Typeface.BOLD);
		
		float humidity = data.getHumidity();
		tvHumidityValue.setText(Float.toString(humidity));
		
		if(ReportUtil.isHumidityAlarm(humidity)){
			ivHumidityIcon.setBackgroundResource(R.drawable.status_icon_alarm);			
		}else if(ReportUtil.isHumidityWarning(humidity)){
			ivHumidityIcon.setBackgroundResource(R.drawable.status_icon_warning);
		}else{
			ivHumidityIcon.setBackgroundResource(R.drawable.status_icon_normal);
		}
		
		if(humidity < 40){
			llHumidityPanel.setBackgroundResource(R.drawable.panel_info_dry);
			llHumidityIconPanel.setBackgroundResource(R.drawable.status_icon_panel_dry);
			tvHumidityCaption.setTextAppearance(context, R.style.status_normal_caption_text);
			tvHumidityValue.setTextAppearance(context, R.style.status_dry_text);
			tvHumidityUnit.setTextAppearance(context, R.style.status_normal_unit_text);
		}else if(humidity > 60){
			llHumidityPanel.setBackgroundResource(R.drawable.panel_info_wet);
			llHumidityIconPanel.setBackgroundResource(R.drawable.status_icon_panel_wet);
			tvHumidityCaption.setTextAppearance(context, R.style.status_normal_caption_text);
			tvHumidityValue.setTextAppearance(context, R.style.status_wet_text);
			tvHumidityUnit.setTextAppearance(context, R.style.status_normal_unit_text);
		}else{
			llHumidityPanel.setBackgroundResource(R.drawable.panel_info);
			llHumidityIconPanel.setBackgroundResource(R.drawable.status_icon_panel);
			tvHumidityCaption.setTextAppearance(context, R.style.status_normal_caption_text);
			tvHumidityValue.setTextAppearance(context, R.style.status_normal_text);
			tvHumidityUnit.setTextAppearance(context, R.style.status_normal_unit_text);
		}
		tvHumidityValue.setTypeface(null, Typeface.BOLD);
		
		return true;
	}

	public void updateTimeInfo() {
		tvInfo.setText(sdf.format(new Date()));
	}
	
	public void updateAlarm() {
		alarmBtn.setBackgroundResource(R.drawable.button_alarm);
		List<Alarm> alarms = getSiteAlarms();
		for(Alarm alarm: alarms){
			if(alarm.getAlarmSite().equals(site.getSiteName())){
				alarmBtn.setBackgroundResource(R.drawable.button_alarm_red);
				break;
			}
		}
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

				Alarm alarm = new Alarm(alarmString);
				result.add(alarm);				
			}
		} catch (Exception exp) {
			IOTLog.e("V2AlarmActivity", "Retrieve alarm list failed", exp);
		}

		return result;
	}
	
	private void logoff() {
		ServiceContainer.getInstance().getSessionService().setLoginUser(null);
		ServiceContainer.getInstance().getSessionService().setSessionID(null);
		ServiceContainer.getInstance().getSessionService()
				.setSessionValue(Constants.SessionKey.THRESHOLD_BREACH, null);
		ServiceContainer.getInstance().getSessionService()
				.setSessionValue(Constants.SessionKey.THRESHOLD_WARNING, null);

		Intent loginIntent = new Intent(Constants.Action.HSINCHUIOT_LOGIN);
		startActivity(loginIntent);
		getActivity().finish();
	}
	
	
	private void showSiteDetail() {
		Calendar to = Calendar.getInstance();
		to.set(Calendar.MINUTE, 0);
		to.set(Calendar.SECOND, 0);
		
		Calendar from = Calendar.getInstance();
		from.set(Calendar.MINUTE, 0);
		from.set(Calendar.SECOND, 0);
		from.add(Calendar.HOUR_OF_DAY, -8);
		
		Intent intent = new Intent(Constants.Action.HSINCHUIOT_SITEDETAIL);
		
		intent.putExtra(Constants.ActivityPassValue.SELECTED_SITE,
				site);
		
		intent.putExtra(Constants.ActivityPassValue.CHART_TYPE,
				Constants.ChartSettings.CHART_TYPE_AGGRAGATION);
		
		intent.putExtra(Constants.ActivityPassValue.CHART_RT_DURATION,
				5);
		
		intent.putExtra(Constants.ActivityPassValue.CHART_AGGR_GRANULARITY,
				Constants.ChartSettings.GRANULARITY_HOUR);
		
		
		intent.putExtra(Constants.ActivityPassValue.CHART_AGGR_STARTTIME,
				from.getTime().getTime());
		
		intent.putExtra(Constants.ActivityPassValue.CHART_AGGR_ENDTIME,
				to.getTime().getTime());
		
		startActivity(intent);
	}
	
	private void showAlarmList(){
		Intent intent = new Intent(Constants.Action.HSINCHUIOT_ALARM);
		intent.putExtra(Constants.ActivityPassValue.SELECTED_SITE,
				site);
		startActivityForResult(intent, Constants.ResultCode.ALARM_LIST);
	}
}
