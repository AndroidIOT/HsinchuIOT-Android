package org.slstudio.hsinchuiot.ui.adapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slstudio.hsinchuiot.Constants;
import org.slstudio.hsinchuiot.R;
import org.slstudio.hsinchuiot.R.color;
import org.slstudio.hsinchuiot.model.IOTMonitorData;
import org.slstudio.hsinchuiot.model.IOTMonitorThreshold;
import org.slstudio.hsinchuiot.model.Site;
import org.slstudio.hsinchuiot.service.ServiceContainer;
import org.slstudio.hsinchuiot.util.ReportUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class V2SiteListViewAdapter extends BaseAdapter {

	public static final int SORT_BY_STATUS = 0;
	public static final int SORT_BY_CO2 = 1;
	public static final int SORT_BY_TEMPERATURE = 2;
	public static final int SORT_BY_HUMIDITY = 3;
	public static final int SORT_BY_LOCATION = 4;

	private List<Site> items = null;

	private LayoutInflater mInflater = null;
	private Context context = null;
	
	private int currentSortBy = SORT_BY_LOCATION;
	
	private boolean[] sortDesc = {true, true, true, true, true};

	public V2SiteListViewAdapter(Context c, List<Site> items) {
		this.context = c;
		this.items = items;
	}

	public List<Site> getItems() {
		return items;
	}

	public void setItems(List<Site> items) {
		this.items = items;
		sortBy(currentSortBy, sortDesc[currentSortBy]);
	}

	public boolean isSortByDesc(int sortBy){
		return sortDesc[sortBy];
	}
	
	public void setSortByDesc(int sortBy, boolean desc){
		for (int i = SORT_BY_STATUS; i <SORT_BY_LOCATION +1 ; i++){
			sortDesc[i] = true;
		}
		sortDesc[sortBy] = desc;
	}
	
	public void sortBy(int sortBy, boolean desc) {
		currentSortBy = sortBy;
		setSortByDesc(sortBy, desc);
		switch (sortBy) {
		case SORT_BY_STATUS:
			Collections.sort(items, new StatusComparator());
			break;
		case SORT_BY_CO2:
			Collections.sort(items, new CO2Comparator());
			break;
		case SORT_BY_TEMPERATURE:
			Collections.sort(items, new TemperatureComparator());
			break;
		case SORT_BY_HUMIDITY:
			Collections.sort(items, new HumidityComparator());
			break;
		case SORT_BY_LOCATION:
			Collections.sort(items, new LocationComparator());
			break;
		default:
			break;
		}
		
	}

	public Site getSiteByDeviceID(String deviceID) {
		for (Site site : items) {
			if (site.getDevice() != null
					&& site.getDevice().getDeviceID().equals(deviceID)) {
				return site;
			}
		}
		return null;
	}

	@Override
	public int getCount() {
		return items == null ? 1 : items.size() + 1;
	}

	@Override
	public Object getItem(int position) {
		if (position == 0)
			return null;
		return items == null ? null : items.get(position - 1);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int pos = position;
		if (mInflater == null) {
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		final Resources resources = context.getResources();

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.v2_listitem_site, parent,
					false);
		}

		TextView statusHeader = (TextView) convertView
				.findViewById(R.id.li_tv_status_header);
		TextView locationHeader = (TextView) convertView
				.findViewById(R.id.li_tv_sitename_header);
		TextView co2Header = (TextView) convertView
				.findViewById(R.id.li_tv_co2value_header);
		TextView temperatureHeader = (TextView) convertView
				.findViewById(R.id.li_tv_temperaturevalue_header);
		TextView humidityHeader = (TextView) convertView
				.findViewById(R.id.li_tv_humidityvalue_header);

		ImageView leftStatusImg = (ImageView) convertView
				.findViewById(R.id.li_iv_co2_status);
		ImageView rightStatusImg = (ImageView) convertView
				.findViewById(R.id.li_iv_temperature_status);
		TextView locationTV = (TextView) convertView
				.findViewById(R.id.li_tv_sitename);
		TextView co2TV = (TextView) convertView
				.findViewById(R.id.li_tv_co2value);
		TextView temperatureTV = (TextView) convertView
				.findViewById(R.id.li_tv_temperaturevalue);
		TextView humidityTV = (TextView) convertView
				.findViewById(R.id.li_tv_humidityvalue);

		if (position == 0) {
			statusHeader.setVisibility(View.VISIBLE);
			locationHeader.setVisibility(View.VISIBLE);
			co2Header.setVisibility(View.VISIBLE);
			temperatureHeader.setVisibility(View.VISIBLE);
			humidityHeader.setVisibility(View.VISIBLE);

			leftStatusImg.setVisibility(View.GONE);
			rightStatusImg.setVisibility(View.GONE);
			locationTV.setVisibility(View.GONE);
			co2TV.setVisibility(View.GONE);
			temperatureTV.setVisibility(View.GONE);
			humidityTV.setVisibility(View.GONE);

		} else {
			statusHeader.setVisibility(View.GONE);
			locationHeader.setVisibility(View.GONE);
			co2Header.setVisibility(View.GONE);
			temperatureHeader.setVisibility(View.GONE);
			humidityHeader.setVisibility(View.GONE);

			leftStatusImg.setVisibility(View.VISIBLE);
			rightStatusImg.setVisibility(View.VISIBLE);
			locationTV.setVisibility(View.VISIBLE);
			co2TV.setVisibility(View.VISIBLE);
			temperatureTV.setVisibility(View.VISIBLE);
			humidityTV.setVisibility(View.VISIBLE);

			Site site = items.get(position - 1);
			IOTMonitorData data = site.getMonitorData();

			locationTV.setText(data == null ? null : site.getSiteName());

			int alarm = resources.getColor(R.color.status_alarm);
			int warning = resources.getColor(R.color.status_warning);
			int normal = resources.getColor(R.color.black);

			if (data == null || data.getCo2() == 0f) {
				leftStatusImg
						.setBackgroundResource(R.drawable.status_icon_co2_missing);
				co2TV.setText("");
			} else if (ReportUtil.isCO2Alarm(data.getCo2())) {
				co2TV.setTextColor(alarm);
				co2TV.setText(Float.toString(data.getCo2()) + " ppm");
				leftStatusImg
						.setBackgroundResource(R.drawable.status_icon_co2_alarm);
			} else if (ReportUtil.isCO2Warning(data.getCo2())) {
				co2TV.setTextColor(warning);
				co2TV.setText(Float.toString(data.getCo2()) + " ppm");
				leftStatusImg
						.setBackgroundResource(R.drawable.status_icon_co2_warning);
			} else {
				co2TV.setTextColor(normal);
				co2TV.setText(Float.toString(data.getCo2()) + " ppm");
				leftStatusImg
						.setBackgroundResource(R.drawable.status_icon_co2_normal);
			}
			if (data == null || data.getTemperature() == 0f) {
				rightStatusImg
						.setBackgroundResource(R.drawable.status_icon_temperature_missing);
				temperatureTV.setText("");
			} else if (ReportUtil.isTemperatureAlarm(data.getTemperature())) {
				temperatureTV.setTextColor(alarm);
				temperatureTV.setText(Float.toString(data.getTemperature())
						+ " ℃");
				rightStatusImg
						.setBackgroundResource(R.drawable.status_icon_temperature_alarm);
			} else if (ReportUtil.isTemperatureWarning(data.getTemperature())) {
				temperatureTV.setTextColor(warning);
				temperatureTV.setText(Float.toString(data.getTemperature())
						+ " ℃");
				rightStatusImg
						.setBackgroundResource(R.drawable.status_icon_temperature_warning);
			} else {
				temperatureTV.setTextColor(normal);
				temperatureTV.setText(Float.toString(data.getTemperature())
						+ " ℃");
				rightStatusImg
						.setBackgroundResource(R.drawable.status_icon_temperature_normal);
			}
			if (data == null || data.getHumidity() == 0f) {
				humidityTV.setText("");
			} else if (ReportUtil.isHumidityAlarm(data.getHumidity())) {
				humidityTV.setTextColor(alarm);
				humidityTV.setText(Float.toString(data.getHumidity()) + " %");
			} else if (ReportUtil.isHumidityWarning(data.getHumidity())) {
				humidityTV.setTextColor(warning);
				humidityTV.setText(Float.toString(data.getHumidity()) + " %");
			} else {
				humidityTV.setTextColor(normal);
				humidityTV.setText(Float.toString(data.getHumidity()) + " %");
			}
		}

		return convertView;
	}

	
	private class StatusComparator implements Comparator<Site> {

		@Override
		public int compare(Site s1, Site s2) {
			int s1Status = getSiteStatus(s1);
			int s2Status = getSiteStatus(s2);
			if (sortDesc[SORT_BY_STATUS]) {
				return s2Status - s1Status;
			} else {
				return s1Status - s2Status;
			}
		}

		private int getSiteStatus(Site site) {
			float co2 = site.getMonitorData().getCo2();
			float temperature = site.getMonitorData().getTemperature();

			int co2Status = 0;
			if (co2 != 0f) {
				if (ReportUtil.isCO2Alarm(co2)) {
					co2Status = 1;
				} else if (ReportUtil.isCO2Warning(co2)) {
					co2Status = 2;
				} else {
					co2Status = 5;
				}
			}

			int temperatureStatus = 0;
			if (temperature != 0f) {
				if (ReportUtil.isTemperatureAlarm(temperature)) {
					temperatureStatus = 3;
				} else if (ReportUtil.isTemperatureWarning(temperature)) {
					temperatureStatus = 4;
				} else {
					temperatureStatus = 6;
				}
			}

			return co2Status * 10 + temperatureStatus;
		}

	}
	
	private class CO2Comparator implements Comparator<Site> {

		@Override
		public int compare(Site s1, Site s2) {
			float s1CO2 = s1.getMonitorData().getCo2();
			float s2CO2 = s2.getMonitorData().getCo2();
					
			if (sortDesc[SORT_BY_CO2]) {
				return Float.compare(s2CO2, s1CO2);
			} else {
				return Float.compare(s1CO2, s2CO2);
			}
		}
	}
	
	private class TemperatureComparator implements Comparator<Site> {

		@Override
		public int compare(Site s1, Site s2) {
			float s1Temperature = s1.getMonitorData().getTemperature();
			float s2Temperature = s2.getMonitorData().getTemperature();
					
			if (sortDesc[SORT_BY_TEMPERATURE]) {
				return Float.compare(s2Temperature, s1Temperature);
			} else {
				return Float.compare(s1Temperature, s2Temperature);
			}
		}
	}
	
	private class HumidityComparator implements Comparator<Site> {

		@Override
		public int compare(Site s1, Site s2) {
			float s1Humidity = s1.getMonitorData().getHumidity();
			float s2Humidity = s2.getMonitorData().getHumidity();
					
			if (sortDesc[SORT_BY_HUMIDITY]) {
				return Float.compare(s2Humidity, s1Humidity);
			} else {
				return Float.compare(s1Humidity, s2Humidity);
			}
		}
	}
	
	private class LocationComparator implements Comparator<Site> {

		@Override
		public int compare(Site s1, Site s2) {
			if (sortDesc[SORT_BY_LOCATION]) {
				return s2.getSiteName().compareTo(s1.getSiteName());
			} else {
				return s1.getSiteName().compareTo(s2.getSiteName());
			}
		}
	}
}
