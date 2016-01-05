package com.yzx.lifeassistants.utils;

/**
 * 
 * @Description: 请求回调接口
 * @author: yzx
 * @date: 2015-11-23
 */
public interface IHttpCallBack {

	/**
	 * @Description: 服务器成功响应请求时调用
	 */
	void onFinish(String response);

	/**
	 * @Description: 进行网络操作出现错误时调用
	 */
	void onError(Exception e);

}
