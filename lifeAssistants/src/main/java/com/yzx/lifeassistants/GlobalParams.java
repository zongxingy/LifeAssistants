package com.yzx.lifeassistants;

import android.app.Activity;
import cn.bmob.v3.BmobUser;

import com.yzx.lifeassistants.bean.User;

/**
 * @Description: 全局
 * @author: yzx
 * @time: 2015-9-16 下午8:45:15
 */
public class GlobalParams {
	public static User userInfo;
	public static Activity context;

	public void init(Activity activity) {
		if (userInfo != null) {
			return;
		}
		userInfo = BmobUser.getCurrentUser(activity, User.class);
		context = activity;
	}
}
