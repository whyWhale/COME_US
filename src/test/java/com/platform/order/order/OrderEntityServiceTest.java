package com.platform.order.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.platform.order.order.service.OrderService;

import com.platform.order.product.domain.respository.ProductRepository;

@ActiveProfiles("container")
@Testcontainers
@SpringBootTest
@WithMockUser
class OrderEntityServiceTest {

	@Autowired
	OrderService orderService;

	@Autowired
	ProductRepository productRepository;

	// @Test
	// @DisplayName("멀티 스레드 재고 감소 동시성 이슈 해결 (Native Query)")
	// void testMultiThreadPurchaseForNative() throws InterruptedException {
	// 	//given
	// 	long quantity = 10001L;
	// 	ProductEntity testProductEntity = getTestProduct(quantity);
	//
	// 	final int totalThread = 200;
	// 	ExecutorService executorService = Executors.newFixedThreadPool(totalThread);
	// 	CountDownLatch countDownLatch = new CountDownLatch(totalThread);
	//
	// 	//when
	// 	IntStream.range(0, totalThread)
	// 		.forEach(e -> {
	// 			executorService.submit(() -> {
	// 				try {
	// 					// orderService.purchaseByNative(testProductEntity.getId(), 10);
	// 				} finally {
	// 					countDownLatch.countDown();
	// 				}
	// 			});
	// 		});
	//
	// 	countDownLatch.await();
	//
	// 	//then
	// 	ProductEntity productEntity = orderService.findById(testProductEntity.getId());
	// 	assertThat(productEntity.getStock()).isEqualTo(quantity - (totalThread * 10));
	// }

}