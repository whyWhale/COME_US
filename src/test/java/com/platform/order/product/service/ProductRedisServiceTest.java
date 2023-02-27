package com.platform.order.product.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.LongStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.platform.order.env.IntegrationTest;
import com.platform.order.product.service.redis.ProductRedisManager;
import com.platform.order.product.service.redis.ProductRedisService;

class ProductRedisServiceTest extends IntegrationTest {

	@Autowired
	ProductRedisService productRedisService;

	@Autowired
	RedisTemplate<String, String> redisTemplate;

	@Test
	@DisplayName("찜 목록 카운팅하기")
	void testRedision() throws InterruptedException {
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
			.score(ProductRedisManager.WISH.getKey(), productId.toString())
			.intValue();
		assertThat(wishCount).isEqualTo(request);
	}
}