package com.yzx.lifeassistants.activity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
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

import com.balysv.materialripple.MaterialRippleLayout;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadBatchListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.METValidator;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.UMImage;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;
import com.yzx.lifeassistants.GlobalParams;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.adapter.EditGridAdapter;
import com.yzx.lifeassistants.adapter.MaterialSimpleListAdapter;
import com.yzx.lifeassistants.adapter.MyArrayAdapter;
import com.yzx.lifeassistants.adapter.UnEditGridAdapter;
import com.yzx.lifeassistants.base.BaseActivity;
import com.yzx.lifeassistants.bean.FindThing;
import com.yzx.lifeassistants.bean.ImageItem;
import com.yzx.lifeassistants.bean.LostThing;
import com.yzx.lifeassistants.bean.ShareListItem;
import com.yzx.lifeassistants.common.CommonConstant;
import com.yzx.lifeassistants.utils.AppUtils;
import com.yzx.lifeassistants.utils.DensityUtils;
import com.yzx.lifeassistants.utils.FileUtils;
import com.yzx.lifeassistants.utils.ImageUtils;
import com.yzx.lifeassistants.utils.LogcatUtils;
import com.yzx.lifeassistants.utils.ScreenUtils;
import com.yzx.lifeassistants.utils.ToastUtils;
import com.yzx.lifeassistants.utils.VerifyUtils;
import com.yzx.lifeassistants.view.widget.CircularLoadingDialog;
import com.yzx.lifeassistants.view.widget.NoScrollGridView;

/**
 * @Description: 失物招领
 * @author: yzx
 * @time: 2015-9-15 下午5:14:22
 */
@SuppressLint({ "SimpleDateFormat", "ClickableViewAccessibility" })
public class LostAndFindActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener, OnItemLongClickListener {
	private String from;//
	private TextView topTitleTV;// 顶部标题
	private ImageButton topBackIB;// 顶部返回按钮
	private ImageButton topShareBtn;// 顶部分享按钮
	private ImageButton topSendBtn;// 顶部发送按钮(仅在新建出现)
	private String lostId;// 丢失物品id
	private String findId;// 招领物品id
	private List<ImageView> imgList;//
	private List<LinearLayout> llList;//
	private List<Boolean> isOpenList;//
	private boolean isNew;// 判断是否是新增
	private CircularLoadingDialog dialog;
	private Handler shareHandler;// 分享
	private RelativeLayout topLayout;// 分享界面头部
	private LinearLayout itemLayout;// 分享物品信息
	private LinearLayout otherLayout;// 分享其他信息
	private RelativeLayout itemsRL;// 物品信息
	private LinearLayout itemsLL;// 物品信息
	private RelativeLayout otherRL;// 其他信息
	private LinearLayout otherLL;// 其他信息
	private ImageView itemsImg;// 向上或向下箭头
	private ImageView otherImg;// 向上或向下箭头
	private MaterialEditText titleET;// 物品标题
	private MaterialBetterSpinner typeSp;// 物品类型
	private MaterialEditText describeET;// 物品描述
	private MaterialEditText placeET;// 拾取/丢失地点
	private MaterialEditText timeET;// 拾取/丢失时间
	private MaterialEditText phoneET;// 联系号码
	private Button deleteBtn;// 删除按钮(仅在修改出现)
	private Button submitBtn;// 提交按钮(仅在修改出现)
	private List<ImageItem> picList;// 图片列表信息
	private NoScrollGridView picGV;// 图片列表

