package org.slstudio.hsinchuiot.upgrade;

import com.fasterxml.jackson.annotation.JsonSetter;

public class CheckUpgradeResult {

	public static final int NEEDLESS_UPGRADE = 0;
	public static final int SUGGEST_UPGRADE = 1;
	public static final int FORCE_UPGRADE = 2;

	private int upgradeSuggest;

	private String packageURL;

	public int getUpgradeSuggest() {
		return upgradeSuggest;
	}

	@JsonSetter(value = "upgrade")
	public void setUpgradeSuggest(int upgradeSuggest) {
		this.upgradeSuggest = upgradeSuggest;
	}

	public String getPackageURL() {
		return packageURL;
	}

	public void setPackageURL(String packageURL) {
		this.packageURL = packageURL;
	}
	
	public String getPackageFilename(){
		return packageURL.substring(packageURL.lastIndexOf("/")+1);
	}
	
	public String toString(){
		return "upgradeSuggest:" + upgradeSuggest + ", packageURL:" + packageURL;
	}

}
