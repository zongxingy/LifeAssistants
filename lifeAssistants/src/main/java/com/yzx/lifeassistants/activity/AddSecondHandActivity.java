package com.yzx.lifeassistants.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

import com.balysv.materialripple.MaterialRippleLayout;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadBatchListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.METValidator;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;
import com.yzx.lifeassistants.GlobalParams;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.adapter.EditGridAdapter;
import com.yzx.lifeassistants.adapter.MyArrayAdapter;
import com.yzx.lifeassistants.base.BaseActivity;
import com.yzx.lifeassistants.bean.ImageItem;
import com.yzx.lifeassistants.bean.SecondHandGoods;
import com.yzx.lifeassistants.common.CommonConstant;
import com.yzx.lifeassistants.utils.DensityUtils;
import com.yzx.lifeassistants.utils.FileUtils;
import com.yzx.lifeassistants.utils.ImageUtils;
import com.yzx.lifeassistants.utils.LogcatUtils;
import com.yzx.lifeassistants.utils.ToastUtils;
import com.yzx.lifeassistants.utils.VerifyUtils;
import com.yzx.lifeassistants.view.widget.CircularLoadingDialog;
import com.yzx.lifeassistants.view.widget.NoScrollGridView;

/**
 * @Description: 添加发布二手物品界面
 * @author: yzx
 * @time: 2015-11-13 上午11:04:16
 */
public class AddSecondHandActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener, OnItemLongClickListener {
	private String from;// 从哪个界面跳转到这
	private Boolean isNew;// 是否新增
	private SecondHandGoods secondHandInfo;// 闲置信息
	private CircularLoadingDialog dialog;// 加载框
	/* ###################### 顶部信息 ############################## */
	private ImageButton topBackBtn;// 顶部返回按钮
	private TextView topTitleTV;// 顶部标题
	private ImageButton topSendBtn;// 顶部发布按钮

	/* ###################### 控制展现内容 ############################ */
	private RelativeLayout itemTitleRL;// 物品信息 标题
	private RelativeLayout contactTitleRL;// 联系信息 标题
	private ImageView itemArrowIV;// 向上或向下箭头
	private ImageView contactArrowIV;// 向上或向下箭头
	private LinearLayout itemLL;// 物品信息 内容
	private LinearLayout contactLL;// 联系信息 内容

	private List<ImageView> imgList;//
	private List<LinearLayout> llList;//
	private List<Boolean> isOpenList;//

	/* ###################### 内容信息编辑 ############################ */
	private MaterialEditText titleET;// 标题
	private MaterialBetterSpinner typeSp;// 类型
	private MaterialEditText priceET;// 价格
	private MaterialEditText describeET;// 描述
	private NoScrollGridView picGV;// 图片列表
	private MaterialBetterSpinner bargainSp;// 议价
	private MaterialEditText qqET;// QQ
	private MaterialEditText phoneET;// 电话

