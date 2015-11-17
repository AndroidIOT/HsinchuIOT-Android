package org.slstudio.hsinchuiot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Session {
	private String sessionID = null;
	
	

	public Session(String sessionID) {
		super();
		this.sessionID = sessionID;
	}

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	
}
