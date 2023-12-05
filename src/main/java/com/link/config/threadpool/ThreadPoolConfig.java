package com.link.config.threadpool;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ScheduledExecutorTask;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author : wangaidong
 * @date : 2023/11/29 13:36
 * @description : 线程池配置
 */
@Configuration
public class ThreadPoolConfig {

	@Bean(initMethod = "initialize", destroyMethod = "shutdown")
	public ThreadPoolTaskExecutor localCacheThreadPool() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(10);
		threadPoolTaskExecutor.setMaxPoolSize(20);
		threadPoolTaskExecutor.setKeepAliveSeconds(3600);
		threadPoolTaskExecutor.setThreadNamePrefix("local-cache-thread-pool");
		threadPoolTaskExecutor.setQueueCapacity(2000);
		return threadPoolTaskExecutor;
	}

	@Bean(initMethod = "initialize", destroyMethod = "shutdown")
	public ThreadPoolTaskExecutor bizThreadPool() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(10);
		threadPoolTaskExecutor.setMaxPoolSize(20);
		threadPoolTaskExecutor.setKeepAliveSeconds(3600);
		threadPoolTaskExecutor.setThreadNamePrefix("biz-thread-pool");
		threadPoolTaskExecutor.setQueueCapacity(2000);
		return threadPoolTaskExecutor;
	}

	@Bean(destroyMethod = "shutdown")
	public ScheduledThreadPoolExecutor scheduledThreadPoolExecutor() {
		return new ScheduledThreadPoolExecutor(3, r -> new Thread(r, "scheduled-thread-pool"));
	}

}
