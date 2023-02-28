package com.platform.order.coupon.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.platform.order.common.protocal.PageResponseDto;
import com.platform.order.coupon.controller.dto.response.coupon.CreateCouponResponseDto;
import com.platform.order.coupon.controller.dto.response.usercoupon.IssueUserCouponResponseDto;
import com.platform.order.coupon.controller.dto.response.usercoupon.ReadUserCouponResponseDto;
import com.platform.order.coupon.domain.coupon.entity.CouponEntity;
import com.platform.order.coupon.domain.usercoupon.entity.UserCouponEntity;

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

	public IssueUserCouponResponseDto toIssueCouponResponseDto(UserCouponEntity issuedUserCoupon) {
		return new IssueUserCouponResponseDto(
			issuedUserCoupon.getId(),
			issuedUserCoupon.getUser().getId(),
			issuedUserCoupon.getCoupon().getId(),
			issuedUserCoupon.getCoupon().getType(),
			issuedUserCoupon.getCoupon().getAmount(),
			issuedUserCoupon.getCoupon().getExpiredAt(),
			issuedUserCoupon.getIssuedAt()
		);
	}

	public PageResponseDto<ReadUserCouponResponseDto> toPageResponseDto(Page<UserCouponEntity> userCouponPage) {
		Pageable pageable = userCouponPage.getPageable();
		List<UserCouponEntity> userCoupons = userCouponPage.getContent();

		List<ReadUserCouponResponseDto> readCouponResponses = userCoupons.stream()
			.map(userCoupon -> new ReadUserCouponResponseDto(
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
