package com.yzx.lifeassistants.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

import com.balysv.materialripple.MaterialRippleLayout;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.METValidator;
import com.yzx.lifeassistants.GlobalParams;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.base.ActivityCollector;
import com.yzx.lifeassistants.base.BaseActivity;
import com.yzx.lifeassistants.bean.User;
import com.yzx.lifeassistants.common.CommonConstant;
import com.yzx.lifeassistants.utils.DensityUtils;
import com.yzx.lifeassistants.utils.LogcatUtils;
import com.yzx.lifeassistants.utils.SpUtils;
import com.yzx.lifeassistants.utils.ToastUtils;
import com.yzx.lifeassistants.utils.VerifyUtils;
import com.yzx.lifeassistants.view.widget.CircularLoadingDialog;

/**
 * @author: yzx
 * @date: 2015-9-11 下午4:23:47
 * @Description: 用户登录
 */
public class LoginActivity extends BaseActivity implements OnClickListener {
	protected SpUtils sputil;// 保存/获取邮箱,密码
	private CircularLoadingDialog dialog;// 加载框
	private ImageButton topBackBtn;// 顶部返回按钮
	private TextView titleTV;// 顶部标题
	private MaterialEditText mailET;// 邮箱输入框
	private MaterialEditText pwdET;// 密码输入框
	private Button loginBtn;// 登陆按钮
	private TextView userRegTV;// 用户注册链接
	private TextView forgetPwdTV;// 忘记密码链接

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
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
		mailET = (MaterialEditText) findViewById(R.id.login_mail_et);
		pwdET = (MaterialEditText) findViewById(R.id.login_pwd_et);
		loginBtn = (Button) findViewById(R.id.login_btn);
		loginBtn.setOnClickListener(this);
		userRegTV = (TextView) findViewById(R.id.user_reg_tv);
		userRegTV.setOnClickListener(this);
		forgetPwdTV = (TextView) findViewById(R.id.forget_pwd_tv);
		forgetPwdTV.setOnClickListener(this);

	}

	/**
	 * @Description: 初始化数据
	 */
	private void initData() {
		dialog = new CircularLoadingDialog(this);
		titleTV.setText(R.string.login_title);
		if (null == sputil) {
			sputil = new SpUtils(this, CommonConstant.SP_FAIL_NAME);
		}
		// 邮箱
		String email = sputil.getValue(CommonConstant.USERNAME_KEY, "");
		// 密码
		String pwd = sputil.getValue(CommonConstant.PASSWORD_KEY, "");
		Intent intent = getIntent();
		if (intent != null) {
			String key = intent.getStringExtra(CommonConstant.TO_LOGIN_NAME);
			if (null == key || "".equals(key)) {
				// 是否记住密码
				Boolean remberPwd = sputil.getValue(
						CommonConstant.REMBER_PWD_KEY, false);
				// 是否自动登陆
				Boolean autoLogin = sputil.getValue(
						CommonConstant.AUTO_LOGIN_KEY, false);
				if (!remberPwd) {// 无记住密码
					LogcatUtils.i("无记住密码");
					mailET.setText(email);
					pwdET.setText("");
					pwdET.requestFocus();
				} else {// 有记住密码
					LogcatUtils.i("有记住密码");
					mailET.setText(email);
					pwdET.setText(pwd);
					if (autoLogin) {// 有自动登陆
						LogcatUtils.i("有自动登陆");
						login(email, pwd);
					} else {// 无自动登陆
						LogcatUtils.i("无自动登陆");
						pwdET.requestFocus();
					}
				}

			} else {
				if (CommonConstant.FROM_MODIFY_TO_LOGIN_KEY.equals(key)) {// 登陆界面<--修改密码
					LogcatUtils.i("来自修改密码界面");
					mailET.setText(email);
					pwdET.setText("");
					pwdET.requestFocus();
					return;
				} else if (CommonConstant.FROM_LOGOUT_TO_LOGIN_KEY.equals(key)) {// 登陆界面<--用户注销
					LogcatUtils.i("来自用户注销界面");
					mailET.setText(email);
					pwdET.setText("");
					pwdET.requestFocus();
					return;
				} else {
				}
			}

		} else {
			mailET.requestFocus();
		}

	}

	/**
	 * 
	 * @Description: 瓷砖特效
	 */
	private void initMaterialRipple() {
		// 设置Padding
		mailET.setPaddings(DensityUtils.sp2px(this, 32), 0, 0, 0);
		pwdET.setPaddings(DensityUtils.sp2px(this, 32), 0, 0, 0);
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
		List<View> views = new ArrayList<View>();
		views.add(loginBtn);
		views.add(userRegTV);
		views.add(forgetPwdTV);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_back_btn: {// 返回按钮
			quitApp();
			break;
		}
		case R.id.login_btn: {// 登陆按钮
			String mail = mailET.getText().toString();
			String pwd = pwdET.getText().toString();
			login(mail, pwd);
			break;
		}
		case R.id.user_reg_tv: {// 注册链接
			Intent intent = new Intent(LoginActivity.this,
					UserRegActivity.class);
			startActivityForResult(intent, CommonConstant.REQUESTCODE_USER_REG);
			break;
		}
		case R.id.forget_pwd_tv: {// 忘记密码链接
			Intent intent = new Intent(LoginActivity.this,
					ForgetPwdActivity.class);
			startActivity(intent);
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
		new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
				.setTitleText("您要残忍退出吗？")
				.setContentText("这里有您想要的信息！！！")
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
	 * @Description: 用户登录
	 */
	private void login(String email, String pwd) {
		if (null == email || "".equals(email)) {
			ToastUtils.showToast("请输入邮箱~");
			mailET.requestFocus();
			return;
		} else if (null == pwd || "".equals(pwd)) {
			ToastUtils.showToast("请输入密码~");
			pwdET.requestFocus();
			return;
		} else if (!VerifyUtils.checkEmail(email)) {
			ToastUtils.showToast("请输入正确的邮箱格式~");
			mailET.requestFocus();
			return;
		} else {
			dialog.show();
			BmobUser user = new BmobUser();
			user.setUsername(email);
			user.setPassword(pwd);
			loginRemto(user);
		}

	}

	/**
	 * 
	 * @Description: 登陆服务器
	 */
	private void loginRemto(BmobUser user) {

		user.login(this, new SaveListener() {

			@Override
			public void onSuccess() {// 登陆成功
				LogcatUtils.i("登陆成功");
				dialog.dismiss();
				User userInfo = BmobUser.getCurrentUser(LoginActivity.this,
						User.class);
				loginSuccess(userInfo);

			}

			@Override
			public void onFailure(int code, String message) {// 登陆失败
				LogcatUtils.e("登陆失败：" + code + " " + message);
				dialog.dismiss();
				switch (code) {
				case 101: {// 登录接口的用户名或密码不正确
					ToastUtils.showToast("邮箱或密码不正确~");
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
					ToastUtils.showToast("登陆失败，请重试~");
					break;
				}
				}

			}
		});

	}

	/**
	 * 
	 * @Description: 登陆成功
	 */
	private void loginSuccess(User userInfo) {
		GlobalParams.userInfo = userInfo;// 添加全局
		sputil.setValue(CommonConstant.USERNAME_KEY, mailET.getText()
				.toString());
		sputil.setValue(CommonConstant.PASSWORD_KEY, pwdET.getText().toString());
		if (GlobalParams.userInfo.getNick() == null
				|| "".equals(GlobalParams.userInfo.getNick())) {
			GlobalParams.userInfo.setNick(GlobalParams.userInfo.getUsername());
		}
		sputil.setValue(CommonConstant.NICK_KEY,
				GlobalParams.userInfo.getNick());
		Intent intent = new Intent(LoginActivity.this, MainActivity.class);
		startActivity(intent);

	}

	/**
	 * 
	 * @Description: 注册返回
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == CommonConstant.RESULTCODE_USER_REG_OK) {// 注册成功
			switch (requestCode) {
			case CommonConstant.REQUESTCODE_USER_REG: {
				String email = data.getStringExtra("email");
				mailET.setText(email);
				String pwd = data.getStringExtra("pwd");
				pwdET.setText(pwd);
				login(email, pwd);
				break;
			}
			default:
				break;
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}
}
