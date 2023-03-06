package com.platform.order.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import com.platform.order.common.exception.custom.BusinessException;
import com.platform.order.coupon.domain.coupon.entity.CouponEntity;
import com.platform.order.coupon.domain.coupon.entity.CouponType;
import com.platform.order.coupon.domain.usercoupon.entity.UserCouponEntity;
import com.platform.order.coupon.domain.usercoupon.repository.UserCouponRepository;
import com.platform.order.order.controller.dto.request.CreateOrderRequestDto;
import com.platform.order.order.controller.dto.request.CreateOrderRequestDto.OrderProductRequestDto;
import com.platform.order.order.domain.order.entity.OrderEntity;
import com.platform.order.order.domain.order.entity.OrderStatus;
import com.platform.order.order.domain.order.repository.OrderRepository;
import com.platform.order.order.domain.orderproduct.entity.OrderProductEntity;
import com.platform.order.order.domain.orderproduct.repository.OrderProductRepository;
import com.platform.order.product.domain.product.entity.ProductEntity;
import com.platform.order.product.domain.product.repository.ProductRepository;
import com.platform.order.testenv.ServiceTest;
import com.platform.order.user.domain.entity.Role;
import com.platform.order.user.domain.entity.UserEntity;

class OrderServiceTest extends ServiceTest {

	@InjectMocks
	OrderService orderService;

	@Mock
	OrderRepository orderRepository;

	@Mock
	OrderProductRepository orderProductRepository;

	@Mock
	ProductRepository productRepository;

	@Mock
	UserCouponRepository userCouponRepository;

	@Mock
	OrderMapper orderMapper;

	UserEntity user;
	ProductEntity product;
	UserCouponEntity userCoupon;
	String address = "서울특별시 강남구 강남동";
	String zipCode = "123-12";
	OrderProductEntity orderProduct;

	@BeforeEach
	public void setUp() {
		user = UserEntity.builder()
			.email("whywhale@cocoa.com")
			.username("whyWhale")
			.password("1")
			.nickName("whale")
			.role(Role.USER)
			.build();
		ReflectionTestUtils.setField(user, "id", 1L);

		product = ProductEntity.builder()
			.name("test상품")
			.price(10000L)
			.quantity(5L)
			.build();
		ReflectionTestUtils.setField(product, "id", 1L);

		CouponEntity coupon = CouponEntity.builder()
			.type(CouponType.FIXED)
			.quantity(100L)
			.amount(10000L)
			.expiredAt(LocalDate.now().plusMonths(1))
			.build();

		userCoupon = UserCouponEntity.builder()
			.id(1L)
			.user(user)
			.coupon(coupon)
			.issuedAt(LocalDate.now())
			.build();

		orderProduct = OrderProductEntity.create(product, 2L);
		ReflectionTestUtils.setField(orderProduct, "id", 1L);
	}

	@Test
	@DisplayName("상품을 주문한다. [쿠폰 사용x]")
	void testOrder() {
		//given
		Long orderQuantity = 1L;
		var orderProductsRequest = new OrderProductRequestDto(product.getId(), orderQuantity, null);
		var createOrderRequest = new CreateOrderRequestDto(address, zipCode, List.of(orderProductsRequest));
		OrderEntity order = OrderEntity.create(user.getId(), address, zipCode);

		given(productRepository.findByIdIn(any())).willReturn(List.of(product));
		given(orderRepository.save(any())).willReturn(order);
		//when
		orderService.order(any(), createOrderRequest);
		//then
		verify(orderRepository, times(1)).save(any());
		verify(productRepository, times(1)).findByIdIn(any());
	}

