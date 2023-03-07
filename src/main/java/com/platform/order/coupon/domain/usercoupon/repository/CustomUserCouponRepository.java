package com.platform.order.coupon.domain.usercoupon.repository;

import org.springframework.data.domain.Page;

import com.platform.order.coupon.controller.dto.request.usercoupon.UserCouponPageRequestDto;
import com.platform.order.coupon.domain.usercoupon.entity.UserCouponEntity;

public interface CustomUserCouponRepository {
	Page<UserCouponEntity> findAllWithConditions(UserCouponPageRequestDto page, Long authId);
}
