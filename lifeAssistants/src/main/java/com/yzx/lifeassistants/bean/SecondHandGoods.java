package com.yzx.lifeassistants.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * @Description: 二手物品信息
 * @author: yzx
 * @time: 2015-11-16 上午10:06:09
 */
public class SecondHandGoods extends BmobObject {

	/**
	 * @Description: TODO
	 */
	private static final long serialVersionUID = -7364251609053205433L;
	/**
	 * @Description: 标题
	 */
	private String title;
	/**
	 * @Description: 类型
	 */
	private String type;
	/**
	 * @Description: 价格
	 */
	private String price;
	/**
	 * @Description: 描述
	 */
	private String describe;
	/**
	 * @Description: 图片列表
	 */
	private List<BmobFile> picFileList;
	/**
	 * @Description: 手机号码
	 */
	private String phone;
	/**
	 * @Description: QQ
	 */
	private String qq;
	/**
	 * @Description: 可否议价
	 */
	private Boolean bargain;
	/**
	 * @Description: 创建者用户名
	 */
	private String username;
	/**
	 * @Description: 创建者信息
	 */
	private User user;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public List<BmobFile> getPicFileList() {
		return picFileList;
	}

	public void setPicFileList(List<BmobFile> picFileList) {
		this.picFileList = picFileList;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public Boolean getBargain() {
		return bargain;
	}

	public void setBargain(Boolean bargain) {
		this.bargain = bargain;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
