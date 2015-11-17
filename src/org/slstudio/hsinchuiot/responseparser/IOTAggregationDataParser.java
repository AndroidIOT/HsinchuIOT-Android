package org.slstudio.hsinchuiot.responseparser;

import java.util.HashMap;
import java.util.Map;

import org.slstudio.hsinchuiot.model.IOTMonitorData;
import org.slstudio.hsinchuiot.service.IOTException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class IOTAggregationDataParser  extends XmlResponseParser<Map<String,IOTMonitorData>> {

	public static final String NAME_CO2 = "CO2";
	public static final String NAME_TEMPERATURE = "Temp";
	public static final String NAME_HUMIDITY = "Humidity";
	
	@Override
	public Map<String, IOTMonitorData> parse(Document doc) throws IOTException {
		Map<String, IOTMonitorData> result = new HashMap<String, IOTMonitorData>();
		
		
		Element itemsNode = (Element)doc.getElementsByTagName("Items").item(0);
		NodeList itemNodeList = itemsNode.getElementsByTagName("Item");
		
		for(int i=0; i<itemNodeList.getLength(); i++){
			Element itemNode = (Element)itemNodeList.item(i);
			String name = itemNode.getElementsByTagName("name").item(0).getTextContent();
			String value = itemNode.getElementsByTagName("value").item(0).getTextContent();
			String time = null;
			if (itemNode.getElementsByTagName("hour_in_epoch").getLength()>0){
				time = itemNode.getElementsByTagName("hour_in_epoch").item(0).getTextContent();
			}else if (itemNode.getElementsByTagName("hours_in_epoch").getLength()>0){
				time = itemNode.getElementsByTagName("hours_in_epoch").item(0).getTextContent();
			}else if (itemNode.getElementsByTagName("day_in_epoch").getLength()>0){
				time = itemNode.getElementsByTagName("day_in_epoch").item(0).getTextContent();
			}else if (itemNode.getElementsByTagName("week_in_epoch").getLength()>0){
				time = itemNode.getElementsByTagName("week_in_epoch").item(0).getTextContent();
			}else if (itemNode.getElementsByTagName("month_in_epoch").getLength()>0){
				time = itemNode.getElementsByTagName("month_in_epoch").item(0).getTextContent();
			}
			
			if(time != null){
				IOTMonitorData d = null;
				if(result.containsKey(time)){
					d = result.get(time);
				}else{
					d = new IOTMonitorData();
				}
				if(name.equalsIgnoreCase(NAME_CO2)){
					d.setCo2(Float.parseFloat(value));
				}else if(name.equalsIgnoreCase(NAME_TEMPERATURE)){
					d.setTemperature(Float.parseFloat(value));
				}else if(name.equalsIgnoreCase(NAME_HUMIDITY)){
					d.setHumidity(Float.parseFloat(value));
				}
				
				result.put(time, d);
			}
			
		}
		
		
		return result;
	}

}