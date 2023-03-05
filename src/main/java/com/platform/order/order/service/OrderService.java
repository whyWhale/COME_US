package com.platform.order.order.service;

import static java.time.LocalDate.now;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.order.common.protocal.CursorPageResponseDto;
import com.platform.order.coupon.domain.usercoupon.entity.UserCouponEntity;
import com.platform.order.coupon.domain.usercoupon.repository.UserCouponRepository;
import com.platform.order.order.controller.dto.request.CreateOrderRequestDto;
import com.platform.order.order.controller.dto.request.CreateOrderRequestDto.OrderProductRequestDto;
import com.platform.order.order.controller.dto.request.OrderPageRequestDto;
import com.platform.order.order.controller.dto.response.CreateOrderResponseDto;
import com.platform.order.order.controller.dto.response.ReadMyOrderResponseDto;
import com.platform.order.order.domain.order.entity.OrderEntity;
import com.platform.order.order.domain.order.repository.OrderRepository;
import com.platform.order.order.domain.orderproduct.entity.OrderProductEntity;
import com.platform.order.order.domain.orderproduct.repository.OrderProductRepository;
import com.platform.order.product.domain.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class OrderService {
	private final OrderRepository orderRepository;
	private final OrderProductRepository orderProductRepository;
	private final ProductRepository productRepository;
	private final UserCouponRepository userCouponRepository;
	private final OrderMapper orderMapper;

	@Transactional
	public CreateOrderResponseDto order(
		Long authId,
		CreateOrderRequestDto creatOrderRequest
	) {
		Map<Long, OrderProductRequestDto> orderProductRequests = creatOrderRequest.orderProductRequests()
			.stream()
			.collect(Collectors.toMap(OrderProductRequestDto::productId, Function.identity()));
		List<Long> productIds = creatOrderRequest.orderProductRequests()
			.stream()
			.map(OrderProductRequestDto::productId)
			.toList();

		OrderEntity order = OrderEntity.create(authId, creatOrderRequest.address(), creatOrderRequest.zipCode());
		List<OrderProductEntity> orderProducts = productRepository.findByIdIn(productIds).stream()
			.map(product -> {
				var orderProductRequest = orderProductRequests.get(product.getId());

				return OrderProductEntity.create(product, orderProductRequest.orderQuantity());
			}).toList();
		order.addOrderProduct(orderProducts);

		List<Long> userCouponIds = creatOrderRequest.orderProductRequests()
			.stream()
			.filter(OrderProductRequestDto::hasCoupon)
			.map(OrderProductRequestDto::userCouponId)
			.toList();

		if (!userCouponIds.isEmpty()) {
			Map<Long, UserCouponEntity> foundUserCoupons = userCouponRepository.findByIdInWithCoupon(userCouponIds,
					now())
				.stream()
				.collect(Collectors.toMap(UserCouponEntity::getId, Function.identity()));

			orderProducts.forEach(orderProduct -> {
				var orderProductRequest = orderProductRequests.get(orderProduct.getProduct().getId());

				if (orderProductRequest.hasCoupon()) {
					UserCouponEntity userCoupon = foundUserCoupons.get(orderProductRequest.userCouponId());
					orderProduct.applyCoupon(userCoupon);
				}
			});
		}

		OrderEntity createdOrder = orderRepository.save(order);

		return orderMapper.toMultiCreateOrderResponseDto(createdOrder);
	}

	public CursorPageResponseDto<ReadMyOrderResponseDto> getMyOrders(
		Long authId,
		OrderPageRequestDto orderPageRequest
	) {
		var orderProducts = orderProductRepository.findMyAllWithConditions(authId, orderPageRequest);
		return orderMapper.toCursorPageResponse(orderProducts);
	}
}
