package com.platform.order.product.service.redis;

import static com.platform.order.product.service.redis.ProductRedisKeyManager.*;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.platform.order.common.aop.redislock.RedLock;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductRedisService {
	private static final String WISH_RED_LOCK_PREFIX = "lock::product::wishCount::";
	private static final String VIEW_RED_LOCK_PREFIX = "lock::product::viewCount::";

	private final RedisTemplate<String, String> redisTemplate;

	@RedLock(keyPrefix = WISH_RED_LOCK_PREFIX, key = "{#productId}")
	public void increaseWishCount(Long productId) {
		redisTemplate.opsForZSet().incrementScore(SORTED_SET_WISH.getKey(), productId.toString(), 1);
	}

	@RedLock(keyPrefix = WISH_RED_LOCK_PREFIX, key = "{#productId}")
	public void decreaseWishCount(Long productId) {
		redisTemplate.opsForZSet().incrementScore(SORTED_SET_WISH.getKey(), productId.toString(), -1);
	}

	@RedLock(keyPrefix = VIEW_RED_LOCK_PREFIX, key = "{#productId}")
	public void increaseViewCount(Long productId, String visitor) {
		String productViewCountKey = generateKey(SET_VIEW, productId);
		int result = Objects.requireNonNull(
			redisTemplate.opsForSet().add(productViewCountKey, visitor)
		).intValue();

		if (result != 0) {
			redisTemplate.expire(productViewCountKey, 30, TimeUnit.MINUTES);
			redisTemplate.opsForZSet().incrementScore(SORTED_SET_VIEW.getKey(), productId.toString(), 1);
		}
	}

	public List<Long> getMaximumWishProducts() {
		return redisTemplate.opsForZSet().reverseRange(SORTED_SET_WISH.getKey(), 0, 9).stream()
			.map(Long::parseLong)
			.toList();
	}

	public List<Long> getMaximumReadProducts() {
		return redisTemplate.opsForZSet().reverseRange(SORTED_SET_WISH.getKey(), 0, 9).stream()
			.map(Long::parseLong)
			.toList();
	}

	private String generateKey(ProductRedisKeyManager redisKey, Long productId) {
		return redisKey.getKey() + productId;
	}

	public long getWishCount(Long productId) {
		Double score = redisTemplate.opsForZSet()
			.score(SORTED_SET_WISH.getKey(), productId.toString());

		return score == null ? 0 : score.longValue();
	}

}
