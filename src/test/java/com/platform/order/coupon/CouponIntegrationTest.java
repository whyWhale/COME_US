package com.platform.order.coupon;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.platform.order.coupon.controller.dto.request.CreateCouponRequestDto;
import com.platform.order.coupon.controller.dto.response.CreateCouponResponseDto;
import com.platform.order.coupon.controller.dto.response.IssueCouponResponseDto;
import com.platform.order.coupon.domain.entity.CouponEntity;
import com.platform.order.coupon.domain.entity.CouponType;
import com.platform.order.coupon.domain.repository.CouponRepository;
import com.platform.order.coupon.domain.repository.UserCouponRepository;
import com.platform.order.coupon.service.CouponService;
import com.platform.order.testenv.IntegrationTest;
import com.platform.order.user.domain.entity.Role;
import com.platform.order.user.domain.entity.UserEntity;
import com.platform.order.user.domain.repository.UserRepository;

public class CouponIntegrationTest extends IntegrationTest {
	@Autowired
	CouponService couponService;

	@Autowired
	UserCouponRepository userCouponRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	CouponRepository couponRepository;

	UserEntity user;

	@BeforeEach
	public void setUp() {
		user = userRepository.save(UserEntity
			.builder()
			.email("nvdia@google.com")
			.username("NAVIaccdd")
			.nickName("NVDIAVvvs")
			.role(Role.USER)
			.password("1")
			.build()
		);
	}

	@AfterEach
	public void setDown() {
		userCouponRepository.deleteAllInBatch();
		couponRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();
	}

	@Test
	@DisplayName("쿠폰을 생성한다.")
	void testCreate() {
		//given
		var createCouponRequest = new CreateCouponRequestDto(CouponType.FIXED, 10000L, 1000L,
			LocalDate.now().plusDays(60));
		//when
		CreateCouponResponseDto createCouponResponse = couponService.create(user.getId(), createCouponRequest);
		//then
		assertThat(createCouponResponse.couponType()).isEqualTo(createCouponRequest.type());
		assertThat(createCouponResponse.quantity()).isEqualTo(createCouponRequest.quantity());
		assertThat(createCouponResponse.amount()).isEqualTo(createCouponRequest.amount());
		assertThat(createCouponResponse.expiredAt()).isEqualTo(createCouponRequest.expiredAt());
	}

	@Test
	@DisplayName("쿠폰을 발급받는다.")
	void testIssue() {
		//given
		CouponEntity coupon = couponRepository.save(CouponEntity.builder()
			.user(user)
			.quantity(10L)
			.expiredAt(LocalDate.now().plusMonths(2))
			.amount(1000L)
			.type(CouponType.FIXED)
			.build());
		//when
		IssueCouponResponseDto issueCouponResponse = couponService.issue(user.getId(), coupon.getId());
		//then
		CouponEntity decreasedCoupon = couponRepository.findById(coupon.getId()).orElseThrow(RuntimeException::new);

		assertThat(issueCouponResponse.couponId()).isEqualTo(coupon.getId());
		assertThat(issueCouponResponse.userId()).isEqualTo(coupon.getUser().getId());
		assertThat(issueCouponResponse.couponType()).isEqualTo(coupon.getType());
		assertThat(issueCouponResponse.amount()).isEqualTo(coupon.getAmount());
		assertThat(issueCouponResponse.expiredAt()).isEqualTo(coupon.getExpiredAt());
		assertThat(decreasedCoupon.getQuantity()).isEqualTo(coupon.getQuantity() - 1);
	}
}
