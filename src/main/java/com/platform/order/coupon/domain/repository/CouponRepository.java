package com.platform.order.coupon.domain.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.platform.order.coupon.domain.entity.CouponEntity;

public interface CouponRepository extends JpaRepository<CouponEntity, Long> {
	Optional<CouponEntity> findByIdAndExpiredAtAfter(Long id, LocalDate now);
}
