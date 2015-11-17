package org.slstudio.hsinchuiot;

import java.util.ArrayList;
import java.util.List;

import org.slstudio.hsinchuiot.fragment.SiteAverageTab;
import org.slstudio.hsinchuiot.fragment.SiteMonitorTab;
import org.slstudio.hsinchuiot.fragment.SiteRTCurveTab;
import org.slstudio.hsinchuiot.fragment.SiteStatisticsTab;
import org.slstudio.hsinchuiot.model.IOTMonitorData;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class SiteDetailActivity extends BaseActivity {
	private ActionBar actionBar;

	private ViewPager viewPager;
	private FragmentPagerAdapter pagerAdapter;
	private List<Fragment> fragments = new ArrayList<Fragment>();

	private LinearLayout tabBtnMonitor;
	private LinearLayout tabBtnRTCurve;
	private LinearLayout tabBtnStatistics;
	private LinearLayout tabBtnAverage;

	private IOTMonitorData targetSite;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sitedetail);
		viewPager = (ViewPager) findViewById(R.id.id_site_viewpager);
		
		targetSite = (IOTMonitorData)getIntent().getSerializableExtra(MainActivity.SELECTED_SITE);
		
		
		initView();
		pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return fragments.size();
			}

			@Override
			public Fragment getItem(int arg0) {
				return fragments.get(arg0);
			}
		};

		viewPager.setAdapter(pagerAdapter);

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			private int currentIndex;

			@Override
			public void onPageSelected(int position) {
				resetTabBtn();
				switch (position) {
				case 0:
					((ImageButton) tabBtnMonitor
							.findViewById(R.id.btn_tab_bottom_monitor))
							.setImageResource(R.drawable.tab_monitor_pressed);
					break;
				case 1:
					((ImageButton) tabBtnRTCurve
							.findViewById(R.id.btn_tab_bottom_rtcurve))
							.setImageResource(R.drawable.tab_rtcurve_pressed);
					break;
				case 2:
					((ImageButton) tabBtnStatistics
							.findViewById(R.id.btn_tab_bottom_statistics))
							.setImageResource(R.drawable.tab_statistics_pressed);
					break;
				case 3:
					((ImageButton) tabBtnAverage
							.findViewById(R.id.btn_tab_bottom_average))
							.setImageResource(R.drawable.tab_average_pressed);
					break;
				}

				currentIndex = position;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

	}

	protected void resetTabBtn() {
		((ImageButton) tabBtnMonitor.findViewById(R.id.btn_tab_bottom_monitor))
				.setImageResource(R.drawable.tab_monitor_normal);
		((ImageButton) tabBtnRTCurve.findViewById(R.id.btn_tab_bottom_rtcurve))
				.setImageResource(R.drawable.tab_rtcurve_normal);
		((ImageButton) tabBtnStatistics
				.findViewById(R.id.btn_tab_bottom_statistics))
				.setImageResource(R.drawable.tab_statistics_normal);
		((ImageButton) tabBtnAverage.findViewById(R.id.btn_tab_bottom_average))
				.setImageResource(R.drawable.tab_average_normal);
	}

	private void initView() {

		tabBtnMonitor = (LinearLayout) findViewById(R.id.id_tab_bottom_monitor);
		tabBtnRTCurve = (LinearLayout) findViewById(R.id.id_tab_bottom_rtcurve);
		tabBtnStatistics = (LinearLayout) findViewById(R.id.id_tab_bottom_statistics);
		tabBtnAverage = (LinearLayout) findViewById(R.id.id_tab_bottom_average);

		SiteMonitorTab tab01 = new SiteMonitorTab();
		SiteRTCurveTab tab02 = new SiteRTCurveTab();
		SiteStatisticsTab tab03 = new SiteStatisticsTab();
		SiteAverageTab tab04 = new SiteAverageTab();
		fragments.add(tab01);
		fragments.add(tab02);
		fragments.add(tab03);
		fragments.add(tab04);

		tabBtnMonitor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				viewPager.setCurrentItem(0);
			}

		});

		tabBtnRTCurve.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				viewPager.setCurrentItem(1);
			}

		});

		tabBtnStatistics.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				viewPager.setCurrentItem(2);
			}

		});

		tabBtnAverage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				viewPager.setCurrentItem(3);
			}

		});

		setupActionBar();
	}

	@Override
	protected void setupActionBar() {
		actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setTitle("");
		super.setupActionBar();
	}
}
