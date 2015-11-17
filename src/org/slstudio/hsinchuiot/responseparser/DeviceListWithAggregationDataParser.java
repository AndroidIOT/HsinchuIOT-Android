package org.slstudio.hsinchuiot.responseparser;

import java.util.ArrayList;
import java.util.List;

import org.slstudio.hsinchuiot.model.Device;
import org.slstudio.hsinchuiot.model.DeviceWithAggregationData;
import org.slstudio.hsinchuiot.model.IOTMonitorData;
import org.slstudio.hsinchuiot.service.IOTException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DeviceListWithAggregationDataParser extends XmlResponseParser<List<DeviceWithAggregationData>> {

	@Override
	public List<DeviceWithAggregationData> parse(Document doc) throws IOTException {
		List<DeviceWithAggregationData> result  = new ArrayList<DeviceWithAggregationData>();
		
		Element itemsNode = (Element)doc.getElementsByTagName("Items").item(0);
		NodeList itemNodeList = itemsNode.getElementsByTagName("Item");
		
		for(int i=0; i<itemNodeList.getLength(); i++){
			Element itemNode = (Element)itemNodeList.item(i);
			String deviceID = itemNode.getElementsByTagName("did").item(0).getTextContent();
			String deviceSN = itemNode.getElementsByTagName("sn").item(0).getTextContent();
			String adminDomain = itemNode.getElementsByTagName("domain_name").item(0).getTextContent();
			
			float co2AggValue = Float.parseFloat( itemNode.getElementsByTagName("co2_eight_hours_avg").item(0).getTextContent());
			float temperatureAggValue = Float.parseFloat( itemNode.getElementsByTagName("temperature_eight_hours_avg").item(0).getTextContent());
			float humidityAggValue = Float.parseFloat( itemNode.getElementsByTagName("humidity_eight_hours_avg").item(0).getTextContent());
			
			Device device = new Device();
			device.setDeviceID(deviceID);
			device.setAdminDomain(adminDomain);
			device.setIpAddress("");
			device.setDeviceSN(deviceSN);
			
			
			IOTMonitorData data = new IOTMonitorData();
			data.setCo2(co2AggValue);
			data.setTemperature(temperatureAggValue);
			data.setHumidity(humidityAggValue);
			
			
			result.add(new DeviceWithAggregationData(device, data));
		}
		
		
		return result;
	}

}
