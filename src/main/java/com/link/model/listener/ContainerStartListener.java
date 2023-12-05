package com.link.model.listener;

import com.alibaba.fastjson.JSONObject;
import com.github.benmanes.caffeine.cache.Cache;
import com.link.constants.constant.RedisConstant;
import com.link.model.bo.CacheLinkBO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : wangaidong
 * @date : 2023/11/30 20:26
 * @description : 容器启动
 */
@Slf4j
@Component
public class ContainerStartListener implements ApplicationRunner {

	@Resource
	private Cache<String, CacheLinkBO> shortSuffixCache;

	@Resource
	private Cache<String, CacheLinkBO> longLinkCache;

	@Resource
	private RedissonClient redissonClient;

	@Override
	public void run(ApplicationArguments args) {
		try {
			RSet<String> serviceCountSet = redissonClient.getSet(RedisConstant.SERVICE_COUNT_SET);
			if (!serviceCountSet.isExists() || serviceCountSet.isEmpty()) {
				return;
			}

			// 随便返回一个服务计数
			long serviceCount = Long.parseLong(serviceCountSet.removeRandom());

			// 拿到短链缓存
			getLocalCacheFromRedis(serviceCount, shortSuffixCache, CacheLinkBO::getShortSuffix, RedisConstant.SHORT_LINK_CACHE_SET);

			// 拿到长链缓存
			getLocalCacheFromRedis(serviceCount, longLinkCache, CacheLinkBO::getLongLink, RedisConstant.LONG_LINK_CACHE_SET);
		} catch (Exception e) {
			log.error("ContainerStartListener error", e);
		}
	}

	private void getLocalCacheFromRedis(long serviceCount,
	                                    Cache<String, CacheLinkBO> localCache,
	                                    Function<CacheLinkBO, String> functionKey,
	                                    String cacheSetKey) {
		// 拿到这个服务的所属批次
		RSet<String> allBatchKeySet = redissonClient.getSet(cacheSetKey + serviceCount);

		RBatch batch = redissonClient.createBatch();
		for (String batchKey : allBatchKeySet) {
			// 缓存的数据
			RSetAsync<String> cacheData = batch.getSet(batchKey);
			cacheData.readAllAsync();
		}

		// 执行
		BatchResult<?> execute = batch.execute();
		if (execute == null || CollectionUtils.isEmpty(execute.getResponses())) {
			return;
		}

		List<Set<String>> resultList = (List<Set<String>>) execute.getResponses();
		localCache.putAll(resultList.stream()
				.flatMap(Collection::stream)
				.map(str -> JSONObject.parseObject(str, CacheLinkBO.class))
				.collect(Collectors.toMap(functionKey, Function.identity())));
	}


}
