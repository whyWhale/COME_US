package com.platform.order.coupon.controller.dto.response.usercoupon;

import java.time.LocalDate;

import com.platform.order.coupon.domain.coupon.entity.CouponType;

import io.swagger.v3.oas.annotations.media.Schema;

public record IssueUserCouponResponseDto(
	@Schema(description = "발급 아이디")
	Long id,

	@Schema(description = "발급한 사용자 아이디")
	Long userId,

	@Schema(description = "발급받은 쿠폰 아이디")
	Long couponId,

	@Schema(description = "발급한 쿠폰 종류")
	CouponType couponType,

	@Schema(description = "발급한 쿠폰 할일금액 및 할인율")
	Long amount,

	@Schema(description = "발급한 쿠폰 만료일자")
	LocalDate expiredAt,

	@Schema(description = "발급한 쿠폰 발행일자")
	LocalDate issuedAt
) {

}
