package com.platform.order.order.controller.dto.response;

import com.platform.order.coupon.domain.coupon.entity.CouponType;

public record ReadMyOrderResponseDto(
	Long orderProductId,
	Long orderQuantity,
	Long totalPrice,
	Long productId,
	String productName,
	String productThumbnailPath,
	Long productPrice,
	boolean isUseCoupon,
	CouponType couponType,
	Long amount
) {
}
