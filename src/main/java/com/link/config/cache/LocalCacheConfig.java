package com.link.config.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.link.model.bo.CacheLinkBO;
import com.link.utils.ApplicationContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author : wangaidong
 * @date : 2023/11/28 14:50
 * @description : 本地缓存配置
 */
@Configuration
public class LocalCacheConfig {

	@Bean
	public Cache<String, CacheLinkBO> shortSuffixCache(RedissonClient redissonClient, ThreadPoolTaskExecutor localCacheThreadPool) {
		return Caffeine.newBuilder()
				.initialCapacity(1000)
				.maximumSize(5000)
				.executor(localCacheThreadPool)
				.expireAfterWrite(Duration.ofHours(6))
				.removalListener((k, v, cause) -> {
					handleLocalCacheRemoveEvent("shortSuffixCache", (String) k, (CacheLinkBO) v, cause, redissonClient);
				})
				.build();
	}

	@Bean
	public Cache<String, CacheLinkBO> longLinkCache(RedissonClient redissonClient, ThreadPoolTaskExecutor localCacheThreadPool) {
		return Caffeine.newBuilder()
				.initialCapacity(1000)
				.maximumSize(5000)
				.executor(localCacheThreadPool)
				.expireAfterWrite(Duration.ofHours(6))
				.removalListener((k, v, cause) -> {
					handleLocalCacheRemoveEvent("longLinkCache", (String) k, (CacheLinkBO) v, cause, redissonClient);
				})
				.build();
	}

	private void handleLocalCacheRemoveEvent(String beanName,
	                                         String key,
	                                         CacheLinkBO cacheLinkBO,
	                                         RemovalCause cause,
	                                         RedissonClient redissonClient) {
		if (RemovalCause.EXPIRED != cause || StringUtils.isBlank(key) || cacheLinkBO == null) {
			return;
		}

		LocalDateTime expireTime = cacheLinkBO.getExpireTime();
		if (expireTime != null && expireTime.isBefore(LocalDateTime.now())) {
			// 短链已经过期，需要删除redis缓存
			RBucket<String> bucket = redissonClient.getBucket(key);
			if (bucket.isExists()) {
				bucket.delete();
			}
			return;
		}

		// 重新加入到本地缓存
		Cache<String, CacheLinkBO> localCache = ApplicationContextUtil.getBean(beanName, Cache.class);
		localCache.put(key, cacheLinkBO);
	}

}
