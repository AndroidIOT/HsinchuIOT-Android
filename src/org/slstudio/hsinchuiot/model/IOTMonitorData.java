package org.slstudio.hsinchuiot.model;

import java.io.Serializable;

public class IOTMonitorData implements Serializable{
	private float co2;
	private float temperature;
	private float humidity;

	public IOTMonitorData(){
		
	}
	
	public IOTMonitorData(float co2, float temperature,
			float humidity) {
		super();
		this.co2 = co2;
		this.temperature = temperature;
		this.humidity = humidity;
	}

	public float getCo2() {
		return co2;
	}

	public void setCo2(float co2) {
		this.co2 = co2;
	}

	public float getTemperature() {
		return temperature;
	}

	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}

	public float getHumidity() {
		return humidity;
	}

	public void setHumidity(float humidity) {
		this.humidity = humidity;
	}

	public boolean isCO2Breach(IOTMonitorThreshold threshold) {
		return threshold.isCO2Breach(this.co2);
	}

	public boolean isTemperatureBreach(IOTMonitorThreshold threshold) {
		return threshold.isTemperatureBreach(this.temperature);
	}

	public boolean isHumidityBreach(IOTMonitorThreshold threshold) {
		return threshold.isHumidityBreach(this.humidity);
	}
}
