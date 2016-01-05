package com.yzx.lifeassistants.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import cn.bmob.v3.listener.SaveListener;

import com.balysv.materialripple.MaterialRippleLayout;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.yzx.lifeassistants.GlobalParams;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.base.BaseActivity;
import com.yzx.lifeassistants.bean.Feedback;
import com.yzx.lifeassistants.common.CommonConstant;
import com.yzx.lifeassistants.utils.DensityUtils;
import com.yzx.lifeassistants.utils.LogcatUtils;
import com.yzx.lifeassistants.utils.ToastUtils;
import com.yzx.lifeassistants.view.widget.CircularLoadingDialog;

/**
 * @Description: 意见反馈
 * @author: yzx
 * @time: 2015-9-21 下午3:22:41
 */
public class FeedbackActivity extends BaseActivity implements OnClickListener {

	private ImageButton topBackBtn;// 顶部返回按钮
	private TextView titleTV;// 顶部标题
	private MaterialEditText contentET;// 反馈内容
	private Button commitBtn;// 提交按钮
	private CircularLoadingDialog loadingDialog;// 加载框

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		initView();
		initData();
		initMaterialRipple();

	}

	/**
	 * 
	 * @Description: 初始化控件
	 */
	private void initView() {
		topBackBtn = (ImageButton) findViewById(R.id.top_back_btn);
		topBackBtn.setOnClickListener(this);
		titleTV = (TextView) findViewById(R.id.top_title_tv);
		contentET = (MaterialEditText) findViewById(R.id.feedback_content_et);
		commitBtn = (Button) findViewById(R.id.feedback_commit_btn);
		commitBtn.setOnClickListener(this);
	}

	/**
	 * 
	 * @Description: 初始化数据
	 */
	private void initData() {
		loadingDialog = new CircularLoadingDialog(this);
		titleTV.setText(R.string.setting_feedback_tips);
	}

	/**
	 * 
	 * @Description: 瓷砖特效
	 */
	private void initMaterialRipple() {
		contentET.setPaddings(DensityUtils.sp2px(this, 32), 0, 0, 0);
		// 动态特效
		MaterialRippleLayout
				.on(commitBtn)
				.rippleColor(Color.parseColor(CommonConstant.RIPPLE_COLOR_DARK))
				.rippleAlpha(CommonConstant.RIPPLE_ALPHA).rippleHover(true)
				.create();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_back_btn: {// 顶部返回
			setResult(CommonConstant.RESULTCODE_FEEDBACK_CANCEL);
			finish();
			break;
		}
		case R.id.feedback_commit_btn: {// 提交按钮
			feedback();
			break;
		}
		default:
			break;
		}
	}

	/**
	 * 
	 * @Description: 发送意见反馈
	 */
	private void feedback() {
		String content = contentET.getText().toString();
		if (null == content || "".equals(content)) {
			ToastUtils.showToast("请输入反馈信息");
		} else {
			loadingDialog.show();
			saveFeedbackMsg(content);
		}
	}

	/**
	 * 
	 * @Description: 保存反馈信息
	 */
	private void saveFeedbackMsg(String content) {
		Feedback feedback = new Feedback();
		feedback.setContent(content);
		feedback.setContact(GlobalParams.userInfo.getEmail());
		feedback.save(this, new SaveListener() {

			@Override
			public void onSuccess() {// 保存成功
				LogcatUtils.i("反馈成功");
				loadingDialog.dismiss();
				setResult(CommonConstant.RESULTCODE_FEEDBACK_OK);
				finish();
			}

			@Override
			public void onFailure(int code, String message) {// 保存失败
				LogcatUtils.e("发送反馈信息失败：" + code + ":" + message);
				loadingDialog.dismiss();
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
					ToastUtils.showToast("发送反馈信息失败，请重试~");
					break;
				}
				}
			}
		});
	}

}
