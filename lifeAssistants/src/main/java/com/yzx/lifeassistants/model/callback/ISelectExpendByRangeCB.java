package com.yzx.lifeassistants.model.callback;

import java.util.List;

import com.yzx.lifeassistants.bean.ExpendByRange;

/**
 * @Description: 通过日期范围获取支出总额回调
 * @author: yzx
 * @time: 2015-12-7 下午3:52:07
 */
public interface ISelectExpendByRangeCB {
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
	void selectSuccess(List<ExpendByRange> expendList, String range);

	/**
	 * 
	 * @Description: 获取失败回调
	 */
	void selectError(int error, String range);
}
