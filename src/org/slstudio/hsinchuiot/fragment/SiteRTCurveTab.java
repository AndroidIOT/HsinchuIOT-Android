package org.slstudio.hsinchuiot.fragment;

import org.slstudio.hsinchuiot.R;
import org.slstudio.hsinchuiot.js.ReportDataToJS;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class SiteRTCurveTab extends Fragment{
	private WebView chartWebView;
	
	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.site_tab_rtcurve, container, false);
		
		chartWebView = (WebView) view.findViewById(R.id.wv_rtcurve);
		chartWebView.getSettings().setJavaScriptEnabled(true);  
		chartWebView.getSettings().setUseWideViewPort(true);
		chartWebView.getSettings().setSupportZoom(false);
		// 设置是否可缩放
		chartWebView.getSettings().setBuiltInZoomControls(false);
		chartWebView.getSettings().setLoadWithOverviewMode(true);
		chartWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
		
		chartWebView.addJavascriptInterface(new ReportDataToJS(this.getActivity()), "JavaScriptInterface");
		chartWebView.requestFocus();
		chartWebView.loadUrl("file:///android_asset/chart.html");
		return view;
	}
}
