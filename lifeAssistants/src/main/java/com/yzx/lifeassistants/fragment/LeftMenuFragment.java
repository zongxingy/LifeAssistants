package com.yzx.lifeassistants.fragment;

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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import cn.bmob.v3.datatype.BmobFile;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.UMImage;
import com.yzx.lifeassistants.GlobalParams;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.activity.MainActivity;
import com.yzx.lifeassistants.adapter.MaterialSimpleListAdapter;
import com.yzx.lifeassistants.adapter.MenuItemAdapter;
import com.yzx.lifeassistants.base.ActivityCollector;
import com.yzx.lifeassistants.base.BaseApplication;
import com.yzx.lifeassistants.bean.ShareListItem;
import com.yzx.lifeassistants.common.CommonConstant;
import com.yzx.lifeassistants.utils.AppUtils;
import com.yzx.lifeassistants.utils.FileUtils;
import com.yzx.lifeassistants.utils.LogcatUtils;
import com.yzx.lifeassistants.utils.ToastUtils;
import com.yzx.lifeassistants.view.RoundedImageView;

/**
 * @author: yzx
 * @date: 2015-9-11 下午3:52:25
 * @description: 左侧菜单栏
 */
public class LeftMenuFragment extends Fragment implements OnItemClickListener,
		OnClickListener {
	private ListView menuLV;// 菜单列表
	private List<String> dataList;// 菜单列表
	private MenuItemAdapter adapter;// 列表适配器
	private RoundedImageView userIconRIM;// 用户头像
	private TextView userNickTV;// 用户昵称
	private String shareImagePath;// 分享图片路径
	// //////////QQ,QZone使用友盟分享,其余使用ShareSDK分享//////////////
	private ShareParams shareParams;// ShareSDK分享内容
	private PlatformActionListener listener;// ShareSDK分享回调监听
	private UMSocialService mShareController;// 友盟分享控制器
	private SnsPostListener snsPostListener;// 友盟分享回调监听

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_leftmenu, container,
				false);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		setListenter();
		initData();
	}

	/**
	 * 
	 * @Description: 初始化控件
	 */
	private void initView() {
		menuLV = (ListView) getView().findViewById(R.id.leftmenu_list);
		userIconRIM = (RoundedImageView) getView().findViewById(
				R.id.leftmenu_user_icon);
		userNickTV = (TextView) getView().findViewById(R.id.leftmenu_user_name);
	}

	/**
	 * 
	 * @Description: 设置监听
	 */
	private void setListenter() {
		userIconRIM.setOnClickListener(this);
		menuLV.setOnItemClickListener(this);
	}

	/**
	 * 
	 * @Description: 初始化数据
	 */
	private void initData() {
		// 把Assets文件夹下的图片加载到本地
		FileUtils.getImageFromAssetsFile(getActivity(), "share.png");
		dataList = new ArrayList<String>();
		dataList.add("失物招领");
		dataList.add("记账能手");
		dataList.add("二手市场");
		dataList.add("个人设置");
		dataList.add("推荐分享");
		dataList.add("退出应用");
		adapter = new MenuItemAdapter(getActivity(), dataList);
		menuLV.setAdapter(adapter);
		menuLV.setChoiceMode(ListView.CHOICE_MODE_SINGLE);// 设置单选
		menuLV.setItemChecked(3, true);// 默认选中个人设置界面
		// //////////////////////ShareSDK分享配置////////////////////////
		if (FileUtils.isFileExist("/share.png")) {
			shareImagePath = FileUtils.SDPATH + "/share.png";
		}
		shareParams = new ShareParams();
		// 标题
		shareParams.setTitle(CommonConstant.SHARE_TITLE);
		// 文字
		shareParams.setText(CommonConstant.SHARE_CONTENT);
		// 网址
		shareParams.setTitleUrl(CommonConstant.SHARE_TARGET_URL);
		if (null != shareImagePath) {
			// 图片 QQ,QZone只支持ImagePath,不支持ImageData(Bitmap)
			shareParams.setImagePath(shareImagePath);
		}
		// 跳转网页
		shareParams.setUrl(CommonConstant.SHARE_TARGET_URL);

		// 分享类型 网页
		shareParams.setShareType(Platform.SHARE_WEBPAGE);
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
		// 文字
		mShareController.setShareContent(CommonConstant.SHARE_CONTENT);
		Bitmap bitmap = FileUtils.getImageFromAssetsFile("share.png");
		if (null != bitmap) {
			// 图片
			mShareController.setShareImage(new UMImage(getActivity(), bitmap));
		}
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
	public void onResume() {
		super.onResume();
		// 头像和用户昵称放onResume，在设置界面更改后也会重新加载
		userNickTV.setText(GlobalParams.userInfo.getNick());
		BmobFile avatarFile = GlobalParams.userInfo.getAvatar();
		if (null != avatarFile) {
			ImageLoader.getInstance().displayImage(
					avatarFile.getFileUrl(getActivity()),
					userIconRIM,
					BaseApplication.getInstance().getOptions(
							R.drawable.user_icon_default_main),
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							super.onLoadingComplete(imageUri, view, loadedImage);
						}
					});
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		menuLV.setItemChecked(position, true);
		Fragment newContent = null;
		switch (position) {
		case 0: {// 失物招领
			newContent = new LostAndFindFragment();
			break;
		}
		case 1: {// 记账能手
			newContent = new ChargeUpFragment();
			break;
		}
		case 2: {// 二手交易
			newContent = new SecondHandFragment();
			break;
		}
		case 3: {// 个人设置
			newContent = new SettingFragment();
			break;
		}
		case 4: {// 推荐分享
			showShareDialog();
			break;
		}
		case 5: {// 退出应用
			quitApp();
			break;
		}
		default:
			break;
		}
		if (newContent != null) {
			switchFragment(newContent, dataList.get(position));
		}
	}

	/**
	 * @function: 切换Fragment
	 * @param newContent
	 * @param string
	 */
	private void switchFragment(Fragment fragment, String title) {
		if (getActivity() == null) {
			return;
		}
		if (getActivity() instanceof MainActivity) {
			MainActivity activity = (MainActivity) getActivity();
			activity.switchContent(fragment, title);

		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.leftmenu_user_icon: {// 点击用户头像跳转到个人设置
			menuLV.setItemChecked(3, true);
			switchFragment(new SettingFragment(), "个人设置");
			break;
		}

		default:
			break;
		}
	}

	/**
	 * 
	 * @Description: 退出应用
	 */
	private void quitApp() {
		new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
				.setTitleText(
						"你确定要退出" + getResources().getString(R.string.app_name)
								+ "应用吗？")
				.setContentText("退出后将收不到消息！！！")
				.setCancelText("取消")
				.setConfirmText("确定")
				.showCancelButton(true)
				.setCancelClickListener(null)
				.setConfirmClickListener(
						new SweetAlertDialog.OnSweetClickListener() {

							@Override
							public void onClick(

							SweetAlertDialog sweetAlertDialog) {// 退出应用
								ActivityCollector.finishAll();

							}
						}).show();
	}

	/**
	 * 
	 * @Description: 弹出推荐分享框
	 */
	@SuppressLint("NewApi")
	private void showShareDialog() {
		AlertDialog.Builder builder = new Builder(getActivity(),
				R.style.ShareDialog);
		builder.setTitle("分享到");
		final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(
				getActivity());
		String[] array = getResources().getStringArray(
				R.array.share_dialog_text);
		adapter.add(new ShareListItem.Builder(getActivity()).content(array[0])
				.icon(R.drawable.ssdk_oks_skyblue_logo_qq_checked).build());
		adapter.add(new ShareListItem.Builder(getActivity()).content(array[1])
				.icon(R.drawable.ssdk_oks_skyblue_logo_qzone_checked).build());
		adapter.add(new ShareListItem.Builder(getActivity()).content(array[2])
				.icon(R.drawable.ssdk_oks_skyblue_logo_sinaweibo_checked)
				.build());
		adapter.add(new ShareListItem.Builder(getActivity()).content(array[3])
				.icon(R.drawable.ssdk_oks_skyblue_logo_wechat_checked).build());
		adapter.add(new ShareListItem.Builder(getActivity()).content(array[4])
				.icon(R.drawable.ssdk_oks_skyblue_logo_wechatfavorite_checked)
				.build());
		adapter.add(new ShareListItem.Builder(getActivity()).content(array[5])
				.icon(R.drawable.ssdk_oks_skyblue_logo_wechatmoments_checked)
				.build());
		adapter.add(new ShareListItem.Builder(getActivity()).content(array[6])
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
		Display display = getActivity().getWindowManager().getDefaultDisplay();
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
		// 使用ShareSDK分享纯图片时无回调
		// Platform qqPlatform = ShareSDK.getPlatform(QQ.NAME);
		// qqPlatform.setPlatformActionListener(listener);
		// qqPlatform.share(shareParams);
		// 使用友盟分享 QQ图文分享
		if (AppUtils.isInstallApplication("com.tencent.mobileqq")) {
			mShareController.postShare(getActivity(), SHARE_MEDIA.QQ,
					snsPostListener);
		} else {
			ToastUtils.showToast("请先安装最新版QQ~");
		}
	}

	/**
	 * 
	 * @Description: 分享到空间
	 */
	private void shareToQzone() {
		// 使用ShareSDK分享 图片需要先上传服务器速度慢
		// Platform qZonePlatform = ShareSDK.getPlatform(QZone.NAME);
		// qZonePlatform.setPlatformActionListener(listener);
		// qZonePlatform.share(shareParams);
		// 使用友盟分享 QQ空间图文分享
		mShareController.postShare(getActivity(), SHARE_MEDIA.QZONE,
				snsPostListener);
	}

	/**
	 * 
	 * @Description: 分享到新浪微博
	 */
	private void shareToSinaWeibo() {
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
			oks.show(getActivity());
		}
	}

	/**
	 * 
	 * @Description: 分享到微信好友
	 */
	private void shareToWeChatSession() {
		Platform wxPlatform = ShareSDK.getPlatform(Wechat.NAME);
		wxPlatform.setPlatformActionListener(listener);
		wxPlatform.share(shareParams);
	}

	/**
	 * 
	 * @Description: 分享到微信朋友圈
	 */
	private void shareToWeChatTimeline() {
		Platform wxTimeLinePlatform = ShareSDK.getPlatform(WechatMoments.NAME);
		wxTimeLinePlatform.setPlatformActionListener(listener);
		wxTimeLinePlatform.share(shareParams);
	}

	/**
	 * 
	 * @Description: 分享到微信收藏
	 */
	private void shareToWeChatFavorite() {
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
