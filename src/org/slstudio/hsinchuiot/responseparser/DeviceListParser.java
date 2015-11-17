package org.slstudio.hsinchuiot.responseparser;

import java.util.ArrayList;
import java.util.List;

import org.slstudio.hsinchuiot.model.Device;
import org.slstudio.hsinchuiot.service.IOTException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DeviceListParser extends XmlResponseParser<List<Device>> {

	@Override
	public List<Device> parse(Document doc) throws IOTException {
		List<Device> result  = new ArrayList<Device>();
		
		Element itemsNode = (Element)doc.getElementsByTagName("Items").item(0);
		NodeList itemNodeList = itemsNode.getElementsByTagName("Item");
		
		for(int i=0; i<itemNodeList.getLength(); i++){
			Element itemNode = (Element)itemNodeList.item(i);
			String deviceID = itemNode.getElementsByTagName("id").item(0).getTextContent();
			String deviceIP = itemNode.getElementsByTagName("ip").item(0).getTextContent();
			String deviceSN = itemNode.getElementsByTagName("sn").item(0).getTextContent();
			String adminDomain = itemNode.getElementsByTagName("admin_domain").item(0).getTextContent();
			
			Device device = new Device();
			device.setDeviceID(deviceID);
			device.setAdminDomain(adminDomain);
			device.setIpAddress(deviceIP);
			device.setDeviceSN(deviceSN);
			
			result.add(device);
		}
		
		
		return result;
	}

}
