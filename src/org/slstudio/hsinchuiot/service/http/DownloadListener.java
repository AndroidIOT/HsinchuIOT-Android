package org.slstudio.hsinchuiot.service.http;

import java.io.File;

public interface DownloadListener {
	public void onStart();

	public void onComplete(File file);

	public void onProcessUpdate(int value);

	public void onException(Exception exception);
}
