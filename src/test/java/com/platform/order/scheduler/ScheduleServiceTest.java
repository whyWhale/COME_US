package com.platform.order.scheduler;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.platform.order.common.scheduler.ScheduleService;
import com.platform.order.coupon.domain.coupon.entity.CouponEntity;
import com.platform.order.coupon.domain.coupon.entity.CouponType;
import com.platform.order.coupon.domain.coupon.repository.CouponRepository;
import com.platform.order.coupon.domain.usercoupon.entity.UserCouponEntity;
import com.platform.order.coupon.domain.usercoupon.repository.UserCouponRepository;
import com.platform.order.testenv.IntegrationTest;
import com.platform.order.user.domain.entity.Role;
import com.platform.order.user.domain.entity.UserEntity;
import com.platform.order.user.domain.repository.UserRepository;

public class ScheduleServiceTest extends IntegrationTest {

	@Autowired
	ScheduleService scheduleService;

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
				.expiredAt(LocalDate.now().minusDays(1))
				.build()
		);

		user = userRepository.save(UserEntity.builder()
			.email("kaikaio@cocoa.com")
			.username("kaikaiokk")
			.password("1")
			.nickName("kaikaio")
			.role(Role.USER)
			.build());

		userCoupon = userCouponRepository.save(UserCouponEntity.builder()
			.user(user)
			.coupon(coupon)
			.build());
	}

	@Test
	@DisplayName("쿠폰이 만료되면 사용자가 발급받은 쿠폰의 isUsable 컬럼은 false가 되고 쿠폰은 deleted =true 인상태로 변경된다")
	void testExpireCouponAndUserCouponIsUsableOff() {
		//given
		LocalDate tomrrow = LocalDate.now().plusDays(1);

		//when
		scheduleService.expireCoupon();
		//then
		UserCouponEntity userCouponEntity = userCouponRepository.findById(userCoupon.getId())
			.orElseThrow(RuntimeException::new);

		assertThat(userCouponEntity.isUsable()).isFalse();
	}
}
