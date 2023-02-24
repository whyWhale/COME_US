package com.platform.order.coupon.controller.dto.response;

import java.time.LocalDate;

import com.platform.order.coupon.domain.entity.CouponType;

public record CreateCouponResponseDto(
	Long id,
	CouponType couponType,
	Long amount,
	Long quantity,
	LocalDate expiredAt
) {
}
