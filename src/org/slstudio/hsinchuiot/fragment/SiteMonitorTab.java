package org.slstudio.hsinchuiot.fragment;

import org.slstudio.hsinchuiot.R;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SiteMonitorTab extends Fragment {
	private TextView tvCO2Value;
	private TextView tvTemperatureValue;
	private TextView tvHumidityValue;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.site_tab_monitor, container, false);
		
		Typeface typeFace =Typeface.createFromAsset(getActivity().getAssets(),"fonts/Clubland.ttf");
		
		tvCO2Value = (TextView)view.findViewById(R.id.site_tv_co2value);
		tvCO2Value.setTypeface(typeFace);
		
		tvTemperatureValue = (TextView)view.findViewById(R.id.site_tv_temperaturevalue);
		tvTemperatureValue.setTypeface(typeFace);
		
		tvHumidityValue = (TextView)view.findViewById(R.id.site_tv_humidityvalue);
		tvHumidityValue.setTypeface(typeFace);
		
		return view;
	}
}
