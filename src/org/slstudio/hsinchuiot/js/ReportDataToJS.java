package org.slstudio.hsinchuiot.js;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;

public class ReportDataToJS {
	private Context mContext;

	public ReportDataToJS(Context context) {
		this.mContext = context;
	}

	public String jsontohtml() {
		JSONObject map;
		JSONArray array = new JSONArray();
		JSONArray data = new JSONArray();
		for(int i=0;i<25; i++){
			data.put(60);
		}
		try {
			map = new JSONObject();
			map.put("name", "CO2");
			map.put("value",
					data);
			map.put("color", "#ec4646");
			map.put("line_width", 2);
			array.put(map);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return array.toString();
	}

	public void showToast(String toast) {
		Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
	}
}
