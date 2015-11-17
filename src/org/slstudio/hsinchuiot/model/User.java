package org.slstudio.hsinchuiot.model;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;






import org.slstudio.hsinchuiot.AppConfig;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "User")
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {
	
	public static final String PERMISSION_USER="0,33,0,0";
	public static final String PERMISSION_ADMINUSER="65664,65705,65664,0";
	

	@DatabaseField(id = true)
	@JsonProperty("user_id")
	private String userID;
	
	@DatabaseField
	private String loginName;
	
	@DatabaseField
	private String password;
	

	@DatabaseField
	@JsonProperty("super_user")
	private boolean superUser = false;

	@DatabaseField
	@JsonProperty("admin_domain")
	private String adminDomain;

	@DatabaseField
	@JsonProperty("first_login")
	private boolean firstLogin = false;

	@DatabaseField
	@JsonProperty("change_pwd_date")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date changePWDDate;

	@DatabaseField
	private String permission;

	@DatabaseField
	@JsonProperty("pwd_expiry_day")
	private int passwordExpiryDays;
	
	
	@DatabaseField
	@JsonProperty("server_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date serverTime;
	
	@DatabaseField
	@JsonProperty("Status")
	private String status;
	
	@DatabaseField
	@JsonProperty("failpwd_alert_before_day")
	private int alertBeforeDays;
	
	@DatabaseField
	@JsonProperty("license_count")
	private int licenseCount;

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isSuperUser() {
		return superUser;
	}

	public void setSuperUser(boolean superUser) {
		this.superUser = superUser;
	}

	public String getAdminDomain() {
		return adminDomain;
	}

	public void setAdminDomain(String adminDomain) {
		this.adminDomain = adminDomain;
	}

	public boolean isFirstLogin() {
		return firstLogin;
	}

	public void setFirstLogin(boolean firstLogin) {
		this.firstLogin = firstLogin;
	}

	public Date getChangePWDDate() {
		return changePWDDate;
	}

	public void setChangePWDDate(Date changePWDDate) {
		this.changePWDDate = changePWDDate;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public int getPasswordExpiryDays() {
		return passwordExpiryDays;
	}

	public void setPasswordExpiryDays(int passwordExpiryDays) {
		this.passwordExpiryDays = passwordExpiryDays;
	}

	
	public Date getServerTime() {
		return serverTime;
	}

	public void setServerTime(Date serverTime) {
		this.serverTime = serverTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getAlertBeforeDays() {
		return alertBeforeDays;
	}

	public void setAlertBeforeDays(int alertBeforeDays) {
		this.alertBeforeDays = alertBeforeDays;
	}

	public int getLicenseCount() {
		return licenseCount;
	}

	public void setLicenseCount(int licenseCount) {
		this.licenseCount = licenseCount;
	}

	public boolean isAdminUser(){
		if(AppConfig.TESTING) return true;
		
		return PERMISSION_ADMINUSER.equals(permission);
	}
	
	public boolean isNormalUser(){
		return PERMISSION_USER.equals(permission);
	}
	
	
	@Override
	public String toString() {
		return "User [uid=" + userID + "]";
	}
	
}
