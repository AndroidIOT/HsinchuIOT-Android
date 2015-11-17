package org.slstudio.hsinchuiot.service.http;

import java.io.InputStream;

import org.slstudio.hsinchuiot.service.IOTException;

public class DummyResponseParser implements ResponseParser {
	@Override
	public Object parseResponse(InputStream is) throws IOTException {
		throw new IOTException(-2, "Unknown Response Parser");
	}

	@Override
	public Class getResultClass() {
		// TODO Auto-generated method stub
		return null;
	}

}
