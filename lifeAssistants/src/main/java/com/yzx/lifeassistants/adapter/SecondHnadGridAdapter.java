package com.yzx.lifeassistants.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.bean.ClassifyItem;
import com.yzx.lifeassistants.common.CommonConstant;

/**
 * 
 * @Description: 二手类别适配器
 * @author: yzx
 * @date: 2015-11-13
 */
public class SecondHnadGridAdapter extends BaseAdapter {

	private List<ClassifyItem> dataList;
	private ClassifyItem data;
	private LayoutInflater inflater;
	private ViewHolder holder;

	public SecondHnadGridAdapter(Context context, List<ClassifyItem> dataList) {
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
			convertView = MaterialRippleLayout
					.on(inflater.inflate(R.layout.item_second_hand_gridview,
							parent, false))
					.rippleInAdapter(true)
					.rippleOverlay(true)
					.rippleColor(
							Color.parseColor(CommonConstant.RIPPLE_COLOR_LITHT))
					.rippleAlpha(CommonConstant.RIPPLE_ALPHA).rippleHover(true)
					.create();
			holder = new ViewHolder();
			holder.image = (ImageView) convertView
					.findViewById(R.id.item_classify_img);
			holder.nameText = (TextView) convertView
					.findViewById(R.id.item_classify_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		data = dataList.get(position);
		holder.image.setImageResource(data.getResID());
		holder.nameText.setText(data.getName());
		return convertView;
	}

	static class ViewHolder {
		ImageView image;
		TextView nameText;
	}

}
