package org.slstudio.hsinchuiot.model;

import java.io.Serializable;
import java.util.Date;

public class IOTSampleData implements Serializable, Comparable{
	public enum IOTSampleDataType {
		CO2, TEMPERATURE, HUMIDITY
	};

	private IOTSampleDataType type;
	private Date time;
	private float value;
	
	

	public IOTSampleData() {
	}

	public IOTSampleData(IOTSampleDataType type, Date time, float value) {
		this.type = type;
		this.time = time;
		this.value = value;
	}

	public IOTSampleDataType getType() {
		return type;
	}

	public void setType(IOTSampleDataType type) {
		this.type = type;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	@Override
	public int compareTo(Object another) {
		IOTSampleData o = (IOTSampleData)another;
		if(this.time.after(o.time)){
			return 1;
		}else if(this.time.before(o.time)){
			return -1;
		}else{
			return 0;
		}
	}

}
