package org.slstudio.hsinchuiot.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.slstudio.hsinchuiot.AppConfig;
import org.slstudio.hsinchuiot.Constants;
import org.slstudio.hsinchuiot.R;
import org.slstudio.hsinchuiot.SuperUserMainActivity;
import org.slstudio.hsinchuiot.UserMainActivity;
import org.slstudio.hsinchuiot.model.Device;
import org.slstudio.hsinchuiot.model.IOTMonitorData;
import org.slstudio.hsinchuiot.model.IOTSampleData;
import org.slstudio.hsinchuiot.model.Site;
import org.slstudio.hsinchuiot.service.ServiceContainer;
import org.slstudio.hsinchuiot.service.http.ForgroundRequestListener;
import org.slstudio.hsinchuiot.service.http.HttpConfig;
import org.slstudio.hsinchuiot.service.http.HttpRequest;
import org.slstudio.hsinchuiot.service.http.NoneAuthedHttpRequest;
import org.slstudio.hsinchuiot.service.http.RequestControl;
import org.slstudio.hsinchuiot.service.http.RequestListener;
import org.slstudio.hsinchuiot.util.IOTLog;
import org.slstudio.hsinchuiot.util.ReportUtil;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;

public class V2RTDataTab extends V2ListViewTab {

	private List<Device> deviceList = new ArrayList<Device>();

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
				Constants.ServerAPIURI.DEVICE_LIST);

		request.addParameter("dataType", "xml");
		request.addParameter("__session_id", sessionID);
		request.addParameter("__page_no", "1");
		request.addParameter("__page_size", "1000");
		request.addParameter("__sort", "-id");

		GetDeviceListListener listener = new GetDeviceListListener(
				this.getActivity(), true,
				getString(R.string.common_please_wait));

		ServiceContainer.getInstance().getHttpHandler()
				.doRequest(request, listener);
	}

	private void sendQueryRealtimeDataRequest(String deviceID) {

		HttpRequest request = new NoneAuthedHttpRequest(
				new HttpConfig.GetHttpConfig(),
				Constants.ServerAPIURI.GET_SAMPLE_DATA);
		String sessionID = ServiceContainer.getInstance().getSessionService()
				.getSessionID();
		request.addParameter("dataType", "xml");
		request.addParameter("__session_id", sessionID);
		request.addParameter("__page_no", "1");
		request.addParameter("__column", "did,sensor,name,value,t");
		request.addParameter("__having_max", "id");
		request.addParameter("__group_by", "did,name");
		request.addParameter("__sort", "-id");
		request.addParameter("did[0]", deviceID);

		GetRealtimeDataListener l = new GetRealtimeDataListener(deviceID);

		ServiceContainer.getInstance().getHttpHandler().doRequest(request, l);
	}

	private void sendQueryMultipleDevicesRealtimeDataRequest(
			List<Device> devices) {

		HttpRequest request = new NoneAuthedHttpRequest(
				new HttpConfig.GetHttpConfig(),
				Constants.ServerAPIURI.GET_REALTIME_DATA_MULTIPLEDEVICES_V2);
		String sessionID = ServiceContainer.getInstance().getSessionService()
				.getSessionID();
		request.addParameter("dataType", "xml");
		request.addParameter("__session_id", sessionID);
		request.addParameter("__page_no", "1");
		request.addParameter("__column", "did,sensor,name,value,t");
		request.addParameter("__having_max", "id");
		request.addParameter("__group_by", "did,name");
		request.addParameter("__sort", "-id");

		for (int i = 0; i < devices.size(); i++) {
			request.addParameter("did[" + i + "]", devices.get(i).getDeviceID());
		}

		GetMultipleDevicesRealtimeDataListener l = new GetMultipleDevicesRealtimeDataListener();

		ServiceContainer.getInstance().getHttpHandler().doRequest(request, l);
	}

	private class GetDeviceListListener extends
			ForgroundRequestListener<List<Device>> {

		public GetDeviceListListener(Context context,
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
		public void onRequestResult(final List<Device> result) {
			deviceList = result;
			List<Site> sites = new ArrayList<Site>();
			for (Device d : deviceList) {
				Site site = new Site();
				site.setSiteID(d.getDeviceID());
				site.setDevice(d);
				site.setSiteName(d.getSiteName());
				site.setSiteImageFilename("site_" + d.getDeviceSN() + ".png");
				site.setMonitorData(new IOTMonitorData(0, 0, 0));
				sites.add(site);
			}

			lvAdapter.setItems(sites);
			if(AppConfig.TESTING){
				sendQueryMultipleDevicesRealtimeDataRequest(deviceList);
			}else{
			
				for (Device d : deviceList) {
					sendQueryRealtimeDataRequest(d.getDeviceID());
				}
			 
			}
			handler.post(new Runnable() {

				@Override
				public void run() {
					lvAdapter.notifyDataSetChanged();
				}

			});

		}

	}

	private class GetRealtimeDataListener implements
			RequestListener<List<IOTSampleData>> {
		private RequestControl control;
		private String deviceID;

		public GetRealtimeDataListener(String deviceID) {
			this.deviceID = deviceID;
		}

		@Override
		public void onRequestCancelled() {
			if (control != null)
				control.cancel();

		}

		@Override
		public void onRequestResult(final List<IOTSampleData> result) {

			final IOTMonitorData data = new IOTMonitorData();
			for (IOTSampleData sample : result) {
				if (sample.getType() == IOTSampleData.IOTSampleDataType.CO2) {
					data.setCo2(sample.getValue());
				} else if (sample.getType() == IOTSampleData.IOTSampleDataType.TEMPERATURE) {
					data.setTemperature(sample.getValue());
				} else if (sample.getType() == IOTSampleData.IOTSampleDataType.HUMIDITY) {
					data.setHumidity(sample.getValue());
				}
			}

			List<Site> sites = lvAdapter.getItems();

			for (Site s : sites) {
				if (s.getDevice().getDeviceID().equals(deviceID)) {
					s.setMonitorData(data);
					break;
				}
			}

			lvAdapter.setItems(sites);

			handler.post(new Runnable() {

				@Override
				public void run() {
					lvAdapter.notifyDataSetChanged();
				}

			});
		}

		@Override
		public void onRequestGetControl(RequestControl control) {
			this.control = control;
		}

		@Override
		public void onRequestStart() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRequestError(Exception e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRequestComplete() {
		}
	}

	private class GetMultipleDevicesRealtimeDataListener implements
			RequestListener<Map<String, List<IOTSampleData>>> {
		private RequestControl control;

		public GetMultipleDevicesRealtimeDataListener() {
		}

		@Override
		public void onRequestCancelled() {
			if (control != null)
				control.cancel();

		}

		@Override
		public void onRequestResult(
				final Map<String, List<IOTSampleData>> result) {

			
			
			

			List<Site> sites = lvAdapter.getItems();

			for (Site s : sites) {
				
				List<IOTSampleData> samples = result.get(s.getDevice().getDeviceID());
				final IOTMonitorData data = new IOTMonitorData();
				for (IOTSampleData sample : samples) {
					if (sample.getType() == IOTSampleData.IOTSampleDataType.CO2) {
						data.setCo2(sample.getValue());
					} else if (sample.getType() == IOTSampleData.IOTSampleDataType.TEMPERATURE) {
						data.setTemperature(sample.getValue());
					} else if (sample.getType() == IOTSampleData.IOTSampleDataType.HUMIDITY) {
						data.setHumidity(sample.getValue());
					}
				}
				s.setMonitorData(data);
			}

			lvAdapter.setItems(sites);

			handler.post(new Runnable() {

				@Override
				public void run() {
					lvAdapter.notifyDataSetChanged();
				}

			});
		}

		@Override
		public void onRequestGetControl(RequestControl control) {
			this.control = control;
		}

		@Override
		public void onRequestStart() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRequestError(Exception e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRequestComplete() {
		}
	}

}
