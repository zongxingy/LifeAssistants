package com.yzx.lifeassistants.model;

/**
 * @Description: 通过日期获取各个类型的支出总额
 * @author: yzx
 * @time: 2015-12-8 上午9:14:21
 */
public interface ISelectExpendByType {
	/**
	 * 
	 * @Description: 通过日期范围获取支出总额
	 */
	void selectExpendByType(String username, String date);
}
