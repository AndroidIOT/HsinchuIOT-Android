package org.slstudio.hsinchuiot;

import java.util.Calendar;
import java.util.Map;

import org.slstudio.hsinchuiot.model.IOTMonitorData;
import org.slstudio.hsinchuiot.model.IOTMonitorThreshold;
import org.slstudio.hsinchuiot.model.IOTReportData;
import org.slstudio.hsinchuiot.model.Site;
import org.slstudio.hsinchuiot.service.ServiceContainer;
import org.slstudio.hsinchuiot.service.SessionService;
import org.slstudio.hsinchuiot.service.http.ForgroundRequestListener;
import org.slstudio.hsinchuiot.service.http.HttpConfig;
import org.slstudio.hsinchuiot.service.http.HttpRequest;
import org.slstudio.hsinchuiot.service.http.NoneAuthedHttpRequest;
import org.slstudio.hsinchuiot.service.http.RequestControl;
import org.slstudio.hsinchuiot.service.http.RequestListener;
import org.slstudio.hsinchuiot.util.ReportUtil;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class SuperUserSiteDetailActivity extends BaseActivity {
	public static final int REPORTTYPE_CO2 = 1;
	public static final int REPORTTYPE_TEMPERATURE = 2;
	public static final int REPORTTYPE_HUMIDITY = 3;

	private Site currentSite;
	private IOTMonitorThreshold warningThreshold;
	private IOTMonitorThreshold alarmThreshold;

	private IOTReportData reportData;

	private Calendar co2From;
	private Calendar co2To;
	private Calendar temperatureFrom;
	private Calendar temperatureTo;
	private Calendar humidityFrom;
	private Calendar humidityTo;

	private float averageValueCO2;
	private float averageValueTemperature;
	private float averageValueHumidity;

	private float maxValueCO2;
	private String timeOfMaxValueCO2;
	private float maxValueTemperature;
	private String timeOfMaxValueTemperature;
	private float maxValueHumidity;
	private String timeOfMaxValueHumidity;

	private float minValueCO2;
	private String timeOfMinValueCO2;
	private float minValueTemperature;
	private String timeOfMinValueTemperature;
	private float minValueHumidity;
	private String timeOfMinValueHumidity;

	private Resources resources;

	private Handler handler;

	private ActionBar actionBar;

	private TextView tvCO2Title;
	private TextView tvTemperatureTitle;
	private TextView tvHumidityTitle;

	private TextView tvCO2AverageValue;
	private TextView tvTemperatureAverageValue;
	private TextView tvHumidityAverageValue;

	private TextView tvCO2MaxValue;
	private TextView tvTemperatureMaxValue;
	private TextView tvHumidityMaxValue;

	private TextView tvCO2MinValue;
	private TextView tvTemperatureMinValue;
	private TextView tvHumidityMinValue;

	private ImageView ivSiteImage;

	private ViewFlipper vfCO2;
	private ViewFlipper vfTemperature;
	private ViewFlipper vfHumidity;

	private ImageButton btnCO2Pre;
	private ImageButton btnCO2Next;
	private ImageButton btnTemperaturePre;
	private ImageButton btnTemperatureNext;
	private ImageButton btnHumidityPre;
	private ImageButton btnHumidityNext;

	private PullToRefreshScrollView refreshScrollView;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_superuser_sitedetail);

		currentSite = (Site) getIntent().getSerializableExtra(Constants.ActivityPassValue.SELECTED_SITE);
		warningThreshold = (IOTMonitorThreshold)ServiceContainer.getInstance().getSessionService().getSessionValue(Constants.SessionKey.THRESHOLD_WARNING);
		alarmThreshold = (IOTMonitorThreshold)ServiceContainer.getInstance().getSessionService().getSessionValue(Constants.SessionKey.THRESHOLD_BREACH);
		
		Calendar c8[] = ReportUtil.get8HoursTimePeriod();
		co2From = c8[0];
		co2To = c8[1];

		Calendar c1[] = ReportUtil.get1HourTimePeriod();
		temperatureFrom = c1[0];
		temperatureTo = c1[1];

		Calendar c1t[] = ReportUtil.get1HourTimePeriod();

		humidityFrom = c1t[0];
		humidityTo = c1t[1];

		resources = getResources();
		handler = new Handler();

		initViews();

		if (currentSite != null) {
			sendQueryReportRequest(currentSite.getDevice().getDeviceID(), ReportUtil.getServerTimeHourString(co2From),
					ReportUtil.getServerTimeHourString(co2To), new CO2ReportDataHandler());

			sendQueryReportRequest(currentSite.getDevice().getDeviceID(),
					ReportUtil.getServerTimeHourString(temperatureFrom), ReportUtil.getServerTimeHourString(temperatureTo),
					new TemperatureReportDataHandler());

			sendQueryReportRequest(currentSite.getDevice().getDeviceID(), ReportUtil.getServerTimeHourString(humidityFrom),
					ReportUtil.getServerTimeHourString(humidityTo), new HumidityReportDataHandler());
		}
	}

	private void initViews() {
		ivSiteImage = (ImageView) findViewById(R.id.iv_site_image_superuser);
		String uri = "file://" + Constants.ImageLoader.IMAGE_ENGINE_CACHE + "/" + currentSite.getSiteImageFilename();
		ImageLoader.getInstance().displayImage(uri, ivSiteImage);

		int imageHeight = this.getWindowManager().getDefaultDisplay().getHeight() / 4;

		LayoutParams para;
		para = ivSiteImage.getLayoutParams();
		para.height = imageHeight;

		ivSiteImage.setLayoutParams(para);

		tvCO2Title = (TextView) findViewById(R.id.tv_co2_title_superuser);
		tvCO2Title.setText(
				resources.getString(R.string.co2) + ": " + ReportUtil.get8HoursTimePeriodString(co2From, co2To));

		tvTemperatureTitle = (TextView) findViewById(R.id.tv_temperature_title_superuser);
		tvTemperatureTitle.setText(resources.getString(R.string.temperature) + ": "
				+ ReportUtil.get1HourTimePeriodString(temperatureFrom, temperatureTo));

		tvHumidityTitle = (TextView) findViewById(R.id.tv_humidity_title_superuser);
		tvHumidityTitle.setText(resources.getString(R.string.humidity) + ": "
				+ ReportUtil.get1HourTimePeriodString(humidityFrom, humidityTo));

		tvCO2AverageValue = (TextView) findViewById(R.id.tv_co2_averagevalue_superuser);
		tvCO2AverageValue.setText("");
		tvCO2MaxValue = (TextView) findViewById(R.id.tv_co2_maxvalue_superuser);
		tvCO2MaxValue.setText("");
		tvCO2MinValue = (TextView) findViewById(R.id.tv_co2_minvalue_superuser);
		tvCO2MinValue.setText("");

		tvTemperatureAverageValue = (TextView) findViewById(R.id.tv_temperature_averagevalue_superuser);
		tvTemperatureAverageValue.setText("");
		tvTemperatureMaxValue = (TextView) findViewById(R.id.tv_temperature_maxvalue_superuser);
		tvTemperatureMaxValue.setText("");
		tvTemperatureMinValue = (TextView) findViewById(R.id.tv_temperature_minvalue_superuser);
		tvTemperatureMinValue.setText("");

		tvHumidityAverageValue = (TextView) findViewById(R.id.tv_humidity_averagevalue_superuser);
		tvHumidityAverageValue.setText("");
		tvHumidityMaxValue = (TextView) findViewById(R.id.tv_humidity_maxvalue_superuser);
		tvHumidityMaxValue.setText("");
		tvHumidityMinValue = (TextView) findViewById(R.id.tv_humidity_minvalue_superuser);
		tvHumidityMinValue.setText("");

		vfCO2 = (ViewFlipper) findViewById(R.id.vf_co2_superuser);
		vfCO2.setOnTouchListener(new MyViewFlipperOnTouchListener(vfCO2) {

			@Override
			public boolean onPreviousAction() {
				return showPreviousCO2();
			}

			@Override
			public boolean onNextAction() {
				// TODO Auto-generated method stub
				return showNextCO2();
			}

		});

		vfTemperature = (ViewFlipper) findViewById(R.id.vf_temperature_superuser);
		vfTemperature.setOnTouchListener(new MyViewFlipperOnTouchListener(vfTemperature) {

			@Override
			public boolean onPreviousAction() {
				return showPreviousTemperature();
			}

			@Override
			public boolean onNextAction() {
				// TODO Auto-generated method stub
				return showNextTemperature();
			}

		});

		vfHumidity = (ViewFlipper) findViewById(R.id.vf_humidity_superuser);
		vfHumidity.setOnTouchListener(new MyViewFlipperOnTouchListener(vfHumidity) {

			@Override
			public boolean onPreviousAction() {
				return showPreviousHumidity();
			}

			@Override
			public boolean onNextAction() {
				// TODO Auto-generated method stub
				return showNextHumidity();
			}

		});

		btnCO2Pre = (ImageButton) findViewById(R.id.btn_co2_pre_superuser);
		btnCO2Pre.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				vfCO2.setInAnimation(
						AnimationUtils.loadAnimation(SuperUserSiteDetailActivity.this, R.anim.slide_in_right));
				vfCO2.setOutAnimation(
						AnimationUtils.loadAnimation(SuperUserSiteDetailActivity.this, R.anim.slide_out_left));

				if (showPreviousCO2()) {
					vfCO2.showPrevious();
				}
			}

		});

		btnCO2Next = (ImageButton) findViewById(R.id.btn_co2_next_superuser);
		btnCO2Next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				vfCO2.setInAnimation(
						AnimationUtils.loadAnimation(SuperUserSiteDetailActivity.this, android.R.anim.slide_in_left));
				vfCO2.setOutAnimation(
						AnimationUtils.loadAnimation(SuperUserSiteDetailActivity.this, android.R.anim.slide_out_right));

				if (showNextCO2()) {
					vfCO2.showNext();
				}
			}

		});

		btnTemperaturePre = (ImageButton) findViewById(R.id.btn_temperature_pre_superuser);
		btnTemperaturePre.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				vfTemperature.setInAnimation(
						AnimationUtils.loadAnimation(SuperUserSiteDetailActivity.this, R.anim.slide_in_right));
				vfTemperature.setOutAnimation(
						AnimationUtils.loadAnimation(SuperUserSiteDetailActivity.this, R.anim.slide_out_left));
				if (showPreviousTemperature()) {
					vfTemperature.showPrevious();
				}
			}

		});

		btnTemperatureNext = (ImageButton) findViewById(R.id.btn_temperature_next_superuser);
		btnTemperatureNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				vfTemperature.setInAnimation(
						AnimationUtils.loadAnimation(SuperUserSiteDetailActivity.this, android.R.anim.slide_in_left));
				vfTemperature.setOutAnimation(
						AnimationUtils.loadAnimation(SuperUserSiteDetailActivity.this, android.R.anim.slide_out_right));
				if (showNextTemperature()) {
					vfTemperature.showNext();
				}
			}

		});

		btnHumidityPre = (ImageButton) findViewById(R.id.btn_humidity_pre_superuser);
		btnHumidityPre.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				vfHumidity.setInAnimation(
						AnimationUtils.loadAnimation(SuperUserSiteDetailActivity.this, R.anim.slide_in_right));
				vfHumidity.setOutAnimation(
						AnimationUtils.loadAnimation(SuperUserSiteDetailActivity.this, R.anim.slide_out_left));
				if (showPreviousHumidity()) {
					vfHumidity.showPrevious();
				}
			}

		});

		btnHumidityNext = (ImageButton) findViewById(R.id.btn_humidity_next_superuser);
		btnHumidityNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				vfHumidity.setInAnimation(
						AnimationUtils.loadAnimation(SuperUserSiteDetailActivity.this, android.R.anim.slide_in_left));
				vfHumidity.setOutAnimation(
						AnimationUtils.loadAnimation(SuperUserSiteDetailActivity.this, android.R.anim.slide_out_right));
				if (showNextHumidity()) {
					vfHumidity.showNext();
				}
			}

		});

		refreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview_superuser);
		refreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				if (currentSite != null) {
					sendQueryReportRequest(currentSite.getDevice().getDeviceID(),
							ReportUtil.getServerTimeHourString(co2From), ReportUtil.getServerTimeHourString(co2To),
							new CO2ReportDataHandler());

					sendQueryReportRequest(currentSite.getDevice().getDeviceID(),
							ReportUtil.getServerTimeHourString(temperatureFrom),
							ReportUtil.getServerTimeHourString(temperatureTo), new TemperatureReportDataHandler());

					sendQueryReportRequest(currentSite.getDevice().getDeviceID(),
							ReportUtil.getServerTimeHourString(humidityFrom), ReportUtil.getServerTimeHourString(humidityTo),
							new HumidityReportDataHandler());
				}

			}
		});

		setupActionBar();
	}

	@Override
	protected void setupActionBar() {
		setActionBarTextSize(15);
		actionBar = getActionBar();
		actionBar.setTitle(currentSite.getSiteName());
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		super.setupActionBar();
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
		getMenuInflater().inflate(R.menu.activity_superuser_sitedetail, menu);
		return true;
	}

	private void updateUI() {
		
		int alarm = getResources().getColor(R.color.status_alarm);
		int warning = getResources().getColor(R.color.status_warning);
		int normal = getResources().getColor(R.color.black);
		
		
		tvCO2AverageValue.setText(Float.toString(averageValueCO2) + " ppm");
		if (alarmThreshold != null && alarmThreshold.isCO2Breach(averageValueCO2)) {
			tvCO2AverageValue.setTextColor(alarm);
		} else if (warningThreshold != null && warningThreshold.isCO2Breach(averageValueCO2)) {
			tvCO2AverageValue.setTextColor(warning);
		} else {
			tvCO2AverageValue.setTextColor(normal);
		}

		tvCO2MaxValue.setText(Integer.toString(Math.round(maxValueCO2)) + " ppm");
		tvCO2MinValue.setText(Integer.toString(Math.round(minValueCO2)) + " ppm");

		tvTemperatureAverageValue.setText(Float.toString(averageValueTemperature) + " ℃");

		if (alarmThreshold != null && alarmThreshold.isTemperatureBreach(averageValueTemperature)) {
			tvTemperatureAverageValue.setTextColor(alarm);
		} else if (warningThreshold != null && warningThreshold.isTemperatureBreach(averageValueTemperature)) {
			tvTemperatureAverageValue.setTextColor(warning);
		} else {
			tvTemperatureAverageValue.setTextColor(normal);
		}

		tvTemperatureMaxValue.setText(Float.toString(maxValueTemperature) + " ℃");
		tvTemperatureMinValue.setText(Float.toString(minValueTemperature) + " ℃");

		tvHumidityAverageValue.setText(Float.toString(averageValueHumidity) + " %");

		if (alarmThreshold != null && alarmThreshold.isHumidityBreach(averageValueHumidity)) {
			tvHumidityAverageValue.setTextColor(alarm);
		} else if (warningThreshold != null && warningThreshold.isHumidityBreach(averageValueHumidity)) {
			tvHumidityAverageValue.setTextColor(warning);
		} else {
			tvHumidityAverageValue.setTextColor(normal);
		}

		tvHumidityMaxValue.setText(Integer.toString(Math.round(maxValueHumidity)) + " %");
		tvHumidityMinValue.setText(Integer.toString(Math.round(minValueHumidity)) + " %");
	}

	private boolean showNextCO2() {
		return showReport(8, co2From, co2To, tvCO2Title, new CO2ReportDataHandler(), resources.getString(R.string.co2));
	}

	private boolean showPreviousCO2() {
		return showReport(-8, co2From, co2To, tvCO2Title, new CO2ReportDataHandler(),
				resources.getString(R.string.co2));
	}

	private boolean showNextTemperature() {
		return showReport(1, temperatureFrom, temperatureTo, tvTemperatureTitle, new TemperatureReportDataHandler(),
				resources.getString(R.string.temperature));
	}

	private boolean showPreviousTemperature() {
		return showReport(-1, temperatureFrom, temperatureTo, tvTemperatureTitle, new TemperatureReportDataHandler(),
				resources.getString(R.string.temperature));
	}

	private boolean showNextHumidity() {
		return showReport(1, humidityFrom, humidityTo, tvHumidityTitle, new HumidityReportDataHandler(),
				resources.getString(R.string.humidity));
	}

	private boolean showPreviousHumidity() {
		return showReport(-1, humidityFrom, humidityTo, tvHumidityTitle, new HumidityReportDataHandler(),
				resources.getString(R.string.humidity));
	}

	private boolean showReport(int timePeriod, Calendar from, Calendar to, TextView tvTitle, IReportDataHandler handler,
			String title) {
		Calendar now = Calendar.getInstance();

		Calendar newFrom = Calendar.getInstance();
		newFrom.setTime(from.getTime());
		newFrom.add(Calendar.HOUR, timePeriod);

		Calendar newTo = Calendar.getInstance();
		newTo.setTime(to.getTime());
		newTo.add(Calendar.HOUR, timePeriod);

		if (newFrom.after(now)) {
			Toast.makeText(this, "No data for this time period", Toast.LENGTH_SHORT).show();
			return false;
		}

		from.setTime(newFrom.getTime());
		to.setTime(newTo.getTime());
		String tStr = "";
		if (timePeriod == 8 || timePeriod == -8) {
			tStr = ReportUtil.get8HoursTimePeriodString(from, to);
		} else if (timePeriod == 1 || timePeriod == -1) {
			tStr = ReportUtil.get1HourTimePeriodString(from, to);
		}
		tvTitle.setText(title + ": " + tStr);

		if (currentSite != null) {
			sendQueryReportRequest(currentSite.getDevice().getDeviceID(), ReportUtil.getServerTimeHourString(from),
					ReportUtil.getServerTimeHourString(to), handler);
		}
		return true;

	}

	private void sendQueryReportRequest(String deviceID, String from, String to, IReportDataHandler handler) {
		HttpRequest request = new NoneAuthedHttpRequest(new HttpConfig.GetHttpConfig(),
				Constants.ServerAPIURI.GET_REPORT_DATA);
		String sessionID = ServiceContainer.getInstance().getSessionService().getSessionID();
		request.addParameter("dataType", "xml");
		request.addParameter("__session_id", sessionID);
		request.addParameter("__page_no", "1");
		request.addParameter("__page_size", "10000");
		request.addParameter("__having_max", "id");
		request.addParameter("__group_by", "did%2Csensor%2Cname");
		request.addParameter("t__from", from);
		request.addParameter("t__to", to);
		request.addParameter("__max", "value");
		request.addParameter("__min", "value");
		request.addParameter("__avg", "value");
		request.addParameter("did%5B0%5D", deviceID);
		GetReportDataListener l = new GetReportDataListener(this, true, getString(R.string.common_please_wait), handler,
				deviceID, from, to);

		ServiceContainer.getInstance().getHttpHandler().doRequest(request, l);
	}

	private abstract class MyViewFlipperOnTouchListener implements OnTouchListener {
		private ViewFlipper vf;
		// 左右滑动时手指按下的X坐标
		private float touchDownX;
		// 左右滑动时手指松开的X坐标
		private float touchUpX;

		public MyViewFlipperOnTouchListener(ViewFlipper vf) {
			this.vf = vf;
		}

		@Override
		public boolean onTouch(View arg0, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				// 取得左右滑动时手指按下的X坐标
				touchDownX = event.getX();
				return true;
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				// 取得左右滑动时手指松开的X坐标
				touchUpX = event.getX();
				// 从左往右，看前一个View
				if (touchUpX - touchDownX > 100) {
					// 设置View切换的动画
					vf.setInAnimation(AnimationUtils.loadAnimation(SuperUserSiteDetailActivity.this,
							android.R.anim.slide_in_left));
					vf.setOutAnimation(AnimationUtils.loadAnimation(SuperUserSiteDetailActivity.this,
							android.R.anim.slide_out_right));
					// 显示下一个View
					if (onPreviousAction()) {
						vf.showPrevious();
					}
					// 从右往左，看后一个View
				} else if (touchDownX - touchUpX > 100) {
					// 设置View切换的动画
					// 由于Android没有提供slide_out_left和slide_in_right，所以仿照slide_in_left和slide_out_right编写了slide_out_left和slide_in_right

					vf.setInAnimation(
							AnimationUtils.loadAnimation(SuperUserSiteDetailActivity.this, R.anim.slide_in_right));
					vf.setOutAnimation(
							AnimationUtils.loadAnimation(SuperUserSiteDetailActivity.this, R.anim.slide_out_left));
					// 显示前一个View
					if (onNextAction()) {
						vf.showNext();
					}
				}
				return true;
			}
			return false;
		}

		public abstract boolean onPreviousAction();

		public abstract boolean onNextAction();

	}

	private class GetReportDataListener extends ForgroundRequestListener<IOTReportData> {
		// private RequestControl control;
		private IReportDataHandler reportDataHandler;
		private String deviceID;
		private String from;
		private String to;

		public GetReportDataListener(Context context, boolean isShowProgressDialog, String content,
				IReportDataHandler handler, String deviceID, String from, String to) {
			super(context, isShowProgressDialog, content);
			this.reportDataHandler = handler;
			this.deviceID = deviceID;
			this.from = from;
			this.to = to;
		}
		/*
		 * @Override public void onRequestCancelled() { if (control != null)
		 * control.cancel();
		 * 
		 * }
		 * 
		 * @Override public void onRequestGetControl(RequestControl control) {
		 * this.control = control; }
		 * 
		 * @Override public void onRequestStart() { // TODO Auto-generated
		 * method stub
		 * 
		 * }
		 * 
		 * @Override public void onRequestError(Exception e) { // TODO
		 * Auto-generated method stub
		 * 
		 * }
		 */

		@Override
		public void onRequestResult(final IOTReportData result) {
			if (currentSite != null) {
				reportDataHandler.handle(result);
			}

			handler.post(new Runnable() {

				@Override
				public void run() {
					updateUI();
				}
			});

		}

		@Override
		public void onRequestComplete() {
			super.onRequestComplete();
			handler.post(new Runnable() {

				@Override
				public void run() {

					if (refreshScrollView.isRefreshing()) {
						refreshScrollView.onRefreshComplete();
					}
				}
			});

		}
	}

	private interface IReportDataHandler {
		public void handle(IOTReportData reportData);
	}

	private class CO2ReportDataHandler implements IReportDataHandler {

		@Override
		public void handle(IOTReportData reportData) {
			// TODO Auto-generated method stub
			averageValueCO2 = reportData.getAverageValueCO2();
			maxValueCO2 = reportData.getMaxValueCO2();
			timeOfMaxValueCO2 = reportData.getTimeOfMaxValueCO2();
			minValueCO2 = reportData.getMinValueCO2();
			timeOfMinValueCO2 = reportData.getTimeOfMinValueCO2();
		}

	}

	private class TemperatureReportDataHandler implements IReportDataHandler {

		@Override
		public void handle(IOTReportData reportData) {
			// TODO Auto-generated method stub
			averageValueTemperature = reportData.getAverageValueTemperature();
			maxValueTemperature = reportData.getMaxValueTemperature();
			timeOfMaxValueTemperature = reportData.getTimeOfMaxValueTemperature();
			minValueTemperature = reportData.getMinValueTemperature();
			timeOfMinValueTemperature = reportData.getTimeOfMinValueTemperature();
		}

	}

	private class HumidityReportDataHandler implements IReportDataHandler {

		@Override
		public void handle(IOTReportData reportData) {
			// TODO Auto-generated method stub
			averageValueHumidity = reportData.getAverageValueHumidity();
			maxValueHumidity = reportData.getMaxValueHumidity();
			timeOfMaxValueHumidity = reportData.getTimeOfMaxValueHumidity();
			minValueHumidity = reportData.getMinValueHumidity();
			timeOfMinValueHumidity = reportData.getTimeOfMinValueHumidity();
		}

	}

}
