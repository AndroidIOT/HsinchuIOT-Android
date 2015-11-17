package org.slstudio.hsinchuiot.responseparser;

import org.slstudio.hsinchuiot.model.Session;
import org.slstudio.hsinchuiot.service.IOTException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class SessionIDParser extends XmlResponseParser<Session>{

	@Override
	public Session parse(Document doc) throws IOTException {
		NodeList idNodeList = doc.getElementsByTagName("SessionID");  
        if(idNodeList == null || idNodeList.getLength()==0){
        	throw new IOTException(-3, "No Session ID found");
        }
        Element idElem = (Element)idNodeList.item(0);  
        String sessionID = idElem.getTextContent();
        if(sessionID == null || sessionID.equals("")){
        	throw new IOTException(-3, "Session ID is empty");
        }
      
        return new Session(sessionID);
	}

}
