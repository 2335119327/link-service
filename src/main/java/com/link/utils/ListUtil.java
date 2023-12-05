package com.link.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author : wangaidong
 * @date : 2023/12/5 17:48
 */
public class ListUtil {

	public static <T> List<T> newArrayList(T... array) {
		return new ArrayList<>(Arrays.asList(array));
	}

}
