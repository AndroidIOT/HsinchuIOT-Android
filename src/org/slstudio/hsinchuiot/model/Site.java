package org.slstudio.hsinchuiot.model;

import java.io.Serializable;

public class Site implements Serializable {
	private String siteID;
	private String siteName;
	private Device device;
	private String siteImageFilename;
	private IOTMonitorData monitorData;

	public String getSiteID() {
		return siteID;
	}

	public void setSiteID(String siteID) {
		this.siteID = siteID;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public String getSiteImageFilename() {
		return siteImageFilename;
	}

	public void setSiteImageFilename(String siteImageFilename) {
		this.siteImageFilename = siteImageFilename;
	}

	public IOTMonitorData getMonitorData() {
		return monitorData;
	}

	public void setMonitorData(IOTMonitorData monitorData) {
		this.monitorData = monitorData;
	}

}
