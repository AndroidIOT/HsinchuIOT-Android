package org.slstudio.hsinchuiot.responseparser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.slstudio.hsinchuiot.model.IOTSampleData;
import org.slstudio.hsinchuiot.service.IOTException;
import org.slstudio.hsinchuiot.util.ReportUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class IOTSampleDataListParser  extends XmlResponseParser<List<IOTSampleData>> {

	public static final String NAME_CO2 = "CO2";
	public static final String NAME_TEMPERATURE = "Temp";
	public static final String NAME_HUMIDITY = "Humidity";
	
	@Override
	public List<IOTSampleData> parse(Document doc) throws IOTException {
		List<IOTSampleData>  result  = new ArrayList<IOTSampleData> ();
		
		Element itemsNode = (Element)doc.getElementsByTagName("Items").item(0);
		NodeList itemNodeList = itemsNode.getElementsByTagName("Item");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		for(int i=0; i<itemNodeList.getLength(); i++){
			Element itemNode = (Element)itemNodeList.item(i);
			String name = itemNode.getElementsByTagName("name").item(0).getTextContent();
			String value = itemNode.getElementsByTagName("value").item(0).getTextContent();
			String timeStr = itemNode.getElementsByTagName("t").item(0).getTextContent();
			
			IOTSampleData data = new IOTSampleData();
			if(name.equalsIgnoreCase(NAME_CO2)){
				data.setType(IOTSampleData.IOTSampleDataType.CO2);
			}else if(name.equalsIgnoreCase(NAME_TEMPERATURE)){
				data.setType(IOTSampleData.IOTSampleDataType.TEMPERATURE);
			}else if(name.equalsIgnoreCase(NAME_HUMIDITY)){
				data.setType(IOTSampleData.IOTSampleDataType.HUMIDITY);
			}
			try {
				data.setTime(ReportUtil.getLocalTime(sdf.parse(timeStr)));
			} catch (ParseException e) {
				throw new IOTException(-1, "parse real time error, invalid date format");
			}
			data.setValue(Float.parseFloat(value));
			
			result.add(data);
		}
		
		
		return result;
	}

}