package com.yzx.lifeassistants.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;
import cn.bmob.v3.update.UpdateStatus;
import cn.pedant.SweetAlert.SweetAlertDialog;

import com.balysv.materialripple.MaterialRippleLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.yzx.lifeassistants.GlobalParams;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.activity.EditUserNickActivity;
import com.yzx.lifeassistants.activity.FeedbackActivity;
import com.yzx.lifeassistants.activity.LoginActivity;
import com.yzx.lifeassistants.activity.ModifyPwdActivity;
import com.yzx.lifeassistants.base.ActivityCollector;
import com.yzx.lifeassistants.base.BaseApplication;
import com.yzx.lifeassistants.common.CommonConstant;
import com.yzx.lifeassistants.utils.CacheUtils;
import com.yzx.lifeassistants.utils.LogcatUtils;
import com.yzx.lifeassistants.utils.SpUtils;
import com.yzx.lifeassistants.utils.ToastUtils;
import com.yzx.lifeassistants.view.RoundedImageView;
import com.yzx.lifeassistants.view.widget.CircularLoadingDialog;

/**
 * @author: yzx
 * @date: 2015-9-11 下午2:15:27
 * @description: 个人设置
 */
@SuppressLint("InflateParams")
public class SettingFragment extends Fragment implements OnClickListener {
	protected SpUtils sputil;// 保存
	private CircularLoadingDialog dialog;// 加载框
	private TextView topTitleTV;// 顶部标题
	private RelativeLayout userIconRL;// 用户头像
	private RoundedImageView userIconIV;// 头像
	private RelativeLayout userNickRL;// 用户昵称
	private TextView userNickTV;// 昵称
	private RelativeLayout userSexRL;// 用户性别
	private CheckBox userSexCB;// 性别
	private RelativeLayout rememberPwdRL;// 记住密码
	private CheckBox rememberPwdCB;// 记住密码
	private RelativeLayout autoLoginRL;// 自动登录
	private CheckBox autoLoginCB;// 自动登录
	private RelativeLayout updateRL;// 检查更新
	private RelativeLayout modifyPwdRL;// 修改密码
	private RelativeLayout logoutRL;// 用户登出
	private RelativeLayout feedbackRL;// 意见反馈
	private Button quitAppBtn;// 退出应用按钮

