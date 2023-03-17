package com.platform.order.coupon.service;

import static java.text.MessageFormat.format;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.order.common.exception.custom.BusinessException;
import com.platform.order.common.exception.custom.NotFoundResourceException;
import com.platform.order.common.exception.model.ErrorCode;
import com.platform.order.common.dto.offset.PageResponseDto;
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
		UserEntity user = getAuth(authId);
		CouponEntity coupon = couponMapper.toCoupon(createCouponRequest, user);
		CouponEntity savedCoupon = couponRepository.save(coupon);

		return couponMapper.toCreateCouponResponse(savedCoupon);
	}

	@Transactional
	public IssueUserCouponResponseDto issue(Long authId, Long couponId) {
		UserEntity auth = getAuth(authId);
		boolean isAvailable = couponRepository.decreaseQuantity(couponId) == 1;

		if (!isAvailable) {
			throw new BusinessException(
				format("쿠폰 수량이 부족합니다. coupon id : {0}", couponId),
				ErrorCode.OUT_OF_QUANTITY);
		}

		CouponEntity coupon = couponRepository.findByIdAndExpiredAtAfter(couponId, LocalDate.now())
			.orElseThrow(() -> new NotFoundResourceException(
				format("coupon id:{0} is not found.", couponId)
			));
		UserCouponEntity userCoupon = couponMapper.toUserCoupon(auth, coupon);
		UserCouponEntity issuedUserCoupon = userCouponRepository.save(userCoupon);

		return couponMapper.toIssueCouponResponse(issuedUserCoupon);
	}

	public PageResponseDto<ReadUserCouponResponseDto> readAll(Long authId, UserCouponPageRequestDto pageRequest) {
		Page<UserCouponEntity> userCouponPage = userCouponRepository.findAllWithConditions(pageRequest, authId);

		return couponMapper.toPageResponse(userCouponPage);
	}

	private UserEntity getAuth(Long authId) {
		return userRepository.findById(authId)
			.orElseThrow(() -> new NotFoundResourceException(
				format("user id:{0} is not found.", authId)
			));
	}

}
