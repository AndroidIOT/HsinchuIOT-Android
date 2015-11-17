package org.slstudio.hsinchuiot.fragment;

import java.util.ArrayList;
import java.util.Calendar;

import org.slstudio.hsinchuiot.Constants;
import org.slstudio.hsinchuiot.R;
import org.slstudio.hsinchuiot.V2SiteDetailActivity;
import org.slstudio.hsinchuiot.model.Site;
import org.slstudio.hsinchuiot.ui.adapter.V2SiteListViewAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public abstract class V2ListViewTab extends Fragment {
	protected ListView listView;
	protected V2SiteListViewAdapter lvAdapter;

	private static final int STATUS_CHANGE = 0;

	protected Handler handler = null;

	public V2ListViewTab() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		lvAdapter = new V2SiteListViewAdapter(this.getActivity(),
				new ArrayList<Site>());
	}
	
	public void updateListView(){
		if (handler == null){
			handler = new Handler();
		}
		handler.post(new Runnable() {

			@Override
			public void run() {
				// use new api
				getListViewData();
			}

		});
		
	}
	
	public void sortListView(int sortBy){
		lvAdapter.sortBy(sortBy, !lvAdapter.isSortByDesc(sortBy));
		lvAdapter.notifyDataSetChanged();
	}
	

	protected abstract void getListViewData();
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.v2_superuser_listview_tab,
				container, false);

		listView = (ListView) view
				.findViewById(R.id.superuser_list_view);
		listView.setVisibility(View.VISIBLE);

		listView.setAdapter(lvAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (position == 0)
					return;

				Calendar to = Calendar.getInstance();
				to.set(Calendar.MINUTE, 0);
				to.set(Calendar.SECOND, 0);
				
				Calendar from = Calendar.getInstance();
				from.set(Calendar.MINUTE, 0);
				from.set(Calendar.SECOND, 0);
				from.add(Calendar.HOUR_OF_DAY, -8);
				
				Intent intent = new Intent();
				intent.putExtra(Constants.ActivityPassValue.SELECTED_SITE,
						(Site) lvAdapter.getItem(position));
				
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
				
				intent.setClass(getActivity(), V2SiteDetailActivity.class);
				startActivity(intent);
			}

		});
		
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	
}
