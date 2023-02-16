package com.platform.order.order.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import com.platform.order.user.domain.entity.UserEntity;
import com.platform.order.user.domain.repository.UserRepository;

@SpringBootTest
class ProductStockServiceTest {
	@Autowired
	ProductStockService productStockService;

	@Autowired
	RedissonClient redissonClient;

	@Autowired
	RedisTemplate<String, String> redisTemplate;

	@Autowired
	UserRepository userRepository;

	@Test
	@DisplayName("모의 데이터 테스트")
	void test() {
		//given
		List<UserEntity> all = userRepository.findAll();
		//when

		System.out.println(all.size());
		System.out.println(all);
		//then
	}

	@Test
	@DisplayName("동시성 테스트")
	void testConcurrency() throws InterruptedException {
		//given
		// productStockService.decrease(1L, Map.of(1L, 1L));
		//when

		final int totalThread = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(totalThread);
		CountDownLatch countDownLatch = new CountDownLatch(totalThread);

		// productStockService.decrease("product::1",Map.of(1L,86L));
		IntStream.range(0, totalThread)
			.forEach(e -> {
				executorService.submit(() -> {
					try {
						productStockService.decrease("", Map.of(1L, 10L));
					} catch (RuntimeException r) {
						System.out.println("시발 못삿노...");
					} finally {
						countDownLatch.countDown();
					}
				});
			});

		countDownLatch.await();

		System.out.println(redisTemplate.opsForValue().get("product::1"));
	}

}