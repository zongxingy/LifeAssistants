package com.yzx.lifeassistants.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.activity.ImagePagerActivity;
import com.yzx.lifeassistants.base.BaseApplication;

/**
 * 
 * @Description: 图片轮播适配器
 * @author: yzx
 * @date: 2015-11-13
 */
public class CyclePagerAdapter extends PagerAdapter {
	private Context context;
	private List<String> picUrlList;// 图片URL列表
	private List<ImageView> imageViewList;// 图片控件列表

	public CyclePagerAdapter(Context context, List<String> picUrlList) {
		this.context = context;
		this.picUrlList = picUrlList;
		initImageList(context, picUrlList);
	}

	private void initImageList(Context context, List<String> picUrlList) {
		// 前后各多加一张图片(如原有1、2、3三张图片,在前后加两张图片变为3、1、2、3、1)
		imageViewList = new ArrayList<ImageView>();
		for (int i = 0; i < picUrlList.size() + 2; i++) {
			ImageView imageView = new ImageView(context);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			int loaction = 0;
			if (i == 0) {// 前面加的图片与最后一张图片一样
				loaction = picUrlList.size() - 1;
			} else if (i == picUrlList.size() + 1) {// 后面加的图片与第一张图片一样
				loaction = 0;
			} else {
				loaction = i - 1;
			}
			ImageLoader.getInstance().displayImage(
					picUrlList.get(loaction),
					imageView,
					BaseApplication.getInstance().getOptions(
							R.drawable.pic_loading),
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							super.onLoadingComplete(imageUri, view, loadedImage);
						}
					});
			imageViewList.add(imageView);
		}
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public int getCount() {
		return imageViewList.size();
	}

	@Override
	public Object instantiateItem(android.view.ViewGroup container,
			final int position) {
		// PagerView在instantiateItem设置按键监听
		imageViewList.get(position).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String[] imagePaths = picUrlList.toArray(new String[0]);
				int location = 0;
				if (0 == position) {
					location = picUrlList.size() - 1;
				} else if (picUrlList.size() + 1 == position) {
					location = 0;
				} else {
					location = position - 1;
				}
				imageBrower(location, imagePaths);
			}
		});
		container.addView(imageViewList.get(position));
		return imageViewList.get(position);
	}

	/**
	 * 
	 * @Description: 加载网络图片
	 */
	private void imageBrower(int position, String[] imagePaths) {
		Intent intent = new Intent(context, ImagePagerActivity.class);
		intent.putExtra(ImagePagerActivity.IMAGE_URLS, imagePaths);
		intent.putExtra(ImagePagerActivity.IMAGE_INDEX, position);
		context.startActivity(intent);
	}

	@Override
	public void destroyItem(android.view.ViewGroup container, int position,
			Object object) {
		container.removeView(imageViewList.get(position));
	}

}
