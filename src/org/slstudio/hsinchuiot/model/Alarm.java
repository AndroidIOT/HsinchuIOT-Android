package org.slstudio.hsinchuiot.model;

import java.util.StringTokenizer;

public class Alarm {
	
	public static final String ALARM_TYPE_BREACHED = "超標";
	public static final String ALARM_TYPE_WARNING = "接近超標";

	private String alarmTime;
	private String deviceID;
	private String alarmSite;
	private String alarmValueType;
	private String alarmValue;
	private String alarmType;
	
	public Alarm(String alarmString){
		StringTokenizer st = new StringTokenizer(alarmString, ";");
		String time = st.nextToken();
		String deviceID = st.nextToken();
		String siteName = st.nextToken();
		String alarmValueType = st.nextToken();
		String alarmValue = st.nextToken();
		String alarmType = st.nextToken();
		
		this.alarmTime = time;
		this.deviceID = deviceID;
		this.alarmSite = siteName;
		this.alarmValueType = alarmValueType;
		this.alarmValue = alarmValue;
		this.alarmType = alarmType;
	}

	public Alarm(String alarmTime, String deviceID, String alarmSite,
			String alarmValueType, String alarmValue, String alarmType) {
		this.alarmTime = alarmTime;
		this.deviceID = deviceID;
		this.alarmSite = alarmSite;
		this.alarmValueType = alarmValueType;
		this.alarmValue = alarmValue;
		this.alarmType = alarmType;
	}

	public String getAlarmSite() {
		return alarmSite;
	}

	public void setAlarmSite(String alarmSite) {
		this.alarmSite = alarmSite;
	}

	public String getAlarmTime() {
		return alarmTime;
	}

	public void setAlarmTime(String alarmTime) {
		this.alarmTime = alarmTime;
	}

	public String getAlarmValueType() {
		return alarmValueType;
	}

	public void setAlarmValueType(String alarmValueType) {
		this.alarmValueType = alarmValueType;
	}

	public String getAlarmValue() {
		return alarmValue;
	}

	public void setAlarmValue(String alarmValue) {
		this.alarmValue = alarmValue;
	}

	public String getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}
	
	


	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public boolean isWarning() {
		return ALARM_TYPE_WARNING.equals(alarmType);
	}
	
	public boolean isBreached(){
		return ALARM_TYPE_BREACHED.equals(alarmType);
	}
	
	@Override
	public String toString(){
		return alarmTime + ";" + deviceID + ";" + alarmSite + ";" + alarmValueType 
				+ ";" + alarmValue + ";" + alarmType;
	}

}
