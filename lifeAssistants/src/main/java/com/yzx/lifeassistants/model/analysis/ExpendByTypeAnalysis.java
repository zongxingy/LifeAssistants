package com.yzx.lifeassistants.model.analysis;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yzx.lifeassistants.bean.ExpendByType;
import com.yzx.lifeassistants.utils.LogcatUtils;

/**
 * @Description: 解析通过日期获取各个类型的支出总额
 * @author: yzx
 * @time: 2015-12-8 上午9:23:16
 */
public class ExpendByTypeAnalysis {
	/**
	 * @Description: 获取请求是否响应成功
	 */
	public static boolean getReqType(String data) {
		boolean isReqSuccess = false;
		try {
			JSONObject json = new JSONObject(data);
			Boolean success = json.getBoolean("sucess");
			if (success) {
				if (data.contains("error")) {
					JSONObject bodyObject = json.getJSONObject("body");
					String code = "";
					String error = "";
					if (!bodyObject.isNull("code")) {
						code = bodyObject.getString("code");
					}
					if ("101".equals(code)) {
						isReqSuccess = true;
					} else {
						if (!bodyObject.isNull("error")) {
							error = bodyObject.getString("error");
						}
						LogcatUtils.e("获取失败：" + code + " " + error);
					}
				} else {
					isReqSuccess = true;
				}
			} else {
				String message = json.getString("message");
				LogcatUtils.e(message);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return isReqSuccess;
	}

	/**
	 * @Description: 获取请求结果
	 */
	public static List<ExpendByType> getResponse(String data) {
		List<ExpendByType> response = new ArrayList<ExpendByType>();
		try {
			JSONObject json = new JSONObject(data);
			JSONObject body = json.getJSONObject("body");
			if (body.isNull("error")) {
				JSONArray result = body.getJSONArray("results");
				for (int i = 0; i < result.length(); i++) {
					JSONObject obj = result.getJSONObject(i);
					ExpendByType expendSum = new ExpendByType();
					expendSum.setType(obj.getString("type"));
					expendSum.setMoney(obj.getInt("_sumMoney"));
					response.add(expendSum);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return response;
	}
}
