package com.platform.order.product.service;

import static com.platform.order.product.service.redis.ProductRedisKeyManager.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.junit.jupiter.api.AfterEach;
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

	@AfterEach
	public void setDown() {
		redisTemplate.delete(SORTED_SET_WISH.getKey());
		redisTemplate.delete(SORTED_SET_VIEW.getKey());
		redisTemplate.delete(SET_VIEW.getKey());
	}

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

	@Test
	@DisplayName("가장 많은 찜 상품 10개 가져오기")
	void testGetMaximumWishProducts() {
		//given
		List<Long> productIds = LongStream.rangeClosed(1, 20)
			.boxed()
			.collect(Collectors.toList());
		productIds.forEach(productId -> {
			long wishCount = productId;
			redisTemplate.opsForZSet().incrementScore(SORTED_SET_WISH.getKey(), productId.toString(), wishCount);
		});
		List<Long> expectedProductIds = productIds.subList(10, productIds.size());
		//when
		List<Long> maximumWishProduct = productRedisService.getMaximumWishProducts();
		//then
		assertThat(maximumWishProduct.size()).isEqualTo(10);
		assertThat(maximumWishProduct).containsAll(expectedProductIds);
	}

	@Test
	@DisplayName("가장 많은 조회수를 가진 상품 10개 가져오기")
	void testGetMaximumReadProducts() {
		//given
		List<Long> productIds = LongStream.rangeClosed(1, 20)
			.boxed()
			.collect(Collectors.toList());
		productIds.forEach(productId -> {
			long readCount = productId;
			redisTemplate.opsForZSet().incrementScore(SORTED_SET_WISH.getKey(), productId.toString(), readCount);
		});
		List<Long> expectedProductIds = productIds.subList(10, productIds.size());
		//when
		List<Long> maximumWishProduct = productRedisService.getMaximumReadProducts();
		//then
		assertThat(maximumWishProduct.size()).isEqualTo(10);
		assertThat(maximumWishProduct).containsAll(expectedProductIds);
	}
}