package org.slstudio.hsinchuiot;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

public class ActivityManager {
	private static List<Activity> runningActivities = new ArrayList<Activity>();
	
	public static void addActivities(Activity activity){
		runningActivities.add(activity);
	}
	public static void removeActivities(Activity activity){
		runningActivities.remove(activity);
	}
	
	public static void clearActivities(){
		runningActivities.clear();
	}
	
	public static void finishAll(){
		for(Activity activity: runningActivities){
			activity.finish();
		}
		runningActivities.clear();
	}
	
}
