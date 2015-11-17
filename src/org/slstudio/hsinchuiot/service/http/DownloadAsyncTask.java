package org.slstudio.hsinchuiot.service.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slstudio.hsinchuiot.service.IOTException;
import org.slstudio.hsinchuiot.util.IOTLog;

import android.os.AsyncTask;

public class DownloadAsyncTask extends AsyncTask<URL, Integer, Object> {

	private File outputFile;

	private DownloadListener listener;

	private final String TAG = "DownloadAsyncTask";

	public DownloadAsyncTask(File outputFile, DownloadListener listener) {

		super();
		this.outputFile = outputFile;
		this.listener = listener;
	}

	public DownloadAsyncTask() {
		super();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		if (listener != null) {
			listener.onProcessUpdate(values[0]);
		}
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPreExecute() {
		if (listener != null) {
			listener.onStart();
		}
		super.onPreExecute();
	}

	@Override
	protected Object doInBackground(URL... params) {
		File file  = null;
		try {
			// URLConnection con = params[0].openConnection();

			// /////////////////////////////////////////////////////////////

			HttpURLConnection con = (HttpURLConnection) params[0]
					.openConnection();
			if (con.getURL().getProtocol().toLowerCase().startsWith("https")) {
				SSLContext context = SSLContext.getInstance("SSL");
				context.init(null,
						new TrustManager[] { new TrustAnyTrustManager() },
						new SecureRandom());

				((HttpsURLConnection) con).setSSLSocketFactory(context
						.getSocketFactory());
				((HttpsURLConnection) con)
						.setHostnameVerifier(new TrustAnyHostnameVerifier());
			}

			con.connect();

			// //////////////////////////////////////////////

			if (HttpURLConnection.HTTP_OK != con.getResponseCode()) {
				throw new IOTException(-2001, "Connection failed");
			}
			InputStream is = con.getInputStream();
			int contentlength = con.getContentLength();
			if(contentlength < 0){
				throw new IOTException(-2002, "Unknown download file size");
			}

			file = outputFile;

			file.deleteOnExit();

			FileOutputStream out = new FileOutputStream(file);
			int current = 0;
			int x = 0;
			byte[] arr = new byte[1024];
			while ((current = is.read(arr)) != -1) {
				out.write(arr, 0, current);
				x = x + current;
				publishProgress((int) (100 * x / contentlength));
			}
			is.close();
			out.close();
			
			if (listener != null) {
				listener.onComplete(file);
			}
			
		} catch (Exception e) {
			if (listener != null) {
				listener.onException(e);
			}
			IOTLog.d("UpgradeHandler", "debuginfo(UPGRADE) - download task failure:" + e.getMessage());
		}
		return null;
	}

	public class TrustAnyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	public class TrustAnyTrustManager implements X509TrustManager {
		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[] {};
		}
	}
}
