package com.yzx.lifeassistants.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * @Description: TODO
 * @author: yzx
 * @time: 2015-9-15 下午1:51:58
 */
public class FindThing extends BmobObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7224980509229779784L;
	/**
	 * @Description: 标题
	 */
	private String title;
	/**
	 * @Description: 类型
	 */
	private String type;
	/**
	 * @Description: 地点
	 */
	private String place;
	/**
	 * @Description: 时间
	 */
	private String time;
	/**
	 * @Description: 手机号码
	 */
	private String phone;
	/**
	 * @Description: 描述
	 */
	private String describe;
	/**
	 * @Description: 创建者用户名
	 */
	private String username;
	/**
	 * @Description: 创建者信息
	 */
	private User user;
	/**
	 * @Description: 图片列表
	 */
	private List<BmobFile> picFileList;

	public List<BmobFile> getPicFileList() {
		return picFileList;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setPicFileList(List<BmobFile> picList) {
		this.picFileList = picList;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

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

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

}
