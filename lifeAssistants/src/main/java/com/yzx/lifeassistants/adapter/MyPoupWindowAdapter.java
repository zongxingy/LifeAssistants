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
 * 
 * @Description: PoupWindowAdapter
 * @author yzx
 * @date 2015-12-26
 */
public class MyPoupWindowAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<String> dataList;
	private ViewHolder holder;

	public MyPoupWindowAdapter(Context context, List<String> dataList) {
		this.context = context;
		this.dataList = dataList;
		inflater = (LayoutInflater) this.context
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
		if (null == convertView) {
			convertView = inflater.inflate(R.layout.item_poupwindowdapter,
					parent, false);
			holder = new ViewHolder();
			holder.itemTV = (TextView) convertView
					.findViewById(R.id.item_poupwindowdapter_text);
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
