package com.yzx.lifeassistants.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yzx.lifeassistants.R;

/**
 * @Description: 自定义ArrayAdapter
 * @author: yzx
 * @time: 2015-12-25 上午9:58:23
 */
public class MyArrayAdapter extends ArrayAdapter<String> {

	private List<String> dataList;
	private LayoutInflater inflater;
	private ViewHolder holder;

	public MyArrayAdapter(Context context, List<String> dataList) {
		super(context, 0, dataList);
		this.dataList = dataList;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (null == convertView) {
			convertView = inflater.inflate(R.layout.item_arrayadapter, parent,
					false);
			holder = new ViewHolder();
			holder.itemTV = (TextView) convertView
					.findViewById(R.id.item_arrayadapter_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.itemTV.setText(dataList.get(position));
		return convertView;
	}

	static class ViewHolder {
		TextView itemTV;
	}
}
