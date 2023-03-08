package com.platform.order.order.controller.dto.response;

import java.util.List;

import com.platform.order.coupon.domain.coupon.entity.CouponType;

public record CreateOrderResponseDto(
	Long orderId,
	List<CreateOrderProductResponse> orderProductResponses
) {
	public record CreateOrderProductResponse(
		Long productId,
		Long orderQuantity,
		Long totalPrice,
		boolean isUseCoupon,
		CouponType couponType
	) {

	}
}
