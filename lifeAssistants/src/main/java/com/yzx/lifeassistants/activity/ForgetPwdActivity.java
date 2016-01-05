package com.yzx.lifeassistants.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.ResetPasswordByEmailListener;

import com.balysv.materialripple.MaterialRippleLayout;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.METValidator;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.base.BaseActivity;
import com.yzx.lifeassistants.common.CommonConstant;
import com.yzx.lifeassistants.utils.DensityUtils;
import com.yzx.lifeassistants.utils.LogcatUtils;
import com.yzx.lifeassistants.utils.ToastUtils;
import com.yzx.lifeassistants.utils.VerifyUtils;
import com.yzx.lifeassistants.view.widget.CircularLoadingDialog;

/**
 * @author: yzx
 * @date: 2015-9-12 下午3:33:29
 * @Description: 忘记密码
 */
public class ForgetPwdActivity extends BaseActivity implements OnClickListener {
	private CircularLoadingDialog dialog;// 加载框
	private ImageButton topBackBtn;// 顶部按钮
	private TextView titleTV;// 顶部标题
	private MaterialEditText emailET; // 账号输入框
	private Button resetBtn;// 重置按钮

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_pwd);
		initView();
		initData();
		initMaterialRipple();
	}

	/**
	 * @Description: 初始化控件
	 */
	private void initView() {
		topBackBtn = (ImageButton) findViewById(R.id.top_back_btn);
		topBackBtn.setOnClickListener(this);
		titleTV = (TextView) findViewById(R.id.top_title_tv);
		emailET = (MaterialEditText) findViewById(R.id.forget_pwd_mail_et);
		resetBtn = (Button) findViewById(R.id.forget_pwd_reset_btn);
		resetBtn.setOnClickListener(this);

	}

	/**
	 * @Description: 初始化数据
	 */
	private void initData() {
		dialog = new CircularLoadingDialog(this);
		titleTV.setText(R.string.forget_pwd_title);
	}

	/**
	 * 
	 * @Description: 瓷砖特效
	 */
	private void initMaterialRipple() {
		// 设置Padding
		emailET.setPaddings(DensityUtils.sp2px(this, 32), 0, 0, 0);
		// 设置验证信息
		emailET.addValidator(new METValidator("请输入正确的邮箱格式~") {

			@Override
			public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
				String email = text.toString();
				if ("".equals(email) || VerifyUtils.checkEmail(email)) {
					return true;
				}
				return false;
			}
		});
		// 动态特效
		MaterialRippleLayout
				.on(resetBtn)
				.rippleColor(Color.parseColor(CommonConstant.RIPPLE_COLOR_DARK))
				.rippleAlpha(CommonConstant.RIPPLE_ALPHA).rippleHover(true)
				.create();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_back_btn: {// 返回按钮
			finish();
			break;
		}
		case R.id.forget_pwd_reset_btn: {// 重置密码
			String email = emailET.getText().toString();
			if (null == email || "".equals(email)) {
				ToastUtils.showToast("请输入邮箱~");
				return;
			} else if (!VerifyUtils.checkEmail(email)) {
				ToastUtils.showToast("请输入正确的邮箱格式~");
				return;
			} else {
				dialog.show();
				resetPwd(email);
			}

			break;
		}
		default:
			break;
		}

	}

	/**
	 * @Description: 重置密码
	 */
	private void resetPwd(final String email) {
		BmobUser.resetPasswordByEmail(this, email,
				new ResetPasswordByEmailListener() {

					@Override
					public void onSuccess() {// 邮件发送成功
						LogcatUtils.i("邮件发送成功");
						dialog.dismiss();
						ToastUtils.showToast("重置密码请求成功，请到" + email
								+ "邮箱进行密码重置操作");
						finish();
					}

					@Override
					public void onFailure(int code, String message) {// 邮件发送失败
						LogcatUtils.e("邮件发送失败：" + code + " " + message);
						dialog.dismiss();
						switch (code) {
						case 205: {// 用户名/邮箱不存在
							ToastUtils.showToast("该邮箱尚未注册~");
							break;
						}
						case 9019: {// 格式不正确：手机号码、邮箱地址、验证码
							ToastUtils.showToast("邮箱地址格式不正确，请输入正确的邮箱~");
							break;
						}
						case 9010: {// 网络超时
							ToastUtils.showToast("网络超时，请检查您的手机网络~");
							break;
						}
						case 9016: {// 无网络连接，请检查您的手机网络
							ToastUtils.showToast("无网络连接，请检查您的手机网络~");
							break;
						}
						default: {
							ToastUtils.showToast("邮件发送失败，请重试～");
							break;
						}
						}
					}
				});

	}
}
