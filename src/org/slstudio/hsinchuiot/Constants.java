package org.slstudio.hsinchuiot;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import android.os.Environment;


public class Constants {
	public static final String ENCRYPT_SEED = "HSINCHU_IOT";
	public static final String debugLogTag = "hsinchuIOT_debug";
	public static final String DEFAULT_URI = "download";
	public static final String DOWNLOAD_URL = AppConfig.HTTP_SCHEME
			+ AppConfig.HTTP_DOMAIN
			+ ":"
			+ AppConfig.HTTP_PORT
			+ "/"
			+ DEFAULT_URI
			+ "?image=";
	public static final String UPGRADE_FILE_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/hsinchuIOT/";
	
	public static final String CACHE_FILE_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/hsinchuIOT/cache/";

	public static final String CRASH_FILE_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/hsinchuIOT/";
	
	public static final String LOG_FILE_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/hsinchuIOT/logs/";
	
	public static final String CONFIG_FILE_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/hsinchuIOT/";
	
	public static class BundleKey {

		public static final String UPGRAGE_TYPE = "UPGRAGE_TYPE";
		public static final String PUSH_MESSAGE = "PUSH_MESSAGE";
			
		public static final String DICTIONARY="DICTIONARY";
	
	}
	
	public static class MessageKey{
		public static final int MESSAGE_GET_REALTIME_DATA = 1;
		public static final int MESSAGE_GET_CHART_DATA = 2;
		public static final int MESSAGE_UPDATE_CHART_DATA = 3;
		public static final int MESSAGE_UPDATE_MONITOR_DATA = 4;
		public static final int V2_MESSAGE_UPDATE_TIME = 5;
		public static final int V2_MESSAGE_UPDATE_ALARM = 6;
	}

	public static class SessionKey{
		public static final String THRESHOLD_WARNING = "org.slstudio.hsinchuiot.THRESHOLD_WARING";
		public static final String THRESHOLD_BREACH = "org.slstudio.hsinchuiot.THRESHOLD_BREACH";
		public static final String REALTIME_DATA_MONITOR_REFRESH_TIME = "org.slstudio.hsinchuiot.REALTIME_DATA_MONITOR_REFRESH_TIME";
	}
	
	public static class PreferenceKey {
		public static final String SESSION_ID = "SESSION_ID";
		public static final String LOGINNAME = "LOGIN_NAME";
		public static final String PASSWORD = "PASSWORD";
		public static final String ENCRYPTED_PASSWORD = "ENCRYPTED_PASSWORD";
		public static final String USER_ID = "USER_ID";
		public static final String LANGUAGE = "LANGUAGE";
		public static final String SERVER_PUSH_BIND_FLAG = "SERVER_PUSH_BIND_FLAG";
		public static final String SERVER_PUSH_BIND_USRID = "SERVER_PUSH_BIND_USRID";
		public static final String SERVER_PUSH_BIND_DECIVEID = "SERVER_PUSH_BIND_DECIVEID";
		
		public static final String WARNING_CO2_LOWERVALUE = "WARNING_CO2_LOWERVALUE";
		public static final String WARNING_CO2_UPPERVALUE = "WARNING_CO2_UPPERVALUE";
		public static final String WARNING_TEMPERATURE_LOWERVALUE = "WARNING_TEMPERATURE_LOWERVALUE";
		public static final String WARNING_TEMPERATURE_UPPERVALUE = "WARNING_TEMPERATURE_UPPERVALUE";
		public static final String WARNING_HUMIDITY_LOWERVALUE = "WARNING_HUMIDITY_LOWERVALUE";
		public static final String WARNING_HUMIDITY_UPPERVALUE = "WARNING_HUMIDITY_UPPERVALUE";
		
