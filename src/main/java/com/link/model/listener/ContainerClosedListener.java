package com.link.model.listener;

import com.alibaba.fastjson.JSONObject;
import com.github.benmanes.caffeine.cache.Cache;
import com.link.constants.constant.RedisConstant;
import com.link.model.bo.CacheLinkBO;
import com.link.utils.collection.MapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.*;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * @author : wangaidong
 * @date : 2023/11/30 20:24
 * @description : 监听容器关闭事件
 */
@Slf4j
@Component
public class ContainerClosedListener implements ApplicationListener<ContextClosedEvent> {

	@Resource
	private Cache<String, CacheLinkBO> shortSuffixCache;

	@Resource
	private Cache<String, CacheLinkBO> longLinkCache;

	@Resource
	private RedissonClient redissonClient;

	@Override
	public void onApplicationEvent(@NonNull ContextClosedEvent event) {
		try {
			// 申请当前服务计数器
			long curServiceCount = applicationServiceCount();

			// 处理短链本地缓存
			saveLocalCacheToRedis(curServiceCount, shortSuffixCache.asMap(), RedisConstant.SHORT_LINK_BATCH_PREFIX, RedisConstant.SHORT_LINK_CACHE_SET);

			// 处理长链本地缓存
			saveLocalCacheToRedis(curServiceCount, longLinkCache.asMap(), RedisConstant.LONG_LINK_BATCH_PREFIX, RedisConstant.LONG_LINK_CACHE_SET);
		} catch (Exception e) {
			log.error("containerClosedListener onApplicationEvent error", e);
		}
	}

	private void saveLocalCacheToRedis(long curServiceCount,
	                                   Map<String, CacheLinkBO> localCacheMap,
	                                   String batchKeyPrefix,
	                                   String cacheSetKey) {
		// 分批次
		List<Map<String, CacheLinkBO>> partitionLinkMap = MapUtils.partition(localCacheMap, RedisConstant.BATCH_CACHE_SIZE);
		if (CollectionUtils.isEmpty(partitionLinkMap)) {
			return;
		}
		log.info("saveLocalCacheToRedis, partitionMapSize={}, batchKeyPrefix={}, cacheSetKey={}", partitionLinkMap.size(), batchKeyPrefix, cacheSetKey);

		// 申请分布式下的批次计数器，一次性申请当前批次所需要的数
		long count = applicationBatchCount(partitionLinkMap.size());

		// 创建批次
		RBatch batch = redissonClient.createBatch();

		// 服务批次set
		RSetAsync<String> serviceBatchSet = batch.getSet(cacheSetKey + curServiceCount);

		for (Map<String, CacheLinkBO> cacheMap : partitionLinkMap) {
			if (MapUtils.isEmpty(cacheMap)) {
				continue;
			}

			// 批次key
			String batchKey = batchKeyPrefix + count--;
			serviceBatchSet.addAsync(batchKey);

			// 当前批次set
			RSetAsync<String> setBatch = batch.getSet(batchKey);

			for (CacheLinkBO cacheLinkBO : cacheMap.values()) {
				if (cacheLinkBO == null) {
					continue;
				}
				setBatch.addAsync(JSONObject.toJSONString(cacheLinkBO));
			}

			setBatch.expireAsync(Duration.ofHours(1));
		}

		serviceBatchSet.expireAsync(Duration.ofHours(1));
		// 执行批次
		batch.execute();
	}

	/**
	 * 申请分布式下的批次计数器
	 *
	 * @param size 批次总数
	 */
	private long applicationBatchCount(int size) {
		RAtomicLong atomicLong = redissonClient.getAtomicLong(RedisConstant.BATCH_COUNT);
		if (!atomicLong.isExists()) {
			atomicLong.expire(Duration.ofHours(1));
		} else {
			// 存活时间（毫秒）
			long survivalTime = atomicLong.remainTimeToLive();
			if (survivalTime <= RedisConstant.RENEWAL_TIME) {
				atomicLong.expire(Duration.ofHours(1));
			}
		}

		return atomicLong.addAndGet(size);
	}

	/**
	 * 申请当前服务计数器
	 */
	private Long applicationServiceCount() {
		RAtomicLong atomicLong = redissonClient.getAtomicLong(RedisConstant.SERVICE_COUNT);
		if (!atomicLong.isExists()) {
			atomicLong.set(1L);
			atomicLong.expire(Duration.ofHours(2));
		}

		// 当前服务计数
		Long curServiceCount = atomicLong.getAndIncrement();

		RSet<String> serviceCountSet = redissonClient.getSet(RedisConstant.SERVICE_COUNT_SET);
		serviceCountSet.add(String.valueOf(curServiceCount));
		if (!serviceCountSet.isExists() || serviceCountSet.remainTimeToLive() <= 0) {
			serviceCountSet.expire(Duration.ofHours(1));
		}

		return curServiceCount;
	}

}
