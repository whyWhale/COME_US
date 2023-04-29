package com.platform.order.order.controller.dto.response;

import com.platform.order.coupon.domain.coupon.entity.CouponType;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReadMyOrderResponseDto(
	@Schema(description = "주문 상품 아이디")
	Long orderProductId,

	@Schema(description = "주문 상품 수량")
	Long orderQuantity,

	@Schema(description = "주문 상품 총 가격")
	Long totalPrice,

	@Schema(description = "주문한 상품 아이디")
	Long productId,

	@Schema(description = "주문한 상품 이름")
	String productName,

	@Schema(description = "주문한 상품 섬네일 경로")
	String productThumbnailPath,

	@Schema(description = "주문한 상품 가격")
	Long productPrice,

	@Schema(description = "주문한 상품 쿠폰 사용 여부")
	boolean isUseCoupon,

	@Schema(description = "주문한 상품에 적용한 쿠폰 종류")
	CouponType couponType,

	@Schema(description = "주문한 상품에 적용한 쿠폰 할인금액 및 비율")
	Long amount
) {
}
