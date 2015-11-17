package org.slstudio.hsinchuiot;

import java.util.ArrayList;
import java.util.List;

import org.slstudio.hsinchuiot.fragment.AverageValueTab;
import org.slstudio.hsinchuiot.fragment.RTDataTab;
import org.slstudio.hsinchuiot.model.Device;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class MainActivity extends BaseActivity {

	public static final String SELECTED_SITE = "org.slstudio.hsinchuiot.SELECTED_SITE";
	public static final String LOGIN_USER = "org.slstudio.hsinchuiot.LOGIN_USER";

	private ActionBar actionBar;

	private ViewPager viewPager;
	private FragmentPagerAdapter pagerAdapter;
	private List<Fragment> fragments = new ArrayList<Fragment>();

	private LinearLayout tabBtnRTData;
	private LinearLayout tabBtnAverageValue;
	
	private List<Device> deviceList = new ArrayList<Device>();

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		viewPager = (ViewPager) findViewById(R.id.id_main_viewpager);
		
		initViews();

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
					((ImageButton) tabBtnAverageValue
							.findViewById(R.id.btn_tab_bottom_averagevalue))
							.setImageResource(R.drawable.tab_averagevalue_pressed);
					actionBar.setTitle("监测点列表(历史均值)");
					break;
				case 1:
					((ImageButton) tabBtnRTData
							.findViewById(R.id.btn_tab_bottom_rtdata))
							.setImageResource(R.drawable.tab_rtdata_pressed);
					actionBar.setTitle("监测点列表(实时数据)");
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
		((ImageButton) tabBtnRTData.findViewById(R.id.btn_tab_bottom_rtdata))
				.setImageResource(R.drawable.tab_rtdata_normal);
		((ImageButton) tabBtnAverageValue
				.findViewById(R.id.btn_tab_bottom_averagevalue))
				.setImageResource(R.drawable.tab_averagevalue_normal);
	}

	private void initViews() {

		tabBtnRTData = (LinearLayout) findViewById(R.id.id_tab_bottom_rtdata);
		tabBtnAverageValue = (LinearLayout) findViewById(R.id.id_tab_bottom_averagevalue);

		AverageValueTab tab01 = new AverageValueTab();
		RTDataTab tab02 = new RTDataTab();
		
		fragments.add(tab01);
		fragments.add(tab02);

		tabBtnRTData.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				viewPager.setCurrentItem(0);
			}

		});

		tabBtnAverageValue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				viewPager.setCurrentItem(1);
			}

		});

		setupActionBar();
	}

	@Override
	protected void setupActionBar() {
		actionBar = getActionBar();
		actionBar.setTitle("监测点列表(实时数据)");
		actionBar.setHomeButtonEnabled(true);
		super.setupActionBar();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			break;
		}
		return true;
	}
}
