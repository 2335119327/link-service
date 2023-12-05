package com.link.utils;


import java.util.*;

/**
 * @author : wangaidong
 * @date : 2023/12/1 10:20
 * @description : map工具类
 */
public class MapUtils {

	/**
	 * map 分区
	 */
	public static <K, V> List<Map<K, V>> partition(Map<K, V> originMap, int batchSize) {
		if (originMap == null || originMap.isEmpty()) {
			return Collections.emptyList();
		}

		// 原始map大小
		int originSize = originMap.size();

		List<Map<K, V>> list = new ArrayList<>(originSize / batchSize);
		if (originSize <= batchSize) {
			list.add(originMap);
			return list;
		}

		int count = 0;
		Map<K, V> tmpMap = new HashMap<>(batchSize);
		for (Map.Entry<K, V> entry : originMap.entrySet()) {
			tmpMap.put(entry.getKey(), entry.getValue());
			count++;
			if (count >= batchSize) {
				list.add(tmpMap);
				tmpMap = new HashMap<>(batchSize);
				count = 0;
			}
		}

		if (tmpMap.size() > 0) {
			list.add(tmpMap);
		}
		return list;
	}

	public static <K, V> boolean isEmpty(Map<K, V> map) {
		if (map == null || map.size() == 0) {
			return true;
		}

		return false;
	}

}
