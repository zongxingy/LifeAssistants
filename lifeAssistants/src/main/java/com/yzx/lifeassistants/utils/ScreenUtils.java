package com.yzx.lifeassistants.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

/**
 * 
 * @Description: 截屏工具类
 * @author yzx
 * @date 2015-11-10
 */
public class ScreenUtils {
	private ScreenUtils() {
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	/**
	 * 
	 * @Description: 获得屏幕高度
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}

	/**
	 * 
	 * @Description: 获得屏幕宽度
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.heightPixels;
	}

	/**
	 * 
	 * @Description: 获得状态栏的高度
	 * @param context
	 * @return
	 */
	public static int getStatusHeight(Context context) {

		int statusHeight = -1;
		try {
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();
			int height = Integer.parseInt(clazz.getField("status_bar_height")
					.get(object).toString());
			statusHeight = context.getResources().getDimensionPixelSize(height);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusHeight;
	}

	/**
	 * 
	 * @Description: 获取当前屏幕截图，包含状态栏
	 * @param activity
	 * @return
	 */
	public static Bitmap snapShotWithStatusBar(Activity activity) {
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		int width = getScreenWidth(activity);
		int height = getScreenHeight(activity);
		Bitmap bp = null;
		bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
		view.destroyDrawingCache();
		return bp;

	}

	/**
	 * 
	 * @Description: 获取当前屏幕截图，不包含状态栏
	 * @param activity
	 * @return
	 */
	public static Bitmap snapShotWithoutStatusBar(Activity activity) {
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;

		int width = getScreenWidth(activity);
		int height = getScreenHeight(activity);
		Bitmap bp = null;
		bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
				- statusBarHeight);
		view.destroyDrawingCache();
		return bp;

	}

	/**
	 * 
	 * @Description: 获取一般View的Bitmap
	 */
	public static Bitmap getViewBitmap(View v) {
		v.setDrawingCacheEnabled(true);
		v.buildDrawingCache();
		Bitmap b1 = v.getDrawingCache();
		Bitmap bitmap = Bitmap.createBitmap(b1, 0, 0, v.getWidth(),
				v.getHeight());
		v.destroyDrawingCache();
		return bitmap;
	}

	/**
	 * 
	 * @Description: 截取ScrollView的屏幕
	 */
	public static Bitmap getScrollViewBitmap(ScrollView scrollView) {
		int h = 0;
		Bitmap bitmap = null;
		// 获取ScrollView实际高度
		for (int i = 0; i < scrollView.getChildCount(); i++) {
			h += scrollView.getChildAt(i).getHeight();
			scrollView.getChildAt(i).setBackgroundColor(
					Color.parseColor("#ffffff"));
		}
		// 创建对应大小的bitmap
		bitmap = Bitmap.createBitmap(scrollView.getWidth(), h,
				Bitmap.Config.ARGB_8888);
		final Canvas canvas = new Canvas(bitmap);
		scrollView.draw(canvas);
		return bitmap;

	}

	/**
	 * 获取ListView的Bitmap
	 */
	public static Bitmap getListViewBitmap(int width, ListView listView) {
		int height, rootHeight = 0;
		Bitmap bitmap;
		Canvas canvas;
		int yPos = 0;
		int listItemNum;
		List<View> childViews = null;
		ListAdapter listAdapter = listView.getAdapter();
		listItemNum = listAdapter.getCount();
		childViews = new ArrayList<View>(listItemNum);
		View itemView;
		// 计算整体高度:
		for (int pos = 0; pos < listItemNum; ++pos) {
			itemView = listAdapter.getView(pos, null, null);
			// measure过程
			itemView.measure(
					MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			childViews.add(itemView);
			rootHeight += itemView.getMeasuredHeight();
		}

		height = rootHeight;
		bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		canvas = new Canvas(bitmap);

		Bitmap itemBitmap;
		View itemView1;
		int childHeight;
		// 把每个ItemView生成图片，并画到背景画布上
		for (int pos = 0; pos < childViews.size(); ++pos) {
			itemView1 = childViews.get(pos);
			childHeight = itemView1.getMeasuredHeight();
			itemBitmap = viewToBitmap(itemView1, width, childHeight);
			if (itemBitmap != null) {
				canvas.drawBitmap(itemBitmap, 0, yPos, null);
			}
			yPos = childHeight + yPos;
		}
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		return bitmap;
	}

	/**
	 * 
	 * @Description: View转Bitmap
	 */
	public static Bitmap viewToBitmap(View view, int viewWidth, int viewHeight) {
		view.layout(0, 0, viewWidth, viewHeight);
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		return bitmap;
	}

	/**
	 * 
	 * @Description: 将两张位图拼接成一张(纵向拼接)
	 */
	public static Bitmap add2Bitmap(Bitmap first, Bitmap second) {
		int width = Math.max(first.getWidth(), second.getWidth());
		int height = first.getHeight() + second.getHeight();
		Bitmap result = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(result);
		canvas.drawBitmap(first, 0, 0, null);
		canvas.drawBitmap(second, 0, first.getHeight(), null);
		return result;
	}

	/**
	 * @Description: 将多张位图拼接成一张(纵向拼接)
	 */
	public static Bitmap addManyBitmap(List<Bitmap> bitmaps) {
		int width = 0;
		int height = 0;
		int nextHeight = 0;
		for (int i = 0; i < bitmaps.size(); i++) {
			if (width < bitmaps.get(i).getWidth()) {
				width = bitmaps.get(i).getWidth();
			}
			height += bitmaps.get(i).getHeight();
		}
		Bitmap result = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(result);
		for (int i = 0; i < bitmaps.size(); i++) {
			if (i == 0) {
				canvas.drawBitmap(bitmaps.get(i), 0, 0, null);
			} else {
				nextHeight += bitmaps.get(i - 1).getHeight();
				canvas.drawBitmap(bitmaps.get(i), 0, nextHeight, null);
			}
			canvas.save(Canvas.ALL_SAVE_FLAG);
		}
		canvas.restore();
		return result;
	}

	/**
	 * 
	 * @Description: bitmap生成PNG图片
	 */
	public static void bitmapToPNG(Bitmap bitmap, String filePath) {
		try {
			FileOutputStream fos = new FileOutputStream(filePath);
			if (null != fos) {
				bitmap.compress(CompressFormat.PNG, 100, fos);
				fos.flush();
				fos.close();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new InvalidParameterException();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @Description: 压缩图片
	 */
	public static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		int options = 100;
		// 循环判断如果压缩后图片是否大于100kb,大于继续压缩
		while (baos.toByteArray().length / 1024 > 100) {
			// 重置baos
			baos.reset();
			// 这里压缩options%，把压缩后的数据存放到baos中
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);
			// 每次都减少10
			options -= 10;
		}
		// 把压缩后的数据baos存放到ByteArrayInputStream中
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		// 把ByteArrayInputStream数据生成图片
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
		return bitmap;
	}

	/**
	 * 
	 * @Description: 保存到sdcard
	 */
	public static String savePic(Bitmap b) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss",
				Locale.US);
		File outfile = new File(Environment.getExternalStorageDirectory()
				.getPath() + "/image");
		// 如果文件不存在，则创建一个新文件
		if (!outfile.isDirectory()) {
			try {
				outfile.mkdir();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String fname = outfile + "/" + sdf.format(new Date()) + ".png";
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fname);
			if (null != fos) {
				b.compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.flush();
				fos.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fname;
	}

}