	@Test
	@DisplayName("상품을 주문한다. [쿠폰 사용]")
	void testOrderWithCoupon() {
		//given
		Long orderQuantity = 1L;
		var orderProductsRequest = new OrderProductRequestDto(product.getId(), orderQuantity, userCoupon.getId());
		OrderEntity order = OrderEntity.create(user.getId(), address, zipCode);
		var createOrderRequest = new CreateOrderRequestDto(address, zipCode, List.of(orderProductsRequest));

		given(productRepository.findByIdIn(any())).willReturn(List.of(product));
		given(orderRepository.save(any())).willReturn(order);
		given(userCouponRepository.findByIdInWithCoupon(any(), any())).willReturn(List.of(userCoupon));
		//when
		orderService.order(any(), createOrderRequest);
		//then
		assertThat(userCoupon.isUsable()).isFalse();
		verify(orderRepository, times(1)).save(any());
		verify(productRepository, times(1)).findByIdIn(any());
		verify(userCouponRepository, times(1)).findByIdInWithCoupon(any(), any());
	}

	@Test
	@DisplayName("구입하려는 상품의 재고가 없다면 비즈니스 예외가 발생한다.")
	void testFailOrder() {
		//given
		Long orderQuantity = 10L;
		var orderProductRequest = new OrderProductRequestDto(product.getId(), orderQuantity, null);
		var createOrderRequest = new CreateOrderRequestDto(address, zipCode, List.of(orderProductRequest));
		//when
		given(productRepository.findByIdIn(any())).willReturn(List.of(product));
		//then
		assertThatThrownBy(() -> orderService.order(1L, createOrderRequest))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("주문 목록을 조회한다")
	void testGetMyOrders() {
		//given
		//when
		orderService.getMyOrders(any(), any());
		//then
		verify(orderProductRepository, times(1)).findMyAllWithConditions(any(), any());
	}

	@Test
	@DisplayName("주문을 취소한다")
	void testCancel() {
		//given
		Long orderProductId = 1L;
		Long expectedProductQuantity = orderProduct.getProduct().getQuantity();

		given(orderProductRepository.findByIdAndAuthId(any(), any())).willReturn(Optional.of(orderProduct));
		//when
		Long cancelId = orderService.cancel(user.getId(), orderProductId);
		//then
		assertThat(cancelId).isEqualTo(orderProductId);
		assertThat(orderProduct.getStatus()).isEqualTo(OrderStatus.CANCEL);
		assertThat(orderProduct.getProduct().getQuantity()).isGreaterThan(expectedProductQuantity);
		verify(orderProductRepository, times(1)).findByIdAndAuthId(orderProductId, user.getId());
	}

	@Test
	@DisplayName("쿠폰을 적용한 주문을 취소하면 쿠폰은 원래 사용가능한 상태로 돌아간다")
	void testCancelWithUsingCoupon() {
		//given
		Long orderProductId = 1L;
		Long expectedProductQuantity = orderProduct.getProduct().getQuantity();
		orderProduct.applyCoupon(userCoupon);

		given(orderProductRepository.findByIdAndAuthId(any(), any())).willReturn(Optional.of(orderProduct));
		//when
		Long cancelId = orderService.cancel(user.getId(), orderProductId);
		//then
		assertThat(cancelId).isEqualTo(orderProductId);
		assertThat(orderProduct.getStatus()).isEqualTo(OrderStatus.CANCEL);
		assertThat(orderProduct.getUserCoupon()).isNull();
		assertThat(userCoupon.isUsable()).isTrue();
		assertThat(orderProduct.getProduct().getQuantity()).isGreaterThan(expectedProductQuantity);
		verify(orderProductRepository, times(1)).findByIdAndAuthId(orderProductId, user.getId());
	}

	@Test
	@DisplayName("주문을 취소할 때 주문 상태가 ACCEPTED가 아니면 비즈니스 예외가 발생한다.")
	void failNotStatusAccept() {
		//given
		Long orderProductId = 1L;
		ReflectionTestUtils.setField(orderProduct, "status", OrderStatus.DELIVERING);
		given(orderProductRepository.findByIdAndAuthId(any(), any())).willReturn(Optional.of(orderProduct));
		//when
		//then
		assertThatThrownBy(() -> {
			orderService.cancel(user.getId(), orderProductId);
		}).isInstanceOf(BusinessException.class);
	}

}