package com.platform.order.coupon.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.platform.order.common.protocal.PageResponseDto;
import com.platform.order.coupon.controller.dto.response.CreateCouponResponseDto;
import com.platform.order.coupon.controller.dto.response.IssueCouponResponseDto;
import com.platform.order.coupon.controller.dto.response.ReadCouponResponseDto;
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

	public PageResponseDto<ReadCouponResponseDto> toPageResponseDto(Page<UserCouponEntity> userCouponPage) {
		Pageable pageable = userCouponPage.getPageable();
		List<UserCouponEntity> userCoupons = userCouponPage.getContent();

		List<ReadCouponResponseDto> readCouponResponses = userCoupons.stream()
			.map(userCoupon -> new ReadCouponResponseDto(
				userCoupon.getId(),
				userCoupon.getCoupon().getId(),
				userCoupon.getCoupon().getType(),
				userCoupon.getCoupon().getAmount(),
				userCoupon.getCoupon().getExpiredAt(),
				userCoupon.getIssuedAt(),
				userCoupon.isUsable()
			)).toList();

		return new PageResponseDto<>(
			userCouponPage.getTotalPages(),
			pageable.getPageNumber(),
			pageable.getPageSize(),
			readCouponResponses
		);
	}
}
