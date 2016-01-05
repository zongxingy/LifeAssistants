package com.yzx.lifeassistants.model;

/**
 * @Description: 通过日期范围获取支出总额
 * @author: yzx
 * @time: 2015-12-7 下午3:49:18
 */
public interface ISelectExpendByRange {
	/**
	 * 
	 * @Description: 通过日期范围获取支出总额
	 */
	void selectExpendByRange(String username, String date, String range);
}
