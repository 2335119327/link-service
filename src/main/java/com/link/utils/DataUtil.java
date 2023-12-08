package com.link.utils;

import java.util.function.Function;

/**
 * @author : wangaidong
 * @date : 2023/12/8 18:25
 */
public class DataUtil {

	public static <T> T getOrDefault(T t, T defaultVal) {
		if (t == null) {
			return defaultVal;
		}

		return t;
	}

	public static <T> T getOrDefault(T t, Function<T, Boolean> function, T defaultVal) {
		if (function.apply(t)) {
			return defaultVal;
		}

		return t;
	}

}