	private LostThing lostInfo;// 失物信息
	private FindThing findInfo;// 招领信息
	private List<BmobFile> picFileList;// 图片文件列表
	private Boolean unEdit;// 图片是否不可编辑
	private EditGridAdapter editAdapter;// 图片列表可编辑的适配器
	private UnEditGridAdapter unEditAdapter;// 图片列表不可编辑的适配器
	private AlertDialog albumDialog;// 选择图片来源
	private File tempFile;// 图片缓存
	private String imagePath;// 图片路径
	private SimpleDateFormat mFormatter = new SimpleDateFormat(
			"yyyy-MM-dd aa hh:mm");// 日期时间格式
	// //////////QQ,QZone使用友盟分享,其余使用ShareSDK分享//////////////
	private ShareParams shareParams;// 分享内容
	private PlatformActionListener listener;// 分享回调监听
	private Bitmap bitmap;// 分享图片
	private String shareImagePath;// 分享图片路径
	private UMSocialService mShareController;// 友盟分享控制器
	private SnsPostListener snsPostListener;// 友盟分享回调监听

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lost_find);
		init();
		initView();
		initData();
		initMaterialRipple();
	}

	/**
	 * 
	 * @Description: 初始化上个界面传来的值
	 */
	private void init() {
		from = getIntent().getStringExtra("from");
	}

	/**
	 * 
	 * @Description: 初始化控件
	 */
	private void initView() {
		dialog = new CircularLoadingDialog(this);
		topLayout = (RelativeLayout) findViewById(R.id.back_head);
		itemsRL = (RelativeLayout) findViewById(R.id.lost_find_items_title_rl);
		itemLayout = (LinearLayout) findViewById(R.id.new_lost_find_items);
		otherLayout = (LinearLayout) findViewById(R.id.new_lost_find_other);
		otherRL = (RelativeLayout) findViewById(R.id.lost_find_other_title_rl);
		itemsLL = (LinearLayout) findViewById(R.id.lost_find_items_ll);
		otherLL = (LinearLayout) findViewById(R.id.lost_find_other_ll);
		itemsImg = (ImageView) findViewById(R.id.lost_find_items_title_arrow_img);
		otherImg = (ImageView) findViewById(R.id.lost_find_other_title_arrow_img);
		topBackIB = (ImageButton) findViewById(R.id.top_back_btn);
		topBackIB.setOnClickListener(this);
		topSendBtn = (ImageButton) findViewById(R.id.top_send_btn);
		topSendBtn.setOnClickListener(this);
		topShareBtn = (ImageButton) findViewById(R.id.top_share_btn);
		topShareBtn.setVisibility(View.VISIBLE);
		topShareBtn.setOnClickListener(this);

		topTitleTV = (TextView) findViewById(R.id.top_title_tv);
		titleET = (MaterialEditText) findViewById(R.id.lost_find_items_title_edit);
		typeSp = (MaterialBetterSpinner) findViewById(R.id.lost_find_items_type_spinner);
		describeET = (MaterialEditText) findViewById(R.id.lost_find_items_describe_edit);
		placeET = (MaterialEditText) findViewById(R.id.lost_find_other_place_edit);
		timeET = (MaterialEditText) findViewById(R.id.lost_find_other_time_edit);
		// 弹出日期时间选择
		timeET.setOnTouchListener(new OnTouchListener() {
			int touch_flag = 0;

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (v.getId()) {
				case R.id.lost_find_other_time_edit: {
					touch_flag++;
					if (touch_flag == 4) {
						pickDateTime();
						touch_flag = 0;
					}
					break;
				}
				default:
					break;
				}
				return false;
			}
		});
		phoneET = (MaterialEditText) findViewById(R.id.lost_find_other_phone_edit);
		deleteBtn = (Button) findViewById(R.id.lost_find_delete_btn);
		deleteBtn.setOnClickListener(this);
		submitBtn = (Button) findViewById(R.id.lost_find_submit_btn);
		submitBtn.setOnClickListener(this);
		picGV = (NoScrollGridView) findViewById(R.id.lost_find_items_pic_gv);
		picGV.setOnItemClickListener(this);
		picGV.setOnItemLongClickListener(this);
	}

	/**
	 * 
	 * @Description: 初始化数据
	 */
	@SuppressLint("HandlerLeak")
	private void initData() {
		// 默认打开时候第一个选项为张开
		itemsLL.setVisibility(View.VISIBLE);
		itemsImg.setImageResource(R.drawable.arrow_up);
		llList = new ArrayList<LinearLayout>();
		llList.add(itemsLL);
		llList.add(otherLL);
		imgList = new ArrayList<ImageView>();
		imgList.add(itemsImg);
		imgList.add(otherImg);
		isOpenList = new ArrayList<Boolean>();
		isOpenList.add(true);
		isOpenList.add(false);
		picList = new ArrayList<ImageItem>();
		picFileList = new ArrayList<BmobFile>();
		if (from.contains("LOST")) {
			topTitleTV.setText(R.string.lost_find_lost);
			lostInfo = new LostThing();
		}
		if (from.contains("FIND")) {
			topTitleTV.setText(R.string.lost_find_find);
			findInfo = new FindThing();
		}
		if (CommonConstant.FROM_LOSTFRAGMENT.equals(from)) {// check
			isNew = false;
			initLostData();
		}
		if (CommonConstant.FROM_FINDFRAGMENT.equals(from)) {// check
			isNew = false;
			initFindData();
		}
		if (CommonConstant.FROM_LOSTANDFRAGMENT.equals(from)) {// add
			isNew = true;

		}
		if (CommonConstant.FROM_ANDFINDFRAGMENT.equals(from)) {// add
			isNew = true;
		}
		if (isNew) {// 新建
			deleteBtn.setVisibility(View.GONE);
			submitBtn.setVisibility(View.GONE);
			topShareBtn.setVisibility(View.GONE);
			topSendBtn.setVisibility(View.VISIBLE);
			unEdit = false;
			editAdapter = new EditGridAdapter(LostAndFindActivity.this, picList);
			picGV.setAdapter(editAdapter);
			editAdapter.notifyDataSetChanged();
		} else {
			topSendBtn.setVisibility(View.GONE);
			topShareBtn.setVisibility(View.VISIBLE);
		}
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
		shareHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 12345: {
					showShareDialog();
					break;
				}
				}
			}
		};

	}

	/**
	 * 
	 * @Description: 初始化拾取数据
	 */
	private void initFindData() {
		FindThing findInfo = (FindThing) getIntent().getSerializableExtra(
				"findInfo");
		if (findInfo == null) {
			isNew = true;
			deleteBtn.setVisibility(View.GONE);
			submitBtn.setVisibility(View.GONE);
			topShareBtn.setVisibility(View.GONE);
			topSendBtn.setVisibility(View.VISIBLE);
		} else {
			findId = findInfo.getObjectId();
			titleET.setText(findInfo.getTitle());
			typeSp.setText(findInfo.getType());
			// setSpinnerItemSelectedByValue(typeSp, findInfo.getType());
			describeET.setText(findInfo.getDescribe());
			placeET.setText(findInfo.getPlace());
			timeET.setText(findInfo.getTime());
			phoneET.setText(findInfo.getPhone());
			picFileList = findInfo.getPicFileList();
			if (null == picFileList || 1 > picFileList.size()) {

			} else {
				for (BmobFile bmobFile : picFileList) {
					if (bmobFile != null) {
						ImageItem imageItem = new ImageItem();
						imageItem.setIsLocalPic(false);
						imageItem.setFileName(bmobFile.getFilename());
						imageItem.setImagePath(bmobFile
								.getFileUrl(LostAndFindActivity.this));
						picList.add(imageItem);
					}
				}
			}
			if (findInfo.getUsername().equals(
					GlobalParams.userInfo.getUsername())) {// 本人发布的可修改/删除
				deleteBtn.setVisibility(View.VISIBLE);
				submitBtn.setVisibility(View.VISIBLE);
				unEdit = false;
				editAdapter = new EditGridAdapter(LostAndFindActivity.this,
						picList);
				picGV.setAdapter(editAdapter);
				editAdapter.notifyDataSetChanged();
			} else {// 非本人发布的不可修改/删除
				unEdit();
				unEdit = true;
				if (null == picList || 1 > picList.size()) {// 若无图片信息隐藏图片信息该栏
					picGV.setVisibility(View.GONE);
				} else {
					unEditAdapter = new UnEditGridAdapter(// 若有图片信息显示图片信息该栏
							LostAndFindActivity.this, picList);
					picGV.setAdapter(unEditAdapter);
					unEditAdapter.notifyDataSetChanged();
				}
			}

		}
	}

	/**
	 * 
	 * @Description: 初始化丢失数据
	 */
	private void initLostData() {
		LostThing lostInfo = (LostThing) getIntent().getSerializableExtra(
				"lostInfo");
		if (lostInfo == null) {
			isNew = true;
			deleteBtn.setVisibility(View.GONE);
			submitBtn.setVisibility(View.GONE);
			topShareBtn.setVisibility(View.GONE);
			topSendBtn.setVisibility(View.VISIBLE);
		} else {
			lostId = lostInfo.getObjectId();
			titleET.setText(lostInfo.getTitle());
			typeSp.setText(lostInfo.getType());
			describeET.setText(lostInfo.getDescribe());
			placeET.setText(lostInfo.getPlace());
			timeET.setText(lostInfo.getTime());
			phoneET.setText(lostInfo.getPhone());
			picFileList = lostInfo.getPicFileList();
			if (null == picFileList || 1 > picFileList.size()) {

			} else {
				for (BmobFile bmobFile : picFileList) {
					if (bmobFile != null) {
						ImageItem imageItem = new ImageItem();
						imageItem.setIsLocalPic(false);
						imageItem.setFileName(bmobFile.getFilename());
						imageItem.setImagePath(bmobFile
								.getFileUrl(LostAndFindActivity.this));
						picList.add(imageItem);
					}
				}
			}
			if (lostInfo.getUsername().equals(
					GlobalParams.userInfo.getUsername())) {// 本人发布的可修改/删除
				deleteBtn.setVisibility(View.VISIBLE);
				submitBtn.setVisibility(View.VISIBLE);
				unEdit = false;
				editAdapter = new EditGridAdapter(LostAndFindActivity.this,
						picList);
				picGV.setAdapter(editAdapter);
				editAdapter.notifyDataSetChanged();
			} else {// 非本人发布的不可修改/删除
				unEdit();
				unEdit = true;
				if (null == picList || 1 > picList.size()) {// 若无图片信息隐藏图片信息该栏
					picGV.setVisibility(View.GONE);
				} else {
					unEditAdapter = new UnEditGridAdapter(// 若有图片信息显示图片信息该栏
							LostAndFindActivity.this, picList);
					picGV.setAdapter(unEditAdapter);
					unEditAdapter.notifyDataSetChanged();
				}
			}
		}
	}

	/**
	 * 
	 * @Description: 瓷砖特效
	 */
	private void initMaterialRipple() {
		// 设置Spinner的Adapter
		if (isNew || !unEdit) {
			String[] list = getResources().getStringArray(
					R.array.lost_find_type);
			List<String> dataList = Arrays.asList(list);
			MyArrayAdapter adapter = new MyArrayAdapter(this, dataList);
			typeSp.setAdapter(adapter);
			describeET.setSingleLine(true);
			describeET.setSingleLineEllipsis();
		}
		// 设置Padding
		titleET.setPaddings(DensityUtils.sp2px(this, 32), 0, 0, 0);
		typeSp.setPaddings(DensityUtils.sp2px(this, 32), 0, 0, 0);
		describeET.setPaddings(DensityUtils.sp2px(this, 32), 0, 0, 0);
		placeET.setPaddings(DensityUtils.sp2px(this, 32), 0, 0, 0);
		timeET.setPaddings(DensityUtils.sp2px(this, 32), 0, 0, 0);
		phoneET.setPaddings(DensityUtils.sp2px(this, 32), 0, 0, 0);
		// 设置验证信息
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

		List<View> views = new ArrayList<View>();
		views.add(itemsRL);
		views.add(otherRL);
		if (isNew) {

		} else if (unEdit) {

		} else {
			views.add(deleteBtn);
		}
		views.add(submitBtn);
		for (View view : views) {
			// 动态特效
			MaterialRippleLayout
					.on(view)
					.rippleColor(
							Color.parseColor(CommonConstant.RIPPLE_COLOR_LITHT))
					.rippleAlpha(CommonConstant.RIPPLE_ALPHA).rippleHover(true)
					.create();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_back_btn: {// 返回按钮
			finish();
			break;
		}
		case R.id.top_send_btn: {// 新建发送按钮
			dialog.show();
			addByType();
			break;
		}
		case R.id.lost_find_submit_btn: {// 修改提交按钮
			dialog.show();
			addByType();
			break;
		}
		case R.id.lost_find_delete_btn: {// 删除按钮
			dialog.show();
			deleteByType();
			break;
		}
		case R.id.top_share_btn: {// 分享按钮
			for (LinearLayout layout : llList) {
				layout.setVisibility(View.VISIBLE);
			}
			for (ImageView image : imgList) {
				image.setImageResource(R.drawable.arrow_up);
			}
			for (Boolean isOpen : isOpenList) {
				if (!isOpen) {
					isOpen = true;
				}
			}
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						// 延时等待物品信息和其他信息都展开
						Thread.sleep(800);
						Message msg = new Message();
						msg.what = 12345;
						shareHandler.sendMessage(msg);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
			break;
		}
		default:
			break;
		}
	}

	/**
	 * 
	 * @Description: 选择时间
	 */
	private void pickDateTime() {
		new SlideDateTimePicker.Builder(getSupportFragmentManager())
				.setListener(new SlideDateTimeListener() {

					@Override
					public void onDateTimeSet(Date date) {// 选择时间
						timeET.setText(mFormatter.format(date));
					}
				})
				.setInitialDate(new Date())
				.setMaxDate(new Date())
				.setIndicatorColor(
						getResources().getColor(R.color.actionbar_color))
				.build().show();
	}

	String title = "";
	String type = "";
	String describe = "";
	String place = "";
	String time = "";
	String phone = "";

	/**
	 * @Description: 根据类型添加失物/招领信息
	 */
	private void addByType() {

		title = titleET.getText().toString();
		type = typeSp.getText().toString();
		describe = describeET.getText().toString();
		place = placeET.getText().toString();
		time = timeET.getText().toString();
		phone = phoneET.getText().toString();
		if (null == title || "".equals(title)) {
			dialog.dismiss();
			ToastUtils.showToast("请输入标题~");
			openLinearLayoutByNull(false);
			titleET.requestFocus();
			return;
		}
		if (null == type || "".equals(type)) {
			dialog.dismiss();
			openLinearLayoutByNull(false);
			ToastUtils.showToast("请输入类型~");
			return;
		}
		if (null == describe || "".equals(describe)) {
			dialog.dismiss();
			openLinearLayoutByNull(false);
			ToastUtils.showToast("请输入详细描述~");
			describeET.requestFocus();
			return;
		}
		if (null == place || "".equals(place)) {
			dialog.dismiss();
			openLinearLayoutByNull(true);
			ToastUtils.showToast("请输入地点~");
			placeET.requestFocus();
			return;
		}
		if (null == time || "".equals(time)) {
			dialog.dismiss();
			ToastUtils.showToast("请输入时间~");
			openLinearLayoutByNull(true);
			timeET.requestFocus();
			return;
		}
		if (null == phone || "".equals(phone)) {
			dialog.dismiss();
			ToastUtils.showToast("请输入联系号码~");
			openLinearLayoutByNull(true);
			phoneET.requestFocus();
			return;
		}
		if (!VerifyUtils.checkPhone(phone)) {
			dialog.dismiss();
			ToastUtils.showToast("请输入正确的联系号码格式~");
			openLinearLayoutByNull(true);
			phoneET.requestFocus();
			return;
		}
		if (from.contains("LOST")) {
			addLost();
		} else if (from.contains("FIND")) {
			addFind();
		}
	}

	/**
	 * @Description: 添加失物信息
	 */
	private void addLost() {
		lostInfo.setTitle(title);
		lostInfo.setType(type);
		lostInfo.setDescribe(describe);
		lostInfo.setPlace(place);
		lostInfo.setTime(time);
		lostInfo.setPhone(phone);
		lostInfo.setUsername(GlobalParams.userInfo.getUsername());// 发布人信息
		lostInfo.setUser(GlobalParams.userInfo);
		List<String> filePathList = new ArrayList<String>();
		for (ImageItem item : picList) {
			if ("".equals(item.getImagePath()) || null == item.getImagePath()
					|| !item.getIsLocalPic()) {

			} else {
				filePathList.add(item.getImagePath());
			}
		}
		String[] filePaths = (String[]) filePathList.toArray(new String[0]);
		if (null == filePaths || 1 > filePaths.length) {
			lostInfo.setPicFileList(picFileList);
			if (isNew) {

				lostInfo.save(LostAndFindActivity.this, new SaveListener() {

					@Override
					public void onSuccess() {// 添加成功
						LogcatUtils.i("新增失物信息成功");
						dialog.dismiss();
						setResult(CommonConstant.RESULTCODE_NEW_LOST_OK);
						finish();
					}

					@Override
					public void onFailure(int code, String message) {
						LogcatUtils.e("新增失物信息失败：" + code + " " + message);
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
							ToastUtils.showToast("新增失物信息失败，请重试~");
							break;
						}
						}
					}
				});
			} else {
				lostInfo.update(LostAndFindActivity.this, lostId,
						new UpdateListener() {

							@Override
							public void onSuccess() {// 修改成功
								LogcatUtils.i("修改失物信息成功");
								dialog.dismiss();
								setResult(CommonConstant.RESULTCODE_UPDATE_LOST_OK);
								finish();
							}

							@Override
							public void onFailure(int code, String message) {
								LogcatUtils.e("修改失物信息失败：" + code + " "
										+ message);
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
			BmobProFile.getInstance(LostAndFindActivity.this).uploadBatch(
					filePaths, new UploadBatchListener() {

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
								lostInfo.setPicFileList(picFileList);
								if (isNew) {

									lostInfo.save(LostAndFindActivity.this,
											new SaveListener() {

												@Override
												public void onSuccess() {// 添加成功
													LogcatUtils.i("新增失物信息成功");
													dialog.dismiss();
													setResult(CommonConstant.RESULTCODE_NEW_LOST_OK);
													finish();
												}

												@Override
												public void onFailure(int code,
														String message) {
													LogcatUtils.e("新增失物信息失败："
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
									lostInfo.update(LostAndFindActivity.this,
											lostId, new UpdateListener() {

												@Override
												public void onSuccess() {// 修改成功
													LogcatUtils.i("修改失物信息成功");
													dialog.dismiss();
													setResult(CommonConstant.RESULTCODE_UPDATE_LOST_OK);
													finish();
												}

												@Override
												public void onFailure(int code,
														String message) {
													LogcatUtils.e("修改失物信息失败："
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
	 * @Description: 添加招领信息
	 */
	private void addFind() {

		findInfo.setTitle(title);
		findInfo.setType(type);
		findInfo.setDescribe(describe);
		findInfo.setPlace(place);
		findInfo.setTime(time);
		findInfo.setPhone(phone);
		findInfo.setUsername(GlobalParams.userInfo.getUsername());// 发布人信息
		findInfo.setUser(GlobalParams.userInfo);
		List<String> filePathList = new ArrayList<String>();
		for (ImageItem item : picList) {
			if ("".equals(item.getImagePath()) || null == item.getImagePath()
					|| !item.getIsLocalPic()) {

			} else {
				filePathList.add(item.getImagePath());
			}
		}
		String[] filePaths = (String[]) filePathList.toArray(new String[0]);
		if (null == filePaths || 1 > filePaths.length) {
			findInfo.setPicFileList(picFileList);
			if (isNew) {

				findInfo.save(LostAndFindActivity.this, new SaveListener() {

					@Override
					public void onSuccess() {// 添加成功
						LogcatUtils.i("新增招领信息成功");
						dialog.dismiss();
						setResult(CommonConstant.RESULTCODE_NEW_FIND_OK);
						finish();
					}

					@Override
					public void onFailure(int code, String message) {
						LogcatUtils.e("新增招领信息失败：" + code + " " + message);
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
							ToastUtils.showToast("新增招领信息失败，请重试~");
							break;
						}
						}
					}
				});
			} else {
				findInfo.update(LostAndFindActivity.this, findId,
						new UpdateListener() {

							@Override
							public void onSuccess() {// 修改成功
								LogcatUtils.i("修改招领信息成功");
								dialog.dismiss();
								setResult(CommonConstant.RESULTCODE_UPDATE_FIND_OK);
								finish();
							}

							@Override
							public void onFailure(int code, String message) {
								LogcatUtils.e("修改招领信息失败：" + code + " "
										+ message);
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
									ToastUtils.showToast("修改招领信息失败，请重试~");
									break;
								}
								}
							}
						});
			}
		} else {
			// 批量上传
			BmobProFile.getInstance(LostAndFindActivity.this).uploadBatch(
					filePaths, new UploadBatchListener() {

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
								findInfo.setPicFileList(picFileList);
								if (isNew) {

									findInfo.save(LostAndFindActivity.this,
											new SaveListener() {

												@Override
												public void onSuccess() {// 添加成功
													LogcatUtils.i("新增招领信息成功");
													dialog.dismiss();
													setResult(CommonConstant.RESULTCODE_NEW_FIND_OK);
													finish();
												}

												@Override
												public void onFailure(int code,
														String message) {
													LogcatUtils.e("新增招领信息失败："
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
																.showToast("新增招领信息失败，请重试~");
														break;
													}
													}
												}
											});
								} else {
									findInfo.update(LostAndFindActivity.this,
											findId, new UpdateListener() {

												@Override
												public void onSuccess() {// 修改成功
													LogcatUtils.i("修改招领信息成功");
													dialog.dismiss();
													setResult(CommonConstant.RESULTCODE_UPDATE_FIND_OK);
													finish();
												}

												@Override
												public void onFailure(int code,
														String message) {
													LogcatUtils.e("修改招领信息失败："
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
																.showToast("修改招领信息失败，请重试~");
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
	 * @Description: 根据类型删除失物/招领信息
	 */
	private void deleteByType() {
		if (CommonConstant.FROM_LOSTFRAGMENT.equals(from)) {
			deleteLost();
		} else if (CommonConstant.FROM_FINDFRAGMENT.equals(from)) {
			deleteFind();
		}
	}

	/**
	 * 
	 * @Description: 删除失物信息
	 */
	private void deleteLost() {
		LostThing lostInfo = new LostThing();
		lostInfo.setObjectId(lostId);
		lostInfo.delete(this, new DeleteListener() {

			@Override
			public void onSuccess() {// 删除成功
				LogcatUtils.i("删除失物信息成功");
				dialog.dismiss();
				setResult(CommonConstant.RESULTCODE_DELETE_LOST_OK);
				finish();

			}

			@Override
			public void onFailure(int code, String message) {
				LogcatUtils.e("删除失物信息失败：" + code + " " + message);
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
					ToastUtils.showToast("删除失物信息失败，请重试~");
					break;
				}
				}

			}
		});
	}

	/**
	 * 
	 * @Description: 删除招领信息
	 */
	private void deleteFind() {
		FindThing findInfo = new FindThing();
		findInfo.setObjectId(findId);
		findInfo.delete(this, new DeleteListener() {

			@Override
			public void onSuccess() {// 删除成功
				LogcatUtils.i("删除招领信息成功");
				dialog.dismiss();
				setResult(CommonConstant.RESULTCODE_DELETE_FIND_OK);
				finish();
			}

			@Override
			public void onFailure(int code, String message) {
				LogcatUtils.e("删除招领信息失败：" + code + " " + message);
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
					ToastUtils.showToast("删除招领信息失败，请重试~");
					break;
				}
				}
			}
		});
	}

	/**
	 * @Description:控件设置不可编辑
	 */
	private void unEdit() {
		titleET.setEnabled(false);
		typeSp.setEnabled(false);
		typeSp.setFocusable(false);
		typeSp.setFocusableInTouchMode(false);
		describeET.setSingleLineEllipsis(false);
		describeET.setShowClearButton(false);
		describeET.setEnabled(false);
		placeET.setEnabled(false);
		timeET.setEnabled(false);
		phoneET.setEnabled(false);
		deleteBtn.setVisibility(View.GONE);
		submitBtn.setVisibility(View.GONE);
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
	 * @Description: 按键监听更改布局
	 */
	public void newLostOrFindClick(View v) {
		for (LinearLayout ll : llList) {
			ll.setVisibility(View.GONE);
		}
		for (ImageView img : imgList) {
			img.setImageResource(R.drawable.arrow_down);
		}
		switch (v.getId()) {
		case R.id.lost_find_items_title_rl: {// 物品信息
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
			break;
		}
		case R.id.lost_find_other_title_rl: {// 其他信息
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
			break;
		}
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
		Intent intent = new Intent(LostAndFindActivity.this,
				ImagePagerActivity.class);
		intent.putExtra(ImagePagerActivity.IMAGE_URLS, imagePaths);
		intent.putExtra(ImagePagerActivity.IMAGE_INDEX, position);
		startActivity(intent);
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
						editAdapter.notifyDataSetChanged();
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
							editAdapter.notifyDataSetChanged();
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
		if (unEdit) {
			return false;
		} else {
			if (position == picList.size()) {
				return false;
			} else {
				showDeleteDialog(position);
				return true;
			}
		}

	}

	/**
	 * @Description: 弹出是否删除提示
	 */
	private void showDeleteDialog(final int position) {
		new SweetAlertDialog(LostAndFindActivity.this,
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
								editAdapter.notifyDataSetChanged();
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

	// /////////////////////////分享////////////////////////////////
	/**
	 * 
	 * @Description: 弹出推荐分享框
	 */
	@SuppressLint("NewApi")
	private void showShareDialog() {
		// 获取图片
		Bitmap topBitmap = ScreenUtils.getViewBitmap(topLayout);
		Bitmap itemsBitmap = ScreenUtils.getViewBitmap(itemLayout);
		Bitmap otherBitmap = ScreenUtils.getViewBitmap(otherLayout);
		if (null == topBitmap || null == itemsBitmap || null == otherBitmap) {

		} else {
			List<Bitmap> bitmaps = new ArrayList<Bitmap>();
			bitmaps.add(topBitmap);
			bitmaps.add(itemsBitmap);
			bitmaps.add(otherBitmap);
			// 图片拼接
			bitmap = ScreenUtils.addManyBitmap(bitmaps);
			if (null != bitmap) {
				shareImagePath = FileUtils.getImagePath();
				if (null != shareImagePath) {
					FileUtils.saveBitmap(bitmap, shareImagePath);
					if (FileUtils.isFilePathExist(shareImagePath)) {
						shareParams.setImagePath(shareImagePath);
					}
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
		// 使用友盟分享 QQ图文分享(QQ不支持无客户端纯图片分享)
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
