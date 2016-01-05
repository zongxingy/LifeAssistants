package com.yzx.lifeassistants.bean;

import cn.bmob.v3.BmobObject;

/**
 * @Description: 意见反馈
 * @author: yzx
 * @time: 2015-9-21 下午3:41:13
 */
@SuppressWarnings("serial")
public class Feedback extends BmobObject {
	/**
	 * @Description: 反馈信息
	 */
	private String content;
	/**
	 * @Description: 联系方式
	 */
	private String contact;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

}
