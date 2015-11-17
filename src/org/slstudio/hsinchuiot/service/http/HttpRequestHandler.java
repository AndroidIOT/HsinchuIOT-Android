package org.slstudio.hsinchuiot.service.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.slstudio.hsinchuiot.AppConfig;
import org.slstudio.hsinchuiot.service.IOTException;
import org.slstudio.hsinchuiot.service.http.HttpConfig.Method;
import org.slstudio.hsinchuiot.util.IOTLog;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpRequestHandler {
	private static final String MESSAGE = "statusMessage";
	private static final String RESULT = "statusCode";
	private static final String DATA = "data";

	private static final String APPLICATION_JSON = "application/json";

	private ExecutorService service;

	public HttpRequestHandler() {
		service = Executors.newFixedThreadPool(AppConfig.MAX_HTTP_THREADS);
	}

	public void doRequest(HttpRequest request, RequestListener listener) {

		if (listener != null) {
			listener.onRequestStart();
		}
		service.execute(new RequestProcessor(request, listener));
	}

	private class RequestProcessor implements Runnable {

		HttpClient client;
		HttpRequest request;
		RequestListener listener;
		ResponseParser parser;
		HttpConfig config;
		static final String BOUNDARY = "-----------ThIs_Is_tHe_bouNdaRY_$";

		public RequestProcessor(HttpRequest hr, RequestListener listener) {
			super();
			config = hr.getConfig();
			if (config.getHostName().startsWith("https")) {
				client = SSLSocketFactoryEx.getNewHttpClient();
			} else {
				client = new DefaultHttpClient();
			}
			this.request = hr;
			this.listener = listener;
			this.parser = ResponseParseMapping.getResponseParser(hr.getRequestURI());
		}
		

		@Override
		public void run() {
			Exception fe = null;
			InputStream is = null;
			try {
				String url = config.getHostName() + ":" + config.getHostPort()
						+ "/" + request.getRequestURI();
				IOTLog.i("HTTPRequest", url);
				List<NameValuePair> pairParameters = request
						.getPairParameters();
				Method method = config.getMethod();
				HttpUriRequest uriRequest = null;

				switch (method) {
				case DELETE:
					uriRequest = createDeleteRequest(pairParameters, url);
					break;
				case GET:
					uriRequest = createGetRequest(pairParameters, url);
					break;
				case PUT:
					uriRequest = createPutRequest(pairParameters, url);
					break;
				default:
					uriRequest = createPostRequest(pairParameters, url);
					break;

				}
				final HttpUriRequest hur = uriRequest;
				listener.onRequestGetControl(new RequestControl() {

					@Override
					public void cancel() {
						try {
							new Thread(new Runnable() {
								public void run() {
									hur.abort();
								};

							}).start();

						} catch (Exception e) {
							IOTLog.e("HTTP", e.toString(), e);
						}
					}
				});
				HttpResponse response = client.execute(uriRequest);
				// Read to String buffer
				
				is = response.getEntity().getContent();
				
				Object result = handleResult(is);

				if (result != null) {
					listener.onRequestResult(result);
				}
			} catch (IOTException e) {
				fe = e;
			} catch (Exception e) {
				e.printStackTrace();
				fe = new IOTException(-1, e.getMessage());
			} finally {
				if (fe != null) {
					IOTLog.e("ReqeustProcessor.run(): Exception", fe.toString());
					listener.onRequestError(fe);
				}
				listener.onRequestComplete();
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}

		}

		private HttpUriRequest createPutRequest(
				List<NameValuePair> pairParameters, String url)
				throws UnsupportedEncodingException, URISyntaxException {
			final HttpPut put = new HttpPut();
			if (pairParameters != null) {
				IOTLog.i("HTTPRequest", pairParameters.toString());

				if (request.getUploadFiles().size() > 0) {
					MultipartEntity entity = new MultipartEntity(
							HttpMultipartMode.BROWSER_COMPATIBLE, BOUNDARY,
							Charset.forName(HTTP.UTF_8));
					for (NameValuePair nameValuePair : pairParameters) {
						Charset charset = Charset.forName(HTTP.UTF_8);
						entity.addPart(nameValuePair.getName(), new StringBody(
								nameValuePair.getValue(), charset));
					}
					Set<String> fileNames = request.getUploadFiles().keySet();
					for (String key : fileNames) {
						FileBody fb = new FileBody(request.getUploadFiles()
								.get(key), config.getMimeType());
						entity.addPart(key, fb);
					}

					put.setEntity(entity);
				} else {
					put.addHeader(HTTP.CONTENT_TYPE,
							"application/x-www-form-urlencoded; charset=utf-8");
					put.setEntity(new UrlEncodedFormEntity(pairParameters,
							HTTP.UTF_8));

				}
			}
			if (request.getPostObject() != null) {
				put.removeHeaders(HTTP.CONTENT_TYPE);
				put.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
				StringEntity se = new StringEntity(request.getPostObject()
						.toString());
				se.setContentEncoding(HTTP.UTF_8);
				put.setEntity(se);
			}
			put.setURI(new URI(url));
			return put;
		}

		private HttpUriRequest createPostRequest(
				List<NameValuePair> pairParameters, String url)
				throws UnsupportedEncodingException, URISyntaxException {
			final HttpPost post = new HttpPost();
			if (pairParameters != null) {
				IOTLog.i("HTTPRequest", pairParameters.toString());

				if (request.getUploadFiles().size() > 0) {
					MultipartEntity entity = new MultipartEntity(
							HttpMultipartMode.BROWSER_COMPATIBLE, BOUNDARY,
							Charset.forName(HTTP.UTF_8));
					for (NameValuePair nameValuePair : pairParameters) {
						Charset charset = Charset.forName(HTTP.UTF_8);
						entity.addPart(nameValuePair.getName(), new StringBody(
								nameValuePair.getValue(), charset));
					}
					Set<String> fileNames = request.getUploadFiles().keySet();
					for (String key : fileNames) {
						FileBody fb = new FileBody(request.getUploadFiles()
								.get(key), config.getMimeType());
						entity.addPart(key, fb);
					}

					post.setEntity(entity);
				} else {
					post.addHeader(HTTP.CONTENT_TYPE,
							"application/x-www-form-urlencoded; charset=utf-8");
					post.setEntity(new UrlEncodedFormEntity(pairParameters,
							HTTP.UTF_8));

				}
			}
			if (request.getPostObject() != null) {
				post.removeHeaders(HTTP.CONTENT_TYPE);
				post.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
				StringEntity se = new StringEntity(request.getPostObject()
						.toString());
				se.setContentEncoding(HTTP.UTF_8);
				post.setEntity(se);
			}
			post.setURI(new URI(url));
			return post;
		}

		private HttpUriRequest createGetRequest(
				List<NameValuePair> pairParameters, String url) {
			if (pairParameters != null && pairParameters.size() > 0
					&& url.indexOf("?") < 0) {
				url = url + "?";
			}
			for (NameValuePair nameValuePair : pairParameters) {
				url = url + nameValuePair.getName() + "="
						+ nameValuePair.getValue() + "&";
			}
			url = url.replaceAll(" ", "%20");
			IOTLog.i("HTTP", url);
			return new HttpGet(url);
		}

		private HttpUriRequest createDeleteRequest(
				List<NameValuePair> pairParameters, String url) {
			if (pairParameters != null && pairParameters.size() > 0
					&& url.indexOf("?") < 0) {
				url = url + "?";
			}
			for (NameValuePair nameValuePair : pairParameters) {
				url = url + nameValuePair.getName() + "="
						+ nameValuePair.getValue() + "&";
			}
			IOTLog.i("HTTP", url);
			return new HttpDelete(url);
		}

		private Object handleResult(InputStream is) throws IOTException {
			return parser.parseResponse(is);
		}

	}

}
