package com.platform.order.review.service.redis;

import static com.platform.order.review.service.redis.ReviewRedisKeyManager.REVIEW_PRODUCT_KEY_PREFIX;
import static java.lang.Enum.valueOf;
import static java.text.MessageFormat.format;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import com.platform.order.common.aop.redislock.RedLock;
import com.platform.order.common.exception.custom.ExteriorException;
import com.platform.order.common.exception.model.ErrorCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ReviewRedisService {

	private static final String REVIEW_RED_LOCK_PREFIX = "lock::review::product::";
	private final RedisTemplate<String, String> redisTemplate;

	@RedLock(keyPrefix = REVIEW_RED_LOCK_PREFIX, key = "{#productId +'::'+ #score}")
	public void increaseReviewScore(Long productId, int score) {
		String reviewScoreKey = generateKey(productId, score);
		redisTemplate.opsForValue().increment(reviewScoreKey);
	}

	@RedLock(keyPrefix = REVIEW_RED_LOCK_PREFIX, key = "{#productId +'::'+ #score}")
	public void decreaseReviewScore(Long productId, int score) {
		String reviewScoreKey = generateKey(productId, score);
		redisTemplate.opsForValue().decrement(reviewScoreKey);
	}

	@RedLock(keyPrefix = REVIEW_RED_LOCK_PREFIX, key = "{#productId}")
	public void increaseReviewCount(Long productId) {
		String key = generateKey(productId);
		redisTemplate.opsForValue().increment(key);
	}

	public long getCount(Long productId) {
		String key = generateKey(productId);
		return Long.parseLong(
			Objects.requireNonNull(redisTemplate.opsForValue().get(key))
		);
	}

	public int getAverage(Long productId, Long count) {
		RedisSerializer<String> stringSerializer = redisTemplate.getStringSerializer();

		List<Long> scores = redisTemplate.executePipelined((RedisCallback<Object>)connection -> {
				IntStream.rangeClosed(1, 5)
					.forEach(score -> {
						String key = generateKey(productId, score);
						connection.stringCommands().get(Objects.requireNonNull(stringSerializer.serialize(key)));
					});

				return null;
			}).stream()
			.map(String::valueOf)
			.map(Long::parseLong)
			.toList();

		long totalScore = IntStream.rangeClosed(1, 5)
			.mapToLong(multiValue -> scores.get(multiValue - 1) * multiValue)
			.reduce(0, Long::sum);
		double avg = totalScore / (double)count;

		return (int)Math.round(avg);
	}


	private String generateKey(Long productId, int score) {
		return REVIEW_PRODUCT_KEY_PREFIX + productId.toString() + "::" + score;
	}

	private String generateKey(Long productId) {
		return REVIEW_PRODUCT_KEY_PREFIX + productId.toString();
	}
}
