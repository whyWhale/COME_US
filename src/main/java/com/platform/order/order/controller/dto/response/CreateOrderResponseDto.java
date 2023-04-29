package com.platform.order.order.controller.dto.response;

import java.util.List;

import com.platform.order.coupon.domain.coupon.entity.CouponType;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateOrderResponseDto(
	@Schema(description = "주문 아이디")
	Long orderId,

	List<CreateOrderProductResponse> orderProductResponses
) {
	public record CreateOrderProductResponse(
		@Schema(description = "주문 상품 아이디")
		Long productId,

		@Schema(description = "주문 상품 수량")
		Long orderQuantity,

		@Schema(description = "주문 상품 총 금액")
		Long totalPrice,

		@Schema(description = "쿠폰 사용 여부")
		boolean isUseCoupon,

		@Schema(description = "쿠폰 사용 종류")
		CouponType couponType
	) {

	}
}
