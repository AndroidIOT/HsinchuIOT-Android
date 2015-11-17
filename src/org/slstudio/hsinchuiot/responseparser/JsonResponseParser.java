package org.slstudio.hsinchuiot.responseparser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slstudio.hsinchuiot.service.IOTException;
import org.slstudio.hsinchuiot.service.http.ResponseParser;
import org.slstudio.hsinchuiot.util.IOTLog;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class JsonResponseParser implements ResponseParser{
 
	@Override
	public Object parseResponse(InputStream is) throws IOTException {
		
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode node = objectMapper.readTree(in);
			IOTLog.i("HTTP Result", node.toString());
			return objectMapper.readValue(node.toString(),getResultClass());
		
		} catch (Exception e) {
			e.printStackTrace();
			IOTLog.d("HttpRequestHandle", e.getMessage());
			if (e instanceof IOTException) {
				throw (IOTException) e;
			} else {
				throw new IOTException(-1, "服务器异常");
			}
		}
	}

	public abstract Class getResultClass();

}
