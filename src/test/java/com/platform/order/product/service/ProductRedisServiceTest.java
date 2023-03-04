package com.platform.order.product.service;

import static com.platform.order.product.service.redis.ProductRedisManager.SET_VIEW;
import static com.platform.order.product.service.redis.ProductRedisManager.SORTED_SET_WISH;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.LongStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.platform.order.product.service.redis.ProductRedisService;
import com.platform.order.testenv.IntegrationTest;

class ProductRedisServiceTest extends IntegrationTest {

	@Autowired
	ProductRedisService productRedisService;

	@Autowired
	RedisTemplate<String, String> redisTemplate;

	@Test
	@DisplayName("찜 상품 1 카운팅하기")
	void testPlusCounting() throws InterruptedException {
		//given
		int request = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		CountDownLatch latch = new CountDownLatch(request);
		Long productId = 1L;
		//when
		LongStream.rangeClosed(1, request).forEach(value -> {
			executorService.submit(() -> {
					try {
						productRedisService.increaseWishCount(productId);
					} finally {
						latch.countDown();
					}
				}
			);
		});

		latch.await();
		//then
		int wishCount = redisTemplate.opsForZSet()
			.score(SORTED_SET_WISH.getKey(), productId.toString())
			.intValue();
		assertThat(wishCount).isEqualTo(request);
	}

	@Test
	@DisplayName("찜 상품 1 감소하기")
	void testMinusCounting() throws InterruptedException {
		//given
		int request = 30;
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		CountDownLatch latch = new CountDownLatch(request);
		Long productId = 2L;
		//when
		LongStream.rangeClosed(1, request).forEach(value -> {
			executorService.submit(() -> {
					try {
						productRedisService.decreaseWishCount(productId);
					} finally {
						latch.countDown();
					}
				}
			);
		});

		latch.await();
		//then
		int wishCount = redisTemplate.opsForZSet()
			.score(SORTED_SET_WISH.getKey(), productId.toString())
			.intValue();
		assertThat(wishCount).isEqualTo(request * -1);
	}

	@Test
	@DisplayName("상품 조회수 1 증가하기")
	void testIncreaseViewCount() {
		//given
		Long productId = 1L;
		String cookieValue = UUID.randomUUID().toString();
		String key = SET_VIEW.getKey() + productId;
		//when
		productRedisService.increaseViewCount(productId, cookieValue);
		//then
		Set<String> members = redisTemplate.opsForSet().members(key);
		assertThat(members).isNotNull();
		assertThat(members.contains(cookieValue)).isTrue();
	}

	@Test
	@DisplayName("상품 조회수 중복 카운팅을 방지할 수 있다.")
	void testIncreaseViewCountWithDuplicate() throws InterruptedException {
		//given
		Long productId = 3L;
		String cookieValue = UUID.randomUUID().toString();
		String key = SET_VIEW.getKey() + productId;
		redisTemplate.delete(key);
		int request = 10;
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		CountDownLatch latch = new CountDownLatch(request);
		//when
		LongStream.rangeClosed(1, request).forEach(value -> {
			executorService.submit(() -> {
					try {
						productRedisService.increaseViewCount(productId, cookieValue);
					} finally {
						latch.countDown();
					}
				}
			);
		});

		latch.await();
		//then
		Set<String> members = redisTemplate.opsForSet().members(key);
		assertThat(members).isNotNull();
		assertThat(members).hasSize(1);
	}
}