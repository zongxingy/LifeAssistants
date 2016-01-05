package com.yzx.lifeassistants.bean;

/**
 * @Description: 一定日期范围的支出总额
 * @author: yzx
 * @time: 2015-12-7 下午4:09:08
 */
public class ExpendByRange {
	/**
	 * @Description: 日期范围
	 */
	private String range;

	/**
	 * @Description: 总额
	 */
	private Integer money;

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}

	public Integer getMoney() {
		return money;
	}

	public void setMoney(Integer money) {
		this.money = money;
	}

}
