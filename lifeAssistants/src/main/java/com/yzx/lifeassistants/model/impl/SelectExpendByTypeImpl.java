package com.yzx.lifeassistants.model.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

import com.yzx.lifeassistants.bean.ExpendByType;
import com.yzx.lifeassistants.model.ISelectExpendByType;
import com.yzx.lifeassistants.model.analysis.ExpendByTypeAnalysis;
import com.yzx.lifeassistants.model.callback.ISelectExpendByTypeCB;
import com.yzx.lifeassistants.utils.HttpClientUtil;
import com.yzx.lifeassistants.utils.IHttpCallBack;
import com.yzx.lifeassistants.utils.LogcatUtils;
import com.yzx.lifeassistants.utils.PropertyUtil;

/**
 * @Description: 通过日期获取各个类型的支出总额
 * @author: yzx
 * @time: 2015-12-8 上午9:26:23
 */
public class SelectExpendByTypeImpl implements ISelectExpendByType {

	private String url;// URL
	private ISelectExpendByTypeCB selectCallBack;// 回调

	public SelectExpendByTypeImpl(Context context,
			ISelectExpendByTypeCB selectCallBack) {
		this.selectCallBack = selectCallBack;
		Properties properties = PropertyUtil.getProperties(context,
				"base.properties");
		url = properties.getProperty("search_expend_by_type_url");
	}

	/**
	 * 
	 * @Description: 通过日期范围获取支出总额
	 */
	@Override
	public void selectExpendByType(String username, String date) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("username", username));
		nameValuePairs.add(new BasicNameValuePair("date", date));
		HttpClientUtil.sendRequest(url, nameValuePairs, new IHttpCallBack() {

			@Override
			public void onFinish(String response) {
				LogcatUtils.i(response);
				boolean isReqSuccess = ExpendByTypeAnalysis
						.getReqType(response);
				if (isReqSuccess) {// 请求响应成功
					LogcatUtils.i("请求响应成功");
					List<ExpendByType> expendSumList = ExpendByTypeAnalysis
							.getResponse(response);
					if (null == expendSumList || 1 > expendSumList.size()) {
						LogcatUtils.e("获取数据为空");
						selectCallBack
								.selectError(ISelectExpendByTypeCB.RESULR_NULL);
					} else {
						LogcatUtils.i("获取数据成功");
						selectCallBack.selectSuccess(expendSumList);
					}
				} else {// 请求响应失败
					LogcatUtils.e("请求响应失败");
					selectCallBack
							.selectError(ISelectExpendByTypeCB.SELECT_ERROR);
				}
			}

			@Override
			public void onError(Exception e) {
				LogcatUtils.e("获取数据失败");
				if (null == e.getMessage()) {
					selectCallBack.selectError(ISelectExpendByTypeCB.NET_ERROR);
				} else {
					LogcatUtils.e(e.getMessage());
					selectCallBack
							.selectError(ISelectExpendByTypeCB.SELECT_ERROR);
				}

			}
		});
	}

}
