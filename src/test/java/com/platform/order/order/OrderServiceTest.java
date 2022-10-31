package com.platform.order.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
class OrderServiceTest {

	@Autowired
	private OrderService orderService;

	@Autowired
	private ProductRepository productRepository;

	@AfterEach
	@Test
	@DisplayName("테스트 데이터 삭제")
	void deleteAllTestDatas() {
		productRepository.deleteAll();
	}

	@Test
	@DisplayName("재고 감소에 성공한다.[싱글 스레드]")
	void testPurchase() {
		//given
		Product testProduct = getTestProduct(20L);

		//when
		int orderAmount = 10;
		orderService.purchase(testProduct.getId(), orderAmount);

		//then
		assertThat(
			orderService.findById(testProduct.getId()).getQuantity()
		).isEqualTo(testProduct.getQuantity()-orderAmount);
	}

	@DisplayName("재고량 보다 더 많이 주문할 수 없다.")
	@Test
	void testPurchaseGraterThanStock() {
		/// given
		Product testProduct = getTestProduct(10L);

		// when
		// then
		assertThatThrownBy(() ->
			orderService.purchase(testProduct.getId(), 200))
			.isInstanceOf(RuntimeException.class);
	}

	@Test
	@DisplayName("재고 감소 테스트에 동시성 이슈가 발생한다. [멀티 스레드]")
	void testMultiThreadPurchase() throws InterruptedException {
		//given
		Product testProduct = getTestProduct(2001L);

		final int totalThread = 200;
		ExecutorService executorService = Executors.newFixedThreadPool(totalThread);
		CountDownLatch countDownLatch = new CountDownLatch(totalThread);

		//when
		IntStream.range(0, totalThread)
			.forEach(e -> {
				executorService.submit(() -> {
					try {
						orderService.purchase(testProduct.getId(), 10);
					} finally {
						countDownLatch.countDown();
					}
				});
			});

		countDownLatch.await();

		//then
		Product product = orderService.findById(testProduct.getId());

		assertThat(product.getQuantity()).isNotEqualTo(1);
	}

	@Test
	@DisplayName("멀티 스레드 재고 감소 동시성 이슈 해결 (pessimistic lock)")
	void testMultiThreadPurchaseForUpdate() throws InterruptedException {
		//given
		Product testProduct = getTestProduct(2001L);

		final int totalThread = 200;
		ExecutorService executorService = Executors.newFixedThreadPool(totalThread);
		CountDownLatch countDownLatch = new CountDownLatch(totalThread);

		//when
		IntStream.range(0, totalThread)
			.forEach(e -> {
				executorService.submit(() -> {
					try {
						orderService.purchaseByPessimisticLock(testProduct.getId(), 10);
					} finally {
						countDownLatch.countDown();
					}
				});
			});

		countDownLatch.await();

		//then
		Product product = orderService.findById(testProduct.getId());

		assertThat(product.getQuantity()).isEqualTo(1);
	}

	@Test
	@DisplayName("멀티 스레드 재고 감소 동시성 이슈 해결 (Native Query)")
	void testMultiThreadPurchaseForNative() throws InterruptedException {
		//given
		Product testProduct = getTestProduct(2001L);

		final int totalThread = 200;
		ExecutorService executorService = Executors.newFixedThreadPool(totalThread);
		CountDownLatch countDownLatch = new CountDownLatch(totalThread);

		//when
		IntStream.range(0, totalThread)
			.forEach(e -> {
				executorService.submit(() -> {
					try {
						orderService.purchaseByNative(testProduct.getId(), 10);
					} finally {
						countDownLatch.countDown();
					}
				});
			});

		countDownLatch.await();

		//then
		Product product = orderService.findById(testProduct.getId());
		assertThat(product.getQuantity()).isEqualTo(1);
	}

	public Product getTestProduct(Long quantity) {
		Product testProduct = Product.builder()
			.name("테스트상품")
			.quantity(quantity)
			.build();

		return productRepository.save(testProduct);
	}
}