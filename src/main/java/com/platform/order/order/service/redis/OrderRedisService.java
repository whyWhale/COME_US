package com.platform.order.order.service.redis;

import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.platform.order.common.aop.redislock.RedLock;
import com.platform.order.order.controller.dto.request.Location;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OrderRedisService {
	private static final String ORDER_RED_LOCK_PREFIX = "lock::product::order::";
	private final RedisTemplate<String, String> redisTemplate;

	@RedLock(keyPrefix = ORDER_RED_LOCK_PREFIX, key = "{#productId}")
	public void increaseOrderByRegion(Long productId, Location location) {
		redisTemplate.opsForZSet().incrementScore(location.city(), productId.toString(), 1);
		redisTemplate.opsForZSet().incrementScore(location.toStringUntilCountry(), productId.toString(), 1);
		redisTemplate.opsForZSet().incrementScore(location.toStringUntilDistrict(), productId.toString(), 1);
	}

	@RedLock(keyPrefix = ORDER_RED_LOCK_PREFIX, key = "{#productId}")
	public void decreaseOrderByRegion(Long productId, Location location) {
		redisTemplate.opsForZSet().incrementScore(location.city(), productId.toString(), -1);
		redisTemplate.opsForZSet().incrementScore(location.toStringUntilCountry(), productId.toString(), -1);
		redisTemplate.opsForZSet().incrementScore(location.toStringUntilDistrict(), productId.toString(), -1);
	}

	public List<Long> getMaximumOrderProductByRegionCity(Location location) {
		return redisTemplate.opsForZSet().reverseRange(location.city(), 0, 9).stream()
			.map(Long::parseLong)
			.toList();
	}

	public List<Long> getMaximumOrderProductByRegionCountry(Location location) {
		return redisTemplate.opsForZSet().reverseRange(location.toStringUntilCountry(), 0, 9).stream()
			.map(Long::parseLong)
			.toList();
	}

	public List<Long> getMaximumOrderProductByRegionDistrict(Location location) {
		return redisTemplate.opsForZSet().reverseRange(location.toStringUntilDistrict(), 0, 9).stream()
			.map(Long::parseLong)
			.toList();
	}

}
