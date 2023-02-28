package com.platform.order.product.service.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.platform.order.common.aop.redislock.RedLock;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductRedisService {
	private static final String WISH_RED_LOCK_PREFIX = "wishCount::";

	private final RedisTemplate<String, String> redisTemplate;

	@RedLock(keyPrefix = WISH_RED_LOCK_PREFIX, key = "{#productId}")
	public void increaseWishCount(Long productId) {
		redisTemplate.opsForZSet().incrementScore(ProductRedisManager.WISH.getKey(), productId.toString(), 1);
	}

}
