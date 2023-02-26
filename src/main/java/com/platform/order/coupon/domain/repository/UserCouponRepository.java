package com.platform.order.coupon.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.platform.order.coupon.domain.entity.UserCouponEntity;
import com.platform.order.coupon.domain.repository.custom.CustomUserCouponRepository;

public interface UserCouponRepository extends JpaRepository<UserCouponEntity, Long>, CustomUserCouponRepository {

}
