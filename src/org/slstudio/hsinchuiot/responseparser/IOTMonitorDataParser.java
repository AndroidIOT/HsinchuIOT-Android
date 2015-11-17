package org.slstudio.hsinchuiot.responseparser;

import org.slstudio.hsinchuiot.model.IOTMonitorData;
import org.slstudio.hsinchuiot.service.IOTException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class IOTMonitorDataParser  extends XmlResponseParser<IOTMonitorData> {

	public static final String NAME_CO2 = "CO2";
	public static final String NAME_TEMPERATURE = "Temp";
	public static final String NAME_HUMIDITY = "Humidity";
	
	@Override
	public IOTMonitorData parse(Document doc) throws IOTException {
		IOTMonitorData result  = new IOTMonitorData();
		
		Element itemsNode = (Element)doc.getElementsByTagName("Items").item(0);
		NodeList itemNodeList = itemsNode.getElementsByTagName("Item");
		
		for(int i=0; i<itemNodeList.getLength(); i++){
			Element itemNode = (Element)itemNodeList.item(i);
			String name = itemNode.getElementsByTagName("name").item(0).getTextContent();
			String value = itemNode.getElementsByTagName("value").item(0).getTextContent();
			if(name.equalsIgnoreCase(NAME_CO2)){
				result.setCo2(Float.parseFloat(value));
			}else if(name.equalsIgnoreCase(NAME_TEMPERATURE)){
				result.setTemperature(Float.parseFloat(value));
			}else if(name.equalsIgnoreCase(NAME_HUMIDITY)){
				result.setHumidity(Float.parseFloat(value));
			}
		}
		
		
		return result;
	}

}