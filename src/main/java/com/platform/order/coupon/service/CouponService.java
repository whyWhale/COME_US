package com.platform.order.coupon.service;

import java.text.MessageFormat;
import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.order.common.exception.custom.NotFoundResource;
import com.platform.order.common.exception.model.ErrorCode;
import com.platform.order.coupon.controller.dto.request.CreateCouponRequestDto;
import com.platform.order.coupon.controller.dto.response.CreateCouponResponseDto;
import com.platform.order.coupon.controller.dto.response.IssueCouponResponseDto;
import com.platform.order.coupon.domain.entity.CouponEntity;
import com.platform.order.coupon.domain.entity.UserCouponEntity;
import com.platform.order.coupon.domain.repository.CouponRepository;
import com.platform.order.coupon.domain.repository.UserCouponRepository;
import com.platform.order.user.domain.entity.UserEntity;
import com.platform.order.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CouponService {
	private final CouponRepository couponRepository;
	private final UserCouponRepository userCouponRepository;
	private final UserRepository userRepository;
	private final CouponMapper couponMapper;

	@Transactional
	public CreateCouponResponseDto create(Long authId, CreateCouponRequestDto createCouponRequest) {
		UserEntity user = userRepository.findById(authId)
			.orElseThrow(() -> new NotFoundResource(
				MessageFormat.format("user id:{0} is not found.", authId),
				ErrorCode.NOT_FOUND_RESOURCES)
			);
		CouponEntity savedCoupon = couponRepository.save(CouponEntity.builder()
			.type(createCouponRequest.type())
			.amount(createCouponRequest.amount())
			.quantity(createCouponRequest.quantity())
			.expiredAt(createCouponRequest.expiredAt())
			.user(user)
			.build());

		return couponMapper.toCreateCouponResponseDto(savedCoupon);
	}

	@Transactional
	public IssueCouponResponseDto issue(Long authId, Long couponId) {
		UserEntity auth = userRepository.findById(authId)
			.orElseThrow(() -> new NotFoundResource(
				MessageFormat.format("user id:{0} is not found.", authId),
				ErrorCode.NOT_FOUND_RESOURCES)
			);
		CouponEntity coupon = couponRepository.findByIdAndExpiredAtAfter(couponId, LocalDate.now())
			.orElseThrow(() -> new NotFoundResource(
				MessageFormat.format("coupon id:{0} is not found.", couponId),
				ErrorCode.NOT_FOUND_RESOURCES)
			);

		UserCouponEntity issuedUserCoupon = userCouponRepository.save(
			UserCouponEntity.builder()
				.user(auth)
				.coupon(coupon)
				.issuedAt(LocalDate.now())
				.build()
		);

		return couponMapper.toIssueCouponResponseDto(issuedUserCoupon);
	}
}
