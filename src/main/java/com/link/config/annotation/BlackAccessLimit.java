package com.link.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * @author : wangaidong
 * @date : 2023/12/4 16:43
 * @description 访问限制
 */
@Target(value = {ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface BlackAccessLimit {

	/**
	 * blackKeyPrefix
	 */
	String blackKeyPrefix();

	/**
	 * 周期
	 */
	int cycle();

	/**
	 * 时间单位
	 */
	ChronoUnit timeUnit() default ChronoUnit.SECONDS;

	/**
	 * 频率
	 */
	int frequency();


}
