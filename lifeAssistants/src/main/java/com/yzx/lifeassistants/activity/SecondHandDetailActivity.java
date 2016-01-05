package com.yzx.lifeassistants.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.v3.datatype.BmobFile;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.romainpiel.titanic.library.Titanic;
import com.romainpiel.titanic.library.TitanicTextView;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.UMImage;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.adapter.CustomPagerAdapter;
import com.yzx.lifeassistants.adapter.CyclePagerAdapter;
import com.yzx.lifeassistants.adapter.MaterialSimpleListAdapter;
import com.yzx.lifeassistants.base.BaseActivity;
import com.yzx.lifeassistants.bean.SecondHandGoods;
import com.yzx.lifeassistants.bean.ShareListItem;
import com.yzx.lifeassistants.common.CommonConstant;
import com.yzx.lifeassistants.fragment.SecondHandContactInfoFragment;
import com.yzx.lifeassistants.fragment.SecondHandItemInfoFragment;
import com.yzx.lifeassistants.utils.AppUtils;
import com.yzx.lifeassistants.utils.FileUtils;
import com.yzx.lifeassistants.utils.LogcatUtils;
import com.yzx.lifeassistants.utils.ScreenUtils;
import com.yzx.lifeassistants.utils.ToastUtils;
import com.yzx.lifeassistants.utils.TypefacesUtils;
import com.yzx.lifeassistants.view.widget.CycleViewPager;

/**
 * @Description: 二手物品详情界面
 * @author: yzx
 * @time: 2015-11-13 上午11:05:22
 */
