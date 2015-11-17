package org.slstudio.hsinchuiot;

import org.slstudio.hsinchuiot.model.IOTMonitorThreshold;
import org.slstudio.hsinchuiot.model.User;
import org.slstudio.hsinchuiot.service.ServiceContainer;
import org.slstudio.hsinchuiot.service.SessionService;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;

public class UserSettingsActivity extends BaseActivity {

	private EditText etCO2LowerValueWarning;
	private EditText etCO2UpperValueWarning;
	private EditText etTemperatureLowerValueWarning;
	private EditText etTemperatureUpperValueWarning;
	private EditText etHumidityLowerValueWarning;
	private EditText etHumidityUpperValueWarning;

	private EditText etCO2LowerValueBreach;
	private EditText etCO2UpperValueBreach;
	private EditText etTemperatureLowerValueBreach;
	private EditText etTemperatureUpperValueBreach;
	private EditText etHumidityLowerValueBreach;
	private EditText etHumidityUpperValueBreach;

	private Switch swRememberPassword;
	
	private EditText etRTDataMonitorRefreshTime;

	private IOTMonitorThreshold warningThreshold;
	private IOTMonitorThreshold breachThreshold;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_settings);

		warningThreshold = (IOTMonitorThreshold) ServiceContainer.getInstance().getSessionService()
				.getSessionValue(Constants.SessionKey.THRESHOLD_WARNING);
		breachThreshold = (IOTMonitorThreshold) ServiceContainer.getInstance().getSessionService()
				.getSessionValue(Constants.SessionKey.THRESHOLD_BREACH);

		initViews();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_user_settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();

			break;
		case R.id.menu_ok:
			// remember password
			if (swRememberPassword.isChecked()) {
				User user = ServiceContainer.getInstance().getSessionService().getLoginUser();
				ServiceContainer.getInstance().getPerferenceService().setValue(Constants.PreferenceKey.LOGINNAME,
						user.getLoginName());
				ServiceContainer.getInstance().getPerferenceService().setValue(Constants.PreferenceKey.PASSWORD,
						user.getPassword());
			} else {
				ServiceContainer.getInstance().getPerferenceService().setValue(Constants.PreferenceKey.LOGINNAME,
						null);
				ServiceContainer.getInstance().getPerferenceService().setValue(Constants.PreferenceKey.PASSWORD,
						null);
			}
			
			//real time data monitor refresh time
			int refreshTime = 10; 
			
			if (!"".equals(etRTDataMonitorRefreshTime.getText().toString().trim())) {
				refreshTime = Integer.parseInt(etRTDataMonitorRefreshTime.getText().toString().trim());
			}
			ServiceContainer.getInstance().getPerferenceService().setValue(Constants.PreferenceKey.REALTIME_DATA_MONITOR_REFRESH_TIME,
					Integer.toString(refreshTime));
			ServiceContainer.getInstance().getSessionService().setSessionValue(Constants.SessionKey.REALTIME_DATA_MONITOR_REFRESH_TIME, refreshTime);
			
			
			// warning threshold
			if (!"".equals(etCO2LowerValueWarning.getText().toString().trim())) {
				int co2LowerValueWarning = Integer.parseInt(etCO2LowerValueWarning.getText().toString().trim());
				warningThreshold.setCo2LowerBound(co2LowerValueWarning);
			} else {
				warningThreshold.setCo2LowerBound(Integer.MIN_VALUE);
			}

			if (!"".equals(etCO2UpperValueWarning.getText().toString().trim())) {
				int co2UpperValueWarning = Integer.parseInt(etCO2UpperValueWarning.getText().toString().trim());
				warningThreshold.setCo2UpperBound(co2UpperValueWarning);
			} else {
				warningThreshold.setCo2UpperBound(Integer.MAX_VALUE);
			}

			if (!"".equals(etTemperatureLowerValueWarning.getText().toString().trim())) {
				int temperatureLowerValueWarning = Integer
						.parseInt(etTemperatureLowerValueWarning.getText().toString().trim());
				warningThreshold.setTemperatureLowerBound(temperatureLowerValueWarning);
			} else {
				warningThreshold.setTemperatureLowerBound(Integer.MIN_VALUE);
			}
			if (!"".equals(etTemperatureUpperValueWarning.getText().toString().trim())) {
				int temperatureUpperValueWarning = Integer
						.parseInt(etTemperatureUpperValueWarning.getText().toString().trim());
				warningThreshold.setTemperatureUpperBound(temperatureUpperValueWarning);
			} else {
				warningThreshold.setTemperatureUpperBound(Integer.MAX_VALUE);
			}

			if (!"".equals(etHumidityLowerValueWarning.getText().toString().trim())) {
				int humidityLowerValueWarning = Integer
						.parseInt(etHumidityLowerValueWarning.getText().toString().trim());
				warningThreshold.setHumidityLowerBound(humidityLowerValueWarning);
			} else {
				warningThreshold.setHumidityLowerBound(Integer.MIN_VALUE);
			}
			if (!"".equals(etHumidityUpperValueWarning.getText().toString().trim())) {
				int humidityUpperValueWarning = Integer
						.parseInt(etHumidityUpperValueWarning.getText().toString().trim());
				warningThreshold.setHumidityUpperBound(humidityUpperValueWarning);
			} else {
				warningThreshold.setHumidityUpperBound(Integer.MAX_VALUE);
			}

			// breach threshold
			if (!"".equals(etCO2LowerValueBreach.getText().toString().trim())) {
				int co2LowerValueBreach = Integer.parseInt(etCO2LowerValueBreach.getText().toString().trim());
				breachThreshold.setCo2LowerBound(co2LowerValueBreach);
			} else {
				breachThreshold.setCo2LowerBound(Integer.MIN_VALUE);
			}

			if (!"".equals(etCO2UpperValueBreach.getText().toString().trim())) {
				int co2UpperValueBreach = Integer.parseInt(etCO2UpperValueBreach.getText().toString().trim());
				breachThreshold.setCo2UpperBound(co2UpperValueBreach);
			} else {
				breachThreshold.setCo2UpperBound(Integer.MAX_VALUE);
			}

			if (!"".equals(etTemperatureLowerValueBreach.getText().toString().trim())) {
				int temperatureLowerValueBreach = Integer
						.parseInt(etTemperatureLowerValueWarning.getText().toString().trim());
				breachThreshold.setTemperatureLowerBound(temperatureLowerValueBreach);
			} else {
				breachThreshold.setTemperatureLowerBound(Integer.MIN_VALUE);
			}
			if (!"".equals(etTemperatureUpperValueBreach.getText().toString().trim())) {
				int temperatureUpperValueBreach = Integer
						.parseInt(etTemperatureUpperValueBreach.getText().toString().trim());
				breachThreshold.setTemperatureUpperBound(temperatureUpperValueBreach);
			} else {
				breachThreshold.setTemperatureUpperBound(Integer.MAX_VALUE);
			}

			if (!"".equals(etHumidityLowerValueBreach.getText().toString().trim())) {
				int humidityLowerValueBreach = Integer.parseInt(etHumidityLowerValueBreach.getText().toString().trim());
				breachThreshold.setHumidityLowerBound(humidityLowerValueBreach);
			} else {
				breachThreshold.setHumidityLowerBound(Integer.MIN_VALUE);
			}
			if (!"".equals(etHumidityUpperValueBreach.getText().toString().trim())) {
				int humidityUpperValueBreach = Integer.parseInt(etHumidityUpperValueBreach.getText().toString().trim());
				breachThreshold.setHumidityUpperBound(humidityUpperValueBreach);
			} else {
				breachThreshold.setHumidityUpperBound(Integer.MAX_VALUE);
			}

			ServiceContainer.getInstance().getSessionService().setSessionValue(Constants.SessionKey.THRESHOLD_WARNING,
					warningThreshold);
			ServiceContainer.getInstance().getSessionService().setSessionValue(Constants.SessionKey.THRESHOLD_BREACH,
					breachThreshold);

			// save to preference

			ServiceContainer.getInstance().getPerferenceService().setValue(
					Constants.PreferenceKey.WARNING_CO2_LOWERVALUE,
					Integer.toString(warningThreshold.getCo2LowerBound()));
			ServiceContainer.getInstance().getPerferenceService().setValue(
					Constants.PreferenceKey.WARNING_CO2_UPPERVALUE,
					Integer.toString(warningThreshold.getCo2UpperBound()));
			ServiceContainer.getInstance().getPerferenceService().setValue(
					Constants.PreferenceKey.WARNING_TEMPERATURE_LOWERVALUE,
					Integer.toString(warningThreshold.getTemperatureLowerBound()));
			ServiceContainer.getInstance().getPerferenceService().setValue(
					Constants.PreferenceKey.WARNING_TEMPERATURE_UPPERVALUE,
					Integer.toString(warningThreshold.getTemperatureUpperBound()));
			ServiceContainer.getInstance().getPerferenceService().setValue(
					Constants.PreferenceKey.WARNING_HUMIDITY_LOWERVALUE,
					Integer.toString(warningThreshold.getHumidityLowerBound()));
			ServiceContainer.getInstance().getPerferenceService().setValue(
					Constants.PreferenceKey.WARNING_HUMIDITY_UPPERVALUE,
					Integer.toString(warningThreshold.getHumidityUpperBound()));

			ServiceContainer.getInstance().getPerferenceService().setValue(
					Constants.PreferenceKey.BREACH_CO2_LOWERVALUE,
					Integer.toString(breachThreshold.getCo2LowerBound()));
			ServiceContainer.getInstance().getPerferenceService().setValue(
					Constants.PreferenceKey.BREACH_CO2_UPPERVALUE,
					Integer.toString(breachThreshold.getCo2UpperBound()));
			ServiceContainer.getInstance().getPerferenceService().setValue(
					Constants.PreferenceKey.BREACH_TEMPERATURE_LOWERVALUE,
					Integer.toString(breachThreshold.getTemperatureLowerBound()));
			ServiceContainer.getInstance().getPerferenceService().setValue(
					Constants.PreferenceKey.BREACH_TEMPERATURE_UPPERVALUE,
					Integer.toString(breachThreshold.getTemperatureUpperBound()));
			ServiceContainer.getInstance().getPerferenceService().setValue(
					Constants.PreferenceKey.BREACH_HUMIDITY_LOWERVALUE,
					Integer.toString(breachThreshold.getHumidityLowerBound()));
			ServiceContainer.getInstance().getPerferenceService().setValue(
					Constants.PreferenceKey.BREACH_HUMIDITY_UPPERVALUE,
					Integer.toString(breachThreshold.getHumidityUpperBound()));

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
		actionBar.setTitle(R.string.action_settings);
		super.setupActionBar();
		
	}

	private void initViews() {
		swRememberPassword = (Switch) findViewById(R.id.sw_settings_remember_password);

		etRTDataMonitorRefreshTime = (EditText)findViewById(R.id.et_settings_monitor_refresh_time);
		
		etCO2LowerValueWarning = (EditText) findViewById(R.id.et_settings_co2_lowervalue_warning);
		etCO2UpperValueWarning = (EditText) findViewById(R.id.et_settings_co2_uppervalue_warning);
		etTemperatureLowerValueWarning = (EditText) findViewById(R.id.et_settings_temperature_lowervalue_warning);
		etTemperatureUpperValueWarning = (EditText) findViewById(R.id.et_settings_temperature_uppervalue_warning);
		etHumidityLowerValueWarning = (EditText) findViewById(R.id.et_settings_humidity_lowervalue_warning);
		etHumidityUpperValueWarning = (EditText) findViewById(R.id.et_settings_humidity_uppervalue_warning);

		etCO2LowerValueBreach = (EditText) findViewById(R.id.et_settings_co2_lowervalue_breach);
		etCO2UpperValueBreach = (EditText) findViewById(R.id.et_settings_co2_uppervalue_breach);
		etTemperatureLowerValueBreach = (EditText) findViewById(R.id.et_settings_temperature_lowervalue_breach);
		etTemperatureUpperValueBreach = (EditText) findViewById(R.id.et_settings_temperature_uppervalue_breach);
		etHumidityLowerValueBreach = (EditText) findViewById(R.id.et_settings_humidity_lowervalue_breach);
		etHumidityUpperValueBreach = (EditText) findViewById(R.id.et_settings_humidity_uppervalue_breach);

		String loginName = ServiceContainer.getInstance().getPerferenceService().getValue(
				Constants.PreferenceKey.LOGINNAME);
		if (loginName != null && (!loginName.equals(""))) {
			swRememberPassword.setChecked(true);
		} else {
			swRememberPassword.setChecked(false);
		}
		if(!"".equals(ServiceContainer.getInstance().getPerferenceService().getValue(Constants.PreferenceKey.REALTIME_DATA_MONITOR_REFRESH_TIME))){
			etRTDataMonitorRefreshTime.setText(ServiceContainer.getInstance().getPerferenceService().getValue(Constants.PreferenceKey.REALTIME_DATA_MONITOR_REFRESH_TIME));
		}else{
			etRTDataMonitorRefreshTime.setText("10");
		}

		if (warningThreshold.getCo2LowerBound() != Integer.MIN_VALUE) {
			etCO2LowerValueWarning.setText(Integer.toString(warningThreshold.getCo2LowerBound()));
		}
		if (warningThreshold.getCo2UpperBound() != Integer.MAX_VALUE) {
			etCO2UpperValueWarning.setText(Integer.toString(warningThreshold.getCo2UpperBound()));
		}
		if (warningThreshold.getTemperatureLowerBound() != Integer.MIN_VALUE) {
			etTemperatureLowerValueWarning.setText(Integer.toString(warningThreshold.getTemperatureLowerBound()));
		}
		if (warningThreshold.getTemperatureUpperBound() != Integer.MAX_VALUE) {
			etTemperatureUpperValueWarning.setText(Integer.toString(warningThreshold.getTemperatureUpperBound()));
		}
		if (warningThreshold.getHumidityLowerBound() != Integer.MIN_VALUE) {
			etHumidityLowerValueWarning.setText(Integer.toString(warningThreshold.getHumidityLowerBound()));
		}
		if (warningThreshold.getHumidityUpperBound() != Integer.MAX_VALUE) {
			etHumidityUpperValueWarning.setText(Integer.toString(warningThreshold.getHumidityUpperBound()));
		}

		if (breachThreshold.getCo2LowerBound() != Integer.MIN_VALUE) {
			etCO2LowerValueBreach.setText(Integer.toString(breachThreshold.getCo2LowerBound()));
		}
		if (breachThreshold.getCo2UpperBound() != Integer.MAX_VALUE) {
			etCO2UpperValueBreach.setText(Integer.toString(breachThreshold.getCo2UpperBound()));
		}
		if (breachThreshold.getTemperatureLowerBound() != Integer.MIN_VALUE) {
			etTemperatureLowerValueBreach.setText(Integer.toString(breachThreshold.getTemperatureLowerBound()));
		}
		if (breachThreshold.getTemperatureUpperBound() != Integer.MAX_VALUE) {
			etTemperatureUpperValueBreach.setText(Integer.toString(breachThreshold.getTemperatureUpperBound()));
		}
		if (breachThreshold.getHumidityLowerBound() != Integer.MIN_VALUE) {
			etHumidityLowerValueBreach.setText(Integer.toString(breachThreshold.getHumidityLowerBound()));
		}
		if (breachThreshold.getHumidityUpperBound() != Integer.MAX_VALUE) {
			etHumidityUpperValueBreach.setText(Integer.toString(breachThreshold.getHumidityUpperBound()));
		}
		setupActionBar();
	}

}
