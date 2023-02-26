package com.platform.order.coupon.controller.dto.response;

import java.time.LocalDate;

import com.platform.order.coupon.domain.entity.CouponType;

public record ReadCouponResponseDto(Long id,
									Long couponId,
									CouponType couponType,
									Long amount,
									LocalDate expiredAt,
									LocalDate issuedAt,
									boolean isUsable) {
}
