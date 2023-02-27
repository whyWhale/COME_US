package com.platform.order.common.aop.redislock;

import static com.platform.order.common.utils.SpelUtils.getDynamicValue;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class RedissionLockAop {

	private final RedissonClient redissonClient;
	private final RedisTransaction redisTransaction;

	@Around("@annotation(com.platform.order.common.aop.redislock.RedLock)")
	public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		Method method = signature.getMethod();
		RedLock distributeLock = method.getAnnotation(RedLock.class);

		String key = distributeLock.keyPrefix() + getDynamicValue(signature.getParameterNames(),
			joinPoint.getArgs(), distributeLock.key());

		RLock rLock = redissonClient.getLock(key);

		try {
			boolean available = rLock.tryLock(distributeLock.waitTime(), distributeLock.leaseTime(), distributeLock.timeUnit());
			if (!available) {
				return false;
			}

			return redisTransaction.proceed(joinPoint);
		} catch (Exception e) {
			Thread.currentThread().interrupt();
			throw new InterruptedException();
		} finally {
			rLock.unlock();
		}
	}
}
