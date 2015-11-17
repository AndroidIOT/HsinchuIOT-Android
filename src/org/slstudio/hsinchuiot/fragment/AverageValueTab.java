package org.slstudio.hsinchuiot.fragment;

import java.util.ArrayList;
import java.util.List;

import org.slstudio.hsinchuiot.MainActivity;
import org.slstudio.hsinchuiot.R;
import org.slstudio.hsinchuiot.SiteDetailActivity;
import org.slstudio.hsinchuiot.model.Device;
import org.slstudio.hsinchuiot.model.IOTMonitorData;
import org.slstudio.hsinchuiot.model.IOTMonitorThreshold;
import org.slstudio.hsinchuiot.ui.adapter.AverageValueListViewAdapter;
import org.slstudio.hsinchuiot.util.IOTLog;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class AverageValueTab extends Fragment {
	private ListView averageValueListView;

	private AverageValueListViewAdapter averageValueListViewAdatper;

	private List<Device> deviceList = new ArrayList<Device>();
	
	private static final int STATUS_CHANGE = 0;  
	
	private Handler handler = null;
	

	public AverageValueTab() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		averageValueListViewAdatper = new AverageValueListViewAdapter(
				this.getActivity(),
				new ArrayList<IOTMonitorData>(),
				new IOTMonitorThreshold(Integer.MIN_VALUE, 800, 16, 27, 0, 100),
				new IOTMonitorThreshold(Integer.MIN_VALUE, 1000, 15, 28, 0, 100));
		
		handler = new Handler() {  
             public void handleMessage(Message msg) {  
                     switch (msg.what) {  
                     case STATUS_CHANGE:  
                             // 处理UI更新等操作  
                             updateUI();  
                             break;  
                     }  
             }

			
     };        
	}

	private void updateUI() {
		List<IOTMonitorData> items = new ArrayList<IOTMonitorData>();

		for (Device d : deviceList) {
			items.add(new IOTMonitorData(1623, 29, 57));
		}

		IOTMonitorThreshold warningThreshold = new IOTMonitorThreshold(
				Integer.MIN_VALUE, 800, 16, 27, 0, 100);
		IOTMonitorThreshold breachThreshold = new IOTMonitorThreshold(
				Integer.MIN_VALUE, 1000, 15, 28, 0, 100);

		averageValueListViewAdatper.setItems(items);
		averageValueListViewAdatper.setWarningThreshold(warningThreshold);
		averageValueListViewAdatper.setBreachThreshold(breachThreshold);
		averageValueListViewAdatper.notifyDataSetChanged();
		IOTLog.d("update ====", "notify data changed-----------");
	};  
	
	public void updateDeviceList(List<Device> deviceList) {
		this.deviceList = deviceList;

		
		new Handler(getActivity().getMainLooper()).post(new Runnable(){

			@Override
			public void run() {
				updateUI();
			}
			
		});
		
		/*
		Message msg = new Message();  
        msg.what = STATUS_CHANGE;  
        handler.sendMessage(msg);
		*/

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.superuser_tab_averagevalue,
				container, false);
		averageValueListView = (ListView) view
				.findViewById(R.id.averagevalue_list_view);
		averageValueListView.setVisibility(View.VISIBLE);

		averageValueListView.setAdapter(averageValueListViewAdatper);
		averageValueListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Intent intent = new Intent();
				intent.putExtra(MainActivity.SELECTED_SITE,
						(IOTMonitorData) averageValueListViewAdatper
								.getItem(position));
				intent.setClass(getActivity(), SiteDetailActivity.class);
				startActivity(intent);
			}

		});
		return view;
	}
}
