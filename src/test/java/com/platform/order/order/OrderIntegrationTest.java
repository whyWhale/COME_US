package com.platform.order.order;

import static com.platform.order.coupon.domain.coupon.entity.CouponType.PERCENT;
import static com.platform.order.order.controller.dto.request.CreateOrderRequestDto.OrderProductRequestDto;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.platform.order.coupon.domain.coupon.entity.CouponEntity;
import com.platform.order.coupon.domain.coupon.repository.CouponRepository;
import com.platform.order.coupon.domain.usercoupon.entity.UserCouponEntity;
import com.platform.order.coupon.domain.usercoupon.repository.UserCouponRepository;
import com.platform.order.order.controller.dto.request.CreateOrderRequestDto;
import com.platform.order.order.controller.dto.response.CreateOrderResponseDto;
import com.platform.order.order.domain.order.repository.OrderRepository;
import com.platform.order.order.domain.orderproduct.repository.OrderProductRepository;
import com.platform.order.order.service.OrderService;
import com.platform.order.product.domain.product.entity.ProductEntity;
import com.platform.order.product.domain.product.repository.ProductRepository;
import com.platform.order.testenv.IntegrationTest;
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
	CouponRepository couponRepository;

	@Autowired
	UserCouponRepository userCouponRepository;

	@Autowired
	OrderRepository orderRepository;

	ProductEntity product;
	UserEntity user;
	UserCouponEntity userCoupon;
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

		CouponEntity coupon = couponRepository.save(CouponEntity.builder()
			.amount(10L)
			.quantity(1L)
			.type(PERCENT)
			.expiredAt(LocalDate.now().plusMonths(1))
			.build());

		userCoupon = userCouponRepository.save(UserCouponEntity.builder()
			.user(user)
			.coupon(coupon)
			.issuedAt(LocalDate.now())
			.build());
	}

	@AfterEach
	public void setDown() {
		orderProductRepository.deleteAllInBatch();
		productRepository.deleteAllInBatch();
		orderRepository.deleteAllInBatch();
		userCouponRepository.deleteAllInBatch();
		couponRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();
	}

	@DisplayName("상품들을")
	@Nested
	class Order {
		Long orderQuantity = 1L;

		@Test
		@DisplayName("쿠폰 없이 주문한다.")
		void testOrder() {
			//given
			var orderProductRequest = new OrderProductRequestDto(product.getId(), orderQuantity, null);
			var requestDto = new CreateOrderRequestDto(address, zipCode, List.of(orderProductRequest));
			//when
			CreateOrderResponseDto createOrderResponse = orderService.order(user.getId(), requestDto);
			// then
			assertThat(createOrderResponse.orderId()).isNotNull();
			assertThat(createOrderResponse.orderProductResponses().get(0).productId()).isEqualTo(product.getId());
			assertThat(createOrderResponse.orderProductResponses().get(0).orderQuantity()).isEqualTo(orderQuantity);
			assertThat(createOrderResponse.orderProductResponses().get(0).totalPrice()).isEqualTo(product.getPrice());
			assertThat(createOrderResponse.orderProductResponses().get(0).isUseCoupon()).isFalse();
			assertThat(createOrderResponse.orderProductResponses().get(0).couponType()).isNull();
		}

		@Test
		@DisplayName("쿠폰을 사용하여 주문한다.")
		void testOrderWithCoupon() {
			//given
			var orderProductRequest = new OrderProductRequestDto(product.getId(), orderQuantity, userCoupon.getId());
			var requestDto = new CreateOrderRequestDto(address, zipCode, List.of(orderProductRequest));
			//when
			CreateOrderResponseDto createOrderResponse = orderService.order(user.getId(), requestDto);
			// then
			assertThat(createOrderResponse.orderId()).isNotNull();
			assertThat(createOrderResponse.orderProductResponses().get(0).productId()).isEqualTo(product.getId());
			assertThat(createOrderResponse.orderProductResponses().get(0).orderQuantity()).isEqualTo(orderQuantity);
			assertThat(createOrderResponse.orderProductResponses().get(0).totalPrice()).isGreaterThan(product.getPrice());
			assertThat(createOrderResponse.orderProductResponses().get(0).isUseCoupon()).isTrue();
			assertThat(createOrderResponse.orderProductResponses().get(0).couponType())
				.isEqualTo(userCoupon.getCoupon().getType());
		}

	}

}
