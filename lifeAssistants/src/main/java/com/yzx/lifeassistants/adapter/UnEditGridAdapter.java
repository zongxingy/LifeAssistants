package com.yzx.lifeassistants.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.base.BaseApplication;
import com.yzx.lifeassistants.bean.ImageItem;

/**
 * 
 * @Description: GridView不可编辑图片的适配器
 * @author yzx
 * @date 2015年11月6日
 */
public class UnEditGridAdapter extends BaseAdapter {

	private List<ImageItem> dataList;
	private ImageItem data;
	private LayoutInflater inflater;
	private viewHolder holder;

	public UnEditGridAdapter(Context context, List<ImageItem> dataList) {
		inflater = LayoutInflater.from(context);
		this.dataList = dataList;
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
			convertView = inflater.inflate(R.layout.item_grid_adapter, parent,
					false);
			holder = new viewHolder();
			holder.picIV = (ImageView) convertView
					.findViewById(R.id.item_grid_image);
			convertView.setTag(holder);
		} else {
			holder = (viewHolder) convertView.getTag();
		}
		data = dataList.get(position);
		ImageLoader.getInstance().displayImage(data.getImagePath(),
				holder.picIV,
				BaseApplication.getInstance().getOptions(R.drawable.mat2),
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						super.onLoadingComplete(imageUri, view, loadedImage);
					}
				});

		return convertView;
	}

	static class viewHolder {
		ImageView picIV;// 图片
	}

}
