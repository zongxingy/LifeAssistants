package com.yzx.lifeassistants.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: yzx
 * @date: 2015-9-14 下午4:38:14
 * @Description: 数据验证工具类
 */
public class VerifyUtils {

	/**
	 * @Description: 验证邮箱的有效性
	 * @param: email
	 * @return
	 */
	public static boolean checkEmail(String email) {

		String mailRegex = "^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$";
		return email.matches(mailRegex);
	}

	/**
	 * @Description: 验证手机号码的有效性
	 * @param: email
	 * @return
	 */
	public static boolean checkPhone(String phone) {
		Pattern pattern = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher matcher = pattern.matcher(phone);

		if (matcher.matches()) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @Description: 验证一个字符串是否都为数字
	 */
	public static boolean isDigit(String strNum) {
		Pattern pattern = Pattern.compile("[0-9]{1,}");
		Matcher matcher = pattern.matcher((CharSequence) strNum);
		return matcher.matches();
	}
}
