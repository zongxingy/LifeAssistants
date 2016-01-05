package com.yzx.lifeassistants.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.content.Context;

/**
 * @Description: 读取配置文件工具类
 * @author: yzx
 * @time: 2015-11-23 上午8:51:12
 */
public class PropertyUtil {
	/**
	 * @Description: 配置文件存放在assets文件夹
	 */
	public static Properties getProperties(Context context, String fileName) {
		Properties props = new Properties();
		InputStream in = null;
		try {
			in = context.getAssets().open(fileName);
			props.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return props;
	}

	/**
	 * @Description: 配置文件存放在res下的raw文件夹
	 */
	public static Properties getProperties(Context context, int resId) {
		Properties props = new Properties();
		InputStream in = null;
		try {
			in = context.getResources().openRawResource(resId);
			props.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return props;
	}

	/**
	 * @Description: 配置文件存放在assets文件夹,不传上下文环境
	 */
	public static Properties getAssetsProperties(String fileName) {
		Properties props = new Properties();
		InputStream in = null;
		try {
			in = PropertyUtil.class.getResourceAsStream("/assets/" + fileName);
			props.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return props;
	}

	/**
	 * @Description: 配置文件存放在res下的raw文件夹,不传上下文环境
	 */
	public static Properties getRawProperties(String fileName) {
		Properties props = new Properties();
		InputStream in = null;
		try {
			in = PropertyUtil.class.getResourceAsStream("/res/raw/" + fileName);
			props.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return props;
	}
}
