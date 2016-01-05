package com.yzx.lifeassistants.model.callback;

import java.util.List;

import com.yzx.lifeassistants.bean.ExpendByType;

/**
 * @Description: 通过日期获取各个类型的支出总额回调
 * @author: yzx
 * @time: 2015-12-8 上午9:19:41
 */
public interface ISelectExpendByTypeCB {
	/**
	 * @Description: 获取成功
	 */
	public static final int SELECT_SUCCESS = 0;

	/**
	 * @Description: 获取失败
	 */
	public static final int SELECT_ERROR = 1;

	/**
	 * @Description: 网络连接错误/超时
	 */
	public static final int NET_ERROR = 2;

	/**
	 * @Description: 获取失败
	 */
	public static final int RESULR_NULL = 3;

	/**
	 * 
	 * @Description: 获取成功回调
	 */
	void selectSuccess(List<ExpendByType> expendList);

	/**
	 * 
	 * @Description: 获取失败回调
	 */
	void selectError(int error);
}
