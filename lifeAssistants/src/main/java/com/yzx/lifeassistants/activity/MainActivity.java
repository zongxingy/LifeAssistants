package com.yzx.lifeassistants.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.base.BaseActivity;
import com.yzx.lifeassistants.fragment.LeftMenuFragment;
import com.yzx.lifeassistants.fragment.SettingFragment;

/**
 * @author: yzx
 * @date: 2015-9-11 上午9:52:25
 * @Description: 主页面
 */
public class MainActivity extends BaseActivity {

	private Fragment mContent;// 内容
	public static SlidingMenu sm;// 侧滑菜单
	public static FragmentManager fm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
		initView();
		initSlidingMenu(savedInstanceState);
	}

	/**
	 * @Description: 初始化进入界面
	 */
	private void init() {
		fm = getSupportFragmentManager();
		mContent = new SettingFragment();// 默认进入的是个人设置界面
		initFragment(mContent);

	}

	/**
	 * @Description: 初始化控件
	 */
	private void initView() {

	}

	/**
	 * @Description: 初始化侧边栏
	 * @param: savedInstanceState
	 */
	private void initSlidingMenu(Bundle savedInstanceState) {
		// 如果保存的状态不为空则得到之前保存的Fragment，否则实例化MyFragment
		if (savedInstanceState != null) {
			mContent = fm.getFragment(savedInstanceState, "mContent");
		}
		// 设置左侧滑动菜单
		setBehindContentView(R.layout.menu_frame_left);
		fm.beginTransaction().replace(R.id.menu_frame, new LeftMenuFragment())
				.commit();
		// 实例化滑动菜单对象
		sm = getSlidingMenu();
		// 设置可以左右滑动的菜单
		sm.setMode(SlidingMenu.LEFT);
		// 设置滑动阴影的宽度
		sm.setShadowWidthRes(R.dimen.shadow_width);
		// 设置滑动菜单阴影的图像资源
		sm.setShadowDrawable(null);
		// 设置滑动菜单视图的宽度
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		// 设置渐入渐出效果的值
		sm.setFadeDegree(0.35f);
		// 设置触摸屏幕的模式,这里设置为全屏
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		// 设置下方视图的在滚动时的缩放比例
		sm.setBehindScrollScale(0.0f);

	}

	/**
	 * @Description: 初始化Fragment
	 * @param: fragment
	 */
	private void initFragment(Fragment fragment) {
		changeFragment(fragment, true);

	}

	/**
	 * @Description: 切换Fragment
	 * @param: fragment
	 * @param: boolean
	 */
	private void changeFragment(Fragment fragment, boolean init) {
		FragmentTransaction ft = fm.beginTransaction().setCustomAnimations(
				R.anim.umeng_fb_slide_in_from_right,
				R.anim.umeng_fb_slide_out_from_left,
				R.anim.umeng_fb_slide_in_from_right,
				R.anim.umeng_fb_slide_out_from_left);

		ft.replace(R.id.content_frame, fragment);
		if (!init) {
			ft.addToBackStack(null);
		}
		ft.commitAllowingStateLoss();
	}

	/**
	 * @Description: 切换Fragment
	 * @param: fragment
	 * @param: title
	 */
	public void switchContent(Fragment fragment, String title) {
		mContent = fragment;
		changeFragment(fragment, true);
		getSlidingMenu().showContent();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		fm.putFragment(outState, "mContent", mContent);
	}

	/**
	 * 
	 * @Description: 侧滑菜单
	 */
	public void topToggleClick(View view) {
		switch (view.getId()) {
		case R.id.top_menu_btn:// 侧滑菜单按钮
			toggle();
			break;

		default:
			break;
		}
	}

	/**
	 * @Description: 重写系统的返回键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			toggle();
			if (sm.isMenuShowing()) {
				new waitThread().start();
			} else {

			}
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1234:
				toggle();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	class waitThread extends Thread {

		@Override
		public void run() {
			try {
				Thread.sleep(100);
				Message msg = new Message();
				msg.what = 1234;
				handler.sendMessage(msg);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			super.run();
		}

	}

}
