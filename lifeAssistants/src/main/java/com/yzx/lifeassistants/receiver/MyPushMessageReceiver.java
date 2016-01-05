package com.yzx.lifeassistants.receiver;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import cn.bmob.push.PushConstants;

import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.common.CommonConstant;
import com.yzx.lifeassistants.utils.LogcatUtils;

/**
 * @Description: 接受推送消息
 * @author: yzx
 * @time: 2016-1-4 上午10:54:29
 */
public class MyPushMessageReceiver extends BroadcastReceiver {

	private String downloadUrl;// 最新版下载地址

	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
			String content = intent
					.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING);
			LogcatUtils.d("bmob", "客户端收到推送内容：" + content);
			if (content.contains("alert")) {
				try {
					JSONObject object = new JSONObject(content);
					downloadUrl = object.getString("alert");
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			// 消息通知栏
			// 定义NotificationManager
			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager mNotificationManager = (NotificationManager) context
					.getSystemService(ns);

			// 定义通知栏展现的内容信息
			int icon = R.drawable.ic_launcher;// 通知图标
			CharSequence tickerText = "找我App更新啦~";// 状态栏(Status Bar)显示的通知文本提示
			long when = System.currentTimeMillis();// 通知产生的时间，会在通知信息里显示
			Notification notification = new Notification(icon, tickerText, when);
			notification.flags = Notification.FLAG_AUTO_CANCEL;// 实现点击后消失
			long[] vibrate = { 0, 100, 200, 300 };
			notification.vibrate = vibrate;// 手机振动

			// 定义下拉通知栏时要展现的内容信息
			CharSequence contentTitle = "找我";
			CharSequence contentText = "赶紧点我来更新吧~";
			if (null == downloadUrl || "".equals(downloadUrl)) {
				downloadUrl = CommonConstant.DOWNLOAD_URL;
			}
			Intent notificationIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(downloadUrl));
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
					notificationIntent, 0);
			notification.setLatestEventInfo(context, contentTitle, contentText,
					contentIntent);
			// 用mNotificationManager的notify方法通知用户生成标题栏消息通知
			mNotificationManager.notify(1, notification);
		}
	}
}
