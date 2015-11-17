package org.slstudio.hsinchuiot.ui.adapter;

import java.util.List;

import org.slstudio.hsinchuiot.R;
import org.slstudio.hsinchuiot.model.IOTMonitorData;
import org.slstudio.hsinchuiot.model.IOTMonitorThreshold;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AverageValueListViewAdapter extends BaseAdapter{

	private List<IOTMonitorData> items = null;
	private IOTMonitorThreshold warningThreshold = null;
	private IOTMonitorThreshold breachThreshold = null;
	
	private LayoutInflater mInflater = null;
	private Context context = null;
	
	
	public AverageValueListViewAdapter(Context c, List<IOTMonitorData> items, IOTMonitorThreshold warningThreshold, IOTMonitorThreshold breachThreshold){
		this.context = c;
		this.items = items;
		this.warningThreshold = warningThreshold;
		this.breachThreshold = breachThreshold;
		mInflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	
	public List<IOTMonitorData> getItems() {
		return items;
	}


	public void setItems(List<IOTMonitorData> items) {
		this.items = items;
	}


	public IOTMonitorThreshold getWarningThreshold() {
		return warningThreshold;
	}


	public void setWarningThreshold(IOTMonitorThreshold warningThreshold) {
		this.warningThreshold = warningThreshold;
	}


	public IOTMonitorThreshold getBreachThreshold() {
		return breachThreshold;
	}


	public void setBreachThreshold(IOTMonitorThreshold breachThreshold) {
		this.breachThreshold = breachThreshold;
	}


	@Override
	public int getCount() {
		return items == null? 0: items.size();
	}

	@Override
	public Object getItem(int position) {
		return items == null? null: items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		IOTMonitorData data = items.get(position);
		final Resources resources = context.getResources();
		
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.listitem_averagevalue, parent, false);								
		}
		
		TextView tv1 = (TextView)convertView.findViewById(R.id.li2_tv_sitename);
		tv1.setText(data == null? null: "");
		
		TextView tv2 = (TextView)convertView.findViewById(R.id.li2_tv_co2value);
		tv2.setText(data == null? null: Float.toString(data.getCo2()));
		
		TextView tv3 = (TextView)convertView.findViewById(R.id.li2_tv_temperaturevalue);
		tv3.setText(data == null? null: Float.toString(data.getTemperature()));
		
		TextView tv4 = (TextView)convertView.findViewById(R.id.li2_tv_humidityvalue);
		tv4.setText(data == null? null: Float.toString(data.getHumidity()));
		
		ImageView iv1 = (ImageView)convertView.findViewById(R.id.li2_icon_co2);
		if(data.isCO2Breach(breachThreshold)){
			iv1.setBackgroundResource(R.drawable.breach);
			iv1.setVisibility(View.VISIBLE);
		}else if(data.isCO2Breach(warningThreshold)){
			iv1.setBackgroundResource(R.drawable.warning);
			iv1.setVisibility(View.VISIBLE);
		}else{
			iv1.setVisibility(View.INVISIBLE);
		}
		
		ImageView iv2 = (ImageView)convertView.findViewById(R.id.li2_icon_temperature);
		if(data.isTemperatureBreach(breachThreshold)){
			iv2.setBackgroundResource(R.drawable.breach);
			iv2.setVisibility(View.VISIBLE);
		}else if(data.isTemperatureBreach(warningThreshold)){
			iv2.setBackgroundResource(R.drawable.warning);
			iv2.setVisibility(View.VISIBLE);
		}else{
			iv2.setVisibility(View.INVISIBLE);
		}
		
		ImageView iv3 = (ImageView)convertView.findViewById(R.id.li2_icon_humidity);
		if(data.isHumidityBreach(breachThreshold)){
			iv3.setBackgroundResource(R.drawable.breach);
			iv3.setVisibility(View.VISIBLE);
		}else if(data.isHumidityBreach(warningThreshold)){
			iv3.setBackgroundResource(R.drawable.warning);
			iv3.setVisibility(View.VISIBLE);
		}else{
			iv3.setVisibility(View.INVISIBLE);
		}
		
		return convertView;
	}

}
