package org.slstudio.hsinchuiot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.slstudio.hsinchuiot.model.IOTMonitorThreshold;
import org.slstudio.hsinchuiot.model.IOTSampleData;
import org.slstudio.hsinchuiot.model.Site;
import org.slstudio.hsinchuiot.service.ServiceContainer;
import org.slstudio.hsinchuiot.ui.LableTimeChart;
import org.slstudio.hsinchuiot.ui.chart.IOTChartFactory;
import org.slstudio.hsinchuiot.util.IOTLog;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class UserChartDetailActivity extends BaseActivity {

	private Handler handler;

	private ActionBar actionBar;

	private Site currentSite;
	private int chartType = Constants.ChartSettings.CHART_TYPE_REALTIME;
	private int chartTimeDuration = 5;
	private int chartGranularity = Constants.ChartSettings.GRANULARITY_HOUR;
	private Date chartStartTime;
	private Date chartEndTime;

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

	private List<IOTSampleData> chartData = new ArrayList<IOTSampleData>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		currentSite = (Site) getIntent().getSerializableExtra(Constants.ActivityPassValue.SELECTED_SITE);
		chartType = getIntent().getIntExtra(Constants.ActivityPassValue.CHART_TYPE,
				Constants.ChartSettings.CHART_TYPE_REALTIME);
		chartTimeDuration = getIntent().getIntExtra(Constants.ActivityPassValue.CHART_RT_DURATION, 5);
		chartGranularity = getIntent().getIntExtra(Constants.ActivityPassValue.CHART_AGGR_GRANULARITY,
				Constants.ChartSettings.GRANULARITY_HOUR);
		long startTimeLong = getIntent().getLongExtra(Constants.ActivityPassValue.CHART_AGGR_STARTTIME, 0);
		chartStartTime = new Date();
		chartStartTime.setTime(startTimeLong);
		long endTimeLong = getIntent().getLongExtra(Constants.ActivityPassValue.CHART_AGGR_ENDTIME, 0);
		chartEndTime = new Date();
		chartEndTime.setTime(endTimeLong);

		chartData = (List<IOTSampleData>) getIntent().getSerializableExtra(Constants.ActivityPassValue.CHART_DATA);

		setContentView(R.layout.activity_user_chartdetail);
		initViews();
		createChart();
		updateChartData();

		chartView.repaint();
	}

	@Override
	protected void onResume() {

		// resendMessage();
		super.onResume();
	}

	@Override
	protected void onPause() {

		super.onPause();
	}

	@Override
	protected void onDestroy() {
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
		tvChartTitle = (TextView) findViewById(R.id.tv_chart_title_chartdetail);
		tvChartTitleBottom = (TextView) findViewById(R.id.tv_chart_bottom_title_chartdetail);

		setupActionBar();
	}

	private void createChart() {
		chartDataset.clear();
		chartRenderer.removeAllRenderers();

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

		chartRenderer.setYLabelsColor(0, resources.getColor(R.color.title_bk_green));
		chartRenderer.setYAxisAlign(Align.LEFT, 0);
		chartRenderer.setYLabelsAlign(Align.RIGHT, 0);

		chartRenderer.setYLabelsColor(1, resources.getColor(R.color.title_bk_brown));
		chartRenderer.setYAxisAlign(Align.RIGHT, 1);
		chartRenderer.setYLabelsAlign(Align.RIGHT, 1);

		chartRenderer.setYLabelsColor(2, resources.getColor(R.color.title_bk_purple));
		chartRenderer.setYAxisAlign(Align.RIGHT, 2);
		chartRenderer.setYLabelsAlign(Align.LEFT, 2);

		chartRenderer.setZoomButtonsVisible(true);// 是否显示放大缩小按钮
		chartRenderer.setPointSize(3);// 设置点的大小(图上显示的点的大小和图例中点的大小都会被设置)
		chartRenderer.setPanEnabled(true);
		chartRenderer.setClickEnabled(false);

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

		chartView = IOTChartFactory.getIOTChartView(this, chartDataset, chartRenderer, dateFormat,
				new String[] { "ppm", "℃", "%" });

		chartLayout.removeAllViews();
		chartLayout.addView(chartView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	}

	private void updateChartData() {
		IOTMonitorThreshold warningThreshold = (IOTMonitorThreshold) ServiceContainer.getInstance().getSessionService()
				.getSessionValue(Constants.SessionKey.THRESHOLD_WARNING);
		IOTMonitorThreshold breachThreshold = (IOTMonitorThreshold) ServiceContainer.getInstance().getSessionService()
				.getSessionValue(Constants.SessionKey.THRESHOLD_BREACH);

		// reset target line
		chartRenderer.clearYTextLabels();
		chartRenderer.addYTextLabel(warningThreshold.getCo2UpperBound(), "warning", 0,
				getResources().getColor(R.color.status_warning));
		chartRenderer.addYTextLabel(breachThreshold.getCo2UpperBound(), "breach", 0,
				getResources().getColor(R.color.status_alarm));

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

	protected void setupActionBar() {
		setActionBarTextSize(15);
		actionBar = getActionBar();
		actionBar.setTitle(currentSite.getSiteName());
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		super.setupActionBar();
	}

}
