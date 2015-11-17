package org.slstudio.hsinchuiot.responseparser;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slstudio.hsinchuiot.service.IOTException;
import org.slstudio.hsinchuiot.service.http.ResponseParser;
import org.slstudio.hsinchuiot.util.IOTLog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public abstract class XmlResponseParser<T> implements ResponseParser {

	private Class<T> clazz;

	@Override
	public Object parseResponse(InputStream is) throws IOTException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(is);
			doc.getDocumentElement().normalize();
			

			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer t = tf.newTransformer();
			t.setOutputProperty("encoding", "GB23121");
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			t.transform(new DOMSource(doc), new StreamResult(bos));
			String xmlStr = bos.toString();
			
			IOTLog.d("XML Parser", xmlStr);
			
			NodeList errorNodeList = doc.getElementsByTagName("NBIError");
			if(errorNodeList!=null && errorNodeList.getLength()>0){
				Element errorNode = (Element)errorNodeList.item(0);
				int errorCode  = Integer.parseInt(errorNode.getElementsByTagName("Code").item(0).getTextContent());
				String errorMsg = errorNode.getElementsByTagName("String").item(0).getTextContent();
				throw new IOTException(errorCode, errorMsg);
			}
			
			return parse(doc);
		} catch (IOTException iotExp) {
			throw iotExp;
		} catch (Exception exp) {
			throw new IOTException(-3, exp.getMessage());
		}
	}

	public abstract T parse(Document doc) throws IOTException;

	@Override
	public Class getResultClass() {
		return clazz;
	}

}
