package com.platform.order.product.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.platform.order.env.RepositoryTest;
import com.platform.order.product.domain.entity.ProductEntity;

class ProductRepositoryTest extends RepositoryTest {

	@Autowired
	ProductRepository productRepository;

	ProductEntity product;

	@BeforeEach
	public void setUp() {
		product = ProductEntity.builder()
			.name("test상품")
			.price(10000L)
			.quantity(5L)
			.build();

		productRepository.save(product);
	}

	@Test
	@DisplayName("재고량 보다 적은 개수를 주문하면 1을 반환한다.")
	void testDecreaseQuantity() {
		//given
		//when
		boolean isPossibleOrder = productRepository.updateQuantity(product.getId(), product.getQuantity()) == 1;
		//then
		assertThat(isPossibleOrder).isTrue();
	}

	@Test
	@DisplayName("재고량 보다 더 많은 개수를 주문한다면 0을 반환한다.")
	void testFailDecreaseQuantity() {
		//given
		//when
		boolean isPossibleOrder = productRepository.updateQuantity(product.getId(), product.getQuantity() + 1) == 1;
		//then
		assertThat(isPossibleOrder).isFalse();
	}
}