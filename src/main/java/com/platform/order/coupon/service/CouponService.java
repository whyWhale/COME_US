package com.platform.order.coupon.service;

import static java.text.MessageFormat.format;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.order.common.exception.custom.BusinessException;
import com.platform.order.common.exception.custom.NotFoundResourceException;
import com.platform.order.common.exception.model.ErrorCode;
import com.platform.order.common.protocal.PageResponseDto;
import com.platform.order.coupon.controller.dto.request.coupon.CreateCouponRequestDto;
import com.platform.order.coupon.controller.dto.request.usercoupon.UserCouponPageRequestDto;
import com.platform.order.coupon.controller.dto.response.coupon.CreateCouponResponseDto;
import com.platform.order.coupon.controller.dto.response.usercoupon.IssueUserCouponResponseDto;
import com.platform.order.coupon.controller.dto.response.usercoupon.ReadUserCouponResponseDto;
import com.platform.order.coupon.domain.coupon.entity.CouponEntity;
import com.platform.order.coupon.domain.coupon.repository.CouponRepository;
import com.platform.order.coupon.domain.usercoupon.entity.UserCouponEntity;
import com.platform.order.coupon.domain.usercoupon.repository.UserCouponRepository;
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
			.orElseThrow(() -> new NotFoundResourceException(
				format("user id:{0} is not found.", authId),
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
	public IssueUserCouponResponseDto issue(Long authId, Long couponId) {
		UserEntity auth = userRepository.findById(authId)
			.orElseThrow(() -> new NotFoundResourceException(
				format("user id:{0} is not found.", authId),
				ErrorCode.NOT_FOUND_RESOURCES));
		boolean isAvailable = couponRepository.decreaseQuantity(couponId) == 1;

		if (!isAvailable) {
			throw new BusinessException(
				format("쿠폰 수량이 부족합니다. coupon id : {0}", couponId),
				ErrorCode.OUT_OF_QUANTITY);
		}

		CouponEntity coupon = couponRepository.findByIdAndExpiredAtAfter(couponId, LocalDate.now())
			.orElseThrow(() -> new NotFoundResourceException(
				format("coupon id:{0} is not found.", couponId),
				ErrorCode.NOT_FOUND_RESOURCES));

		UserCouponEntity issuedUserCoupon = userCouponRepository.save(
			UserCouponEntity.builder()
				.user(auth)
				.coupon(coupon)
				.issuedAt(LocalDate.now())
				.build()
		);

		return couponMapper.toIssueCouponResponseDto(issuedUserCoupon);
	}

	public PageResponseDto<ReadUserCouponResponseDto> readAll(Long authId, UserCouponPageRequestDto pageRequest) {
		Page<UserCouponEntity> userCouponPage = userCouponRepository.findAllWithConditions(pageRequest, authId);

		return couponMapper.toPageResponseDto(userCouponPage);
	}

}
