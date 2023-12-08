package com.link.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : wangaidong
 * @date : 2023/12/8 18:00
 * @description : xxljob
 */
@Configuration
public class XxlJobConfig {

	@Value("${xxl.job.admin.addresses}")
	private String adminAddresses;

	@Value("${spring.application.name}")
	private String appname;

	@Value("${xxl.job.executor.logpath}")
	private String logPath;

	@Value("${xxl.job.executor.logretentiondays}")
	private int logRetentionDays;

	@Bean
	public XxlJobSpringExecutor xxlJobExecutor() {
		XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
		xxlJobSpringExecutor.setAdminAddresses(adminAddresses);
		xxlJobSpringExecutor.setAppname(appname);
		xxlJobSpringExecutor.setLogPath(logPath);
		xxlJobSpringExecutor.setLogRetentionDays(logRetentionDays);
		return xxlJobSpringExecutor;
	}

}
