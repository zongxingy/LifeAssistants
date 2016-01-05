package com.yzx.lifeassistants.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.yzx.lifeassistants.base.BaseApplication;

/**
 * 
 * @ClassName: FileUtils
 * @Description: 文件工具类
 * @author: yzx
 * @date: 2015-11-9
 */
public class FileUtils {

	/**
	 * @Description: 保存图片的文件夹路径
	 */
	public static String SDPATH = getSDCardPath() + "/LifeAssistants/pic";

	/**
	 * 
	 * @Description: 获得保存图片的路径
	 */
	public static String getImagePath() {
		// 目录路径
		File path = new File(SDPATH);
		if (!path.exists()) {
			path.mkdirs();
		}
		String time = String.valueOf(System.currentTimeMillis());
		String imagePath = SDPATH + "/pic_" + time.replace(" ", "") + ".jpg";
		return imagePath;
	}

	/**
	 * @Description: 获取SDCard的目录路径功能
	 */
	public static String getSDCardPath() {
		String SDCardPath = null;
		if (hasSdcard()) {
			// SD卡的路径
			SDCardPath = Environment.getExternalStorageDirectory().toString(); // /mnt/sdcard
		}
		return SDCardPath;
	}

	/**
	 * 
	 * @Description: 判断存储卡是否可以用
	 */
	public static boolean hasSdcard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @Description: 将Bitmap转JPEG文件
	 * @param: Bitmap
	 * @param: ImagePath
	 */
	@SuppressWarnings("unused")
	public static void saveBitmap(Bitmap bm, String imagePath) {
		try {
			if (!isFileExist("")) {
				File tempf = createSDDir("");
			}
			File f = new File(imagePath);
			if (f.exists()) {
				f.delete();
			}
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @Description: 创建子目录
	 * @param: dirName
	 */
	public static File createSDDir(String dirName) throws IOException {
		File dir = new File(SDPATH + dirName);
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
		}
		return dir;
	}

	/**
	 * 
	 * @Description: 判断文件是否存在
	 * @param: fileName
	 */
	public static boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		file.isFile();
		return file.exists();
	}

	/**
	 * 
	 * @Description: 判断文件是否存在
	 * @param: fileName
	 */
	public static boolean isFilePathExist(String fileName) {
		File file = new File(fileName);
		file.isFile();
		return file.exists();
	}

	/**
	 * 
	 * @Description: 删除图片
	 * @param: fileName
	 * @throws:
	 */
	public static void delFile(String fileName) {
		File file = new File(SDPATH + fileName);
		if (file.isFile()) {
			file.delete();
		}
		file.exists();
	}

	/**
	 * 
	 * @Description: 删除文件夹
	 */
	public static void deleteDir() {
		File dir = new File(SDPATH);
		if (dir == null || !dir.exists() || !dir.isDirectory())
			return;

		for (File file : dir.listFiles()) {
			if (file.isFile())
				file.delete();
			else if (file.isDirectory())
				deleteDir();
		}
		dir.delete();
	}

	/**
	 * 
	 * @Description: 文件是否存在
	 * @param: path
	 */
	public static boolean fileIsExists(String path) {
		try {
			File f = new File(path);
			if (!f.exists()) {
				return false;
			}
		} catch (Exception e) {

			return false;
		}
		return true;
	}

	/**
	 * 
	 * @Description: 加载Assets文件夹下的图片
	 */
	public static Bitmap getImageFromAssetsFile(String fileName) {
		Bitmap image = null;
		AssetManager am = BaseApplication.getContext().getResources()
				.getAssets();
		try {
			InputStream is = am.open(fileName);
			image = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return image;

	}

	/**
	 * 读取Assets文件夹中的图片资源
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static Bitmap getImageFromAssetsFile(Context context, String fileName) {
		// 定义存放这些图片的内存路径
		String path = SDPATH;
		// 如果这个路径不存在则新建
		File file = new File(path);
		Bitmap image = null;
		boolean isExist = file.exists();
		if (!isExist) {
			file.mkdirs();
		}
		// 获取assets下的资源
		AssetManager am = context.getAssets();
		try {
			// 图片放在Assets文件夹下
			InputStream is = am.open(fileName);
			image = BitmapFactory.decodeStream(is);
			FileOutputStream out = new FileOutputStream(path + "/" + fileName);
			// 这个方法非常赞
			image.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
}
