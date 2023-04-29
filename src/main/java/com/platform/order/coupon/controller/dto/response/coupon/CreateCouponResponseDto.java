package com.platform.order.coupon.controller.dto.response.coupon;

import java.time.LocalDate;

import com.platform.order.coupon.domain.coupon.entity.CouponType;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateCouponResponseDto(
	@Schema(description = "쿠폰 아이디")
	Long id,

	@Schema(description = "쿠폰 종류")
	CouponType couponType,

	@Schema(description = "쿠폰 금액 및 할일율")
	Long amount,

	@Schema(description = "쿠폰 재고량")
	Long quantity,

	@Schema(description = "쿠폰 만료일자")
	LocalDate expiredAt
) {
}
