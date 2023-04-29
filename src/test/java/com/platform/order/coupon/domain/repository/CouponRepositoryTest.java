package com.platform.order.coupon.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.platform.order.coupon.domain.coupon.entity.CouponEntity;
import com.platform.order.coupon.domain.coupon.entity.CouponType;
import com.platform.order.coupon.domain.coupon.repository.CouponRepository;
import com.platform.order.coupon.domain.usercoupon.entity.UserCouponEntity;
import com.platform.order.coupon.domain.usercoupon.repository.UserCouponRepository;
import com.platform.order.testenv.RepositoryTest;
import com.platform.order.user.domain.entity.Role;
import com.platform.order.user.domain.entity.UserEntity;
import com.platform.order.user.domain.repository.UserRepository;

class CouponRepositoryTest extends RepositoryTest {
	@Autowired
	CouponRepository couponRepository;

	@Autowired
	UserCouponRepository userCouponRepository;

	@Autowired
	UserRepository userRepository;

	CouponEntity coupon;
	UserEntity user;
	UserCouponEntity userCoupon;

	@BeforeEach
	public void setUp() {
		coupon = couponRepository.save(
			CouponEntity.builder()
				.type(CouponType.FIXED)
				.quantity(100L)
				.amount(10000L)
				.expiredAt(LocalDate.now().plusMonths(1))
				.build()
		);

		user = userRepository.save(UserEntity.builder()
			.email("whywhale@cocoa.com")
			.username("whyWhale")
			.password("1")
			.nickName("whale")
			.role(Role.USER)
			.build());

		userCoupon = userCouponRepository.save(UserCouponEntity.builder()
			.user(user)
			.coupon(coupon)
			.build());
	}

	@Test
	@DisplayName("재고 감소 Native 쿼리로 수량이 정상적으로 차감되면 1을 리턴한다.")
	void testDecreaseQuantity() {
		//given
		//when
		boolean isAvailable = couponRepository.decreaseQuantity(coupon.getId()) == 1;
		//then
		assertThat(isAvailable).isTrue();
	}

	@Test
	@DisplayName("재고 감소 Native 쿼리로 수량이 차감되지 않으면 0을 리턴한다.")
	void failDecreaseQuantity() {
		//given
		CouponEntity notIssueCoupon = couponRepository.save(
			CouponEntity.builder()
				.type(CouponType.FIXED)
				.quantity(0L)
				.amount(10000L)
				.expiredAt(LocalDate.now().plusMonths(1))
				.build()
		);
		//when
		boolean isAvailable = couponRepository.decreaseQuantity(notIssueCoupon.getId()) == 1;

		//then
		assertThat(isAvailable).isFalse();
	}
}
