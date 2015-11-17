package org.slstudio.hsinchuiot.responseparser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slstudio.hsinchuiot.AppConfig;
import org.slstudio.hsinchuiot.service.IOTException;
import org.slstudio.hsinchuiot.service.http.ResponseParser;
import org.slstudio.hsinchuiot.util.IOTLog;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slstudio.hsinchuiot.model.Device;
import org.slstudio.hsinchuiot.model.DeviceWithAggregationData;
import org.slstudio.hsinchuiot.model.IOTMonitorData;
import org.slstudio.hsinchuiot.model.Site;

import java.util.ArrayList;
import java.util.List;

public class SiteListJSONParser implements ResponseParser{

	private Class<List<Site>> clz;
	
	@Override
	public Object parseResponse(InputStream is) throws IOTException {
		List<Site> result = new ArrayList<Site>();
		
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode node = objectMapper.readTree(in);
			IOTLog.i("HTTP Result", node.toString());
			
			String symbol = "$";
			if(AppConfig.TESTING){
				symbol = "@";
			}
			
			int count = node.get("count").asInt();
			for(int i=1; i<=count; i++){
				int did = node.get(Integer.toString(i)).get("did").get(symbol).asInt();
				String deviceSN = node.get(Integer.toString(i)).get("sn").get(symbol).asText();
				String domainName = node.get(Integer.toString(i)).get("domain_name").get(symbol).asText();
				
				String co2Value = node.get(Integer.toString(i)).get("co2_eight_hours_avg").get(symbol).asText();
				String temperatureValue = node.get(Integer.toString(i)).get("temp_hour_avg").get(symbol).asText();
				String humidityValue = node.get(Integer.toString(i)).get("humidity_hour_avg").get(symbol).asText();
				
				Device d = new Device();
				d.setDeviceID(Integer.toString(did));
				d.setDeviceSN(deviceSN);
				d.setAdminDomain(domainName);
				
				IOTMonitorData data = new IOTMonitorData();
				if(co2Value.equals("")){
					data.setCo2(0);
				}else{
					float f1 = Float.parseFloat(co2Value);
					float f2 = (float) (Math.round(f1 * 100.0) / 100.0);
					data.setCo2(f2);
				}
				
				if(temperatureValue.equals("")){
					data.setTemperature(0);
				}else{
					float f1 = Float.parseFloat(temperatureValue);
					float f2 = (float) (Math.round(f1 * 100.0) / 100.0);
					data.setTemperature(f2);
				}
				
				if(humidityValue.equals("")){
					data.setHumidity(0);
				}else{
					float f1 = Float.parseFloat(humidityValue);
					float f2 = (float) (Math.round(f1 * 100.0) / 100.0);
					data.setHumidity(f2);
				}
				
				Site site = new Site();
				site.setDevice(d);
				site.setSiteID(d.getDeviceID());
				site.setSiteName(d.getSiteName());
				site.setSiteImageFilename("site_" + d.getDeviceSN() + ".png");
				site.setMonitorData(data);
				
				result.add(site);
				
			}
			return result;
			
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

	@Override
	public Class getResultClass() {
		return clz;
	}

}
