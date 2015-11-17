package org.slstudio.hsinchuiot.service.http;

import java.io.File;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.protocol.HTTP;

import com.fasterxml.jackson.databind.ObjectMapper;


public interface HttpRequest {
	public static int SUCESS = 1;

	public String getRequestURI();

	public List<NameValuePair> getPairParameters();

	public void addParameter(String key, String value);

	public Map<String, File> getUploadFiles();

	public PostObject getPostObject();

	public void addPostObject(PostObject postObj);

	public HttpConfig getConfig();
	
	static class PostObject {
		
		
		private String token;

		private Object data;
		
		public Object getData() {
			return data;
		}

		public void setData(Object data) {
			this.data = data;
		}


		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public String toString() {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				String json = objectMapper.writeValueAsString(this);
				return URLEncoder.encode(json, HTTP.UTF_8);
			} catch (Exception e) {
				e.printStackTrace();
				return "";
			}
		}
	}

	

}
