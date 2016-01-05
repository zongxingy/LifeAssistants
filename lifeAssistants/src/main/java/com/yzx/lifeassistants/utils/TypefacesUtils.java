package com.yzx.lifeassistants.utils;

import java.util.Hashtable;

import android.content.Context;
import android.graphics.Typeface;

/**
 * 
 * @Description: 加载字体
 * @author: yzx
 * @date: 2015-11-26
 */
public class TypefacesUtils {
	private static final String TAG = "Typefaces";
	private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();

	/**
	 * 
	 * @Description: 加载Assets文件夹下的字体
	 */
	public static Typeface get(Context c, String assetPath) {
		synchronized (cache) {
			if (!cache.containsKey(assetPath)) {
				try {
					Typeface t = Typeface.createFromAsset(c.getAssets(),
							assetPath);
					cache.put(assetPath, t);
				} catch (Exception e) {
					LogcatUtils.e(TAG, "Could not get typeface '" + assetPath
							+ "' because " + e.getMessage());
					return null;
				}
			}

			return cache.get(assetPath);
		}
	}
}