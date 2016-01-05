package com.yzx.lifeassistants.view.widget;

import com.yzx.lifeassistants.R;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

public class CustomDialog extends Dialog {
	private TextView text;

	public CustomDialog(Context context) {
		super(context, R.style.CommonDialog);
		setContentView(R.layout.widget_progress_dialog);
		setCancelable(false);
		text = (TextView) findViewById(R.id.process_dialog_text);
	}

	public CustomDialog(Context context, String msg) {
		super(context, R.style.CommonDialog);
		setContentView(R.layout.widget_progress_dialog);
		setCancelable(false);
		text = (TextView) findViewById(R.id.process_dialog_text);
		text.setText(msg);
	}

	public void setMessage(String msg) {
		text.setText(msg);
	}

	public void setMessage(int resId) {
		text.setText(resId);
	}

	public CustomDialog(Context context, int theme) {
		super(context, theme);
	}

	public CustomDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

}
