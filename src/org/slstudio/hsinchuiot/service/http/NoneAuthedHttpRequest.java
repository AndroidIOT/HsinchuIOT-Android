package org.slstudio.hsinchuiot.service.http;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slstudio.hsinchuiot.Constants;

public class NoneAuthedHttpRequest implements HttpRequest {
	List<NameValuePair> parameters = new ArrayList<NameValuePair>();
	protected String requestURI = Constants.DEFAULT_URI;
	
	protected PostObject postObject = null;
	protected Map<String, File> files = new HashMap<String, File>();

	public HttpConfig config = new HttpConfig.DefaultHttpConfig();

	public NoneAuthedHttpRequest(HttpConfig config, String requestURI) {
		this.requestURI = requestURI;
		this.config = config;
	}

	private NoneAuthedHttpRequest() {

	}

	@Override
	public String getRequestURI() {
		return requestURI;
	}

	@Override
	public List<NameValuePair> getPairParameters() {
		return parameters;
	}

	@Override
	public void addParameter(String key, String value) {
		parameters.add(new BasicNameValuePair(key, value));

	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (NameValuePair nameValuePair : parameters) {
			sb.append(nameValuePair.getName() + "=" + nameValuePair.getValue()
					+ ";");
		}
		return sb.toString();
	}

	public Map<String, File> getUploadFiles() {
		return files;
	}

	public void addUploadFile(String fileName, File uploadFile) {
		files.put(fileName, uploadFile);
	}

	@Override
	public PostObject getPostObject() {
		return postObject;
	}

	@Override
	public void addPostObject(PostObject postObj) {
		this.postObject = postObj;

	}

	@Override
	public HttpConfig getConfig() {
		return config;
	}

}
