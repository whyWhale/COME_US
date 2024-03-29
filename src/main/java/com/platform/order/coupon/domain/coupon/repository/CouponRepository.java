package com.platform.order.coupon.domain.coupon.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.platform.order.coupon.domain.coupon.entity.CouponEntity;

public interface CouponRepository extends JpaRepository<CouponEntity, Long> {
	Optional<CouponEntity> findByIdAndExpiredAtAfter(Long id, LocalDate now);

	@Modifying(clearAutomatically = true)
	@Query(value = "update coupon c "
		+ "set c.quantity=c.quantity-1 "
		+ "where c.id =:couponId and c.quantity>=1"
		, nativeQuery = true)
	Integer decreaseQuantity(@Param("couponId") Long couponId);

	@Modifying(clearAutomatically = true)
	@Query(value = "update coupon c "
		+ "join user_coupon uc on c.id = uc.coupon_id "
		+ "set uc.is_usable=false, c.deleted=true "
		+ "where c.expired_at < :now and c.deleted=false",
		nativeQuery = true)
	void expireAllBatch(@Param("now") LocalDate now);
}