public class SecondHandDetailActivity extends BaseActivity implements
		OnClickListener, OnPageChangeListener {
	private ImageButton topBackBtn;// 顶部返回按钮
	private TextView topTitleTV;// 顶部标题
	private ImageButton topShareBtn;// 顶部分享按钮
	private RelativeLayout picRL;// 图片布局
	private CycleViewPager picPager;// 轮播图片
	private LinearLayout dotLL;// 图片下方的小点
	private TitanicTextView noPivTV;// 暂无图片
	private TextView titleTV;// 标题
	private RadioButton itemRB;// 物品信息按钮
	private RadioButton contactRB;// 联系信息按钮
	private ViewPager infoPager;// 信息

	private SecondHandGoods secondHandInfo;// 闲置物品
	private List<String> picUrlList;// 图片URL列表
	private CyclePagerAdapter picAdapter;// 图片适配器
	private CustomPagerAdapter infoAdapter;// 信息适配器
	public static final int TAB_ITEM_INFO = 0;// 物品信息
	public static final int TAB_CONTACT_INFO = 1;// 联系信息

	// //////////QQ,QZone使用友盟分享,其余使用ShareSDK分享//////////////
	private ShareParams shareParams;// 分享内容
	private PlatformActionListener listener;// 分享回调监听
	private Bitmap bitmap;// 分享图片
	private String shareImagePath;// 分享图片路径
	private UMSocialService mShareController;// 友盟分享控制器
	private SnsPostListener snsPostListener;// 友盟分享回调监听

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second_hand_detail);
		init();
		initView();
		setListener();
		initCycleImage();
		initData();
	}

	/**
	 * 
	 * @Description: 初始化上个界面传来的值
	 */
	private void init() {
		Intent intent = getIntent();
		if (null != intent) {
			secondHandInfo = (SecondHandGoods) intent
					.getSerializableExtra(CommonConstant.TO_SECOND_HAND_DETAIL_ACTIVITY_KEY);
		}
	}

	/**
	 * 
	 * @Description: 初始化控件
	 */
	private void initView() {
		topBackBtn = (ImageButton) findViewById(R.id.top_back_btn);
		topTitleTV = (TextView) findViewById(R.id.top_title_tv);
		topShareBtn = (ImageButton) findViewById(R.id.top_share_btn);
		picRL = (RelativeLayout) findViewById(R.id.second_hand_detail_pic_rl);
		picPager = (CycleViewPager) findViewById(R.id.second_hand_detail_pic_viewpager);
		dotLL = (LinearLayout) findViewById(R.id.second_hand_detail_pic_dot_ll);
		noPivTV = (TitanicTextView) findViewById(R.id.second_hand_detail_no_pic_tv);
		titleTV = (TextView) findViewById(R.id.second_hand_detail_info_title_tv);
		itemRB = (RadioButton) findViewById(R.id.second_hand_detail_info_item_rb);
		contactRB = (RadioButton) findViewById(R.id.second_hand_detail_info_contact_rb);
		infoPager = (ViewPager) findViewById(R.id.second_hand_detail_info_viewpager);

	}

	/**
	 * 
	 * @Description: 设置监听
	 */
	@SuppressWarnings("deprecation")
	private void setListener() {
		topBackBtn.setOnClickListener(this);
		topShareBtn.setOnClickListener(this);
		itemRB.setOnClickListener(this);
		contactRB.setOnClickListener(this);
		infoPager.setOnPageChangeListener(this);
	}

	/**
	 * 
	 * @Description: 初始化图片轮播
	 */
	private void initCycleImage() {
		picUrlList = new ArrayList<String>();
		List<BmobFile> picFileList = secondHandInfo.getPicFileList();
		if (null == picFileList || 1 > picFileList.size()) {
			picPager.setVisibility(View.GONE);
			dotLL.setVisibility(View.GONE);
			// 设置图片布局背景
			picRL.setBackgroundResource(R.color.blue_btn_bg_color);
			// 设置字体
			noPivTV.setTypeface(TypefacesUtils.get(this, "Satisfy-Regular.ttf"));
			Titanic titanic = new Titanic();
			titanic.start(noPivTV);
			noPivTV.setVisibility(View.VISIBLE);
		} else {
			noPivTV.setVisibility(View.GONE);
			picPager.setVisibility(View.VISIBLE);
			dotLL.setVisibility(View.VISIBLE);
			for (BmobFile picFile : picFileList) {
				picUrlList.add(picFile.getFileUrl(this));
			}
			picPager.setDotViewList(picUrlList.size(), R.drawable.icon_dot_on,
					R.drawable.icon_dot_off, dotLL);
			picAdapter = new CyclePagerAdapter(this, picUrlList);
			picPager.setAdapter(picAdapter);

		}
	}

	/**
	 * 
	 * @Description: 初始化数据
	 */
	private void initData() {
		topTitleTV.setText("闲置详情");
		topShareBtn.setVisibility(View.VISIBLE);
		titleTV.setText(secondHandInfo.getTitle());
		FragmentManager fManager = getSupportFragmentManager();
		Bundle bundle = new Bundle();
		bundle.putSerializable(
				CommonConstant.FROM_SECOND_HAND_DETAIL_ACTIVITY_KEY,
				secondHandInfo);
		List<Fragment> fragments = new ArrayList<Fragment>();
		SecondHandItemInfoFragment itemInfoFragment = new SecondHandItemInfoFragment();
		// Activity传值到Fragment通过fragment.setArguments(bundle);
		itemInfoFragment.setArguments(bundle);
		fragments.add(itemInfoFragment);
		SecondHandContactInfoFragment contactInfoFragment = new SecondHandContactInfoFragment();
		contactInfoFragment.setArguments(bundle);
		fragments.add(contactInfoFragment);
		infoAdapter = new CustomPagerAdapter(fManager, fragments);
		infoPager.setAdapter(infoAdapter);
		infoPager.setOffscreenPageLimit(2);
		infoPager.setCurrentItem(TAB_ITEM_INFO);

		// //////////////////////ShareSDK分享配置////////////////////////
		shareParams = new ShareParams();
		listener = new PlatformActionListener() {

			@Override
			public void onError(Platform arg0, int code, Throwable throwable) {
				LogcatUtils.e("分享失败： " + code + " " + throwable.getMessage());
				ToastUtils.showToast("分享失败，请重试~ ");
			}

			@Override
			public void onComplete(Platform platform, int code,
					HashMap<String, Object> map) {
				ToastUtils.showToast("分享到" + platform.getName() + "成功~");
			}

			@Override
			public void onCancel(Platform arg0, int code) {
				ToastUtils.showToast("您已取消分享~");
			}
		};
		// //////////////////////友盟分享配置////////////////////////
		mShareController = UMServiceFactory
				.getUMSocialService("com.umeng.share");
		// 关闭自身的Toast
		mShareController.getConfig().closeToast();
		snsPostListener = new SnsPostListener() {

			@Override
			public void onStart() {
				ToastUtils.showToast("分享中···");
			}

			@Override
			public void onComplete(SHARE_MEDIA media, int code,
					SocializeEntity entity) {
				switch (code) {
				case 200: {
					ToastUtils.showToast("分享到" + media.name() + "成功~");
					break;
				}
				case 40000: {
					ToastUtils.showToast("您已取消分享~");
					break;
				}
				default: {
					LogcatUtils.e("分享失败： " + code);
					ToastUtils.showToast("分享失败 ，请重试~");
					break;
				}
				}
			}
		};

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	/**
	 * @Description: 滑动监听
	 */
	@Override
	public void onPageSelected(int position) {
		switch (position) {
		case TAB_ITEM_INFO: {// 物品信息
			itemRB.setChecked(true);
			break;
		}
		case TAB_CONTACT_INFO: {// 联系信息
			contactRB.setChecked(true);
			break;
		}
		default:
			break;
		}
	}

	/**
	 * 
	 * @Description: 按键监听
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_back_btn: {// 返回
			finish();
			break;
		}
		case R.id.top_share_btn: {// 分享
			showShareDialog();
			break;
		}
		case R.id.second_hand_detail_info_item_rb: {// 物品信息
			infoPager.setCurrentItem(TAB_ITEM_INFO);
			break;
		}
		case R.id.second_hand_detail_info_contact_rb: {// 联系信息
			infoPager.setCurrentItem(TAB_CONTACT_INFO);
			break;
		}
		default:
			break;
		}
	}

	// /////////////////////////分享////////////////////////////////
	/**
	 * 
	 * @Description: 弹出推荐分享框
	 */
	@SuppressLint("NewApi")
	private void showShareDialog() {
		// 获取图片
		bitmap = ScreenUtils.snapShotWithoutStatusBar(this);
		if (null != bitmap) {
			shareImagePath = FileUtils.getImagePath();
			if (null != shareImagePath) {
				FileUtils.saveBitmap(bitmap, shareImagePath);
				if (FileUtils.isFilePathExist(shareImagePath)) {
					shareParams.setImagePath(shareImagePath);
				}
			}

		}
		AlertDialog.Builder builder = new Builder(this, R.style.ShareDialog);
		builder.setTitle("分享到");
		final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(
				this);
		String[] array = getResources().getStringArray(
				R.array.share_dialog_text);
		adapter.add(new ShareListItem.Builder(this).content(array[0])
				.icon(R.drawable.ssdk_oks_skyblue_logo_qq_checked).build());
		adapter.add(new ShareListItem.Builder(this).content(array[1])
				.icon(R.drawable.ssdk_oks_skyblue_logo_qzone_checked).build());
		adapter.add(new ShareListItem.Builder(this).content(array[2])
				.icon(R.drawable.ssdk_oks_skyblue_logo_sinaweibo_checked)
				.build());
		adapter.add(new ShareListItem.Builder(this).content(array[3])
				.icon(R.drawable.ssdk_oks_skyblue_logo_wechat_checked).build());
		adapter.add(new ShareListItem.Builder(this).content(array[4])
				.icon(R.drawable.ssdk_oks_skyblue_logo_wechatfavorite_checked)
				.build());
		adapter.add(new ShareListItem.Builder(this).content(array[5])
				.icon(R.drawable.ssdk_oks_skyblue_logo_wechatmoments_checked)
				.build());
		adapter.add(new ShareListItem.Builder(this).content(array[6])
				.icon(R.drawable.ic_share_more).build());

		builder.setAdapter(adapter, new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0: {// 分享到QQ
					shareToQQ();
					break;
				}
				case 1: {// 分享到QQ空间
					shareToQzone();
					break;
				}
				case 2: {// 分享到新浪微博
					shareToSinaWeibo();
					break;
				}
				case 3: {// 分享到微信好友
					ToastUtils.showToast("分享中···");
					shareToWeChatSession();
					break;
				}
				case 4: {// 分享到微信收藏
					ToastUtils.showToast("分享中···");
					shareToWeChatFavorite();
					break;
				}
				case 5: {// 分享到微信朋友圈
					ToastUtils.showToast("分享中···");
					shareToWeChatTimeline();
					break;
				}
				case 6: {// 更多
					share("", null);
					break;
				}
				default:
					break;
				}
			}

		});
		AlertDialog dialog = builder.create();
		// dialog.show()需要在dialog.getWindow()前
		dialog.show();
		Window window = dialog.getWindow();
		window.setGravity(Gravity.BOTTOM);
		WindowManager.LayoutParams lp = window.getAttributes();
		Display display = getWindowManager().getDefaultDisplay();
		Point out = new Point();
		display.getSize(out);
		lp.width = out.x;
		window.setAttributes(lp);
		final View decorView = window.getDecorView();
		decorView.setBackgroundColor(getResources().getColor(
				R.color.window_background));
		dialog.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {
				// 按下去效果
				Animator animator = ObjectAnimator.ofFloat(decorView,
						"translationY", decorView.getMeasuredHeight() / 1.5F, 0);
				animator.setDuration(200);
				animator.start();
			}
		});

	}

	/**
	 * 
	 * @Description: 分享到QQ好友
	 */
	private void shareToQQ() {
		// ShareSDK图文分享 纯图片分享无法回调
		// shareParams.setText(CommonConstant.SHARE_CONTENT);
		// shareParams.setTitle(CommonConstant.SHARE_TITLE);
		// shareParams.setTitleUrl(CommonConstant.SHARE_TARGET_URL);
		// Platform qqPlatform = ShareSDK.getPlatform(QQ.NAME);
		// qqPlatform.setPlatformActionListener(listener);
		// qqPlatform.share(shareParams);
		// 使用友盟分享 QQ图文分享
		if (AppUtils.isInstallApplication("com.tencent.mobileqq")) {
			mShareController.setShareContent(null);
			if (null != bitmap) {
				mShareController.setShareImage(new UMImage(this, bitmap));
			}
			mShareController.postShare(this, SHARE_MEDIA.QQ, snsPostListener);
		} else {
			ToastUtils.showToast("请先安装最新版QQ~");
		}
	}

	/**
	 * 
	 * @Description: 分享到空间
	 */
	private void shareToQzone() {
		// 使用ShareSDK分享 图片需要先上传服务器速度慢 空间不支持纯图片
		// shareParams.setTitle(CommonConstant.SHARE_TITLE);
		// shareParams.setTitleUrl(CommonConstant.SHARE_TARGET_URL);
		// shareParams.setText(CommonConstant.SHARE_CONTENT);
		// shareParams.setSite("Lifeassistants");
		// shareParams.setSiteUrl(CommonConstant.SHARE_TARGET_URL);
		// Platform qZonePlatform = ShareSDK.getPlatform(QZone.NAME);
		// // 设置false表示使用SSO授权方式
		// qZonePlatform.SSOSetting(false);
		// qZonePlatform.setPlatformActionListener(listener);
		// qZonePlatform.share(shareParams);
		// 使用友盟分享 QQ空间图文分享
		mShareController.setShareContent(CommonConstant.SHARE_CONTENT);
		if (null != bitmap) {
			mShareController.setShareImage(new UMImage(this, bitmap));
		}
		mShareController.postShare(this, SHARE_MEDIA.QZONE, snsPostListener);
	}

	/**
	 * 
	 * @Description: 分享到新浪微博
	 */
	private void shareToSinaWeibo() {
		// 新浪微博图文分享
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("Id", "1");// 自定义字段，整型，用于您项目中对此平台的识别符
		hashMap.put("SortId", "1");// "此平台在您分享列表中的位置，整型，数值越大越靠后"
		hashMap.put("AppKey", CommonConstant.SINA_APP_ID);// 填写您在新浪微博上注册到的AppKey
		hashMap.put("AppSecret", CommonConstant.SINA_APP_SECRET);// 填写您在新浪微博上注册到的AppSeret
		hashMap.put("RedirectUrl", CommonConstant.SINA_REDIRECT_URL);// 填写您在新浪微博上注册的RedirectUrl
		if (AppUtils.isInstallApplication("com.sina.weibo")) {
			hashMap.put("ShareByAppClient", "true");// 使用客户端进行分享
		} else {
			hashMap.put("ShareByAppClient", "false");// 不使用客户端进行分享
		}
		hashMap.put("Enable", "true");// 布尔值，标记此平台是否有效
		ShareSDK.setPlatformDevInfo(SinaWeibo.NAME, hashMap);
		if (AppUtils.isInstallApplication("com.sina.weibo")) {
			// 调用新浪微博客户端授权分享
			shareParams.setText(CommonConstant.SHARE_CONTENT);
			shareParams.setUrl(CommonConstant.SHARE_TARGET_URL);
			shareParams.setTitle(CommonConstant.SHARE_TITLE);
			shareParams.setTitleUrl(CommonConstant.SHARE_TARGET_URL);
			shareParams.setShareType(Platform.SHARE_WEBPAGE);
			Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
			// 设置false表示使用SSO授权方式
			weibo.SSOSetting(false);
			// 设置分享事件回调
			weibo.setPlatformActionListener(listener);
			weibo.share(shareParams);
		} else {
			// 调用网页授权分享
			OnekeyShare oks = new OnekeyShare();
			oks.setPlatform(SinaWeibo.NAME);
			// 显示编辑页面
			oks.setSilent(false);
			// 显示为Dialog
			oks.setDialogMode();
			oks.setText(CommonConstant.SHARE_CONTENT + "下载地址"
					+ CommonConstant.SHARE_TARGET_URL + " ");
			if (null != shareImagePath) {
				oks.setImagePath(shareImagePath);
			}
			oks.setCallback(listener);
			oks.show(this);
		}
	}

	/**
	 * 
	 * @Description: 分享到微信好友
	 */
	private void shareToWeChatSession() {
		// 纯图片分享
		shareParams.setText(null);
		shareParams.setTitle(CommonConstant.SHARE_TITLE);
		// 分享类型 图片
		shareParams.setShareType(Platform.SHARE_IMAGE);
		Platform wxPlatform = ShareSDK.getPlatform(Wechat.NAME);
		wxPlatform.setPlatformActionListener(listener);
		wxPlatform.share(shareParams);
	}

	/**
	 * 
	 * @Description: 分享到微信朋友圈
	 */
	private void shareToWeChatTimeline() {
		// 纯图片分享
		shareParams.setText(null);
		shareParams.setTitle(CommonConstant.SHARE_TITLE);
		// 分享类型 图片
		shareParams.setShareType(Platform.SHARE_IMAGE);
		Platform wxTimeLinePlatform = ShareSDK.getPlatform(WechatMoments.NAME);
		wxTimeLinePlatform.setPlatformActionListener(listener);
		wxTimeLinePlatform.share(shareParams);
	}

	/**
	 * 
	 * @Description: 分享到微信收藏
	 */
	private void shareToWeChatFavorite() {
		// 纯图片分享
		shareParams.setText(null);
		shareParams.setTitle(CommonConstant.SHARE_TITLE);
		// 分享类型 图片
		shareParams.setShareType(Platform.SHARE_IMAGE);
		Platform wxFavoritePlatform = ShareSDK.getPlatform(WechatFavorite.NAME);
		wxFavoritePlatform.setPlatformActionListener(listener);
		wxFavoritePlatform.share(shareParams);
	}

	/**
	 * 
	 * @Description: 分享更多
	 */
	private void share(String packages, Uri uri) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		if (uri != null) {
			intent.setType("image/*");
			intent.putExtra(Intent.EXTRA_STREAM, uri);
		} else {
			intent.setType("text/plain");
		}
		intent.putExtra(Intent.EXTRA_SUBJECT, "分享到");
		intent.putExtra(Intent.EXTRA_TEXT, CommonConstant.SHARE_CONTENT
				+ "下载地址" + CommonConstant.SHARE_TARGET_URL);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (!TextUtils.isEmpty(packages))
			intent.setPackage(packages);
		startActivity(Intent.createChooser(intent, "分享到"));
	}
}
