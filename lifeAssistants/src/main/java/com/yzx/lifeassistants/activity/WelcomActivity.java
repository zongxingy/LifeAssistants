package com.yzx.lifeassistants.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.common.CommonConstant;
import com.yzx.lifeassistants.utils.AppUtils;
import com.yzx.lifeassistants.utils.SpUtils;

/**
 * @author: yzx
 * @date: 2015-9-11 上午11:02:35
 * @description: 欢迎页面
 */
public class WelcomActivity extends Activity {
	protected SpUtils sputil;// 获取用户昵称
	private RelativeLayout welcomeRL;//
	private TextView userTV;// 欢迎用户
	private TextView versionTV;// 版本

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		initView();
		initData();
		initAnimation();
	}

	/**
	 * @Description: 初始化控件
	 */
	private void initView() {
		welcomeRL = (RelativeLayout) findViewById(R.id.welcome_rl);
		versionTV = (TextView) findViewById(R.id.weclome_app_version);
		userTV = (TextView) findViewById(R.id.welcome_user);
	}

	/**
	 * @Description: 初始化数据
	 */
	private void initData() {
		if (null == sputil) {
			sputil = new SpUtils(this, CommonConstant.SP_FAIL_NAME);
		}
		String nick = sputil.getValue(CommonConstant.NICK_KEY, "");
		versionTV.setText("当前版本:" + AppUtils.getVersionName(this));
		if (null == nick || "".equals(nick)) {
			userTV.setText("欢迎进入" + getResources().getString(R.string.app_name));
		} else {
			userTV.setText(nick + ",欢迎回来");
		}

	}

	/**
	 * @Description: 渐变展示启动屏
	 */
	private void initAnimation() {
		AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);// 渐变过程
		animation.setDuration(3000);// 3秒
		welcomeRL.startAnimation(animation);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 渐变完进入登陆界面
				Intent intent = new Intent();
				intent.setClass(WelcomActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();

			}
		});

	}

}
