package com.link.utils;


/**
 * @author : wangaidong
 * @date : 2023/11/28 16:22
 * @description : 断言工具类
 */
public class LinkAssert {

	public static void notEmpty(Object object, String message) {
		if (object == null) {
			throw new RuntimeException(message);
		}
	}

	public static void isTrue(boolean flag, String message) {
		if (!flag) {
			throw new RuntimeException(message);
		}
	}

}