	private List<BmobFile> picFileList;// 已经上传在服务器的图片文件
	private List<ImageItem> picList;// 图片列表
	private EditGridAdapter editGridAdapter;// 图片列表适配器
	private AlertDialog albumDialog;// 选择图片来源
	private String imagePath;// 图片路径
	private File tempFile;// 图片缓存

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_second_hand);
		init();
		initView();
		setListener();
		initData();
		initMaterialRipple();
	}

	/**
	 * 
	 * @Description: 初始化从上个界面传来的值
	 */
	private void init() {
		Intent intent = getIntent();
		if (null != intent) {
			from = intent.getStringExtra("from");
		}

	}

	/**
	 * 
	 * @Description: 初始化控件
	 */
	private void initView() {
		topBackBtn = (ImageButton) findViewById(R.id.top_back_btn);
		topTitleTV = (TextView) findViewById(R.id.top_title_tv);
		topSendBtn = (ImageButton) findViewById(R.id.top_send_btn);

		itemTitleRL = (RelativeLayout) findViewById(R.id.second_hand_item_title_rl);
		contactTitleRL = (RelativeLayout) findViewById(R.id.second_hand_contact_title_rl);
		itemArrowIV = (ImageView) findViewById(R.id.second_hand_item_title_arrow_img);
		contactArrowIV = (ImageView) findViewById(R.id.second_hand_contact_title_arrow_img);
		itemLL = (LinearLayout) findViewById(R.id.second_hand_item_ll);
		contactLL = (LinearLayout) findViewById(R.id.second_hand_contact_ll);

		titleET = (MaterialEditText) findViewById(R.id.second_hand_item_title_et);
		typeSp = (MaterialBetterSpinner) findViewById(R.id.second_hand_item_type_spinner);
		priceET = (MaterialEditText) findViewById(R.id.second_hand_item_price_et);
		describeET = (MaterialEditText) findViewById(R.id.second_hand_item_describe_et);
		picGV = (NoScrollGridView) findViewById(R.id.second_hand_item_pic_gv);
		bargainSp = (MaterialBetterSpinner) findViewById(R.id.second_hand_contact_bargain_spinner);
		qqET = (MaterialEditText) findViewById(R.id.second_hand_contact_qq_et);
		phoneET = (MaterialEditText) findViewById(R.id.second_hand_contact_phone_et);

	}

	/**
	 * 
	 * @Description: 设置监听器
	 */
	private void setListener() {
		topBackBtn.setOnClickListener(this);
		topSendBtn.setOnClickListener(this);

		itemTitleRL.setOnClickListener(this);
		contactTitleRL.setOnClickListener(this);

		picGV.setOnItemClickListener(this);
		picGV.setOnItemLongClickListener(this);
	}

	/**
	 * 
	 * @Description: 初始化数据
	 */
	private void initData() {
		dialog = new CircularLoadingDialog(this);
		topTitleTV.setText("闲置发布");
		topSendBtn.setVisibility(View.VISIBLE);

		// 默认打开时候第一个选项为张开
		itemArrowIV.setImageResource(R.drawable.arrow_up);
		itemLL.setVisibility(View.VISIBLE);
		llList = new ArrayList<LinearLayout>();
		llList.add(itemLL);
		llList.add(contactLL);
		imgList = new ArrayList<ImageView>();
		imgList.add(itemArrowIV);
		imgList.add(contactArrowIV);
		isOpenList = new ArrayList<Boolean>();
		isOpenList.add(true);
		isOpenList.add(false);

		picList = new ArrayList<ImageItem>();
		editGridAdapter = new EditGridAdapter(this, picList);
		picGV.setAdapter(editGridAdapter);
		if (CommonConstant.FROM_SECOND_HAND_FRAGMENT.equals(from)) {
			// 添加
			isNew = true;
			secondHandInfo = new SecondHandGoods();
			picFileList = new ArrayList<BmobFile>();
		} else if (CommonConstant.FROM_SECOND_HAND_MY_FRAGMENT.equals(from)) {
			// 修改
			isNew = false;
			secondHandInfo = (SecondHandGoods) getIntent()
					.getSerializableExtra("secondHandInfo");
			initSecondHandInfo();
		}
	}

	/**
	 * 
	 * @Description: 初始化闲置物品信息
	 */
	private void initSecondHandInfo() {
		titleET.setText(secondHandInfo.getTitle());
		typeSp.setText(secondHandInfo.getType());
		priceET.setText(secondHandInfo.getPrice());
		describeET.setText(secondHandInfo.getDescribe());
		picFileList = secondHandInfo.getPicFileList();
		if (null == picFileList || 1 > picFileList.size()) {

		} else {
			for (BmobFile bmobFile : picFileList) {
				if (null != bmobFile) {
					ImageItem item = new ImageItem();
					item.setIsLocalPic(false);
					item.setFileName(bmobFile.getFilename());
					item.setImagePath(bmobFile
							.getFileUrl(AddSecondHandActivity.this));
					picList.add(item);
				}
			}
			editGridAdapter.notifyDataSetChanged();
		}
		if (secondHandInfo.getBargain()) {
			bargainSp.setText("可议价");
		} else {
			bargainSp.setText("不可议价");
		}
		qqET.setText(secondHandInfo.getQq());
		phoneET.setText(secondHandInfo.getPhone());

	}

	/**
	 * 
	 * @Description: 瓷砖特效
	 */
	private void initMaterialRipple() {
		// 设置Spinner的Adapter
		String[] types = getResources()
				.getStringArray(R.array.second_hand_type);
		List<String> typeList = Arrays.asList(types);
		MyArrayAdapter typeAdapter = new MyArrayAdapter(this, typeList);
		typeSp.setAdapter(typeAdapter);
		String[] bargains = getResources().getStringArray(
				R.array.second_hand_bargain);
		List<String> bargainList = Arrays.asList(bargains);
		MyArrayAdapter bargainAdapter = new MyArrayAdapter(this, bargainList);
		bargainSp.setAdapter(bargainAdapter);
		// 设置Padding
		titleET.setPaddings(DensityUtils.sp2px(this, 32), 0, 0, 0);
		typeSp.setPaddings(DensityUtils.sp2px(this, 32), 0, 0, 0);
		priceET.setPaddings(DensityUtils.sp2px(this, 32), 0, 0, 0);
		describeET.setPaddings(DensityUtils.sp2px(this, 32), 0, 0, 0);
		bargainSp.setPaddings(DensityUtils.sp2px(this, 32), 0, 0, 0);
		qqET.setPaddings(DensityUtils.sp2px(this, 32), 0, 0, 0);
		phoneET.setPaddings(DensityUtils.sp2px(this, 32), 0, 0, 0);
		// 设置验证信息
		priceET.addValidator(new METValidator("只能输入整数~") {

			@Override
			public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
				String number = text.toString();
				if ("".equals(number) || VerifyUtils.isDigit(number)) {
					return true;
				}
				return false;
			}
		});
		qqET.addValidator(new METValidator("请输入正确的QQ格式~") {

			@Override
			public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
				String number = text.toString();
				if ("".equals(number) || VerifyUtils.isDigit(number)) {
					return true;
				}
				return false;
			}
		});
		phoneET.addValidator(new METValidator("请输入正确的联系号码格式~") {

			@Override
			public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
				String number = text.toString();
				if ("".equals(number) || VerifyUtils.checkPhone(number)) {
					return true;
				}
				return false;
			}
		});

		// 动态特效
		MaterialRippleLayout
				.on(itemTitleRL)
				.rippleColor(
						Color.parseColor(CommonConstant.RIPPLE_COLOR_LITHT))
				.rippleAlpha(CommonConstant.RIPPLE_ALPHA).rippleHover(true)
				.create();
		MaterialRippleLayout
				.on(contactTitleRL)
				.rippleColor(
						Color.parseColor(CommonConstant.RIPPLE_COLOR_LITHT))
				.rippleAlpha(CommonConstant.RIPPLE_ALPHA).rippleHover(true)
				.create();
	}

	/**
	 * @Description: 按键监听
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_back_btn: {// 顶部返回
			finish();
			break;
		}
		case R.id.second_hand_item_title_rl: {// 展现物品信息
			showItemInfo();
			break;
		}
		case R.id.second_hand_contact_title_rl: {// 展现联系信息
			showContactInfo();
			break;
		}
		case R.id.top_send_btn: {// 顶部发布
			sendSecondHand();
			break;
		}
		default:

			break;
		}
	}

	/**
	 * 
	 * @Description: 展现物品信息
	 */
	private void showItemInfo() {
		for (LinearLayout ll : llList) {
			ll.setVisibility(View.GONE);
		}
		for (ImageView img : imgList) {
			img.setImageResource(R.drawable.arrow_down);
		}
		if (isOpenList.get(0)) {
			isOpenList.set(0, false);
		} else {
			llList.get(0).setVisibility(View.VISIBLE);
			for (int i = 0; i < isOpenList.size(); i++) {
				isOpenList.set(i, false);
			}
			isOpenList.set(0, true);
			imgList.get(0).setImageResource(R.drawable.arrow_up);
		}
	}

	/**
	 * 
	 * @Description: 展现联系信息
	 */
	private void showContactInfo() {
		for (LinearLayout ll : llList) {
			ll.setVisibility(View.GONE);
		}
		for (ImageView img : imgList) {
			img.setImageResource(R.drawable.arrow_down);
		}
		if (isOpenList.get(1)) {
			isOpenList.set(1, false);
		} else {
			llList.get(1).setVisibility(View.VISIBLE);
			for (int i = 0; i < isOpenList.size(); i++) {
				isOpenList.set(i, false);
			}
			isOpenList.set(1, true);
			imgList.get(1).setImageResource(R.drawable.arrow_up);
		}
	}

	String title = "";
	String type = "";
	String price = "";
	String describe = "";
	String bargain = "";
	String qq = "";
	String phone = "";

	/**
	 * 
	 * @Description: 发布闲置物品
	 */
	private void sendSecondHand() {
		title = titleET.getText().toString();
		type = typeSp.getText().toString();
		price = priceET.getText().toString();
		describe = describeET.getText().toString();
		bargain = bargainSp.getText().toString();
		qq = qqET.getText().toString();
		phone = phoneET.getText().toString();
		if (null == title || "".equals(title)) {
			ToastUtils.showToast("请输入标题~");
			openLinearLayoutByNull(false);
			titleET.requestFocus();
			return;
		}
		if (null == type || "".equals(type)) {
			openLinearLayoutByNull(false);
			ToastUtils.showToast("请选择类型~");
			return;
		}
		if (null == price || "".equals(price)) {
			openLinearLayoutByNull(false);
			ToastUtils.showToast("请输入价格~");
			priceET.requestFocus();
			return;
		}
		if (null == describe || "".equals(describe)) {
			openLinearLayoutByNull(false);
			ToastUtils.showToast("请输入详细描述~");
			describeET.requestFocus();
			return;
		}
		if (null == bargain || "".equals(bargain)) {
			openLinearLayoutByNull(true);
			ToastUtils.showToast("请选择可否议价~");
			return;
		}
		if ((null == qq || "".equals(qq))
				&& (null == phone || "".equals(phone))) {
			ToastUtils.showToast("请输入联系qq或号码其中一种~");
			openLinearLayoutByNull(true);
			if ((null == qq || "".equals(qq))) {
				qqET.requestFocus();
			} else {
				phoneET.requestFocus();
			}
			return;
		}
		if (!"".equals(phone) && !VerifyUtils.checkPhone(phone)) {
			ToastUtils.showToast("请输入正确的联系号码格式~");
			openLinearLayoutByNull(true);
			phoneET.requestFocus();
			return;
		}
		send();
	}

	/**
	 * @Description: 打开对应的LinearLayout
	 */
	private void openLinearLayoutByNull(boolean b) {
		if (b) {
			llList.get(0).setVisibility(View.GONE);
			imgList.get(0).setImageResource(R.drawable.arrow_down);
			llList.get(1).setVisibility(View.VISIBLE);
			imgList.get(1).setImageResource(R.drawable.arrow_up);
		} else {
			llList.get(1).setVisibility(View.GONE);
			imgList.get(1).setImageResource(R.drawable.arrow_down);
			llList.get(0).setVisibility(View.VISIBLE);
			imgList.get(0).setImageResource(R.drawable.arrow_up);
		}
	}

	/**
	 * @Description: 发布
	 */
	private void send() {
		dialog.show();
		secondHandInfo.setTitle(title);
		secondHandInfo.setType(type);
		secondHandInfo.setPrice(price);
		secondHandInfo.setDescribe(describe);
		if ("可议价".equals(bargain)) {
			secondHandInfo.setBargain(true);
		} else if ("不可议价".equals(bargain)) {
			secondHandInfo.setBargain(false);
		}
		secondHandInfo.setQq(qq);
		secondHandInfo.setPhone(phone);
		secondHandInfo.setUsername(GlobalParams.userInfo.getUsername());// 发布人信息
		secondHandInfo.setUser(GlobalParams.userInfo);
		// 要上传的图片文件路径
		List<String> filePathList = new ArrayList<String>();
		for (ImageItem item : picList) {
			if ("".equals(item.getImagePath()) || null == item.getImagePath()
					|| !item.getIsLocalPic()) {

			} else {
				filePathList.add(item.getImagePath());
			}
		}
		// List转换成数组
		String[] filePaths = (String[]) filePathList.toArray(new String[0]);
		if (null == filePaths || 1 > filePaths.length) {
			secondHandInfo.setPicFileList(picFileList);
			if (isNew) {
				secondHandInfo.save(this, new SaveListener() {

					@Override
					public void onSuccess() {// 新增发布成功
						LogcatUtils.i("新增闲置物品成功");
						dialog.dismiss();
						setResult(CommonConstant.RESULTCODE_ADD_SECOND_HAND_OK);
						finish();
					}

					@Override
					public void onFailure(int code, String message) {// 发布失败
						LogcatUtils.e("新增闲置物品失败：" + code + " " + message);
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
							ToastUtils.showToast("新增闲置物品失败，请重试~");
							break;
						}
						}
					}
				});
			} else {
				secondHandInfo.update(this, new UpdateListener() {

					@Override
					public void onSuccess() {// 修改发布成功
						LogcatUtils.i("修改闲置物品成功");
						dialog.dismiss();
						setResult(CommonConstant.RESULTCODE_MODIFY_SECOND_HAND_OK);
						finish();
					}

					@Override
					public void onFailure(int code, String message) {// 发布失败
						LogcatUtils.e("修改闲置物品失败：" + code + " " + message);
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
							ToastUtils.showToast("修改失物信息失败，请重试~");
							break;
						}
						}
					}
				});
			}
		} else {
			// 批量上传
			BmobProFile.getInstance(this).uploadBatch(filePaths,
					new UploadBatchListener() {

						@Override
						public void onError(int code, String message) {
							LogcatUtils.e("批量上传图片失败：" + code + " " + message);
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
								ToastUtils.showToast("批量上传图片失败，请重试~");
								break;
							}
							}
						}

						@Override
						public void onSuccess(boolean isFinish,
								String[] fileNames, String[] urls,
								BmobFile[] files) {
							if (isFinish) {
								LogcatUtils.i("批量上传图片成功");
								for (int i = 0; i < files.length; i++) {
									picFileList.add(files[i]);
								}
								secondHandInfo.setPicFileList(picFileList);
								if (isNew) {
									secondHandInfo.save(
											AddSecondHandActivity.this,
											new SaveListener() {

												@Override
												public void onSuccess() {// 新增发布成功
													LogcatUtils.i("新增闲置物品成功");
													dialog.dismiss();
													setResult(CommonConstant.RESULTCODE_ADD_SECOND_HAND_OK);
													finish();
												}

												@Override
												public void onFailure(int code,
														String message) {// 发布失败
													LogcatUtils.e("新增闲置物品失败："
															+ code + " "
															+ message);
													dialog.dismiss();
													switch (code) {
													case 9010: {// 网络超时
														ToastUtils
																.showToast("网络超时，请检查您的手机网络~");
														break;
													}
													case 9016: {// 无网络连接，请检查您的手机网络
														ToastUtils
																.showToast("无网络连接，请检查您的手机网络~");
														break;
													}
													default: {
														ToastUtils
																.showToast("新增失物信息失败，请重试~");
														break;
													}
													}
												}
											});
								} else {
									secondHandInfo.update(
											AddSecondHandActivity.this,
											new UpdateListener() {

												@Override
												public void onSuccess() {// 修改发布成功
													LogcatUtils.i("修改闲置物品成功");
													dialog.dismiss();
													setResult(CommonConstant.RESULTCODE_MODIFY_SECOND_HAND_OK);
													finish();
												}

												@Override
												public void onFailure(int code,
														String message) {// 发布失败
													LogcatUtils.e("修改闲置物品失败："
															+ code + " "
															+ message);
													dialog.dismiss();
													switch (code) {
													case 9010: {// 网络超时
														ToastUtils
																.showToast("网络超时，请检查您的手机网络~");
														break;
													}
													case 9016: {// 无网络连接，请检查您的手机网络
														ToastUtils
																.showToast("无网络连接，请检查您的手机网络~");
														break;
													}
													default: {
														ToastUtils
																.showToast("修改失物信息失败，请重试~");
														break;
													}
													}
												}
											});
								}
							}
						}

						@Override
						public void onProgress(int curIndex, int curPercent,
								int total, int totalPercent) {

						}
					});
		}

	}

	/**
	 * @Description: 图片列表按键监听
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long arg3) {
		if (position == picList.size()) {
			showAlbumDialog();// 打开菜单选择图片来源
		} else {
			String[] imagePaths = new String[picList.size()];
			for (int i = 0; i < picList.size(); i++) {
				if (picList.get(i).getIsLocalPic()) {
					imagePaths[i] = "file://" + picList.get(i).getImagePath();
				} else {
					imagePaths[i] = picList.get(i).getImagePath();
				}
			}
			loadLocalImage(position, imagePaths);
		}
	}

	/**
	 * 
	 * @Description: 加载本地的图片
	 * @param position
	 * @param imagePath
	 */
	private void loadLocalImage(int position, String[] imagePaths) {
		Intent intent = new Intent(AddSecondHandActivity.this,
				ImagePagerActivity.class);
		intent.putExtra(ImagePagerActivity.IMAGE_URLS, imagePaths);
		intent.putExtra(ImagePagerActivity.IMAGE_INDEX, position);
		startActivity(intent);
	}

	/**
	 * 
	 * @Description: 弹框选择图片来源
	 */
	@SuppressLint("InflateParams")
	private void showAlbumDialog() {
		albumDialog = new AlertDialog.Builder(this).create();
		albumDialog.setCanceledOnTouchOutside(true);
		View v = LayoutInflater.from(this).inflate(
				R.layout.dialog_select_picture, null);
		albumDialog.show();
		albumDialog.setContentView(v);
		albumDialog.getWindow().setGravity(Gravity.CENTER);

		TextView albumPic = (TextView) v.findViewById(R.id.album_pic);
		TextView cameraPic = (TextView) v.findViewById(R.id.camera_pic);
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
				getPicFromAlbum();
			}
		});
		cameraPic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				albumDialog.dismiss();
				getPicFromCamera();
			}
		});
	}

	/**
	 * 
	 * @Description: 从相册获取
	 */
	private void getPicFromAlbum() {
		// 激活系统图库，选择一张图片
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, CommonConstant.REQUESTCODE_PHOTO_ALBUM);
	}

	/**
	 * 
	 * @Description: 从拍照获取
	 */
	private void getPicFromCamera() {
		imagePath = FileUtils.getImagePath();
		File f = new File(imagePath);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			// 访问相册
			if (requestCode == CommonConstant.REQUESTCODE_PHOTO_ALBUM) {
				String fileName = null;
				if (data != null) {
					// 得到图片的全路径
					Uri uri = data.getData();
					ContentResolver cr = getContentResolver();
					Cursor cursor = cr.query(uri, null, null, null, null);
					if (cursor.moveToFirst()) {
						do {
							fileName = cursor.getString(cursor
									.getColumnIndex("_data"));
						} while (cursor.moveToNext());
					}
					tempFile = new File(fileName);
					if (tempFile.exists() && tempFile.length() > 0) {
						ImageItem albumPic = new ImageItem();
						albumPic.setIsLocalPic(true);
						albumPic.setImagePath(fileName);
						picList.add(albumPic);
						editGridAdapter.notifyDataSetChanged();
					}
				}

			}
			// 访问相机
			if (requestCode == CommonConstant.REQUESTCODE_PHOTO_CAMERA) {
				if (FileUtils.hasSdcard()) {
					Bitmap bitmap = ImageUtils.scale(
							BitmapFactory.decodeFile(imagePath), 0.7F, 0.7F);
					if (null == imagePath || "".equals(imagePath)) {

					} else {
						tempFile = new File(imagePath);
						if (tempFile.exists() && tempFile.length() > 0) {
							// 判断照片是否被旋转，如果旋转则旋转回0°
							int degree = ImageUtils.getBitmapDegree(imagePath);
							if (degree != 0) {
								bitmap = ImageUtils.rotateBitmapByDegree(
										bitmap, degree);
							}
							ImageUtils.toFile(bitmap, tempFile);
							ImageItem takePhoto = new ImageItem();
							takePhoto.setIsLocalPic(true);
							takePhoto.setImagePath(imagePath);
							picList.add(takePhoto);
							editGridAdapter.notifyDataSetChanged();
						} else {
						}
					}

				} else {
					ToastUtils.showToast("未找到存储卡，无法存储照片！");
				}
			}

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * @Description: 列表长按监听
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View view,
			int position, long arg3) {
		if (position == picList.size()) {
			return false;
		} else {
			showDeleteDialog(position);
			return true;
		}
	}

	/**
	 * @Description: 弹出是否删除提示
	 */
	private void showDeleteDialog(final int position) {
		new SweetAlertDialog(AddSecondHandActivity.this,
				SweetAlertDialog.WARNING_TYPE)
				.setTitleText("你确定删除该图片吗？")
				.setCancelText("取消")
				.setConfirmText("删除")
				.showCancelButton(true)
				.setCancelClickListener(null)
				.setConfirmClickListener(
						new SweetAlertDialog.OnSweetClickListener() {

							@Override
							public void onClick(

							SweetAlertDialog sweetAlertDialog) {// 删除图片
								deletePic(position);
								editGridAdapter.notifyDataSetChanged();
								sweetAlertDialog.dismiss();
							}

						}).show();
	}

	/**
	 * 
	 * @Description: 删除图片
	 */
	private void deletePic(int position) {
		ImageItem item = picList.get(position);
		if (item.getIsLocalPic()) {
			picList.remove(position);
		} else {
			picFileList.remove(position);
			picList.remove(position);
		}

	}

}
