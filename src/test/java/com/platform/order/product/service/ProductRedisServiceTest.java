package com.platform.order.product.service;

import static com.platform.order.product.service.redis.ProductRedisManager.WISH;
import static org.assertj.core.api.Assertions.assertThat;

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
		ExecutorService executorService = Executors.newFixedThreadPool(32);
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
			.score(WISH.getKey(), productId.toString())
			.intValue();
		assertThat(wishCount).isEqualTo(request);
	}

	@Test
	@DisplayName("찜 상품 1 감소하기")
	void testMinusCounting() throws InterruptedException {
		//given
		int request = 30;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
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
			.score(WISH.getKey(), productId.toString())
			.intValue();
		assertThat(wishCount).isEqualTo(request * -1);
	}
}