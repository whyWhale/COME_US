package com.platform.order.coupon.domain.usercoupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.platform.order.coupon.domain.usercoupon.entity.UserCouponEntity;

public interface UserCouponRepository extends JpaRepository<UserCouponEntity, Long>, CustomUserCouponRepository {

}