	private View albumView;// 选择图片来源
	private AlertDialog albumDialog;// 选择图片来源
	private File tempFile;// 头像缓存
	private Bitmap bitmap;// 头像
	private String dateTime;// 拍照时间
	private String iconUrl;// 头像Url

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (null == sputil) {
			sputil = new SpUtils(getActivity(), CommonConstant.SP_FAIL_NAME);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_setting, container,
				false);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		setListener();
		initMaterialRipple();
		initAlbumDialog();
		initData();

	}

	/**
	 * 
	 * @Description: 初始化控件
	 */
	private void initView() {
		topTitleTV = (TextView) getView().findViewById(R.id.top_title_tv);
		userIconRL = (RelativeLayout) getView().findViewById(
				R.id.setting_user_icon_rl);
		userIconIV = (RoundedImageView) getView().findViewById(
				R.id.setting_user_icon_image);
		userNickRL = (RelativeLayout) getView().findViewById(
				R.id.setting_user_nick_rl);
		userNickTV = (TextView) getView().findViewById(
				R.id.setting_user_nick_text);
		userSexRL = (RelativeLayout) getView().findViewById(
				R.id.setting_sex_choice_rl);
		userSexCB = (CheckBox) getView().findViewById(
				R.id.setting_sex_choice_switch);
		rememberPwdRL = (RelativeLayout) getView().findViewById(
				R.id.setting_rember_pwd_rl);
		rememberPwdCB = (CheckBox) getView().findViewById(
				R.id.setting_rember_pwd_switch);
		autoLoginRL = (RelativeLayout) getView().findViewById(
				R.id.setting_auto_login_rl);
		autoLoginCB = (CheckBox) getView().findViewById(
				R.id.setting_auto_login_switch);
		updateRL = (RelativeLayout) getView().findViewById(
				R.id.setting_update_rl);
		modifyPwdRL = (RelativeLayout) getView().findViewById(
				R.id.setting_modify_pwd_rl);
		logoutRL = (RelativeLayout) getView().findViewById(
				R.id.setting_logout_rl);
		feedbackRL = (RelativeLayout) getView().findViewById(
				R.id.setting_feedback_rl);
		quitAppBtn = (Button) getView().findViewById(R.id.setting_quit_app_btn);

	}

	/**
	 * 
	 * @Description: 设置监听
	 */
	private void setListener() {
		userIconRL.setOnClickListener(this);
		userNickRL.setOnClickListener(this);
		userSexCB.setOnClickListener(this);
		rememberPwdCB.setOnClickListener(this);
		autoLoginCB.setOnClickListener(this);
		updateRL.setOnClickListener(this);
		modifyPwdRL.setOnClickListener(this);
		logoutRL.setOnClickListener(this);
		feedbackRL.setOnClickListener(this);
		quitAppBtn.setOnClickListener(this);

	}

	/**
	 * 
	 * @Description: 瓷砖特效
	 */
	private void initMaterialRipple() {
		List<View> views = new ArrayList<View>();
		views.add(userIconRL);
		views.add(userNickRL);
		views.add(userSexRL);
		views.add(rememberPwdRL);
		views.add(autoLoginRL);
		views.add(updateRL);
		views.add(modifyPwdRL);
		views.add(logoutRL);
		views.add(feedbackRL);
		for (View view : views) {
			// 动态特效
			MaterialRippleLayout
					.on(view)
					.rippleColor(
							Color.parseColor(CommonConstant.RIPPLE_COLOR_LITHT))
					.rippleAlpha(CommonConstant.RIPPLE_ALPHA).rippleHover(true)
					.create();
		}
		// 动态特效
		MaterialRippleLayout
				.on(quitAppBtn)
				.rippleColor(Color.parseColor(CommonConstant.RIPPLE_COLOR_DARK))
				.rippleAlpha(CommonConstant.RIPPLE_ALPHA).rippleHover(true)
				.create();

	}

	/**
	 * 
	 * @Description: 初始化选择图片来源弹框
	 */
	private void initAlbumDialog() {
		albumDialog = new AlertDialog.Builder(getActivity()).create();
		albumDialog.setCanceledOnTouchOutside(true);
		albumView = LayoutInflater.from(getActivity()).inflate(
				R.layout.dialog_select_picture, null);
		TextView albumPic = (TextView) albumView.findViewById(R.id.album_pic);
		TextView cameraPic = (TextView) albumView.findViewById(R.id.camera_pic);
		// 动态特效
		MaterialRippleLayout
				.on(albumPic)
				.rippleColor(Color.parseColor(CommonConstant.RIPPLE_COLOR_DARK))
				.rippleAlpha(CommonConstant.RIPPLE_ALPHA).rippleHover(true)
				.create();
		MaterialRippleLayout
				.on(cameraPic)
				.rippleColor(Color.parseColor(CommonConstant.RIPPLE_COLOR_DARK))
				.rippleAlpha(CommonConstant.RIPPLE_ALPHA).rippleHover(true)
				.create();
		albumPic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				albumDialog.dismiss();
				Date date1 = new Date(System.currentTimeMillis());
				dateTime = date1.getTime() + "";
				getAvataFromAlbum();
			}
		});
		cameraPic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				albumDialog.dismiss();
				Date date = new Date(System.currentTimeMillis());
				dateTime = date.getTime() + "";
				getAvataFromCamera();
			}
		});
	}

	/**
	 * 
	 * @Description: 初始化数据
	 */
	private void initData() {
		dialog = new CircularLoadingDialog(getActivity());
		topTitleTV.setText(getResources().getString(R.string.setting));
		// 昵称
		userNickTV.setText(GlobalParams.userInfo.getNick());
		// 性别
		if (GlobalParams.userInfo.getSex() != null) {
			userSexCB.setChecked(GlobalParams.userInfo.getSex());
		} else {
			userSexCB.setChecked(false);
		}
		// 是否记住密码
		Boolean remberPwd = sputil.getValue(CommonConstant.REMBER_PWD_KEY,
				false);
		rememberPwdCB.setChecked(remberPwd);
		// 是否自动登陆
		Boolean autoLogin = sputil.getValue(CommonConstant.AUTO_LOGIN_KEY,
				false);
		autoLoginCB.setChecked(autoLogin);
		// 头像
		BmobFile avatarFile = GlobalParams.userInfo.getAvatar();
		if (null != avatarFile) {
			ImageLoader.getInstance().displayImage(
					avatarFile.getFileUrl(getActivity()),
					userIconIV,
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setting_user_icon_rl: {// 修改头像
			showAlbumDialog();
			break;
		}
		case R.id.setting_user_nick_rl: {// 修改昵称
			editNick();
			break;
		}
		case R.id.setting_sex_choice_switch: {// 修改性别
			switchSex();
			break;
		}
		case R.id.setting_rember_pwd_switch: {// 记住密码
			switchRemberPwd();
			break;
		}
		case R.id.setting_auto_login_switch: {// 自动登录
			switchAutoLogin();
			break;
		}
		case R.id.setting_update_rl: {// 检查更新
			CheckUpdate();
			break;
		}
		case R.id.setting_modify_pwd_rl: {// 修改密码
			modifyPwd();
			break;
		}
		case R.id.setting_logout_rl: {// 用户登出
			loginOut();
			break;
		}
		case R.id.setting_feedback_rl: {// 意见反馈
			feedback();
			break;
		}
		case R.id.setting_quit_app_btn: {// 退出应用
			quitApp();
			break;
		}
		default:
			break;
		}
	}

	/**
	 * 
	 * @Description: 弹框选择图片来源
	 */
	@SuppressLint("InflateParams")
	private void showAlbumDialog() {
		albumDialog.show();
		albumDialog.setContentView(albumView);
		albumDialog.getWindow().setGravity(Gravity.CENTER);
	}

	/**
	 * 
	 * @Description: 从相册获取
	 */
	protected void getAvataFromAlbum() {
		// 激活系统图库，选择一张图片
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, CommonConstant.REQUESTCODE_PHOTO_ALBUM);
	}

	/**
	 * 
	 * @Description: 从拍照获取
	 */
	protected void getAvataFromCamera() {
		File f = new File(CacheUtils.getCacheDirectory(getActivity(), true,
				"icon") + dateTime);
		if (f.exists()) {
			f.delete();
		}
		try {
			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Uri uri = Uri.fromFile(f);
		Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		camera.putExtra(MediaStore.EXTRA_OUTPUT, uri);

		startActivityForResult(camera, CommonConstant.REQUESTCODE_PHOTO_CAMERA);
	}

	/**
	 * 
	 * @Description: 判断存储卡是否可以用
	 */
	private boolean hasSdcard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @Description: 修改昵称
	 */
	private void editNick() {
		Intent intent = new Intent(getActivity(), EditUserNickActivity.class);
		startActivityForResult(intent, CommonConstant.REQUESTCODE_EDIT_NICK);
	}

	/**
	 * 
	 * @Description: 修改性别
	 */
	private void switchSex() {
		dialog.show();
		if (userSexCB.isChecked()) {
			GlobalParams.userInfo.setSex(true);
		} else {
			GlobalParams.userInfo.setSex(false);
		}
		GlobalParams.userInfo.update(getActivity(),
				GlobalParams.userInfo.getObjectId(), new UpdateListener() {

					@Override
					public void onSuccess() {// 修改成功
						LogcatUtils.i("修改性别成功");
						dialog.dismiss();
						ToastUtils.showToast("修改性别成功~");
					}

					@Override
					public void onFailure(int code, String message) {// 修改失败
						LogcatUtils.e("修改性别失败：" + code + " " + message);
						GlobalParams.userInfo.setSex(!GlobalParams.userInfo
								.getSex());
						dialog.dismiss();
						switch (code) {
						case 9010: {// 网络超时
							ToastUtils.showToast("网络超时，请检查您的手机网络~");
							break;
						}
						case 9016: {// 无网络连接，请检查您的手机网络
							ToastUtils.showToast("无网络连接，请检查您的手机网络~");
							break;
						}
						default: {
							ToastUtils.showToast("修改失败，请重试~");
							break;
						}
						}
					}
				});
	}

	/**
	 * 
	 * @Description: 检查更新
	 */
	private void CheckUpdate() {
		dialog.show();
		// 关闭Toast
		BmobUpdateAgent.setUpdateCheckConfig(false);
		// 不只是在WiFi条件下更新
		BmobUpdateAgent.setUpdateOnlyWifi(false);
		// 设置监听
		BmobUpdateAgent.setUpdateListener(new BmobUpdateListener() {

			@Override
			public void onUpdateReturned(int updateStatus,
					UpdateResponse updateInfo) {
				dialog.dismiss();
				switch (updateStatus) {
				case UpdateStatus.Yes: {// 版本有更新
					break;
				}
				case UpdateStatus.No: {// 版本无更新
					ToastUtils.showToast("目前版本为最新版~");
					break;
				}
				case UpdateStatus.IGNORED: {// 忽略更新
					ToastUtils.showToast("该版本已被忽略更新~");
					break;
				}
				case UpdateStatus.TimeOut: {// 查询出错或超时
					ToastUtils.showToast("网络超时，请检查您的手机网络~");
					break;
				}
				case UpdateStatus.EmptyField: {// 此提示只是提醒开发者关注那些必填项，测试成功后，无需对用户提示
					LogcatUtils
							.e("请检查你AppVersion表的必填项，1、target_size（文件大小）是否填写；2、path或者android_url两者必填其中一项。");
					ToastUtils.showToast("更新失败，请重试~");
					break;
				}
				case UpdateStatus.ErrorSizeFormat: {// 此提示只是提醒开发者关注那些必填项，测试成功后，无需对用户提示
					LogcatUtils
							.e("请检查target_size填写的格式，请使用file.length()方法获取apk大小。");
					ToastUtils.showToast("更新失败，请重试~");
					break;
				}
				default: {
					ToastUtils.showToast("更新失败，请重试~");
					break;
				}
				}
			}
		});
		// 更新
		BmobUpdateAgent.forceUpdate(getActivity());
	}

	/**
	 * 
	 * @Description: 修改密码
	 */
	private void modifyPwd() {
		Intent intent = new Intent(getActivity(), ModifyPwdActivity.class);
		startActivity(intent);
	}

	/**
	 * 
	 * @Description: 切换是否记住密码
	 */
	private void switchRemberPwd() {
		if (rememberPwdCB.isChecked()) {
			sputil.setValue(CommonConstant.REMBER_PWD_KEY, true);
		} else {
			sputil.setValue(CommonConstant.REMBER_PWD_KEY, false);
			autoLoginCB.setChecked(false);
			sputil.setValue(CommonConstant.AUTO_LOGIN_KEY, false);
		}
	}

	/**
	 * 
	 * @Description: 切换是否自动登录
	 */
	private void switchAutoLogin() {
		if (autoLoginCB.isChecked()) {
			sputil.setValue(CommonConstant.AUTO_LOGIN_KEY, true);
			rememberPwdCB.setChecked(true);
			sputil.setValue(CommonConstant.REMBER_PWD_KEY, true);
		} else {
			sputil.setValue(CommonConstant.AUTO_LOGIN_KEY, false);
		}
	}

	/**
	 * 
	 * @Description: 用户注销
	 */
	private void loginOut() {
		new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
				.setTitleText("你确定要退出该用户吗？")
				.setContentText("退出后将收不到消息！！！")
				.setCancelText("取消")
				.setConfirmText("确定")
				.showCancelButton(true)
				.setCancelClickListener(null)
				.setConfirmClickListener(
						new SweetAlertDialog.OnSweetClickListener() {

							@Override
							public void onClick(

							SweetAlertDialog sweetAlertDialog) {// 用户登出
								BmobUser.logOut(getActivity());
								// 清除是否记住密码以及自动登录
								sputil.setValue(CommonConstant.REMBER_PWD_KEY,
										false);
								sputil.setValue(CommonConstant.AUTO_LOGIN_KEY,
										false);
								Intent intent = new Intent(getActivity(),
										LoginActivity.class);
								intent.putExtra(CommonConstant.TO_LOGIN_NAME,
										CommonConstant.FROM_LOGOUT_TO_LOGIN_KEY);
								startActivity(intent);

							}
						}).show();
	}

	/**
	 * 
	 * @Description: 意见反馈
	 */
	private void feedback() {
		Intent intent = new Intent(getActivity(), FeedbackActivity.class);
		startActivityForResult(intent, CommonConstant.REQUESTCODE_FEEDBACK);
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == CommonConstant.REQUESTCODE_EDIT_NICK) {

			if (resultCode == CommonConstant.RESULTCODE_EDIT_NICK_OK) {
				// 修改昵称成功
				userNickTV.setText(GlobalParams.userInfo.getNick());
				ToastUtils.showToast("更改昵称成功~");

			} else if (resultCode == CommonConstant.RESULTCODE_EDIT_NICK_CANCEL) {
				// 取消修改昵称
				LogcatUtils.i("已取消更改昵称");

			}
		}

		if (resultCode == Activity.RESULT_OK) {
			// 访问相册
			if (requestCode == CommonConstant.REQUESTCODE_PHOTO_ALBUM) {
				if (data != null) {
					// 得到图片的全路径
					Uri uri = data.getData();
					crop(uri);
				}
			}
			// 访问相机
			if (requestCode == CommonConstant.REQUESTCODE_PHOTO_CAMERA) {

				if (hasSdcard()) {
					String files = CacheUtils.getCacheDirectory(getActivity(),
							true, "icon") + dateTime;
					tempFile = new File(files);
					if (tempFile.exists() && tempFile.length() > 0) {
						Uri uri = Uri.fromFile(tempFile);
						crop(uri);
					} else {
						ToastUtils.showToast("!!!");
					}
				} else {
					ToastUtils.showToast("未找到存储卡，无法存储照片！");
				}
			}
			// 图片裁剪
			if (requestCode == CommonConstant.REQUESTCODE_PHOTO_CUT) {

				try {
					bitmap = data.getParcelableExtra("data");
					iconUrl = saveToSdCard(bitmap);
					userIconIV.setImageBitmap(bitmap);
					updateIcon(iconUrl);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (requestCode == CommonConstant.REQUESTCODE_FEEDBACK) {
			if (resultCode == CommonConstant.RESULTCODE_FEEDBACK_CANCEL) {
				// 取消反馈
				LogcatUtils.i("已取消反馈意见");
			} else if (resultCode == CommonConstant.RESULTCODE_FEEDBACK_OK) {
				// 反馈成功
				ToastUtils.showToast("感谢您的宝贵意见~");
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 
	 * @Description: 切图
	 */
	private void crop(Uri uri) {
		// 裁剪图片意图
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// 裁剪框的比例，1：1
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// 裁剪后输出图片的尺寸大小
		intent.putExtra("outputX", 200);
		intent.putExtra("outputY", 200);
		// 图片格式
		intent.putExtra("outputFormat", "JPEG");
		intent.putExtra("noFaceDetection", true);// 取消人脸识别
		intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
		startActivityForResult(intent, CommonConstant.REQUESTCODE_PHOTO_CUT);
	}

	/**
	 * 
	 * @Description: 把图片保存到sdcard
	 */
	private String saveToSdCard(Bitmap bitmap) {
		String files = CacheUtils
				.getCacheDirectory(getActivity(), true, "icon")
				+ dateTime
				+ "_12.jpg";
		File file = new File(files);
		try {
			FileOutputStream out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
				out.flush();
				out.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file.getAbsolutePath();
	}

	/**
	 * 
	 * @Description: 更新头像
	 */
	private void updateIcon(String avataPath) {
		if (avataPath != null) {
			final BmobFile file = new BmobFile(new File(avataPath));
			dialog.show();
			file.upload(getActivity(), new UploadFileListener() {

				@Override
				public void onSuccess() {// 上传成功
					dialog.dismiss();
					GlobalParams.userInfo.setAvatar(file);
					dialog.show();
					GlobalParams.userInfo.update(getActivity(),
							new UpdateListener() {

								@Override
								public void onSuccess() {// 更新成功
									dialog.dismiss();
									LogcatUtils.i("更改头像成功");
									ToastUtils.showToast("更改头像成功~");
									// 更新侧滑菜单的用户头像
									LeftMenuFragment leftMenu = (LeftMenuFragment) getFragmentManager()
											.findFragmentById(R.id.menu_frame);
									leftMenu.onResume();
								}

								@Override
								public void onFailure(int code, String message) {// 更新失败
									LogcatUtils.e("更改头像失败：" + code + " "
											+ message);
									dialog.dismiss();
									switch (code) {
									case 9010: {// 网络超时
										ToastUtils.showToast("网络超时，请检查您的手机网络~");
										break;
									}
									case 9016: {// 无网络连接，请检查您的手机网络
										ToastUtils
												.showToast("无网络连接，请检查您的手机网络~");
										break;
									}
									default: {
										ToastUtils.showToast("更改头像失败，请重试~");
										break;
									}
									}
								}
							});
				}

				@Override
				public void onFailure(int code, String message) {// 上传失败
					dialog.dismiss();
					LogcatUtils.e("上传头像失败：" + code + " " + message);
					switch (code) {
					case 9010: {// 网络超时
						ToastUtils.showToast("网络超时，请检查您的手机网络~");
						break;
					}
					case 9016: {// 无网络连接，请检查您的手机网络
						ToastUtils.showToast("无网络连接，请检查您的手机网络~");
						break;
					}
					default: {
						ToastUtils.showToast("更改头像失败，请重试~");
						break;
					}
					}
				}
			});
		}
	}
}
