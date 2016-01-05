package com.yzx.lifeassistants.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.balysv.materialripple.MaterialRippleLayout;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.METValidator;
import com.squareup.timessquare.CalendarPickerView;
import com.yzx.lifeassistants.GlobalParams;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.base.BaseActivity;
import com.yzx.lifeassistants.bean.ExpendInfo;
import com.yzx.lifeassistants.common.CommonConstant;
import com.yzx.lifeassistants.utils.CalendarUtil;
import com.yzx.lifeassistants.utils.DensityUtils;
import com.yzx.lifeassistants.utils.LogcatUtils;
import com.yzx.lifeassistants.utils.ToastUtils;
import com.yzx.lifeassistants.utils.VerifyUtils;
import com.yzx.lifeassistants.view.widget.CircularLoadingDialog;

/**
 * @Description: 添加支出记录
 * @author: yzx
 * @time: 2015-11-30 下午3:13:17
 */
@SuppressLint({ "ClickableViewAccessibility", "InflateParams", "InlinedApi" })
public class AddExpendActivity extends BaseActivity implements OnClickListener {
	private Integer balanceLevel;// 余额等级

	private RelativeLayout headRL;// 头部布局
	private ImageButton backBtn;// 返回按钮
	private ImageButton saveBtn;// 日历按钮
	private RadioButton studyRB;// 学习
	private RadioButton shoppingRB;// 购物
	private RadioButton entertainmentRB;// 娱乐
	private RadioButton medicalRB;// 医疗
	private RadioButton trafficRB;// 交通
	private RadioButton eatRB;// 饮食
	private RadioButton loverRB;// 爱人
	private RadioButton otherRB;// 其他

	private TextView checkedTV;// 选中类型
	private MaterialEditText moneyET;// 金额输入框
	private MaterialEditText dateET;// 日期输入框
	private MaterialEditText remarkET;// 备注输入框

