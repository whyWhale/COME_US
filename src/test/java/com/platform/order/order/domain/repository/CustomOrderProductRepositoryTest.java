package com.platform.order.order.domain.repository;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.platform.order.env.RepositoryTest;
import com.platform.order.order.domain.entity.OrderEntity;
import com.platform.order.order.domain.entity.OrderProductEntity;
import com.platform.order.product.domain.entity.ProductEntity;
import com.platform.order.product.domain.repository.ProductRepository;
import com.platform.order.user.domain.entity.Role;
import com.platform.order.user.domain.entity.UserEntity;
import com.platform.order.user.domain.repository.UserRepository;

class CustomOrderProductRepositoryTest extends RepositoryTest {

	@Autowired
	OrderProductRepository orderProductRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	OrderRepository orderRepository;

	UserEntity user;
	ProductEntity productA;
	ProductEntity productB;
	OrderEntity createdOrder;

	@BeforeEach
	public void setUp() {
		user = userRepository.save(UserEntity.builder()
			.email("whywhale@cocoa.com")
			.username("whyWhale")
			.password("1")
			.nickName("whale")
			.role(Role.USER)
			.build());
		productA = productRepository.save(ProductEntity.builder()
			.name("testA")
			.price(10000L)
			.quantity(5L)
			.build());
		productB = productRepository.save(ProductEntity.builder()
			.name("testB")
			.price(1000L)
			.quantity(5L)
			.build());
		createdOrder = orderRepository.save(OrderEntity.create(user, "서울특별시 강남구 강남동", "123-123"));
	}

	@AfterEach
	public void setDown() {
		orderProductRepository.deleteAllInBatch();
		orderRepository.deleteAllInBatch();
		productRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();
	}

	@DisplayName("주문 상품 테이블 벌크 연산 테스트")
	@Test
	void saveAllInBulk() {
		/// given
		List<OrderProductEntity> orderProducts = List.of(OrderProductEntity.builder()
				.product(productA)
				.order(createdOrder)
				.price(productA.getPrice())
				.orderQuantity(1L)
				.build(),
			OrderProductEntity.builder()
				.product(productB)
				.order(createdOrder)
				.price(productB.getPrice())
				.orderQuantity(1L)
				.build());
		// when
		List<OrderProductEntity> createdOrderProducts = orderProductRepository.saveAllInBulk(orderProducts);
		List<OrderProductEntity> foundOrderProductEntities = orderProductRepository.findAll();
		// then
		Assertions.assertThat(createdOrderProducts.size()).isEqualTo(foundOrderProductEntities.size());
	}
}