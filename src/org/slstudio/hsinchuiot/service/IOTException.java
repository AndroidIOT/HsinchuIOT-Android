package org.slstudio.hsinchuiot.service;

public class IOTException extends Exception {

	protected int errorCode = 0;

	public IOTException(int errorCode, String message) {
		super(message);
		this.errorCode = errorCode;

	}

	public final int getErrorCode() {
		return this.errorCode;
	}

	@Override
	public String toString() {
		return "Error Code :" + errorCode + ";Message:" + getMessage();
	}
}