	private View contentView;// 日历弹框
	private AlertDialog theDialog;// 日历弹框
	private CalendarPickerView calendarPV;// 日历选择
	private CircularLoadingDialog dialog;// 加载框
	private Boolean isNew;// 是否新增
	private String type;// 类型
	private Drawable drawable;// 选中类型图形
	private Integer money;// 金额
	private String date;// 日期
	private String remark;// 备注
	private ExpendInfo expendInfo;// 支出信息

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_expend);
		init();
		initView();
		setListener();
		initCalendarPickerView();
		initData();
		initMaterialRipple();
	}

	/**
	 * 
	 * @Description: 初始化
	 */
	private void init() {
		Intent intent = getIntent();
		if (null != intent) {
			balanceLevel = intent.getIntExtra(
					CommonConstant.TO_ADD_EXPEND_ACTIVITY_BALANCE_LEVEL, 0);
			isNew = intent.getBooleanExtra(
					CommonConstant.TO_ADD_EXPEND_ACTIVITY_IS_NEW, false);
			if (isNew) {
				expendInfo = new ExpendInfo();
			} else {
				expendInfo = (ExpendInfo) intent
						.getSerializableExtra(CommonConstant.TO_ADD_EXPEND_ACTIVITY_IS_MODIFY);
			}
		} else {
			balanceLevel = 0;
		}
	}

	/**
	 * 
	 * @Description: 初始化控件
	 */
	private void initView() {
		headRL = (RelativeLayout) findViewById(R.id.add_expend_head_rl);
		backBtn = (ImageButton) findViewById(R.id.add_expend_back_btn);
		saveBtn = (ImageButton) findViewById(R.id.add_expend_save_btn);
		studyRB = (RadioButton) findViewById(R.id.add_expend_type_study_rb);
		shoppingRB = (RadioButton) findViewById(R.id.add_expend_type_shopping_rb);
		entertainmentRB = (RadioButton) findViewById(R.id.add_expend_type_entertainment_rb);
		medicalRB = (RadioButton) findViewById(R.id.add_expend_type_medical_rb);
		trafficRB = (RadioButton) findViewById(R.id.add_expend_type_traffic_rb);
		eatRB = (RadioButton) findViewById(R.id.add_expend_type_eat_rb);
		loverRB = (RadioButton) findViewById(R.id.add_expend_type_lover_rb);
		otherRB = (RadioButton) findViewById(R.id.add_expend_type_other_rb);
		checkedTV = (TextView) findViewById(R.id.add_expend_checked_type_tv);
		moneyET = (MaterialEditText) findViewById(R.id.add_expend_money_et);
		dateET = (MaterialEditText) findViewById(R.id.add_expend_time_et);
		remarkET = (MaterialEditText) findViewById(R.id.add_expend_remark_et);
	}

	/**
	 * 
	 * @Description: 设置监听
	 */
	private void setListener() {
		backBtn.setOnClickListener(this);
		saveBtn.setOnClickListener(this);
		studyRB.setOnClickListener(this);
		shoppingRB.setOnClickListener(this);
		entertainmentRB.setOnClickListener(this);
		medicalRB.setOnClickListener(this);
		trafficRB.setOnClickListener(this);
		eatRB.setOnClickListener(this);
		loverRB.setOnClickListener(this);
		otherRB.setOnClickListener(this);
		// 弹出日期时间选择
		dateET.setOnTouchListener(new OnTouchListener() {
			int touch_flag = 0;

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (v.getId()) {
				case R.id.add_expend_time_et: {
					touch_flag++;
					if (touch_flag == 2) {
						theDialog.show();
						theDialog.setContentView(contentView);
						theDialog.getWindow().setGravity(Gravity.CENTER);
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
	}

	/**
	 * 
	 * @Description: 初始化日历控件
	 */
	private void initCalendarPickerView() {
		// 弹框
		theDialog = new AlertDialog.Builder(this).create();
		theDialog.setCanceledOnTouchOutside(true);
		contentView = LayoutInflater.from(this).inflate(
				R.layout.dialog_calendarpicker, null);
		// 日历控件
		calendarPV = (CalendarPickerView) contentView
				.findViewById(R.id.calendar_view);
		final Calendar nextYear = Calendar.getInstance();
		nextYear.add(Calendar.YEAR, 1);
		final Calendar lastYear = Calendar.getInstance();
		lastYear.add(Calendar.YEAR, -1);
		calendarPV.init(lastYear.getTime(), nextYear.getTime()) // 设置显示时间范围
				.withSelectedDate(new Date());
		// 取消按钮
		Button cancleBtn = (Button) contentView
				.findViewById(R.id.dialog_cancle_btn);
		// 确定按钮
		Button confirmBtn = (Button) contentView
				.findViewById(R.id.dialog_confirm_btn);
		// 动态特效
		MaterialRippleLayout
				.on(cancleBtn)
				.rippleColor(Color.parseColor(CommonConstant.RIPPLE_COLOR_DARK))
				.rippleAlpha(CommonConstant.RIPPLE_ALPHA).rippleHover(true)
				.create();
		MaterialRippleLayout
				.on(confirmBtn)
				.rippleColor(Color.parseColor(CommonConstant.RIPPLE_COLOR_DARK))
				.rippleAlpha(CommonConstant.RIPPLE_ALPHA).rippleHover(true)
				.create();
		// 取消事件
		cancleBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				theDialog.dismiss();
			}
		});
		// 确定事件
		confirmBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				theDialog.dismiss();
				long time = calendarPV.getSelectedDate().getTime();
				String date = CalendarUtil.longToString("yyyy-MM-dd", time);
				dateET.setText(date);

			}
		});
		theDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialogInterface) {
				calendarPV.fixDialogDimens();
			}
		});
	}

	/**
	 * 
	 * @Description: 初始化数据
	 */
	@SuppressLint("SimpleDateFormat")
	private void initData() {
		if (1 == balanceLevel) {// 余额不足25%
			headRL.setBackgroundResource(R.color.red);
			backBtn.setBackgroundResource(R.drawable.selector_bg_red);
			saveBtn.setBackgroundResource(R.drawable.selector_bg_red);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				getWindow().addFlags(
						WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
				getWindow().addFlags(
						WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
				SystemBarTintManager tintManager = new SystemBarTintManager(
						this);
				tintManager.setStatusBarTintEnabled(true);// 设置可沉浸
				// 设置一个颜色给系统栏
				tintManager.setStatusBarTintResource(R.color.red);
			}
		}
		dialog = new CircularLoadingDialog(this);
		if (isNew) {
			type = "学习";
		} else {
			type = expendInfo.getType();
			setType();
			moneyET.setText(expendInfo.getMoney() + "");
			dateET.setText(expendInfo.getDate());
			if (null != expendInfo.getRemark()) {
				remarkET.setText(expendInfo.getRemark());
			}
		}
	}

	/**
	 * 
	 * @Description: 设置选中类型
	 */
	private void setType() {
		checkedTV.setText(type);
		if ("学习".equals(type)) {
			drawable = getResources().getDrawable(R.drawable.icon_study);
		} else if ("购物".equals(type)) {
			drawable = getResources().getDrawable(R.drawable.icon_shopping);
		} else if ("娱乐".equals(type)) {
			drawable = getResources()
					.getDrawable(R.drawable.icon_entertainment);
		} else if ("医疗".equals(type)) {
			drawable = getResources().getDrawable(R.drawable.icon_medical);
		} else if ("交通".equals(type)) {
			drawable = getResources().getDrawable(R.drawable.icon_traffic);
		} else if ("饮食".equals(type)) {
			drawable = getResources().getDrawable(R.drawable.icon_eat);
		} else if ("爱人".equals(type)) {
			drawable = getResources().getDrawable(R.drawable.icon_lover);
		} else if ("其他".equals(type)) {
			drawable = getResources().getDrawable(R.drawable.icon_type_other);
		}
		// 这一步必须要做,否则不会显示.
		drawable.setBounds(0, 0, drawable.getMinimumWidth(),
				drawable.getMinimumHeight());
		checkedTV.setCompoundDrawables(null, drawable, null, null);
	}

	/**
	 * 
	 * @Description: 瓷砖特效
	 */
	private void initMaterialRipple() {
		dateET.setPaddings(DensityUtils.sp2px(this, 32), 0, 0, 0);
		remarkET.setPaddings(DensityUtils.sp2px(this, 32), 0, 0, 0);
		// 设置验证信息
		moneyET.addValidator(new METValidator("请输入整数~") {

			@Override
			public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
				String number = text.toString();
				if ("".equals(number) || VerifyUtils.isDigit(number)) {
					return true;
				}
				return false;
			}
		});
		List<View> views = new ArrayList<View>();
		views.add(studyRB);
		views.add(shoppingRB);
		views.add(entertainmentRB);
		views.add(medicalRB);
		views.add(trafficRB);
		views.add(eatRB);
		views.add(loverRB);
		views.add(otherRB);
		for (View view : views) {
			// 动态特效
			MaterialRippleLayout
					.on(view)
					.rippleColor(
							Color.parseColor(CommonConstant.RIPPLE_COLOR_DARK))
					.rippleAlpha(CommonConstant.RIPPLE_ALPHA).rippleHover(true)
					.create();
		}
	}

	/**
	 * 
	 * @Description: 按键监听
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_expend_back_btn: {// 返回
			finish();
			break;
		}
		case R.id.add_expend_save_btn: {// 保存
			if ("".equals(moneyET.getText().toString().replace(" ", ""))) {
				ToastUtils.showToast("请输入支出金额~");
				moneyET.requestFocus();
				return;
			} else {
				money = Integer.valueOf(moneyET.getText().toString());
			}
			if ("".equals(dateET.getText().toString().replace(" ", ""))) {
				ToastUtils.showToast("请选择日期~");
				dateET.requestFocus();
				return;
			} else {
				date = dateET.getText().toString().replace(" ", "");
			}
			saveExpend();
			break;
		}
		case R.id.add_expend_type_study_rb: {// 学习
			type = "学习";
			setType();
			break;
		}
		case R.id.add_expend_type_shopping_rb: {// 购物
			type = "购物";
			setType();
			break;
		}
		case R.id.add_expend_type_entertainment_rb: {// 娱乐
			type = "娱乐";
			setType();
			break;
		}
		case R.id.add_expend_type_medical_rb: {// 医疗
			type = "医疗";
			setType();
			break;
		}
		case R.id.add_expend_type_traffic_rb: {// 交通
			type = "交通";
			setType();
			break;
		}
		case R.id.add_expend_type_eat_rb: {// 饮食
			type = "饮食";
			setType();
			break;
		}
		case R.id.add_expend_type_lover_rb: {// 爱人
			type = "爱人";
			setType();
			break;
		}
		case R.id.add_expend_type_other_rb: {// 其他
			type = "其他";
			setType();
			break;
		}
		default:
			break;
		}
	}

	/**
	 * 
	 * @Description: 保存支出
	 */
	private void saveExpend() {
		dialog.show();
		remark = remarkET.getText().toString().replace(" ", "");
		expendInfo.setMoney(money);
		expendInfo.setDate(date);
		expendInfo.setMonth(date.substring(0, date.lastIndexOf("-")));
		expendInfo.setYear(date.substring(0, date.indexOf("-")));
		expendInfo.setType(type);
		expendInfo.setRemark(remark);
		expendInfo.setUsername(GlobalParams.userInfo.getUsername());
		if (isNew) {// 新增
			expendInfo.save(this, new SaveListener() {

				@Override
				public void onSuccess() {// 成功
					LogcatUtils.i("新增支出记录成功");
					dialog.dismiss();
					ToastUtils.showToast("保存成功~");
					setResult(CommonConstant.RESULTCODE_ADD_EXPEND_SUCCESS);
					finish();
				}

				@Override
				public void onFailure(int code, String message) {// 失败
					LogcatUtils.e("新增支出记录失败：" + code + " " + message);
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
						ToastUtils.showToast("保存失败，请重试~");
						break;
					}
					}
				}
			});
		} else {// 修改
			expendInfo.update(this, new UpdateListener() {

				@Override
				public void onSuccess() {
					LogcatUtils.i("修改支出记录成功");
					dialog.dismiss();
					ToastUtils.showToast("修改成功~");
					setResult(CommonConstant.RESULTCODE_MODIFY_EXPEND_SUCCESS);
					finish();
				}

				@Override
				public void onFailure(int code, String message) {
					LogcatUtils.e("修改支出记录失败：" + code + " " + message);
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

	}
}
