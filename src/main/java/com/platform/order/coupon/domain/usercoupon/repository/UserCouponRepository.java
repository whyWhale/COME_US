package com.platform.order.coupon.domain.usercoupon.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.platform.order.coupon.domain.usercoupon.entity.UserCouponEntity;

public interface UserCouponRepository extends JpaRepository<UserCouponEntity, Long>, CustomUserCouponRepository {

	@Query(value = "select uc from UserCouponEntity uc "
		+ "join fetch uc.coupon ucc "
		+ "where uc.id in :userCouponIds and"
		+ " ucc.expiredAt >=:now and"
		+ " uc.isUsable = true")
	List<UserCouponEntity> findByIdInWithCoupon(
		@Param("userCouponIds") List<Long> userCouponIds,
		@Param("now") LocalDate now
	);
}
