package org.slstudio.hsinchuiot;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slstudio.hsinchuiot.service.IOTException;

import com.amo.demo.arrWheelview.StrArrayWheelView;
import com.amo.demo.arrWheelview.StrArrayWheelView.OnWheelChangedListener;
import com.amo.demo.wheelview.NumericWheelAdapter;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UserChartSettingsActivity extends BaseActivity {

	public static final String[] CHART_TYPE = new String[] { "即時資料", "歷史統計" };
	public static final String[] GRANULARITY_TYPE = new String[] { "1小時",
		"8小時", "日", "周", "月" };

	public static final int TIME_PERIOD_TODAY = 0;
	public static final int TIME_PERIOD_YESTERDAY = 1;
	public static final int TIME_PERIOD_THIS_WEEK = 2;
	public static final int TIME_PERIOD_LAST_WEEK = 3;
	public static final int TIME_PERIOD_THIS_MONTH = 4;
	public static final int TIME_PERIOD_LAST_MONTH = 5;
	public static final int TIME_PERIOD_LAST_3DAYS = 6;
	public static final int TIME_PERIOD_LAST_5DAYS = 7;
	public static final int TIME_PERIOD_LAST_7DAYS = 8;
	public static final int TIME_PERIOD_LAST_1WEEK = 9;
	public static final int TIME_PERIOD_LAST_2WEEKS = 10;
	public static final int TIME_PERIOD_LAST_3WEEKS = 11;
	public static final int TIME_PERIOD_LAST_1MONTH = 12;
	public static final int TIME_PERIOD_LAST_2MONTHS = 13;
	public static final int TIME_PERIOD_LAST_3MONTHS = 14;
	public static final int TIME_PERIOD_1_HOUR = 15;
	public static final int TIME_PERIOD_4_HOURS = 16;
	public static final int TIME_PERIOD_8_HOURS = 17;
	
	

	private static int START_YEAR = 1990, END_YEAR = 2100;

	private View chartTypeLayout;
	private TextView tvChartType;

	private EditText etDuration;
	
	private LinearLayout rtChartParameterLayout;
	private LinearLayout aggrChartParameterLayout;

	private View granularityTypeLayout;
	private TextView tvGranularityType;

	private View timePeriodLayout;

	private View timePeriodStartTimeLayout;
	private TextView tvTimePeriodStartTime;
	private View timePeriodEndTimeLayout;
	private TextView tvTimePeriodEndTime;

	
	private int currentChartType = Constants.ChartSettings.CHART_TYPE_REALTIME;

	private int currentTimeDuration = 5;

	private int currentGranularityType = Constants.ChartSettings.GRANULARITY_HOUR;
	
	
	private static final String[] TIME_PERIOD_QUICKLINK = new String[] { "今天",
			"昨天", "本周", "上周", "本月", "上月", "過去3天", "過去5天", "過去7天", "過去1周",
			"過去2周", "過去3周", "過去1月", "過去2月", "過去3月" };
	private int currentTimePeriodQuickLink = -1;

	private Date startTime;
	private Date endTime;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getPassedData(this.getIntent());
		
		setContentView(R.layout.activity_user_chart_settings);
		
		initViews();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_user_chart_settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();

			break;
		case R.id.menu_ok:
			if(currentChartType == Constants.ChartSettings.CHART_TYPE_REALTIME){
				String durationStr = etDuration.getText().toString();
				if(durationStr.equals("")){
					setException(new IOTException(-1, getString(R.string.error_message_chart_settings_require_duration)));
					showDialog(DIALOG_ERROR);
					return true;
				}
				int duration=0;
				try{
					duration = Integer.parseInt(durationStr);
				}catch(Exception exp){
					setException(new IOTException(-1, getString(R.string.error_message_chart_settings_invalid_duration)));
					showDialog(DIALOG_ERROR);
					return true;
				}
				if(duration<=0){
					setException(new IOTException(-1, getString(R.string.error_message_chart_settings_invalid_duration)));
					showDialog(DIALOG_ERROR);
					return true;
				}
				Intent result = new Intent();
				result.putExtra(Constants.ActivityPassValue.CHART_TYPE, currentChartType);
				result.putExtra(Constants.ActivityPassValue.CHART_RT_DURATION, duration);
				setResult(this.RESULT_OK, result);
				
			}else if(currentChartType == Constants.ChartSettings.CHART_TYPE_AGGRAGATION){
				if(startTime == null){
					setException(new IOTException(-1, getString(R.string.error_message_chart_settings_require_starttime)));
					showDialog(DIALOG_ERROR);
					return true;
				}
				if(endTime == null){
					setException(new IOTException(-1, getString(R.string.error_message_chart_settings_require_endtime)));
					showDialog(DIALOG_ERROR);
					return true;
				}
				
				if(startTime.after(endTime)){
					setException(new IOTException(-1, getString(R.string.error_message_chart_settings_invalid_starttime)));
					showDialog(DIALOG_ERROR);
					return true;
				}
				
				Intent result = new Intent();
				result.putExtra(Constants.ActivityPassValue.CHART_TYPE, currentChartType);
				result.putExtra(Constants.ActivityPassValue.CHART_AGGR_STARTTIME, startTime.getTime());
				result.putExtra(Constants.ActivityPassValue.CHART_AGGR_ENDTIME, endTime.getTime());
				result.putExtra(Constants.ActivityPassValue.CHART_AGGR_GRANULARITY, currentGranularityType);
				setResult(this.RESULT_OK, result);
			}
			
			finish();
			break;

		}
		return true;
	}

	@Override
	protected void setupActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.chart_settings);
		super.setupActionBar();
	}
	
	private void getPassedData(Intent data){
		currentChartType = data.getIntExtra(Constants.ActivityPassValue.CHART_TYPE, Constants.ChartSettings.CHART_TYPE_REALTIME);
		if(currentChartType == Constants.ChartSettings.CHART_TYPE_REALTIME){
			currentTimeDuration = data.getIntExtra(Constants.ActivityPassValue.CHART_RT_DURATION, 5);
		}else if(currentChartType == Constants.ChartSettings.CHART_TYPE_AGGRAGATION){
			currentGranularityType = data.getIntExtra(Constants.ActivityPassValue.CHART_AGGR_GRANULARITY, Constants.ChartSettings.GRANULARITY_HOUR);
			long startTimeLong = data.getLongExtra(Constants.ActivityPassValue.CHART_AGGR_STARTTIME, 0);
			if(startTimeLong != 0){
				startTime = new Date();
				startTime.setTime(startTimeLong);
			}
			long endTimeLong = data.getLongExtra(Constants.ActivityPassValue.CHART_AGGR_ENDTIME, 0);
			if(endTimeLong != 0){
				endTime = new Date();
				endTime.setTime(endTimeLong);
			}
		}
	}

	private void initViews() {

		tvChartType = (TextView) findViewById(R.id.tv_chartsettings_type);
		chartTypeLayout = findViewById(R.id.layout_chartsettings_type);
		etDuration = (EditText) findViewById(R.id.et_chartsettings_rtchart_duration);
		rtChartParameterLayout = (LinearLayout) findViewById(R.id.layout_chartsettings_rtchart_parameter);
		aggrChartParameterLayout = (LinearLayout) findViewById(R.id.layout_chartsettings_aggrchart_parameter);
		tvGranularityType = (TextView) findViewById(R.id.tv_chartsettings_aggrchart_granularity);
		granularityTypeLayout = findViewById(R.id.layout_chartsettings_aggrchart_granularity);
		timePeriodLayout = findViewById(R.id.layout_chartsettings_aggrchart_timeperiod);
		tvTimePeriodStartTime = (TextView) findViewById(R.id.tv_chartsettings_aggrchart_starttime);
		tvTimePeriodEndTime = (TextView) findViewById(R.id.tv_chartsettings_aggrchart_endtime);
		timePeriodStartTimeLayout = findViewById(R.id.layout_chartsettings_aggrchart_starttime);
		timePeriodEndTimeLayout = findViewById(R.id.layout_chartsettings_aggrchart_endtime);

		tvChartType.setText(CHART_TYPE[currentChartType]);
		setupChartTypeDlg(this);
		
		etDuration.setText(Integer.toString(currentTimeDuration));

		tvGranularityType.setText(GRANULARITY_TYPE[currentGranularityType]);
		setupChartGranularityTypeDlg(this);

		setupTimePeriodDlg(this);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		if(startTime != null){
			tvTimePeriodStartTime.setText(sdf.format(startTime));
		}
		
		setupStartTimePickerDlg(this);
		
		if(endTime != null){
			tvTimePeriodEndTime.setText(sdf.format(endTime));
		}

		setupEndTimePickerDlg(this);
		

		if (currentChartType == Constants.ChartSettings.CHART_TYPE_REALTIME) {
			rtChartParameterLayout.setVisibility(View.VISIBLE);
			aggrChartParameterLayout.setVisibility(View.INVISIBLE);
		} else if (currentChartType == Constants.ChartSettings.CHART_TYPE_AGGRAGATION){
			rtChartParameterLayout.setVisibility(View.INVISIBLE);
			aggrChartParameterLayout.setVisibility(View.VISIBLE);
		}
		
		setupActionBar();
	}
	
	private void setupChartTypeDlg(final Context context) {
		chartTypeLayout.setOnClickListener(new View.OnClickListener() {
			private int tempSelect = 0;

			@Override
			public void onClick(View v) {

				Dialog dialog = new AlertDialog.Builder(
						UserChartSettingsActivity.this)
						.setTitle("請選擇圖表類型")
						.setIcon(android.R.drawable.ic_menu_more)
						.setPositiveButton(
								context.getResources().getString(R.string.action_ok),
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										currentChartType = tempSelect;
										tvChartType
												.setText(CHART_TYPE[currentChartType]);
										if (currentChartType == Constants.ChartSettings.CHART_TYPE_AGGRAGATION) {
											rtChartParameterLayout
													.setVisibility(View.INVISIBLE);
											aggrChartParameterLayout
													.setVisibility(View.VISIBLE);
										} else {
											rtChartParameterLayout
													.setVisibility(View.VISIBLE);
											aggrChartParameterLayout
													.setVisibility(View.INVISIBLE);
										}
									}
								})
						.setSingleChoiceItems(CHART_TYPE, currentChartType,
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										tempSelect = which;
									}
								})
						.setNegativeButton(
								context.getResources().getString(R.string.action_cancel),
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {

									}
								}).create();
				dialog.show();
			}
		});
	}

	private void setupChartGranularityTypeDlg(final Context context) {
		
		
		granularityTypeLayout.setOnClickListener(new View.OnClickListener() {
			private int tempSelect = 0;

			@Override
			public void onClick(View v) {

				Dialog dialog = new AlertDialog.Builder(
						UserChartSettingsActivity.this)
						.setTitle("請選擇統計單位")
						.setIcon(android.R.drawable.ic_menu_more)
						.setPositiveButton(
								context.getResources().getString(R.string.action_ok),
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										currentGranularityType = tempSelect;
										tvGranularityType
												.setText(GRANULARITY_TYPE[currentGranularityType]);
									}
								})
						.setSingleChoiceItems(GRANULARITY_TYPE,
								currentGranularityType,
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										tempSelect = which;
									}
								})
						.setNegativeButton(
								context.getResources().getString(R.string.action_cancel),
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {

									}
								}).create();
				dialog.show();
			}
		});
	}

	private void setupTimePeriodDlg(final Context context){
		timePeriodLayout.setOnClickListener(new View.OnClickListener() {
			private int tempSelect = 0;

			@Override
			public void onClick(View v) {

				Dialog dialog = new AlertDialog.Builder(
						UserChartSettingsActivity.this)
						.setTitle("請選擇統計週期")
						.setIcon(android.R.drawable.ic_menu_more)
						.setPositiveButton(
								context.getResources().getString(R.string.action_ok),
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										currentTimePeriodQuickLink = tempSelect;
										startTime = getStartTime(currentTimePeriodQuickLink);
										endTime = getEndTime(currentTimePeriodQuickLink);

										SimpleDateFormat sdf = new SimpleDateFormat(
												"yyyy-MM-dd HH:mm:ss");
										if (startTime != null) {
											tvTimePeriodStartTime.setText(sdf
													.format(startTime));
										}
										if (endTime != null) {
											tvTimePeriodEndTime.setText(sdf
													.format(endTime));
										}
									}
								})
						.setSingleChoiceItems(TIME_PERIOD_QUICKLINK,
								currentTimePeriodQuickLink,
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										tempSelect = which;
									}
								})
						.setNegativeButton(
								context.getResources().getString(R.string.action_cancel),
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {

									}
								}).create();
				dialog.show();
			}
		});
	}
	
	private void setupStartTimePickerDlg(final Context context){
		timePeriodStartTimeLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setupTimePickerDlg(context, true);
			}
		});
	}
	
	private void setupEndTimePickerDlg(final Context context){
		timePeriodEndTimeLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setupTimePickerDlg(context, false);
			}
		});
	}
	
	private void setupTimePickerDlg(final Context context, final boolean isStartTime) {

		Resources resources = context.getResources();
		View view = View.inflate(getApplicationContext(),
				R.layout.date_time_picker2, null);

		Calendar now = Calendar.getInstance();
		if (isStartTime && startTime != null) {
			now.setTime(startTime);
		} else if (!isStartTime && endTime != null) {
			now.setTime(endTime);
		}

		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH);
		int day = now.get(Calendar.DAY_OF_MONTH);
		int hour = now.get(Calendar.HOUR_OF_DAY);
		int minute = now.get(Calendar.MINUTE);

		String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
		String[] months_little = { "4", "6", "9", "11" };

		final List<String> list_big = Arrays.asList(months_big);
		final List<String> list_little = Arrays.asList(months_little);

		final StrArrayWheelView wv_year = (StrArrayWheelView) view
				.findViewById(R.id.year);
		wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));
		wv_year.setCyclic(true);
		wv_year.setLabel("年");
		wv_year.setCurrentItem(year - START_YEAR);

		final StrArrayWheelView wv_month = (StrArrayWheelView) view
				.findViewById(R.id.month);
		wv_month.setAdapter(new NumericWheelAdapter(1, 12));
		wv_month.setCyclic(true);
		wv_month.setLabel("月");
		wv_month.setCurrentItem(month);

		final StrArrayWheelView wv_day = (StrArrayWheelView) view
				.findViewById(R.id.day);
		wv_day.setCyclic(true);
		if (list_big.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdapter(1, 31));
		} else if (list_little.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdapter(1, 30));
		} else {
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
				wv_day.setAdapter(new NumericWheelAdapter(1, 29));
			else
				wv_day.setAdapter(new NumericWheelAdapter(1, 28));
		}
		wv_day.setLabel("日");
		wv_day.setCurrentItem(day - 1);

		final StrArrayWheelView wv_hours = (StrArrayWheelView) view
				.findViewById(R.id.hour);
		wv_hours.setAdapter(new NumericWheelAdapter(0, 23));
		wv_hours.setCyclic(true);
		wv_hours.setCurrentItem(hour);
		wv_hours.setLabel("時");

		final StrArrayWheelView wv_mins = (StrArrayWheelView) view
				.findViewById(R.id.mins);
		wv_mins.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
		wv_mins.setCyclic(true);
		wv_mins.setCurrentItem(minute);
		wv_mins.setLabel("分");

		OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {

			@Override
			public void onChanged(StrArrayWheelView wheel, int oldValue,
					int newValue) {
				int year_num = newValue + START_YEAR;
				if (list_big
						.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(wv_month
						.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					if ((year_num % 4 == 0 && year_num % 100 != 0)
							|| year_num % 400 == 0)
						wv_day.setAdapter(new NumericWheelAdapter(1, 29));
					else
						wv_day.setAdapter(new NumericWheelAdapter(1, 28));
				}
			}
		};

		OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {

			@Override
			public void onChanged(StrArrayWheelView wheel, int oldValue,
					int newValue) {
				int month_num = newValue + 1;
				if (list_big.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					if (((wv_year.getCurrentItem() + START_YEAR) % 4 == 0 && (wv_year
							.getCurrentItem() + START_YEAR) % 100 != 0)
							|| (wv_year.getCurrentItem() + START_YEAR) % 400 == 0)
						wv_day.setAdapter(new NumericWheelAdapter(1, 29));
					else
						wv_day.setAdapter(new NumericWheelAdapter(1, 28));
				}
			}
		};

		wv_year.addChangingListener(wheelListener_year);
		wv_month.addChangingListener(wheelListener_month);

		int textSize = 0;

		textSize = 15;

		wv_day.TEXT_SIZE = textSize;
		wv_hours.TEXT_SIZE = textSize;
		wv_mins.TEXT_SIZE = textSize;
		wv_month.TEXT_SIZE = textSize;
		wv_year.TEXT_SIZE = textSize;

		AlertDialog.Builder builder = new AlertDialog.Builder(context)
				.setView(view)
				.setTitle(isStartTime ? "請設定開始時間" : "請設定結束時間")
				.setIcon(android.R.drawable.ic_menu_more)
				.setPositiveButton(resources.getString(R.string.action_ok),
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								int year = wv_year.getCurrentItem()
										+ START_YEAR;
								int month = wv_month.getCurrentItem();
								int day = wv_day.getCurrentItem() + 1;
								int hour = wv_hours.getCurrentItem();
								int minute = wv_mins.getCurrentItem();
								Calendar c = Calendar.getInstance();
								c.set(Calendar.YEAR, year);
								c.set(Calendar.MONTH, month);
								c.set(Calendar.DAY_OF_MONTH, day);
								c.set(Calendar.HOUR_OF_DAY, hour);
								c.set(Calendar.MINUTE, minute);
								c.set(Calendar.SECOND, 0);

								SimpleDateFormat sdf = new SimpleDateFormat(
										"yyyy-MM-dd HH:mm:ss");
								if (isStartTime) {
									startTime = c.getTime();
									if (startTime != null) {
										tvTimePeriodStartTime.setText(sdf
												.format(startTime));
									}
								} else {
									endTime = c.getTime();
									if (endTime != null) {
										tvTimePeriodEndTime.setText(sdf
												.format(endTime));
									}
								}
							}
						})
				.setNegativeButton(resources.getString(R.string.action_cancel),
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {

							}
						});
		builder.create().show();

	}

	private Date getStartTime(int preDefinedTimePeriod) {
		Date result = null;
		switch (preDefinedTimePeriod) {
		case TIME_PERIOD_TODAY: {
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			result = now.getTime();
			break;
		}
		case TIME_PERIOD_YESTERDAY: {
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			now.add(Calendar.DATE, -1);

			result = now.getTime();
			break;
		}
		case TIME_PERIOD_THIS_WEEK: {
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			while (now.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
				now.add(Calendar.DATE, -1);
			}

			result = now.getTime();
			break;
		}
		case TIME_PERIOD_LAST_WEEK: {
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			while (now.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
				now.add(Calendar.DATE, -1);
			}
			now.add(Calendar.DATE, -7);

			result = now.getTime();
			break;
		}
		case TIME_PERIOD_THIS_MONTH: {
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			now.set(Calendar.DAY_OF_MONTH, 1);

			result = now.getTime();
			break;
		}
		case TIME_PERIOD_LAST_MONTH: {
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			now.set(Calendar.DAY_OF_MONTH, 1);
			now.add(Calendar.MONTH, -1);
			result = now.getTime();
			break;
		}
		case TIME_PERIOD_LAST_3DAYS: {
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			now.add(Calendar.DATE, -2);
			result = now.getTime();
			break;
		}
		case TIME_PERIOD_LAST_5DAYS: {
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			now.add(Calendar.DATE, -4);
			result = now.getTime();
			break;
		}
		case TIME_PERIOD_LAST_7DAYS: {
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			now.add(Calendar.DATE, -6);
			result = now.getTime();
			break;
		}
		case TIME_PERIOD_LAST_1WEEK: {
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			while (now.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
				now.add(Calendar.DATE, -1);
			}
			now.add(Calendar.DATE, -7);

			result = now.getTime();
			break;
		}
		case TIME_PERIOD_LAST_2WEEKS: {
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			while (now.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
				now.add(Calendar.DATE, -1);
			}
			now.add(Calendar.DATE, -14);

			result = now.getTime();
			break;
		}
		case TIME_PERIOD_LAST_3WEEKS: {
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			while (now.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
				now.add(Calendar.DATE, -1);
			}
			now.add(Calendar.DATE, -21);

			result = now.getTime();
			break;
		}
		case TIME_PERIOD_LAST_1MONTH: {
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			now.set(Calendar.DAY_OF_MONTH, 1);
			now.add(Calendar.MONTH, -1);
			result = now.getTime();
			break;
		}
		case TIME_PERIOD_LAST_2MONTHS: {
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			now.set(Calendar.DAY_OF_MONTH, 1);
			now.add(Calendar.MONTH, -2);
			result = now.getTime();
			break;
		}
		case TIME_PERIOD_LAST_3MONTHS: {
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			now.set(Calendar.DAY_OF_MONTH, 1);
			now.add(Calendar.MONTH, -3);
			result = now.getTime();
			break;
		}
		case TIME_PERIOD_1_HOUR: {
			Calendar now = Calendar.getInstance();
			now.add(Calendar.HOUR_OF_DAY, -1);
			result = now.getTime();
			break;
		}
		case TIME_PERIOD_4_HOURS: {
			Calendar now = Calendar.getInstance();
			now.add(Calendar.HOUR_OF_DAY, -4);
			result = now.getTime();
			break;
		}
		case TIME_PERIOD_8_HOURS: {
			Calendar now = Calendar.getInstance();
			now.add(Calendar.HOUR_OF_DAY, -8);
			result = now.getTime();
			break;
		}
		}

		return result;
	}

	private Date getEndTime(int preDefinedTimePeriod) {
		Date startTime = getStartTime(preDefinedTimePeriod);
		if (startTime == null) {
			return null;
		} else {
			Calendar cal = Calendar.getInstance();
			cal.setTime(startTime);

			Date result = null;
			switch (preDefinedTimePeriod) {
			case TIME_PERIOD_TODAY: {
				cal.add(Calendar.DATE, 1);
				result = cal.getTime();
				break;
			}
			case TIME_PERIOD_YESTERDAY: {
				cal.add(Calendar.DATE, 1);
				result = cal.getTime();
				break;
			}
			case TIME_PERIOD_THIS_WEEK: {
				cal.add(Calendar.DATE, 7);
				result = cal.getTime();
				break;
			}
			case TIME_PERIOD_LAST_WEEK: {
				cal.add(Calendar.DATE, 7);
				result = cal.getTime();
				break;
			}
			case TIME_PERIOD_THIS_MONTH: {
				cal.add(Calendar.MONTH, 1);
				result = cal.getTime();
				break;
			}
			case TIME_PERIOD_LAST_MONTH: {
				cal.add(Calendar.MONTH, 1);
				result = cal.getTime();
				break;
			}
			case TIME_PERIOD_LAST_3DAYS: {
				cal.add(Calendar.DATE, 3);
				result = cal.getTime();
				break;
			}
			case TIME_PERIOD_LAST_5DAYS: {
				cal.add(Calendar.DATE, 5);
				result = cal.getTime();
				break;
			}
			case TIME_PERIOD_LAST_7DAYS: {
				cal.add(Calendar.DATE, 7);
				result = cal.getTime();
				break;
			}
			case TIME_PERIOD_LAST_1WEEK: {
				result = getEndTime(TIME_PERIOD_TODAY);
				break;
			}
			case TIME_PERIOD_LAST_2WEEKS: {
				result = getEndTime(TIME_PERIOD_TODAY);
				break;
			}
			case TIME_PERIOD_LAST_3WEEKS: {
				result = getEndTime(TIME_PERIOD_TODAY);
				break;
			}
			case TIME_PERIOD_LAST_1MONTH: {
				result = getEndTime(TIME_PERIOD_TODAY);
				break;
			}
			case TIME_PERIOD_LAST_2MONTHS: {
				result = getEndTime(TIME_PERIOD_TODAY);
				break;
			}
			case TIME_PERIOD_LAST_3MONTHS: {
				result = getEndTime(TIME_PERIOD_TODAY);
				break;
			}
			case TIME_PERIOD_1_HOUR: {
				cal.add(Calendar.HOUR_OF_DAY, 1);
				result = cal.getTime();
				break;
			}
			case TIME_PERIOD_4_HOURS: {
				cal.add(Calendar.HOUR_OF_DAY, 4);
				result = cal.getTime();
				break;
			}
			case TIME_PERIOD_8_HOURS: {
				cal.add(Calendar.HOUR_OF_DAY, 8);
				result = cal.getTime();
				break;
			}
			}
			return result;
		}
	}
}
