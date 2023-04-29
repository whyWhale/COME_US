package com.platform.order.coupon.controller.dto.response.usercoupon;

import java.time.LocalDate;

import com.platform.order.coupon.domain.coupon.entity.CouponType;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReadUserCouponResponseDto(
	@Schema(description = "발급받은 아이디")
	Long id,

	@Schema(description = "발급한 쿠폰아이디")
	Long couponId,

	@Schema(description = "발급받은 아이디")
	CouponType couponType,

	@Schema(description = "발급한 쿠폰 종류")
	Long amount,

	@Schema(description = "발급한 쿠폰 만료일자")
	LocalDate expiredAt,

	@Schema(description = "발급한 쿠폰 발급일자")
	LocalDate issuedAt,

	@Schema(description = "쿠폰 사용여부")
	boolean isUsable
) {
}
