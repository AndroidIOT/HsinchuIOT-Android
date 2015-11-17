package org.slstudio.hsinchuiot.model;

import java.io.Serializable;

public class DeviceWithAggregationData implements Serializable {
	private Device device;
	private IOTMonitorData aggregationData;

	public DeviceWithAggregationData(Device device,
			IOTMonitorData aggregationData) {
		super();
		this.device = device;
		this.aggregationData = aggregationData;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public IOTMonitorData getAggregationData() {
		return aggregationData;
	}

	public void setAggregationData(IOTMonitorData aggregationData) {
		this.aggregationData = aggregationData;
	}

}
