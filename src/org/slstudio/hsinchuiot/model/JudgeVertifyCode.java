package org.slstudio.hsinchuiot.model;

import com.fasterxml.jackson.annotation.JsonSetter;


public class JudgeVertifyCode {
	private boolean verified;

	public boolean isVerified() {
		return verified;
	}

	@JsonSetter(value = "isVerify")
	public void setVerified(boolean verified) {
		this.verified = verified;
	}

}