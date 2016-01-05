package com.yzx.lifeassistants.bean;

import cn.bmob.v3.BmobObject;

/**
 * @Description: 支出信息
 * @author: yzx
 * @time: 2015-11-27 下午2:21:34
 */
public class ExpendInfo extends BmobObject {

	/**
	 * @Description: TODO
	 */
	private static final long serialVersionUID = -5586588778148642166L;

	/**
	 * @Description: 金额
	 */
	private Integer money;

	/**
	 * @Description: 备注
	 */
	private String remark;

	/**
	 * @Description: 类型
	 */
	private String type;

	/**
	 * @Description: 日期
	 */
	private String date;

	/**
	 * @Description: 月份
	 */
	private String month;

	/**
	 * @Description: 年份
	 */
	private String year;

	/**
	 * @Description: 创建者
	 */
	private String username;

	public Integer getMoney() {
		return money;
	}

	public void setMoney(Integer money) {
		this.money = money;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

}
