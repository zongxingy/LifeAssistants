package com.yzx.lifeassistants.bean;

import cn.bmob.v3.BmobObject;

/**
 * @Description: 借出明细
 * @author: yzx
 * @time: 2015-9-24 下午3:21:42
 */
public class LendDetail extends BmobObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4599442910185909450L;
	/**
	 * @Description: 金额
	 */
	private String money;
	/**
	 * @Description: 姓名
	 */
	private String person;
	/**
	 * @Description: 备注
	 */
	private String remark;
	/**
	 * @Description: 时间
	 */
	private String time;
	/**
	 * @Description: 创建者
	 */
	private String username;

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
