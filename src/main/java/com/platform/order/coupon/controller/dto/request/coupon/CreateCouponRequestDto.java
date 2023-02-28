package com.platform.order.coupon.controller.dto.request.coupon;

import java.time.LocalDate;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import com.platform.order.coupon.domain.coupon.entity.CouponType;

public record CreateCouponRequestDto(
	@NotNull
	CouponType type,

	@NotNull
	@PositiveOrZero
	Long amount,

	@NotNull
	@PositiveOrZero
	Long quantity,

	@NotNull
	@FutureOrPresent
	LocalDate expiredAt
) {
}
