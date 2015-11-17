package org.slstudio.hsinchuiot.responseparser;

import java.io.ByteArrayOutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slstudio.hsinchuiot.service.IOTException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class PushDeviceBindingParser extends XmlResponseParser<String> {

	@Override
	public String parse(Document doc) throws IOTException {
		// TODO Auto-generated method stub
		
		NodeList statusNodeList = doc.getElementsByTagName("Status");  
        if(statusNodeList == null || statusNodeList.getLength()==0){
        	throw new IOTException(-3, "Status Unknown");
        }
        Element statusElem = (Element)statusNodeList.item(0);  
        String status = statusElem.getTextContent();
        return status;
        
        /*
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer t = tf.newTransformer();
			t.setOutputProperty("encoding", "GB23121");// 解决中文问题，试过用GBK不行
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			t.transform(new DOMSource(doc), new StreamResult(bos));
			String xmlStr = bos.toString();
			return xmlStr;
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			throw new IOTException(-2, "get push register response error:" + e.getMessage());
		}
		*/

	}

}
