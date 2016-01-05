package com.yzx.lifeassistants.bean;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * @author: yzx
 * @date: 2015-9-12 下午9:16:51
 * @Description: 用户信息
 */
@SuppressWarnings("serial")
public class User extends BmobUser {
	// 继承BmobUser类其中已有username,password字段
	// private String username;// 用户名
	// private String password;// 用户密码
	/**
	 * @Description: 昵称
	 */
	private String nick;

	/**
	 * @Description: 性别 女性为true
	 */
	private Boolean sex;

	/**
	 * @Description: 头像文件
	 */
	private BmobFile avatar;

	/**
	 * @Description: 生活费指标
	 */
	private Integer alimony;

	public User() {

	}

	public Integer getAlimony() {
		return alimony;
	}

	public void setAlimony(Integer alimony) {
		this.alimony = alimony;
	}

	public BmobFile getAvatar() {
		return avatar;
	}

	public void setAvatar(BmobFile avatar) {
		this.avatar = avatar;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public Boolean getSex() {
		return sex;
	}

	public void setSex(Boolean sex) {
		this.sex = sex;
	}

}
