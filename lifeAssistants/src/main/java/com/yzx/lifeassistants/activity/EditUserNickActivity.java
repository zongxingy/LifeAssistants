package com.yzx.lifeassistants.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import cn.bmob.v3.listener.UpdateListener;

import com.balysv.materialripple.MaterialRippleLayout;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.yzx.lifeassistants.GlobalParams;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.base.BaseActivity;
import com.yzx.lifeassistants.common.CommonConstant;
import com.yzx.lifeassistants.utils.DensityUtils;
import com.yzx.lifeassistants.utils.LogcatUtils;
import com.yzx.lifeassistants.utils.SpUtils;
import com.yzx.lifeassistants.utils.ToastUtils;
import com.yzx.lifeassistants.view.widget.CircularLoadingDialog;

/**
 * @Description: 修改用户昵称
 * @author: yzx
 * @time: 2015-9-18 下午3:03:39
 */
public class EditUserNickActivity extends BaseActivity implements
		OnClickListener {
	protected SpUtils sputil;
	private CircularLoadingDialog dialog;// 加载框
	private ImageButton topBackIB;// 顶部返回按钮
	private TextView topTitleTV;// 顶部返回按钮
	private MaterialEditText nickET;// 编辑昵称
	private Button modifyBtn;// 修改按钮

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_nick);
		initView();
		initData();
		initMaterialRipple();
	}

	/**
	 * 
	 * @Description: 初始化控件
	 */
	private void initView() {
		topBackIB = (ImageButton) findViewById(R.id.top_back_btn);
		topBackIB.setOnClickListener(this);
		topTitleTV = (TextView) findViewById(R.id.top_title_tv);
		nickET = (MaterialEditText) findViewById(R.id.user_nick_edit_et);
		modifyBtn = (Button) findViewById(R.id.user_nick_btn);
		modifyBtn.setOnClickListener(this);
	}

	/**
	 * 
	 * @Description: 初始化数据
	 */
	private void initData() {
		dialog = new CircularLoadingDialog(this);
		if (null == sputil) {
			sputil = new SpUtils(this, CommonConstant.SP_FAIL_NAME);
		}
		topTitleTV.setText(getResources().getString(R.string.user_nick_title));
		if (null == GlobalParams.userInfo.getNick()
				|| "".equals(GlobalParams.userInfo.getNick())) {
			nickET.setText(GlobalParams.userInfo.getUsername());
		} else {
			nickET.setText(GlobalParams.userInfo.getNick());
		}
	}

	/**
	 * 
	 * @Description: 瓷砖特效
	 */
	private void initMaterialRipple() {
		nickET.setPaddings(DensityUtils.sp2px(this, 32), 0, 0, 0);
		// 动态特效
		MaterialRippleLayout
				.on(modifyBtn)
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
		case R.id.top_back_btn: {// 顶部返回
			setResult(CommonConstant.RESULTCODE_EDIT_NICK_CANCEL);
			finish();
			break;
		}
		case R.id.user_nick_btn: {// 确认更改
			String nick = nickET.getText().toString();
			if (null == nick || "".equals(nick)) {
				ToastUtils.showToast("用户昵称不允许为空~");
				nickET.requestFocus();
				return;
			}
			if (GlobalParams.userInfo.getNick().equals(nick)) {
				ToastUtils.showToast("用户昵称没有发生改变~");
				nickET.requestFocus();
				return;
			}
			if (2 > nick.length() || 6 < nick.length()) {
				ToastUtils.showToast("用户昵称在2-6字符内~");
				nickET.requestFocus();
				return;
			}
			dialog.show();
			editNick(nick);
			break;
		}
		default:
			break;
		}
	}

	/**
	 * 
	 * @Description: 修改昵称
	 */
	private void editNick(String nick) {
		GlobalParams.userInfo.setNick(nick);
		GlobalParams.userInfo.update(this, GlobalParams.userInfo.getObjectId(),
				new UpdateListener() {

					@Override
					public void onSuccess() {// 修改成功
						LogcatUtils.i("修改昵称成功");
						dialog.dismiss();
						setResult(CommonConstant.RESULTCODE_EDIT_NICK_OK);
						sputil.setValue(CommonConstant.NICK_KEY,
								GlobalParams.userInfo.getNick());
						finish();
					}

					@Override
					public void onFailure(int code, String message) {// 修改失败
						LogcatUtils.e("修改昵称失败：" + code + " " + message);
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
							ToastUtils.showToast("修改昵称失败，请重试~");
							break;
						}
						}

					}
				});
	}

}