		public static final String BREACH_CO2_LOWERVALUE = "BREACH_CO2_LOWERVALUE";
		public static final String BREACH_CO2_UPPERVALUE = "BREACH_CO2_UPPERVALUE";
		public static final String BREACH_TEMPERATURE_LOWERVALUE = "BREACH_TEMPERATURE_LOWERVALUE";
		public static final String BREACH_TEMPERATURE_UPPERVALUE = "BREACH_TEMPERATURE_UPPERVALUE";
		public static final String BREACH_HUMIDITY_LOWERVALUE = "BREACH_HUMIDITY_LOWERVALUE";
		public static final String BREACH_HUMIDITY_UPPERVALUE = "BREACH_HUMIDITY_UPPERVALUE";
		
		public static final String REALTIME_DATA_MONITOR_REFRESH_TIME = "REALTIME_DATA_MONITOR_REFRESH_TIME";
		
		public static final String DEVICE_UUID = "DEVICE_UUID";
		
		public static final String ALARM_LIST = "ALARM_LIST";
		
	}
	
	public static class ConfigurationKey{
		public static final String SERVER_URL = "SERVER_URL";	
		public static final String FIX_FLAG = "FIX_FLAG";
		public static final String REALTIME_DATA_REFRESH_INTERVAL = "REALTIME_DATA_REFRESH_INTERVAL";
		public static final String REALTIME_CHART_REFRESH_INTERVAL = "REALTIME_CHART_REFRESH_INTERVAL";
	}

	public static final class Expression {
		public static final String PASSWORD = "^[\u0021-\u007E]+$";
		public static final String EMAIL = "^[a-zA-Z0-9]([\\w\\-\\.\\+]*)@([\\w\\-\\.]*)(\\.[a-zA-Z]{2,4}(\\.[a-zA-Z]{2}){0,2})$";
		public static final String NUMBER = "^[0-9]*$";
		public static final String PHONE_NUMBER = "^[\\+][0-9]*$";
	}

	public static class Action {
		public static String HSINCHUIOT_LOGIN = "org.slstudio.hsinchuiot.login";
		public static String HSINCHUIOT_MAIN = "org.slstudio.hsinchuiot.main";
		public static String HSINCHUIOT_SUPERUSER_MAIN = "org.slstudio.hsinchuiot.superuser_main";
		public static String HSINCHUIOT_SUPERUSER_SITEDETAIL = "org.slstudio.hsinchuiot.superuser_sitedetail";
		public static String HSINCHUIOT_SUPERUSER_SETTINGS = "org.slstudio.hsinchuiot.superuser_settings";
		public static String HSINCHUIOT_USER_MAIN = "org.slstudio.hsinchuiot.user_main";
		public static String HSINCHUIOT_USER_SETTINGS = "org.slstudio.hsinchuiot.user_settings";
		public static String HSINCHUIOT_USER_CHART_SETTINGS = "org.slstudio.hsinchuiot.user_chart_settings";
		public static String HSINCHUIOT_USER_SITEDETAIL = "org.slstudio.hsinchuiot.user_sitedetail";
		public static String HSINCHUIOT_USER_CHARTDETAIL = "org.slstudio.hsinchuiot.user_chartdetail";
		public static String HSINCHUIOT_SIGNUP_FIRST_BIT = "org.slstudio.hsinchuiot.signup.first.bit.sucess";
		public static String HSINCHUIOT_SITEDETAIL = "org.slstudio.hsinchuiot.sitedetail";
		public static String HSINCHUIOT_ALARM = "org.slstudio.hsinchuiot.alarm";
		
	}

	public static class ResultCode{
		public static final int CHART_SETTINGS = 1;
		public static final int ALARM_LIST = 2;
	}

	public static class ActivityPassValue{
		public static final String SELECTED_SITE = "SELECTED_SITE";
		public static final String CHART_TYPE = "CHART_TYPE";
		public static final String CHART_RT_DURATION = "CHART_RT_DURATION";
		public static final String CHART_AGGR_GRANULARITY = "CHART_AGGR_GRANULARITY";
		public static final String CHART_AGGR_STARTTIME = "CHART_AGGR_STARTTIME";
		public static final String CHART_AGGR_ENDTIME = "CHART_AGGR_ENDTIME";
		public static final String CHART_DATA = "CHART_DATA";
	}
	
