package com.link.constants.lambda;

import com.alibaba.fastjson.JSONObject;
import com.github.benmanes.caffeine.cache.Cache;
import com.link.constants.constant.RedisConstant;
import com.link.model.entity.LinkDO;
import com.link.model.bo.CacheLinkBO;
import com.link.utils.convert.ConvertUtil;
import com.link.utils.lambda.ThreeConsumer;
import com.link.utils.lambda.ThreeFunction;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.Function;


/**
 * @author : wangaidong
 * @Date : 2023/11/29 19:42
 * @Description : lambda
 */
public class LinkLambda {

	/**
	 * 短链缓存key拼接
	 */
	public static final Function<String, String> SHORT_LINK_KEY_APPEND = shortLink -> {
		return RedisConstant.SHORT_LINK_PREFIX + shortLink;
	};

	/**
	 * 长链缓存key拼接
	 */
	public static final Function<String, String> LONG_LINK_KEY_APPEND = longLink -> {
		return RedisConstant.LONG_LINK_PREFIX + longLink;
	};

	/**
	 * 从本地缓存获取数据
	 */
	public static final BiFunction<Cache<String, CacheLinkBO>, String, CacheLinkBO> LOCAL_CACHE_GET = (cache, key) -> {
		CacheLinkBO cacheLinkBO = cache.getIfPresent(key);
		if (cacheLinkBO == null) {
			// 缓存中不存在
			return null;
		}

		LocalDateTime expireTime = cacheLinkBO.getExpireTime();
		if (expireTime != null && expireTime.isBefore(LocalDateTime.now())) {
			// 存在但已过期
			cache.invalidate(key);
			return null;
		}

		return cacheLinkBO;
	};

	/**
	 * 写入数据到本地缓存
	 */
	public static final ThreeConsumer<Cache<String, CacheLinkBO>, String, LinkDO> LOCAL_CACHE_SET = (cache, key, value) -> {
		cache.put(key, ConvertUtil.linkDoConvertToCacheLinkBo(value));
	};

	/**
	 * 从redis获取数据
	 */
	public static final ThreeFunction<RedissonClient, Cache<String, CacheLinkBO>, String, CacheLinkBO> REDIS_CACHE_GET = (redissonClient, localCache, key) -> {
		RBucket<String> bucket = redissonClient.getBucket(key);
		if (!bucket.isExists() || bucket.remainTimeToLive() <= 0) {
			// 缓存中不存在
			return null;
		}

		CacheLinkBO cacheLinkBO = JSONObject.parseObject(bucket.get(), CacheLinkBO.class);
		LocalDateTime expireTime = cacheLinkBO.getExpireTime();
		if (expireTime != null && expireTime.isBefore(LocalDateTime.now())) {
			// 存在但已过期
			bucket.delete();
			return null;
		}

		// 添加到本地缓存
		localCache.put(key, cacheLinkBO);

		return cacheLinkBO;
	};

	/**
	 * 写入redis
	 */
	public static final ThreeConsumer<RedissonClient, String, LinkDO> REDIS_CACHE_SET = (redisCache, key, value) -> {
		RBucket<String> bucket = redisCache.getBucket(key);
		bucket.set(JSONObject.toJSONString(ConvertUtil.linkDoConvertToCacheLinkBo(value)), Duration.ofMinutes(RedisConstant.REDIS_CACHE_EXPIRE_TIME + ThreadLocalRandom.current().nextInt(360)));
	};

}
