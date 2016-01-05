package com.yzx.lifeassistants.base;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: yzx
 * @date: 2015-9-11 上午10:53:55
 * @Description: 活动管理器
 */
public class ActivityCollector {
	private static List<BaseActivity> activities = new ArrayList<BaseActivity>();

	public static void addActivity(BaseActivity activity) {
		activities.add(activity);
	}

	public static void removeActivity(BaseActivity activity) {
		activities.remove(activity);
	}

	public static void finishAll() {
		for (BaseActivity activity : activities) {
			activity.finish();
		}
	}
}
