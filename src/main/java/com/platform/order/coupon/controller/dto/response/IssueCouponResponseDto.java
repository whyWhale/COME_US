package com.platform.order.coupon.controller.dto.response;

import java.time.LocalDate;

import com.platform.order.coupon.domain.entity.CouponType;

public record IssueCouponResponseDto(
	Long id,
	Long userId,
	Long couponId,
	CouponType couponType,
	Long amount,
	LocalDate expiredAt,
	LocalDate issuedAt
) {

}