	public static class ChartSettings{
		public static final int CHART_TYPE_REALTIME = 0;
		public static final int CHART_TYPE_AGGRAGATION = 1;

		public static final int GRANULARITY_SECONDS = 0;
		public static final int GRANULARITY_QUARTER = 1;
		public static final int GRANULARITY_HOUR = 2;
		public static final int GRANULARITY_HOURS = 3;
		public static final int GRANULARITY_DAY = 4;
		public static final int GRANULARITY_WEEK = 5;
		public static final int GRANULARITY_MONTH = 6;
		
	}
	
	
	public static class ServerAPIURI {
		public static final String COMMON_CHKVERSION="api/v1/check_version";//升级检测接口（提供客户端升级检测功能）
		public static final String COMMON_GETVERTIFYCODE="v1/common/getVertifyCode.json";//获取验证码接口（获取验证码接口)
		public static final String COMMON_JUDGEVERTIFYCODE="v1/common/judgeVertifyCode.json";//获取验证码接口（获取验证码接口)
		public static final String COMMON_UPLOADLOCATION="v1/common/uploadLocation.json";//上传地理位置接口
		public static final String COMMON_GETCONSTANTS="v1/common/getConstants.json";//获取客户端常量接口
		public static final String GET_SESSION_ID = "_NBI/get_session_id.lua";
		public static final String LOGIN = "_NBI/login.lua";
		public static final String DEVICE_LIST = "Device/_NBI/list.lua";
		public static final String GET_SAMPLE_DATA = "M2M/_NBI/list.lua";
		public static final String GET_QUARTER_AGG_DATA = "M2MAggByQuarter/_NBI/list.lua";
		public static final String GET_HOUR_AGG_DATA = "M2MAggByHour/_NBI/list.lua";
		public static final String GET_HOURS_AGG_DATA = "M2MAggByHours/_NBI/list.lua";
		public static final String GET_DAY_AGG_DATA = "M2MAggByDay/_NBI/list.lua";
		public static final String GET_WEEK_AGG_DATA = "M2MAggByWeek/_NBI/list.lua";
		public static final String GET_MONTH_AGG_DATA = "M2MAggByMonth/_NBI/list.lua";
		public static final String GET_REPORT_DATA = "M2M/_NBI/report.lua";
		public static final String GET_SITE_LIST_WITH_AGG_DATA = "_NBI/app_list.lua";
		public static final String GET_REALTIME_DATA_MULTIPLEDEVICES_V2 = "M2M_Last/_NBI/list.lua";
		public static final String PUSH_DEVICE_BINDING = "_NBI/mobile_dev_binding.lua";
		
		
		
		public static final String USER_LOGIN="api/session";//登陆接口
		public static final String USER_UPDATEPWD="v1/user/updatePwd.json";//修改密码和忘记
		public static final String UPLOAD_IMAGE="v1/user/updatePwd.json";
	}




	public static class ImageLoader {
		public static final int IMAGE_ENGINE_CORETHREAD = 5;
		public static DisplayImageOptions DEFAULT_IMAGE_OPTIONS = new DisplayImageOptions.Builder()
				.cacheInMemory(true).cacheOnDisk(true).build();
		public static final int IMAGE_ENGINE_FREQ_LIMITED_MEMECACHE = 2 * 1024 * 1024;
		public static final String IMAGE_ENGINE_CACHE = Environment
				.getExternalStorageDirectory().getAbsolutePath()
				+ "/hsinchuiot/images";
		public static final int IMAGE_UPLOAD_MAX_SIZE =1024 * 1024;
	}

	public static class Language{
		public static final String EN = "en";
		public static final String CN = "cn";
		public static final String TW = "tw";
	}

}
