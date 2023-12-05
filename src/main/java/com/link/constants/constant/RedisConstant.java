package com.link.constants.constant;

import static com.link.constants.constant.LinkConstant.SERVICE_PREFIX;

/**
 * @author : wangaidong
 * @date : 2023/12/5 16:11
 */
public class RedisConstant {

	/**
	 * redis缓存过期时间（分钟）
	 */
	public static final Integer REDIS_CACHE_EXPIRE_TIME = 360;

	/**
	 * 批次缓存大小
	 */
	public static final Integer BATCH_CACHE_SIZE = 500;

	/**
	 * 服务计数器续期时间
	 */
	public static final Integer RENEWAL_TIME = 120000;

	/**
	 * short link prefix
	 */
	public static final String SHORT_LINK_PREFIX = SERVICE_PREFIX + "short_link_prefix:";

	/**
	 * long link prefix
	 */
	public static final String LONG_LINK_PREFIX = SERVICE_PREFIX + "long_link_prefix:";

	/**
	 * short link batch prefix
	 */
	public static final String SHORT_LINK_BATCH_PREFIX = SERVICE_PREFIX + "short_link_batch_prefix:";

	/**
	 * long link batch prefix
	 */
	public static final String LONG_LINK_BATCH_PREFIX = SERVICE_PREFIX + "long_link_batch_prefix:";

	/**
	 * short link cache set
	 */
	public static final String SHORT_LINK_CACHE_SET = SERVICE_PREFIX + "short_link_cache_set:";

	/**
	 * long link cache set
	 */
	public static final String LONG_LINK_CACHE_SET = SERVICE_PREFIX + "long_link_cache_set:";

	/**
	 * batch count
	 */
	public static final String BATCH_COUNT = SERVICE_PREFIX + "batch_count";

	/**
	 * service count
	 */
	public static final String SERVICE_COUNT = SERVICE_PREFIX + "service_count";

	/**
	 * service count set
	 */
	public static final String SERVICE_COUNT_SET = SERVICE_PREFIX + "service_count_set";

}
