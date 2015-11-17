package org.slstudio.hsinchuiot.service.http;

import java.io.InputStream;

import org.slstudio.hsinchuiot.service.IOTException;

public interface ResponseParser {
	
	public Object parseResponse(InputStream is) throws IOTException;
	
	public Class getResultClass();
	
}
