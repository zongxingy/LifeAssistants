package com.yzx.lifeassistants.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import cn.bmob.v3.listener.UpdateListener;

import com.balysv.materialripple.MaterialRippleLayout;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.METValidator;
import com.yzx.lifeassistants.GlobalParams;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.base.BaseActivity;
import com.yzx.lifeassistants.common.CommonConstant;
import com.yzx.lifeassistants.utils.DensityUtils;
import com.yzx.lifeassistants.utils.LogcatUtils;
import com.yzx.lifeassistants.utils.ToastUtils;
import com.yzx.lifeassistants.utils.VerifyUtils;
import com.yzx.lifeassistants.view.widget.CircularLoadingDialog;

/**
 * @Description: 设置生活费指标
 * @author: yzx
 * @time: 2015-11-30 下午2:19:06
 */
public class SetAlimonyActivity extends BaseActivity implements OnClickListener {
	private ImageButton backBtn;// 返回按钮
	private TextView titleTV;// 顶部标题
	private MaterialEditText alimonyET;// 生活费指标
	private Button commitBtn;// 提交按钮

	private CircularLoadingDialog dialog;//
	private Integer alimonyLast;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_alimony);
		initView();
		setListener();
		initData();
		initMaterialRipple();
	}

	/**
	 * 
	 * @Description: 初始化控件
	 */
	private void initView() {
		backBtn = (ImageButton) findViewById(R.id.top_back_btn);
		titleTV = (TextView) findViewById(R.id.top_title_tv);
		alimonyET = (MaterialEditText) findViewById(R.id.set_alimony_alimony_et);
		commitBtn = (Button) findViewById(R.id.set_alimony_commit_btn);
	}

	/**
	 * 
	 * @Description: 设置监听
	 */
	private void setListener() {
		backBtn.setOnClickListener(this);
		commitBtn.setOnClickListener(this);
	}

	/**
	 * 
	 * @Description: 初始化数据
	 */
	private void initData() {
		titleTV.setText("设置指标");
		dialog = new CircularLoadingDialog(this);
		Intent intent = getIntent();
		if (null != intent) {
			alimonyLast = intent.getIntExtra("alimony", 1500);
		}
	}

	/**
	 * 
	 * @Description: 瓷砖特效
	 */
	private void initMaterialRipple() {
		alimonyET.setPaddings(DensityUtils.sp2px(this, 32), 0, 0, 0);
		// 设置验证信息
		alimonyET.addValidator(new METValidator("请输入整数~") {

			@Override
			public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
				String number = text.toString();
				if ("".equals(number) || VerifyUtils.isDigit(number)) {
					return true;
				}
				return false;
			}
		});
		// 动态特效
		MaterialRippleLayout
				.on(commitBtn)
				.rippleColor(Color.parseColor(CommonConstant.RIPPLE_COLOR_DARK))
				.rippleAlpha(CommonConstant.RIPPLE_ALPHA).rippleHover(true)
				.create();

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
		case R.id.set_alimony_commit_btn: {// 提交
			String content = alimonyET.getText().toString().replace(" ", "");
			if (VerifyUtils.isDigit(content)) {
				dialog.show();
				Integer alimony = Integer.parseInt(content);
				GlobalParams.userInfo.setAlimony(alimony);
				GlobalParams.userInfo.update(this, new UpdateListener() {

					@Override
					public void onSuccess() {// 成功
						LogcatUtils.i("设置生活费指标成功");
						dialog.dismiss();
						setResult(CommonConstant.RESULTCODE_SET_ALIMONY_SUCCESS);
						finish();
					}

					@Override
					public void onFailure(int code, String message) {// 失败
						LogcatUtils.e("设置生活费指标失败：" + code + " " + message);
						dialog.dismiss();
						GlobalParams.userInfo.setAlimony(alimonyLast);
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
							ToastUtils.showToast("提交失败，请重试~");
							break;
						}
						}

					}
				});
			} else {
				ToastUtils.showToast("请输入整数~");
				alimonyET.requestFocus();
			}
			break;
		}
		default:
			break;
		}
	}

}
