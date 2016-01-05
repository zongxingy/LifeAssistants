package com.yzx.lifeassistants.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yzx.lifeassistants.R;

/**
 * @author: yzx
 * @date: 2015-9-11 下午4:46:47
 * @Description: 菜单选项适配器
 */
public class MenuItemAdapter extends BaseAdapter {
	private List<String> dataList;// 数据类集合
	private String data;// 数据类
	private LayoutInflater inflater;
	private ViewHolder holder;// 控件实例存储类

	public MenuItemAdapter(Context context, List<String> dataList) {
		this.dataList = dataList;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_leftmenu, parent,
					false);
			holder = new ViewHolder();
			holder.nameTV = (TextView) convertView
					.findViewById(R.id.leftmenu_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		data = dataList.get(position);
		holder.nameTV.setText(data);
		return convertView;
	}

	static class ViewHolder {
		TextView nameTV;// 菜单名称
	}
}
