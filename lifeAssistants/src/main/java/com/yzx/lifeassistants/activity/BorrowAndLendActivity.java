package com.yzx.lifeassistants.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.yzx.lifeassistants.GlobalParams;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.base.BaseActivity;
import com.yzx.lifeassistants.bean.BorrowDetail;
import com.yzx.lifeassistants.bean.LendDetail;
import com.yzx.lifeassistants.common.CommonConstant;
import com.yzx.lifeassistants.utils.ToastUtils;
import com.yzx.lifeassistants.view.widget.CircularLoadingDialog;

/**
 * @Description: 借入借出
 * @author: yzx
 * @time: 2015-9-15 下午5:14:22
 */
@SuppressLint("SimpleDateFormat")
public class BorrowAndLendActivity extends BaseActivity implements
		OnClickListener {
	private String from;//
	private TextView topTitleTV;// 顶部标题
	private ImageButton topBackIB;// 顶部返回按钮
	private String borrowId;// 借入明细id
	private String lendId;// 借出明细id
	private boolean isNew;// 判断是否是新增
	private CircularLoadingDialog dialog;
	private EditText moneyET;// 金额
	private EditText personET;// 姓名
	private EditText remarkET;// 备注
	private EditText timeET;// 时间
	private Button deleteBtn;// 删除按钮
	private Button submitBtn;// 提交按钮
	private SimpleDateFormat mFormatter = new SimpleDateFormat(
			"yyyy-MMMM-dd aa hh:mm");// 日期时间格式

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_borrow_lend);
		init();
		initView();
		initData();
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
		topBackIB = (ImageButton) findViewById(R.id.top_back_btn);
		topBackIB.setOnClickListener(this);
		topTitleTV = (TextView) findViewById(R.id.top_title_tv);
		moneyET = (EditText) findViewById(R.id.borrow_lend_money_edit);
		personET = (EditText) findViewById(R.id.borrow_lend_person_edit);
		remarkET = (EditText) findViewById(R.id.borrow_lend_remark_edit);
		timeET = (EditText) findViewById(R.id.borrow_lend_time_edit);
		// 弹出日期时间选择
		timeET.setOnTouchListener(new OnTouchListener() {
			int touch_flag = 0;

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (v.getId()) {
				case R.id.borrow_lend_time_edit: {
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
		deleteBtn = (Button) findViewById(R.id.borrow_lend_delete_btn);
		deleteBtn.setOnClickListener(this);
		submitBtn = (Button) findViewById(R.id.borrow_lend_submit_btn);
		submitBtn.setOnClickListener(this);
	}

	/**
	 * 
	 * @Description: 初始化数据
	 */
	private void initData() {
		if (from.contains("BORROW")) {
			topTitleTV.setText(R.string.borrow_lend_borrow);
		}
		if (from.contains("LEND")) {
			topTitleTV.setText(R.string.borrow_lend_lend);
		}
		if (CommonConstant.FROM_BORROWFRAGMENT.equals(from)) {// check
			initBorrowData();
			return;
		}
		if (CommonConstant.FROM_LENDFRAGMENT.equals(from)) {// check
			initLendData();
			return;
		}
		if (CommonConstant.FROM_BORROWANDFRAGMENT.equals(from)) {// add
			isNew = true;
			deleteBtn.setVisibility(View.GONE);
			return;
		}
		if (CommonConstant.FROM_ANDLENDFRAGMENT.equals(from)) {// add
			isNew = true;
			deleteBtn.setVisibility(View.GONE);
			return;
		}

	}

	/**
	 * 
	 * @Description: 初始化借出数据
	 */
	private void initLendData() {
		LendDetail lendInfo = (LendDetail) getIntent().getSerializableExtra(
				"lendInfo");
		if (lendInfo == null) {
			isNew = true;
			deleteBtn.setVisibility(View.GONE);
		} else {
			lendId = lendInfo.getObjectId();
			moneyET.setText(lendInfo.getMoney());
			personET.setText(lendInfo.getPerson());
			remarkET.setText(lendInfo.getRemark());
			timeET.setText(lendInfo.getTime());

		}
	}

	/**
	 * 
	 * @Description: 初始化借入数据
	 */
	private void initBorrowData() {
		BorrowDetail borrowInfo = (BorrowDetail) getIntent()
				.getSerializableExtra("borrowInfo");
		if (borrowInfo == null) {
			isNew = true;
			deleteBtn.setVisibility(View.GONE);
		} else {
			borrowId = borrowInfo.getObjectId();
			moneyET.setText(borrowInfo.getMoney());
			personET.setText(borrowInfo.getPerson());
			remarkET.setText(borrowInfo.getRemark());
			timeET.setText(borrowInfo.getTime());

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_back_btn: {// 返回按钮
			finish();
			break;
		}
		case R.id.borrow_lend_submit_btn: {// 提交按钮
			dialog.setTitle("提交中···");
			dialog.show();
			addByType();
			break;
		}
		case R.id.borrow_lend_delete_btn: {// 删除按钮
			dialog.setTitle("删除中···");
			dialog.show();
			deleteByType();
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
				}).setInitialDate(new Date()).build().show();
	}

	String money = "";
	String person = "";
	String remark = "";
	String time = "";

	/**
	 * @Description: 根据类型添加借入/借出明细
	 */
	private void addByType() {

		money = moneyET.getText().toString();
		person = personET.getText().toString();
		remark = remarkET.getText().toString();
		time = timeET.getText().toString();
		if (null == money || "".equals(money)) {
			dialog.dismiss();
			ToastUtils.showToast("请输入金额");
			moneyET.requestFocus();
			return;
		}
		if (null == person || "".equals(person)) {
			dialog.dismiss();
			ToastUtils.showToast("请输入姓名");
			return;
		}
		if (null == remark || "".equals(remark)) {
			dialog.dismiss();
			ToastUtils.showToast("请输入备注");
			personET.requestFocus();
			return;
		}
		if (null == time || "".equals(time)) {
			dialog.dismiss();
			ToastUtils.showToast("请输入时间");
			timeET.requestFocus();
			return;
		}
		if (from.contains("BORROW")) {
			addBorrow();
		} else if (from.contains("LEND")) {
			addLend();
		}
	}

	/**
	 * @Description: 添加借入明细
	 */
	private void addBorrow() {
		BorrowDetail borrowInfo = new BorrowDetail();
		borrowInfo.setMoney(money);
		borrowInfo.setPerson(person);
		borrowInfo.setRemark(remark);
		borrowInfo.setTime(time);
		borrowInfo.setUsername(GlobalParams.userInfo.getUsername());// 发布人信息
		if (isNew) {
			borrowInfo.save(this, new SaveListener() {
				@Override
				public void onSuccess() {// 添加成功
					dialog.dismiss();
					setResult(CommonConstant.RESULTCODE_NEW_BORROW_OK);
					finish();
				}

				@Override
				public void onFailure(int code, String message) {
					dialog.dismiss();
					switch (code) {
					case 9010:
						ToastUtils.showToast("连接超时，请确认网络连接后再重试");
						break;

					default:
						ToastUtils.showToast("添加失败" + code + ":" + message);
						break;
					}
				}
			});
		} else {
			borrowInfo.update(this, borrowId, new UpdateListener() {
				@Override
				public void onSuccess() {// 修改成功
					dialog.dismiss();
					setResult(CommonConstant.RESULTCODE_UPDATE_BORROW_OK);
					finish();
				}

				@Override
				public void onFailure(int code, String message) {
					dialog.dismiss();
					switch (code) {
					case 9010:
						ToastUtils.showToast("连接超时，请确认网络连接后再重试");
						break;

					default:
						ToastUtils.showToast("修改失败" + code + ":" + message);
						break;
					}
				}
			});
		}

	}

	/**
	 * @Description: 添加借出明细
	 */
	private void addLend() {
		LendDetail lendInfo = new LendDetail();
		lendInfo.setMoney(money);
		lendInfo.setPerson(person);
		lendInfo.setRemark(remark);
		lendInfo.setTime(time);
		lendInfo.setUsername(GlobalParams.userInfo.getUsername());// 发布人信息
		if (isNew) {
			lendInfo.save(this, new SaveListener() {
				@Override
				public void onSuccess() {// 添加成功
					dialog.dismiss();
					setResult(CommonConstant.RESULTCODE_NEW_LEND_OK);
					finish();
				}

				@Override
				public void onFailure(int code, String message) {
					dialog.dismiss();
					switch (code) {
					case 9010:
						ToastUtils.showToast("连接超时，请确认网络连接后再重试");
						break;

					default:
						ToastUtils.showToast("添加失败" + code + ":" + message);
						break;
					}
				}
			});
		} else {
			lendInfo.update(this, lendId, new UpdateListener() {

				@Override
				public void onSuccess() {// 修改成功
					dialog.dismiss();
					setResult(CommonConstant.RESULTCODE_UPDATE_LEND_OK);
					finish();
				}

				@Override
				public void onFailure(int code, String message) {
					dialog.dismiss();
					switch (code) {
					case 9010:
						ToastUtils.showToast("连接超时，请确认网络连接后再重试");
						break;

					default:
						ToastUtils.showToast("修改失败" + code + ":" + message);
						break;
					}
				}
			});
		}

	}

	/**
	 * @Description: 根据类型删除借入/借出明细
	 */
	private void deleteByType() {
		if (CommonConstant.FROM_BORROWFRAGMENT.equals(from)) {
			deleteBorrow();
		} else if (CommonConstant.FROM_LENDFRAGMENT.equals(from)) {
			deleteLend();
		}
	}

	/**
	 * 
	 * @Description: 删除借入明细
	 */
	private void deleteBorrow() {
		BorrowDetail borrowInfo = new BorrowDetail();
		borrowInfo.setObjectId(borrowId);
		borrowInfo.delete(this, new DeleteListener() {

			@Override
			public void onSuccess() {// 删除成功
				dialog.dismiss();
				setResult(CommonConstant.RESULTCODE_DELETE_BORROW_OK);
				finish();

			}

			@Override
			public void onFailure(int code, String message) {
				dialog.dismiss();
				switch (code) {
				case 9010:
					ToastUtils.showToast("连接超时，请确认网络连接后再重试");
					break;

				default:
					ToastUtils.showToast("删除失败" + code + ":" + message);
					break;
				}

			}
		});
	}

	/**
	 * 
	 * @Description: 删除借出明细
	 */
	private void deleteLend() {
		LendDetail lendInfo = new LendDetail();
		lendInfo.setObjectId(lendId);
		lendInfo.delete(this, new DeleteListener() {

			@Override
			public void onSuccess() {// 删除成功
				dialog.dismiss();
				setResult(CommonConstant.RESULTCODE_DELETE_LEND_OK);
				finish();
			}

			@Override
			public void onFailure(int code, String message) {
				dialog.dismiss();
				switch (code) {
				case 9010:
					ToastUtils.showToast("连接超时，请确认网络连接后再重试");
					break;

				default:
					ToastUtils.showToast("删除失败" + code + ":" + message);
					break;
				}
			}
		});
	}
}
