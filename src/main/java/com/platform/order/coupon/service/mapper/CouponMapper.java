package com.platform.order.coupon.service.mapper;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.platform.order.common.dto.offset.OffsetPageResponseDto;
import com.platform.order.coupon.controller.dto.request.coupon.CreateCouponRequestDto;
import com.platform.order.coupon.controller.dto.response.coupon.CreateCouponResponseDto;
import com.platform.order.coupon.controller.dto.response.usercoupon.IssueUserCouponResponseDto;
import com.platform.order.coupon.controller.dto.response.usercoupon.ReadUserCouponResponseDto;
import com.platform.order.coupon.domain.coupon.entity.CouponEntity;
import com.platform.order.coupon.domain.usercoupon.entity.UserCouponEntity;
import com.platform.order.user.domain.entity.UserEntity;

@Component
public class CouponMapper {
	public CreateCouponResponseDto toCreateCouponResponse(CouponEntity coupon) {
		return new CreateCouponResponseDto(
			coupon.getId(),
			coupon.getType(),
			coupon.getAmount(),
			coupon.getQuantity(),
			coupon.getExpiredAt()
		);
	}

	public IssueUserCouponResponseDto toIssueCouponResponse(UserCouponEntity issuedUserCoupon) {
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

	public OffsetPageResponseDto<ReadUserCouponResponseDto> toPageResponse(Page<UserCouponEntity> userCouponPage) {
		Pageable pageable = userCouponPage.getPageable();
		List<UserCouponEntity> userCoupons = userCouponPage.getContent();

		List<ReadUserCouponResponseDto> readCouponResponses = userCoupons.stream()
			.map(this::toReadUserCouponResponse)
			.toList();

		return new OffsetPageResponseDto<>(
			userCouponPage.getTotalPages(),
			pageable.getPageNumber(),
			pageable.getPageSize(),
			readCouponResponses
		);
	}

	public ReadUserCouponResponseDto toReadUserCouponResponse(UserCouponEntity userCoupon) {
		return new ReadUserCouponResponseDto(
			userCoupon.getId(),
			userCoupon.getCoupon().getId(),
			userCoupon.getCoupon().getType(),
			userCoupon.getCoupon().getAmount(),
			userCoupon.getCoupon().getExpiredAt(),
			userCoupon.getIssuedAt(),
			userCoupon.isUsable()
		);
	}

	public CouponEntity toCoupon(CreateCouponRequestDto createCouponRequest, UserEntity user) {
		return CouponEntity.builder()
			.type(createCouponRequest.type())
			.amount(createCouponRequest.amount())
			.quantity(createCouponRequest.quantity())
			.expiredAt(createCouponRequest.expiredAt())
			.user(user)
			.build();
	}

	public UserCouponEntity toUserCoupon(UserEntity auth, CouponEntity coupon) {
		return UserCouponEntity.builder()
			.user(auth)
			.coupon(coupon)
			.issuedAt(LocalDate.now())
			.build();
	}
}
