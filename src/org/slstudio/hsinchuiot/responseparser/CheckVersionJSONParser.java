package org.slstudio.hsinchuiot.responseparser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.slstudio.hsinchuiot.service.IOTException;
import org.slstudio.hsinchuiot.service.http.ResponseParser;
import org.slstudio.hsinchuiot.upgrade.CheckUpgradeResult;
import org.slstudio.hsinchuiot.util.IOTLog;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CheckVersionJSONParser implements ResponseParser{

	private Class<CheckUpgradeResult> clz;
	
	@Override
	public Object parseResponse(InputStream is) throws IOTException {
		CheckUpgradeResult result = new CheckUpgradeResult();
		
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode node = objectMapper.readTree(in);
			IOTLog.i("HTTP Result", node.toString());
			
			int upgrade = node.get("upgrade").asInt();
			result.setUpgradeSuggest(upgrade);
			
			String url = node.get("packageURL").asText();
			result.setPackageURL(url);
			return result;
			
		} catch (Exception e) {
			e.printStackTrace();
			IOTLog.d("HttpRequestHandle", e.getMessage());
			if (e instanceof IOTException) {
				throw (IOTException) e;
			} else {
				throw new IOTException(-1, e.getMessage());
			}
		}
	}

	@Override
	public Class getResultClass() {
		return clz;
	}

}
