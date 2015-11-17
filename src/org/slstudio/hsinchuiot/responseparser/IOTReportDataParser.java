package org.slstudio.hsinchuiot.responseparser;

import org.slstudio.hsinchuiot.model.IOTReportData;
import org.slstudio.hsinchuiot.service.IOTException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class IOTReportDataParser extends XmlResponseParser<IOTReportData> {

	public static final String NAME_CO2 = "CO2";
	public static final String NAME_TEMPERATURE = "Temp";
	public static final String NAME_HUMIDITY = "Humidity";
	@Override
	public IOTReportData parse(Document doc) throws IOTException {
		IOTReportData result  = new IOTReportData();
		
		Element itemsNode = (Element)doc.getElementsByTagName("Items").item(0);
		NodeList itemNodeList = itemsNode.getElementsByTagName("Item");
		
		for(int i=0; i<itemNodeList.getLength(); i++){
			Element itemNode = (Element)itemNodeList.item(i);
			String name = itemNode.getElementsByTagName("name").item(0).getTextContent();
			String averageValue = itemNode.getElementsByTagName("__avg_value").item(0).getTextContent();
			String maxValue = itemNode.getElementsByTagName("__max_value").item(0).getTextContent();
			String minValue = itemNode.getElementsByTagName("__min_value").item(0).getTextContent();
			
			if(name.equalsIgnoreCase(NAME_CO2)){
				result.setAverageValueCO2(Float.parseFloat(averageValue));
				result.setMaxValueCO2(Float.parseFloat(maxValue));
				result.setMinValueCO2(Float.parseFloat(minValue));
			}else if(name.equalsIgnoreCase(NAME_TEMPERATURE)){
				result.setAverageValueTemperature(Float.parseFloat(averageValue));
				result.setMaxValueTemperature(Float.parseFloat(maxValue));
				result.setMinValueTemperature(Float.parseFloat(minValue));
			}else if(name.equalsIgnoreCase(NAME_HUMIDITY)){
				result.setAverageValueHumidity(Float.parseFloat(averageValue));
				result.setMaxValueHumidity(Float.parseFloat(maxValue));
				result.setMinValueHumidity(Float.parseFloat(minValue));
			}
		}
		
		
		return result;
	}

}
