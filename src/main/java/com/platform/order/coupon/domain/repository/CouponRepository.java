package com.platform.order.coupon.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.platform.order.coupon.domain.entity.CouponEntity;

public interface CouponRepository extends JpaRepository<CouponEntity, Long> {
}
