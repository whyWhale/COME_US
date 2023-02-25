package com.platform.order.coupon.service;

import org.springframework.stereotype.Component;

import com.platform.order.coupon.controller.dto.response.CreateCouponResponseDto;
import com.platform.order.coupon.controller.dto.response.IssueCouponResponseDto;
import com.platform.order.coupon.domain.entity.CouponEntity;
import com.platform.order.coupon.domain.entity.UserCouponEntity;

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

	public IssueCouponResponseDto toIssueCouponResponseDto(UserCouponEntity issuedUserCoupon) {
		return new IssueCouponResponseDto(
			issuedUserCoupon.getId(),
			issuedUserCoupon.getUser().getId(),
			issuedUserCoupon.getCoupon().getId(),
			issuedUserCoupon.getCoupon().getType(),
			issuedUserCoupon.getCoupon().getAmount(),
			issuedUserCoupon.getCoupon().getExpiredAt(),
			issuedUserCoupon.getIssuedAt()
		);
	}
}
