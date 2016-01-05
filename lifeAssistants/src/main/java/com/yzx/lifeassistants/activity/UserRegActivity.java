package com.yzx.lifeassistants.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import cn.bmob.v3.listener.SaveListener;

import com.balysv.materialripple.MaterialRippleLayout;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.METValidator;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.base.BaseActivity;
import com.yzx.lifeassistants.bean.User;
import com.yzx.lifeassistants.common.CommonConstant;
import com.yzx.lifeassistants.utils.DensityUtils;
import com.yzx.lifeassistants.utils.LogcatUtils;
import com.yzx.lifeassistants.utils.ToastUtils;
import com.yzx.lifeassistants.utils.VerifyUtils;
import com.yzx.lifeassistants.view.widget.CircularLoadingDialog;

//import com.yzx.lifeassistants.bean.User;

/**
 * @author: yzx
 * @date: 2015-9-12 下午3:32:19
 * @Description: 用户注册
 */
public class UserRegActivity extends BaseActivity implements OnClickListener {
	private CircularLoadingDialog dialog;// 加载框
	private ImageButton topBackBtn;// 顶部按钮
	private TextView titleTV;// 顶部标题
	private MaterialEditText mailET; // 账号输入框
	private MaterialEditText pwdET;// 密码输入框
	private MaterialEditText pwdAgainET;// 再次输入密码框
	private Button regBtn;// 注册按钮

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_reg);
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
		mailET = (MaterialEditText) findViewById(R.id.reg_mail_et);
		pwdET = (MaterialEditText) findViewById(R.id.reg_pwd_et);
		pwdAgainET = (MaterialEditText) findViewById(R.id.reg_pwd_again_et);
		regBtn = (Button) findViewById(R.id.reg_btn);
		regBtn.setOnClickListener(this);

	}

	/**
	 * @Description: 初始化数据
	 */
	private void initData() {
		dialog = new CircularLoadingDialog(this);
		titleTV.setText(R.string.reg_title);
	}

	/**
	 * 
	 * @Description: 瓷砖特效
	 */
	private void initMaterialRipple() {
		// 设置Padding
		mailET.setPaddings(DensityUtils.sp2px(this, 32), 0, 0, 0);
		pwdET.setPaddings(DensityUtils.sp2px(this, 32), 0, 0, 0);
		pwdAgainET.setPaddings(DensityUtils.sp2px(this, 32), 0, 0, 0);
		// 设置验证信息
		mailET.addValidator(new METValidator("请输入正确的邮箱格式~") {

			@Override
			public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
				String email = text.toString();
				if ("".equals(email) || VerifyUtils.checkEmail(email)) {
					return true;
				}
				return false;
			}
		});
		pwdAgainET.addValidator(new METValidator("两次输入的密码不一致~") {

			@Override
			public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
				String pwd = pwdET.getText().toString();
				String pwdAgain = text.toString();
				if ("".equals(pwdAgain) || pwd.equals(pwdAgain)) {
					return true;
				}
				return false;
			}
		});
		// 动态特效
		MaterialRippleLayout
				.on(regBtn)
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
		case R.id.top_back_btn: {// 返回按钮
			finish();
			break;
		}
		case R.id.reg_btn: {// 注册按钮
			register();
			break;
		}
		default:
			break;
		}

	}

	/**
	 * @Description: 用户注册
	 */
	private void register() {
		final String email = mailET.getText().toString();
		final String pwd = pwdET.getText().toString();
		String pwdAgain = pwdAgainET.getText().toString();
		if (!authInput(email, pwd, pwdAgain)) {// 验证
			return;
		} else {
			dialog.show();
			User user = new User();
			// 用户名
			user.setUsername(email);
			// 邮箱
			user.setEmail(email);
			// 密码
			user.setPassword(pwd);
			// 默认昵称
			if (email.contains("@")) {
				user.setNick(email.substring(0, email.indexOf("@")));
			} else {
				user.setNick(email);
			}
			// 默认性别
			user.setSex(false);
			// 默认月生活费指标
			user.setAlimony(1500);
			// 注册
			user.signUp(this, new SaveListener() {

				@Override
				public void onSuccess() {// 注册成功->登陆界面登陆
					LogcatUtils.i("注册成功");
					dialog.dismiss();
					Intent intent = new Intent(UserRegActivity.this,
							LoginActivity.class);
					intent.putExtra("email", email);
					intent.putExtra("pwd", pwd);
					setResult(CommonConstant.RESULTCODE_USER_REG_OK, intent);
					finish();
				}

				@Override
				public void onFailure(int code, String message) {// 注册失败
					dialog.dismiss();
					LogcatUtils.e("注册失败：" + code + " " + message);
					switch (code) {
					case 202: {// 用户名已存在
						ToastUtils.showToast("该邮箱已被注册，请直接登陆~");
						mailET.requestFocus();
						break;
					}
					case 203: {// 邮箱已存在
						ToastUtils.showToast("该邮箱已被注册，请直接登陆~");
						mailET.requestFocus();
						break;
					}
					case 9019: {// 格式不正确：手机号码、邮箱地址、验证码
						ToastUtils.showToast("邮箱地址格式不正确，请输入正确的邮箱~");
						mailET.requestFocus();
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
						ToastUtils.showToast("注册失败，请重试～");
						break;
					}
					}

				}
			});
		}
	}

	/**
	 * @Description: 验证输入的有效性
	 * @param: email
	 * @param: pwd
	 * @param: pwdAgain
	 * @return
	 */
	private boolean authInput(String email, String pwd, String pwdAgain) {
		if (null == email || "".equals(email)) {
			ToastUtils.showToast("请输入邮箱~");
			mailET.requestFocus();
			return false;
		}
		if (!VerifyUtils.checkEmail(email)) {
			ToastUtils.showToast("邮箱格式错误~");
			mailET.requestFocus();
			return false;
		}
		if (null == pwd || "".equals(pwd)) {
			ToastUtils.showToast("请输入密码~");
			pwdET.requestFocus();
			return false;
		}
		if (16 < pwd.length()) {
			ToastUtils.showToast("密码长度不能超过16位~");
			pwdET.requestFocus();
			return false;
		}
		if (null == pwdAgain || "".equals(pwdAgain)) {
			ToastUtils.showToast("请再次输入密码~");
			pwdAgainET.requestFocus();
			return false;
		}
		if (!TextUtils.equals(pwd, pwdAgain)) {
			ToastUtils.showToast("两次输入的密码不一致~");
			return false;
		}
		return true;
	}
}
