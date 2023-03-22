package com.platform.order.order.service;

import static java.text.MessageFormat.format;
import static java.time.LocalDate.now;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.order.common.dto.cursor.CursorPageResponseDto;
import com.platform.order.common.exception.custom.NotFoundResourceException;
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
import com.platform.order.order.service.redis.OrderRedisService;
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
	private final OrderRedisService orderRedisService;
	private final OrderMapper orderMapper;

	@Transactional
	public CreateOrderResponseDto order(Long authId, CreateOrderRequestDto creatOrderRequest) {
		Map<Long, OrderProductRequestDto> orderProductRequests = creatOrderRequest.orderProductRequests().stream()
			.collect(Collectors.toMap(OrderProductRequestDto::productId, Function.identity()));
		List<Long> productIds = creatOrderRequest.orderProductRequests().stream()
			.map(CreateOrderRequestDto.OrderProductRequestDto::productId)
			.toList();

		OrderEntity order = OrderEntity.create(authId, creatOrderRequest.address(), creatOrderRequest.zipCode());
		List<OrderProductEntity> orderProducts = createOrderProducts(orderProductRequests, productIds);
		order.addOrderProduct(orderProducts);

		List<Long> userCouponIds = creatOrderRequest.orderProductRequests().stream()
			.filter(CreateOrderRequestDto.OrderProductRequestDto::hasCoupon)
			.map(CreateOrderRequestDto.OrderProductRequestDto::userCouponId)
			.toList();

		if (!userCouponIds.isEmpty()) {
			applyCoupon(orderProductRequests, orderProducts, userCouponIds);
		}

		OrderEntity savedOrder = orderRepository.save(order);
		productIds.forEach(productId ->orderRedisService.increaseOrderByRegion(productId,creatOrderRequest.location()));

		return orderMapper.toCreateOrderResponseDto(savedOrder);
	}

	public CursorPageResponseDto<ReadMyOrderResponseDto> getMyOrders(
		Long authId,
		OrderPageRequestDto orderPageRequest
	) {
		var orderProducts = orderProductRepository.findMyAllWithConditions(authId, orderPageRequest);

		return orderMapper.toCursorPageResponse(orderProducts);
	}

	@Transactional
	public Long cancel(Long authId, Long orderProductId) {
		OrderProductEntity orderProduct = orderProductRepository.findByIdAndAuthId(orderProductId, authId)
			.orElseThrow(() -> new NotFoundResourceException(
				format("orderProduct : {0}  and authId : {1} not found", orderProductId, authId)
			));
		OrderProductEntity cancelledOrderProduct = orderProduct.cancel();

		return cancelledOrderProduct.getId();
	}

	private void applyCoupon(
		Map<Long, OrderProductRequestDto> orderProductRequests,
		List<OrderProductEntity> orderProducts,
		List<Long> userCouponIds
	) {
		List<UserCouponEntity> foundUserCoupons = userCouponRepository.findByIdInWithCoupon(userCouponIds, now());
		Map<Long, UserCouponEntity> UserCoupons = foundUserCoupons.stream()
			.collect(Collectors.toMap(UserCouponEntity::getId, Function.identity()));

		orderProducts.forEach(orderProduct -> {
			var orderProductRequest = orderProductRequests.get(orderProduct.getProduct().getId());

			if (orderProductRequest.hasCoupon()) {
				UserCouponEntity userCoupon = UserCoupons.get(orderProductRequest.userCouponId());
				orderProduct.applyCoupon(userCoupon);
			}
		});
	}

	private List<OrderProductEntity> createOrderProducts(
		Map<Long, OrderProductRequestDto> orderProductRequests,
		List<Long> productIds
	) {
		return productRepository.findByIdIn(productIds).stream()
			.map(product -> {
				var orderProductRequest = orderProductRequests.get(product.getId());

				return OrderProductEntity.create(product, orderProductRequest.orderQuantity());
			}).toList();
	}
}
