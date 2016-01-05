package com.yzx.lifeassistants.model.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

import com.yzx.lifeassistants.bean.ExpendByRange;
import com.yzx.lifeassistants.model.ISelectExpendByRange;
import com.yzx.lifeassistants.model.analysis.ExpendByRangeAnalysis;
import com.yzx.lifeassistants.model.callback.ISelectExpendByRangeCB;
import com.yzx.lifeassistants.utils.HttpClientUtil;
import com.yzx.lifeassistants.utils.IHttpCallBack;
import com.yzx.lifeassistants.utils.LogcatUtils;
import com.yzx.lifeassistants.utils.PropertyUtil;

/**
 * @Description: 通过日期范围获取支出总额
 * @author: yzx
 * @time: 2015-12-7 下午4:20:22
 */
public class SelectExpendByRangeImpl implements ISelectExpendByRange {

	private String url;// URL
	private ISelectExpendByRangeCB selectCallBack;// 回调

	public SelectExpendByRangeImpl(Context context,
			ISelectExpendByRangeCB selectCallBack) {
		this.selectCallBack = selectCallBack;
		Properties properties = PropertyUtil.getProperties(context,
				"base.properties");
		url = properties.getProperty("search_expend_by_range_url");
	}

	/**
	 * 
	 * @Description: 通过日期范围获取支出总额
	 */
	@Override
	public void selectExpendByRange(String username, String date,
			final String range) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("username", username));
		nameValuePairs.add(new BasicNameValuePair("date", date));
		nameValuePairs.add(new BasicNameValuePair("range", range));
		HttpClientUtil.sendRequest(url, nameValuePairs, new IHttpCallBack() {

			@Override
			public void onFinish(String response) {
				LogcatUtils.i(response);
				boolean isReqSuccess = ExpendByRangeAnalysis
						.getReqType(response);
				if (isReqSuccess) {// 请求响应成功
					LogcatUtils.i("请求响应成功");
					List<ExpendByRange> expendSumList = ExpendByRangeAnalysis
							.getResponse(response, range);
					if (null == expendSumList || 1 > expendSumList.size()) {
						LogcatUtils.e("获取数据为空");
						selectCallBack.selectError(
								ISelectExpendByRangeCB.RESULR_NULL, range);
					} else {
						LogcatUtils.i("获取数据成功");
						selectCallBack.selectSuccess(expendSumList, range);
					}
				} else {// 请求响应失败
					LogcatUtils.e("请求响应失败");
					selectCallBack.selectError(
							ISelectExpendByRangeCB.SELECT_ERROR, range);
				}
			}

			@Override
			public void onError(Exception e) {
				LogcatUtils.e("获取数据失败");
				if (null == e.getMessage()) {
					selectCallBack.selectError(
							ISelectExpendByRangeCB.NET_ERROR, range);
				} else {
					LogcatUtils.e(e.getMessage());
					selectCallBack.selectError(
							ISelectExpendByRangeCB.SELECT_ERROR, range);
				}

			}
		});
	}

}
