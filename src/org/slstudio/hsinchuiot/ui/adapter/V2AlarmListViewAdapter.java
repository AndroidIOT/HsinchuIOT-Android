package org.slstudio.hsinchuiot.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import org.slstudio.hsinchuiot.Constants;
import org.slstudio.hsinchuiot.R;
import org.slstudio.hsinchuiot.model.Alarm;
import org.slstudio.hsinchuiot.model.Site;
import org.slstudio.hsinchuiot.service.ServiceContainer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class V2AlarmListViewAdapter extends BaseAdapter {

	private LayoutInflater mInflater = null;
	private Context context = null;

	private List<Alarm> allItems = null;

	private Site site = null;

	private List<Alarm> items = null;

	public V2AlarmListViewAdapter(Context c, List<Alarm> allItems, Site site) {
		this.context = c;
		this.allItems = allItems;
		this.site = site;

		this.items = new ArrayList<Alarm>();
		for (Alarm alarm : allItems) {

			if (site == null || site.getSiteName().equals(alarm.getAlarmSite())) {
				items.add(alarm);
			}
		}
	}

	public List<Alarm> getItems() {
		return items;
	}

	public void setItems(List<Alarm> items) {
		this.items = items;
	}

	@Override
	public int getCount() {
		return items == null ? 0 : items.size();
	}

	@Override
	public Object getItem(int position) {
		return items == null ? null : items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int pos = position;

		Alarm alarm = items.get(pos);

		if (mInflater == null) {
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		final Resources resources = context.getResources();

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.v2_listitem_alarm, parent,
					false);
		}

		ImageView alarmType = (ImageView) convertView
				.findViewById(R.id.li_iv_alarmtype);
		if (alarm.isWarning()) {
			alarmType.setBackgroundResource(R.drawable.alarm_type_icon_warning);
		} else if (alarm.isBreached()) {
			alarmType
					.setBackgroundResource(R.drawable.alarm_type_icon_breached);
		}

		TextView alarmTime = (TextView) convertView
				.findViewById(R.id.li_tv_alarmtime);
		alarmTime.setText(alarm == null ? "" : alarm.getAlarmTime());

		TextView alarmSite = (TextView) convertView
				.findViewById(R.id.li_tv_alarmsite);
		alarmSite.setText(alarm == null ? "" : alarm.getAlarmSite());

		TextView alarmValueType = (TextView) convertView
				.findViewById(R.id.li_tv_alarmvaluetype);
		alarmValueType.setText(alarm == null ? "" : alarm.getAlarmValueType());

		TextView alarmValue = (TextView) convertView
				.findViewById(R.id.li_tv_alarmvalue);
		alarmValue.setText(alarm == null ? "" : alarm.getAlarmValue());

		ImageButton deleteBtn = (ImageButton) convertView
				.findViewById(R.id.li_ib_delete_btn);
		deleteBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(context)
						.setTitle(
								resources
										.getString(R.string.dlg_title_systemprompt))
						.setMessage(
								resources
										.getString(R.string.dlg_caption_deletealarm))
						.setPositiveButton(resources.getString(R.string.yes),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Alarm removedAlarm = items
												.remove(pos);
										allItems.remove(removedAlarm);
										String alarmString = "";
										for (int i = 0 ;i < allItems.size(); i++) {
											Alarm alarm = allItems.get(i);
											alarmString += alarm.toString();
											if(i<allItems.size()-1){
												alarmString += "|";
											}
										}
										ServiceContainer
												.getInstance()
												.getPerferenceService()
												.setValue(
														Constants.PreferenceKey.ALARM_LIST,
														alarmString);
										V2AlarmListViewAdapter.this.notifyDataSetChanged();
									}

								})
						.setNegativeButton(resources.getString(R.string.no),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
									}

								}).create().show();
			}

		});
		return convertView;
	}
}
