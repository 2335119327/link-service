package com.link.config.aspect;

import com.link.config.annotation.AccessLimit;
import com.link.config.annotation.BlackAccessLimit;
import com.link.model.context.KeyParserContext;
import com.link.utils.parse.KeyParserRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.time.Duration;

/**
 * @author : wangaidong
 * @date : 2023/12/4 17:09
 */
@Component
@Slf4j
@Aspect
public class AccessLimitAspect {

	@Resource
	private RedissonClient redissonClient;

	@Pointcut("@annotation(com.link.config.annotation.AccessLimit)")
	public void accessLimitPointCut() {

	}

	@Around("accessLimitPointCut()")
	public Object aroundAccessLimit(ProceedingJoinPoint joinPoint) {
		Object result = null;
		String limitKey = null;
		try {
			Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
			AccessLimit accessLimit = method.getAnnotation(AccessLimit.class);

			// 解析key
			limitKey = KeyParserRegistry.parser(KeyParserContext.prepareKeyParserContext(accessLimit.limitKey(), accessLimit.parseType(), joinPoint));
			if (StringUtils.isEmpty(limitKey)) {
				return proceed(joinPoint);
			}

			// 检查是否限流、拉黑
			RAtomicLong count = redissonClient.getAtomicLong(limitKey);
			if (count.isExists() && count.get() >= accessLimit.frequency()) {
				BlackAccessLimit blackAccessLimit = method.getAnnotation(BlackAccessLimit.class);
				if (count.incrementAndGet() >= blackAccessLimit.frequency()) {
					String blackLimitKey = blackAccessLimit.blackKeyPrefix() + limitKey;
					RBucket<String> bucket = redissonClient.getBucket(blackLimitKey);
					if (bucket.isExists()) {
						return null;
					}

					bucket.set("black");
					bucket.expire(Duration.of(blackAccessLimit.cycle(), blackAccessLimit.timeUnit()));
				}
				return null;
			}

			// execute
			result = proceed(joinPoint);

			// 只针对返回null的行为限流
			if (result == null) {
				count.incrementAndGet();
				if (count.remainTimeToLive() <= 0) {
					count.expire(Duration.of(accessLimit.cycle(), accessLimit.timeUnit()));
				}
			}
		} catch (Exception e) {
			log.error("aroundAccessLimit error, limitKey={}", limitKey, e);
		}

		return result;
	}

	private Object proceed(ProceedingJoinPoint joinPoint) {
		try {
			return joinPoint.proceed();
		} catch (Throwable throwable) {
			log.error("[proceed business error], throwable:", throwable);
			return null;
		}
	}

}
