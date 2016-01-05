package com.yzx.lifeassistants.bean;

/**
 * 
 * @Description: 类别
 * @author: yzx
 * @date: 2015-11-13
 */
public class ClassifyItem {
	/**
	 * @Description: 大类
	 */
	private String bigClass;
	/**
	 * @Description: 图片ID
	 */
	private int resID;
	/**
	 * @Description: 类别名称
	 */
	private String name;

	public ClassifyItem(String bigClass, int resID, String name) {
		this.bigClass = bigClass;
		this.resID = resID;
		this.name = name;
	}

	public String getBigClass() {
		return bigClass;
	}

	public void setBigClass(String bigClass) {
		this.bigClass = bigClass;
	}

	public int getResID() {
		return resID;
	}

	public void setResID(int resID) {
		this.resID = resID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
