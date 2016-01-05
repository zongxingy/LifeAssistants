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
import com.yzx.lifeassistants.utils.LoadLocalImageUtil;

/**
 * 
 * @Description: GridView可编辑图片的适配器
 * @author yzx
 * @date 2015年11月5日
 */
public class EditGridAdapter extends BaseAdapter {

	private List<ImageItem> dataList;
	private ImageItem data;
	private LayoutInflater inflater;
	private viewHolder holder;

	public EditGridAdapter(Context context, List<ImageItem> dataList) {
		inflater = LayoutInflater.from(context);
		this.dataList = dataList;
	}

	@Override
	public int getCount() {
		if (dataList.size() == 3) {
			return 3;
		} else {
			return dataList.size() + 1;
		}
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

		if (position == dataList.size()) {
			LoadLocalImageUtil.getInstance().displayFromDrawable(
					R.drawable.icon_add_pic, holder.picIV);
			if (position == 3) {
				holder.picIV.setVisibility(View.GONE);
			}
		} else {
			// LoadLocalImageUtil.getInstance().displayFromSDCard(
			// data.getImagePath(), holder.picIV);
			data = dataList.get(position);
			if (data.getIsLocalPic()) {
				LoadLocalImageUtil.getInstance().displayFromSDCard(
						data.getImagePath(), holder.picIV);
			} else {
				ImageLoader.getInstance().displayImage(
						data.getImagePath(),
						holder.picIV,
						BaseApplication.getInstance().getOptions(
								R.drawable.mat2),
						new SimpleImageLoadingListener() {
							@Override
							public void onLoadingComplete(String imageUri,
									View view, Bitmap loadedImage) {
								super.onLoadingComplete(imageUri, view,
										loadedImage);
							}
						});
			}

		}
		return convertView;
	}

	static class viewHolder {
		ImageView picIV;// 图片
	}

}
