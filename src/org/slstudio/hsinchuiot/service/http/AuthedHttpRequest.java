package org.slstudio.hsinchuiot.service.http;

import org.slstudio.hsinchuiot.service.ServiceContainer;

public class AuthedHttpRequest extends NoneAuthedHttpRequest {


	public AuthedHttpRequest(HttpConfig config, String requestURI) {
		super(config, requestURI);
		init();

	}

	private void init() {
		String token = ServiceContainer.getInstance().getPerferenceService()
				.getSessionId();
		addParameter("token", token);
		String version = ServiceContainer.getInstance().getVersion();
		addParameter("clientVersion", version);
		if (postObject != null) {
			postObject.setToken(token);
		}

	}

}
