package com.platform.order.coupon.controller.dto.request.usercoupon;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import io.swagger.v3.oas.annotations.media.Schema;

public record IssueUserCouponRequestDto(
	@Schema(description = "발급 받을 쿠폰 식별자", required = true)
	@NotNull
	@Positive Long couponId
) {
}
