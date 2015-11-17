package org.slstudio.hsinchuiot.model;

import java.io.Serializable;

public class Device implements Serializable {
	private String deviceID;
	private String deviceSN;
	private String adminDomain;
	private String ipAddress;

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public String getDeviceSN() {
		return deviceSN;
	}

	public void setDeviceSN(String deviceSN) {
		this.deviceSN = deviceSN;
	}

	public String getAdminDomain() {
		return adminDomain;
	}

	public void setAdminDomain(String adminDomain) {
		this.adminDomain = adminDomain;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getSiteName() {
		String siteName = null;
		if (adminDomain != null) {
			if(adminDomain.lastIndexOf(".") != -1){
				siteName = adminDomain.substring(0, adminDomain.lastIndexOf("."));
				siteName = siteName.substring(siteName.lastIndexOf(".") + 1);
			}else{
				siteName = adminDomain;
			}
		} else {
			siteName = "Unknown";
		}
		return siteName;
	}

}
