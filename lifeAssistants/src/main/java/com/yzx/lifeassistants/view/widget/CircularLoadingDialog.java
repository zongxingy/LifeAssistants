package com.yzx.lifeassistants.view.widget;

import android.app.Dialog;
import android.content.Context;

import com.yzx.lifeassistants.R;

/**
 * @Description: 圆形加载对话框
 * @author: yzx
 * @time: 2015-12-28 上午8:50:08
 */
public class CircularLoadingDialog extends Dialog {

	// /**
	// *
	// * @Description: 默认xx毫秒数后消失
	// */
	// private static final int DEFAULT_MILLISECONDS = 10000;
	// /**
	// *
	// * @Description: xx毫秒数后消失
	// */
	// private int milliSeconds;
	//
	// private WaitThread waitThread;
	//
	// private CircularLoadingDialog dialog;

	public CircularLoadingDialog(Context context) {
		super(context, R.style.LodingDialog);
		setContentView(R.layout.dialog_loading);
		setCancelable(false);
		// dialog = this;
		// milliSeconds = DEFAULT_MILLISECONDS;
	}

	// @Override
	// public void show() {
	// super.show();
	// waitThread = new WaitThread();
	// waitThread.start();
	// }
	//
	// @Override
	// public void dismiss() {
	// super.dismiss();
	// if (null != waitThread && waitThread.isAlive()) {
	// waitThread.interrupt();
	// waitThread = null;
	// }
	// }
	//
	// public void setMilliSeconds(int milliSeconds) {
	// this.milliSeconds = milliSeconds;
	// }
	//
	// @SuppressLint("HandlerLeak")
	// Handler handler = new Handler() {
	//
	// @Override
	// public void handleMessage(Message msg) {
	// switch (msg.what) {
	// case 1234:
	// if (dialog.isShowing()) {
	// dialog.dismiss();
	// ToastUtils.showToast("连接超时，请确认网络正常后再试");
	// }
	// break;
	// default:
	// break;
	// }
	// super.handleMessage(msg);
	// }
	//
	// };
	//
	// class WaitThread extends Thread {
	//
	// @Override
	// public void run() {
	// try {
	// Thread.sleep(milliSeconds);
	// Message msg = new Message();
	// msg.what = 1234;
	// handler.sendMessage(msg);
	// } catch (InterruptedException e) {
	// if (null != e.getMessage()) {
	// LogcatUtils.e(e.getMessage());
	// }
	// }
	// super.run();
	// }
	// }
}
