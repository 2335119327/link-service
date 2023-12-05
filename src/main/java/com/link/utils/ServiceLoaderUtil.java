package com.link.utils;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.ServiceLoader;

/**
 * spi util
 *
 * @author : wangaidong
 * @date : 2023/5/6 11:07
 */
public class ServiceLoaderUtil {

	public static <T> Collection<T> load(final Class<T> service) {
		Collection<T> result = new LinkedHashSet<>();
		for (T each : ServiceLoader.load(service)) {
			result.add(each);
		}

		return result;
	}

}
