package com.platform.order.review.service.redis;

import static com.platform.order.review.service.redis.ReviewRedisKeyManager.REVIEW_PRODUCT_KEY_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.platform.order.review.service.ReviewService;
import com.platform.order.testenv.IntegrationTest;

class ReviewRedisServiceTest extends IntegrationTest {

	@Autowired
	ReviewRedisService reviewRedisService;

	@Autowired
	RedisTemplate<String, String> redisTemplate;

	@Autowired
	ReviewService reviewService;
	Long productId = 1L;
	String reviewProductCountKey = REVIEW_PRODUCT_KEY_PREFIX + productId.toString();

	@BeforeEach
	public void setUp(){
		Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().flushAll();
	}

	@Test
	@DisplayName("상품리뷰를 작성할 때마다 리뷰가 카운팅된다.")
	void testIncreaseReviewProductCount() {

		Long initialReviewCount = 10L;
		redisTemplate.opsForValue().set(reviewProductCountKey, initialReviewCount.toString());
		//given
		reviewRedisService.increaseReviewCount(productId);
		//when
		long count = reviewRedisService.getCount(productId);
		//then
		assertThat(count).isEqualTo(initialReviewCount + 1);
	}

	@Test
	@DisplayName("상품리뷰 평균 점수를 가져온다.")
	void testGetReviewProductAverage() {
		//given
		long reviewCount = 5;
		redisTemplate.opsForValue().set(reviewProductCountKey, String.valueOf(reviewCount));
		int totalScore = IntStream.rangeClosed(1, 5)
			.peek(score -> reviewRedisService.increaseReviewScore(productId, score))
			.sum();
		int expectedAverage = (int)(totalScore / reviewCount);
		//when
		int average = reviewRedisService.getAverage(productId, reviewCount);
		//then
		assertThat(average).isEqualTo(expectedAverage);
	}

}