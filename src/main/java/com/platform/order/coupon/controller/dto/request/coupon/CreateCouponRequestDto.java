package com.platform.order.coupon.controller.dto.request.coupon;

import java.time.LocalDate;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import com.platform.order.coupon.domain.coupon.entity.CouponType;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateCouponRequestDto(
	@Schema(description = "쿠폰의 종류(고정,정률) 선택", required = true)
	@NotNull
	CouponType type,

	@Schema(description = "쿠폰의 할인률 또는 고정금액의 양", required = true)
	@NotNull
	@PositiveOrZero
	Long amount,

	@Schema(description = "발급 수량", required = true)
	@NotNull
	@PositiveOrZero
	Long quantity,

	@Schema(description = "만료 일자", required = true)
	@NotNull
	@FutureOrPresent
	LocalDate expiredAt
) {
}
