package com.platform.order.coupon.domain.coupon.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.platform.order.coupon.domain.coupon.entity.CouponEntity;
import com.platform.order.user.domain.entity.UserEntity;

public interface CouponRepository extends JpaRepository<CouponEntity, Long> {
	Optional<CouponEntity> findByIdAndExpiredAtAfter(Long id, LocalDate now);

	@Modifying
	@Query(value = "update coupon c set c.quantity=c.quantity-1 where c.id =:couponId and c.quantity>=1", nativeQuery = true)
	Integer decreaseQuantity(@Param("couponId") Long couponId);

	List<CouponEntity> findByIdInAndUserAndExpiredAtBefore(List<Long> couponIds, UserEntity orderUser, LocalDate now);
}
