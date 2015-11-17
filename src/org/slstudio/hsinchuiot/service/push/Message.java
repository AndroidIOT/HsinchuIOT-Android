package org.slstudio.hsinchuiot.service.push;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Message implements Serializable {

	private String type;

	private String userId;

	public String getType() {
		return type;
	}

	@JsonSetter(value = "name")
	public void setType(String type) {
		this.type = type;
	}

	public String getUserId() {
		return userId;
	}

	@JsonSetter(value = "baiduUserId")
	public void setUserId(String userId) {
		this.userId = userId;
	}

}