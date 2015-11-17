package org.slstudio.hsinchuiot.service.push;

import java.util.List;

import org.slstudio.hsinchuiot.AppConfig;
import org.slstudio.hsinchuiot.Constants;
import org.slstudio.hsinchuiot.util.IOTLog;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ServerPushMessageReceiver extends FrontiaPushMessageReceiver {
	@Override
	public void onBind(Context context, int errorCode, String appid,
			String userId, String channelId, String requestId) {
		String responseString = "onBind errorCode=" + errorCode + " appid="
				+ appid + " userId=" + userId + " channelId=" + channelId
				+ " requestId=" + requestId;
		IOTLog.i(TAG, responseString);
		if (errorCode == 0) {
			ServerPushHelper.setBind(context, true, userId, channelId);
		}
	}

	/**
	 * 接收透传消息的函数。
	 * 
	 * @param context
	 *            上下文
	 * @param message
	 *            推送的消息
	 * @param customContentString
	 *            自定义内容,为空或者json字符串
	 */
	@Override
	public void onMessage(Context context, String message,
			String customContentString) {
		String messageString = "透传消息 message=\"" + message
				+ "\" customContentString=" + customContentString;
		IOTLog.i(TAG, messageString);

		// 自定义内容获取方式，mykey和myvalue对应透传消息推送时自定义内容中设置的键和值
		if (!TextUtils.isEmpty(customContentString)) {
			sendAction(context, customContentString);
		} else {
			sendAction(context, message);
		}

	}

	/**
	 * 接收通知点击的函数。注：推送通知被用户点击前，应用无法通过接口获取通知的内容。
	 * 
	 * @param context
	 *            上下文
	 * @param title
	 *            推送的通知的标题
	 * @param description
	 *            推送的通知的描述
	 * @param customContentString
	 *            自定义内容，为空或者json字符串
	 */
	@Override
	public void onNotificationClicked(Context context, String title,
			String description, String customContentString) {
		String notifyString = "通知点击 title=\"" + title + "\" description=\""
				+ description + "\" customContent=" + customContentString;
		IOTLog.d(TAG, notifyString);

		if (!TextUtils.isEmpty(customContentString)) {
			sendAction(context, customContentString);
		}

	}

	/**
	 * setTags() 的回调函数。
	 * 
	 * @param context
	 *            上下文
	 * @param errorCode
	 *            错误码。0表示某些tag已经设置成功；非0表示所有tag的设置均失败。
	 * @param successTags
	 *            设置成功的tag
	 * @param failTags
	 *            设置失败的tag
	 * @param requestId
	 *            分配给对云推送的请求的id
	 */
	@Override
	public void onSetTags(Context context, int errorCode,
			List<String> sucessTags, List<String> failTags, String requestId) {
		String responseString = "onSetTags errorCode=" + errorCode
				+ " sucessTags=" + sucessTags + " failTags=" + failTags
				+ " requestId=" + requestId;
		IOTLog.d(TAG, responseString);

	}

	/**
	 * delTags() 的回调函数。
	 * 
	 * @param context
	 *            上下文
	 * @param errorCode
	 *            错误码。0表示某些tag已经删除成功；非0表示所有tag均删除失败。
	 * @param successTags
	 *            成功删除的tag
	 * @param failTags
	 *            删除失败的tag
	 * @param requestId
	 *            分配给对云推送的请求的id
	 */
	@Override
	public void onDelTags(Context context, int errorCode,
			List<String> sucessTags, List<String> failTags, String requestId) {
		String responseString = "onDelTags errorCode=" + errorCode
				+ " sucessTags=" + sucessTags + " failTags=" + failTags
				+ " requestId=" + requestId;
		IOTLog.d(TAG, responseString);

	}

	/**
	 * listTags() 的回调函数。
	 * 
	 * @param context
	 *            上下文
	 * @param errorCode
	 *            错误码。0表示列举tag成功；非0表示失败。
	 * @param tags
	 *            当前应用设置的所有tag。
	 * @param requestId
	 *            分配给对云推送的请求的id
	 */
	@Override
	public void onListTags(Context context, int errorCode, List<String> tags,
			String requestId) {
		String responseString = "onListTags errorCode=" + errorCode + " tags="
				+ tags;
		IOTLog.i(TAG, responseString);

	}

	/**
	 * PushManager.stopWork() 的回调函数。
	 * 
	 * @param context
	 *            上下文
	 * @param errorCode
	 *            错误码。0表示从云推送解绑定成功；非0表示失败。
	 * @param requestId
	 *            分配给对云推送的请求的id
	 */
	@Override
	public void onUnbind(Context context, int errorCode, String requestId) {
		String responseString = "onUnbind errorCode=" + errorCode
				+ " requestId = " + requestId;
		IOTLog.d(TAG, responseString);

		// 解绑定成功，设置未绑定flag，
		if (errorCode == 0) {
			ServerPushHelper.setBind(context, false, null, null);
		}

	}

	private void sendAction(Context context, String content) {
		String uid = context.getSharedPreferences(AppConfig.PREF_NAME,
				Context.MODE_PRIVATE).getString(
				Constants.PreferenceKey.SERVER_PUSH_BIND_USRID, "");

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			Message node = objectMapper.readValue(content, Message.class);
			if (!uid.equals(node.getUserId())) {
				return;
			}
			String type = node.getType();
			Intent intent = new Intent();
			Intent pushIntent = new Intent();
			intent.putExtra(Constants.BundleKey.PUSH_MESSAGE, node);
			pushIntent.putExtra(Constants.BundleKey.PUSH_MESSAGE, node);

			context.sendBroadcast(pushIntent);

		} catch (Exception e) {
			e.printStackTrace();
			IOTLog.e(getClass().getName(), e.getMessage());
		}

	}

}
