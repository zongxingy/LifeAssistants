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
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

import com.balysv.materialripple.MaterialRippleLayout;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.METValidator;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.base.BaseActivity;
import com.yzx.lifeassistants.common.CommonConstant;
import com.yzx.lifeassistants.utils.DensityUtils;
import com.yzx.lifeassistants.utils.LogcatUtils;
import com.yzx.lifeassistants.utils.SpUtils;
import com.yzx.lifeassistants.utils.ToastUtils;
import com.yzx.lifeassistants.view.widget.CircularLoadingDialog;

/**
 * @author: yzx
 * @date: 2015-9-13 上午9:41:04
 * @Description: 修改密码
 */
public class ModifyPwdActivity extends BaseActivity implements OnClickListener {

	private ImageButton topBackBtn;// 顶部按钮
	private TextView titleTV;// 顶部标题
	private MaterialEditText oldPwdrET; // 旧密码输入框
	private MaterialEditText newPwdET;// 新密码输入框
	private MaterialEditText newPwdAgainET;// 再次输入密码框
	private Button modifyBtn;// 重置按钮

	private CircularLoadingDialog dialog;// 加载框
	protected SpUtils sputil;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_pwd);
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
		oldPwdrET = (MaterialEditText) findViewById(R.id.modify_pwd_old_et);
		newPwdET = (MaterialEditText) findViewById(R.id.modify_pwd_new_et);
		newPwdAgainET = (MaterialEditText) findViewById(R.id.modify_pwd_new_again_et);
		modifyBtn = (Button) findViewById(R.id.modify_pwd_modify_btn);
		modifyBtn.setOnClickListener(this);
	}

	/**
	 * @Description: 初始化数据
	 */
	private void initData() {
		dialog = new CircularLoadingDialog(ModifyPwdActivity.this);
		titleTV.setText(R.string.modify_pwd_title);
		sputil = new SpUtils(this, CommonConstant.SP_FAIL_NAME);
	}

	/**
	 * 
	 * @Description: 瓷砖特效
	 */
	private void initMaterialRipple() {
		// 设置Padding
		oldPwdrET.setPaddings(DensityUtils.sp2px(this, 32), 0, 0, 0);
		newPwdET.setPaddings(DensityUtils.sp2px(this, 32), 0, 0, 0);
		newPwdAgainET.setPaddings(DensityUtils.sp2px(this, 32), 0, 0, 0);
		// 设置验证信息
		oldPwdrET.addValidator(new METValidator("请输入正确的密码~") {

			@Override
			public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
				String pwd = sputil.getValue(CommonConstant.PASSWORD_KEY, "");
				String oldPwd = text.toString();
				if ("".equals(oldPwd) || pwd.equals(oldPwd)) {
					return true;
				}
				return false;
			}
		});
		newPwdET.addValidator(new METValidator("请输入与旧密码不一样的密码~") {

			@Override
			public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
				String pwd = sputil.getValue(CommonConstant.PASSWORD_KEY, "");
				String newPwd = text.toString();
				if ("".equals(newPwd) || !pwd.equals(newPwd)) {
					return true;
				}
				return false;
			}
		});
		newPwdAgainET.addValidator(new METValidator("两次输入的密码不一致~") {

			@Override
			public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
				String pwd = newPwdET.getText().toString();
				String pwdAgain = text.toString();
				if ("".equals(pwdAgain) || pwd.equals(pwdAgain)) {
					return true;
				}
				return false;
			}
		});
		// 动态特效
		MaterialRippleLayout
				.on(modifyBtn)
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
		case R.id.modify_pwd_modify_btn: {// 修改密码
			modifyPwd();
			break;
		}
		default:
			break;
		}
	}

	String oldPwd = "";
	String newPwd = "";
	String newPwdAgain = "";

	/**
	 * @Description: 修改密码
	 */
	private void modifyPwd() {
		oldPwd = oldPwdrET.getText().toString();
		newPwd = newPwdET.getText().toString();
		newPwdAgain = newPwdAgainET.getText().toString();
		if (null == oldPwd || "".equals(oldPwd)) {
			ToastUtils.showToast("请输入旧密码~");
			oldPwdrET.requestFocus();
			return;
		}
		if (null == newPwd || "".equals(newPwd)) {
			ToastUtils.showToast("请输入新密码~");
			newPwdET.requestFocus();
			return;
		}
		if (oldPwd.equals(newPwd)) {
			ToastUtils.showToast("请输入与旧密码不一样的密码~");
			newPwdET.setText("");
			newPwdAgainET.setText("");
			newPwdET.requestFocus();
			return;
		}
		if (16 < newPwd.length()) {
			ToastUtils.showToast("密码长度不能超过16位~");
			newPwdET.requestFocus();
			return;
		}
		if (null == newPwdAgain || "".equals(newPwdAgain)) {
			ToastUtils.showToast("请再输入新密码~");
			newPwdAgainET.requestFocus();
			return;
		}
		if (!newPwd.equals(newPwdAgain)) {
			ToastUtils.showToast("两次输入的密码不一致~");
			newPwdAgainET.setText("");
			newPwdAgainET.requestFocus();
			return;
		} else {
			dialog.show();
			modifyPwd(oldPwd, newPwd);
		}
	}

	/**
	 * 
	 * @Description: 根据旧密码更改密码
	 */
	private void modifyPwd(String oldPwd, String newPwd) {
		BmobUser.updateCurrentUserPassword(ModifyPwdActivity.this, oldPwd,
				newPwd, new UpdateListener() {

					@Override
					public void onSuccess() {// 修改成功
						LogcatUtils.i("修改密码成功");
						dialog.dismiss();
						ToastUtils.showToast("修改密码成功请重新登陆");
						Intent intent = new Intent(ModifyPwdActivity.this,
								LoginActivity.class);
						intent.putExtra(CommonConstant.TO_LOGIN_NAME,
								CommonConstant.FROM_MODIFY_TO_LOGIN_KEY);
						startActivity(intent);
						finish();
					}

					@Override
					public void onFailure(int code, String message) {// 修改失败
						LogcatUtils.e("修改密码错误：" + code + " " + message);
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
						case 210: {// 旧密码错误
							oldPwdrET.setText("");
							oldPwdrET.requestFocus();
							ToastUtils.showToast("请输入正确的旧密码~");
							break;
						}
						default: {
							ToastUtils.showToast("修改密码失败，请重试~");
							break;
						}
						}

					}
				});
	}

}
