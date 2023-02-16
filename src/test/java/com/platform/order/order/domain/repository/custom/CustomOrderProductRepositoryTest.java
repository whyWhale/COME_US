package com.platform.order.order.domain.repository.custom;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.platform.order.config.TestJpaAuditConfig;
import com.platform.order.order.domain.entity.OrderEntity;
import com.platform.order.order.domain.entity.OrderProductEntity;
import com.platform.order.order.domain.repository.OrderProductRepository;
import com.platform.order.product.domain.entity.ProductEntity;
import com.platform.order.user.domain.entity.Role;
import com.platform.order.user.domain.entity.UserEntity;

@Import(TestJpaAuditConfig.class)
@DataJpaTest
class CustomOrderProductRepositoryTest {

	@Autowired
	OrderProductRepository orderProductRepository;
	@PersistenceContext
	EntityManager em;
	UserEntity user;
	ProductEntity productA;
	ProductEntity productB;
	private OrderEntity createdOrder;

	@BeforeEach
	public void setUp() {
		user = UserEntity.builder()
			.email("whywhale@cocoa.com")
			.username("whyWhale")
			.password("1")
			.nickName("whale")
			.role(Role.USER)
			.build();
		productA = ProductEntity.builder()
			.name("testA")
			.price(10000L)
			.quantity(5L)
			.build();

		productB = ProductEntity.builder()
			.name("testB")
			.price(1000L)
			.quantity(5L)
			.build();

		createdOrder = OrderEntity.create(user, "서울특별시 강남구 강남동", "123-123");

		em.persist(user);
		em.persist(productA);
		em.persist(productB);
		em.persist(createdOrder);

	}

	@DisplayName("주문 상품 테이블 벌크 연산 테스트")
	@Test
	void saveAllInBulk() {
		/// given
		OrderProductEntity orderProductA = OrderProductEntity.builder()
			.product(productA)
			.order(createdOrder)
			.price(productA.getPrice())
			.orderQuantity(1L)
			.build();
		OrderProductEntity orderProductB = OrderProductEntity.builder()
			.product(productB)
			.order(createdOrder)
			.price(productB.getPrice())
			.orderQuantity(1L)
			.build();
		List<OrderProductEntity> orderProducts = List.of(orderProductA, orderProductB);

		// when
		List<OrderProductEntity> createdOrderProducts = orderProductRepository.saveAllInBulk(orderProducts);
		List<OrderProductEntity> foundOrderProductEntities = orderProductRepository.findAll();

		// then
		Assertions.assertThat(createdOrderProducts.size()).isEqualTo(foundOrderProductEntities.size());
	}
}