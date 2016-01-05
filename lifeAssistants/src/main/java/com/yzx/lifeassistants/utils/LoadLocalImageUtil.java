package com.yzx.lifeassistants.utils;

import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 
 * @ClassName: LoadLocalImageUtil
 * @Description: 加载本地图片工具类
 * @author yzx
 * @date 2015年11月5日
 */
public class LoadLocalImageUtil {

	private LoadLocalImageUtil() {

	}

	private static LoadLocalImageUtil instance = null;

	public static synchronized LoadLocalImageUtil getInstance() {
		if (instance == null) {
			instance = new LoadLocalImageUtil();
		}
		return instance;
	}

	/**
	 * 
	 * @Description: 从内存卡中异步加载本地图片
	 */
	public void displayFromSDCard(String uri, ImageView imageView) {
		// String imageUri = "file:///mnt/sdcard/image.png"; // from SD card
		ImageLoader.getInstance().displayImage("file://" + uri, imageView);
	}

	/**
	 * 
	 * @Description: 从assets文件夹中异步加载图片
	 */
	public void dispalyFromAssets(String imageName, ImageView imageView) {
		// String imageUri = "assets://image.png"; // from assets
		ImageLoader.getInstance().displayImage("assets://" + imageName,
				imageView);
	}

	/**
	 * 
	 * @Description: 从drawable中异步加载本地图片
	 */
	public void displayFromDrawable(int imageId, ImageView imageView) {
		// String imageUri = "drawable://" + R.drawable.image; // from drawables
		// (only images, non-9patch)
		ImageLoader.getInstance().displayImage("drawable://" + imageId,
				imageView);
	}

	/**
	 * 
	 * @Description: 从内容提提供者中抓取图片
	 */
	public void displayFromContent(String uri, ImageView imageView) {
		// String imageUri = "content://media/external/audio/albumart/13"; //
		// from content provider
		ImageLoader.getInstance().displayImage("content://" + uri, imageView);
	}
}
