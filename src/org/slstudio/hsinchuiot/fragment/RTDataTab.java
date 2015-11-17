package org.slstudio.hsinchuiot.fragment;

import java.util.ArrayList;
import java.util.List;

import org.slstudio.hsinchuiot.MainActivity;
import org.slstudio.hsinchuiot.R;
import org.slstudio.hsinchuiot.SiteDetailActivity;
import org.slstudio.hsinchuiot.model.IOTMonitorData;
import org.slstudio.hsinchuiot.model.IOTMonitorThreshold;
import org.slstudio.hsinchuiot.ui.adapter.RealTimeDataListViewAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class RTDataTab extends Fragment {
	private ListView rtDataListView;
	private RealTimeDataListViewAdapter rtDataListViewAdapter;
	
	
	public RealTimeDataListViewAdapter getRtDataListViewAdapter() {
		return rtDataListViewAdapter;
	}



	public void setRtDataListViewAdapter(
			RealTimeDataListViewAdapter rtDataListViewAdapter) {
		this.rtDataListViewAdapter = rtDataListViewAdapter;
	}



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		createRTDataListViewAdapter();
	}



	private void createRTDataListViewAdapter() {
		List<IOTMonitorData> items = new ArrayList<IOTMonitorData>();
		/*
		items.add(new IOTMonitorData("国立台湾大学医学院附设医院竹东分院", 1623, 29, 57));
		items.add(new IOTMonitorData("竹东镇公所", 758, 26, 33));
		items.add(new IOTMonitorData("竹北市立图书馆", 758, 28, 33));
		items.add(new IOTMonitorData("新竹县妇幼馆", 758, 33, 33));
		items.add(new IOTMonitorData("国立台湾大学医学院附设医院竹东分院", 1623, 29, 57));
		items.add(new IOTMonitorData("竹东镇公所", 758, 26, 33));
		items.add(new IOTMonitorData("竹北市立图书馆", 758, 28, 33));
		items.add(new IOTMonitorData("新竹县妇幼馆", 758, 33, 33));
		*/
		IOTMonitorThreshold warningThreshold = new IOTMonitorThreshold(Integer.MIN_VALUE, 800, 16, 27, 0, 100);
		IOTMonitorThreshold breachThreshold = new IOTMonitorThreshold(Integer.MIN_VALUE, 1000, 15, 28, 0, 100);
		
		rtDataListViewAdapter = new RealTimeDataListViewAdapter(this.getActivity(), items, warningThreshold, breachThreshold);
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		
		View view = inflater.inflate(R.layout.superuser_tab_rtdata, container, false);
		rtDataListView = (ListView) view.findViewById(R.id.rtdata_list_view);
		rtDataListView.setVisibility(View.VISIBLE);
		rtDataListView.setAdapter(rtDataListViewAdapter);
		rtDataListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Intent intent = new Intent();
				intent.putExtra(MainActivity.SELECTED_SITE, (IOTMonitorData)rtDataListViewAdapter.getItem(position));
				intent.setClass(getActivity(), SiteDetailActivity.class);
				startActivity(intent);
			}
			
		});
		return view;

	}

}
