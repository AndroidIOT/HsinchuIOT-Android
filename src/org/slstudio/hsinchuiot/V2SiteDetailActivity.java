package org.slstudio.hsinchuiot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.slstudio.hsinchuiot.model.IOTMonitorData;
import org.slstudio.hsinchuiot.model.IOTSampleData;
import org.slstudio.hsinchuiot.model.Site;
import org.slstudio.hsinchuiot.service.ServiceContainer;
import org.slstudio.hsinchuiot.service.http.HttpConfig;
import org.slstudio.hsinchuiot.service.http.HttpRequest;
import org.slstudio.hsinchuiot.service.http.NoneAuthedHttpRequest;
import org.slstudio.hsinchuiot.service.http.RequestControl;
import org.slstudio.hsinchuiot.service.http.RequestListener;
import org.slstudio.hsinchuiot.ui.chart.IOTChartFactory;
import org.slstudio.hsinchuiot.util.IOTLog;
import org.slstudio.hsinchuiot.util.ReportUtil;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Resources;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class V2SiteDetailActivity extends BaseActivity {
	public final String[] monthSelection = { "01", "02", "03", "04", "05",
			"06", "07", "08", "09", "10", "11", "12" };
	public final String[] dateSelection = { "01", "02", "03", "04", "05", "06",
			"07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17",
			"18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28",
			"29", "30", "31" };
	public final String[] hourSelection = { "00", "01", "02", "03", "04", "05",
			"06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16",
			"17", "18", "19", "20", "21", "22", "23" };
	public final String[] minuteSelection = { "00", "01", "02", "03", "04",
			"05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15",
			"16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26",
			"27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37",
			"38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48",
			"49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" };
	public final String[] secondSelection = { "00", "01", "02", "03", "04",
			"05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15",
			"16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26",
			"27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37",
			"38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48",
			"49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" };

	public static final int TIME_PERIOD_1_HOUR = 0;
	public static final int TIME_PERIOD_4_HOURS = 1;
	public static final int TIME_PERIOD_8_HOURS = 2;
	public static final int TIME_PERIOD_TODAY = 3;
	public static final int TIME_PERIOD_YESTERDAY = 4;
	public static final int TIME_PERIOD_THIS_WEEK = 5;
	public static final int TIME_PERIOD_LAST_WEEK = 6;
	public static final int TIME_PERIOD_THIS_MONTH = 7;
	public static final int TIME_PERIOD_LAST_MONTH = 8;
	public static final int TIME_PERIOD_LAST_3DAYS = 9;
	public static final int TIME_PERIOD_LAST_5DAYS = 10;
	public static final int TIME_PERIOD_LAST_7DAYS = 11;
	public static final int TIME_PERIOD_LAST_1WEEK = 12;
	public static final int TIME_PERIOD_LAST_2WEEKS = 13;
	public static final int TIME_PERIOD_LAST_3WEEKS = 14;
	public static final int TIME_PERIOD_LAST_1MONTH = 15;
	public static final int TIME_PERIOD_LAST_2MONTHS = 16;
	public static final int TIME_PERIOD_LAST_3MONTHS = 17;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.MessageKey.MESSAGE_GET_CHART_DATA:
				IOTLog.d("Handler",
						"debuginfo(CHART_DATA) - handleMessage: receive msg MESSAGE_GET_CHART_DATA");
				if (currentSite != null && !isPaused) {
					if (sendQueryChartDataRequest(currentSite.getDevice()
							.getDeviceID())) {
						updateChartDataInProcessing();
					}
				}
				break;
			case Constants.MessageKey.MESSAGE_UPDATE_CHART_DATA:
				IOTLog.d("Handler",
						"debuginfo(CHART_DATA) - handleMessage: receive msg MESSAGE_UPDATE_CHART_DATA");
				if (currentSite != null && !isPaused) {
					if (updateChartData((List<IOTSampleData>) msg.obj)) {
						updateChartDataFinished();
					}
				}
				break;
			}
			super.handleMessage(msg);

		}
	};

	private Site currentSite;
	private int chartType = Constants.ChartSettings.CHART_TYPE_REALTIME;
	private int chartTimeDuration = 5;
	private int chartGranularity = Constants.ChartSettings.GRANULARITY_HOUR;
	private Date chartStartTime;
	private Date chartEndTime;

	private XYMultipleSeriesDataset chartDataset;
	private XYMultipleSeriesRenderer chartRenderer;
	private XYSeries co2Series;
	private XYSeriesRenderer co2Renderer;
	private XYSeries temperatureSeries;
	private XYSeriesRenderer temperatureRenderer;
	private XYSeries humiditySeries;
	private XYSeriesRenderer humidityRenderer;

	private GraphicalView chartView;
	private LinearLayout chartLayout;
	// private TextView tvChartTitleBottom;

	private List<IOTSampleData> chartData = new ArrayList<IOTSampleData>();

	private boolean isPaused = false;
	private boolean isChartRequestHandling = false;

	private RequestControl chartRC;

	private TextView tvChartTitle;
	private ImageButton btnBack;
	private ImageButton btnRefresh;
	private ImageButton btnTimeScope;
	private ImageButton btnTimeScope2;
	private RadioGroup rgInterval;
	private RadioButton rbIntervalRealtime;
	private RadioButton rbIntervalCurrent;
	private RadioButton rbIntervalByQuarter;
	private RadioButton rbIntervalByHour;
	private RadioButton rbIntervalBy8Hours;
	private RadioButton rbIntervalByDay;
	private RadioButton rbIntervalByWeek;
	private RadioButton rbIntervalByMonth;
	
	private ProgressDialog progressDialog;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		currentSite = (Site) getIntent().getSerializableExtra(
				Constants.ActivityPassValue.SELECTED_SITE);
		chartType = getIntent().getIntExtra(
				Constants.ActivityPassValue.CHART_TYPE,
				Constants.ChartSettings.CHART_TYPE_REALTIME);
		chartTimeDuration = getIntent().getIntExtra(
				Constants.ActivityPassValue.CHART_RT_DURATION, 5);
		chartGranularity = getIntent().getIntExtra(
				Constants.ActivityPassValue.CHART_AGGR_GRANULARITY,
				Constants.ChartSettings.GRANULARITY_HOUR);
		long startTimeLong = getIntent().getLongExtra(
				Constants.ActivityPassValue.CHART_AGGR_STARTTIME, 0);
		chartStartTime = new Date();
		if (startTimeLong != 0) {
			chartStartTime.setTime(startTimeLong);
		}
		long endTimeLong = getIntent().getLongExtra(
				Constants.ActivityPassValue.CHART_AGGR_ENDTIME, 0);
		chartEndTime = new Date();
		if (endTimeLong != 0) {
			chartEndTime.setTime(endTimeLong);
		}

		setContentView(R.layout.v2_activity_sitedetail);

		initViews();
		createChart();
		updateChartData();
		chartView.repaint();

		handler.sendEmptyMessage(Constants.MessageKey.MESSAGE_GET_CHART_DATA);
	}

	@Override
	protected void onResume() {

		IOTLog.d("V2SuperUserSiteDetailActivity",
				"debuginfo - onResume: re-send messages");
		isPaused = false;

		resendMessage();

		super.onResume();
	}

	@Override
	protected void onPause() {
		isPaused = true;

		IOTLog.d("V2SuperUserSiteDetailActivity",
				"debuginfo(CHART_DATA) - onPause: remove msgs from queue");
		handler.removeMessages(Constants.MessageKey.MESSAGE_GET_CHART_DATA);

		IOTLog.d("V2SuperUserSiteDetailActivity",
				"debuginfo(CHART_DATA) - onPause: cancel http request");

		if (chartRC != null) {
			chartRC.cancel();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {

		IOTLog.d("V2SuperUserSiteDetailActivity",
				"debuginfo(CHART_DATA) - onDestroy: remove msgs from queue");
		handler.removeMessages(Constants.MessageKey.MESSAGE_GET_CHART_DATA);

		IOTLog.d("V2SuperUserSiteDetailActivity",
				"debuginfo(CHART_DATA) - onDestroy: cancel http request");

		if (chartRC != null) {
			chartRC.cancel();
		}
		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.menu_close:
			finish();
			break;

		}
		return true;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_user_chartdetail, menu);
		return true;
	}

	private void initViews() {
		chartLayout = (LinearLayout) findViewById(R.id.id_chart_chartdetail);

		tvChartTitle = (TextView) findViewById(R.id.superuser_sitedetail_title);
		tvChartTitle.setText(currentSite.getSiteName());

		btnBack = (ImageButton) findViewById(R.id.btn_back);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}

		});

		btnRefresh = (ImageButton) findViewById(R.id.btn_refresh);
		btnRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				resendMessage();
			}

		});

		btnTimeScope = (ImageButton) findViewById(R.id.btn_timescope);
		btnTimeScope.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showTimeScopeDialog();
			}

		});

		btnTimeScope2 = (ImageButton) findViewById(R.id.btn_timescope2);
		btnTimeScope2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showTimeScope2Dialog();
			}

		});

		rgInterval = (RadioGroup) findViewById(R.id.interval_btn_group);
		rgInterval
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						if (checkedId == R.id.interval_btn_realtime) {
							chartType = Constants.ChartSettings.CHART_TYPE_REALTIME;
							btnTimeScope.setVisibility(View.GONE);
							btnTimeScope2.setVisibility(View.GONE);
						}else if (checkedId == R.id.interval_btn_current) {
							chartType = Constants.ChartSettings.CHART_TYPE_AGGRAGATION;
							chartGranularity = Constants.ChartSettings.GRANULARITY_MINUTE;
							btnTimeScope.setVisibility(View.VISIBLE);
							btnTimeScope2.setVisibility(View.VISIBLE);
						}else if (checkedId == R.id.interval_btn_byquarter) {
							chartType = Constants.ChartSettings.CHART_TYPE_AGGRAGATION;
							chartGranularity = Constants.ChartSettings.GRANULARITY_QUARTER;
							btnTimeScope.setVisibility(View.VISIBLE);
							btnTimeScope2.setVisibility(View.VISIBLE);
						}else if (checkedId == R.id.interval_btn_byhour) {
							chartType = Constants.ChartSettings.CHART_TYPE_AGGRAGATION;
							chartGranularity = Constants.ChartSettings.GRANULARITY_HOUR;
							btnTimeScope.setVisibility(View.VISIBLE);
							btnTimeScope2.setVisibility(View.VISIBLE);
						} else if (checkedId == R.id.interval_btn_by8hours) {
							chartType = Constants.ChartSettings.CHART_TYPE_AGGRAGATION;
							chartGranularity = Constants.ChartSettings.GRANULARITY_HOURS;
							btnTimeScope.setVisibility(View.VISIBLE);
							btnTimeScope2.setVisibility(View.VISIBLE);
						} else if (checkedId == R.id.interval_btn_byday) {
							chartType = Constants.ChartSettings.CHART_TYPE_AGGRAGATION;
							chartGranularity = Constants.ChartSettings.GRANULARITY_DAY;
							btnTimeScope.setVisibility(View.VISIBLE);
							btnTimeScope2.setVisibility(View.VISIBLE);
						} else if (checkedId == R.id.interval_btn_byweek) {
							chartType = Constants.ChartSettings.CHART_TYPE_AGGRAGATION;
							chartGranularity = Constants.ChartSettings.GRANULARITY_WEEK;
							btnTimeScope.setVisibility(View.VISIBLE);
							btnTimeScope2.setVisibility(View.VISIBLE);
						} else if (checkedId == R.id.interval_btn_bymonth) {
							chartType = Constants.ChartSettings.CHART_TYPE_AGGRAGATION;
							chartGranularity = Constants.ChartSettings.GRANULARITY_MONTH;
							btnTimeScope.setVisibility(View.VISIBLE);
							btnTimeScope2.setVisibility(View.VISIBLE);
						}
						//generateChart();
						resendMessage();
					}

				});
		rbIntervalRealtime = (RadioButton) findViewById(R.id.interval_btn_realtime);
		rbIntervalCurrent = (RadioButton) findViewById(R.id.interval_btn_current);
		rbIntervalByQuarter = (RadioButton) findViewById(R.id.interval_btn_byquarter);
		rbIntervalByHour = (RadioButton) findViewById(R.id.interval_btn_byhour);
		rbIntervalBy8Hours = (RadioButton) findViewById(R.id.interval_btn_by8hours);
		rbIntervalByDay = (RadioButton) findViewById(R.id.interval_btn_byday);
		rbIntervalByWeek = (RadioButton) findViewById(R.id.interval_btn_byweek);
		rbIntervalByMonth = (RadioButton) findViewById(R.id.interval_btn_bymonth);

		if (chartType == Constants.ChartSettings.CHART_TYPE_REALTIME) {
			rbIntervalRealtime.setChecked(true);
			btnTimeScope.setVisibility(View.GONE);
			btnTimeScope2.setVisibility(View.GONE);
		} else if (chartType == Constants.ChartSettings.CHART_TYPE_AGGRAGATION) {
			btnTimeScope.setVisibility(View.VISIBLE);
			btnTimeScope2.setVisibility(View.VISIBLE);
			if (chartGranularity == Constants.ChartSettings.GRANULARITY_MINUTE) {
				rbIntervalCurrent.setChecked(true);
			}if (chartGranularity == Constants.ChartSettings.GRANULARITY_QUARTER) {
				rbIntervalByQuarter.setChecked(true);
			}else if (chartGranularity == Constants.ChartSettings.GRANULARITY_HOUR) {
				rbIntervalByHour.setChecked(true);
			} else if (chartGranularity == Constants.ChartSettings.GRANULARITY_HOURS) {
				rbIntervalBy8Hours.setChecked(true);
			} else if (chartGranularity == Constants.ChartSettings.GRANULARITY_DAY) {
				rbIntervalByDay.setChecked(true);
			} else if (chartGranularity == Constants.ChartSettings.GRANULARITY_WEEK) {
				rbIntervalByWeek.setChecked(true);
			} else if (chartGranularity == Constants.ChartSettings.GRANULARITY_MONTH) {
				rbIntervalByMonth.setChecked(true);
			}
		}

	}

	private void showTimeScopeDialog() {
		View view = View.inflate(getApplicationContext(),
				R.layout.v2_dialog_chart_timescope, null);
		ImageButton btnClose = (ImageButton) view
				.findViewById(R.id.btn_chart_timescope_close);
		Button btnOk = (Button) view.findViewById(R.id.btn_chart_timescope_ok);
		Button btnCancel = (Button) view
				.findViewById(R.id.btn_chart_timescope_cancel);
		final Button btnFromYear = (Button) view
				.findViewById(R.id.btn_chart_timescope_from_year);
		final Button btnFromMonth = (Button) view
				.findViewById(R.id.btn_chart_timescope_from_month);
		final Button btnFromDay = (Button) view
				.findViewById(R.id.btn_chart_timescope_from_day);
		final Button btnFromHour = (Button) view
				.findViewById(R.id.btn_chart_timescope_from_hour);
		final Button btnFromMinute = (Button) view
				.findViewById(R.id.btn_chart_timescope_from_minute);
		final Button btnFromSecond = (Button) view
				.findViewById(R.id.btn_chart_timescope_from_second);
		final Button btnToYear = (Button) view
				.findViewById(R.id.btn_chart_timescope_to_year);
		final Button btnToMonth = (Button) view
				.findViewById(R.id.btn_chart_timescope_to_month);
		final Button btnToDay = (Button) view
				.findViewById(R.id.btn_chart_timescope_to_day);
		final Button btnToHour = (Button) view
				.findViewById(R.id.btn_chart_timescope_to_hour);
		final Button btnToMinute = (Button) view
				.findViewById(R.id.btn_chart_timescope_to_minute);
		final Button btnToSecond = (Button) view
				.findViewById(R.id.btn_chart_timescope_to_second);

		AlertDialog.Builder builder = new AlertDialog.Builder(
				V2SiteDetailActivity.this,
				android.R.style.Theme_Black_NoTitleBar).setView(view);

		final AlertDialog dlg = builder.create();

		Window dialogWindow = dlg.getWindow();

		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams p = dlg.getWindow().getAttributes(); // 获取对话框当前的参数值
		p.height = (int) (d.getHeight() * 0.8); // 高度设置为屏幕的0.6
		p.width = (int) (d.getWidth() * 0.65); // 宽度设置为屏幕的0.95
		dialogWindow.setAttributes(p);
		dlg.setCancelable(false);

		btnClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
			}

		});
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int fromYear = Integer.parseInt(btnFromYear.getText()
						.toString());
				int fromMonth = Integer.parseInt(btnFromMonth.getText()
						.toString());
				int fromDay = Integer.parseInt(btnFromDay.getText().toString());
				int fromHour = Integer.parseInt(btnFromHour.getText()
						.toString());
				int fromMinute = Integer.parseInt(btnFromMinute.getText()
						.toString());
				int fromSecond = Integer.parseInt(btnFromSecond.getText()
						.toString());

				int toYear = Integer.parseInt(btnToYear.getText().toString());
				int toMonth = Integer.parseInt(btnToMonth.getText().toString());
				int toDay = Integer.parseInt(btnToDay.getText().toString());
				int toHour = Integer.parseInt(btnToHour.getText().toString());
				int toMinute = Integer.parseInt(btnToMinute.getText()
						.toString());
				int toSecond = Integer.parseInt(btnToSecond.getText()
						.toString());

				Calendar sCal = Calendar.getInstance();
				sCal.set(Calendar.YEAR, fromYear);
				sCal.set(Calendar.MONTH, fromMonth - 1);
				sCal.set(Calendar.DATE, fromDay);
				sCal.set(Calendar.HOUR_OF_DAY, fromHour);
				sCal.set(Calendar.MINUTE, fromMinute);
				sCal.set(Calendar.SECOND, fromSecond);

				chartStartTime = sCal.getTime();

				Calendar eCal = Calendar.getInstance();
				eCal.set(Calendar.YEAR, toYear);
				eCal.set(Calendar.MONTH, toMonth - 1);
				eCal.set(Calendar.DATE, toDay);
				eCal.set(Calendar.HOUR_OF_DAY, toHour);
				eCal.set(Calendar.MINUTE, toMinute);
				eCal.set(Calendar.SECOND, toSecond);

				chartEndTime = eCal.getTime();

				dlg.dismiss();

				//generateChart();
				resendMessage();
			}

		});
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
			}

		});

		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		final String[] yearSelection = { Integer.toString(currentYear - 2),
				Integer.toString(currentYear - 1),
				Integer.toString(currentYear),
				Integer.toString(currentYear + 1),
				Integer.toString(currentYear + 2) };

		btnFromYear.setText(Integer.toString(chartStartTime.getYear() + 1900));
		btnFromYear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Dialog dialog = new AlertDialog.Builder(
						V2SiteDetailActivity.this).setItems(
						yearSelection, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								btnFromYear.setText(yearSelection[which]);
							}
						}).create();
				dialog.show();

			}

		});

		btnFromMonth.setText(monthSelection[chartStartTime.getMonth()]);
		btnFromMonth.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Dialog dialog = new AlertDialog.Builder(
						V2SiteDetailActivity.this).setItems(
						monthSelection, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								btnFromMonth.setText(monthSelection[which]);
							}
						}).create();
				dialog.show();

			}

		});

		btnFromDay.setText(dateSelection[chartStartTime.getDate() - 1]);
		btnFromDay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int selectYear = Integer.parseInt(btnFromYear.getText()
						.toString());
				int selectMonth = Integer.parseInt(btnFromMonth.getText()
						.toString());

				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, selectYear);
				cal.set(Calendar.MONTH, selectMonth - 1);
				int maxDate = cal.getActualMaximum(Calendar.DATE);

				final String[] date = new String[maxDate];
				System.arraycopy(dateSelection, 0, date, 0, maxDate);

				Dialog dialog = new AlertDialog.Builder(
						V2SiteDetailActivity.this).setItems(date,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								btnFromDay.setText(date[which]);
							}
						}).create();
				dialog.show();

			}

		});

		btnFromHour.setText(hourSelection[chartStartTime.getHours()]);
		btnFromHour.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Dialog dialog = new AlertDialog.Builder(
						V2SiteDetailActivity.this).setItems(
						hourSelection, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								btnFromHour.setText(hourSelection[which]);
							}
						}).create();
				dialog.show();

			}

		});

		btnFromMinute.setText(minuteSelection[chartStartTime.getMinutes()]);
		btnFromMinute.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Dialog dialog = new AlertDialog.Builder(
						V2SiteDetailActivity.this).setItems(
						minuteSelection, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								btnFromMinute.setText(minuteSelection[which]);
							}
						}).create();
				dialog.show();

			}

		});

		btnFromSecond.setText(secondSelection[chartStartTime.getSeconds()]);
		btnFromSecond.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Dialog dialog = new AlertDialog.Builder(
						V2SiteDetailActivity.this).setItems(
						secondSelection, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								btnFromSecond.setText(secondSelection[which]);
							}
						}).create();
				dialog.show();

			}

		});

		btnToYear.setText(Integer.toString(chartEndTime.getYear() + 1900));
		btnToYear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Dialog dialog = new AlertDialog.Builder(
						V2SiteDetailActivity.this).setItems(
						yearSelection, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								btnToYear.setText(yearSelection[which]);
							}
						}).create();
				dialog.show();

			}

		});

		btnToMonth.setText(monthSelection[chartEndTime.getMonth()]);
		btnToMonth.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Dialog dialog = new AlertDialog.Builder(
						V2SiteDetailActivity.this).setItems(
						monthSelection, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								btnToMonth.setText(monthSelection[which]);
							}
						}).create();
				dialog.show();

			}

		});

		btnToDay.setText(dateSelection[chartEndTime.getDate() - 1]);
		btnToDay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int selectYear = Integer.parseInt(btnToYear.getText()
						.toString());
				int selectMonth = Integer.parseInt(btnToMonth.getText()
						.toString());

				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, selectYear);
				cal.set(Calendar.MONTH, selectMonth - 1);
				int maxDate = cal.getActualMaximum(Calendar.DATE);

				final String[] date = new String[maxDate];
				System.arraycopy(dateSelection, 0, date, 0, maxDate);

				Dialog dialog = new AlertDialog.Builder(
						V2SiteDetailActivity.this).setItems(date,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								btnToDay.setText(date[which]);
							}
						}).create();
				dialog.show();

			}

		});

		btnToHour.setText(hourSelection[chartEndTime.getHours()]);
		btnToHour.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Dialog dialog = new AlertDialog.Builder(
						V2SiteDetailActivity.this).setItems(
						hourSelection, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								btnToHour.setText(hourSelection[which]);
							}
						}).create();
				dialog.show();

			}

		});

		btnToMinute.setText(minuteSelection[chartEndTime.getMinutes()]);
		btnToMinute.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Dialog dialog = new AlertDialog.Builder(
						V2SiteDetailActivity.this).setItems(
						minuteSelection, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								btnToMinute.setText(minuteSelection[which]);
							}
						}).create();
				dialog.show();

			}

		});

		btnToSecond.setText(secondSelection[chartEndTime.getSeconds()]);
		btnToSecond.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Dialog dialog = new AlertDialog.Builder(
						V2SiteDetailActivity.this).setItems(
						secondSelection, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								btnToSecond.setText(secondSelection[which]);
							}
						}).create();
				dialog.show();

			}

		});

		dlg.show();
	}

	private void showTimeScope2Dialog() {
		View view = View.inflate(getApplicationContext(),
				R.layout.v2_dialog_chart_timescope2, null);

		ImageButton btnClose = (ImageButton) view
				.findViewById(R.id.btn_chart_timescope2_close);
		Button btn1Hour = (Button) view
				.findViewById(R.id.btn_chart_timescope2_1hour);
		Button btn4Hours = (Button) view
				.findViewById(R.id.btn_chart_timescope2_4hours);
		Button btn8Hours = (Button) view
				.findViewById(R.id.btn_chart_timescope2_8hours);
		Button btnToday = (Button) view
				.findViewById(R.id.btn_chart_timescope2_today);
		Button btnYesterday = (Button) view
				.findViewById(R.id.btn_chart_timescope2_yesterday);
		Button btnLast3Days = (Button) view
				.findViewById(R.id.btn_chart_timescope2_last3days);
		Button btnLast5Days = (Button) view
				.findViewById(R.id.btn_chart_timescope2_last5days);
		Button btnThisWeek = (Button) view
				.findViewById(R.id.btn_chart_timescope2_thisweek);
		Button btnLastWeek = (Button) view
				.findViewById(R.id.btn_chart_timescope2_lastweek);
		Button btnLast2Weeks = (Button) view
				.findViewById(R.id.btn_chart_timescope2_last2weeks);
		Button btnLast3Weeks = (Button) view
				.findViewById(R.id.btn_chart_timescope2_last3weeks);
		Button btnThisMonth = (Button) view
				.findViewById(R.id.btn_chart_timescope2_thismonth);
		Button btnLastMonth = (Button) view
				.findViewById(R.id.btn_chart_timescope2_lastmonth);
		Button btnLast2Months = (Button) view
				.findViewById(R.id.btn_chart_timescope2_last2months);
		Button btnLast3Months = (Button) view
				.findViewById(R.id.btn_chart_timescope2_last3months);

		AlertDialog.Builder builder = new AlertDialog.Builder(
				V2SiteDetailActivity.this,
				android.R.style.Theme_Black_NoTitleBar).setView(view);

		final AlertDialog dlg = builder.create();

		Window dialogWindow = dlg.getWindow();

		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams p = dlg.getWindow().getAttributes(); // 获取对话框当前的参数值
		p.height = (int) (d.getHeight() * 0.5); // 高度设置为屏幕的0.6
		p.width = (int) (d.getWidth() * 0.95); // 宽度设置为屏幕的0.95
		dialogWindow.setAttributes(p);
		dlg.setCancelable(false);

		btnClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
			}

		});

		btn1Hour.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setChartTimePeriod(TIME_PERIOD_1_HOUR);
				dlg.dismiss();
			}

		});

		btn4Hours.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setChartTimePeriod(TIME_PERIOD_4_HOURS);
				dlg.dismiss();
			}

		});

		btn8Hours.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setChartTimePeriod(TIME_PERIOD_8_HOURS);
				dlg.dismiss();
			}

		});
		
		btnToday.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setChartTimePeriod(TIME_PERIOD_TODAY);
				dlg.dismiss();
			}

		});

		btnYesterday.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setChartTimePeriod(TIME_PERIOD_YESTERDAY);
				dlg.dismiss();
			}

		});
		
		btnLast3Days.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setChartTimePeriod(TIME_PERIOD_LAST_3DAYS);
				dlg.dismiss();
			}

		});
		
		btnLast5Days.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setChartTimePeriod(TIME_PERIOD_LAST_5DAYS);
				dlg.dismiss();
			}

		});
		
		btnThisWeek.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setChartTimePeriod(TIME_PERIOD_THIS_WEEK);
				dlg.dismiss();
			}

		});
		
		btnLastWeek.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setChartTimePeriod(TIME_PERIOD_LAST_WEEK);
				dlg.dismiss();
			}

		});
		
		btnLast2Weeks.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setChartTimePeriod(TIME_PERIOD_LAST_2WEEKS);
				dlg.dismiss();
			}

		});
		
		btnLast3Weeks.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setChartTimePeriod(TIME_PERIOD_LAST_3WEEKS);
				dlg.dismiss();
			}

		});
		
		btnThisMonth.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setChartTimePeriod(TIME_PERIOD_THIS_MONTH);
				dlg.dismiss();
			}

		});
		
		btnLastMonth.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setChartTimePeriod(TIME_PERIOD_LAST_MONTH);
				dlg.dismiss();
			}

		});
		
		btnLast2Months.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setChartTimePeriod(TIME_PERIOD_LAST_2MONTHS);
				dlg.dismiss();
			}

		});

		btnLast3Months.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setChartTimePeriod(TIME_PERIOD_LAST_3MONTHS);
				dlg.dismiss();
			}

		});

		
		dlg.show();
	}

	private void setChartTimePeriod(int period) {
		chartStartTime = getStartTime(period);
		chartEndTime = getEndTime(period);
		//generateChart();
		resendMessage();
	}

	private void createChart() {
		//chartDataset.clear();
		//chartRenderer.removeAllRenderers();
		chartDataset = new XYMultipleSeriesDataset();
		chartRenderer = new XYMultipleSeriesRenderer(3);
		Resources resources = getResources();

		chartRenderer.setApplyBackgroundColor(true);// 设置是否显示背景色
		chartRenderer.setBackgroundColor(resources.getColor(R.color.white));// 设置背景色
		chartRenderer.setMargins(new int[] { 20, 60, 35, 30 });// 设置图表的外边框(上/左/下/右)
		chartRenderer.setMarginsColor(resources.getColor(R.color.white));

		chartRenderer.setChartTitleTextSize(0);// ?设置整个图表标题文字大小

		chartRenderer.setAxesColor(resources.getColor(R.color.dark_gray));
		chartRenderer.setAxisTitleTextSize(16); // 设置轴标题文字的大小
		chartRenderer.setLabelsColor(resources.getColor(R.color.dark_gray));

		chartRenderer.setLabelsTextSize(15);// 设置刻度显示文字的大小(XY轴都会被设置)
		chartRenderer.setLegendTextSize(18);// 图例文字大小
		chartRenderer.setFitLegend(true);
		chartRenderer.setTextTypeface("sans_serif", Typeface.BOLD);

		chartRenderer.setXLabelsColor(resources.getColor(R.color.dark_gray));
		chartRenderer.setXLabels(5);
		chartRenderer.setYLabels(5);

		chartRenderer.setYLabelsColor(0,
				resources.getColor(R.color.chart_color_co2));
		chartRenderer.setYAxisAlign(Align.LEFT, 0);
		chartRenderer.setYLabelsAlign(Align.RIGHT, 0);

		chartRenderer.setYLabelsColor(1,
				resources.getColor(R.color.chart_color_temperature));
		chartRenderer.setYAxisAlign(Align.RIGHT, 1);
		chartRenderer.setYLabelsAlign(Align.RIGHT, 1);

		chartRenderer.setYLabelsColor(2,
				resources.getColor(R.color.chart_color_humidity));
		chartRenderer.setYAxisAlign(Align.RIGHT, 2);
		chartRenderer.setYLabelsAlign(Align.LEFT, 2);

		chartRenderer.setZoomButtonsVisible(true);// 是否显示放大缩小按钮
		chartRenderer.setPointSize(3);// 设置点的大小(图上显示的点的大小和图例中点的大小都会被设置)
		chartRenderer.setPanEnabled(true);
		chartRenderer.setClickEnabled(true);
		

		//chartRenderer.setShowCustomTextTargetLineY(true);
		//chartRenderer.setFillTargetLineWithColor(true);

		co2Series = new XYSeries(resources.getString(R.string.co2), 0);// 定义XYSeries
		chartDataset.addSeries(co2Series);// 在XYMultipleSeriesDataset中添加XYSeries
		co2Renderer = new XYSeriesRenderer();// 定义XYSeriesRenderer

		chartRenderer.addSeriesRenderer(co2Renderer);// 将单个XYSeriesRenderer增加到XYMultipleSeriesRenderer
		co2Renderer.setPointStyle(PointStyle.CIRCLE);// 点的类型是圆形
		co2Renderer.setFillPoints(true);// 设置点是否实心
		co2Renderer.setColor(resources.getColor(R.color.chart_color_co2));
		co2Renderer.setLineWidth(3);
		co2Renderer.setDisplayChartValues(true);
		co2Renderer.setChartValuesTextSize(15);

		temperatureSeries = new XYSeries(
				resources.getString(R.string.temperature), 1);// 定义XYSeries
		chartDataset.addSeries(temperatureSeries);// 在XYMultipleSeriesDataset中添加XYSeries
		temperatureRenderer = new XYSeriesRenderer();// 定义XYSeriesRenderer
		chartRenderer.addSeriesRenderer(temperatureRenderer);// 将单个XYSeriesRenderer增加到XYMultipleSeriesRenderer
		temperatureRenderer.setPointStyle(PointStyle.CIRCLE);// 点的类型是圆形
		temperatureRenderer.setFillPoints(true);// 设置点是否实心
		temperatureRenderer
				.setColor(resources.getColor(R.color.chart_color_temperature));
		temperatureRenderer.setLineWidth(3);

		temperatureRenderer.setDisplayChartValues(true);
		temperatureRenderer.setChartValuesTextSize(15);

		humiditySeries = new XYSeries(resources.getString(R.string.humidity), 2);// 定义XYSeries
		chartDataset.addSeries(humiditySeries);// 在XYMultipleSeriesDataset中添加XYSeries
		humidityRenderer = new XYSeriesRenderer();// 定义XYSeriesRenderer
		chartRenderer.addSeriesRenderer(humidityRenderer);// 将单个XYSeriesRenderer增加到XYMultipleSeriesRenderer
		humidityRenderer.setPointStyle(PointStyle.CIRCLE);// 点的类型是圆形
		humidityRenderer.setPointStrokeWidth(2);
		humidityRenderer.setFillPoints(true);// 设置点是否实心
		humidityRenderer.setColor(resources.getColor(R.color.chart_color_humidity));
		humidityRenderer.setLineWidth(3);
		humidityRenderer.setDisplayChartValues(true);
		humidityRenderer.setChartValuesTextSize(15);

		// FillOutsideLine fill2 = new
		// FillOutsideLine(FillOutsideLine.Type.BOUNDS_ALL);
		// fill2.setColor(Color.argb(60, 255, 255, 255));
		// co2AlarmRenderer.addFillOutsideLine(fill2);

		// FillOutsideLine fill = new
		// FillOutsideLine(FillOutsideLine.Type.BOUNDS_ALL);
		// fill.setColor(Color.argb(60, 0, 255, 0));
		// co2WarningRenderer.addFillOutsideLine(fill);
		String dateFormat = null;

		if (chartType == Constants.ChartSettings.CHART_TYPE_AGGRAGATION) {
			if (chartGranularity == Constants.ChartSettings.GRANULARITY_HOUR) {
				dateFormat = "yyyy/MM/dd-HH:mm:ss";
			} else if (chartGranularity == Constants.ChartSettings.GRANULARITY_HOURS) {
				dateFormat = "yyyy/MM/dd-HH:mm:ss";
			} else if (chartGranularity == Constants.ChartSettings.GRANULARITY_DAY) {
				dateFormat = "yyyy/MM/dd";
			} else if (chartGranularity == Constants.ChartSettings.GRANULARITY_WEEK) {
				dateFormat = "yyyy/MM/dd";
			} else {
				dateFormat = "yyyy/MM";
			}
		} else {
			dateFormat = "HH:mm:ss";
		}

		chartView = IOTChartFactory.getIOTChartView(this, chartDataset,
				chartRenderer, dateFormat, new String[] { "ppm", "℃", "%" });
		chartView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SeriesSelection seriesSelection = chartView.getCurrentSeriesAndPoint();
				if(seriesSelection != null){
					int index = seriesSelection.getSeriesIndex();
					String info;
					if(index == 0){
						info = "CO2:";
					}else if(index == 1){
						info = "Temperature:";
					}else if(index == 2){
						info = "Humidity:";
					}else{
						info = "Unknown";
					}
					
					Toast.makeText(V2SiteDetailActivity.this, info + seriesSelection.getValue(), Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		chartLayout.removeAllViews();
		chartLayout.addView(chartView, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	}

	private void updateChartData() {
		/*
		 * IOTMonitorThreshold warningThreshold = (IOTMonitorThreshold)
		 * ServiceContainer .getInstance().getSessionService()
		 * .getSessionValue(Constants.SessionKey.THRESHOLD_WARNING);
		 * IOTMonitorThreshold breachThreshold = (IOTMonitorThreshold)
		 * ServiceContainer .getInstance().getSessionService()
		 * .getSessionValue(Constants.SessionKey.THRESHOLD_BREACH);
		 * 
		 * // reset target line chartRenderer.clearYTextLabels();
		 * chartRenderer.addYTextLabel(warningThreshold.getCo2UpperBound(),
		 * "warning", 0, getResources().getColor(R.color.status_warning));
		 * chartRenderer.addYTextLabel(breachThreshold.getCo2UpperBound(),
		 * "breach", 0, getResources().getColor(R.color.status_alarm));
		 */
		
		co2Series.clear();
		temperatureSeries.clear();
		humiditySeries.clear();

		Date minTime = new Date();
		Date maxTime = new Date();

		for (IOTSampleData sample : chartData) {
			if (minTime.after(sample.getTime())) {
				minTime = sample.getTime();
			}
			if (maxTime.before(sample.getTime())) {
				maxTime = sample.getTime();
			}
			if (sample.getType() == IOTSampleData.IOTSampleDataType.CO2) {
				double dvalue = (double) (Math.round(sample.getValue() * 100.0) / 100.0);
				co2Series.add(sample.getTime().getTime(), dvalue);
			} else if (sample.getType() == IOTSampleData.IOTSampleDataType.TEMPERATURE) {
				double dvalue = (double) (Math.round(sample.getValue() * 100.0) / 100.0);
				temperatureSeries.add(sample.getTime().getTime(), dvalue);
			} else if (sample.getType() == IOTSampleData.IOTSampleDataType.HUMIDITY) {
				double dvalue = (double) (Math.round(sample.getValue() * 100.0) / 100.0);
				humiditySeries.add(sample.getTime().getTime(), dvalue);
			}

		}

		if (co2Series.getItemCount() > 0) {
			double maxX = co2Series.getMaxX();
			double minX = co2Series.getMinX();

			double maxY0 = co2Series.getMaxY();
			double minY0 = co2Series.getMinY();

			if (Math.abs(maxY0 - minY0) < 20) {
				maxY0 += 10;
				minY0 = minY0 - 10 < 0 ? 0 : minY0 - 10;
			}
			/*
			 * if (maxY0 < breachThreshold.getCo2UpperBound()) { maxY0 =
			 * breachThreshold.getCo2UpperBound() + 10; }
			 * 
			 * if (minY0 > warningThreshold.getCo2UpperBound()) { minY0 =
			 * warningThreshold.getCo2UpperBound() - 10; }
			 */
			chartRenderer.setYAxisMax(maxY0, 0);
			chartRenderer.setYAxisMin(minY0, 0);

		}

		if (temperatureSeries.getItemCount() > 0) {
			double maxY1 = temperatureSeries.getMaxY();
			double minY1 = temperatureSeries.getMinY();

			if (Math.abs(maxY1 - minY1) < 10) {
				maxY1 += 5;
				minY1 = minY1 - 5 < 0 ? 0 : minY1 - 5;
			}
			chartRenderer.setYAxisMax(maxY1, 1);
			chartRenderer.setYAxisMin(minY1, 1);
		}

		if (humiditySeries.getItemCount() > 0) {
			double maxY2 = humiditySeries.getMaxY();
			double minY2 = humiditySeries.getMinY();

			if (Math.abs(maxY2 - minY2) < 20) {
				maxY2 = maxY2 + 10 > 100 ? 100 : maxY2 + 10;
				minY2 = ((minY2 - 10) < 0) ? 0 : minY2 - 10;
			}
			chartRenderer.setYAxisMax(maxY2, 2);
			chartRenderer.setYAxisMin(minY2, 2);

		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		// tvChartTitleBottom.setText(sdf.format(minTime) + " - " +
		// sdf.format(maxTime));
		// tvChartTitle.setText(getChartTitle());
	}

	private void updateChartDataFinished() {
		progressDialog.dismiss();
	}

	private void updateChartDataInProcessing() {
		IOTLog.d("UserSiteHomePageFragment",
				"debuginfo - updateChartDataInProcessing: update chart title in processing");
		showProgressDialog();
	}

	private boolean updateChartData(List<IOTSampleData> samples) {
		createChart();
		chartData = samples;
		Collections.sort(chartData);
		updateChartData();
		chartView.repaint();
		return true;
	}

	private void generateChart() {
		chartData = new ArrayList<IOTSampleData>();
		createChart();
		updateChartData();
		chartView.repaint();
	}

	public void resendMessage() {

		handler.removeMessages(Constants.MessageKey.MESSAGE_GET_CHART_DATA);

		if (chartRC != null) {
			chartRC.cancel();
		}

		//Message msg = new Message();
		//msg.what = Constants.MessageKey.MESSAGE_GET_CHART_DATA;
		// handler.sendMessageDelayed(msg, 2000);
		handler.sendEmptyMessage(Constants.MessageKey.MESSAGE_GET_CHART_DATA);

	}

	private String getChartTitle() {
		if (chartType == Constants.ChartSettings.CHART_TYPE_AGGRAGATION) {
			if (chartGranularity == Constants.ChartSettings.GRANULARITY_HOUR) {
				return "歷史統計(單位:每1小時)";
			} else if (chartGranularity == Constants.ChartSettings.GRANULARITY_HOURS) {
				return "歷史統計(單位:每8小時)";
			} else if (chartGranularity == Constants.ChartSettings.GRANULARITY_DAY) {
				return "歷史統計(單位:每日)";
			} else if (chartGranularity == Constants.ChartSettings.GRANULARITY_WEEK) {
				return "歷史統計(單位:每周)";
			} else {
				return "歷史統計(單位:每月)";
			}
		} else {
			return "即時資料(資料範圍:" + chartTimeDuration + "分鐘)";
		}
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
			now.add(Calendar.DATE, -3);
			result = now.getTime();
			break;
		}
		case TIME_PERIOD_LAST_5DAYS: {
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			now.add(Calendar.DATE, -5);
			result = now.getTime();
			break;
		}
		case TIME_PERIOD_LAST_7DAYS: {
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			now.add(Calendar.DATE, -7);
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
				result = Calendar.getInstance().getTime();
				break;
			}
			case TIME_PERIOD_YESTERDAY: {
				cal.add(Calendar.DATE, 1);
				result = cal.getTime();
				break;
			}
			case TIME_PERIOD_THIS_WEEK: {
				result = Calendar.getInstance().getTime();
				break;
			}
			case TIME_PERIOD_LAST_WEEK: {
				cal.add(Calendar.DATE, 7);
				result = cal.getTime();
				break;
			}
			case TIME_PERIOD_THIS_MONTH: {
				result = Calendar.getInstance().getTime();
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
				cal.add(Calendar.DATE, 7);
				result = cal.getTime();
				break;
			}
			case TIME_PERIOD_LAST_2WEEKS: {
				cal.add(Calendar.DATE, 14);
				result = cal.getTime();
				break;
			}
			case TIME_PERIOD_LAST_3WEEKS: {
				cal.add(Calendar.DATE, 21);
				result = cal.getTime();
				break;
			}
			case TIME_PERIOD_LAST_1MONTH: {
				cal.add(Calendar.MONTH, 1);
				result = cal.getTime();
				break;
			}
			case TIME_PERIOD_LAST_2MONTHS: {
				cal.add(Calendar.MONTH, 2);
				result = cal.getTime();
				break;
			}
			case TIME_PERIOD_LAST_3MONTHS: {
				cal.add(Calendar.MONTH, 3);
				result = cal.getTime();
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
	
	
	private void showProgressDialog() {
		progressDialog = ProgressDialog.show(this, "", getString(R.string.chart_waiting), true);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(true);
		progressDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
			}
		});
	}

	private boolean sendQueryChartDataRequest(String deviceID) {
		if (chartType == Constants.ChartSettings.CHART_TYPE_REALTIME) {
			int pageSize = 12 * chartTimeDuration; // 15s for each type sample,
													// so 12 samples for 3
													// types in 1 min
			if (!isChartRequestHandling) {
				sendQueryRealtimeChartDataRequest(deviceID, pageSize);
			} else {
				return false;
			}
		} else if (chartType == Constants.ChartSettings.CHART_TYPE_AGGRAGATION) {
			if (chartGranularity == Constants.ChartSettings.GRANULARITY_MINUTE) {
				sendQuery10MinutesAggrChartDataRequest(deviceID, chartStartTime,
						chartEndTime);
			} else if (chartGranularity == Constants.ChartSettings.GRANULARITY_QUARTER) {
				sendQuery1QuarterAggrChartDataRequest(deviceID, chartStartTime,
						chartEndTime);
			} else if (chartGranularity == Constants.ChartSettings.GRANULARITY_HOUR) {
				sendQuery1HourAggrChartDataRequest(deviceID, chartStartTime,
						chartEndTime);
			} else if (chartGranularity == Constants.ChartSettings.GRANULARITY_HOURS) {
				sendQuery8HoursAggrChartDataRequest(deviceID, chartStartTime,
						chartEndTime);
			} else if (chartGranularity == Constants.ChartSettings.GRANULARITY_DAY) {
				sendQueryDayAggrChartDataRequest(deviceID, chartStartTime,
						chartEndTime);
			} else if (chartGranularity == Constants.ChartSettings.GRANULARITY_WEEK) {
				sendQueryWeekAggrChartDataRequest(deviceID, chartStartTime,
						chartEndTime);
			} else if (chartGranularity == Constants.ChartSettings.GRANULARITY_MONTH) {
				sendQueryMonthAggrChartDataRequest(deviceID, chartStartTime,
						chartEndTime);
			} else {
				return false;
			}
		}
		return true;
	}

	private void sendQueryRealtimeChartDataRequest(String deviceID, int pageSize) {
		isChartRequestHandling = true;

		HttpRequest request = new NoneAuthedHttpRequest(
				new HttpConfig.GetHttpConfig(),
				Constants.ServerAPIURI.GET_SAMPLE_DATA);
		String sessionID = ServiceContainer.getInstance().getSessionService()
				.getSessionID();
		request.addParameter("dataType", "xml");
		request.addParameter("__session_id", sessionID);
		request.addParameter("__page_no", "1");
		request.addParameter("__column", "name,value,t");
		request.addParameter("__page_size", Integer.toString(pageSize));
		request.addParameter("__group_by", "did,name");
		request.addParameter("__sort", "-id");
		request.addParameter("did[0]", deviceID);
		GetRealtimeChartDataListener l = new GetRealtimeChartDataListener(
				deviceID);

		ServiceContainer.getInstance().getHttpHandler().doRequest(request, l);
	}

	private void sendQuery10MinutesAggrChartDataRequest(String deviceID,
			Date startTime, Date endTime) {

		String from = ReportUtil.getServerTimeHourString(startTime);
		String to = ReportUtil.getServerTimeHourString(endTime);

		HttpRequest request = new NoneAuthedHttpRequest(
				new HttpConfig.GetHttpConfig(),
				Constants.ServerAPIURI.GET_HOUR_AGG_DATA);
		String sessionID = ServiceContainer.getInstance().getSessionService()
				.getSessionID();
		request.addParameter("dataType", "xml");
		request.addParameter("__session_id", sessionID);
		request.addParameter("__page_no", "1");
		request.addParameter("__page_size", "10000");
		request.addParameter("__column",
				"did%2Csensor%2Cname%2Cvalue%2Chour_in_epoch");
		request.addParameter("__group_by",
				"did%2Csensor%2Cname%2Chour_in_epoch");
		request.addParameter("__sort", "-id");
		request.addParameter("hour_in_epoch__from", from);
		request.addParameter("hour_in_epoch__to", to);
		request.addParameter("did%5B0%5D", deviceID);
		GetAggrChartDataListener l = new GetAggrChartDataListener(deviceID,
				from, to);

		ServiceContainer.getInstance().getHttpHandler().doRequest(request, l);
	}
	
	private void sendQuery1QuarterAggrChartDataRequest(String deviceID,
			Date startTime, Date endTime) {

		String from = ReportUtil.getServerTimeHourString(startTime);
		String to = ReportUtil.getServerTimeHourString(endTime);

		HttpRequest request = new NoneAuthedHttpRequest(
				new HttpConfig.GetHttpConfig(),
				Constants.ServerAPIURI.GET_HOUR_AGG_DATA);
		String sessionID = ServiceContainer.getInstance().getSessionService()
				.getSessionID();
		request.addParameter("dataType", "xml");
		request.addParameter("__session_id", sessionID);
		request.addParameter("__page_no", "1");
		request.addParameter("__page_size", "10000");
		request.addParameter("__column",
				"did%2Csensor%2Cname%2Cvalue%2Chour_in_epoch");
		request.addParameter("__group_by",
				"did%2Csensor%2Cname%2Chour_in_epoch");
		request.addParameter("__sort", "-id");
		request.addParameter("hour_in_epoch__from", from);
		request.addParameter("hour_in_epoch__to", to);
		request.addParameter("did%5B0%5D", deviceID);
		GetAggrChartDataListener l = new GetAggrChartDataListener(deviceID,
				from, to);

		ServiceContainer.getInstance().getHttpHandler().doRequest(request, l);
	}
	
	private void sendQuery1HourAggrChartDataRequest(String deviceID,
			Date startTime, Date endTime) {

		String from = ReportUtil.getServerTimeHourString(startTime);
		String to = ReportUtil.getServerTimeHourString(endTime);

		HttpRequest request = new NoneAuthedHttpRequest(
				new HttpConfig.GetHttpConfig(),
				Constants.ServerAPIURI.GET_HOUR_AGG_DATA);
		String sessionID = ServiceContainer.getInstance().getSessionService()
				.getSessionID();
		request.addParameter("dataType", "xml");
		request.addParameter("__session_id", sessionID);
		request.addParameter("__page_no", "1");
		request.addParameter("__page_size", "10000");
		request.addParameter("__column",
				"did%2Csensor%2Cname%2Cvalue%2Chour_in_epoch");
		request.addParameter("__group_by",
				"did%2Csensor%2Cname%2Chour_in_epoch");
		request.addParameter("__sort", "-id");
		request.addParameter("hour_in_epoch__from", from);
		request.addParameter("hour_in_epoch__to", to);
		request.addParameter("did%5B0%5D", deviceID);
		GetAggrChartDataListener l = new GetAggrChartDataListener(deviceID,
				from, to);

		ServiceContainer.getInstance().getHttpHandler().doRequest(request, l);
	}

	private void sendQuery8HoursAggrChartDataRequest(String deviceID,
			Date startTime, Date endTime) {

		String from = ReportUtil.getServerTimeHourString(startTime);
		String to = ReportUtil.getServerTimeHourString(endTime);

		HttpRequest request = new NoneAuthedHttpRequest(
				new HttpConfig.GetHttpConfig(),
				Constants.ServerAPIURI.GET_HOURS_AGG_DATA);
		String sessionID = ServiceContainer.getInstance().getSessionService()
				.getSessionID();
		request.addParameter("dataType", "xml");
		request.addParameter("__session_id", sessionID);
		request.addParameter("__page_no", "1");
		request.addParameter("__page_size", "10000");
		request.addParameter("__column",
				"did%2Csensor%2Cname%2Cvalue%2Chours_in_epoch");
		request.addParameter("__group_by",
				"did%2Csensor%2Cname%2Chours_in_epoch");
		request.addParameter("__sort", "-id");
		request.addParameter("hours_in_epoch__from", from);
		request.addParameter("hours_in_epoch__to", to);
		request.addParameter("did%5B0%5D", deviceID);
		GetAggrChartDataListener l = new GetAggrChartDataListener(deviceID,
				from, to);

		ServiceContainer.getInstance().getHttpHandler().doRequest(request, l);
	}

	private void sendQueryDayAggrChartDataRequest(String deviceID,
			Date startTime, Date endTime) {

		String from = ReportUtil.getServerTimeDayString(startTime);
		String to = ReportUtil.getServerTimeDayString(endTime);

		HttpRequest request = new NoneAuthedHttpRequest(
				new HttpConfig.GetHttpConfig(),
				Constants.ServerAPIURI.GET_DAY_AGG_DATA);
		String sessionID = ServiceContainer.getInstance().getSessionService()
				.getSessionID();
		request.addParameter("dataType", "xml");
		request.addParameter("__session_id", sessionID);
		request.addParameter("__page_no", "1");
		request.addParameter("__page_size", "10000");
		request.addParameter("__column",
				"did%2Csensor%2Cname%2Cvalue%2Cday_in_epoch");
		request.addParameter("__group_by", "did%2Csensor%2Cname%2Cday_in_epoch");
		request.addParameter("__sort", "-id");
		request.addParameter("day_in_epoch__from", from);
		request.addParameter("day_in_epoch__to", to);
		request.addParameter("did%5B0%5D", deviceID);
		GetAggrChartDataListener l = new GetAggrChartDataListener(deviceID,
				from, to);

		ServiceContainer.getInstance().getHttpHandler().doRequest(request, l);
	}

	private void sendQueryWeekAggrChartDataRequest(String deviceID,
			Date startTime, Date endTime) {

		String from = ReportUtil.getServerTimeDayString(startTime);
		String to = ReportUtil.getServerTimeDayString(endTime);

		HttpRequest request = new NoneAuthedHttpRequest(
				new HttpConfig.GetHttpConfig(),
				Constants.ServerAPIURI.GET_WEEK_AGG_DATA);
		String sessionID = ServiceContainer.getInstance().getSessionService()
				.getSessionID();
		request.addParameter("dataType", "xml");
		request.addParameter("__session_id", sessionID);
		request.addParameter("__page_no", "1");
		request.addParameter("__page_size", "10000");
		request.addParameter("__column",
				"did%2Csensor%2Cname%2Cvalue%2Cweek_in_epoch");
		request.addParameter("__group_by",
				"did%2Csensor%2Cname%2Cweek_in_epoch");
		request.addParameter("__sort", "-id");
		request.addParameter("week_in_epoch__from", from);
		request.addParameter("week_in_epoch__to", to);
		request.addParameter("did%5B0%5D", deviceID);
		GetAggrChartDataListener l = new GetAggrChartDataListener(deviceID,
				from, to);

		ServiceContainer.getInstance().getHttpHandler().doRequest(request, l);
	}

	private void sendQueryMonthAggrChartDataRequest(String deviceID,
			Date startTime, Date endTime) {

		String from = ReportUtil.getServerTimeDayString(startTime);
		String to = ReportUtil.getServerTimeDayString(endTime);

		HttpRequest request = new NoneAuthedHttpRequest(
				new HttpConfig.GetHttpConfig(),
				Constants.ServerAPIURI.GET_MONTH_AGG_DATA);
		String sessionID = ServiceContainer.getInstance().getSessionService()
				.getSessionID();
		request.addParameter("dataType", "xml");
		request.addParameter("__session_id", sessionID);
		request.addParameter("__page_no", "1");
		request.addParameter("__page_size", "10000");
		request.addParameter("__column",
				"did%2Csensor%2Cname%2Cvalue%2Cmonth_in_epoch");
		request.addParameter("__group_by",
				"did%2Csensor%2Cname%2Cmonth_in_epoch");
		request.addParameter("__sort", "-id");
		request.addParameter("month_in_epoch__from", from);
		request.addParameter("month_in_epoch__to", to);
		request.addParameter("did%5B0%5D", deviceID);
		GetAggrChartDataListener l = new GetAggrChartDataListener(deviceID,
				from, to);

		ServiceContainer.getInstance().getHttpHandler().doRequest(request, l);
	}

	private class GetRealtimeChartDataListener implements
			RequestListener<List<IOTSampleData>> {
		private RequestControl control;
		private String deviceID;

		public GetRealtimeChartDataListener(String deviceID) {
			this.deviceID = deviceID;
		}

		@Override
		public void onRequestCancelled() {
			if (control != null)
				control.cancel();

		}

		@Override
		public void onRequestResult(final List<IOTSampleData> result) {
			IOTLog.d("GetRealtimeChartDataListener",
					"debuginfo(CHART_DATA) - onRequestResult: receive response for request:"
							+ deviceID);
			if (currentSite != null
					&& currentSite.getDevice().getDeviceID().equals(deviceID)) {
				Message msg = new Message();
				msg.what = Constants.MessageKey.MESSAGE_UPDATE_CHART_DATA;
				msg.obj = result;
				handler.sendMessage(msg);
			}

		}

		@Override
		public void onRequestGetControl(RequestControl control) {
			this.control = control;
			chartRC = control;
		}

		@Override
		public void onRequestStart() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRequestError(Exception e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRequestComplete() {
			isChartRequestHandling = false;
			chartRC = null;

			IOTLog.d(
					"GetRealtimeChartDataListener",
					"debuginfo(CHART_DATA) - onRequestComplete: set isChartRequestHandling is false");

			Message msg = new Message();
			msg.what = Constants.MessageKey.MESSAGE_GET_CHART_DATA;

			int refreshTime = (Integer) ServiceContainer
					.getInstance()
					.getSessionService()
					.getSessionValue(
							Constants.SessionKey.REALTIME_DATA_MONITOR_REFRESH_TIME,
							30);
			IOTLog.d(
					"GetRealtimeChartDataListener",
					"debuginfo(CHART_DATA) - onRequestComplete: send message MESSAGE_GET_CHART_DATA for "
							+ deviceID
							+ " with deplay time:"
							+ refreshTime
							+ "s");
			handler.sendMessageDelayed(msg, refreshTime * 1000);

		}
	}

	private class GetAggrChartDataListener implements
			RequestListener<Map<String, IOTMonitorData>> {
		private RequestControl control;
		private String deviceID;
		private String from;
		private String to;

		public GetAggrChartDataListener(String deviceID, String from, String to) {
			this.deviceID = deviceID;
			this.from = from;
			this.to = to;
		}

		@Override
		public void onRequestCancelled() {
			if (control != null)
				control.cancel();

		}

		@Override
		public void onRequestResult(final Map<String, IOTMonitorData> result) {
			IOTLog.d("GetAggrChartDataListener",
					"debuginfo(CHART_DATA) - onRequestResult: receive response for request:"
							+ deviceID);
			final List<IOTSampleData> chartData = convertToChartData(result);

			if (currentSite != null
					&& currentSite.getDevice().getDeviceID().equals(deviceID)) {
				Message msg = new Message();
				msg.what = Constants.MessageKey.MESSAGE_UPDATE_CHART_DATA;
				msg.obj = chartData;
				handler.sendMessage(msg);
			}

		}

		@Override
		public void onRequestGetControl(RequestControl control) {
			this.control = control;
			chartRC = control;
		}

		@Override
		public void onRequestStart() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRequestError(Exception e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRequestComplete() {
			chartRC = null;
		}
	}

	private List<IOTSampleData> convertToChartData(
			Map<String, IOTMonitorData> result) {
		List<IOTSampleData> chartData = new ArrayList<IOTSampleData>();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Set<String> keys = result.keySet();
		for (String key : keys) {
			IOTMonitorData data = result.get(key);
			float co2 = data.getCo2();
			float temperature = data.getTemperature();
			float humidity = data.getHumidity();
			Date time = null;
			try {
				time = ReportUtil.getLocalTime(sdf.parse(key));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (time != null) {
				IOTSampleData sample1 = new IOTSampleData(
						IOTSampleData.IOTSampleDataType.CO2, time, co2);
				IOTSampleData sample2 = new IOTSampleData(
						IOTSampleData.IOTSampleDataType.TEMPERATURE, time,
						temperature);
				IOTSampleData sample3 = new IOTSampleData(
						IOTSampleData.IOTSampleDataType.HUMIDITY, time,
						humidity);

				chartData.add(sample1);
				chartData.add(sample2);
				chartData.add(sample3);
			}
		}

		return chartData;
	}

}
