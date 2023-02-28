package com.platform.order.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.platform.order.testenv.IntegrationTest;
import com.platform.order.order.controller.dto.request.CreateOrderRequestDto;
import com.platform.order.order.controller.dto.response.CreateOrderResponseDto;
import com.platform.order.order.domain.orderproduct.repository.OrderProductRepository;
import com.platform.order.order.domain.order.repository.OrderRepository;
import com.platform.order.order.service.OrderService;
import com.platform.order.product.domain.product.entity.ProductEntity;
import com.platform.order.product.domain.product.repository.ProductRepository;
import com.platform.order.user.domain.entity.Role;
import com.platform.order.user.domain.entity.UserEntity;
import com.platform.order.user.domain.repository.UserRepository;

public class OrderIntegrationTest extends IntegrationTest {
	@Autowired
	OrderService orderService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	OrderProductRepository orderProductRepository;
	@Autowired
	OrderRepository orderRepository;
	ProductEntity product;
	UserEntity user;
	String address = "서울특별시 강남구 강남동";
	String zipCode = "123-12";

	@BeforeEach
	public void setUp() {
		user = userRepository.save(UserEntity
			.builder()
			.email("orderIntegration@google.com")
			.username("orderIntegration")
			.nickName("order")
			.role(Role.USER)
			.password("1")
			.build());

		product = productRepository.save(ProductEntity.builder()
			.name("test상품1")
			.quantity(10L)
			.price(4500L)
			.isDisplay(true)
			.build());
	}

	@AfterEach
	public void setDown() {
		orderProductRepository.deleteAllInBatch();
		productRepository.deleteAllInBatch();
		orderRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();
	}

	@Test
	@DisplayName("상품을 주문하다.")
	void testOrder() {
		//given
		Long orderQuantity = 1L;
		var orderProductsRequest = List.of(
			new CreateOrderRequestDto.OrderProductRequestDto(product.getId(), orderQuantity)
		);
		CreateOrderRequestDto requestDto = new CreateOrderRequestDto(orderProductsRequest, address, zipCode);
		//when
		CreateOrderResponseDto createOrderResponse = orderService.placeOrder(user.getId(), requestDto);
		// then
		assertThat(createOrderResponse.orderId()).isNotNull();
		assertThat(createOrderResponse.orderProducts().size()).isEqualTo(orderProductsRequest.size());
		assertThat(createOrderResponse.orderProducts().get(0).productId()).isEqualTo(product.getId());
		assertThat(createOrderResponse.orderProducts().get(0).orderQuantity()).isEqualTo(orderQuantity);
		assertThat(createOrderResponse.orderProducts().get(0).price()).isEqualTo(product.getPrice());
	}
}
