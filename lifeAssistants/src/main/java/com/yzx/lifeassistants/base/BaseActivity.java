package com.yzx.lifeassistants.base;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.umeng.scrshot.adapter.UMAppAdapter;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sensor.UMSensor.OnSensorListener;
import com.umeng.socialize.sensor.UMSensor.WhitchButton;
import com.umeng.socialize.sensor.controller.UMShakeService;
import com.umeng.socialize.sensor.controller.impl.UMShakeServiceFactory;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.common.CommonConstant;
import com.yzx.lifeassistants.utils.ToastUtils;

/**
 * @author: yzx
 * @date: 2015-9-11 上午10:58:30
 * @Description:
 */
@SuppressLint({ "InlinedApi", "InflateParams" })
public class BaseActivity extends SlidingFragmentActivity {
	private UMSocialService mShareController;// 分享控制器
	private UMShakeService mShakeController;// 摇一摇分享器
	private OnSensorListener mSensorListener;// 传感器监听器

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityCollector.addActivity(this);
		// 设置侧滑菜单布局
		setBehindContentView(R.layout.fragment_leftmenu);
		// 设置侧滑菜单不出现
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		// 沉浸状态栏
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			SystemBarTintManager tintManager = new SystemBarTintManager(this);
			tintManager.setStatusBarTintEnabled(true);// 设置可沉浸
			// 设置一个颜色给系统栏
			tintManager.setStatusBarTintResource(R.color.actionbar_color);
		}
		// 初始化ShareSDK
		initShareSDK();
		// 初始化友盟分享
		initUmShare();
		// 初始化友盟摇一摇分享
		initUmShake();
		// 初始化QQ信息
		initQQ();
		// 初始化微信信息
		initWeiXin();
	}

	/**
	 * 
	 * @Description: 初始化ShareSDK
	 */
	private void initShareSDK() {
		// 初始化ShareSdk
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		// 显示编辑页面
		oks.setSilent(false);
	}

	/**
	 * 
	 * @Description: 初始化友盟分享
	 */
	private void initUmShare() {
		mShareController = UMServiceFactory
				.getUMSocialService("com.umeng.share");
		// 移除不需要的分享平台
		mShareController.getConfig().removePlatform(SHARE_MEDIA.RENREN,
				SHARE_MEDIA.DOUBAN);
		// 设置分享列表的平台排列顺序
		mShareController.getConfig().setPlatformOrder(SHARE_MEDIA.QQ,
				SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
				SHARE_MEDIA.SINA, SHARE_MEDIA.QZONE, SHARE_MEDIA.TENCENT,
				SHARE_MEDIA.SMS, SHARE_MEDIA.EMAIL);
	}

	/**
	 * 
	 * @Description: 初始化友盟摇一摇分享
	 */
	private void initUmShake() {
		mShakeController = UMShakeServiceFactory
				.getShakeService("com.umeng.share");
		// 关闭Toast提示
		// mShakeController.getSocialController().getConfig().closeToast();
		mSensorListener = new OnSensorListener() {

			@Override
			public void onStart() {

			}

			@Override
			public void onComplete(SHARE_MEDIA platform, int code,
					SocializeEntity entity) {
				switch (code) {
				case 200: {// 分享成功
					ToastUtils.showToast("分享成功");
					break;
				}
				case -101: {// 未授权
					ToastUtils.showToast("没有授权");
					break;
				}
				case 40000: {// 取消分享
					break;
				}
				case 40002: {// 不支持纯图片分享
					ToastUtils.showToast("该平台暂不支持纯图片分享,建议使用图文分享");
					break;
				}
				case -102: {// 未知错误
					ToastUtils.showToast("网络连接出错,请确认网络连接后再重试");
					break;
				}
				default:
					ToastUtils.showToast("分享失败 " + code);
					break;
				}
			}

			@Override
			public void onButtonClick(WhitchButton button) {

			}

			@Override
			public void onActionComplete(SensorEvent event) {

			}
		};
	}

	/**
	 * 
	 * @Description: 初始化QQ
	 */
	private void initQQ() {
		// 添加发送QQ好友
		UMQQSsoHandler qqHandler = new UMQQSsoHandler(this,
				CommonConstant.QQ_APP_ID, CommonConstant.QQ_APP_SECRET);
		qqHandler.setTitle(CommonConstant.SHARE_TITLE);
		qqHandler.setTargetUrl(CommonConstant.SHARE_TARGET_URL);
		qqHandler.addToSocialSDK();
		// 添加发送QQ空间
		QZoneSsoHandler qZoneHandler = new QZoneSsoHandler(this,
				CommonConstant.QQ_APP_ID, CommonConstant.QQ_APP_SECRET);
		qZoneHandler.setTargetUrl(CommonConstant.SHARE_TARGET_URL);
		qZoneHandler.addToSocialSDK();
	}

	/**
	 * 
	 * @Description: 初始化微信信息
	 */
	private void initWeiXin() {
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(this,
				CommonConstant.WEIXIN_APP_ID, CommonConstant.WEIXIN_APP_SECRET);
		wxHandler.setTitle(CommonConstant.SHARE_TITLE);
		wxHandler.setTargetUrl(CommonConstant.SHARE_TARGET_URL);
		wxHandler.addToSocialSDK();
		// 添加微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(this,
				CommonConstant.WEIXIN_APP_ID, CommonConstant.WEIXIN_APP_SECRET);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.setTitle(CommonConstant.SHARE_TITLE);
		wxCircleHandler.setTargetUrl(CommonConstant.SHARE_TARGET_URL);
		wxCircleHandler.addToSocialSDK();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		/** 使用SSO授权必须添加如下代码 */
		UMSsoHandler ssoHandler = mShareController.getConfig().getSsoHandler(
				requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 
	 * @Description: 返回友盟分享的控制器
	 */
	public UMSocialService getmShareController() {
		return mShareController;
	}

	/**
	 * 
	 * @Description: 返回友盟摇一摇分享器
	 */
	public UMShakeService getmShakeController() {
		return mShakeController;
	}

	/**
	 * @Description: 在onResume中注册摇一摇截图分享
	 */
	@Override
	protected void onResume() {
		UMAppAdapter appAdapter = new UMAppAdapter(BaseActivity.this);
		// 配置摇一摇截屏分享时用户可选的平台，最多支持五个平台
		List<SHARE_MEDIA> platforms = new ArrayList<SHARE_MEDIA>();
		platforms.add(SHARE_MEDIA.QQ);
		platforms.add(SHARE_MEDIA.QZONE);
		platforms.add(SHARE_MEDIA.WEIXIN);
		platforms.add(SHARE_MEDIA.WEIXIN_CIRCLE);
		// 设置异步截屏
		mShakeController.setAsyncTakeScrShot(true);
		// 设置摇一摇分享的文字内容
		mShakeController.setShareContent(CommonConstant.SHAKE_SHARE_CONTENT);
		// 设置分享内容类型, PLATFORM_SCRSHOT代表使用摇一摇的截图，而文字内容为开发者预设的平台独立的内容
		// 例如WeiXinShareContent, SinaShareContent等. 一般情况可不设置.
		// mShakeController.setShakeMsgType(ShakeMsgType.PLATFORM_SCRSHOT);
		// 注册摇一摇截屏分享功能,mSensorListener在2.1.2中定义
		mShakeController.registerShakeListender(BaseActivity.this, appAdapter,
				platforms, mSensorListener);
		super.onResume();
	}

	/**
	 * @Description: 应用退出时，请在onPause方法中手动注销摇一摇功能
	 */
	@Override
	protected void onPause() {
		mShakeController.unregisterShakeListener(BaseActivity.this);
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		ActivityCollector.removeActivity(this);
		super.onDestroy();
	}
}
