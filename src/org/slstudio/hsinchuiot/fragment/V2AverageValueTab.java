package org.slstudio.hsinchuiot.fragment;

import java.util.List;

import org.slstudio.hsinchuiot.Constants;
import org.slstudio.hsinchuiot.R;
import org.slstudio.hsinchuiot.model.Site;
import org.slstudio.hsinchuiot.service.ServiceContainer;
import org.slstudio.hsinchuiot.service.http.ForgroundRequestListener;
import org.slstudio.hsinchuiot.service.http.HttpConfig;
import org.slstudio.hsinchuiot.service.http.HttpRequest;
import org.slstudio.hsinchuiot.service.http.NoneAuthedHttpRequest;
import android.content.Context;
import android.os.Bundle;

public class V2AverageValueTab extends V2ListViewTab {
	
		
	public V2AverageValueTab() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	
	@Override
	protected void getListViewData() {
		String sessionID = ServiceContainer.getInstance().getSessionService()
				.getSessionID();

		HttpRequest request = new NoneAuthedHttpRequest(
				new HttpConfig.GetHttpConfig(),
				Constants.ServerAPIURI.GET_SITE_LIST_WITH_AGG_DATA);

		request.addParameter("dataType", "xml");
		request.addParameter("__session_id", sessionID);

		GetSiteListListener listener = new GetSiteListListener(this.getActivity(), true,
				getString(R.string.common_please_wait));

		ServiceContainer.getInstance().getHttpHandler()
				.doRequest(request, listener);

	}



	private class GetSiteListListener extends
			ForgroundRequestListener<List<Site>> {

		public GetSiteListListener(Context context,
				boolean isShowProgressDialog, String content) {
			super(context, isShowProgressDialog, content);
		}

		@Override
		public void onRequestComplete() {
			handler.post(new Runnable() {

				@Override
				public void run() {
				}
			});

			super.onRequestComplete();
		}

		@Override
		public void onRequestResult(final List<Site> result) {

			lvAdapter.setItems(result);

			handler.post(new Runnable() {

				@Override
				public void run() {
					lvAdapter.notifyDataSetChanged();
				}

			});

		}

	}

	
}