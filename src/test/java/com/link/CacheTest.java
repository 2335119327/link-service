package com.link;

import com.alibaba.fastjson.JSONObject;
import com.github.benmanes.caffeine.cache.Cache;
import com.link.model.bo.CacheLinkBO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author : wangaidong
 * @date : 2023/11/29 17:59
 */
@Slf4j
@SpringBootTest
public class CacheTest {

	@Resource
	private Cache<String, CacheLinkBO> shortSuffixCache;

	@Resource
	private Cache<String, CacheLinkBO> longLinkCache;

	@Resource
	private RedissonClient redissonClient;

	@Test
	public void test() throws InterruptedException {
		CacheLinkBO cacheLinkBO1 = new CacheLinkBO();
		cacheLinkBO1.setLongLink("wfkajvfbw");
		cacheLinkBO1.setShortSuffix("obigoiabf");
		shortSuffixCache.put("wfkajvfbw", cacheLinkBO1);

		longLinkCache.put("obigoiabf", cacheLinkBO1);
	}


	@Test
	public void test2() {
		RBatch batch = redissonClient.createBatch();
		RSetAsync<String> set = batch.getSet("hahahahaha");

		set.addAsync("1");
		set.addAsync("2");
		set.addAsync("3");
		set.addAsync("4");

		// 执行批次
		BatchResult<?> execute = batch.execute();
		log.info("saveLocalCacheToRedis, executeResult={}", JSONObject.toJSONString(execute));
	}

	@Test
	public void batch() {
		RBatch batch = redissonClient.createBatch();
		for (int i = 0; i < 5; i++) {
			batch.getBucket(i + "").getAsync();
		}
		BatchResult<?> execute = batch.execute();
	}

}
