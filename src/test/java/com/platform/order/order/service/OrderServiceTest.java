package com.platform.order.order.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.platform.order.common.exception.custom.BusinessException;
import com.platform.order.order.controller.dto.request.CreateOrderRequestDto;
import com.platform.order.order.controller.dto.response.CreateOrderResponseDto;
import com.platform.order.order.domain.entity.OrderEntity;
import com.platform.order.order.domain.repository.OrderProductRepository;
import com.platform.order.order.domain.repository.OrderRepository;
import com.platform.order.product.domain.entity.ProductEntity;
import com.platform.order.product.domain.repository.ProductRepository;
import com.platform.order.user.domain.entity.Role;
import com.platform.order.user.domain.entity.UserEntity;
import com.platform.order.user.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

	@InjectMocks
	OrderService orderService;
	@Mock
	UserRepository userRepository;
	@Mock
	OrderRepository orderRepository;
	@Mock
	OrderProductRepository orderProductRepository;
	@Mock
	ProductRepository productRepository;
	@Mock
	OrderMapper orderMapper;

	UserEntity user;
	ProductEntity product;
	Long buyingProductId = 1L;
	String address = "서울특별시 강남구 강남동";
	String zipCode = "123-12";

	@BeforeEach
	public void setUp() {
		String username = "whyWhale";
		user = UserEntity.builder()
			.email("whywhale@cocoa.com")
			.username("whyWhale")
			.password("1")
			.nickName("whale")
			.role(Role.USER)
			.build();

		product = ProductEntity.builder()
			.name("test상품")
			.price(10000L)
			.quantity(5L)
			.build();
	}

	@Test
	@DisplayName("상품을 주문하다")
	void testOrder() {
		//given
		Long orderQuantity = 1L;
		int orderPossibility = 1;
		var orderProductsRequest = List.of(
			new CreateOrderRequestDto.OrderProductRequestDto(buyingProductId, orderQuantity)
		);
		OrderEntity order = OrderEntity.create(user, address, zipCode);
		CreateOrderRequestDto createOrderRequest = new CreateOrderRequestDto(orderProductsRequest, address, zipCode);

		given(userRepository.findById(any())).willReturn(Optional.of(user));
		given(productRepository.updateQuantity(buyingProductId, orderQuantity)).willReturn(orderPossibility);
		given(orderRepository.save(any())).willReturn(order);
		given(productRepository.findByIdIn(Set.of(buyingProductId))).willReturn(List.of(product));

		//when
		CreateOrderResponseDto createOrderResponseDto = orderService.placeOrder(any(), createOrderRequest);

		//then
		verify(userRepository, times(1)).findById(any());
		verify(productRepository, times(1)).updateQuantity(buyingProductId, orderQuantity);
		verify(orderRepository, times(1)).save(any());
		verify(productRepository, times(1)).findByIdIn(any());
	}

	@Test
	@DisplayName("구입하려는 상품의 재고가 없다면 비즈니스 예외가 발생한다.")
	void testFailOrder() {
		//given
		int orderImPossibility = 0;
		Long orderQuantity = 10L;
		CreateOrderRequestDto createOrderRequest = new CreateOrderRequestDto(
			List.of(new CreateOrderRequestDto.OrderProductRequestDto(buyingProductId, orderQuantity)),
			address, zipCode
		);
		//when
		given(userRepository.findById(any(Long.class))).willReturn(Optional.of(user));
		given(productRepository.updateQuantity(any(Long.class), any(Long.class))).willReturn(orderImPossibility);

		//then
		Assertions.assertThatThrownBy(() -> orderService.placeOrder(1L, createOrderRequest))
			.isInstanceOf(BusinessException.class);
	}
}