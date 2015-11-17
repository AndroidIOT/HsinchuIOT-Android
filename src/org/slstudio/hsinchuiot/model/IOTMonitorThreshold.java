package org.slstudio.hsinchuiot.model;

import java.io.Serializable;

public class IOTMonitorThreshold implements Serializable{
	private int co2LowerBound = Integer.MIN_VALUE;
	private int co2UpperBound = Integer.MAX_VALUE;
	private int temperatureLowerBound = Integer.MIN_VALUE;
	private int temperatureUpperBound = Integer.MAX_VALUE;
	private int humidityLowerBound = 0;
	private int humidityUpperBound = 100;
	
	
	public IOTMonitorThreshold(){
		this.co2LowerBound = Integer.MIN_VALUE;
		this.co2UpperBound = Integer.MAX_VALUE;
		this.temperatureLowerBound = Integer.MIN_VALUE;
		this.temperatureUpperBound = Integer.MAX_VALUE;
		this.humidityLowerBound = 0;
		this.humidityUpperBound = 100;
	}
	
	public IOTMonitorThreshold(int co2LowerBound, int co2UpperBound,
			int temperatureLowerBound, int temperatureUpperBound,
			int humidityLowerBound, int humidityUpperBound) {
		super();
		this.co2LowerBound = co2LowerBound;
		this.co2UpperBound = co2UpperBound;
		this.temperatureLowerBound = temperatureLowerBound;
		this.temperatureUpperBound = temperatureUpperBound;
		this.humidityLowerBound = humidityLowerBound;
		this.humidityUpperBound = humidityUpperBound;
	}
	
	public IOTMonitorThreshold(IOTMonitorThreshold value){
		this.co2LowerBound = value.co2LowerBound;
		this.co2UpperBound = value.co2UpperBound;
		this.temperatureLowerBound = value.temperatureLowerBound;
		this.temperatureUpperBound = value.temperatureUpperBound;
		this.humidityLowerBound = value.humidityLowerBound;
		this.humidityUpperBound = value.humidityUpperBound;
	}

	public int getCo2LowerBound() {
		return co2LowerBound;
	}

	public void setCo2LowerBound(int co2LowerBound) {
		this.co2LowerBound = co2LowerBound;
	}

	public int getCo2UpperBound() {
		return co2UpperBound;
	}

	public void setCo2UpperBound(int co2UpperBound) {
		this.co2UpperBound = co2UpperBound;
	}

	public int getTemperatureLowerBound() {
		return temperatureLowerBound;
	}

	public void setTemperatureLowerBound(int temperatureLowerBound) {
		this.temperatureLowerBound = temperatureLowerBound;
	}

	public int getTemperatureUpperBound() {
		return temperatureUpperBound;
	}

	public void setTemperatureUpperBound(int temperatureUpperBound) {
		this.temperatureUpperBound = temperatureUpperBound;
	}

	public int getHumidityLowerBound() {
		return humidityLowerBound;
	}

	public void setHumidityLowerBound(int humidityLowerBound) {
		this.humidityLowerBound = humidityLowerBound;
	}

	public int getHumidityUpperBound() {
		return humidityUpperBound;
	}

	public void setHumidityUpperBound(int humidityUpperBound) {
		this.humidityUpperBound = humidityUpperBound;
	}
	
	public boolean isCO2Breach(float co2Value) {
		return !(co2Value >= this.getCo2LowerBound() && co2Value <= this
				.getCo2UpperBound());
	}

	public boolean isTemperatureBreach(float temperatureValue) {
		return !(temperatureValue >= this.getTemperatureLowerBound() && temperatureValue <= this
				.getTemperatureUpperBound());
	}

	public boolean isHumidityBreach(float humidityValue) {
		return !(humidityValue >= this.getHumidityLowerBound() && humidityValue <= this
				.getHumidityUpperBound());
	}

}
