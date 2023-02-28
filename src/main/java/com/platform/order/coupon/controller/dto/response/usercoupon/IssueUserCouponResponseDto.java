package com.platform.order.coupon.controller.dto.response.usercoupon;

import java.time.LocalDate;

import com.platform.order.coupon.domain.coupon.entity.CouponType;

public record IssueUserCouponResponseDto(
	Long id,
	Long userId,
	Long couponId,
	CouponType couponType,
	Long amount,
	LocalDate expiredAt,
	LocalDate issuedAt
) {

}
