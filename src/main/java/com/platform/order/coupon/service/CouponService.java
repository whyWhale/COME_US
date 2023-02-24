package com.platform.order.coupon.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.order.coupon.controller.dto.request.CreateCouponRequestDto;
import com.platform.order.coupon.controller.dto.response.CreateCouponResponseDto;
import com.platform.order.coupon.domain.entity.CouponEntity;
import com.platform.order.coupon.domain.repository.CouponRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CouponService {
	private final CouponRepository couponRepository;
	private final CouponMapper couponMapper;

	public CreateCouponResponseDto create(CreateCouponRequestDto createCouponRequest) {
		CouponEntity savedCoupon = couponRepository.save(CouponEntity.builder()
			.type(createCouponRequest.type())
			.amount(createCouponRequest.amount())
			.quantity(createCouponRequest.quantity())
			.expiredAt(createCouponRequest.expiredAt())
			.build());

		return couponMapper.toCreateCouponResponseDto(savedCoupon);
	}

}
