package com.platform.order.coupon.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.platform.order.coupon.domain.entity.UserCouponEntity;

public interface UserCouponRepository extends JpaRepository<UserCouponEntity, Long> {
}
