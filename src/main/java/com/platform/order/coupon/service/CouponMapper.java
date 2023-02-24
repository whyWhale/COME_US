package com.platform.order.coupon.service;

import org.springframework.stereotype.Component;

import com.platform.order.coupon.controller.dto.response.CreateCouponResponseDto;
import com.platform.order.coupon.domain.entity.CouponEntity;

@Component
public class CouponMapper {
	public CreateCouponResponseDto toCreateCouponResponseDto(CouponEntity coupon) {
		return new CreateCouponResponseDto(
			coupon.getId(),
			coupon.getType(),
			coupon.getAmount(),
			coupon.getQuantity(),
			coupon.getExpiredAt()
		);
	}
}
