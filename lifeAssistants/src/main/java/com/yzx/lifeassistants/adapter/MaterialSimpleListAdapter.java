package com.yzx.lifeassistants.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.bean.ShareListItem;

/**
 * 
 * @Description: 分享列表适配器
 * @author yzx
 * @date 2015-11-15
 */
public class MaterialSimpleListAdapter extends ArrayAdapter<ShareListItem> {

	public MaterialSimpleListAdapter(Context context) {
		super(context, R.layout.md_simplelist_item, android.R.id.title);
	}

	@Override
	public View getView(final int index, View convertView, ViewGroup parent) {
		final View view = super.getView(index, convertView, parent);
		final ShareListItem item = getItem(index);
		ImageView ic = (ImageView) view.findViewById(android.R.id.icon);
		if (item.getIcon() != null)
			ic.setImageDrawable(item.getIcon());
		else
			ic.setVisibility(View.GONE);
		TextView tv = (TextView) view.findViewById(android.R.id.title);
		tv.setText(item.getContent());
		return view;
	}
}
