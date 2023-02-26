package com.platform.order.coupon.domain.repository.custom;

import org.springframework.data.domain.Page;

import com.platform.order.coupon.controller.dto.request.UserCouponPageRequestDto;
import com.platform.order.coupon.domain.entity.UserCouponEntity;

public interface CustomUserCouponRepository {
	Page<UserCouponEntity> findAllWithConditions(UserCouponPageRequestDto page,Long authId);
}
