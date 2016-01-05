package com.yzx.lifeassistants.listener;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.yzx.lifeassistants.activity.SecondHandClassifyActivity;
import com.yzx.lifeassistants.bean.ClassifyItem;

/**
 * @Description: 二手物品分类监听器
 * @author: yzx
 * @time: 2015-11-16 上午8:48:59
 */
public class ClassifyGridListener implements OnItemClickListener {
	private Context context;
	/**
	 * @Description: 类别列表
	 */
	private List<ClassifyItem> classifyList;

	public ClassifyGridListener(Context context, List<ClassifyItem> classifyList) {
		this.context = context;
		this.classifyList = classifyList;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long arg3) {
		Intent intent = new Intent(context, SecondHandClassifyActivity.class);
		intent.putExtra("bigClass", classifyList.get(position).getBigClass());
		intent.putExtra("classify", classifyList.get(position).getName());
		context.startActivity(intent);
	}

}
