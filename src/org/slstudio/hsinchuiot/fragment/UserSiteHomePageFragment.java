package org.slstudio.hsinchuiot.fragment;

import java.util.Date;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.TimeChart;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.slstudio.hsinchuiot.Constants;
import org.slstudio.hsinchuiot.R;
import org.slstudio.hsinchuiot.UserMainActivity;
import org.slstudio.hsinchuiot.model.IOTMonitorData;
import org.slstudio.hsinchuiot.model.IOTMonitorThreshold;
import org.slstudio.hsinchuiot.model.IOTSampleData;
import org.slstudio.hsinchuiot.model.Site;
import org.slstudio.hsinchuiot.service.ServiceContainer;
import org.slstudio.hsinchuiot.ui.LableTimeChart;
import org.slstudio.hsinchuiot.ui.chart.IOTChartFactory;
import org.slstudio.hsinchuiot.util.IOTLog;
import org.slstudio.hsinchuiot.util.ReportUtil;
import org.slstudio.hsinchuiot.widget.CurtainMenu;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class UserSiteHomePageFragment extends Fragment {

	private Site site;
	private int index;

	private Button btnSettings;
	private Button btnLogoff;
	
	private CurtainMenu menu;

	private PullToRefreshScrollView refreshScrollView;
	
	private ImageButton btnPreSite;
	private ImageButton btnNextSite;
	private Button btnTitle;

	private View layoutMonitor;
	private TextView tvCO2Value;
	private TextView tvTemperatureValue;
	private TextView tvHumidityValue;
	private ImageView ivCO2Status;

	private XYMultipleSeriesDataset chartDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer chartRenderer = new XYMultipleSeriesRenderer(3);
	private XYSeries co2Series;
	private XYSeriesRenderer co2Renderer;
	private XYSeries temperatureSeries;
	private XYSeriesRenderer temperatureRenderer;
	private XYSeries humiditySeries;
	private XYSeriesRenderer humidityRenderer;

	private GraphicalView chartView;
	private LinearLayout chartLayout;
	private TextView tvChartTitle;
	private TextView tvChartTitleBottom;
	private Button btnChartSettings;

	private List<IOTSampleData> chartData = new ArrayList<IOTSampleData>();

	public UserSiteHomePageFragment() {
		super();
	}

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
		View view = inflater.inflate(R.layout.fragment_user_site_homepage, container, false);
		initViews(view);

		updateMonitorData();

		createChart();
		updateChartData();

		chartView.repaint();

		return view;
	}

	@Override
	public void onDestroyView() {
		destroyChart();
		super.onDestroyView();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (chartView != null) {
			chartView.repaint();
		}

	}

	@SuppressLint("NewApi")
	public boolean updateMonitorData(IOTMonitorData data) {
		if (site != null && getActivity() != null && !getActivity().isDestroyed()) {
			site.setMonitorData(data);
		}
		updateMonitorData();
		
		if (refreshScrollView.isRefreshing()) {
			refreshScrollView.onRefreshComplete();
		}
		
		return true;
	}

	public void updateChartDataFinished() {
		IOTLog.d("UserSiteHomePageFragment", "debuginfo - updateChartDataFinished: update chart title proceed");
		tvChartTitle.setText(getChartTitle());
	}

	public void updateChartDataInProcessing() {
		IOTLog.d("UserSiteHomePageFragment",
				"debuginfo - updateChartDataInProcessing: update chart title in processing");
		String title = getChartTitle();
		tvChartTitle.setText(title + " - 正在獲取數據...");
	}

	@SuppressLint("NewApi")
	private void updateMonitorData() {
		if (site != null && getActivity() != null && !getActivity().isDestroyed()) {

			IOTMonitorThreshold warningThreshold = (IOTMonitorThreshold) ServiceContainer.getInstance()
					.getSessionService().getSessionValue(Constants.SessionKey.THRESHOLD_WARNING);
			IOTMonitorThreshold breachThreshold = (IOTMonitorThreshold) ServiceContainer.getInstance()
					.getSessionService().getSessionValue(Constants.SessionKey.THRESHOLD_BREACH);

			btnTitle.setText(site.getSiteName());

			int alarm = getActivity().getResources().getColor(R.color.status_alarm);
			int warning = getActivity().getResources().getColor(R.color.status_warning);
			int normal = getActivity().getResources().getColor(R.color.status_ok);

			IOTMonitorData data = site.getMonitorData();

			if (breachThreshold != null && data.isCO2Breach(breachThreshold)) {
				tvCO2Value.setTextColor(alarm);
				ivCO2Status.setImageResource(R.drawable.status_co2_alarm);
			} else if (warningThreshold != null && data.isCO2Breach(warningThreshold)) {
				tvCO2Value.setTextColor(warning);
				ivCO2Status.setImageResource(R.drawable.status_co2_warning);
			} else {
				tvCO2Value.setTextColor(normal);
				ivCO2Status.setImageResource(R.drawable.status_co2_ok);
			}
			tvCO2Value.setText(Float.toString(data.getCo2()));

			if (breachThreshold != null && data.isTemperatureBreach(breachThreshold)) {
				tvTemperatureValue.setTextColor(alarm);
			} else if (warningThreshold != null && data.isTemperatureBreach(warningThreshold)) {
				tvTemperatureValue.setTextColor(warning);
			} else {
				tvTemperatureValue.setTextColor(normal);
			}
			tvTemperatureValue.setText(Float.toString(site.getMonitorData().getTemperature()));

			if (breachThreshold != null && data.isHumidityBreach(breachThreshold)) {
				tvHumidityValue.setTextColor(alarm);
			} else if (warningThreshold != null && data.isHumidityBreach(warningThreshold)) {
				tvHumidityValue.setTextColor(warning);
			} else {
				tvHumidityValue.setTextColor(normal);
			}
			tvHumidityValue.setText(Float.toString(site.getMonitorData().getHumidity()));

		}
	}

	private String getChartTitle() {
		UserMainActivity parent = (UserMainActivity) getActivity();
		if (parent.getChartType() == Constants.ChartSettings.CHART_TYPE_AGGRAGATION) {
			if (parent.getChartGranularity() == Constants.ChartSettings.GRANULARITY_HOUR) {
				return "歷史統計(單位:每1小時)";
			} else if (parent.getChartGranularity() == Constants.ChartSettings.GRANULARITY_HOURS) {
				return "歷史統計(單位:每8小時)";
			} else if (parent.getChartGranularity() == Constants.ChartSettings.GRANULARITY_DAY) {
				return "歷史統計(單位:每日)";
			} else if (parent.getChartGranularity() == Constants.ChartSettings.GRANULARITY_WEEK) {
				return "歷史統計(單位:每周)";
			} else {
				return "歷史統計(單位:每月)";
			}
		} else {
			return "即時資料(資料範圍:" + parent.getChartTimeDuration() + "分鐘)";
		}
	}

	public boolean updateChartData(List<IOTSampleData> samples) {
		chartData = samples;
		updateChartData();
		chartView.repaint();
		return true;
	}

	public void generateChart() {
		chartData = new ArrayList<IOTSampleData>();
		createChart();
		updateChartData();
		chartView.repaint();
	}
	
	public void onMenuRopeClick(){
		menu.onRopeClick();
	}
	

	private void updateChartData() {

		IOTMonitorThreshold warningThreshold = (IOTMonitorThreshold) ServiceContainer.getInstance().getSessionService()
				.getSessionValue(Constants.SessionKey.THRESHOLD_WARNING);
		IOTMonitorThreshold breachThreshold = (IOTMonitorThreshold) ServiceContainer.getInstance().getSessionService()
				.getSessionValue(Constants.SessionKey.THRESHOLD_BREACH);

		chartRenderer.clearYTextLabels();
		chartRenderer.addYTextLabel(warningThreshold.getCo2UpperBound(),"warning",0, getActivity().getResources().getColor(R.color.status_warning));
		chartRenderer.addYTextLabel(breachThreshold.getCo2UpperBound(),"breach",0, getActivity().getResources().getColor(R.color.status_alarm));
		
		co2Series.clear();
		temperatureSeries.clear();
		humiditySeries.clear();

		Date minTime = new Date();
		Date maxTime = new Date();

		Collections.sort(chartData);
		
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

			/*
			double interval = (maxX - minX) / 80;
			for (double x = minX; x < maxX; x = x + interval) {
				co2WarningSeries.add(x, warningThreshold.getCo2UpperBound());
				co2AlarmSeries.add(x, breachThreshold.getCo2UpperBound());
			}
			*/
			double maxY0 = co2Series.getMaxY();
			double minY0 = co2Series.getMinY();

			if (Math.abs(maxY0 - minY0) < 20) {
				maxY0 += 10;
				minY0 = minY0 - 10 < 0 ? 0 : minY0 - 10;
			}

			if (maxY0 < breachThreshold.getCo2UpperBound()) {
				maxY0 = breachThreshold.getCo2UpperBound() + 10;
			}

			if (minY0 > warningThreshold.getCo2UpperBound()) {
				minY0 = warningThreshold.getCo2UpperBound() - 10;
			}

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
		tvChartTitleBottom.setText(sdf.format(minTime) + " - " + sdf.format(maxTime));

		tvChartTitle.setText(getChartTitle());
	}

	private void initViews(View parentView) {
		final UserMainActivity parentActivity = (UserMainActivity) this.getActivity();

		btnPreSite = (ImageButton) parentView.findViewById(R.id.ib_pre_site);
		if (index == 0) {
			btnPreSite.setVisibility(View.INVISIBLE);
		} else {
			btnPreSite.setVisibility(View.VISIBLE);
		}
		btnPreSite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				parentActivity.moveToPreSite();
			}

		});

		btnNextSite = (ImageButton) parentView.findViewById(R.id.ib_next_site);
		if (index == parentActivity.getSiteList().size() - 1) {
			btnNextSite.setVisibility(View.INVISIBLE);
		} else {
			btnNextSite.setVisibility(View.VISIBLE);
		}
		btnNextSite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				parentActivity.moveToNextSite();
			}

		});

		btnTitle = (Button) parentView.findViewById(R.id.btn_userhome_title);
		btnTitle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Constants.Action.HSINCHUIOT_USER_SITEDETAIL);
				intent.putExtra(Constants.ActivityPassValue.SELECTED_SITE, site);

				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.zoom_in2,
						R.anim.zoom_out2);
			}
		});

		layoutMonitor = parentView.findViewById(R.id.layout_monitor);
		layoutMonitor.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Constants.Action.HSINCHUIOT_USER_SITEDETAIL);
				intent.putExtra(Constants.ActivityPassValue.SELECTED_SITE, site);

				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.zoom_in2,
						R.anim.zoom_out2);
			}
		});

		Typeface typeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/DS-DIGIB.TTF");

		tvCO2Value = (TextView) parentView.findViewById(R.id.tv_userhome_co2value);
		tvCO2Value.setTypeface(typeFace);

		tvTemperatureValue = (TextView) parentView.findViewById(R.id.tv_userhome_temperaturevalue);
		tvTemperatureValue.setTypeface(typeFace);

		tvHumidityValue = (TextView) parentView.findViewById(R.id.tv_userhome_humidityvalue);
		tvHumidityValue.setTypeface(typeFace);

		ivCO2Status = (ImageView) parentView.findViewById(R.id.iv_userhome_co2_status);

		chartLayout = (LinearLayout) parentView.findViewById(R.id.id_chart);
		tvChartTitle = (TextView) parentView.findViewById(R.id.tv_chart_title);
		tvChartTitleBottom = (TextView) parentView.findViewById(R.id.tv_chart_bottom_title);
		btnChartSettings = (Button) parentView.findViewById(R.id.btn_chart_settings);

		btnChartSettings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(Constants.Action.HSINCHUIOT_USER_CHART_SETTINGS);
				intent.putExtra(Constants.ActivityPassValue.CHART_TYPE, parentActivity.getChartType());
				if (parentActivity.getChartType() == Constants.ChartSettings.CHART_TYPE_REALTIME) {
					intent.putExtra(Constants.ActivityPassValue.CHART_RT_DURATION,
							parentActivity.getChartTimeDuration());
				} else if (parentActivity.getChartType() == Constants.ChartSettings.CHART_TYPE_AGGRAGATION) {
					intent.putExtra(Constants.ActivityPassValue.CHART_AGGR_GRANULARITY,
							parentActivity.getChartGranularity());
					if (parentActivity.getChartStartTime() != null) {
						intent.putExtra(Constants.ActivityPassValue.CHART_AGGR_STARTTIME,
								parentActivity.getChartStartTime().getTime());
					}
					if (parentActivity.getChartEndTime() != null) {
						intent.putExtra(Constants.ActivityPassValue.CHART_AGGR_ENDTIME,
								parentActivity.getChartEndTime().getTime());
					}
				}
				getActivity().startActivityForResult(intent, Constants.ResultCode.CHART_SETTINGS);
			}

		});
		
		refreshScrollView = (PullToRefreshScrollView) parentView.findViewById(R.id.pull_refresh_scrollview_user_main);
		refreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				parentActivity.resendMessage();

			}
		});
		
		menu = (CurtainMenu)parentView.findViewById(R.id.layout_curtain_user_main);
		
		
		btnSettings = (Button)parentView.findViewById(R.id.curtainmenu_btn_settings_user_main);
		btnSettings.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((UserMainActivity)getActivity()).showSettingsActivity();
			}
			
		});
		
		btnLogoff = (Button)parentView.findViewById(R.id.curtainmenu_btn_logoff_user_main);
		btnLogoff.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((UserMainActivity)getActivity()).logoff();
			}
		});

	}

	private void createChart() {
		chartDataset.clear();
		chartRenderer.removeAllRenderers();

		Resources resources = getActivity().getResources();

		chartRenderer.setApplyBackgroundColor(true);// 设置是否显示背景色
		chartRenderer.setBackgroundColor(resources.getColor(R.color.white));// 设置背景色
		chartRenderer.setMargins(new int[] { 20, 60, 20, 30 });// 设置图表的外边框(上/左/下/右)
		chartRenderer.setMarginsColor(resources.getColor(R.color.white));

		chartRenderer.setChartTitleTextSize(0);// ?设置整个图表标题文字大小
		// chartRenderer.setChartTitle("2015/07/20 15:00:00 - 2015/07/20
		// 16:00:00");

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

		chartRenderer.setYLabelsColor(0, resources.getColor(R.color.title_bk_green));
		chartRenderer.setYAxisAlign(Align.LEFT, 0);
		chartRenderer.setYLabelsAlign(Align.RIGHT, 0);
		// chartRenderer.setYAxisMax(600, 0);
		// chartRenderer.setYAxisMin(500, 0);
		chartRenderer.setYLabelsColor(1, resources.getColor(R.color.title_bk_brown));
		chartRenderer.setYAxisAlign(Align.RIGHT, 1);
		chartRenderer.setYLabelsAlign(Align.RIGHT, 1);
		// chartRenderer.setYAxisMax(40, 1);
		// chartRenderer.setYAxisMin(0, 1);

		chartRenderer.setYLabelsColor(2, resources.getColor(R.color.title_bk_purple));
		chartRenderer.setYAxisAlign(Align.RIGHT, 2);
		chartRenderer.setYLabelsAlign(Align.LEFT, 2);
		
		// chartRenderer.setYAxisMax(100, 2);
		// chartRenderer.setYAxisMin(0, 2);

		chartRenderer.setZoomButtonsVisible(false);// 是否显示放大缩小按钮
		chartRenderer.setPointSize(3);// 设置点的大小(图上显示的点的大小和图例中点的大小都会被设置)
		chartRenderer.setPanEnabled(false);
		chartRenderer.setClickEnabled(true);
		
		chartRenderer.setShowCustomTextTargetLineY(true);
		chartRenderer.setFillTargetLineWithColor(true);
		

		co2Series = new XYSeries(resources.getString(R.string.co2), 0);// 定义XYSeries
		chartDataset.addSeries(co2Series);// 在XYMultipleSeriesDataset中添加XYSeries
		co2Renderer = new XYSeriesRenderer();// 定义XYSeriesRenderer

		chartRenderer.addSeriesRenderer(co2Renderer);// 将单个XYSeriesRenderer增加到XYMultipleSeriesRenderer
		co2Renderer.setPointStyle(PointStyle.CIRCLE);// 点的类型是圆形
		co2Renderer.setFillPoints(true);// 设置点是否实心
		co2Renderer.setColor(resources.getColor(R.color.title_bk_green));
		co2Renderer.setLineWidth(3);
		co2Renderer.setDisplayChartValues(true);
		

		temperatureSeries = new XYSeries(resources.getString(R.string.temperature), 1);// 定义XYSeries
		chartDataset.addSeries(temperatureSeries);// 在XYMultipleSeriesDataset中添加XYSeries
		temperatureRenderer = new XYSeriesRenderer();// 定义XYSeriesRenderer
		chartRenderer.addSeriesRenderer(temperatureRenderer);// 将单个XYSeriesRenderer增加到XYMultipleSeriesRenderer
		temperatureRenderer.setPointStyle(PointStyle.CIRCLE);// 点的类型是圆形
		temperatureRenderer.setFillPoints(true);// 设置点是否实心
		temperatureRenderer.setColor(resources.getColor(R.color.title_bk_brown));
		temperatureRenderer.setLineWidth(3);
		temperatureRenderer.setDisplayChartValues(true);

		humiditySeries = new XYSeries(resources.getString(R.string.humidity), 2);// 定义XYSeries
		chartDataset.addSeries(humiditySeries);// 在XYMultipleSeriesDataset中添加XYSeries
		humidityRenderer = new XYSeriesRenderer();// 定义XYSeriesRenderer
		chartRenderer.addSeriesRenderer(humidityRenderer);// 将单个XYSeriesRenderer增加到XYMultipleSeriesRenderer
		humidityRenderer.setPointStyle(PointStyle.CIRCLE);// 点的类型是圆形
		humidityRenderer.setPointStrokeWidth(2);
		humidityRenderer.setFillPoints(true);// 设置点是否实心
		humidityRenderer.setColor(resources.getColor(R.color.title_bk_purple));
		humidityRenderer.setLineWidth(3);
		humidityRenderer.setDisplayChartValues(true);


		/*
		 * Calendar now = Calendar.getInstance(); Calendar lastHour =
		 * Calendar.getInstance(); lastHour.add(Calendar.HOUR, -1);
		 * 
		 * Random r = new Random();
		 * 
		 * for (int i = 0; i < 60; i++) { Calendar c = Calendar.getInstance();
		 * c.add(Calendar.HOUR, -1); c.add(Calendar.MINUTE, i);
		 * co2WarningSeries.add(c.getTime().getTime() , 800);
		 * co2Series.add(c.getTime().getTime() , Math.abs(r.nextInt() % 2000));
		 * temperatureSeries.add(c.getTime().getTime(),
		 * Math.abs(r.nextInt(4000)) / 100.0f);
		 * humiditySeries.add(c.getTime().getTime(), Math.abs(r.nextInt(10000))
		 * / 100.0f); }
		 */

		UserMainActivity parent = (UserMainActivity) getActivity();

		String dateFormat = null;
		
		if (parent.getChartType() == Constants.ChartSettings.CHART_TYPE_AGGRAGATION) {
			if (parent.getChartGranularity() == Constants.ChartSettings.GRANULARITY_HOUR) {
				dateFormat = "yyyy/MM/dd-HH:mm:ss";
			} else if (parent.getChartGranularity() == Constants.ChartSettings.GRANULARITY_HOURS) {
				dateFormat = "yyyy/MM/dd-HH:mm:ss";
			} else if (parent.getChartGranularity() == Constants.ChartSettings.GRANULARITY_DAY) {
				dateFormat = "yyyy/MM/dd";
			} else if (parent.getChartGranularity() == Constants.ChartSettings.GRANULARITY_WEEK) {
				dateFormat = "yyyy/MM/dd";
			} else {
				dateFormat = "yyyy/MM";
			}
		} else {
			dateFormat = "HH:mm:ss";
		}
		
		
		
		chartView = IOTChartFactory.getIOTChartView(getActivity(), chartDataset, chartRenderer, dateFormat, new String[]{"ppm","℃", "%"});
		
		chartLayout.removeAllViews();
		chartLayout.addView(chartView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		chartLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				UserMainActivity parent = (UserMainActivity) getActivity();

				Intent intent = new Intent(Constants.Action.HSINCHUIOT_USER_CHARTDETAIL);

				intent.putExtra(Constants.ActivityPassValue.SELECTED_SITE, site);
				intent.putExtra(Constants.ActivityPassValue.CHART_TYPE, parent.getChartType());
				intent.putExtra(Constants.ActivityPassValue.CHART_DATA, (Serializable) chartData);
				intent.putExtra(Constants.ActivityPassValue.CHART_RT_DURATION, parent.getChartTimeDuration());
				intent.putExtra(Constants.ActivityPassValue.CHART_AGGR_GRANULARITY, parent.getChartGranularity());
				intent.putExtra(Constants.ActivityPassValue.CHART_AGGR_STARTTIME, parent.getChartStartTime()==null?0: parent.getChartStartTime().getTime());
				intent.putExtra(Constants.ActivityPassValue.CHART_AGGR_ENDTIME, parent.getChartEndTime()==null?0:parent.getChartEndTime().getTime());

				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.zoom_in,
						R.anim.zoom_out);

			}
		});
	}

	private void destroyChart() {
		chartDataset.clear();
		chartRenderer.removeAllRenderers();
	}
}
