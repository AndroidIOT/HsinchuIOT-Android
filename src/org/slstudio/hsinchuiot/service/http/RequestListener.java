package org.slstudio.hsinchuiot.service.http;

public interface RequestListener<T> {

	public void onRequestStart();

	public void onRequestGetControl(RequestControl control);

	public void onRequestCancelled();

	public void onRequestError(Exception e);

	public void onRequestResult(T result);

	public void onRequestComplete();

	public static class DefaultRequestListener<Object> implements
			RequestListener<Object> {

		@Override
		public void onRequestStart() {
		}

		@Override
		public void onRequestGetControl(RequestControl control) {
		}

		@Override
		public void onRequestCancelled() {
		}

		@Override
		public void onRequestError(Exception e) {
		}

		@Override
		public void onRequestResult(Object result) {
		}

		@Override
		public void onRequestComplete() {
		}

	}
}
