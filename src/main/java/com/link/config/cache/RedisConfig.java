package com.link.config.cache;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author : wangaidong
 * @date : 2023/11/28 14:50
 * @description redis config
 */
@Configuration
public class RedisConfig {

	@Bean
	public RedissonClient redissonClient() {
		Config config = new Config();
		config.setCodec(StringCodec.INSTANCE);
		SingleServerConfig singleServerConfig = config.useSingleServer();
		singleServerConfig.setAddress("redis://127.0.0.1:6379");
		return Redisson.create(config);
	}

}
